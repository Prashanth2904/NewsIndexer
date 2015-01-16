/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;

/**
 * Filter to remove any numeric tokens.
 * 
 * @author Prashanth
 */
public class NumberFilter extends TokenFilter {

	public NumberFilter(TokenStream stream) {
		super(stream);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		Pattern p = Pattern.compile(".*[A-Za-z].*");
		while (this.getStream().hasNext()) {
			Token t = this.getStream().next();
			if(!p.matcher(t.getTermText()).matches()){
				t.setTermText(removeNumbers(t.getTermBuffer()));
			}
		}
		this.getStream().reset();
		return false;
	}

	private String removeNumbers(char[] termBuffer) {
		char[] clean = new char[64];
		int i=0;
		for(char ch: termBuffer){
			if(!(Character.isDigit(ch) || ch=='.' || ch==',')){
				clean[i++]=ch;
			}
		}
		return String.valueOf(clean).trim();
	}
	
}
