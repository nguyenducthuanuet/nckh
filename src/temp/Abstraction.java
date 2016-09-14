package temp;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtIfImpl;
import util.Helper;
import util.StringBox;
import util.Variable;


public class Abstraction {
	
	public Abstraction(CtMethod method) {
		this.method = method;
	}
	
	public List<String> methodAbstraction() {
		List<CtParameter> parameters = method.getParameters();
//		int nParameters = parameters.size();
		List<Variable> listVariables = new ArrayList<Variable>();
		
//		listVariables.add(loop);
		
		Variable varTemp;
		for(CtParameter p: parameters) {
			varTemp = new Variable(p.getSimpleName(), p.getType().toString());
			varTemp.initialize();
			listVariables.add(varTemp);
		}

		String returnType = method.getType().toString();
		if( !returnType.equals("void")) {
			retVar = new Variable("return", returnType);
			retVar.initialize();
//			listVariables.add(retVar);
		}
/*		
		listVariables.add(breakVar);
		listVariables.add(loopTemp);
		listVariables.add(ifTemp);
		listVariables.add(loop);
*/		
		List<String> f = formularize(method.getBody(), listVariables);
		
//		for(Variable v: listVariables) {
//			System.out.println(v);
//		}
		
		return f;
	}
	
	public List<String> formularize(CtBlock block, List<Variable> listVariables) {
		
		if(block == null)
			return new ArrayList<>();
		
		return formularize(block.getStatements(), listVariables);
	}
	
	public List<String> formularize(List<CtStatement> list, List<Variable> listVariables) {
		
		List<String> f = new ArrayList<>();
		if(list == null)
			return f;
		
		List<String> temp = new ArrayList<>();
		for(CtStatement s: list) {
			temp = formularize(s, listVariables);
			f.addAll(temp);
		}
	
		return f;
	}
	
	public List<String> formularize(CtStatement statement, List<Variable> listVariables) {
		List<String> f = new ArrayList<>();
		String s = null;
		if(statement instanceof CtAssignment) {
			f = formularize((CtAssignment) statement, listVariables);
		}
		else if(statement instanceof CtUnaryOperator) {
			s = formularize((CtUnaryOperator) statement, listVariables);
		}
		else if(statement instanceof CtIf) {
			f = formularize((CtIf) statement, listVariables); 
		} 
		else if(statement instanceof CtSwitch) {
			f = formularize((CtSwitch) statement, listVariables);
		}
		else if(statement instanceof CtBlock) {
			f = formularize((CtBlock) statement, listVariables);
		} 
		else if(statement instanceof CtLocalVariable) {
			f = formularize( (CtLocalVariable) statement, 
												listVariables);
		}
		else if(statement instanceof CtFor) {
			f = formularize((CtFor) statement, listVariables);
		}
		else if(statement instanceof CtReturn) {
			s = formularize((CtReturn) statement, listVariables);
		}	
		
		if(s != null)
			f.add(s);
		
		return f;
	}
	
	
	public String formularize(CtUnaryOperator unaryOp, List<Variable> listVariables) {
		
		CtExpression operand = unaryOp.getOperand();
		if(operand instanceof CtLiteral) {
			return unaryOp.toString();
		}
			
		Variable variable = Variable.getVariable(operand.toString(), listVariables);
		UnaryOperatorKind operator = unaryOp.getKind();
		String opStr = Helper.getUnaryOperator(operator);
		String exp = null;
		String f;
		if(opStr.equals("+") || opStr.equals("-")) {
			exp = wrap(variable.getValue(), opStr, "1");
			variable.increase();
			f = wrap(variable.getValue(), "=", exp);
		}
		else {
			exp = formularize(operand, listVariables);
			f = wrap(opStr, exp);
		}
			
		return f;
	}

