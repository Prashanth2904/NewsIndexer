/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * Analyzer for the date field.
 * 
 * @author Prashanth
 */
public class NewsDateAnalyzer implements Analyzer {

	private TokenStream stream = new TokenStream();
	
	/**
	 * 
	 */
	public NewsDateAnalyzer(TokenStream stream) {
		this.stream = stream;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
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
