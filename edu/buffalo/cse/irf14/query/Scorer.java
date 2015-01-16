package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.index.PostingsList;

/**
 * @author Prashanth
 *
 */
public class Scorer {
	
	private double qdenm;
	
	private double ddenm;
	
	private double deno;
	
	public List<ResultDocument> rankDocuments(char mode, ScoringModel model, PostingsList pl,
		Map<String, PostingsList> qTermMap, Map<Integer, Map<String, Integer>> iPostings) {
		if(model.equals(ScoringModel.TFIDF)){ return rankDocuments(pl, qTermMap, iPostings);}	
		float k_1 = 1.2f;
        float k_3 = 8f;
        float b = new Float(0.75d);
        float Lavg = 0;
        for(Entry<Integer, Map<String, Integer>> p :iPostings.entrySet()){
        	Lavg= Lavg+ p.getValue().size();
        }
		List<ResultDocument> scoredDocuments = new ArrayList<ResultDocument>();
		Float deno = new Float(0);
		for(Entry<Integer, Integer> doc: pl.getFrequencyMap().entrySet()){
			Float relevancy = new Float(0);
			for(Term query: calculateQueryTermWeights(qTermMap, doc.getKey())){
				Integer N = iPostings.size();
				float dft = 0;
				for(Map<String, Integer> p :iPostings.values()){
		        	if(p.containsKey(query)) dft++;
		        }
				Integer tfd = iPostings.get(doc.getKey()).get(query.getTerm());
				float ft = (k_1+1)*(k_3+1)*(tfd==null?0:tfd);
				int Ld = iPostings.get(doc.getKey())!=null?iPostings.get(doc.getKey()).size():1;
				float fb = k_1*((1-b)+b*(Ld/Lavg))+(tfd!=null?tfd:1);
				if(fb!=0 && dft!=0)relevancy = (float) (relevancy + (Math.log10(N/dft)*(ft/fb)));
			}
			deno = deno + (relevancy*relevancy);
			scoredDocuments.add(new ResultDocument(doc.getKey(), relevancy));
		}
		for(ResultDocument d: scoredDocuments){
			d.setRelevancy(d.getRelevancy()/new Float(Math.sqrt(deno)).floatValue());
		}
		Collections.sort(scoredDocuments, new Comparator<ResultDocument>(){
			@Override
			public int compare(ResultDocument o1, ResultDocument o2) {
				return o2.getRelevancy().compareTo(o1.getRelevancy());
			}});
		return scoredDocuments;
	}
	
	public List<ResultDocument> rankDocuments(PostingsList pl, Map<String, PostingsList> qTermMap, Map<Integer, Map<String, Integer>> iPostings) {
		List<ResultDocument> scoredDocuments = new ArrayList<ResultDocument>();
		for(Entry<Integer, Integer> doc: pl.getFrequencyMap().entrySet()){
			Float relevancy = calculateRelevancyScore(calculateQueryTermWeights(qTermMap, doc.getKey()), calculateDocumentTermWeights(pl, iPostings));
			scoredDocuments.add(new ResultDocument(doc.getKey(), relevancy));
		}
		for(ResultDocument d: scoredDocuments){
			d.setRelevancy((float) (d.getRelevancy()/Math.sqrt(this.deno)));
		}
		Collections.sort(scoredDocuments, new Comparator<ResultDocument>(){
			@Override
			public int compare(ResultDocument o1, ResultDocument o2) {
				return o2.getRelevancy().compareTo(o1.getRelevancy());
			}});
		return scoredDocuments;
	}

	private Float calculateRelevancyScore(List<Term> queryTerms, List<Term> dcmntTerms) {
		double score = 0;
		for(Term td: dcmntTerms){
			for(Term tq: queryTerms){
				if(td.equals(tq)){
					score = score + (td.getTdIdf()*tq.getTdIdf());
					this.qdenm = this.qdenm +(tq.getTdIdf()*tq.getTdIdf());
					this.ddenm = this.ddenm +(td.getTdIdf()*td.getTdIdf());
				}
			}
		}
		this.deno = this.deno + (score*score);
		return (float) score;
	}

	private List<Term> calculateQueryTermWeights(Map<String, PostingsList> qTermMap, Integer docId) {
		List<Term> queryTerms = new ArrayList<Term>();
		for(Entry<String, PostingsList> entry: qTermMap.entrySet()){
			if(entry.getValue().getFrequencyMap()!=null && entry.getValue().getFrequencyMap().get(docId)!=null){
			queryTerms.add(new Term(entry.getKey(), entry.getValue().getFrequencyMap().get(docId), entry.getValue().getIDF()));
			}
		}
		return queryTerms;
	}
	
	private List<Term> calculateDocumentTermWeights(PostingsList pl, Map<Integer, Map<String, Integer>> iPostings) {
		List<Term> dcmntTerms = new ArrayList<Term>();
		for(Entry<Integer, Integer> doc: pl.getFrequencyMap().entrySet()){
			Map<String, Integer> termTfMap = iPostings.get(doc.getKey());
			for(Entry<String, Integer> termTf: termTfMap.entrySet()){
				dcmntTerms.add(new Term(termTf.getKey(), termTf.getValue(), pl.getIDF()));
			}
		}
		return dcmntTerms;
	}
	
	class Term{
		private String term;
		public String getTerm() {
			return term;
		}

		private int termFreq;
		private double idf;
		private double nTdIdf;
		
		public Term(String term, int termFreq, double idf){
			this.term = term;
			this.termFreq = termFreq;
			this.idf = idf;
		}

		public double getTdIdf() {
			double tf = Math.log10(this.termFreq)>0 ? 1+Math.log10(this.termFreq) : 0;
			this.nTdIdf = tf*idf;
			return nTdIdf;
		}
		
		public void setTdIdf(double nTdIdf) {
			this.nTdIdf = nTdIdf;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
		    if (obj == this) return true;
		    if (!(obj instanceof Term)) return false;
		    Term other = (Term)obj;
			return this.term.equalsIgnoreCase(other.term);
		}
	}
	
}
