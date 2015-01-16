/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Prashanth
 *
 */
public class InvertedIndex implements Serializable {

	private static final long serialVersionUID = -8837045017909638264L;

	private Map<Integer, PostingsList> entries;
	
	public InvertedIndex(Map<Integer, PostingsList> map){
		this.entries = map;
	}

	/**
	 * @return the entries
	 */
	public Map<Integer, PostingsList> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(Map<Integer, PostingsList> entries) {
		this.entries = entries;
	}
}
