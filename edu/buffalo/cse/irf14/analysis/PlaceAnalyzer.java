/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author Prashanth
 *
 */
public class PlaceAnalyzer implements Analyzer {
	
	private TokenStream stream = new TokenStream();
	
	/**
	 * 
	 */
	public PlaceAnalyzer(TokenStream stream) {
		this.stream = stream;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		TokenFilterFactory tfactory = TokenFilterFactory.getInstance();
		TokenFilter currentFilter = tfactory.getFilterByType(TokenFilterType.SPECIALCHARS, this.getStream());
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
