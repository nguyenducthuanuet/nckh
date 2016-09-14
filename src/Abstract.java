
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class Abstract {
	
	public Abstract(MethodDeclaration method) {
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
		if(statement instanceof ExpressionStatement) {
			f = expressionAbstraction(((ExpressionStatement) statement).getExpression(), 
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
		
		Expression conditionExp = ifs.getExpression();
		String condition = expressionAbstraction(conditionExp, listVariables);
		
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
			
			Expression initializer = vdf.getInitializer();
			if(initializer != null) {
				fInitialize = v.getValue() + "=" + expressionAbstraction(initializer, listVariables);
				f += " ^ " + fInitialize; 
			}
		}
		
		if(f.equals(""))
			return null;
		else
			return f.substring(3);	// bo " ^ " o dau
	}
	
	public String expressionAbstraction(Expression exp, List<Variable> listVariables) {
		String f = null;
		
		if(exp instanceof Assignment) {
			f = assignmentAbstraction((Assignment) exp, listVariables);
		}
		else if(exp instanceof InfixExpression) {			
			f = infixExpAbstraction((InfixExpression) exp, listVariables);
		}
		else if(exp instanceof SimpleName) {	// cho nay co thieu sot
			f = simpleNameAbstraction((SimpleName) exp, listVariables);
		}
		else if(isLiteral(exp)) {
			f = exp.toString();
		}
			
		return f;
	}
	
	public String assignmentAbstraction(Assignment ass, List<Variable> listVariables) {
		String f = "";
		Expression left = ass.getLeftHandSide();
		Expression right = ass.getRightHandSide();
		
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
		f += expressionAbstraction(right, listVariables);
		
		return f;
	}
	
	public String infixExpAbstraction(InfixExpression infix, List<Variable> listVariables) {
		Expression left = infix.getLeftOperand();
		Expression right = infix.getRightOperand();
		
		String fLeft = expressionAbstraction(left, listVariables);
		String fRight = expressionAbstraction(right, listVariables);
		
		return fLeft + infix.getOperator() + fRight;
	}
	
	public String simpleNameAbstraction(SimpleName simplename, List<Variable> listVariables) {
		Variable v = Variable.getVariable(simplename.toString(), listVariables);
		
		return v.getValue();
	}
	
	private boolean isLiteral(Expression exp) {
		return 	exp instanceof StringLiteral || exp instanceof NumberLiteral ||
				exp instanceof CharacterLiteral || exp instanceof BooleanLiteral ||
				exp instanceof NullLiteral;
	}
	
	
	private MethodDeclaration method;
}
