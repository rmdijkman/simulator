package nl.tue.bpmn.parser;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import nl.tue.bpmn.parser.DistributionGrammarParser.*;
import nl.tue.util.RandomGenerator;

public class DistributionEvaluator {	
	/**
	 * Evaluates a data item distribution and returns a value according to 
	 * the probability distributions specified. 
	 * Examples:
	 * N(4,10) returns a normally distributed number with mean 4 and standard deviation 10
	 * exp(5) returns an exponentially distributed number with lambda 5 
	 * [{high, 10%}, {medium, 20%}, {low, 70%}] returns "high" with probability 10%, "medium" with probability 20%, or "low" with probability 70%
	 *  
	 * @param distribution A string representing the data item distribution in the agreed upon format.
	 * @return a value that is sampled from the specified distribution.
	 */
	public String evaluate(String distribution) {
		DistributionGrammarLexer lexer = new DistributionGrammarLexer(new ANTLRInputStream(distribution));
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    DistributionGrammarParser parser = new DistributionGrammarParser(tokens);
	    return evaluate(parser.distribution());
	}
	
	private String evaluate(DistributionContext dc){
		if (dc.getText().startsWith("exp")){
			long lambda = Long.parseLong(dc.NUMBER(0).getText());
			return Long.toString(RandomGenerator.generateExponential(lambda));
		}else if (dc.getText().startsWith("N")){
			long mu = Long.parseLong(dc.NUMBER(0).getText());
			long sigma = Long.parseLong(dc.NUMBER(1).getText());
			return Long.toString(RandomGenerator.generateNormal(mu,sigma));			
		}else{
			long r = RandomGenerator.generateUniform(100);
			long curr = 0;
			for (ValueContext vc: dc.value_series().value()){
				curr += Long.parseLong(vc.NUMBER().getText());
				if (r < curr){
					return vc.TERM().getText();
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks the syntax of the given distribution specification.
	 * Returns a list of errors, which is empty if there are no errors. 
	 * Valid Examples:
	 * N(4,10) returns a normally distributed number with mean 4 and standard deviation 10
	 * exp(5) returns an exponentially distributed number with lambda 5 
	 * [{high, 10%}, {medium, 20%}, {low, 70%}] returns "high" with probability 10%, "medium" with probability 20%, or "low" with probability 70%
	 *  
	 * @param distribution A string representing the data item distribution in the agreed upon format.
	 * @return a list of errors.
	 */
	public List<String> validate(String distribution){
		GrammarErrorHandler errorListener = new GrammarErrorHandler(distribution);
		DistributionGrammarLexer lexer = new DistributionGrammarLexer(new ANTLRInputStream(distribution));
		lexer.addErrorListener(errorListener);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    DistributionGrammarParser parser = new DistributionGrammarParser(tokens);
	    parser.addErrorListener(errorListener);
	    parser.distribution();
	    return errorListener.errors;
	}
}
