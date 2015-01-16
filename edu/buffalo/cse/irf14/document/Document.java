/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Prashanth
 * Wrapper class that holds {@link FieldNames} to value mapping
 */
public class Document {

	//Sample implementation - you can change this if you like
	private HashMap<FieldNames, String[]> map;
	
	/**
	 * Default constructor
	 */
	public Document() {
		map = new HashMap<FieldNames, String[]>();
	}
	
	/**
	 * Method to set the field value for the given {@link FieldNames} field
	 * @param fn : The {@link FieldNames} to be set
	 * @param o : The value to be set to
	 */
	public void setField(FieldNames fn, String... o) {
		if(o == null){
			o = new String[10];
		}
		map.put(fn, o);
	}
	
	/**
	 * Method to get the field value for a given {@link FieldNames} field
	 * @param fn : The field name to query
	 * @return The associated value, null if not found
	 */
	public String[] getField(FieldNames fn) {
		return map.get(fn);
	}
	
	public Set<FieldNames> getValidFields(){
		Set<FieldNames> fNames = new HashSet<FieldNames>();
		for(FieldNames fName : this.map.keySet()){
			if(isValid(fName)){ fNames.add(fName);}
		}
		return fNames;
	}
	
	public boolean isValid(FieldNames fieldName) {
		return (map.containsKey(fieldName) && map.get(fieldName).length != 0);
	}
	
	public int numOfTerms(){
		int numOfTerms = 0;
		for(FieldNames fName : getValidFields()){
			numOfTerms = + map.get(fName).length;
		}
		return numOfTerms;
	}
}
