package util;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;

public class Helper {
	public static List<Variable> copyList(List<Variable> list) {
		List<Variable> copiedList = new ArrayList<>();
		
		for(Variable v: list) {
			copiedList.add(v.clone());
		}
		
		return copiedList;
	}
	
	public static String getBinaryOperator(BinaryOperatorKind operator) {
		String opStr = "";
		if(operator == BinaryOperatorKind.PLUS)
			opStr = "+";
		else if(operator == BinaryOperatorKind.MINUS)
			opStr = "-";
		else if(operator == BinaryOperatorKind.DIV)
			opStr = "/";
		else if(operator == BinaryOperatorKind.MUL)
			opStr = "*";
		else if(operator == BinaryOperatorKind.LT)
			opStr = "<";
		else if(operator == BinaryOperatorKind.LE)
			opStr = "<=";
		else if(operator == BinaryOperatorKind.GT)
			opStr = ">";
		else if(operator == BinaryOperatorKind.GE)
			opStr = ">=";
		else if(operator == BinaryOperatorKind.EQ)
			opStr = "=";
		else if(operator == BinaryOperatorKind.AND)
			opStr = "and";
		else if(operator == BinaryOperatorKind.OR)
			opStr = "or";
		else if(operator == BinaryOperatorKind.NE)
			opStr = "distinct";
		else 
			System.out.println("operator: " + operator);
		
		return opStr;
	}
	
	public static String getUnaryOperator(UnaryOperatorKind operator) {
		String opStr = "n/a";
		if(operator == UnaryOperatorKind.POSTDEC)
			opStr = "--";
		else if(operator == UnaryOperatorKind.PREDEC)
			opStr = "--";
		else if(operator == UnaryOperatorKind.POSTINC)
			opStr = "++";
		else if(operator == UnaryOperatorKind.PREINC)
			opStr = "++";
		else if(operator == UnaryOperatorKind.NOT)
			opStr = "not";
		else if(operator == UnaryOperatorKind.NEG)
			opStr = "-";
		else if(operator == UnaryOperatorKind.POS)
			opStr = "+";
		else
			System.out.println("unary operator: " + operator);
		
		return opStr;
	}

	public static void syncIndex(List<Variable> dest, List<Variable> source) {
			
		Variable temp;
		for(Variable v: source) {
			
			temp = Variable.getVariable(v.getName(), source);

			if( v.getIndex() > temp.getIndex()) {
				temp.initialize();
				temp.setIndex(v.getIndex());
			}
		}
	}

}
