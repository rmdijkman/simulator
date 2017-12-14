// Generated from ConditionGrammar.g4 by ANTLR 4.4

    package nl.tue.bpmn.parser;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConditionGrammarParser}.
 */
public interface ConditionGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ConditionGrammarParser#and_term}.
	 * @param ctx the parse tree
	 */
	void enterAnd_term(@NotNull ConditionGrammarParser.And_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionGrammarParser#and_term}.
	 * @param ctx the parse tree
	 */
	void exitAnd_term(@NotNull ConditionGrammarParser.And_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConditionGrammarParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(@NotNull ConditionGrammarParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionGrammarParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(@NotNull ConditionGrammarParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConditionGrammarParser#or_term}.
	 * @param ctx the parse tree
	 */
	void enterOr_term(@NotNull ConditionGrammarParser.Or_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionGrammarParser#or_term}.
	 * @param ctx the parse tree
	 */
	void exitOr_term(@NotNull ConditionGrammarParser.Or_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConditionGrammarParser#basic_condition}.
	 * @param ctx the parse tree
	 */
	void enterBasic_condition(@NotNull ConditionGrammarParser.Basic_conditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionGrammarParser#basic_condition}.
	 * @param ctx the parse tree
	 */
	void exitBasic_condition(@NotNull ConditionGrammarParser.Basic_conditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConditionGrammarParser#nominal_condition}.
	 * @param ctx the parse tree
	 */
	void enterNominal_condition(@NotNull ConditionGrammarParser.Nominal_conditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionGrammarParser#nominal_condition}.
	 * @param ctx the parse tree
	 */
	void exitNominal_condition(@NotNull ConditionGrammarParser.Nominal_conditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConditionGrammarParser#numeric_condition}.
	 * @param ctx the parse tree
	 */
	void enterNumeric_condition(@NotNull ConditionGrammarParser.Numeric_conditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionGrammarParser#numeric_condition}.
	 * @param ctx the parse tree
	 */
	void exitNumeric_condition(@NotNull ConditionGrammarParser.Numeric_conditionContext ctx);
}