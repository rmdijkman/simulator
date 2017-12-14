// Define the grammar for conditions on flows
grammar DistributionGrammar;
@header {
    package nl.tue.bpmn.parser;
}
distribution	: 'exp' '(' NUMBER ')' | 'N' '(' NUMBER ',' NUMBER ')' | '[' value_series']';
value_series	: value (',' value)*;
value			: '{' TERM ',' NUMBER '%' '}';   
TERM			: [a-zA-Z]+;
NUMBER			: [0-9]+('.'[0-9]+)? ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
