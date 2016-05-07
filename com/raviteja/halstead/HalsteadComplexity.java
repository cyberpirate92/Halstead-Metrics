import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class HalsteadComplexity
{
	public static ArrayList<String> reorderVariables(ArrayList<String> variables)
	{
		int[] lengths = new int[variables.size()];
		for(int i=0;i<variables.size();i++)
		{
			lengths[i] = variables.get(i).length();
		}
		for(int i=0;i<lengths.length;i++)
		{
			for(int j=i+1;j<lengths.length;j++)
			{
				if(lengths[i] < lengths[j])
				{
					int temp = lengths[i];
					lengths[i] = lengths[j];
					lengths[j] = temp;

					String tmp_var = variables.get(i);
					variables.set(i,variables.get(j));
					variables.set(j,tmp_var);
				}
			}
		}
		return variables;
	}
	public static ArrayList<String> extractConstants(String line) // extract constants from string
	{
		boolean continueFlag = false;
		ArrayList<String> extracted = new ArrayList<String>();
		String temp = "";
		for(int i=0;i<line.length();i++)
		{
			if(line.charAt(i) >= '0' && line.charAt(i) <= '9')
			{
				if(!continueFlag)
					continueFlag = !continueFlag;
				temp = temp + line.charAt(i) + "";
			}
			else
			{
				if(continueFlag)
				{
					extracted.add(temp);
					temp = "";
					continueFlag = !continueFlag;
				}
			}
		}
		return extracted;
	}
	public static Map<String,Integer> getUniqueCount(ArrayList<String> list)
	{
		Map<String,Integer> uniqueList = new HashMap<String,Integer>();
		for(int i=0;i<list.size();i++)
		{
			String s = list.get(i);
			if(!uniqueList.containsKey(s))
			{
				int count = 0;
				for(int j=0;j<list.size();j++)
				{
					if(list.get(j).equals(s))
						count++;
				}
				uniqueList.put(s, count);
			}
		}
		return uniqueList;
	}
	public static void displayMetrics(int N1,int N2,int n1,int n2)
	{
		int N,n;
		float V,D,L,E,T,B;
		
		N = N1+N2;
		n = n1+n2;
		V = N * (float)( Math.log(n) / Math.log(2));
		D = (n1/2)*(N2/n2);
		L = 1/D;
		E = V*D;
		T = E/18;
		B = (float)(Math.pow(E, 2/3)/3000);
		
		System.out.println("\t[N] Program Length      : "+N);
		System.out.println("\t[n] Vocabulary Size     : "+n);
		System.out.println("\t[V] Program Volume      : "+V);
		System.out.println("\t[D] Difficulty          : "+D);
		System.out.println("\t[K] Program Level       : "+L);
		System.out.println("\t[E] Effort to implement : "+E);
		System.out.print("\t[T] Time to implement   : ");
		System.out.format("%-10.5f%n", T);
		System.out.print("\t[B] # of delivered bugs : ");
		System.out.format("%-10.5f%n\n", B);
		
	}
	public static void main(String[] args)
	{
		try
		{
			String[] keywords = { "scanf","printf","main","static" }; 	// datatypes shouldnt be added to keywords 
			String[] datatypes = { "int","float","double","char"};
			ArrayList<String> operators = new ArrayList<String>();
			ArrayList<String> operands = new ArrayList<String>();			
			ArrayList<String> variables = new ArrayList<String>();

			int operatorCount = 0, operandCount = 0;
			boolean skipFlag = false;
			BufferedReader reader = new BufferedReader(new FileReader(new File("code.c")));
			String line;

			while((line = reader.readLine()) != null)
			{
				line = line.trim();
				for(String keyword : keywords)
				{
					if(line.startsWith(keyword))
					{
						line = line.substring(0+keyword.length());
						operators.add(keyword);
						operatorCount++;
					}
				}
				for(String datatype : datatypes)
				{
					if(line.startsWith(datatype))
					{
						operators.add(datatype);
						operatorCount++;
						int index = line.indexOf(datatype);
						line = line.substring(index+datatype.length(),line.length()-1);  // -1 to ignore the semicolon
						String[] vars = line.split(",");
						for(String v : vars)
						{
							v = v.trim();
							variables.add(v);
						}
					}
				}
				variables = reorderVariables(variables);  // very important !
				skipFlag = false;
				for(int i=0;i<line.length();i++)
				{
					if(line.charAt(i) >= 'A' && line.charAt(i) <= 'Z' || line.charAt(i) >= 'a' && line.charAt(i) <= 'z' || line.charAt(i) >= '0' && line.charAt(i) <= '9' || line.charAt(i) == ' ' || line.charAt(i) == ',' || line.charAt(i)==';' || line.charAt(i) == '(' || line.charAt(i) == '{')
					{
					}
					else if(line.charAt(i) == ')'  )
					{
						if(skipFlag == false)
						{
							operatorCount++;
							operators.add("()");
						}
					}
					else if(line.charAt(i) == '}'  )
					{
						if(skipFlag == false)
						{
							operatorCount++;
							operators.add("{}");
						}
					}
					else if(line.charAt(i) == '"')   // for detecting double quotes
					{
						skipFlag = !skipFlag;
						if(skipFlag)
							operandCount++;
						else
						{
							int startIndex = line.indexOf("\"");
							int endIndex = line.lastIndexOf("\"");
							operands.add(line.substring(startIndex,endIndex+1));
						}
					}
					else
					{
						if(!skipFlag)
						{
							operators.add(line.charAt(i)+"");
							operatorCount++;
						}
					}
				}
				// removing string literals from line if any
				if(line.contains("\""))
				{
					int startIndex = line.indexOf("\"");
					int endIndex = line.lastIndexOf("\"");
					line = line.substring(0, startIndex) + line.substring(endIndex+1);
				}
				for(String variable : variables)
				{
					while(line.contains(variable))
					{
						int index = line.indexOf(variable);
						line = line.substring(0, index) + line.substring(index+variable.length(), line.length());
						operands.add(variable);
						operandCount++;
					}
				}
				// checking for constants 
				operands.addAll(extractConstants(line));
			}
			System.out.println("Operators identified : ");
			for(String o : operators)
				System.out.println("\t"+o);
			System.out.println("Operands identified : ");
			for(String o : operands)
				System.out.println("\t"+o);
			System.out.println("Variables identified : ");
			for(String v : variables)
				System.out.println("\t"+v);
			System.out.println("Number of operators (N1) " + operators.size());
			System.out.println("Number of operands  (N2) " + operands.size());
						
			Map<String,Integer> uniqueOperators = HalsteadComplexity.getUniqueCount(operators);
			Map<String,Integer> uniqueOperands = HalsteadComplexity.getUniqueCount(operands);
			System.out.println("Number of unique operators (n1) "+uniqueOperators.size());
			System.out.println("Number of unique operands (n2) "+uniqueOperands.size());
			
			displayMetrics(operators.size(),operands.size(),uniqueOperators.size(),uniqueOperands.size());
			reader.close();
		}
		catch(IOException ioe)
		{
			System.out.println(ioe);
		}
	}
}