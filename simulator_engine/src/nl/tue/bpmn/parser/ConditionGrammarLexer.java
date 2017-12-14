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
		T__8=1, T__7=2, T__6=3, T__5=4, T__4=5, T__3=6, T__2=7, T__1=8, T__0=9, 
		DATA_ITEM=10, NOMINAL_VALUE=11, COMPARATOR=12, NUMBER=13, WS=14;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'"
	};
	public static final String[] ruleNames = {
		"T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", 
		"DATA_ITEM", "NOMINAL_VALUE", "COMPARATOR", "NUMBER", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\20X\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3"+
		"\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\7\13:\n\13\f\13\16\13=\13\13\3\f\3\f\7\fA\n\f\f\f\16\fD\13\f\3\r"+
		"\3\r\3\r\3\r\3\r\5\rK\n\r\3\16\6\16N\n\16\r\16\16\16O\3\17\6\17S\n\17"+
		"\r\17\16\17T\3\17\3\17\2\2\20\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\3\2\7\3\2C\\\4\2C\\c|\3\2c|\3\2\62;\5\2\13"+
		"\f\17\17\"\"]\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3\37\3\2\2\2\5\"\3"+
		"\2\2\2\7%\3\2\2\2\t\'\3\2\2\2\13)\3\2\2\2\r+\3\2\2\2\17/\3\2\2\2\21\61"+
		"\3\2\2\2\23\63\3\2\2\2\25\67\3\2\2\2\27>\3\2\2\2\31J\3\2\2\2\33M\3\2\2"+
		"\2\35R\3\2\2\2\37 \7Q\2\2 !\7T\2\2!\4\3\2\2\2\"#\7K\2\2#$\7P\2\2$\6\3"+
		"\2\2\2%&\7*\2\2&\b\3\2\2\2\'(\7+\2\2(\n\3\2\2\2)*\7}\2\2*\f\3\2\2\2+,"+
		"\7C\2\2,-\7P\2\2-.\7F\2\2.\16\3\2\2\2/\60\7.\2\2\60\20\3\2\2\2\61\62\7"+
		"\177\2\2\62\22\3\2\2\2\63\64\7P\2\2\64\65\7Q\2\2\65\66\7V\2\2\66\24\3"+
		"\2\2\2\67;\t\2\2\28:\t\3\2\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<"+
		"\26\3\2\2\2=;\3\2\2\2>B\t\4\2\2?A\t\3\2\2@?\3\2\2\2AD\3\2\2\2B@\3\2\2"+
		"\2BC\3\2\2\2C\30\3\2\2\2DB\3\2\2\2EK\4>@\2FG\7@\2\2GK\7?\2\2HI\7>\2\2"+
		"IK\7?\2\2JE\3\2\2\2JF\3\2\2\2JH\3\2\2\2K\32\3\2\2\2LN\t\5\2\2ML\3\2\2"+
		"\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\34\3\2\2\2QS\t\6\2\2RQ\3\2\2\2ST\3\2"+
		"\2\2TR\3\2\2\2TU\3\2\2\2UV\3\2\2\2VW\b\17\2\2W\36\3\2\2\2\b\2;BJOT\3\b"+
		"\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}