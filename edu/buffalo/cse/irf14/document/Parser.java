/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Prashanth
 *  Class that parses a given file into a Document
 */
public class Parser {

	private static final Pattern PATTERN_TITLE_PARSER = Pattern.compile("^[^a-z]*$");
	
	private static final Pattern PATTERN_AUTHOR_PARSER = Pattern.compile(".*AUTHOR.*", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern AUTHOR_NAME_EXTRACTOR = Pattern.compile("(?<=(\\s)(By|by|BY)(\\s))[A-Z][\\w ]*");

	private static final Pattern PATTERN_PLACE_DATE_PARSER = Pattern.compile("[\\t]*[\\s]*[A-Z].*, (JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC) [0-9]* -.*", Pattern.CASE_INSENSITIVE);

	/**
	 * Static method to parse the given file into the Document object
	 * 
	 * @param filename
	 *            : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException
	 *             In case any error occurs during parsing
	 */
	
	public static void main(String[] args) {
		try {
			Document d = parse("F:\\Fall2014\\temp\\ipDir\\acq\\0000027");
			System.out.println(d);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Document parse(String filename) throws ParserException {
		Document doc = new Document();

		File file = null;
		BufferedReader buffReader = null;
		String currentLine;
		boolean parsedForTitle = false;
		boolean parsedForAuthor = false;
		boolean parsedForPlaceAndDate = false;
		StringBuffer content = new StringBuffer();

		try {
			file = new File(filename);
			buffReader = new BufferedReader(new FileReader(file));
			doc.setField(FieldNames.FILEID, file.getName());
			doc.setField(FieldNames.CATEGORY, file.getParentFile().getName());
			
			while ((currentLine = buffReader.readLine()) != null) {
				if(currentLine.trim().isEmpty()) continue;
				if (!parsedForTitle && isTitle(currentLine)) {
					populateTitle(doc, currentLine);
					parsedForTitle = true;
				} else if (!parsedForAuthor && containsAuthorInfo(currentLine)) {
					populateAuthorInfo(doc, currentLine);
					parsedForAuthor = true;
				} else if (!parsedForPlaceAndDate
						&& containsPlaceDateInfo(currentLine)) {
					populatePlaceAndDate(doc, currentLine, content);
					parsedForPlaceAndDate = true;
				} else {
					content.append(" "+currentLine.trim());
				}
			}
			if(content.toString() != null) doc.setField(FieldNames.CONTENT, content.toString().trim());

		} catch (FileNotFoundException e) {
			throw new ParserException();
		} catch (IOException e) {
			throw new ParserException();
		}catch (Exception e) {
			e.printStackTrace();
			throw new ParserException();
		}  finally {
			if (buffReader != null) {
				try {
					buffReader.close();
				} catch (IOException e) {
					throw new ParserException();
				}
			}
		}
		return doc;
	}

	private static boolean isTitle(String potentialTitle) {
		return PATTERN_TITLE_PARSER.matcher(potentialTitle).matches();
	}
	
	private static void populateTitle(Document doc, String title)
			throws IOException {
		if(title != null) doc.setField(FieldNames.TITLE, title.trim());
	}
	
	private static boolean containsAuthorInfo(String currentLine) {
		return PATTERN_AUTHOR_PARSER.matcher(currentLine).matches();
	}

	private static void populateAuthorInfo(Document doc, String readLine) {
		String authorNames = null;
		String authorOrg = null;
		Matcher m = AUTHOR_NAME_EXTRACTOR.matcher(readLine);
		if (m.find()) {
			authorNames = m.group(0);
			Pattern p1 = Pattern.compile("(?<=" + authorNames + ",\\s?)[ A-Z][\\w ]*");
			Matcher m1 = p1.matcher(readLine);
			if (m1.find()) {
				authorOrg = m1.group(0);
			}
		}
		if(authorNames != null) doc.setField(FieldNames.AUTHOR, authorNames.split("and"));
		if(authorOrg != null) doc.setField(FieldNames.AUTHORORG, authorOrg.trim());
	}

	private static boolean containsPlaceDateInfo(String currentLine) {
		return PATTERN_PLACE_DATE_PARSER.matcher(currentLine).matches();
	}

	private static void populatePlaceAndDate(Document doc, String currentLine, StringBuffer content) {
		Pattern p1 = Pattern.compile("[\\w .,]*(?= - )");
		Matcher m1 = p1.matcher(currentLine);
		if (m1.find() && m1.group(0) != null) {
			char[] placeDate = m1.group(0).toCharArray();
			int last = m1.group(0).lastIndexOf(",");
			
			doc.setField(FieldNames.PLACE, String.valueOf(placeDate, 0, last).trim());
			doc.setField(FieldNames.NEWSDATE, String.valueOf(placeDate, last+1, placeDate.length-last-1).trim());
		}

		Pattern p2 = Pattern.compile("(?<= - ).*");
		Matcher m2 = p2.matcher(currentLine);
		if (m2.find()) {
			content.append(m2.group(0).trim());
		}
	}

}
