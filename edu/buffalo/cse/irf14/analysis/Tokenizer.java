/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Prashanth
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	
	private String delimiter;
	
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		this.setDelimiter(" ");
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		this.setDelimiter(delim);
	}
	
	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		if(str == null || str.isEmpty()){
			throw new TokenizerException();
		}
		
		TokenStream ts = new TokenStream();
		List<Token> tokenList = new ArrayList<Token>();
		
		Token token = null;
		
		StringTokenizer tknizr = new StringTokenizer(str, getDelimiter());
		
		while(tknizr.hasMoreTokens()){
			token = new Token(tknizr.nextToken());
			tokenList.add(token);
		}
		
		ts.setTokenList(tokenList);
		return ts;
	}
}
