package nl.tue.simulatorgui.executor;

public class EvaluationResult {

	/**
	 * Result types of an evaluation. 
	 */
	public enum ResultType {
		/**
		 * A regular result. E.g. the result of the expression '1+1'.
		 */
		RESULT, 
		/**
		 * An error.
		 */
		ERROR, 
		/**
		 * No result. E.g. the 'result' of the expression 'var i = 1'.
		 */
		UNDEFINED
	}
	
	private String result;
	private ResultType type;
	
	/**
	 * Produces an evaluation result of the specified type with the specified text.
	 * 
	 * @param result the text of the evaluation result.
	 * @param type the type of result
	 */
	public EvaluationResult(String result, ResultType type){
		this.result = result;
		this.type = type;
	}

	/**
	 * Produces an evaluation result of the type undefined.
	 */
	public EvaluationResult(){
		this.type = ResultType.UNDEFINED;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the resultType
	 */
	public ResultType getType() {
		return type;
	}
	
}
