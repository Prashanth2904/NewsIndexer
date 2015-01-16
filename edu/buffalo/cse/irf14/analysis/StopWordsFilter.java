/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author Prashanth
 * 
 */
public class StopWordsFilter extends TokenFilter {

	public static final String[] STOP_WORDS = new String[] { "a", "able", "about", "across", "after", "all", "almost", "also", "am", "among",
		"an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does",
		"either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however",
		"i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must","my","neither",
		"no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should",
		"since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us",
		"wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your" };

	public StopWordsFilter(TokenStream stream) {
		super(stream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		while (this.getStream().hasNext()) {
			if (isStopWord(this.getStream().next()))
				this.getStream().remove();
		}
		this.getStream().reset();
		return false;
	}

	private boolean isStopWord(Token token) {
		for (String stopWord : STOP_WORDS) {
			if (token.getTermText().equals(stopWord)) {
				return true;
			}
		}
		return false;
	}
	
}
