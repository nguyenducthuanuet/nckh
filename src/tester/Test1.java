package tester;

public class Test1 {
	public static boolean checkBracket(String exp) {
		int length = exp.length();
		int open = 0;
		for(int i = 0; i < length; i++) {
			if(exp.charAt(i) == '(')
				open++;
			else if(exp.charAt(i) == ')')
				open--;
			if(open < 0)
				return false;
		}
			
		if(open == 0)
			return true;
		else
			return false;
	}
	
	public static boolean checkDoubleBracket(String exp) {
		int length = exp.length();
		int open = 0;
		for(int i = 1; i < length; i++) {
			if( exp.substring(i-1, i+1).equals("((") )
				open++;
			else if(exp.substring(i-1, i+1) == "))" || open > 0)
				return false;
		}
			
		return true;
	}
	
	
	private static int count = 0;
}
