package edu.buffalo.cse.irf14.query;

/**
 * @author Prashanth
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	
	/**
	 * Method to parse the given user query into a Query object
	 * 
	 * @param userQuery: The query to parse
	 * @param defaultOperator: The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator){
		Query parsedQuery = null;
		try{
			parsedQuery = new Query(null, userQuery, defaultOperator);
		}catch(RuntimeException e){
			System.out.println("A Runtime Exception Occured");
		}
		return parsedQuery;
	}
}