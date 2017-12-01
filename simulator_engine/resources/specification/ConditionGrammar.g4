// Define the grammar for conditions on flows
grammar ConditionGrammar;
@header {
    package nl.tue.bpmn.parser;
}
condition			: or_term ('OR' or_term)*;
or_term				: and_term ('AND' and_term)*;
and_term			: basic_condition | 'NOT' basic_condition | '(' condition ')' ;
basic_condition		: nominal_condition | numeric_condition ;
nominal_condition	: DATA_ITEM 'IN' '{' NOMINAL_VALUE (',' NOMINAL_VALUE)* '}' ;
numeric_condition	: DATA_ITEM COMPARATOR NUMBER ;
DATA_ITEM			: [A-Z][a-zA-Z]* ;
NOMINAL_VALUE		: [a-z][a-zA-Z]* ;
COMPARATOR			: '>' | '<' | '=' | '>=' | '<=' ;
NUMBER				: [0-9]+ ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
