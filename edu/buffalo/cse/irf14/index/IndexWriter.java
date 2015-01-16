package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author Prashanth Class responsible for writing indexes to disk
 */
public class IndexWriter {

	private String indexDirectory;
	
	private AnalyzerFactory aFactory;
	
	private Tokenizer tknizer;
	
	private Map<String, Integer> documentDictionary;
	
	private Index termIndex;
	
	private Index authorIndex;
	
	private Index categoryIndex;
	
	private Index placeIndex;
	
	//to be removed before submission
	private static boolean writeHtml = false;
	private StringBuffer buffer = new StringBuffer();

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		this.indexDirectory = indexDir;
		this.aFactory = AnalyzerFactory.getInstance();
		this.tknizer = new Tokenizer();
		this.documentDictionary = new TreeMap<String, Integer>();
		this.termIndex = new Index();
		this.authorIndex = new Index();
		this.categoryIndex = new Index();
		this.placeIndex = new Index();
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *            : The Document to be added
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		Integer docId = getId(d.getField(FieldNames.FILEID)[0], documentDictionary);
		documentDictionary.put(d.getField(FieldNames.FILEID)[0], docId);
		
		Analyzer analyzer = null;
		Index index = null;
		try {
			for(FieldNames fName: d.getValidFields()){
				switch(fName){
					case CATEGORY :
						analyzer = aFactory.getAnalyzerForField(FieldNames.CATEGORY, tknizer.consume(d.getField(FieldNames.CATEGORY)[0]));
						index = categoryIndex;
						break;
					case TITLE:
						analyzer = aFactory.getAnalyzerForField(FieldNames.TITLE, tknizer.consume(d.getField(FieldNames.TITLE)[0]));
						index = termIndex;
						break;
					case AUTHOR:
						analyzer = aFactory.getAnalyzerForField(FieldNames.AUTHOR, tknizer.consume(d.getField(FieldNames.AUTHOR)[0]));
						index = authorIndex;
						break;
					case AUTHORORG:
						analyzer = aFactory.getAnalyzerForField(FieldNames.AUTHORORG, tknizer.consume(d.getField(FieldNames.AUTHORORG)[0]));
						index = authorIndex;
						break;
					case PLACE:
						analyzer = aFactory.getAnalyzerForField(FieldNames.PLACE, tknizer.consume(d.getField(FieldNames.PLACE)[0]));
						index = placeIndex;
						break;
					case NEWSDATE:
						analyzer = aFactory.getAnalyzerForField(FieldNames.NEWSDATE, tknizer.consume(d.getField(FieldNames.NEWSDATE)[0]));
						index = termIndex;
						break;
					case CONTENT:
					default:
						analyzer = aFactory.getAnalyzerForField(FieldNames.CONTENT, tknizer.consume(d.getField(FieldNames.CONTENT)[0]));
						index = termIndex;
						break;
				}
				while(analyzer.increment());
				for(Token term: analyzer.getStream().getTokenList()){
					index.add(term.toString(), docId);
				}
			}
		} catch (TokenizerException e) {
			e.printStackTrace();
			throw new IndexerException();
		}
	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		String path = indexDirectory + File.separator;
		
		writeDocDictToDisk(path);
		if(writeHtml)writeDocDictToHTML();
		
		buffer.append("<br><b>Author:</b></br>");
		writeToDisk(authorIndex, path.concat(FileNames.DICT_AUTH), path.concat(FileNames.INDX_AUTH));
		buffer.append("<br><b>Category:</b></br>");
		writeToDisk(categoryIndex, path.concat(FileNames.DICT_CAT), path.concat(FileNames.INDX_CAT));
		buffer.append("<br><b>Place:</b></br>");
		writeToDisk(placeIndex, path.concat(FileNames.DICT_PLC), path.concat(FileNames.INDX_PLC));
		buffer.append("<br><b>Term:</b></br>");
		writeToDisk(termIndex, path.concat(FileNames.DICT_TERM), path.concat(FileNames.INDX_TERM));
		if(writeHtml)writeHTMLToDisk();
	}
	
	private void writeDocDictToHTML(){
		buffer.append("<!DOCTYPE html>");
		buffer.append("<html lang=\"en\">");
		buffer.append("<head><title>Title</title></head>");
		buffer.append("<body>");
		buffer.append("<table>");
		for(Entry<String, Integer> entry:documentDictionary.entrySet()){
			buffer.append("<tr><td>"+entry.getKey()+"</td><td>"+entry.getValue()+"</td></tr>");
		}
		buffer.append("</table>");
	}
	
	private void writeHTMLToDisk(){
		buffer.append("</body></html>");
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(indexDirectory + File.separator + "report.html"));
			oos.writeObject(buffer.toString());
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeDocDictToDisk(String path){
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(path.concat(FileNames.DICT_DOC)));
			oos.writeObject(documentDictionary);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeToDisk(Index index, String dict, String indx) {
		Map<String, Integer> dictEntries = new TreeMap<String, Integer>();
		Map<Integer, PostingsList> indxEntries = new TreeMap<Integer, PostingsList>();
		Integer currentId = 0;
		
		if(writeHtml)buffer.append("<table border=\"1\">");
		for(Entry<String, PostingsList> entry : index.getEntries().entrySet()){
			entry.getValue().calculateIDF(this.documentDictionary.size());
			dictEntries.put(entry.getKey(), currentId);
			indxEntries.put(currentId, entry.getValue());
			if(writeHtml)buffer.append("<tr><td>"+entry.getKey()+"</td><td>"+currentId+"</td>"+ "<td>"+currentId+"</td><td>"+entry.getValue()+"</td></tr>");
			currentId++;
		}
		if(writeHtml)buffer.append("</table>");
		
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(dict));
			oos.writeObject(new Dictionary(dictEntries));
			oos.close();
			
			oos = new ObjectOutputStream(new FileOutputStream(indx));
			oos.writeObject(new InvertedIndex(indxEntries));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Integer getId(String item, Map<String, Integer> dictionary) {
		if(dictionary.containsKey(item)){
			return dictionary.get(item);
		}else{
			Integer newId = getMaxDictionaryValue(dictionary)+1;
			dictionary.put(item, newId);
			return newId;
		}
	}
	
	private Integer getMaxDictionaryValue(Map<String, Integer> dictionary) {
		Integer max = 0;
		for(Integer integer: dictionary.values()){
			max = max < integer ? integer : max;
		}
		return max;
	}

	public String getIndexDirectory() {
		return indexDirectory;
	}

	public void setIndexDirectory(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

}
