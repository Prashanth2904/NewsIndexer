/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;

/**
 * @author Prashanth
 * 
 */
public class SpecialCharFilter extends TokenFilter {

	public SpecialCharFilter(TokenStream stream) {
		super(stream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		while (this.getStream().hasNext()) {
			Token t = this.getStream().next();
			t.setTermText(clearSpecials(t.getTermText()));
		}
		this.getStream().reset();
		return false;
	}

	private String clearSpecials(String word) {
		if (!isValid(word))
			return "";
		String[] temp = new String[] {};
		int validBegin = 0;
		int validEnd = word.length() - 1;
		while (!(Character.isLetterOrDigit(word.charAt(validBegin)))) {
			if (validBegin < validEnd) {
				validBegin++;
			} else
				break;
		}
		while (!(Character.isLetterOrDigit(word.charAt(validEnd)))) {
			if (validBegin < validEnd) {
				validEnd--;
			} else
				break;
		}

		word = word.substring(validBegin, validEnd + 1);
		if (!isValid(word)) return "";
		if (!Pattern.compile("[^A-Za-z0-9]").matcher(word).matches()) return word;

		int j = 0, k = 0;
		char[] specials = new char[500];
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLetterOrDigit(word.charAt(i))) {
				temp[j++] = String.valueOf(word.charAt(i));
			} else {
				specials[k++] = word.charAt(i);
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append(temp[0]);
		String result = new String();
		for (int i = 0; i < specials.length; i++) {
			switch (specials[i]) {
			case '-':
				result = doHyphens(temp[i], temp[i + 1]);
			default:
				result = doOthers(temp[i], specials[i], temp[i + 1]);
			}
			sb.append(result);
		}

		return sb.toString();
	}

	private String doHyphens(String previous, String next) {
		return hasOnlyLetters(previous) && hasOnlyLetters(next) ? next : "-"
				+ next;
	}

	private String doOthers(String previous, char ch, String next) {
		return hasOnlyLetters(previous) && hasOnlyLetters(next) ? String.valueOf(ch) + next
				: next;
	}

	private boolean hasOnlyLetters(String parts) {
		boolean flag = true;
		for (char ch : parts.toCharArray()) {
			if (!Character.isLetter(ch)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	private boolean isValid(String word) {
		boolean onlySpecialChars = true;
		for(char ch: word.toCharArray()){
			if(Character.isLetterOrDigit(ch)){
				onlySpecialChars = false;
				break;
			}
		}
		return !((word == null || word.trim().isEmpty()) || onlySpecialChars);
	}

}
