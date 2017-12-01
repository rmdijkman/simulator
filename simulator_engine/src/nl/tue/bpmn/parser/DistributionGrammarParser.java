// Generated from DistributionGrammar.g4 by ANTLR 4.7

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
public class DistributionGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, TERM=11, NUMBER=12, WS=13;
	public static final int
		RULE_distribution = 0, RULE_value_series = 1, RULE_value = 2;
	public static final String[] ruleNames = {
		"distribution", "value_series", "value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'exp'", "'('", "')'", "'N'", "','", "'['", "']'", "'{'", "'%'", 
		"'}'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, "TERM", 
		"NUMBER", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "DistributionGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DistributionGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DistributionContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(DistributionGrammarParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(DistributionGrammarParser.NUMBER, i);
		}
		public Value_seriesContext value_series() {
			return getRuleContext(Value_seriesContext.class,0);
		}
		public DistributionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_distribution; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DistributionGrammarListener ) ((DistributionGrammarListener)listener).enterDistribution(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DistributionGrammarListener ) ((DistributionGrammarListener)listener).exitDistribution(this);
		}
	}

	public final DistributionContext distribution() throws RecognitionException {
		DistributionContext _localctx = new DistributionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_distribution);
		try {
			setState(20);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(6);
				match(T__0);
				setState(7);
				match(T__1);
				setState(8);
				match(NUMBER);
				setState(9);
				match(T__2);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(10);
				match(T__3);
				setState(11);
				match(T__1);
				setState(12);
				match(NUMBER);
				setState(13);
				match(T__4);
				setState(14);
				match(NUMBER);
				setState(15);
				match(T__2);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(16);
				match(T__5);
				setState(17);
				value_series();
				setState(18);
				match(T__6);
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

	public static class Value_seriesContext extends ParserRuleContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public Value_seriesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_series; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DistributionGrammarListener ) ((DistributionGrammarListener)listener).enterValue_series(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DistributionGrammarListener ) ((DistributionGrammarListener)listener).exitValue_series(this);
		}
	}

	public final Value_seriesContext value_series() throws RecognitionException {
		Value_seriesContext _localctx = new Value_seriesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_value_series);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			value();
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(23);
				match(T__4);
				setState(24);
				value();
				}
				}
				setState(29);
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

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode TERM() { return getToken(DistributionGrammarParser.TERM, 0); }
		public TerminalNode NUMBER() { return getToken(DistributionGrammarParser.NUMBER, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DistributionGrammarListener ) ((DistributionGrammarListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DistributionGrammarListener ) ((DistributionGrammarListener)listener).exitValue(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(T__7);
			setState(31);
			match(TERM);
			setState(32);
			match(T__4);
			setState(33);
			match(NUMBER);
			setState(34);
			match(T__8);
			setState(35);
			match(T__9);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\17(\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2"+
		"\27\n\2\3\3\3\3\3\3\7\3\34\n\3\f\3\16\3\37\13\3\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\2\2\5\2\4\6\2\2\2\'\2\26\3\2\2\2\4\30\3\2\2\2\6 \3\2\2\2\b"+
		"\t\7\3\2\2\t\n\7\4\2\2\n\13\7\16\2\2\13\27\7\5\2\2\f\r\7\6\2\2\r\16\7"+
		"\4\2\2\16\17\7\16\2\2\17\20\7\7\2\2\20\21\7\16\2\2\21\27\7\5\2\2\22\23"+
		"\7\b\2\2\23\24\5\4\3\2\24\25\7\t\2\2\25\27\3\2\2\2\26\b\3\2\2\2\26\f\3"+
		"\2\2\2\26\22\3\2\2\2\27\3\3\2\2\2\30\35\5\6\4\2\31\32\7\7\2\2\32\34\5"+
		"\6\4\2\33\31\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\5\3"+
		"\2\2\2\37\35\3\2\2\2 !\7\n\2\2!\"\7\r\2\2\"#\7\7\2\2#$\7\16\2\2$%\7\13"+
		"\2\2%&\7\f\2\2&\7\3\2\2\2\4\26\35";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}