/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Prashanth
 * 
 */
public class DatesFilter extends TokenFilter {

	public DatesFilter(TokenStream stream) {
		super(stream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.analysis.Analyzer#increment()
	 */
	@Override
	public boolean increment() throws TokenizerException {
		datefilter(this.getStream());
		return false;
	}

	public void datefilter(TokenStream stream) {

		List<Token> tokenList = new ArrayList<Token>();

		String regex = "([,]*\\b[\\d]{1,4}\\b[,]*)|([,]*(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)[,]*)|([,]*[\\d]+[\\s]*(BC|AD)[,]*)|^AD$|^BC$|^AD.$|^BC.$";
		String year = "1900";
		String day = "01";
		String month = "01";
		String modifiedDate = "19000101";
		String hh = "00:";
		String mm = "00:";
		String ss = "00";
		String originalDate = "";
		String originalTime = "";
		String endTag = "";

		Map<String, String> months = new HashMap<String, String>();
		months.put("JAN", "01");
		months.put("FEB", "02");
		months.put("MAR", "03");
		months.put("APR", "04");
		months.put("MAY", "05");
		months.put("JUN", "06");
		months.put("JUL", "07");
		months.put("AUG", "08");
		months.put("SEP", "09");
		months.put("OCT", "10");
		months.put("NOV", "11");
		months.put("DEC", "12");

		int i;
		int j = 2;
		while (stream.hasNext()) {
			j = 2;
			year = "1900";
			day = "01";
			month = "01";
			i = 0;

			String token = stream.next().toString();

			Pattern p3 = Pattern
					.compile("\\b[\\d]{1,2}:[\\d]{1,2}(am|pm|AM|PM)\\b");
			Matcher m3 = p3.matcher(token);
			if (m3.find()) {
				originalTime = token;
				String temp3 = originalTime;
				p3 = Pattern.compile("AM|am");
				m3 = p3.matcher(temp3);
				if (m3.find()) {

					String Temptime = originalTime;
					if (Temptime.contains("."))
						endTag = ".";
					if (Temptime.contains(","))
						endTag = ",";
					Temptime = Temptime.toUpperCase();
					Temptime = Temptime.replace("AM", "");
					String[] timeSplit = Temptime.split(":");
					if (timeSplit.length == 3) {

						hh = timeSplit[0].replace(":", "");
						if (hh.length() == 1)
							hh = "0" + hh;
						mm = timeSplit[1].replace(":", "");
						ss = timeSplit[2];
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						finalTime = finalTime + endTag;
						tokenList.add(new Token(finalTime));
						i = 1;
					}
					if (timeSplit.length == 2) {
						hh = timeSplit[0].replace(":", "");
						if (hh.length() == 1)
							hh = "0" + hh;
						mm = timeSplit[1].replace(":", "");
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						finalTime = finalTime + endTag;
						tokenList.add(new Token(finalTime));
						i = 1;
					}
				}

				p3 = Pattern.compile("PM|pm");
				m3 = p3.matcher(temp3);
				if (m3.find()) {
					String Temptime = originalTime;
					Temptime = Temptime.toUpperCase();
					Temptime = Temptime.replace("PM", "");
					String[] timeSplit = Temptime.split(":");
					if (timeSplit.length == 3) {
						hh = timeSplit[0].replace(":", "");
						int h = Integer.parseInt(hh);
						h = h + 12;
						if (h == 12)
							h = 0;
						hh = "" + h;
						mm = timeSplit[1].replace(":", "");
						ss = timeSplit[2];
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						finalTime = finalTime + endTag;
						tokenList.add(new Token(finalTime));
						i = 1;
					}
					if (timeSplit.length == 2) {
						hh = timeSplit[0].replace(":", "");
						int h = Integer.parseInt(hh);
						h = h + 12;
						if (h == 12)
							h = 0;
						hh = "" + h;
						mm = timeSplit[1].replace(":", "");
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						finalTime = finalTime + endTag;
						tokenList.add(new Token(finalTime));
						i = 1;
					}
				}

			}
			p3 = Pattern.compile("\\b[\\d]{1,2}:[\\d]{1,2}\\b");
			m3 = p3.matcher(token);
			if (m3.find()) {
				originalTime = token;
				if (originalTime.contains("."))
					endTag = ".";
				String temp3 = "";
				if (stream.hasNext()) {

					temp3 = stream.next().toString();
					j = 1;
				}

				p3 = Pattern.compile("AM|am");
				m3 = p3.matcher(temp3);
				if (m3.find()) {
					j = 0;
					String Temptime = originalTime;
					String[] timeSplit = Temptime.split(":");
					if (timeSplit.length == 3) {
						hh = timeSplit[0].replace(":", "");
						if (hh.length() == 1)
							hh = "0" + hh;
						mm = timeSplit[1].replace(":", "");
						ss = timeSplit[2];
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						finalTime = finalTime + endTag;
						tokenList.add(new Token(finalTime));
						i = 1;
					}
					if (timeSplit.length == 2) {
						hh = timeSplit[0].replace(":", "");
						if (hh.length() == 1)
							hh = "0" + hh;
						mm = timeSplit[1].replace(":", "");
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						tokenList.add(new Token(finalTime));
						i = 1;
					}
				}

				p3 = Pattern.compile("PM|pm");
				m3 = p3.matcher(temp3);
				if (m3.find()) {
					String Temptime = originalTime;
					String[] timeSplit = Temptime.split(":");
					if (timeSplit.length == 3) {
						hh = timeSplit[0].replace(":", "");
						int h = Integer.parseInt(hh);
						h = h + 12;
						if (h == 12)
							h = 0;
						hh = "" + h;
						mm = timeSplit[1].replace(":", "");
						ss = timeSplit[2];
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						tokenList.add(new Token(finalTime));

					}
					if (timeSplit.length == 2) {
						hh = timeSplit[0].replace(":", "");
						int h = Integer.parseInt(hh);
						h = h + 12;
						if (h == 12)
							h = 0;
						hh = "" + h;
						mm = timeSplit[1].replace(":", "");
						String finalTime = hh + ":" + mm + ":" + ss;
						finalTime = finalTime.trim();
						finalTime = finalTime.replace(".", "");
						finalTime = finalTime + endTag;
						tokenList.add(new Token(finalTime));
					}
				}

			}

			Pattern p2 = Pattern.compile("^[\\d]{4}-[\\d]{2,4}");
			Matcher m2 = p2.matcher(token);
			if (m2.find()) {
				String temp = token;
				if (temp.contains("."))
					endTag = ".";
				String[] tempp = temp.split("-");
				year = tempp[0].trim();
				String g = "-" + tempp[0].charAt(0) + tempp[0].charAt(1)
						+ tempp[1];
				year = year + month + day + g + month + day;
				year = year.replace(".", "");
				year = year + endTag;
				tokenList.add(new Token(year));
				i = 1;
			}

			if (i == 0) {

				Pattern p1 = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher m = p1.matcher(token);
				if (m.find()) {
					originalDate = m.group(0);
					if (stream.hasNext()) {
						token = stream.next().toString();
						if (token.contains("."))
							endTag = ".";
						if (token.contains(","))
							endTag = ",";
						j = 1;
					}
					m = p1.matcher(token);
					if (m.find()) {
						j = 0;
						originalDate = originalDate + " " + m.group(0);
						if (stream.hasNext()) {
							token = stream.next().toString();
							if (token.contains("."))
								endTag = ".";
							if (token.contains(","))
								endTag = ",";
							j = 1;
						}
						m = p1.matcher(token);
						if (m.find()) {
							j = 0;
							originalDate = originalDate + " " + m.group(0);

						}
					}

					originalDate = originalDate.replace("-", " ");
					//if(originalDate.contains(",")) endTag=",";
					originalDate = originalDate.replace(",", "");

					if (i == 0) {
						p2 = Pattern.compile("^[\\d]{4}$");
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							year = m2.group(0);
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;

						}
					}

					if (i == 0) {
						p2 = Pattern.compile("([\\d]*BC)|[\\d]*\\sBC",
								Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							temp = temp.replace("BC", " ");
							temp = temp.trim();
							if (temp.length() == 1)
								year = "-000" + temp;
							if (temp.length() == 2)
								year = "-00" + temp;
							if (temp.length() == 3)
								year = "-0" + temp;
							if (temp.length() >= 4)
								year = "-" + temp;
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern.compile("(\\d+AD)|(\\d+\\sAD)",
								Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							temp = temp.replace("AD", " ");
							temp = temp.trim();
							if (temp.length() == 1)
								year = "000" + temp;
							if (temp.length() == 2)
								year = "00" + temp;
							if (temp.length() == 3)
								year = "0" + temp;
							if (temp.length() >= 4)
								year = temp;
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{4}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{2}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							year = temp2[0].trim();
							month = temp2[1].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							day = temp2[2].trim();
							if (day.length() == 1)
								day = "0" + day;
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}

					}
					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{1,2}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{4}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							day = temp2[0].trim();
							if (day.length() == 1)
								day = "0" + day;
							month = temp2[1].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = temp2[2].trim();
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{2}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{2}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							day = temp2[0].trim();
							if (day.length() == 1)
								day = "0" + day;
							month = temp2[1].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = "19" + temp2[2].trim();
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{1,2}\\s[\\d]{4}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							day = temp2[1].trim();
							if (day.length() == 1)
								day = "0" + day;
							month = temp2[0].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = temp2[2].trim();
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{2}\\s[\\d]{2}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							day = temp2[1].trim();
							if (day.length() == 1)
								day = "0" + day;
							month = temp2[0].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = "19" + temp2[2].trim();
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}
					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{4}\\s[\\d]{2}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							day = temp2[1].trim();
							if (day.length() == 1)
								day = "0" + day;
							month = temp2[2].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = temp2[0].trim();
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{2}\\s[\\d]{2}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							day = temp2[1].trim();
							if (day.length() == 1)
								day = "0" + day;
							month = temp2[2].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = "19" + temp2[0].trim();
							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{1,2}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							month = temp2[1].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							day = temp2[0].trim();
							if (day.length() == 1)
								day = "0" + day;

							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{1,2}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							month = temp2[0].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							day = temp2[1].trim();
							if (day.length() == 1)
								day = "0" + day;

							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)\\s[\\d]{4}",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							month = temp2[0].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = temp2[1].trim();
							if (day.length() == 1)
								day = "0" + day;

							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

					if (i == 0) {
						p2 = Pattern
								.compile(
										"[\\d]{4}\\s(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEPT|SEP|OCT|NOV|DEC)",
										Pattern.CASE_INSENSITIVE);
						m2 = p2.matcher(originalDate);
						if (m2.find()) {
							String temp = m2.group(0);
							String[] temp2 = temp.split(" ");
							month = temp2[1].trim();
							month = month.substring(0, 3).toUpperCase();
							month = months.get(month);
							year = temp2[0].trim();
							if (day.length() == 1)
								day = "0" + day;

							modifiedDate = year + month + day + endTag;
							tokenList.add(new Token(modifiedDate));
							i = 1;
						}
					}

				}
			}
			if (i == 0) {
				tokenList.add(new Token(token));
			}
			if (j == 1 && i != 0) {
				tokenList.add(new Token(stream.getCurrent().toString()));
			}

		}
		stream.setTokenList(tokenList);
		stream.reset();
	}
}
