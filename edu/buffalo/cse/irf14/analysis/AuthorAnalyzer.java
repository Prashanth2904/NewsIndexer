/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * Analyzer for the author field.
 * 
 * @author Prashanth
 */
public class AuthorAnalyzer implements Analyzer {
	
	private TokenStream stream = new TokenStream();
	
	/**
	 * Parameterized constructor.
	 */
	public AuthorAnalyzer(TokenStream stream) {
		this.stream = stream;
	}

	/* (non-Javadoc)
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
		
		this.stream = currentFilter.getStream();
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#getStream()
	 */
	@Override
	public TokenStream getStream() {
		return this.stream;
	}

}
