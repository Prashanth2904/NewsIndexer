/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * Analyzer for the content field.
 * 
 * @author Prashanth
 */
public class ContentAnalyzer implements Analyzer {

	private TokenStream stream = new TokenStream();

	/**
	 * 
	 */
	public ContentAnalyzer(TokenStream stream) {
		this.stream = stream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		TokenFilterFactory tfactory = TokenFilterFactory.getInstance();
		TokenFilter currentFilter;
		
		currentFilter = tfactory.getFilterByType(TokenFilterType.ACCENT, this.stream);
		while(currentFilter.increment());
		
		currentFilter = tfactory.getFilterByType(TokenFilterType.SYMBOL, currentFilter.getStream());
		while(currentFilter.increment());
		
		currentFilter = tfactory.getFilterByType(TokenFilterType.SPECIALCHARS, currentFilter.getStream());
		while(currentFilter.increment());
		
		currentFilter = tfactory.getFilterByType(TokenFilterType.NUMERIC, currentFilter.getStream());
		while(currentFilter.increment());
		
		currentFilter = tfactory.getFilterByType(TokenFilterType.STOPWORD, currentFilter.getStream());
		while(currentFilter.increment());
		
		this.stream = currentFilter.getStream();
		for(Token token: this.stream.getTokenList()){
			token.setTermText(token.getTermText().toLowerCase());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#getStream()
	 */
	@Override
	public TokenStream getStream() {
		return this.stream;
	}

}
