/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Prashanth
 *
 */
public class PostingsList implements Serializable {
	
	private static final long serialVersionUID = -6517498577456859949L;
	
	//Map<docid, term frequency(t,d)>
	private Map<Integer, Integer> frequencyMap = new TreeMap<Integer, Integer>();
	
	private double inverseDocumentFrequency;
	
	public PostingsList(){
	}
	
	public PostingsList(Integer newDocId){
		add(newDocId);
	}
	
	public void add(Integer docId) {
		if(this.frequencyMap.containsKey(docId)){
			frequencyMap.put(docId, frequencyMap.get(docId)+1);
		}else{
			frequencyMap.put(docId, 1);
		}
	}
	
	public void add(Entry<Integer, Integer> entry) {
		if(this.frequencyMap.containsKey(entry.getKey())){
			frequencyMap.put(entry.getKey(), frequencyMap.get(entry.getKey())+entry.getValue());
		}else{
			frequencyMap.put(entry.getKey(), entry.getValue());
		}
	}
	
	public void remove(Entry<Integer, Integer> entry) {
		if(this.frequencyMap.containsKey(entry.getKey())){
			frequencyMap.remove(entry);
		}
	}
	
	public Map<Integer, Integer> getFrequencyMap() {
		return this.frequencyMap;
	}
	
	public Set<Integer> getPostings() {
		return this.frequencyMap.keySet();
	}
	
	public int getDocumentFrequency(){
		if(this.frequencyMap==null || getPostings()==null) return 0;
		return getPostings().size();
	}

	public int getTotalFrequency(){
		Integer totalFrequency = 0;
		for(Integer docFreq: this.frequencyMap.values()){
			totalFrequency = totalFrequency + docFreq;
		}
		return totalFrequency;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Integer post: this.frequencyMap.keySet()){
			sb.append(post);sb.append("(");sb.append(this.frequencyMap.get(post));sb.append(")");
			sb.append(", ");
		}
		return sb.toString();
	}
	
	public void union(PostingsList pl){
		if(pl==null ||  pl.getFrequencyMap().isEmpty()) return;
		if(this.frequencyMap.isEmpty()){
			this.frequencyMap = pl.getFrequencyMap();
			return;
		}
		PostingsList small;
		PostingsList large;
		if(this.getDocumentFrequency() < pl.getDocumentFrequency()){
			small=this; large=pl;
		}else{
			small=pl; large=this;
		}
		for(Entry<Integer, Integer> entry : small.getFrequencyMap().entrySet()){
			large.add(entry);
		}
		this.frequencyMap = large.frequencyMap;
	}
	
	public void intersect(PostingsList pl){
		if(pl==null || pl.getFrequencyMap().isEmpty()) return;
		if(this.frequencyMap.isEmpty()){
			this.frequencyMap = pl.getFrequencyMap();
			return;
		}
		PostingsList small;
		PostingsList large;
		PostingsList result = new PostingsList();
		if(this.getDocumentFrequency() < pl.getDocumentFrequency()){
			small=this; large=pl;
		}else{
			small=pl; large=this;
		}
		boolean doSNext=true; boolean doLNext=true;
		Iterator<Entry<Integer, Integer>> sml = small.getFrequencyMap().entrySet().iterator();
		Iterator<Entry<Integer, Integer>> lge = large.getFrequencyMap().entrySet().iterator();
		Entry<Integer, Integer> smlCurrent = null;
		Entry<Integer, Integer> lgeCurrent = null;
		while(sml.hasNext() && lge.hasNext() && (doSNext || doLNext)){
			if(doSNext) smlCurrent = sml.next();
			if(doLNext) lgeCurrent = lge.next();
			if(smlCurrent.getKey().equals(lgeCurrent.getKey())){
				result.add(smlCurrent);
				doSNext = doLNext = true;
			}else if(smlCurrent.getKey()<lgeCurrent.getKey()){
				doSNext = true;
				doLNext = false;
			}else{
				doSNext = false;
				doLNext = true;
			}
		}
		this.frequencyMap = result.getFrequencyMap();
	}

	public void calculateIDF(int size) {
		this.inverseDocumentFrequency = getDocumentFrequency()==0 ? 0 : Math.log10(size/getDocumentFrequency());
	}
	
	public double getIDF(){
		return this.inverseDocumentFrequency;
	}

}