	public List<String> formularize(CtIf ifs, List<Variable> listVariables) {
		List<String> f = new ArrayList<>();
		
		CtExpression<Boolean> conditionExp = ifs.getCondition();
		String condition = formularize(conditionExp, listVariables);
		if( !ifTemp.hasInitialized() )
			ifTemp.initialize();
		f.add( wrap(ifTemp.getValue(), "=", condition)); 
		String ifTempStr = ifTemp.getValue();
		ifTemp.increase();
		
		CtStatement thenCtStatement = ifs.getThenStatement();
		List<Variable> thenList = Helper.copyList(listVariables);
		String fThen = wrapAll( formularize(thenCtStatement, thenList), "and") ;
		
		CtStatement elseCtStatement = ifs.getElseStatement();
		if(elseCtStatement == null) {
			if(fThen != null)
				f.add(wrap(ifTempStr, "=>", fThen));
			return f;
		}
			
		
		List<Variable> elseList = Helper.copyList(listVariables);
		String fElse = wrapAll( formularize(elseCtStatement, elseList), "and" );
		if(fElse == null)
			return f;
		
		Variable vThen, vElse;
		
		
		int indexOfVElse;
		int indexOfVThen;
		for(Variable v: listVariables) {
			vThen = Variable.getVariable(v.getName(), thenList);
			vElse = Variable.getVariable(v.getName(), elseList);
			if( vThen.getIndex() > vElse.getIndex()) 
				v.setIndex(vThen.getIndex());
			else
				v.setIndex(vElse.getIndex());
		}
		
		String thenUpdate = updateVariable(listVariables, thenList);
		if(thenUpdate != null)
			fThen = wrap(fThen, "and", thenUpdate);
		String elseUpdate = updateVariable(listVariables, elseList);
		if(elseUpdate != null)
			fElse = wrap(fElse, "and", elseUpdate);
		
		Variable.addVariable(listVariables, thenList);
		Variable.addVariable(listVariables, elseList);
		
		f.add( wrap(ifTempStr, "=>", fThen));
		f.add( wrap(wrap("not", ifTempStr), "=>", fElse) );
		
		return f;
	}
	
	
	public List<String> formularize(CtSwitch sw, List<Variable> listVariables) {
		List<String> f = new ArrayList<>();
		
		StringBox lastBreak = new StringBox();
		List<String> caseExpList = new ArrayList<>();
		
		List<CtCase> caseList = sw.getCases();
		String selector = formularize(sw.getSelector(), listVariables);
		
		List<String> caseState;
		String breakDefault = null, lastBreakBeforeDefault = null;
		CtCase defaultCase = null;
		int indexOfDefault = -1;
		for(CtCase c: caseList) {
			if(c.getCaseExpression() == null) {		// default clause
				lastBreakBeforeDefault = lastBreak.getString();
				if( !breakVar.hasInitialized())
					breakVar.initialize();
				else
					breakVar.increase();
				breakDefault = breakVar.getValue();
				indexOfDefault = f.size();
				defaultCase = c;
				f.add(null);
				f.add(null);
			} 
			else {
				caseState = formularize(c, listVariables, selector, lastBreak, caseExpList);
				f.addAll(caseState);
			}
		}
		
		if(defaultCase != null) {	// Switch has default clause
			caseState = formularize(defaultCase, lastBreakBeforeDefault, breakDefault, caseExpList, listVariables);
			f.set(indexOfDefault, caseState.get(0));
			f.set(indexOfDefault+1, caseState.get(1));
		}
		
		return f;
	}
	
	public List<String> formularize(CtCase caseSwitch, List<Variable> listVariables, String selector,
								StringBox lastBreak, List<String> caseExpList) {
		List<String> f = new ArrayList<>();
		CtBlock blockTemp = new CtBlockImpl();
		
		String caseExpStr = formularize(caseSwitch.getCaseExpression(), listVariables);
		String selectionExp = wrap(selector, "=", caseExpStr);
		caseExpList.add(selectionExp);
		String condition;
		if(lastBreak.getString() == null) 
			condition = selectionExp;
		else
			condition = wrap(wrap("not", lastBreak.getString()), "or", selectionExp);
		
		List<CtStatement> statements = caseSwitch.getStatements();
		blockTemp.setStatements(statements);
		List<Variable> syn = Helper.copyList(listVariables);
		String caseStateStr = wrapAll( formularize(blockTemp, listVariables), "and");
		breakVar.increase();
		String breakStr = breakVar.getValue();
		lastBreak.setString(breakStr);
		if( hasBreak(blockTemp) ) {
			caseStateStr = wrap(caseStateStr, "and", wrap(breakStr, "=", "true"));
		} else {
			caseStateStr = wrap(caseStateStr, "and", wrap(breakStr, "=", "false"));
		}
		f.add( wrap(condition, "=>", caseStateStr) );
		
		String updateVariable = updateVariable(syn, listVariables);
		if(updateVariable == null)
			f.add( wrap(wrap("not", condition), "=>", wrap(breakStr, "=", "true")) );
		else
			f.add( wrap(wrap("not", condition), "=>", wrap(wrap(breakStr, "=", "true"), "and", updateVariable)));
		
		return f;
	}
	
	
	public List<String> formularize(CtCase defaultCase, String lastBreakBeforeDefault, 
				String breakDefault, List<String> caseExpList, List<Variable> listVariables)
	{
		List<String> f = new ArrayList<>();
		
		String condition = wrap("not", wrapAll(caseExpList, "or"));
		condition = wrap(lastBreakBeforeDefault, "or", condition);
		
		CtBlock blockTemp = new CtBlockImpl();
		List<CtStatement> statements = defaultCase.getStatements();
		blockTemp.setStatements(statements);
		
		List<Variable> syn = Helper.copyList(listVariables);
		String caseStateStr = wrapAll( formularize(blockTemp, listVariables), "and");

		if( hasBreak(blockTemp) ) {
			caseStateStr = wrap(caseStateStr, "and", wrap(breakDefault, "=", "true"));
		} else {
			caseStateStr = wrap(caseStateStr, "and", wrap(breakDefault, "=", "false"));
		}
		f.add( wrap(condition, "=>", caseStateStr) );
		
		String updateVariable = updateVariable(syn, listVariables);
		if(updateVariable == null)
			f.add( wrap(wrap("not", condition), "=>", wrap(breakDefault, "=", "true")) );
		else
			f.add( wrap(wrap("not", condition), "=>", wrap(wrap(breakDefault, "=", "true"), "and", updateVariable)));

		
		return f;
	}
	

