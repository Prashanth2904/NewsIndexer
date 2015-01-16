package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.PostingsList;
import edu.buffalo.cse.irf14.query.QueryEngine;
import edu.buffalo.cse.irf14.query.ResultDocument;
import edu.buffalo.cse.irf14.query.Scorer;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	
	private String corpusDir;
	private char mode;
	private PrintStream printStream;
	
	private QueryEngine searchEngine;
	
	public enum ScoringModel {TFIDF, OKAPI};
	
	/**
	 * Default (and only public) constructor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		this.searchEngine = new QueryEngine(indexDir);
		this.corpusDir = corpusDir;
		this.mode = mode;
		this.printStream = stream;
	}
	
	public static void main(String[] args) {
		SearchRunner sr;
		try {
			sr = new SearchRunner("F:\\Fall2014\\temp\\indexDir", "F:\\Fall20"
					+ "14\\temp\\ipDir", 'Q', new PrintStream(new File("F:\\Fall2014\\temp\\tmp\\ResultOkapi")));
			sr.query(new File("F:\\Fall2014\\temp\\tmp\\Result"));
			//sr.query("Author:torday AND (debt OR currency)", ScoringModel.TFIDF);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		List<ResultDocument> display = new LinkedList<ResultDocument>();
		long start = System.currentTimeMillis();
		Scorer sc = new Scorer();
		try {
			List<ResultDocument> list = sc.rankDocuments(this.mode, model, searchEngine.run(userQuery, "OR"),
					searchEngine.readTermsIntoIndex(), searchEngine.getIPostings());
			int i=1; String fileId;
			for(ResultDocument d: list){
				fileId = searchEngine.getFileId(d.getDocId());
				if(fileId==null) continue;
				if(i>10) break;
				d.setRank(i++);
				Document doc = getDocument(fileId);
				d.setTitle(doc.getField(FieldNames.TITLE)!=null?doc.getField(FieldNames.TITLE)[0]:"");
				d.setSnippet(doc.getField(FieldNames.CONTENT)!=null?doc.getField(FieldNames.CONTENT)[0]:"");
				display.add(d);
			}
			this.printStream.println("Query :"+ searchEngine.getQuery().toString());
			this.printStream.println("Query Time :"+ (System.currentTimeMillis()-start));
			for(ResultDocument d: display){
				this.printStream.println("\n");
				this.printStream.println("\nResult rank: "+ d.getRank());
				this.printStream.println("\nResult title: "+ d.getTitle());
				this.printStream.println("\nResult snippet: "+ d.getSnippet());
				this.printStream.println("\nResult relevancy :"+ d.getRelevancy());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Document getDocument(String fileId) throws IOException {
		Document doc = null;
		try {
			for(File file: getAllFiles(new File(this.corpusDir))){
				if(file.getName().endsWith(fileId)){
					doc =  Parser.parse(file.getAbsolutePath());
					return doc;
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return doc;
    }

	public List<File> getAllFiles(File src){
		List<File> files = new ArrayList<File>();
		if(src.isDirectory()){
			for (String file : src.list()) {
				files.addAll(getAllFiles(new File(src, file)));
	    	}
		}else{
			files.add(src);
		}
		return files;
	}
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		BufferedReader buffReader;
		String currentLine;
		int numQueries=-1;
		int numResults=0;
		List<ResultDocument> list;
		StringBuilder buffer = null;
		try {
			buffReader = new BufferedReader(new FileReader(queryFile));
			buffer = new StringBuilder();
			while((currentLine = buffReader.readLine()) != null){
				if(numQueries<0){
					numQueries = Integer.valueOf(currentLine.split("=")[1]);
					continue;
				}
				int i = 0,j = 0,k = 0; char[] stream = currentLine.toCharArray();
				char[] queryId = new char[100];
				char[] query = new char[100];
				while(i < stream.length && stream[i] != ':'){
					queryId[j++] = stream[i];
					i++;
				}
				i++;
				while(i < stream.length && stream[i] != '}'){
					query[k++] = stream[i];
					i++;
				}
				PostingsList pl = searchEngine.run(String.valueOf(query).trim(), "OR");
				Scorer sc = new Scorer();
				list = sc.rankDocuments(this.mode, ScoringModel.TFIDF, pl, searchEngine.readTermsIntoIndex(), searchEngine.getIPostings());
				String fileId;
				buffer.append("\n"+String.valueOf(queryId).trim()+":{");
				for(int r=0; r<(list.size()<10?list.size():10); r++){
					if(r!=0) buffer.append(", ");
					fileId = searchEngine.getFileId(list.get(r).getDocId());
					if(r!=0) buffer.append(" ");
					buffer.append((fileId+"#"+list.get(r).getRelevancy()));
				}
				buffer.append("}\n");
				numResults++;
			}
			this.printStream.println("numResults="+String.valueOf(numResults));
			this.printStream.println(buffer.toString());
			this.printStream.close();
			buffReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		this.printStream.close();
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}