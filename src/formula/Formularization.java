package formula;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import util.Helper;
import util.StringBox;
import util.Variable;

public abstract class Formularization {
	
	public abstract Formula getFormula();
	
	public Formula formularize(CtStatement statement,
									StringBox lastReturnFlag,
									StringBox lastBreakFlag,
									StringBox lastContinueFlag) {

		Formula f = new Formula();
		if (statement == null)
			return f;
		
		String preCondition = getPreCondition(lastReturnFlag, 
							lastBreakFlag, lastContinueFlag);
		
		String s = null;
		if (statement instanceof CtAssignment) {
			f = formularize((CtAssignment) statement);
		}
		else if (statement instanceof CtUnaryOperator) {
			s = formularize((CtUnaryOperator) statement);
		}
		else if (statement instanceof CtIf) {
			IfFormularization ifF = new IfFormularization((CtIf) statement, listVariables, 
															flagVariables, lastReturnFlag,
															lastBreakFlag, lastContinueFlag);
			f = ifF.getFormula();
		} 
		else if (statement instanceof CtSwitch) {
			SwitchFormularization sf = 
						new SwitchFormularization((CtSwitch) statement, 
													listVariables, flagVariables, 
													lastReturnFlag,
													lastContinueFlag);
			f = sf.getFormula();
		}
		else if (statement instanceof CtBlock) {
			f = formularize((CtBlock) statement, lastReturnFlag,
							lastBreakFlag, lastContinueFlag);
		} 
		else if (statement instanceof CtLocalVariable) {
			f = formularize( (CtLocalVariable) statement);
		}
		else if (statement instanceof CtFor) {
			
			ForFormularization forF = new ForFormularization((CtFor) statement, 
								listVariables, flagVariables, lastReturnFlag);
			f = forF.getFormula();
		}
		else if (statement instanceof CtReturn) {
			f = formularize((CtReturn) statement, lastReturnFlag);
		}
		else if (statement instanceof CtBreak) {
			s = breakFormularization(lastBreakFlag);
		}
		else if (statement instanceof CtContinue) {
			s = continueFormularization(lastContinueFlag);
		}
		
		
		if (s != null) {
			f.add(s);
		}
		
		if (preCondition != null) {
			Formula temp = new Formula();
			temp.add(f);
			temp.setCondition(preCondition);
			f = temp;
		}
		
		return f;
	}
	
	public Formula formularize(List<CtStatement> list,
									StringBox lastReturnFlag,
									StringBox lastBreakFlag,
									StringBox lastContinueFlag) {
		
		Formula f = new Formula();
		
		if (list == null)
			return f;
	
		Formula temp = new Formula();
		StringBox lastReturnFlagTemp = new StringBox();
		StringBox lastBreakFlagTemp = new StringBox();
		StringBox lastContinueFlagTemp = new StringBox();
		
		
		for(CtStatement s: list) {
			temp = formularize(s, lastReturnFlagTemp, 
								lastBreakFlagTemp, lastContinueFlagTemp);
			f.add(temp);
		}
		
		if(lastReturnFlag != null && lastReturnFlagTemp.getString() != null)
			lastReturnFlag.setString(lastReturnFlagTemp.getString());
		
		if(lastBreakFlag != null && lastBreakFlagTemp.getString() != null)
			lastBreakFlag.setString(lastBreakFlagTemp.getString());
		
		if(lastContinueFlag != null && lastContinueFlagTemp.getString() != null)
			lastContinueFlag.setString(lastContinueFlagTemp.getString());
		
		return f;
	}
	
	public Formula formularize(CtBlock block, 
									StringBox lastReturnFlag,
									StringBox lastBreakFlag,
									StringBox lastContinueFlag) {
		
		if (block == null)
			return new Formula();
		
		return formularize(block.getStatements(), lastReturnFlag, 
							lastBreakFlag, lastBreakFlag);
	}
	
	public String formularize(CtUnaryOperator unaryOp) {
		
		CtExpression operand = unaryOp.getOperand();
		if (operand instanceof CtLiteral) {
			return unaryOp.toString();
		}
			
		Variable variable = Variable.getVariable(operand.toString(), listVariables);
		UnaryOperatorKind operator = unaryOp.getKind();
		String opStr = Helper.getUnaryOperator(operator);
		String exp = null;
		String f;
		if (opStr.equals("++") || opStr.equals("--")) {
			opStr = opStr.substring(1);
			exp = wrap(variable.getValue(), opStr, "1");
			variable.increase();
			f = wrap(variable.getValue(), "=", exp);
		}
		else {
			exp = formularize(operand);
			f = wrap(opStr, exp);
		}
			
		return f;
	}
	
	
	public Formula formularize(CtLocalVariable var) 
	{	
		Formula f = new Formula();

		String variableName;
		variableName = var.getSimpleName();
		Variable v = Variable.getVariable(variableName, listVariables);
		
		// nếu biến v chưa có trong list thì thêm vào, ngược lại thì tăng index cũ
		if (v == null) {
			v = new Variable(variableName, var.getType().toString());
			listVariables.add(v);
		}
/*		else {
			v.increase();
		}
*/
		
		String initializer;
		CtExpression initializerExp = var.getAssignment();
		if (initializerExp != null) {
			v.initialize();
			initializer = formularize(initializerExp);
			f.add( wrap(v.getValue(), "=", initializer) ); 
		}
		
		return f;
	}
	
