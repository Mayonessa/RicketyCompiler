package cse340;

//import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

public class Parser 
{
	Vector <String> instructions=new Vector<String>();//labels and variables need to go first, store all instructions here meanwhile
	static Vector<Token> toke;
	static String[] args1;
	String type, type2, scope1, prim1, prim2, relop;//prims are the types of the primary, used just for typechecking in the cube. 
	//type and type2 are for assigning types.
	int relop2;
	String operator;
	String varName;
	String lab="#e";
	static String label4="";
	int labCount=1;
	int switchCount=1;
	String switchCh="s";
	int pc=1;
	boolean empty=true;//smily in output file.
	Semantic sem=new Semantic();
	int opcode;
	String switchVar;
	
	public void parse(Vector<Token> tokens,String[] arm) 
	{
		args1=arm;
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
			toke = tokens;
			if (program()&&empty)
				out.write(":-D");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	int current=0;
	
	boolean program()
	{
		if(VARSEC())
		{
			try{
			Writer out = new OutputStreamWriter(new FileOutputStream(args1[1],true));
			Enumeration<String> items=sem.table.keys();
			while(items.hasMoreElements())//print all variables to file
			{
				String thing=items.nextElement();
				out.write(thing);
				out.write(","+sem.table.get(thing).elementAt(0)+"\n");
				
			} out.close();
			}
			catch(IOException e)
			{
				
			}
			if (BODY())//labels are printed throughout the body.
			{
				try
				{
					Writer out1 = new OutputStreamWriter(new FileOutputStream(args1[1],true));
				
					out1.write("@\n");
					
					for(int i = 0; i<pc-1;i++)//print all instructions to output file
					{
						out1.write(instructions.elementAt(i));
					}
					out1.write("OPR 1,0\n");
					out1.write("OPR 0,0");
					out1.close();
					
				}
				catch(IOException e)
				{
				}
				return true;
			}
			else return false;
		}
		else
		{
			BODY();
			return false;
		}
	}
	
	boolean VARSEC()
	{
		
			if(TYPE())
			{
				if(IDLIST())
				{
					if(toke.get(current).getWord().equals(";"))
					{
						current++;
						if(!toke.get(current).getWord().equals("{"))
							return VARSEC();
						else
						return true;
					}
					else 
					{
						current--;
							//expected semicolon
						try
						{
							int currLine=toke.get(current).getLine();
							Writer out = new OutputStreamWriter(new FileOutputStream(args1[1],true));
							out.write("Line "+toke.get(current).getLine()+":	Expected semi-colon.\n");
							out.close();
							while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
								current++;
						}
						catch(IOException e)
						{
						}
						return false;
					}
				}
				else
				{
					/*try
					{
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1],true));
						out.write("Line "+toke.get(current).getLine()+":	Expected variable names.\n");
						current+=2;
						out.close();
					}
					catch(IOException e)
					{
					}*/
					return false;//expected variable names
				}
			}
			else
			{
				IDLIST();
				return false;//expected variable section or begin body
			}
	}
	
	boolean BODY()
	{
		if(toke.get(current).getWord().equals("{"))
		{
			current++;
			if(STMT_LIST())
			{
				if(toke.get(current).getWord().equals("}"))
				{
					current++;
					return true;
				}
				else
				{
					int currLine=toke.get(current).getLine();
					try 
					{
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Expected close bracket.\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
					//current--;
					return false;//expected closing brace
				}
			}
			else
				return false;
		}
		else 
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected open bracket.\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			STMT_LIST();
			return false;//expected open curl brace
		}
	}
	
	boolean STMT_LIST() 
	{
		if(STMT())
		{
			if(!toke.get(current).getWord().equals("}")&&current<toke.size()-1)
			{
				return STMT_LIST();
			}
			else return true;
		}
		else return false;//statement expected
	}

	boolean TYPE()
	{
		if(toke.get(current).getWord().equals("boolean"))
		{
			type="boolean";
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals("integer"))
		{
			type="integer";
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals("char"))
		{
			type="char";
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals("float"))
		{
			type="float";
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals("String"))
		{
			type="string";
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals("void"))
		{
			type="void";
			current++;
			return true;
		}
		else
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected type.\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;//error type expected
		}
	}
	