	// cho lap so lan mac dinh 
	public List<String> formularize(CtFor loop, List<Variable> listVariables) {
		List<String> f = new ArrayList<>();
		List<Variable> syn = Helper.copyList(listVariables);
		
		
		List<CtStatement> forInit = loop.getForInit();
		List<String> init = formularize(forInit, listVariables);
		f.addAll(init);
		
		List<CtStatement> forUpdate = loop.getForUpdate();
		CtStatement bodyLoop = loop.getBody();
		List<CtStatement> body = new ArrayList<>();
		body.add(bodyLoop);
		body.addAll(forUpdate);
		
		String loopBody = null;
		String aLoop = null;
		int nLoop = defaultNumOfLoop;
		if( !forLoop.hasInitialized())
			forLoop.initialize();
		
		String condition = null;
		String updateVarsExp = null;
		
		List<String> temp;
		
		List<String> updatedVarsList = new ArrayList<>();
		String conditionAssign = null;
		String forLoopValue = null;
		
		
		for(int i = 0; i < nLoop; i++) {
			
			condition = formularize(loop.getExpression(), listVariables);
			if(condition != null) {
				if(forLoopValue != null)
					condition = wrap(forLoopValue, "and", condition);
				conditionAssign = wrap(forLoop.getValue(), "=", condition);
				f.add(conditionAssign);
				forLoopValue = forLoop.getValue();
			}
			temp = formularize(body, listVariables);
			loopBody = wrapAll(temp, "and");
			if(condition == null) {
				f.add(loopBody);
			}
			else {
				aLoop = wrap(forLoopValue, "=>", loopBody);
				f.add(aLoop);
				updateVarsExp = updateVariable(syn, listVariables, updatedVarsList);
				if(updateVarsExp != null)
					f.add(wrap(wrap("not", forLoopValue), "=>", updateVarsExp));
			}
			syn = Helper.copyList(listVariables);
		}
		
		return f;
	}
	
	public List<String> formularize(CtLocalVariable var, 
											List<Variable> listVariables) 
	{
		
		List<String> f = new ArrayList<>();

		String variableName;
		variableName = var.getSimpleName();
		Variable v = Variable.getVariable(variableName, listVariables);
		
		// nếu biến v chưa có trong list thì thêm vào, ngược lại thì tăng index cũ
		if(v == null) {
			v = new Variable(variableName, var.getType().toString());
			listVariables.add(v);
		}
		else {
			v.increase();
		}
		
		String initializer;
		CtExpression initializerExp = var.getAssignment();
		if(initializerExp != null) {
			v.initialize();
			initializer = formularize(initializerExp, listVariables);
			f.add( wrap(v.getValue(), "=", initializer) ); 
		}
		
		return f;
	}
	
	public String formularize(CtReturn retExp, List<Variable> listVariables) {
		
		if(retExp.getReturnedExpression() == null)
			return null;
					
		String ret = formularize(retExp.getReturnedExpression(), listVariables);
		return wrap(retVar.getValue(), "=", ret); 
	}
	
	public String formularize(CtExpression exp, List<Variable> listVariables) {
		String f = null;
		
		if(exp instanceof CtBinaryOperator) {			
			f = formularize((CtBinaryOperator) exp, listVariables);
		}
		else if(exp instanceof CtUnaryOperator) {
			f = formularize((CtUnaryOperator) exp, listVariables);
		}
		else if(exp instanceof CtVariableAccess) {
			f = formularize((CtVariableAccess) exp, listVariables);
		}
		else if(exp instanceof CtLiteral) {
			f = exp.toString();
		}
		
		return f;
	}
	
