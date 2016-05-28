# RicketyCompiler
This was an old assignment for building a simple compiler. There are several things I would like to change now. Particularly, I would like to implement state machines for the lexer class rather than relying on java regex- I know it's more work for the same results, but I think using regex had defeated the purpsoe of trying to code a compiler.

5 files

Compiler.java 

  main method here, run this. Specify input file in command line.
  main method contains a tokenizer which will take every "token" in input file and add it to an arraylist along with its line number. calls lexer methods. Calls parse method. 

Token.java 

  token class

Parser.java

  The output file is generated here (either "compiled" code in an assembly language, or error messages indicating what is wrong with input file)
  
  Language syntax rules are implemented here. 

Lexer.java

Semantic.java

  uses a type cube structure and a symbol table.
  
  typechecking- checks if variable types are compatible with operator.
  
  variable instantiation- checks for use of uninstantiated variables, and variables that are instantiated more than once.



Language Syntax Rules

