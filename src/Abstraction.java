/*

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtMethod;


public class Abstraction {
	
	public Abstraction(CtMethod method) {
		this.method = method;
	}
	
	public String methodAbstraction() {
		List<SingleVariableDeclaration> parameters = method.parameters();
//		int nParameters = parameters.size();
		List<Variable> listVariables = new ArrayList<Variable>();
		Variable varTemp;
		for(SingleVariableDeclaration svd: parameters) {
			varTemp = new Variable(svd.getName().toString());
			varTemp.initialize();
			listVariables.add(varTemp);
		}
		
		String f;
		
		f =  blockAbstraction(method.getBody(), listVariables);
		
		return f;
	}
	
	public String blockAbstraction(Block block, List<Variable> listVariables) {
		if(block == null)
			return null;
		
		List<Statement> statements = block.statements();
		if(statements.isEmpty()) {
			return null;
		}
		
		String f = "1";
		String fStatement;
		for(Statement s: statements) {
			fStatement = statementAbstraction(s, listVariables);
			if(fStatement != null)
				f += " ^ " + fStatement;
		}
		
		return f;
	}
	
	public String statementAbstraction(Statement statement, List<Variable> listVariables) {
		String f = null;
		if(statement instanceof CtExpressionStatement) {
			f = CtExpressionAbstraction(((CtExpressionStatement) statement).getCtExpression(), 
											listVariables);
		}
		else if(statement instanceof IfStatement) {
			f = ifAbstraction((IfStatement) statement, listVariables); 
		} 
		else if(statement instanceof Block) {
			f = blockAbstraction((Block) statement, listVariables);
		} 
		else if(statement instanceof VariableDeclarationStatement) {
			f = varDeclarationAbstraction( (VariableDeclarationStatement) statement, 
												listVariables);
		}
		
		return f;
	}
	
	public String ifAbstraction(IfStatement ifs, List<Variable> listVariables) {
		String f = "";
		
		CtExpression conditionExp = ifs.getCtExpression();
		String condition = CtExpressionAbstraction(conditionExp, listVariables);
		
		Statement thenStatement = ifs.getThenStatement();
		Statement elseStatement = ifs.getThenStatement();
		
		String fThen = statementAbstraction(thenStatement, listVariables);
		String fElse = statementAbstraction(elseStatement, listVariables);
		f = "(" + condition + "->" + fThen + ") ^ ( ~" + condition + "->" + fElse + ")";
		
		return f;
	}
	
	public String varDeclarationAbstraction(VariableDeclarationStatement var, 
											List<Variable> listVariables) 
	{
		
		System.out.println("VAR: " + var);
		String f = "";
//		Type type = var.getType();
		List<VariableDeclarationFragment> listVar = var.fragments();
		String variableName;
		String fInitialize = null;
		for(VariableDeclarationFragment vdf: listVar) {
			variableName = vdf.getName().toString();
			Variable v = Variable.getVariable(variableName, listVariables);
			vdf.getInitializer();
			if(v == null) {
				v = new Variable(variableName);
				listVariables.add(v);
			}
			else {
				v.increase();
			}
			
			CtExpression initializer = vdf.getInitializer();
			if(initializer != null) {
				fInitialize = v.getValue() + "=" + CtExpressionAbstraction(initializer, listVariables);
				f += " ^ " + fInitialize; 
			}
		}
		
		if(f.equals(""))
			return null;
		else
			return f.substring(3);	// bo " ^ " o dau
	}
	
	public String CtExpressionAbstraction(CtExpression exp, List<Variable> listVariables) {
		String f = null;
		
		if(exp instanceof Assignment) {
			f = assignmentAbstraction((Assignment) exp, listVariables);
		}
		else if(exp instanceof CtBinaryOperator) {			
			f = infixExpAbstraction((CtBinaryOperator) exp, listVariables);
		}
		else if(exp instanceof SimpleName) {	// cho nay co thieu sot
			f = simpleNameAbstraction((SimpleName) exp, listVariables);
		}
		else if(isLiteral(exp)) {
			f = exp.toString();
		}
		
		CtBinaryOperator<T>
			
		return f;
	}
	
	public String assignmentAbstraction(CtAssignment ass, List<Variable> listVariables) {
		String f = "";
		CtExpression left = 
		CtExpression right = ass.getRightHandSide();
		
		Variable v = Variable.getVariable(left.toString(), listVariables);

		String leftHandSide = "N/A";
		if(v != null) {
			if(v.hasInitialized())
				v.increase();
			else
				v.initialize();
			leftHandSide = v.getValue();
		}
		
		f = leftHandSide + ass.getOperator();
		f += CtExpressionAbstraction(right, listVariables);
		
		return f;
	}
	
	public String infixExpAbstraction(CtBinaryOperator binOp, List<Variable> listVariables) {
		CtExpression left = binOp.getLeftHandOperand();
		CtExpression right = binOp.getRightHandOperand();
		
		String fLeft = CtExpressionAbstraction(left, listVariables);
		String fRight = CtExpressionAbstraction(right, listVariables);
		
		return fLeft + binOp.getKind() + fRight;
	}
	
	public String simpleNameAbstraction(SimpleName simplename, List<Variable> listVariables) {
		Variable v = Variable.getVariable(simplename.toString(), listVariables);
		
		return v.getValue();
	}
	
	private CtMethod method;
}

*/