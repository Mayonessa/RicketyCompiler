package cse340;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Vector;

public class Compiler {

  private static Vector<Token> tokens;  
  
  private static ArrayList<String> split(String line) {
    // 1. SPLIT THE LINE IN STRINGS (WORDS);
    // 2. INSERT EACH WORD IN THE ARRAY strings
    ArrayList<String> strings= new ArrayList<String>();
    String temp="";
    boolean isInString=false;
    for(int i=0; i<line.length();i++)
    {
    	if(line.charAt(i)=='"')//if double quote detected...
    	{
    		if(isInString)//if we are already in string
    		{
    			temp=temp+line.charAt(i);//add the double quote to the end of the string
    			isInString=false;//and finish the string
    		}
    		isInString=true;//if we are not in string, now we are in string.//completely unrealistic, fix.
    	}
    	if(!Lexer.isDelimiter(line.charAt(i))&&!Lexer.isOperator(line.charAt(i))&&line.charAt(i)!=' '&&line.charAt(i)!='\t'&&line.charAt(i)!='\n'||isInString||line.charAt(i)=='!')
    	{
    		temp=temp+line.charAt(i);
    	}
    	else///I suspect the problem is in this block
    	{
    		if(temp.length()!=0||line.charAt(i)=='\n')
    		{
    		strings.add(temp);
			temp="";
			}
    		if(line.charAt(i)!=' '&&line.charAt(i)!='\t'&&line.charAt(i)!='\n')
    		{
    			
    			if(Lexer.isDelimiter(line.charAt(i))||Lexer.isOperator(line.charAt(i)))
    			{
    				/*if(line.charAt(i)=='='&&line.charAt(i-1)=='!')
    				{
    					temp=temp+line.charAt(i);
    					strings.add(temp);
    					temp="";
    				}
    				else*/
    				strings.add(""+line.charAt(i));
    			}
    		}
    	}
    	if((i==line.length()-1)&&!temp.equals(""))
    	{
    		strings.add(temp);
			temp="";
    	}
    }
    return strings;
  }
  
  public static void main(String[] args) throws FileNotFoundException, IOException 
  {

    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    Writer out = new OutputStreamWriter(new FileOutputStream(args[1]));  
    int totalLexicalErrors = 0;
    int lines=0;
    tokens=new Vector<Token>();
    try 
    {            
      String line = br.readLine(); 
      while (line != null) 
      {  
    	  lines++;//line number incremented every new line
        ArrayList<String> strings = split (line);
        for (String string : strings) 
        {
          String token = Lexer.lexer(string);
          Token newt=new Token(string,token,lines);
          tokens.addElement(newt);
          if (token.equals("ERROR")) 
          {
            totalLexicalErrors++;
          }
        //  out.write(newt.getToken()+"	"+newt.getWord()+"	"+newt.getLine()+"\n");//tokenizer output.
        }
        line = br.readLine();  
      }        
    } 
    finally 
    {
      br.close();  
      out.close();
    }   
    Parser par1=new Parser();
    par1.parse(tokens,args);
  }
  
}
