/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Prashanth
 * 
 */
public class SymbolFilter extends TokenFilter {

	private static Map<String, String> contractions;

	public SymbolFilter(TokenStream stream) {
		super(stream);
	}

	static {
		contractions = new HashMap<String, String>();
		loadContractions();
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
			t.setTermText(clean(t.getTermText()));
		}
		this.getStream().reset();
		return false;
	}

	public String clean(String word) {
		String step1 = isValid(word) ? replaceContractions(word) : word;
		String step2 = isValid(step1) ? trimPossessives(step1) : step1;
		String step3 = isValid(step2) ? removeApostrophes(step2) : step2;
		String step4 = isValid(step3) ? doHyphens(step3) : step3;
		return step4;
	}

	private String replaceContractions(String word) {
		char ch = word.charAt(word.length() - 1);
		while (ch == '.' || ch == '?' || ch == '!') {
			word = word.substring(0, word.length() - 1);
			ch = word.isEmpty()?' ':word.charAt(word.length() - 1);
		}
		return contractions.containsKey(word.toLowerCase()) ? contractions
				.get(word.toLowerCase()) : word;
	}

	private String trimPossessives(String word) {
		if (!(word.endsWith("'s") || word.endsWith("s'") || word.endsWith("'"))) {
			return word;
		} else if (word.endsWith("'s")) {
			return word.substring(0, word.length() - 2);
		} else {
			return word.substring(0, word.length() - 1);
		}
	}

	private String removeApostrophes(String word) {
		char[] clean = new char[word.length()];
		int i = 0;
		for (char ch : word.toCharArray()) {
			if (ch != '\'') {
				clean[i++] = ch;
			}
		}
		return String.valueOf(clean).trim();
	}

	private String doHyphens(String word) {
		String[] parts = word.split("-");
		if (parts.length > 2) {
			return word.replace("-", "").trim();
		} else if (parts.length < 2) {
			return parts.length != 0 ? parts[0].trim() : "";
		} else if (hasHypenSpacing(parts)) {
			return (parts[0] + " " + parts[1]).trim();
		} else {
			return hasOnlyLetters(parts[0]) && hasOnlyLetters(parts[1]) ? word
					.replace("-", " ").trim() : word;
		}
	}

	private boolean hasHypenSpacing(String[] parts) {
		parts[0] = parts[0].isEmpty()?" ":parts[0];
		parts[1] = parts[1].isEmpty()?" ":parts[1];
		return (parts[0].charAt(parts[0].length() - 1) == ' ' || parts[0]
				.charAt(0) == ' ');
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
	
	private  boolean isValid(String word) {
		return !(word == null || word.trim().isEmpty());
	}

	private static void loadContractions() {
		contractions.put("aren't", "are not");
		contractions.put("can't", "cannot");
		contractions.put("could've", "could have");
		contractions.put("couldn't", "could not");
		contractions.put("couldn't've", "could not have");
		contractions.put("didn't", "did not");
		contractions.put("doesn't", "does not");
		contractions.put("don't", "do not");
		contractions.put("hadn't", "had not");
		contractions.put("hadn't've", "had not have");
		contractions.put("hasn't", "has not");
		contractions.put("haven't", "have not");
		contractions.put("he'd", "he would");
		contractions.put("he'd've", "he would have");
		contractions.put("he'll", "he will");
		contractions.put("he's", "he is");
		contractions.put("how'd", "how would");
		contractions.put("how'll", "how will");
		contractions.put("how's", "how is");
		contractions.put("i'd", "I would");
		contractions.put("i'd've", "I would have");
		contractions.put("i'll", "I will");
		contractions.put("i'm", "I am");
		contractions.put("i've", "I have");
		contractions.put("isn't", "is not");
		contractions.put("it'd", "it would");
		contractions.put("it'd've", "it would have");
		contractions.put("it'll", "it will");
		contractions.put("it's", "it is");
		contractions.put("let's", "let us");
		contractions.put("ma'am", "madam");
		contractions.put("mightn't", "might not");
		contractions.put("mightn't've", "might not have");
		contractions.put("might've", "might have");
		contractions.put("mustn't", "must not");
		contractions.put("must've", "must have");
		contractions.put("needn't", "need not");
		contractions.put("not've", "not have");
		contractions.put("o'clock", "of the clock");
		contractions.put("shan't", "shall not");
		contractions.put("she'd", "she would");
		contractions.put("she'd've", "she would have");
		contractions.put("she'll", "she will");
		contractions.put("she's", "she is");
		contractions.put("should've", "should have");
		contractions.put("shouldn't", "should not");
		contractions.put("shouldn't've", "should not have");
		contractions.put("that's", "that is");
		contractions.put("there'd", "there would");
		contractions.put("there'd've", "there would have");
		contractions.put("there're", "there are");
		contractions.put("there's", "there has / there is");
		contractions.put("they'd", "they had / they would");
		contractions.put("they'd've", "they would have");
		contractions.put("they'll", "they will");
		contractions.put("they're", "they are");
		contractions.put("they've", "they have");
		contractions.put("wasn't", "was not");
		contractions.put("we'd", "we would");
		contractions.put("we'd've", "we would have");
		contractions.put("we'll", "we will");
		contractions.put("we're", "we are");
		contractions.put("we've", "we have");
		contractions.put("weren't", "were not");
		contractions.put("what'll", "what will");
		contractions.put("what're", "what are");
		contractions.put("what's", "what is");
		contractions.put("what've", "what have");
		contractions.put("when's", "when is");
		contractions.put("where'd", "where did");
		contractions.put("where's", "where is");
		contractions.put("where've", "where have");
		contractions.put("who'd", "who would");
		contractions.put("who'll", "who will");
		contractions.put("who're", "who are");
		contractions.put("who's", "who is");
		contractions.put("who've", "who have");
		contractions.put("why'll", "why will");
		contractions.put("why're", "why are");
		contractions.put("why's", "why is");
		contractions.put("won't", "will not");
		contractions.put("would've", "would have");
		contractions.put("wouldn't", "would not");
		contractions.put("wouldn't've", "would not have");
		contractions.put("y'all", "you all");
		contractions.put("you'd", "you would");
		contractions.put("you'd've", "you would have");
		contractions.put("you'll", "you will");
		contractions.put("you're", "you are");
		contractions.put("you've", "you have");
	}

}
