// Generated from DistributionGrammar.g4 by ANTLR 4.7

    package nl.tue.bpmn.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DistributionGrammarParser}.
 */
public interface DistributionGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DistributionGrammarParser#distribution}.
	 * @param ctx the parse tree
	 */
	void enterDistribution(DistributionGrammarParser.DistributionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DistributionGrammarParser#distribution}.
	 * @param ctx the parse tree
	 */
	void exitDistribution(DistributionGrammarParser.DistributionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DistributionGrammarParser#value_series}.
	 * @param ctx the parse tree
	 */
	void enterValue_series(DistributionGrammarParser.Value_seriesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DistributionGrammarParser#value_series}.
	 * @param ctx the parse tree
	 */
	void exitValue_series(DistributionGrammarParser.Value_seriesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DistributionGrammarParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(DistributionGrammarParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DistributionGrammarParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(DistributionGrammarParser.ValueContext ctx);
}