	public List<String> formularize(CtAssignment ass, List<Variable> listVariables) {
	
		CtExpression left = ass.getAssigned();
		CtExpression right = ass.getAssignment();
		
		Variable v = Variable.getVariable(left.toString(), listVariables);

		
		
		if(v.hasInitialized())
			v.increase();
		else
			v.initialize();
		
		String leftHandSide = v.getValue();
		String rightHandSide = formularize(right, listVariables);
		
		String s = wrap(leftHandSide, "=", rightHandSide);
		List<String> f = new ArrayList<>();
		f.add(s);
		return f;
	}
	
	public String formularize(CtBinaryOperator binOp, List<Variable> listVariables) {

		CtExpression left = binOp.getLeftHandOperand();
		CtExpression right = binOp.getRightHandOperand();
		
		String fLeft = formularize(left, listVariables);
		String fRight = formularize(right, listVariables);
		String operator = Helper.getBinaryOperator(binOp.getKind());
		
		return wrap(fLeft, operator, fRight);
	}
	
	public String formularize(CtVariableAccess var, List<Variable> listVariables) {
		Variable v = Variable.getVariable(var.toString(), listVariables);
		
		return v.getValue();
	}
	
	public String formularize(CtLiteral literal, List<Variable> listVariables) {
		return literal.toString();
	}
	
	private boolean hasBreak(CtStatement s) {
		if(s instanceof CtBlock) {
			CtBlock b = (CtBlock) s;
			List<CtStatement> list = b.getStatements();
			for(CtStatement ss: list) {
				if( hasBreak(ss) )
					return true;
			}
		}
		else if(s instanceof CtBreak) {
			return true;
		}
		
		
		return false;
	}
	
	private String updateVariable(List<Variable> l1, List<Variable> l2, List<String> updatedVarsList) {
		if(updatedVarsList == null) 
			return updateVariable(l1, l2);
		
		Variable temp;
		String updateExp = null;
		String updateAVar = null;
		String exp;
		
		if (updatedVarsList.size() != 0) {

			for(String v: updatedVarsList) {
				updateAVar = updateAVar(l1, l2, v);
				if(updateAVar != null) {
					if(updateExp == null) 
						updateExp = updateAVar;
					else
						updateExp = wrap(updateAVar, "and", updateExp);
				}
			}
		}
		else {
			for(Variable v: l1) {
				temp = Variable.getVariable(v.getName(), l2);
				if(temp == null)
					continue;
				if(temp.getIndex() > v.getIndex() ) {
					updatedVarsList.add(v.getName());
					exp = wrap(temp.getValue(), "=", v.getValue());
					if(updateExp == null) 
						updateExp = exp;
					else
						updateExp = wrap(exp, "and", updateExp);
				}
			}
		}
		
		return updateExp;
	}
	
	private String updateAVar(List<Variable> l1, List<Variable> l2, String varName) {
		Variable v1 = Variable.getVariable(varName, l1);
		Variable v2 = Variable.getVariable(varName, l2);
		
		if(v1 == null || v2 == null)
			return null;
		
		return wrap(v2.getValue(), "=", v1.getValue());
	}
	
	private String updateVariable(List<Variable> l1, List<Variable> l2) {
		Variable temp;
		String updateExp = null;
		String exp;
		for(Variable v: l1) {
			temp = Variable.getVariable(v.getName(), l2);
			if(temp == null)
				continue;
			if(temp.getIndex() > v.getIndex() ) {
				exp = wrap(temp.getValue(), "=", v.getValue());
				if(updateExp == null) 
					updateExp = exp;
				else
					updateExp = wrap(exp, "and", updateExp);
			}
		}
		
		return updateExp;
	}
	
	

	
	private String wrapAll(List<String> list, String conjunction) {
		if(list == null || list.size() == 0)
			return null;
		
		int size = list.size();
		String str = list.get(size-1);
		if(size == 1)
			return str;
		
		for(int i = size - 2; i >= 0; i--) {
			str = wrap(list.get(i), conjunction, str);
		//	str = list.get(i) + conjuntion + str;
		}
		
		return str;
//		return wrap(str);
	}
	
	private void deleteVarible() {
		ifTemp.delete();
		forLoop.delete();
		retVar.delete();
	}
	
	private String wrap(String exp1, String op, String exp2) {
//		return "(" + exp1 + " " + op + " " + exp2 + ")";
		return "(" + op + " " + exp1 + " " + exp2 + ")";
	}
	
	private String wrap(String op, String exp) {
		return "(" + op + " " + exp + ")";
	}
	
	private String wrap(String exp) {
		return "(" + exp + ")";
	}
	
	
	private CtMethod method;
	
	private int defaultNumOfLoop = 4;
	
	private Variable forLoop = new Variable("for", "bool");
	
	private Variable ifTemp = new Variable("if", "bool");
	private Variable retVar;
	
	private Variable breakVar = new Variable("break", "bool");
}
