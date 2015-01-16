/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Prashanth
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	private String indexDir;
	
	private IndexType type;
	
	private Map<String, Integer> documentDictionary;
	
	private InvertedIndex invertedIndex;
	
	private Dictionary dictionary;
	
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		this.indexDir = indexDir;
		this.type = type;
		readDocDictFromDisk();
		readFromDisk();
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		return this.dictionary.getEntries().size();
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		return this.documentDictionary.size();
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		if(!this.dictionary.getEntries().containsKey(term))return null;
		Map<String, Integer> postings = new HashMap<String, Integer>();
		Integer termId = this.dictionary.getEntries().get(term);
		for(Integer i : this.invertedIndex.getEntries().get(termId).getPostings()){
			String fileId = getFileId(i);
			if(postings.containsKey(fileId)){
				postings.put(fileId, postings.get(getFileId(i))+1);
			}else{
				postings.put(fileId, 1);
			}
		}
		return postings;
	}
	
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		if(k < 1)return null;
		return sortByValue(this.invertedIndex.getEntries(), k);
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void readDocDictFromDisk(){
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(getDocDictFileName()));
			this.documentDictionary = (Map<String, Integer>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void readFromDisk() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(getDictFileName()));
			this.dictionary = (Dictionary) ois.readObject();
			ois.close();
			
			ois = new ObjectInputStream(new FileInputStream(getIndexFileName()));
			this.invertedIndex = (InvertedIndex)ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getDocDictFileName() {
		return this.indexDir + File.separator + FileNames.DICT_DOC;
	}
	
	private String getDictFileName() {
		String path = this.indexDir + File.separator;
		switch(this.type){
			case AUTHOR:
				return path.concat(FileNames.DICT_AUTH);
			case CATEGORY:
				return path.concat(FileNames.DICT_CAT);
			case PLACE:
				return path.concat(FileNames.DICT_PLC);
			case TERM:
				return path.concat(FileNames.DICT_TERM);
		}
		return null;
	}
	
	private String getIndexFileName() {
		String path = this.indexDir + File.separator;
		switch(this.type){
			case AUTHOR:
				return path.concat(FileNames.INDX_AUTH);
			case CATEGORY:
				return path.concat(FileNames.INDX_CAT);
			case PLACE:
				return path.concat(FileNames.INDX_PLC);
			case TERM:
				return path.concat(FileNames.INDX_TERM);
		}
		return null;
	}
	
	private String getFileId(Integer docId){
		for(Entry<String, Integer> e: this.documentDictionary.entrySet()){
			if(docId.equals(e.getValue())){
				return e.getKey();
			}
		}
		return null;
	}
	
	private String getTerm(Integer termId){
		for(Entry<String, Integer> e: this.dictionary.getEntries().entrySet()){
			if(termId.equals(e.getValue())){
				return e.getKey();
			}
		}
		return null;
	}
	
	public List<String> sortByValue(Map<Integer, PostingsList> map, int k) {
		SortedSet<Map.Entry<Integer, PostingsList>> sortedset = new TreeSet<Map.Entry<Integer, PostingsList>>(
				new Comparator<Map.Entry<Integer, PostingsList>>() {
					@Override
					public int compare(Map.Entry<Integer, PostingsList> pl1, Map.Entry<Integer, PostingsList> pl2) {
						return pl2.getValue().getTotalFrequency() - pl1.getValue().getTotalFrequency();
					}
				});

		sortedset.addAll(map.entrySet());

		int count = 0;
		List<String> result = new LinkedList<String>();
		for (Map.Entry<Integer, PostingsList> entry : sortedset) {
			if(k == count) break;
			result.add(getTerm(entry.getKey()));
			count++;
		}
		return result;
	}

	/**
	 * @return the indexDir
	 */
	public String getIndexDir() {
		return indexDir;
	}

	/**
	 * @param indexDir the indexDir to set
	 */
	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}

	/**
	 * @return the type
	 */
	public IndexType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(IndexType type) {
		this.type = type;
	}
	
}
