/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

/**
 * Accent filter removes flattens all characters to ASCII format.
 * 
 * @author Prashanth
 *
 */
public class AccentFilter extends TokenFilter {
	
	public AccentFilter(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		while (this.getStream().hasNext()) {
			Token t = this.getStream().next();
			t.setTermText(flattenToAscii(t.getTermText()));
		}
		this.getStream().reset();
		return false;
	}
	
	private String flattenToAscii(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString();
    }
	
}
