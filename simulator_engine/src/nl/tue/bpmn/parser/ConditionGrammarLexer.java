// Generated from ConditionGrammar.g4 by ANTLR 4.4

    package nl.tue.bpmn.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConditionGrammarLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__9=1, T__8=2, T__7=3, T__6=4, T__5=5, T__4=6, T__3=7, T__2=8, T__1=9, 
		T__0=10, DATA_ITEM=11, NOMINAL_VALUE=12, COMPARATOR=13, NUMBER=14, WS=15;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'"
	};
	public static final String[] ruleNames = {
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "DATA_ITEM", "NOMINAL_VALUE", "COMPARATOR", "NUMBER", "WS"
	};


	public ConditionGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ConditionGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\21d\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\3\3\3\3"+
		"\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\f\3\f\7\f>\n\f\f\f\16\fA\13\f\3\r\3\r\7\rE\n\r\f"+
		"\r\16\rH\13\r\3\16\3\16\3\16\3\16\3\16\5\16O\n\16\3\17\6\17R\n\17\r\17"+
		"\16\17S\3\17\3\17\6\17X\n\17\r\17\16\17Y\5\17\\\n\17\3\20\6\20_\n\20\r"+
		"\20\16\20`\3\20\3\20\2\2\21\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21\3\2\7\3\2C\\\4\2C\\c|\3\2c|\3\2\62;\5"+
		"\2\13\f\17\17\"\"k\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3"+
		"!\3\2\2\2\5$\3\2\2\2\7&\3\2\2\2\t)\3\2\2\2\13+\3\2\2\2\r-\3\2\2\2\17/"+
		"\3\2\2\2\21\63\3\2\2\2\23\65\3\2\2\2\25\67\3\2\2\2\27;\3\2\2\2\31B\3\2"+
		"\2\2\33N\3\2\2\2\35Q\3\2\2\2\37^\3\2\2\2!\"\7Q\2\2\"#\7T\2\2#\4\3\2\2"+
		"\2$%\7\'\2\2%\6\3\2\2\2&\'\7K\2\2\'(\7P\2\2(\b\3\2\2\2)*\7*\2\2*\n\3\2"+
		"\2\2+,\7+\2\2,\f\3\2\2\2-.\7}\2\2.\16\3\2\2\2/\60\7C\2\2\60\61\7P\2\2"+
		"\61\62\7F\2\2\62\20\3\2\2\2\63\64\7.\2\2\64\22\3\2\2\2\65\66\7\177\2\2"+
		"\66\24\3\2\2\2\678\7P\2\289\7Q\2\29:\7V\2\2:\26\3\2\2\2;?\t\2\2\2<>\t"+
		"\3\2\2=<\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@\30\3\2\2\2A?\3\2\2\2B"+
		"F\t\4\2\2CE\t\3\2\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2G\32\3\2\2"+
		"\2HF\3\2\2\2IO\4>@\2JK\7@\2\2KO\7?\2\2LM\7>\2\2MO\7?\2\2NI\3\2\2\2NJ\3"+
		"\2\2\2NL\3\2\2\2O\34\3\2\2\2PR\t\5\2\2QP\3\2\2\2RS\3\2\2\2SQ\3\2\2\2S"+
		"T\3\2\2\2T[\3\2\2\2UW\7\60\2\2VX\t\5\2\2WV\3\2\2\2XY\3\2\2\2YW\3\2\2\2"+
		"YZ\3\2\2\2Z\\\3\2\2\2[U\3\2\2\2[\\\3\2\2\2\\\36\3\2\2\2]_\t\6\2\2^]\3"+
		"\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\b\20\2\2c \3\2\2\2\n"+
		"\2?FNSY[`\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}