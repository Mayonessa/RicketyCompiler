package cse340;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Semantic 
{
	public Hashtable<String, Vector<String>> table=new Hashtable<String,Vector<String>>();//to keep track of initialized variables and scope. 
	Stack<String> stack=new Stack<String>();
	String scope;

	//begin cube initialization
	
	//add operator 0
	//minus/mult 1
	//div 2
	//notequal/less than/greater than operator 3
	//assign 4
	
	//int type 0
	//float type 1
	//string type 2
	//char type 3
	//bool type 4
	//void type 5
		//error 6
		//ok 7
	int [][][] cube=new int[5][7][7];//type cube
	{
	for (int i=0;i<5;i++)//operator
	{
		for(int j=0;j<7;j++)//first operand
		{
			for(int k=0;j<7;j++)//second operand
			{
				
				if(i==0&&(j==2&&k<5||j<5&&k==2))//String plus int, bool, float, char, or string evaluates to string.
					cube[i][j][k]=2;
				else if(i<3&&j==0&&k==0)//+-*/ between two ints=int. Assume no remainders.
					cube[i][j][k]=0;
				else if(i<3&&(j==1||k==1))//+=*/ with at least one float operand=float.//fix
					cube[i][j][k]=1;
				else if(i==3&&(j<2&&k<2))// relational operator between two numerical values evaluates to boolean.
					cube[i][j][k]=4;
				else if(i==4&&(j==k))//assignment operator. types must match, evaluates to ok.
					cube[i][j][k]=7;
				else cube[i][j][k]=6;//everything else evaluates to error
			}
		}
	}
	}
	
	public boolean isInitialized(String sr)
	{
		if(table.get(sr)!=null)
		return true;
		else return false;
	}
	
	public String calculateType(String op, String opType1, String opType2)
	{
		int i,j,k;
		i=numerizePlz(op);
		j=numerizePlz(opType1);
		k=numerizePlz(opType2);
		return denumerizePlz(cube[i][j][k]);
	}
	
	public int numerizePlz(String op)
	{
		int answer;
		if(op.equals("+")||op.equals("integer"))
		{
			answer=0;
		}
		else if(op.equals("-")||op.equals("*")||op.equals("float"))
		{
			answer=1;
		}
		else if(op.equals("/")||op.equals("string"))
		{
			answer=2;
		}
		else if(op.equals("<")||op.equals(">")||op.equals("!=")||op.equals("char"))
		{
			answer=3;
		}
		else if(op.equals("=")||op.equals("boolean"))
		{
			answer=4;
		}
		else if(op.equals("void"))
		{
			answer=5;
		}
		else
		{
			answer=6;
		}
		return answer;
	}
	
	public String denumerizePlz(int op)
	{
		String answer;
		
		switch (op)
		{
		case 0: 
			answer="integer";
			break;
		case 1:
			answer="float";
			break;
		case 2:
			answer="string";
			break;
		case 3:
			answer="char";
			break;
		case 4:
			answer="boolean";
			break;
		case 5:
			answer="void";
			break;
		case 7:
			answer="ok";
			break;
		default:
			answer="error";
		}
		return answer;
	}
}
