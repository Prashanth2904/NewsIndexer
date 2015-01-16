/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Prashanth
 *
 */
public class CapitalizationFilter extends TokenFilter {

	public CapitalizationFilter(TokenStream stream) {
		super(stream);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		caseFold(this.getStream());
		return false;
	}
	
	private void caseFold(TokenStream stream) {

		List<Token> tokenList = new ArrayList<Token>();
		String currentToken;
		
		int i = 0;

		if (stream.getPosition() == -1) {
			if (stream.hasNext()) {
				String fToken = stream.next().toString();
				Pattern pCaps = Pattern.compile("[A-Z]{2,}|[a-z]+[A-Z]");
				Matcher mCaps = pCaps.matcher(fToken);
				if (mCaps.find()) {
					tokenList.add(new Token(fToken));
					i = 1;
				} else {
					fToken = fToken.toLowerCase();
					tokenList.add(new Token(fToken));
					i = 1;
				}

			}
		}
		while (stream.hasNext()) {
			i = 0;
			currentToken = stream.next().toString();
			Pattern p = Pattern.compile("\\.$"); // ending dot
			Matcher m = p.matcher(currentToken);

			if (m.find()) // If Dot is present at the end of token
			{
				if (stream.hasNext()) // Check next Token is there or not
				{
					tokenList.add(new Token(currentToken));
					String Temp = stream.next().toString();
					Pattern pCaps = Pattern.compile("[A-Z]{2,}|[a-z]+[A-Z]");
					Matcher mCaps = pCaps.matcher(Temp);
					if (mCaps.find()) {
						tokenList.add(new Token(Temp));
						i = 1;
					} else {
						Temp = Temp.toLowerCase();
						tokenList.add(new Token(Temp));
						i = 1;
					}
				}
			}

			if (i == 0) {
				p = Pattern.compile("^[A-Z]");
				m = p.matcher(currentToken);
				String Temp = "";
				while (m.find()) {
					Temp = Temp + " " + currentToken;
					if (stream.hasNext()) {
						currentToken = stream.next().toString();
						m = p.matcher(currentToken);
					} else {
						i = 1;
						break;
					}
				}
				Temp = Temp.trim();
				if (Temp != "") {
					tokenList.add(new Token(Temp));
					i = 1;
				}
				currentToken = currentToken.trim();

				if (stream.hasNext()) {
					tokenList.add(new Token(currentToken));
					i = 1;
				}

			}
			if (i == 0) {
				tokenList.add(new Token(currentToken));
			}
		}
		stream.setTokenList(tokenList);
		stream.reset();
	}

}
