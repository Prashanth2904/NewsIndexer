package edu.buffalo.cse.irf14.query;


/**
 * @author Prashanth
 *
 */
public class ResultDocument {

	private int docId;

	private Integer rank;

	private Float relevancy;

	private String title;

	private String snippet;

	private String cat;

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public ResultDocument(int docId, Float relevancy) {
		this.docId = docId;
		this.relevancy = relevancy;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Float getRelevancy() {
		return this.relevancy;
	}

	public int getComparable() {
		return relevancy.intValue();
	}

	public void setRelevancy(Float relevancy) {
		this.relevancy = relevancy;
	}

	public String getTitle() {
		if (title != null)
			return title;
		return "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		if (snippet != null) {
			int l = snippet.length();
			return snippet.substring(0, l / 4);
		}
		return "";
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}
}
