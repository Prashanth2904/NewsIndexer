/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author Prashanth
 *
 */
public class CategoryAnalyzer implements Analyzer {

	private TokenStream stream = new TokenStream();
	
	/**
	 * Parameterized constructor
	 */
	public CategoryAnalyzer(TokenStream stream) {
		this.stream = stream;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
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
