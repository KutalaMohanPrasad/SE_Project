/**
 * Define a grammar called Hello
 */
grammar FileNameRule;

@header {
	package sample;
}

r  :  ID ;         // match keyword hello followed by an identifier

ID : ~[0-9]* [A-Za-z_][A-Za-z_0-9]*'.''r''m''d';             // match lower-case identifiers

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

NUM: '0' | [1-9] [0-9]*;



