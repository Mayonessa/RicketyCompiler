# RicketyCompiler
This was an old assignment for building a simple compiler. There are several things I would like to change now. Particularly, I would like to implement state machines for the lexer class rather than relying on java regex- I know it's more work for the same results, but I think using regex had defeated the purpsoe of trying to code a compiler.

5 files
Compiler.java 
  main method here, run this. Specify input file in command line.
  main method contains a tokenizer which will take every "token" in input file and add it to an arraylist along with its line number. calls lexer methods. Calls parse method. 

Token.java 
  token class

Parser.java
  parse method code here.
  Primarily checks grammar. Calls semantic method for typechecking.
  The output file is generated here (either "compiled" code in an assembly language, or error messages indicating what is wrong with input file)

Lexer.java

Semantic.java
  primarily does typechecking. I'd implemented a type cube for that. I think I should not have used number codes for the different types. It does make the rest of the code a little unreadable.
      dimension one: type of operand 1
      dimension two: type of operand 2
      dimension three: operator
      contents: the type of the result, or an error should the types be incompatible.



Language Syntax Rules