	public Formula formularize(CtReturn retExp, StringBox returnFlagVal) {
		if (retExp.getReturnedExpression() == null)
			return null;
		
		Formula f = new Formula();
		
		if (returnFlag.hasInitialized())
			returnFlag.increase();
		else
			returnFlag.initialize();
		String assign = wrap(returnFlag.getValue(), "=", "true");
		f.add(assign);
		
		if (returnFlagVal != null) {
			returnFlagVal.setString(returnFlag.getValue());
		}
		
		if (retExp.getReturnedExpression() == null)
			return f;
					
		String ret = formularize(retExp.getReturnedExpression());
		
		assign = wrap("return", "=", ret); 
		
		f.add(assign);
		return f;
	}
	
	
	public String breakFormularization(StringBox breakFlagVal) {
		
		if (breakFlag.hasInitialized())
			breakFlag.increase();
		else
			breakFlag.initialize();
		String assign = wrap(breakFlag.getValue(), "=", "true");
		
		if (breakFlagVal != null) {
			breakFlagVal.setString(breakFlag.getValue());
		}

		return assign;
	}
	
	public String continueFormularization(StringBox continueFlagVal) {
		
		if (continueFlag.hasInitialized())
			continueFlag.increase();
		else
			continueFlag.initialize();
		String assign = wrap(continueFlag.getValue(), "=", "true");
		
		if (continueFlagVal != null) {
			continueFlagVal.setString(continueFlag.getValue());
		}
		
		return assign;
	}
	
	
	public String formularize(CtExpression exp) {
		String f = null;
		
		if (exp instanceof CtBinaryOperator) {			
			f = formularize((CtBinaryOperator) exp);
		}
		else if (exp instanceof CtUnaryOperator) {
			f = formularize((CtUnaryOperator) exp);
		}
		else if (exp instanceof CtVariableAccess) {
			f = formularize((CtVariableAccess) exp);
		}
		else if (exp instanceof CtLiteral) {
			f = formularize((CtLiteral) exp);
		}
		
		return f;
	}
	
	public Formula formularize(CtAssignment ass) {
	
		CtExpression left = ass.getAssigned();
		CtExpression right = ass.getAssignment();
		
		Variable v = Variable.getVariable(left.toString(), listVariables);
		
		String rightHandSide = formularize(right);
		
		if (v.hasInitialized())
			v.increase();
		else
			v.initialize();
		String leftHandSide = v.getValue();
		
		String s = wrap(leftHandSide, "=", rightHandSide);
		Formula f = new Formula();
		f.add(s);
		return f;
	}
	
	public Formula formularize(CtOperatorAssignment ass) {
		
		CtExpression left = ass.getAssigned();
		CtExpression right = ass.getAssignment();
		BinaryOperatorKind op = ass.getKind();
		
		CtBinaryOperator rightOperation = new CtBinaryOperatorImpl();
		rightOperation.setLeftHandOperand(left);
		rightOperation.setRightHandOperand(right);
		rightOperation.setKind(op);
		
		Variable v = Variable.getVariable(left.toString(), listVariables);
		
		String rightHandSide = formularize(right);
		
		if (v.hasInitialized())
			v.increase();
		else
			v.initialize();
		String leftHandSide = v.getValue();
		
		String s = wrap(leftHandSide, "=", rightHandSide);
		Formula f = new Formula();
		f.add(s);
		return f;
	}
	
	public String formularize(CtBinaryOperator binOp) {

		CtExpression left = binOp.getLeftHandOperand();
		CtExpression right = binOp.getRightHandOperand();
		
		String fLeft = formularize(left);
		String fRight = formularize(right);
		String operator = Helper.getBinaryOperator(binOp.getKind());
		
		return wrap(fLeft, operator, fRight);
	}
	
	public String formularize(CtVariableAccess var) {
		Variable v = Variable.getVariable(var.toString(), listVariables);
		if (v == null)
			return "n/a";
		
		return v.getValue();
	}
	
	public String formularize(CtLiteral literal) {
		String l = literal.toString();
		char lastChar = l.charAt(l.length()-1);
		if ( lastChar == 'f' || lastChar == 'F' ) 	// literal is a float number
			l = l.substring(0, l.length()-1);
		
		return l;
	}
	
