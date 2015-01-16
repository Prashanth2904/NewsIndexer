/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Prashanth
 *
 */
public class Dictionary implements Serializable {
	
	private static final long serialVersionUID = 8670211387278156587L;
	
	private Map<String, Integer> entries; 
	
	public Dictionary(Map<String, Integer> map){
		this.entries = map;
	}

	/**
	 * @return the entries
	 */
	public Map<String, Integer> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(Map<String, Integer> entries) {
		this.entries = entries;
	}
	
	public String getTerm(Integer termId){
		for(Entry<String, Integer> e: this.entries.entrySet()){
			if(termId.equals(e.getValue())){
				return e.getKey();
			}
		}
		return null;
	}
}
