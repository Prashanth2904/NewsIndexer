/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Prashanth
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{
	
	private List<Token> tokenList = new ArrayList<Token>();

	private int position = -1;

	private Token current;

	/**
	 * Method that checks if there is any Token left in the stream with regards
	 * to the current pointer. DOES NOT ADVANCE THE POINTER
	 * 
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		return !this.tokenList.isEmpty()
				&& this.position < this.tokenList.size() - 1;
	}

	/**
	 * Method to return the next Token in the stream. If a previous hasNext()
	 * call returned true, this method must return a non-null Token. If for any
	 * reason, it is called at the end of the stream, when all tokens have
	 * already been iterated, return null
	 */
	@Override
	public Token next() {
		if (hasNext()) {
			this.setCurrent(this.tokenList.get(++this.position));
		} else {
			this.setCurrent(null);
		}
		return getCurrent();
	}

	/**
	 * Method to remove the current Token from the stream. Note that "current"
	 * token refers to the Token just returned by the next method. Must thus be
	 * NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		if (this.position != -1 || this.position != this.tokenList.size() - 1) {
			this.tokenList.remove(current);
			this.position--;
		}
		this.current = null;
	}

	/**
	 * Method to reset the stream to bring the iterator back to the beginning of
	 * the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		resetPointers();
		while(next()!=null){
			if(!isValid(getCurrent())) remove();
		}
		resetPointers();
	}

	/**
	 * 
	 */
	private void resetPointers() {
		this.position = -1;
		this.current = null;
	}

	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the
	 * iterator currently stands. After appending, the iterator position must be
	 * unchanged Of course this means if the iterator was at the end of the
	 * stream and a new stream was appended, the iterator hasn't moved but that
	 * is no longer the end of the stream.
	 * 
	 * @param stream
	 *            : The stream to be appended
	 */
	public void append(TokenStream stream) {
		if (stream != null) {
			for (Token token : stream.getTokenList()) {
				this.getTokenList().add(token);
			}
		}
	}

	/**
	 * Method to get the current Token from the stream without iteration. The
	 * only difference between this method and {@link TokenStream#next()} is
	 * that the latter moves the stream forward, this one does not. Calling this
	 * method multiple times would not alter the return value of
	 * {@link TokenStream#hasNext()}
	 * 
	 * @return The current {@link Token} if one exists, null if end of stream
	 *         has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		return this.current;
	}
	
	public boolean isValid(Token token) {
		return token.getTermText() != null
				&& !token.getTermText().trim().isEmpty();
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<Token> tokenList) {
		this.tokenList = tokenList;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setCurrent(Token current) {
		this.current = current;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Token t: this.tokenList){
			sb.append(t.getTermText()+",");
		}
		return "TokenStream [tokenList=" + tokenList + ", position=" + position
				+ ", current=" + current + "]";
	}
	
	
}
