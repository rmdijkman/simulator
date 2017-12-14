// Generated from ConditionGrammar.g4 by ANTLR 4.4

    package nl.tue.bpmn.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConditionGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__8=1, T__7=2, T__6=3, T__5=4, T__4=5, T__3=6, T__2=7, T__1=8, T__0=9, 
		DATA_ITEM=10, NOMINAL_VALUE=11, COMPARATOR=12, NUMBER=13, WS=14;
	public static final String[] tokenNames = {
		"<INVALID>", "'OR'", "'IN'", "'('", "')'", "'{'", "'AND'", "','", "'}'", 
		"'NOT'", "DATA_ITEM", "NOMINAL_VALUE", "COMPARATOR", "NUMBER", "WS"
	};
	public static final int
		RULE_condition = 0, RULE_or_term = 1, RULE_and_term = 2, RULE_basic_condition = 3, 
		RULE_nominal_condition = 4, RULE_numeric_condition = 5;
	public static final String[] ruleNames = {
		"condition", "or_term", "and_term", "basic_condition", "nominal_condition", 
		"numeric_condition"
	};

	@Override
	public String getGrammarFileName() { return "ConditionGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ConditionGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ConditionContext extends ParserRuleContext {
		public List<Or_termContext> or_term() {
			return getRuleContexts(Or_termContext.class);
		}
		public Or_termContext or_term(int i) {
			return getRuleContext(Or_termContext.class,i);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).exitCondition(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12); or_term();
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(13); match(T__8);
				setState(14); or_term();
				}
				}
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Or_termContext extends ParserRuleContext {
		public And_termContext and_term(int i) {
			return getRuleContext(And_termContext.class,i);
		}
		public List<And_termContext> and_term() {
			return getRuleContexts(And_termContext.class);
		}
		public Or_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).enterOr_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).exitOr_term(this);
		}
	}

	public final Or_termContext or_term() throws RecognitionException {
		Or_termContext _localctx = new Or_termContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_or_term);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20); and_term();
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(21); match(T__3);
				setState(22); and_term();
				}
				}
				setState(27);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class And_termContext extends ParserRuleContext {
		public Basic_conditionContext basic_condition() {
			return getRuleContext(Basic_conditionContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public And_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).enterAnd_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).exitAnd_term(this);
		}
	}

	public final And_termContext and_term() throws RecognitionException {
		And_termContext _localctx = new And_termContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_and_term);
		try {
			setState(35);
			switch (_input.LA(1)) {
			case DATA_ITEM:
				enterOuterAlt(_localctx, 1);
				{
				setState(28); basic_condition();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(29); match(T__0);
				setState(30); basic_condition();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 3);
				{
				setState(31); match(T__6);
				setState(32); condition();
				setState(33); match(T__5);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Basic_conditionContext extends ParserRuleContext {
		public Nominal_conditionContext nominal_condition() {
			return getRuleContext(Nominal_conditionContext.class,0);
		}
		public Numeric_conditionContext numeric_condition() {
			return getRuleContext(Numeric_conditionContext.class,0);
		}
		public Basic_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basic_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).enterBasic_condition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).exitBasic_condition(this);
		}
	}

	public final Basic_conditionContext basic_condition() throws RecognitionException {
		Basic_conditionContext _localctx = new Basic_conditionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_basic_condition);
		try {
			setState(39);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(37); nominal_condition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(38); numeric_condition();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Nominal_conditionContext extends ParserRuleContext {
		public TerminalNode DATA_ITEM() { return getToken(ConditionGrammarParser.DATA_ITEM, 0); }
		public TerminalNode NOMINAL_VALUE(int i) {
			return getToken(ConditionGrammarParser.NOMINAL_VALUE, i);
		}
		public List<TerminalNode> NOMINAL_VALUE() { return getTokens(ConditionGrammarParser.NOMINAL_VALUE); }
		public Nominal_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nominal_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).enterNominal_condition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).exitNominal_condition(this);
		}
	}

	public final Nominal_conditionContext nominal_condition() throws RecognitionException {
		Nominal_conditionContext _localctx = new Nominal_conditionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_nominal_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41); match(DATA_ITEM);
			setState(42); match(T__7);
			setState(43); match(T__4);
			setState(44); match(NOMINAL_VALUE);
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(45); match(T__2);
				setState(46); match(NOMINAL_VALUE);
				}
				}
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(52); match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Numeric_conditionContext extends ParserRuleContext {
		public TerminalNode DATA_ITEM() { return getToken(ConditionGrammarParser.DATA_ITEM, 0); }
		public TerminalNode COMPARATOR() { return getToken(ConditionGrammarParser.COMPARATOR, 0); }
		public TerminalNode NUMBER() { return getToken(ConditionGrammarParser.NUMBER, 0); }
		public Numeric_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).enterNumeric_condition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionGrammarListener ) ((ConditionGrammarListener)listener).exitNumeric_condition(this);
		}
	}

	public final Numeric_conditionContext numeric_condition() throws RecognitionException {
		Numeric_conditionContext _localctx = new Numeric_conditionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_numeric_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54); match(DATA_ITEM);
			setState(55); match(COMPARATOR);
			setState(56); match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\20=\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\7\2\22\n\2\f\2\16\2\25"+
		"\13\2\3\3\3\3\3\3\7\3\32\n\3\f\3\16\3\35\13\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\5\4&\n\4\3\5\3\5\5\5*\n\5\3\6\3\6\3\6\3\6\3\6\3\6\7\6\62\n\6\f\6"+
		"\16\6\65\13\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\2\2\b\2\4\6\b\n\f\2\2<\2\16"+
		"\3\2\2\2\4\26\3\2\2\2\6%\3\2\2\2\b)\3\2\2\2\n+\3\2\2\2\f8\3\2\2\2\16\23"+
		"\5\4\3\2\17\20\7\3\2\2\20\22\5\4\3\2\21\17\3\2\2\2\22\25\3\2\2\2\23\21"+
		"\3\2\2\2\23\24\3\2\2\2\24\3\3\2\2\2\25\23\3\2\2\2\26\33\5\6\4\2\27\30"+
		"\7\b\2\2\30\32\5\6\4\2\31\27\3\2\2\2\32\35\3\2\2\2\33\31\3\2\2\2\33\34"+
		"\3\2\2\2\34\5\3\2\2\2\35\33\3\2\2\2\36&\5\b\5\2\37 \7\13\2\2 &\5\b\5\2"+
		"!\"\7\5\2\2\"#\5\2\2\2#$\7\6\2\2$&\3\2\2\2%\36\3\2\2\2%\37\3\2\2\2%!\3"+
		"\2\2\2&\7\3\2\2\2\'*\5\n\6\2(*\5\f\7\2)\'\3\2\2\2)(\3\2\2\2*\t\3\2\2\2"+
		"+,\7\f\2\2,-\7\4\2\2-.\7\7\2\2.\63\7\r\2\2/\60\7\t\2\2\60\62\7\r\2\2\61"+
		"/\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\66\3\2\2\2\65\63"+
		"\3\2\2\2\66\67\7\n\2\2\67\13\3\2\2\289\7\f\2\29:\7\16\2\2:;\7\17\2\2;"+
		"\r\3\2\2\2\7\23\33%)\63";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}