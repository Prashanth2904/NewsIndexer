package edu.buffalo.cse.irf14.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.buffalo.cse.irf14.index.Dictionary;
import edu.buffalo.cse.irf14.index.FileNames;
import edu.buffalo.cse.irf14.index.Index;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.InvertedIndex;
import edu.buffalo.cse.irf14.index.PostingsList;
import edu.buffalo.cse.irf14.query.Query.Clause;
import edu.buffalo.cse.irf14.query.Query.Operator;
import edu.buffalo.cse.irf14.query.Query.Term;

/**
 * @author Prashanth
 *
 */
public class QueryEngine {
	
	private String indexDir;
	
	private Map<String, Integer> documentDictionary;
	
	private InvertedIndex authorIndex;
	
	private InvertedIndex categoryIndex;
	
	private InvertedIndex placeIndex;

	private InvertedIndex termIndex;

	private Dictionary authorDictionary;
	
	private Dictionary categoryDictionary;
	
	private Dictionary placeDictionary;
	
	private Dictionary termDictionary;
	
	private Query query;
	
	private Map<Integer, Map<String, Integer>> iPostings;
	
	public QueryEngine(String indexDir){
		this.indexDir = indexDir;
		readDocDictFromDisk();
		readFromDisk(IndexType.AUTHOR);
		readFromDisk(IndexType.CATEGORY);
		readFromDisk(IndexType.PLACE);
		readFromDisk(IndexType.TERM);
		this.iPostings = populateIPostings();
	}
	
	public PostingsList run(String userQuery, String defOp){
		this.query = QueryParser.parse(userQuery, defOp);
		PostingsList result = execute(query);
		result.calculateIDF(this.documentDictionary.size());
		return result;
	}

	private List<String> getQueryTerms(Query query) {
		List<String> terms = new ArrayList<String>();
		for(Clause clause: query.getClauses()){
			if(clause.getQuery()!=null){
				terms.addAll(getQueryTerms(clause.getQuery()));
			}else{
				terms.addAll(getQueryTerms(clause.getTerm()));
			}
		}
		return terms;
	}

	private List<String> getQueryTerms(Term term) {
		List<String> terms = new ArrayList<String>();
		if(term.isNegation()) return terms;
		if(term.getQueryTerm()!=null){
			terms.add(term.getQueryTerm());
		}else{
			for(String s: term.getQueryPhrase()){
				terms.add(s);
			}
		}
		return terms;
	}

	private PostingsList execute(Query query) {
		PostingsList answer = execute(query.getClauses().get(0));
		for(Operator op: query.getOperators()){
			execute(answer, op, query.getClauses().get(query.getOperators().indexOf(op)+1));
		}
		return answer;
	}
	
	private void execute(PostingsList left, Operator op, Clause right) {
		switch(op){
			case OR:
				left.union(execute(right));
				break;
			case AND:
				left.intersect(execute(right));
				break;
		default:
			break;
		}
	}

	private PostingsList execute(Clause clause) {
		if(clause.getTerm() != null){
			return execute(clause.getTerm());
		}else{
			return execute(clause.getQuery());
		}
	}

	private PostingsList execute(Term term) {
		if(term.getQueryTerm() != null){
			return executeQueryTerm(term);
		}else{
			return executeQueryPhrase(term);
		}
	}

	private PostingsList executeQueryTerm(Term term) {
		PostingsList pl = getPostings(term);
		return pl!=null ? pl : new PostingsList();
	}

	private PostingsList executeQueryPhrase(Term term) {
		PostingsList pl = new PostingsList();
		for(String s: term.getQueryPhrase()){
			term.setQueryTerm(s);
			if(term.isNegation() || term.getDefOp().equalsIgnoreCase("AND")){
				pl.intersect(getPostings(term));
			}else{
				pl.union(getPostings(term));
			}
		}
		return pl;
	}
	
	public PostingsList getPostings(Term term) {
		PostingsList result;
		switch(term.getIndxType()){
			case Author:
				if(!this.authorDictionary.getEntries().containsKey(term.getQueryTerm())) return null;
				result = this.authorIndex.getEntries().get(this.authorDictionary.getEntries().get(term.getQueryTerm()));
				break;
			case Category:
				if(!this.categoryDictionary.getEntries().containsKey(term.getQueryTerm())) return null;
				result = this.categoryIndex.getEntries().get(this.categoryDictionary.getEntries().get(term.getQueryTerm()));
				break;
			case Place:
				if(!this.placeDictionary.getEntries().containsKey(term.getQueryTerm())) return null;
				result = this.placeIndex.getEntries().get(this.placeDictionary.getEntries().get(term.getQueryTerm()));
				break;
			default:
			case Term:
				if(!this.termDictionary.getEntries().containsKey(term.getQueryTerm())) return null;
				result = this.termIndex.getEntries().get(this.termDictionary.getEntries().get(term.getQueryTerm()));
				break;
		}
		return term.isNegation() ? getNegation(result) : result;
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
	
	private void readFromDisk(IndexType indexType) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(getDictFileName(indexType)));
			switch(indexType){
				case AUTHOR:
					this.authorDictionary = (Dictionary) ois.readObject();
					break;
				case CATEGORY:
					this.categoryDictionary = (Dictionary) ois.readObject();
					break;
				case PLACE:
					this.placeDictionary = (Dictionary) ois.readObject();
					break;
				default:
				case TERM:
					this.termDictionary = (Dictionary) ois.readObject();
					break;
			}
			ois.close();
			
