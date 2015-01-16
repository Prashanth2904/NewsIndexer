/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Prashanth
 *
 */
public class Index implements Serializable {

	private static final long serialVersionUID = 4254739741839361829L;
	
	private Map<String, PostingsList> entries;
	
	public Index(){
		this.entries = new TreeMap<String, PostingsList>();
	}

	public void add(String term, Integer docId) {
		if(this.entries.containsKey(term)){
			this.entries.get(term).add(docId);
		}else{
			this.entries.put(term, new PostingsList(docId));
		}
	}
	
	/**
	 * @return the entries
	 */
	public Map<String, PostingsList> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(Map<String, PostingsList> entries) {
		this.entries = entries;
	}

}
