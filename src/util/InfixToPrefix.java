package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class InfixToPrefix  {
	String[] elementMath = null;
	List<Variable> list = null;
	
	public static void main(String[] agrs) throws Exception{
		List<Variable> list = new ArrayList<>();
		list.add(new Variable("x", "int"));
		list.add(new Variable("y", "Real"));
		list.add(new Variable("z", "bool"));
		
		String sMath = "(x > 100) & (y < x)";
		System.out.println(new InfixToPrefix(list).getOutput(sMath));
	}
	public InfixToPrefix(){
		
	}
	
	public InfixToPrefix(List<Variable> list){
		this.list = list;
	}

	public String getOutput(String input) throws Exception{
		//String[] agrs = input.split("");
		String sMath, elementMath[] = null;
		InfixToPrefix IFP = new InfixToPrefix();
		String output="";
		//for(int i = 0; i < agrs.length; i++){
			sMath = input;
			if(sMath.contains(">="))
				sMath = sMath.replaceAll(">=", "@");
			
			if(sMath.contains("<="))
				sMath.replaceAll("<=", "~");
			elementMath = IFP.processString(sMath);	//	tach bieu thuc thanh cac phan tu
			
			
			elementMath = IFP.reverse(elementMath);
			
			elementMath = IFP.postfix(elementMath);	// dua cac phan tu ve dang postfix
			
			elementMath = IFP.reverse(elementMath);
			IFP.elementMath = elementMath;
			IFP.elementMath[IFP.elementMath.length-1] = null;
			for(int j = 0; j < elementMath.length - 1; j++){
				if(!IFP.isOperator(elementMath[j].charAt(0)) && Character.isAlphabetic(elementMath[j].charAt(0))){
					
					Variable tmp = Variable.getVariable(elementMath[j], list);
					if(tmp == null) throw new Exception("Bien khong xac dinh");
					else elementMath[j] = elementMath[j] + "_0";
				}
				
			}
			IFP.putParenthesis2();
			String result = elementMath[0];
			result = result.replaceAll("@", ">=");
			result = result.replaceAll("~", "<=");
			result = result.replaceAll("%", "mod");
			result = result.replaceAll("&", "and");
			output = output  + result;
		
		return output;
	}
	
	public String[] reverse(String[] elementMath){
		String[] result = elementMath.clone();
		int k = 0;
		for(int i = elementMath.length - 1; i >= 0; i--){
			result[k++] = elementMath[i];
		}
		return result;
	}
	
	
	
	
	
	public void putParenthesis2(){
		InfixToPrefix IFP = new InfixToPrefix();
		for(int i = elementMath.length - 2; i >= 0; i--){
			if(IFP.isOperator(elementMath[i].charAt(0))){
				String temp = "(" + elementMath[i] + " " + elementMath[i+1] + " " + elementMath[i+2] + ")";
				elementMath[i] = temp;
				for (int j = i+1; j < elementMath.length-3; j++) {
					elementMath[j] = elementMath[j+2];
				}
			}
		}
	}
	
	
	public int priority(char c){	// thiet lap thu tu uu tien
		
		int x = 9;
		int y = 10;
		boolean b = x > 100 && y > x;
		
		if (c == '+' || c == '-') return 1;
		else if ( c == '*' || c == '/' || c == '%') return 2;
		else if ( c == '>' || c == '<') return -1;
		else if ( c == '&') return -2;
		else return 0;
		}
	
	public boolean isOperator(char c){ // kiem tra xem co phai toan tu
		char operator[] = {'>', '<', '=', '+', '-', '*', '/', ')', '(' , '@', '?', '&'};
		Arrays.sort(operator);
		if (Arrays.binarySearch(operator, c) > -1)
			return true;
		else return false;
		}
	
	public String[] processString(String sMath){ // xu ly bieu thuc nhap vao thanh cac phan tu
		String s1 = "", elementMath[] = null;
		InfixToPrefix IFP = new InfixToPrefix();
		sMath = sMath.trim();
		sMath = sMath.replaceAll(" ",""); //	chuan hoa sMath
		for (int i=0; i<sMath.length(); i++){
			char c = sMath.charAt(i);//sMath.substring(i,1);
			if (!IFP.isOperator(c))
				s1 = s1 + c;
			else s1 = s1 + " " + c + " ";
		}
		s1 = s1.trim();
		s1 = s1.replaceAll("  "," "); //	chuan hoa s1
		
		elementMath = s1.split(" "); //tach s1 thanh cac phan tu
		//System.out.println(s1);
		return elementMath;
	}
	
	public String[] postfix(String[] elementMath){
		InfixToPrefix IFP = new InfixToPrefix();
		String s1 = "", E[];
		Stack <String> S = new Stack <String>();
		//System.out.println(elementMath.length);
		for (int i=0; i<elementMath.length; i++){ // duyet cac phan tu
			char c = elementMath[i].charAt(0);	// c la ky tu dau tien cua moi phan tu
			if (!IFP.isOperator(c)) // neu c khong la toan tu
				s1 = s1 + " " + elementMath[i];	// xuat elem vao s1
			else{	// c la toan tu
				if (c == ')') S.push(elementMath[i]);	// c la "(" -> day phan tu vao Stack
				else{
					if (c == '('){	// c la ")"
						
						char c1;	//duyet lai cac phan tu trong Stack
						do{
							c1 = S.peek().charAt(0);	// c1 la ky tu dau tien cua phan tu
							if (c1 != ')') s1 = s1 + " " + S.peek(); // trong khi c1 != "("
							
							S.pop();
						}while (c1 != ')');
					}
					else{
						while (!S.isEmpty() && IFP.priority(S.peek().charAt(0)) >= IFP.priority(c)){
							// Stack khong rong va trong khi phan tu trong Stack co do uu tien >= phan tu hien tai
							s1 = s1 + " " + S.peek();	// xuat phan tu trong Stack ra s1
							S.pop();
						}
						S.push(elementMath[i]); // dua phan tu hien tai vao Stack
					}
				}
			}
		}
		while (!S.isEmpty()){	// Neu Stack con phan tu thi day het vao s1
			s1 = s1 + " " + S.peek();
			S.pop();
		}
		E = s1.split(" ");	//	tach s1 thanh cac phan tu
		return E;
	}
}
	