			ois = new ObjectInputStream(new FileInputStream(getIndexFileName(indexType)));
			switch(indexType){
				case AUTHOR:
					this.authorIndex = (InvertedIndex)ois.readObject();
					break;
				case CATEGORY:
					this.categoryIndex = (InvertedIndex)ois.readObject();
					break;
				case PLACE:
					this.placeIndex = (InvertedIndex)ois.readObject();
					break;
				default:
				case TERM:
					this.termIndex = (InvertedIndex)ois.readObject();
					break;
			}
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
	
	public Map<String, PostingsList> readTermsIntoIndex() {
		Index index = new Index();
		PostingsList answer = new PostingsList();
		for(String term: getQueryTerms(this.query)){
			if(this.authorDictionary.getEntries().containsKey(term)){
				answer.union(this.authorIndex.getEntries().get(this.authorDictionary.getEntries().get(term)));
			}
			if(this.categoryDictionary.getEntries().containsKey(term)){
				answer.union(this.categoryIndex.getEntries().get(this.categoryDictionary.getEntries().get(term)));
			}
			if(this.placeDictionary.getEntries().containsKey(term)){
				answer.union(this.placeIndex.getEntries().get(this.placeDictionary.getEntries().get(term)));
			}
			if(this.termDictionary.getEntries().containsKey(term)){
				answer.union(this.termIndex.getEntries().get(this.termDictionary.getEntries().get(term)));
			}
			answer.calculateIDF(this.documentDictionary.size());
			index.getEntries().put(term, answer);
		}
		return index.getEntries();
	}
	
	private String getDictFileName(IndexType indexType) {
		String path = this.indexDir + File.separator;
		switch(indexType){
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
	
	private String getIndexFileName(IndexType indexType) {
		String path = this.indexDir + File.separator;
		switch(indexType){
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
	
	private String getDocDictFileName() {
		return this.indexDir + File.separator + FileNames.DICT_DOC;
	}
	
	private PostingsList getNegation(PostingsList result) {
		PostingsList neg = new PostingsList();
		Set<Integer> all =  new HashSet<Integer>();
		for(Integer i :this.documentDictionary.values()){
			all.add(i);
		}
		all.removeAll(result.getFrequencyMap().keySet());
		for(Integer i: all){
			neg.add(i);
		}
		return neg;
	}
	
	public String getFileId(Integer docId){
		for(Entry<String, Integer> e: this.documentDictionary.entrySet()){
			if(docId.equals(e.getValue())){
				return e.getKey();
			}
		}
		return null;
	}

	public Map<Integer, Map<String, Integer>> getIPostings() {
		return this.iPostings;
	}
	public Map<Integer, Map<String, Integer>> populateIPostings() {
		Map<Integer, Map<String, Integer>> iPostings = new HashMap<Integer, Map<String, Integer>>();
		for(Entry<Integer, PostingsList> OEntry: this.authorIndex.getEntries().entrySet()){
			for(Entry<Integer, Integer> IEntry: OEntry.getValue().getFrequencyMap().entrySet()){
				addToIPostings(iPostings, IEntry.getKey(), this.authorDictionary.getTerm(OEntry.getKey()), IEntry.getValue());
			}
		}
		for(Entry<Integer, PostingsList> OEntry: this.categoryIndex.getEntries().entrySet()){
			for(Entry<Integer, Integer> IEntry: OEntry.getValue().getFrequencyMap().entrySet()){
				addToIPostings(iPostings, IEntry.getKey(), this.categoryDictionary.getTerm(OEntry.getKey()), IEntry.getValue());
			}
		}
		for(Entry<Integer, PostingsList> OEntry: this.placeIndex.getEntries().entrySet()){
			for(Entry<Integer, Integer> IEntry: OEntry.getValue().getFrequencyMap().entrySet()){
				addToIPostings(iPostings, IEntry.getKey(), this.placeDictionary.getTerm(OEntry.getKey()), IEntry.getValue());
			}
		}
		for(Entry<Integer, PostingsList> OEntry: this.termIndex.getEntries().entrySet()){
			for(Entry<Integer, Integer> IEntry: OEntry.getValue().getFrequencyMap().entrySet()){
				addToIPostings(iPostings, IEntry.getKey(), this.termDictionary.getTerm(OEntry.getKey()), IEntry.getValue());
			}
		}
		return iPostings;
	}

	private void addToIPostings(Map<Integer, Map<String, Integer>> iPostings,
			Integer key, String term, Integer value) {
		Map<String, Integer> iMap = new HashMap<String, Integer>();
		if(iPostings.containsKey(key)){
			iMap = iPostings.get(key);
			if(iMap.containsKey(term)){
				iMap.put(term, iMap.get(term)+value);
			}else{
				iMap.put(term, value);
			}
		}else{
			iMap.put(term, value);
			iPostings.put(key, iMap);
		}
		
	}
	
	public Query getQuery(){
		return this.query;
	}
}