	boolean IDLIST()
	{
		if(toke.get(current).getToken().equals("IDENTIFIER"))
		{
			
			if(sem.table.get(toke.get(current).getWord())==null)//if variable isn't already in symbol table, add it.
			{
				Vector<String> entry=new Vector<String>();
				entry.add(type);
				entry.add("global");
				sem.table.put(toke.get(current).getWord(),entry);
			}
			else
			{
				//semantic error, variable already in table
				try
				{
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1],true));
					out.write("Line "+toke.get(current).getLine()+":	Duplicate Variable.\n");
					out.close();
				}
				catch(IOException e)
				{
				}
			}
			current++;
			if(toke.get(current).getWord().equals(","))
			{
				current++;
				return IDLIST();
			}
			return true;
		}
		else
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected variable name or ID.\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;//id expected
		}
	}
	
	boolean STMT()//don't increment current here, because these tokens are rechecked inside the methods that are called by stmt
	{
		if(toke.get(current).getToken().equals("IDENTIFIER"))
			return ASSIGN();
		else if(toke.get(current).getWord().equals("WHILE"))
			return WHILE();
		else if(toke.get(current).getWord().equals("print"))
			return PRINT();
		else if(toke.get(current).getWord().equals("SWITCH"))
			return SWITCH();
		else if(toke.get(current).getWord().equals("IF"))
			return IF();
		else return false;
	}
	
	boolean WHILE()//check if condition is boolean
	{
		if(toke.get(current).getWord().equals("WHILE"))
		{

			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				
				out.write(lab+labCount+","+pc+"\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			String lbel1=lab+labCount;
			//labelList.put(lab+labCount,pc+"");
			labCount++;
			current++;
			if(CONDITION())
			{
				if(!(sem.calculateType(relop, prim1, prim2).equals("boolean")))
				{
					//semantic error, condition not boolean;
					int currLine=toke.get(current).getLine();
					try 
					{
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Boolean expression expected.\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
				}
				String lab2=lab+labCount;//need to capture this label

				pc++;
				instructions.add("JMC "+lab2+",false\n");
				labCount++;
				if(BODY())
				{
					pc++;
						//Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					instructions.add("JMP "+lbel1+",0\n");//lab is just the string #e, lab2 is the labcount value used to vary the labels
					try 
					{
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write(lab2+","+pc+"\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
					//labelList.put(lab+labCount,pc+"");
					//labCount++;
					
					return true;
				}
				else return false;
			}
			else 
			{
				BODY();
				return false;
			}
		}
		else
		{
			int currLine=toke.get(current).getLine();
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
		}
	}
	
	boolean CONDITION() {//calculate type
		if(PRIMARY())
		{
			prim1=prim2;
			if(RELOP())
			{

				if (PRIMARY())
				{
						pc++;
						instructions.add("OPR "+relop2+",0\n");
				
					return true;
				}
				else return false;
			}
			else return false;
		}
		else
		{
			return false;
		}
		
	}

	boolean RELOP() {
		if(toke.get(current).getWord().equals("<"))
		{
			relop=toke.get(current).getWord();//for cube
			relop2=12;//for assembly
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals(">"))
		{
			relop=toke.get(current).getWord();//for cube
			relop2=11;//for assembly
			current++;
			return true;
		}
		else if(toke.get(current).getWord().equals("!")&&toke.get(current+1).getWord().equals("="))//if the operator is !=, increment twice.
		{
			current++;
			current++;
			relop="!=";
			relop2=13;
			return true;
		}
		else
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected relational operator.\n"+toke.get(current).getWord());
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;//relational operator expected
		}
	}

	boolean PRIMARY() {//stackpush (type)
		String token=toke.get(current).getToken();
		if(token.equals("IDENTIFIER")||token.equals("FLOAT")||token.equals("INTEGER")||token.equals("OCTAL")||token.equals("HEXADECIMAL"))
		{
			if(token.equals("IDENTIFIER")&&sem.isInitialized(toke.get(current).getWord()))
			{
				type=sem.table.get(toke.get(current).getWord()).elementAt(0);//element at 0 is type, at 1 is scope

				pc++;
				empty=false;//guess I don't really need this at all for this assignment, the file won't ever be empty.
				instructions.add("LOD "+ toke.get(current).getWord()+",0\n");
				
			}
			else if(token.equals("FLOAT"))
			{
				type="float";//1
				pc++;
				empty=false;
				instructions.add("LIT "+ toke.get(current).getWord()+",0\n");

			}
			else if(token.equals("INTEGER"))
			{
				type="integer";//1
				pc++;
				empty=false;
				instructions.add("LIT "+ toke.get(current).getWord()+",0\n");
			
			}
			else
			{
				type="error";
				int currLine=toke.get(current).getLine();
				try 
				{
					empty=false;
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line "+currLine+":	Variable "+toke.get(current).getWord()+" not found\n");
					out.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				} 
			}
			prim2=type;
			current++;
			return true;
		}
		else
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected ID or number."+"\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
		}//ID or number expected
		
	}

	boolean IF()//check if condition is boolean
	{
		if(toke.get(current).getWord().equals("IF"))
		{
			current++;
			if(CONDITION())
			{
				pc++;
				instructions.add("JMC "+lab+labCount+",false\n");
				String lab3=lab+labCount;
				if(!(sem.calculateType(relop, prim1, prim2).equals("boolean")))
				{
					//semantic error, condition not boolean;
					int currLine=toke.get(current).getLine();
					try 
					{
						empty=false;
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Boolean expression expected.\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
				}
				if( BODY())
				{
					try 
					{
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write(lab3+","+pc+"\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
					//labelList.put(lab+labCount,pc+"");
					labCount++;
					
					return true;
				}
				else return false;
			}
			else 
			{
				BODY();
				return false;
			}
		}
		else
		{
			int currLine=toke.get(current).getLine();
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
		}
	}
	
	boolean ASSIGN()
	{
		if(toke.get(current).getToken().equals("IDENTIFIER"))
		{
			if(sem.table.get(toke.get(current).getWord())==null)
			{
				//semantic error
				int currLine=toke.get(current).getLine();
				try 
				{
					empty=false;
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line "+currLine+":	Variable "+toke.get(current).getWord()+" not found.\n");
					out.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				} 
			}
			else
			{
				type=sem.table.get(toke.get(current).getWord()).elementAt(0);
				varName=toke.get(current).getWord();
				type2=type;
			}
			current++;
			if(toke.get(current).getWord().equals("="))
			{
				if(toke.get(current+2).getToken().equals("OPERATOR"))//
				{
					current++;
					if(EXPR());
					{
						if(!(type.equals(sem.calculateType(operator, prim1, prim2)))||!(type2.equals(sem.calculateType(operator, prim1, prim2))))
						{
								//semantic error
							int currLine=toke.get(current).getLine();
							try 
							{
								empty=false;
									Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
									out.write("Line "+currLine+":	Type Mismatch.\n");
									out.close();
							} catch (IOException e) 
							{
									e.printStackTrace();
							} 
						}
						//try 
						//{
						empty=false;
						pc++;
									//Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						instructions.add("STO "+varName+",0\n");//line of 
									/*out.close();
						} catch (IOException e) 
						{
									e.printStackTrace();
						}*/
						if(toke.get(current).getWord().equals(";"))
						{
						
							current++;
							return true;
						}
						else
						{
							int currLine=toke.get(current).getLine();
							try 
							{
								Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
								out.write("Line "+currLine+":	Expected Semi-Colon."+"\n");
								out.close();
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							} 
							if(current<toke.size()-1)
								current++;
							return false;
						}
						
					
						
					}
				}
				else
				{
					current++;
					if(PRIMARY())
					{
						//try 
						//{
						empty=false;
						pc++;
								//	Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						instructions.add("STO "+varName+",0\n");//line of 
					//				out.close();
					//	} catch (IOException e) 
					//	{
					//				e.printStackTrace();
					//	}
						if(!type2.equals(prim2))
						{
							int currLine=toke.get(current).getLine();
							try 
							{
								empty=false;
								Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
								out.write("Line "+currLine+":	Type Mismatch.\n");
								out.close();
							} catch (IOException e) 
							{
								e.printStackTrace();
							} 
						}
						else{}
						if(toke.get(current).getWord().equals(";"))
						{
							current++;
							return true;
						}
						else
						{
							int currLine=toke.get(current).getLine();
							try 
							{
								Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
								out.write("Line "+currLine+":	Expected Semi-Colon."+"\n");
								out.close();
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							} 
							if(current<toke.size()-1)
								current++;
							return false;
						}
					}
					else return false;
				}
				//else return false;//current incremented only after a token or word is directly checked.
			}
			else
			{
				int currLine=toke.get(current).getLine();
				try 
				{
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line "+currLine+":	Expected assignment operator.\n");
					out.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				} 
				while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
					current++;
				return false;//equality expected
			}
		}
		else
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected variable name or id.\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;//id expected
		}
	}
	
	boolean EXPR() {//calculate type
		if(PRIMARY())
		{
			prim1=prim2;//prim2(a type) is decided inside primary().
			if(OP())
			{
				operator=toke.get(current-1).getWord();
				if( PRIMARY())//prim2 is decided again, now we have prim1 and prim2, and can compare the two
				{
					empty=false;
					pc++;
					instructions.add("OPR "+opcode+",0\n");//line of 
							
					return true;
				}
				else return false;
			}
			else return false;
		}
		else 
		{
			return false;
		}
	}

	boolean OP() {
		String op=toke.get(current).getWord();
		switch(op)
		{
		case "+":
			current++;
			opcode=2;
			return true;
		case "-":
			current++;
			opcode=3;
			return true;
		case "*":
			current++;
			opcode=4;
			return true;
		case "/":
			current++;
			opcode=5;
			return true;
		default:
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected operator.\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
		}}
		
	}

	boolean SWITCH()
	{
		switchCount++;
		if(toke.get(current).getWord().equals("SWITCH"))
		{
			current++;
			if(toke.get(current).getToken().equals("IDENTIFIER"))
			{
				//Semantic error warnings begin
				if(sem.isInitialized(toke.get(current).getWord()))
				{
					type=sem.table.get(toke.get(current).getWord()).elementAt(0);//element at 0 is type, at 1 is scope
					sem.stack.push(type);
				}
				else
				{
					type="error";
					int currLine=toke.get(current).getLine();
					try 
					{
						empty=false;
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Variable "+toke.get(current).getWord()+" not found\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
					
				}
				if(!(type.equals("integer")))
				{
					int currLine=toke.get(current).getLine();
					try 
					{
						empty=false;
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Incompatible types: "+type+" cannot be converted to integer.\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
				}
				///Semantic error warnings end.
				
				//begin code generation
				
				switchVar=toke.get(current).getWord();
				//end code generation
				//continue parsing
				
				current++;
				if(toke.get(current).getWord().equals("{"))
				{
					current++;
					if(CASE_LIST());
					{
						
						if(!toke.get(current).getWord().equals("}"))///if curly brace, okay. There does not need to be default case
						{

							if(DEFAULT_CASE())
							{
								
								if(toke.get(current).getWord().equals("}"))
								{
									
									current++;
									try 
									{
										Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
										out.write(label4+","+pc+"\n");//finish here
										out.close();
									} catch (IOException e) 
									{
										e.printStackTrace();
									}
									return true;
								}
								else
								{
									int currLine=toke.get(current).getLine();
									try 
									{
										Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
										out.write("Line "+currLine+":	Expected close bracket.\n");
										out.close();
									} catch (IOException e) 
									{
										e.printStackTrace();
									} 
									if(current<toke.size()-1)
									current++;
									return false;
								}
							}
							else
								{
								int currLine=toke.get(current).getLine();
								try 
								{
									Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
									out.write("Line "+currLine+":	Expected close bracket or default case.\n");
									out.close();
								} catch (IOException e) 
								{
									e.printStackTrace();
								} 
								if(current<toke.size()-1)
								current++;
								return false;//expected curly brace or default case.
								}
						}
						else
						{
							try 
							{
								Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
								out.write(label4+","+pc+"\n");//finish here
								out.close();
							} catch (IOException e) 
							{
								e.printStackTrace();
							}
							current++;
							return true;
						}
					}
				}
				else
				{
					int currLine=toke.get(current).getLine();
					try 
					{
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Expected open bracket.\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
					while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
						current++;
					CASE_LIST();
					return false;
				}
			}
			else
			{	
				int currLine=toke.get(current).getLine();
				try 
				{
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line "+currLine+":	Expected variable name or id.\n");
					out.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				} 
				while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
					current++;
				return false;
			}
		}	
		else
		{
			int currLine=toke.get(current).getLine();
			try 
			{
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line "+currLine+":	Expected Switch statement.\n");
				out.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
		}
	}
	
	boolean CASE_LIST() 
	{
		
		boolean case1=CASE();
		if(toke.get(current).getWord().equals("CASE"))
			return CASE_LIST();
		else return case1;
	}
	
	boolean CASE()
	{
		if(toke.get(current).getWord().equals("CASE"))
		{
			current++;
		
			if(toke.get(current).getToken().equals("OCTAL")||toke.get(current).getToken().equals("INTEGER")||toke.get(current).getToken().equals("FLOAT")||toke.get(current).getToken().equals("HEXADECIMAL"))
			{
				String num=toke.get(current).getWord();
				current++;
				if(toke.get(current).getWord().equals(":"))
				{
					current++;
					String label=lab+labCount;
					labCount++;
					pc++;
					instructions.add("LOD "+ switchVar+",0\n");
					pc++;
					instructions.add("LIT "+num+",0\n");
					pc++;
					instructions.add("OPR 15,0\n");
					pc++;
					instructions.add("JMC "+label+",false \n");
					///try this, increment pc after if it doesn't work
					
					if (BODY())
					{
						label4=lab+switchCh+switchCount;
						pc++;
						instructions.add("JMP "+label4+",0\n");//jump out of switch
						labCount++;
						try 
						{
							Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
							out.write(label+","+pc+"\n");//jump here if previos case was no met.
							out.close();
						} catch (IOException e) 
						{
							e.printStackTrace();
						} 
						return true;
					}
					else return false;
				}
				else
				{
					
					int currLine=toke.get(current).getLine();
					try {
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Expected colon.\n");
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
					BODY();
					return false;//colon expected
				}
			}
			else
			{
				int currLine=toke.get(current).getLine();
				try 
				{
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line "+currLine+":	Expected case number."+"\n");
					out.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				} 
				while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
					current++;
				BODY();
				return false;//case number expected
			}
		}
		else
			{
			int currLine=toke.get(current).getLine();
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
			}
	}

	boolean DEFAULT_CASE() {
		if(toke.get(current).getWord().equals("DEFAULT"))
		{
			current++;
			if(toke.get(current).getWord().equals(":"))
			{
				current++;
				return BODY();
			}
			else
			{
				int currLine=toke.get(current).getLine();
				try {
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line:	"+currLine+"	Expected colon.");
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
					current++;
				BODY();
				return false;//colon expected
			}
		
		}
		else
		{
			int currLine=toke.get(current).getLine();
			
			try {
				Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
				out.write("Line:	"+currLine+"	Expected default case or closing bracket.");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
				current++;
			return false;
		}
	}
	int counter;
	boolean PRINT()

	{
		if(toke.get(current).getWord().equals("print"))
		{
			current++;
			if(toke.get(current).getToken().equals("IDENTIFIER"))
			{
				if(sem.isInitialized(toke.get(current).getWord()))
				{
					type=sem.table.get(toke.get(current).getWord()).elementAt(0);//element at 0 is type, at 1 is scope
					sem.stack.push(type);
				}
				else
				{
					type="error";
					int currLine=toke.get(current).getLine();
					try 
					{
						empty=false;
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line "+currLine+":	Variable "+toke.get(current).getWord()+" not found\n");
						out.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 
					
				}
			
					pc++;
					empty=false;
					
					instructions.add("LOD "+ toke.get(current).getWord()+",0\n");
				
				current++;
				if(toke.get(current).getWord().equals(";"))
				{
					current++;
					
					pc++;
					empty=false;
					instructions.add("OPR 21,0\n");
					
					return true;
				}
				else
				{
					int currLine=toke.get(current).getLine();
					try {
						Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
						out.write("Line:	"+currLine+"	Expected Semi-Colon.");
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
					if(current<toke.size()-1)
					current++;
					
					return false;//expected semicolon
				}
			}
			else
			{
				int currLine=toke.get(current).getLine();
				try {
					Writer out = new OutputStreamWriter(new FileOutputStream(args1[1], true));
					out.write("Line:	"+currLine+"	Expected variable name or id.");
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				while((toke.get(current).getLine()==currLine)&&current<toke.size()-1)
					current++;
				return false;//expected Id
			}
		}
		else return false;
	}

}
