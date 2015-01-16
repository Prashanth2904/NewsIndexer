package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class that represents a parsed query
 * @author Prashanth
 *
 */
public class Query {
	
	private char[] stream;
	
	private int position = 0;
	
	private List<Clause> clauses = new ArrayList<Clause>();
	
	private List<Operator> operators = new ArrayList<Operator>();
	
	public enum IndexType { Term, Category, Author, Place};
	
	public enum Operator { AND, OR, NOT};
	
 	public Query(IndexType indxType, String userQuery, String defOp){
		stream = (userQuery!=null && !userQuery.isEmpty()) ? userQuery.trim().toCharArray() : new char[0];
		this.clauses.add(new Clause(indxType, defOp));
		while(position < stream.length){
			while(Character.isWhitespace(stream[position])) position++;
			if(isNext(Operator.AND)){
				position = position+4;
				operators.add(Operator.AND);
				clauses.add(new Clause(indxType, defOp));
			}else if(isNext(Operator.OR)){
				position = position+3;
				operators.add(Operator.OR);
				clauses.add(new Clause(indxType, defOp));
			}else if(isNext(Operator.NOT)){
				operators.add(Operator.AND);
				clauses.add(new Clause(indxType, defOp));
			}else{
				operators.add(defOp.equalsIgnoreCase("OR") ? Operator.OR : Operator.AND);
				clauses.add(new Clause(indxType, defOp));
			}
		}
	}

	private boolean isNext(Operator op) {
		switch(op){
			case AND:
				return stream[position] == 'A' && stream[position+1] == 'N' && stream[position+2] == 'D';
			case OR:
				return stream[position] == 'O' && stream[position+1] == 'R';
			case NOT:
				return stream[position] == 'N' && stream[position+1] == 'O' && stream[position+2] == 'T';
			default:
				return true;
		}
	}
	
	class Clause{
		private IndexType indxType;
		private Term term;
		private Query query;

		public Clause(IndexType indxType, String defOp){
			this.indxType =  indxType!=null ? indxType : getClauseIndexType();
			int i=0; char[] t = new char[stream.length];
			if(stream[position] == '('){
				position++; int p=1;
				while(p != 0){
					if(stream[position] == '(') p++;
					t[i++] = stream[position++];
					if(stream[position] == ')') p--;
				}
				position++;
				this.query = new Query(this.indxType, String.valueOf(t), defOp);
			}else{
				boolean negation = false;
				if(isNext(Operator.NOT)){negation = true; position = position+4;}
				while(position < stream.length && !isTermEnd()){
					t[i++] = stream[position];
					position++;
				}
				this.term = new Term(this.indxType, String.valueOf(t), negation, defOp);
			}
		}
		 
		private IndexType getClauseIndexType() {
			char[] indxType = new char[9]; int i=0;
			while(i<9 && position+i < stream.length){
				indxType[i] = stream[position+i];
				i++;
			}
			if(String.valueOf(indxType).startsWith("Category:")){
				position = position+9;
				return IndexType.Category;
			}else if(String.valueOf(indxType).startsWith("Author:")){
				position = position+7;
				return IndexType.Author;
			}else if(String.valueOf(indxType).startsWith("Place:")){
				position = position+6;
				return IndexType.Place;
			}else if(String.valueOf(indxType).startsWith("Term:")){
				position = position+5;
				return IndexType.Term;
			}else{
				return null;
			}
		}
		
		private boolean isTermEnd() {
			int i=0; char[] t = new char[9];
			while(i<9 && position+i < stream.length){
				t[i] = stream[position+i]; i++;
			}
				String next = String.valueOf(t);
						return next.startsWith("(")
						|| next.startsWith(")") 
						|| next.startsWith("OR")
						|| next.startsWith("AND")
						|| next.startsWith("NOT") 
						|| next.startsWith("Term:") 
						|| next.startsWith("Place:") 
						|| next.startsWith("Author:") 
						|| next.startsWith("Category:") ;
		}
		
		public IndexType getIndxType() {
			return indxType;
		}

		public Term getTerm() {
			return term;
		}

		public Query getQuery() {
			return query;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if(this.term!=null){
				sb.append(this.term) ;
			}else{
				sb.append("["+this.query.printString()+"]");
			}
			return sb.toString();
		}
	}
	
	class Term{
		private IndexType indxType;
		private boolean negation;
		private String[] queryPhrase;
		private String defOp;
		private String queryTerm;
		
		public Term(IndexType indxType, String term, boolean negation, String defOp){
			this.defOp = defOp;
			this.indxType = indxType!=null ? indxType : IndexType.Term;
			this.negation = negation;
			if(term!=null && (!term.trim().contains(" ") || Pattern.matches("^\".*\"$", term.trim()))){
				if(term.trim().charAt(0)=='"' && term.trim().charAt(term.trim().length()-1)=='"'){
					setQueryTerm(term.trim().substring(1, term.trim().length()-1));
				}else{
					setQueryTerm(term.trim());
				}
			}else{
				this.queryPhrase = getTerms(term.split(" "));
			}
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if(this.queryTerm!=null){
				if(this.negation){sb.append("<");}
				sb.append(this.indxType+":"+this.queryTerm);
				if(this.negation){sb.append(">");}
			}else{
				if(clauses.size()>1) sb.append("[");
				for(int i=0; i < this.queryPhrase.length; i++){
					if(this.negation){sb.append("<");}
					sb.append(this.indxType+":"+this.queryPhrase[i]);
					if(this.negation){sb.append(">");}
					if(i < this.queryPhrase.length-1) sb.append(" "+this.defOp+" ");
				}
				if(clauses.size()>1) sb.append("]");
			}
			return sb.toString();
		}
		
		private String[] getTerms(String[] array){
			List<String> trimmed = new ArrayList<String>();
			for(String s: array){
				if(!s.trim().isEmpty())trimmed.add(s.trim().toLowerCase());
			}
			return trimmed.toArray(new String[trimmed.size()]);
		}

		public IndexType getIndxType() {
			return indxType;
		}

		public boolean isNegation() {
			return negation;
		}

		public String[] getQueryPhrase() {
			return queryPhrase;
		}

		public String getDefOp() {
			return defOp;
		}

		public String getQueryTerm() {
			return queryTerm;
		}
		
		public void setQueryTerm(String queryTerm) {
			this.queryTerm = queryTerm.toLowerCase();
		}
	}
	
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{"+printString()+"}");
		return sb.toString().trim();
	}
	
	public String printString(){
		StringBuffer sb = new StringBuffer();
		int length = clauses.size() > operators.size() ? clauses.size() : operators.size();
		for(int i=0; i<length; i++){
			if(i < clauses.size()) sb.append(" "+clauses.get(i));
			if(i < operators.size()) sb.append(" "+operators.get(i));
		}
		return sb.toString().trim();
	}
	
	public List<Clause> getClauses() {
		return clauses;
	}

	public List<Operator> getOperators() {
		return operators;
	}
	
	public static void main(String[] args) {
		Query q = new Query(null, "Author:torday AND (debt OR currency)", "OR");
		System.out.println(q.toString());
	}
	
}