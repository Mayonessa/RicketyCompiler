//package cse340;
import java.util.regex.*;

public class Lexer {
 
  private final static String[] KEYWORDS = {"IF", "ELSE", "WHILE", "SWITCH", "CASE", "return", "integer", "float", "void", "char", "string", "boolean", "true", "false", "PRINT"};
  
  public static String lexer(String string) {
    // 4. IMPLEMENT THE LEXICAL RULES HERE
    // 5. RETURN THE TOKEN FOR THE string received as parameter; 
    // 5. RETURN "ERROR" if the string is not a word
    // (each time hat you detect and ID, search it in the array "keywords". If it exist then it is a keyword, else it is an ID)
	  Pattern integerRule=Pattern.compile("^([1-9][0-9]*)|0$");
	  Pattern floatRule=Pattern.compile("^(([1-9][0-9]*)?\\.[0-9][1-9]*)|(\\.?[1-9][0-9]*((e|E)((-|\\+)?[1-9][0-9]*)))$");
	  Pattern binaryRule=Pattern.compile("^0b(0|1)*$");
	  Pattern octalRule=Pattern.compile("^(0[1-7][0-7]*)|00$");
	  Pattern hexRule=Pattern.compile("^0x[0-9A-F]$");
	  Pattern stringRule=Pattern.compile("^\".*\"$");
	  Pattern charRule=Pattern.compile("^'.'$");
	  Pattern idRule=Pattern.compile("^(\\$|_|[a-zA-Z])(\\$|_|[a-zA-Z]|[0-9])*$");
	  
//begin checking strings for rulecompliance
	  if (string.length()==1&&isOperator(string.charAt(0)))
	  {
		  return "OPERATOR";
	  }
	  else{
	  if(string.length()==1&&isDelimiter(string.charAt(0)))
	  {
		  return "DELIMITER";
	  }
	  else{
	  if(integerRule.matcher(string).matches())
	  {
		  return "INTEGER";
	  }
	  else 
	  {
		if(floatRule.matcher(string).matches()&&!integerRule.matcher(string).matches())
	  		{
			  return "FLOAT";
	  		}
	  	else 
	  	{
	  		if(binaryRule.matcher(string).matches())
	  		{
	  			return "BINARY";
	  		}
	  		else 
	  		{
	  			if(octalRule.matcher(string).matches())
	  			{
	  				return "OCTAL";
	  			}
	  			else 
	  			{
	  				if(hexRule.matcher(string).matches())
	  				{
	  					return "HEXADECIMAL";
	  				}
	  				else 
	  				{
	  					if(stringRule.matcher(string).matches())
	  					{
	  						return "STRING";
	  					}
	  					else 
	  					{
	  						if(charRule.matcher(string).matches())
	  						{
	  							return "CHAR";
	  						}
	  						else 
	  						{
	  							if(idRule.matcher(string).matches())
	  							{
	  								boolean isKeyword=false;
	  								for(int i=0;i<15;i++)
	  								{
	  									if(KEYWORDS[i].equals(string))
	  										isKeyword=true;					
	  								}
	  								if(isKeyword)
	  									return "KEYWORD";
	  								else return "IDENTIFIER";
	  							}
	  							else return "ERROR";
	  						}
	  					}
	  				}
	  			}
	  		}
	  	}
	  }
	  }
	  }
	  
  }

  public static boolean isDelimiter(char c) 
  {
     char [] delimiters = {';', ' ', '}','{', '[',']','(',')',',',':'};
     for (int x=0; x<delimiters.length; x++) 
     {
      if (c == delimiters[x])
          return true;      
     }
     return false;
  }
  
  public static boolean isOperator(char o) 
  {
     char [] operators = {'+', '-', '*','/', '%','<','>','=','&','|'};
     for (int x=0; x<operators.length; x++) 
     {
      if (o == operators[x])
          return true;      
     }
     return false;
  }

}
