package nl.tue.bpmn.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import nl.tue.bpmn.parser.ConditionGrammarParser.*;

public class ConditionEvaluator {
	
	private Map<String,String> variableToValue;
	
	public ConditionEvaluator(){
		variableToValue = new HashMap<String,String>();
	}
		
	public void setVariableValue(String variable, String value){
		variableToValue.put(variable, value);
	}

	/**
	 * Evaluates the given condition on the stored variable-value combinations.
	 * The condition has the form of the BNF in ConditionGrammar.g4.
	 * 
	 * @return True or False depending on how the condition evaluates, False if the value is not initialized.
	 */
	public Boolean evaluate(String condition) {
		ConditionGrammarLexer lexer = new ConditionGrammarLexer(new ANTLRInputStream(condition));
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    ConditionGrammarParser parser = new ConditionGrammarParser(tokens);
	    return evaluate(parser.condition());
	}
	
	private Boolean evaluate(ConditionContext condition) {
		for (Or_termContext otc: condition.or_term()){
			Boolean sub = evaluate(otc); 
			if (sub == null){
				return null;
			}else if (sub){
				return true;
			}
		}
		return false;
	}
	
	private Boolean evaluate(Or_termContext condition) {
		for (And_termContext otc: condition.and_term()){
			Boolean sub = evaluate(otc);
			if (sub == null){
				return null;
			}else if (!sub){
				return false;
			}
		}
		return true;
	}

	private Boolean evaluate(And_termContext condition) {
		if (condition.getStart().getText().startsWith("NOT")){
			Boolean sub = evaluate(condition.basic_condition()); 
			return (sub == null)?null:!sub; 
		}
		if (condition.getStart().getText().startsWith("(")){
			Boolean sub = evaluate(condition.condition()); 
			return (sub == null)?null:sub;
		}
		if (condition.basic_condition() != null){
			Boolean sub = evaluate(condition.basic_condition()); 
			return (sub == null)?null:sub;
		}
		return false;
	}

	private Boolean evaluate(Basic_conditionContext condition) {
		if (condition.nominal_condition() != null){
			Boolean sub = evaluate(condition.nominal_condition()); 
			return (sub == null)?null:sub;
		}
		if (condition.numeric_condition() != null){
			Boolean sub = evaluate(condition.numeric_condition());
			return (sub == null)?null:sub;
		}
		return false;
	}

	private Boolean evaluate(Nominal_conditionContext condition) {
		String dataItemValue = variableToValue.get(condition.DATA_ITEM().getText());
		if (dataItemValue == null){
			return false;
		}
		for (TerminalNode tn: condition.NOMINAL_VALUE()){
			if (tn.getText().equals(dataItemValue)){
				return true;
			}
		}		
		return false;				
	}

	private Boolean evaluate(Numeric_conditionContext condition) {
		String dataItemValue = variableToValue.get(condition.DATA_ITEM().getText());
		if (dataItemValue == null){
			return false;
		}
		long numericValue = Long.parseLong(dataItemValue);
		long compareValue = Long.parseLong(condition.NUMBER().getText());
		String comparator = condition.COMPARATOR().getText();
		if (comparator.equals("=")){
			return numericValue == compareValue;
		}else if (comparator.equals(">")){
			return numericValue > compareValue;
		}else if (comparator.equals("<")){
			return numericValue < compareValue;
		}else if (comparator.equals(">=")){
			return numericValue >= compareValue;
		}else if (comparator.equals("<=")){
			return numericValue <= compareValue;			
		}
		return false;				
	}
	
	/**
	 * Checks the syntax of the given condition expression.
	 * Returns a list of errors, which is empty if there are no errors. 
	 *  
	 * @param expression A string representing a condition expression in the agreed upon format.
	 * @return a list of errors.
	 */
	public List<String> validate(String condition){
		GrammarErrorHandler errorListener = new GrammarErrorHandler(condition);
		ConditionGrammarLexer lexer = new ConditionGrammarLexer(new ANTLRInputStream(condition));
		lexer.addErrorListener(errorListener);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    ConditionGrammarParser parser = new ConditionGrammarParser(tokens);
	    parser.addErrorListener(errorListener);
	    parser.condition();
	    return errorListener.errors;
	}
	
}