	protected String getPreCondition(StringBox lastReturnFlag, 
									 StringBox lastBreakFlag, 
									 StringBox lastContinueFlag) {
		
		String preCondition = null;
		
		if (lastReturnFlag != null && lastReturnFlag.getString() != null)
			preCondition = wrap("not", lastReturnFlag.getString());
		
		String temp;
		if (lastBreakFlag != null && lastBreakFlag.getString() != null) {
			temp = wrap("not", lastBreakFlag.getString());
			if (preCondition != null)
				preCondition = wrap(preCondition, "or", temp);
			else
				preCondition = temp;
		}	
		
		if (lastContinueFlag != null && lastContinueFlag.getString() != null) {
			temp = wrap("not", lastContinueFlag.getString());
			if (preCondition != null)
				preCondition = wrap(preCondition, "or", temp);
			else
				preCondition = temp;
		}
			
		return preCondition;
	}
	

	
	protected Formula syncVariable(List<Variable> dest, 
										List<Variable> source, 
										List<String> syncVarsList) {
		if (syncVarsList == null) 
			return syncVariable(dest, source);
		
		Formula sync = new Formula();
		Variable temp;
		String updateExp = null;
		String syncAVar = null;
		String exp;
		
		if (syncVarsList.size() != 0) {

			for(String v: syncVarsList) {
				syncAVar = syncAVar(dest, source, v);
				if (syncAVar != null) {
					sync.add(syncAVar);
				}
			}
		}
		else {
			for(Variable v: dest) {
				temp = Variable.getVariable(v.getName(), source);
				if (temp == null)
					continue;
				if (temp.getIndex() > v.getIndex() ) {
					syncVarsList.add(v.getName());
					exp = wrap(temp.getValue(), "=", v.getValue());
					sync.add(exp);
				}
			}
		}
		
		return sync;
	}
	
	protected String syncAVar(List<Variable> dest, List<Variable> source, String varName) {
		Variable v1 = Variable.getVariable(varName, dest);
		Variable v2 = Variable.getVariable(varName, source);
		
		if (v1 == null || v2 == null)
			return null;
		
		return wrap(v2.getValue(), "=", v1.getValue());
	}
	
	
	protected Formula syncVariable(List<Variable> dest, List<Variable> source) {
		Formula sync = new Formula();
		Variable temp;
		String exp;
		for(Variable v: dest) {
			temp = Variable.getVariable(v.getName(), source);
			if (temp == null)
				continue;
			if (temp.getIndex() > v.getIndex() ) {
				exp = wrap(temp.getValue(), "=", v.getValue());
				sync.add(exp);
			}
		}
		
		return sync;
	}
	
	
	protected void init() {
		forFlag = new Variable("for", "bool");
		ifFlag = new Variable("if", "bool");
		returnFlag = new Variable("return", "bool");
		breakFlag = new Variable("break", "bool");
		continueFlag = new Variable("continue", "bool");
		
		flagVariables = new ArrayList<>();
		flagVariables.add(breakFlag);
		flagVariables.add(returnFlag);
		flagVariables.add(forFlag);
		flagVariables.add(ifFlag);
		flagVariables.add(continueFlag);
		
		lastReturnFlag = new StringBox();
	}
	
	protected void assign(	List<Variable> listVariables, 
							List<Variable> flagVariables,
							StringBox lastReturnFlag) 
	{
		this.listVariables = listVariables;
		this.flagVariables = flagVariables;
		this.lastReturnFlag = lastReturnFlag;
		
		returnFlag = Variable.getVariable("return", flagVariables);
		
		forFlag = Variable.getVariable("for", flagVariables);
		ifFlag = Variable.getVariable("if", flagVariables);
		breakFlag = Variable.getVariable("break", flagVariables);
		continueFlag = Variable.getVariable("continue", flagVariables);

	}
	
	
	public String wrap(String exp1, String op, String exp2) {
		return "(" + exp1 + " " + op + " " + exp2 + ")";
//		return "(" + op + " " + exp1 + " " + exp2 + ")";
	}
	
	public String wrap(String op, String exp) {
		return "(" + op + " " + exp + ")";
	}
	
	public String wrap(String exp) {
		return "(" + exp + ")";
	}
	
	public String wrapAll(List<String> list, String conjunction) {
		if (list == null || list.size() == 0)
			return null;
		
		int size = list.size();
		String str = list.get(size-1);
		if (size == 1)
			return str;
		
		for(int i = size - 2; i >= 0; i--) {
//			str = wrap(list.get(i), conjunction, str);
			str = list.get(i) + " " + conjunction + " " + str;
		}
		
//		return str;
		return wrap(str);
	}
	
	void printListVar(List<Variable> list) {
		if (list == null) {
			System.exit(1);
		}
	
		for(Variable v: list) {
			System.out.println(v);
		}
	}
	
	protected List<Variable> listVariables;
	
	protected Variable forFlag;
	protected Variable ifFlag;
	protected Variable returnFlag;
	protected Variable breakFlag;
	protected Variable continueFlag;
	protected List<Variable> flagVariables;
	
	protected StringBox lastReturnFlag;
	
	Formula formula;
}
