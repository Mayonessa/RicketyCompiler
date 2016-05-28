# RicketyCompiler
This was an old assignment for building a simple compiler. 
There are several things I would like to change now. 
Particularly, I would like to implement state machines for the lexer class rather than relying on java regex- 
I know it's more work for the same results, but I think using regex had defeated the purpsoe of trying to code a compiler.


Compiler.java 

Compile and run this in command line. The first argument is the input file. 
The second argument is the output file- will either be the documented errors in the input, or an assembly code.

  Keywords: "IF", "else", "WHILE", "SWITCH", "CASE", "return", "integer", "float", "void", "char", "string", "boolean", "true", "false", "print"
  Delimiters: ';', ' ', '}','{', '[',']','(',')',',',':'
  Operators: '+', '-', '*','/', '%','<','>','=','&','|'
  Types: integer, float, char, string, boolean, void
  variable names: Must start with a letter. Can have letters and numbers.

Syntax Rules:

to be described ins eperate document
