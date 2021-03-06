/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * The abstract class that you must extend when implementing your TokenFilter
 * rule implementations. Apart from the inherited Analyzer methods, we would use
 * the inherited constructor (as defined here) to test your code.
 * 
 * @author Prashanth
 * 
 */
public abstract class TokenFilter implements Analyzer {

	private TokenStream stream = new TokenStream();

	/**
	 * Default constructor, creates an instance over the given TokenStream
	 * 
	 * @param stream
	 *            : The given TokenStream instance
	 */
	public TokenFilter(TokenStream stream) {
		this.stream = stream;
	}

	@Override
	public TokenStream getStream() {
		return this.stream;
	}

}
