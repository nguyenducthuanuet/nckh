
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;

public class Main {

    //use ASTParse to parse string
    public static void parse(String str) {
       
    	@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(str.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        
 //       ASTNode node = (ASTNode)parser.createAST(null);	// ko dc moi ao
        ASTNode root = (ASTNode) cu;	// OK
        AST ast = root.getAST();
        
        List<MethodDeclaration> methods = new ArrayList<>();
        
        
        ASTVisitor visitor= new ASTVisitor() {
        	
        	@Override
        	public boolean visit(SimpleName node) {
        		// TODO Auto-generated method stub
 //       		SimpleName sn = (SimpleName) node;
 //       		System.out.println(node + " has identifier " + sn.getIdentifier());
        		return super.visit(node);
        	}
        	
            @Override
            public void preVisit(ASTNode node) {
            	
            	if(node instanceof MethodDeclaration){
                	MethodDeclaration method = (MethodDeclaration) node;
                	methods.add(method);
//                	System.out.println("Ten phuong thuc: " +  method.getName().toString());
//                	foo(node);
                	
                }
            	
            	
            	if(node instanceof ExpressionStatement){
            		ExpressionStatement type = (ExpressionStatement) node;
            		System.out.println(type.getExpression());
            	}
            	
            	if(node instanceof IfStatement){
            		IfStatement ifstatement = (IfStatement) node;
            		System.out.println("Block IF_ELSE\nBieu thuc dieu kien: " + ifstatement.getExpression());
            		System.out.println("Bieu thuc if: " + ifstatement.getThenStatement());
            		System.out.println("Bieu thuc else: " + ifstatement.getElseStatement());
            		foo(node);
            	}

                super.preVisit(node);
            }
        };
        cu.accept(visitor);
        
        ASTVisitor infixVistor = new ASTVisitor() {
        	@Override
        	public void preVisit(ASTNode node) {
        		// TODO Auto-generated method stub
        		if(node instanceof InfixExpression) {
        			System.out.println("InfixExpression: " + node);
        			foo((InfixExpression)node);
        		}
        		
        		super.preVisit(node);
        	}
        	
        	public void foo(InfixExpression node) {
        		
        		List<Expression> list = node.extendedOperands();
        		if(list.isEmpty())
        			return;
        					
        		Operator op = node.getOperator();
        		
        		List<Expression> tempList;
        		InfixExpression tempInfix;
        		while(list.size() > 1) {
        			tempList = new ArrayList<Expression>();
        			for(int i = 1; i < list.size(); i += 2) {
        				tempInfix = ast.newInfixExpression();
        				
        				tempInfix.setLeftOperand(list.get(i-1));
        				tempInfix.setRightOperand(list.get(i));
        				tempInfix.setOperator(op);
        				tempList.add(tempInfix);
        			}
        			list = tempList;
        		}
        		
        		System.out.println("list.size() = " + list.size());
        		
        		
        		tempInfix = ast.newInfixExpression();
        		Expression tempExp = node.getLeftOperand();
//        		node.setLeftOperand(null);
//        		tempInfix.setLeftOperand(node.getLeftOperand());
        		tempInfix.setLeftOperand(tempExp);
 //       		tempInfix.setRightOperand(node.getRightOperand());
        		tempInfix.setOperator(op);
        		node.setLeftOperand(tempInfix);
//        		node.setRightOperand((Expression)list.get(0));
        	
        	}

		};
		
//		cu.accept(infixVistor);
        
		SwitchStatement switchStatement;
//		switchStatement.statements();
        
        MethodDeclaration firstMethod = methods.get(0);
        System.out.println("hello--------------");
        System.out.println(firstMethod);
//        visit(firstMethod.getBody());
        
        Abstract ab = new Abstract(firstMethod);
        System.out.println("f = " + ab.methodAbstraction());

    }
    
    public static void visit(ASTNode node) {
//    	System.out.println("node: " + node + ", Class: " + node.getClass());
    	if(node == null) 
    		return;
    	
    	if(node instanceof Block) {
    		Block block = (Block) node;
    		List<Statement> statements = block.statements();
    		for(Statement s: statements) {
    			visit(s);
    		}
    	} else if(node instanceof InfixExpression) {
    		InfixExpression ex = (InfixExpression) node;
			System.out.println("infix: " + ex);
			visit(((InfixExpression) ex).getLeftOperand());
			visit(((InfixExpression) ex).getRightOperand());
			System.out.println("end infix");
			List<?> list = ((InfixExpression) ex).extendedOperands();
			if(list == null) {
				System.out.println("list is null");
			}
			else {
				System.out.println("list.size = " + list.size());
			}
			System.out.println("list extended Operands: ");
			for(Object e: list) {
				System.out.println(e);
			}
		}
    	else if(node instanceof IfStatement) {
    		IfStatement ifstatement = (IfStatement) node;
    		System.out.println("Block IF_ELSE\nBieu thuc dieu kien: " + ifstatement.getExpression());
    		Statement thenStatement = ifstatement.getThenStatement();
    		System.out.println("Bieu thuc then: ");
    		visit(thenStatement);
    		Statement elseStatement = ifstatement.getElseStatement();
    		System.out.println("Bieu thuc else: ");
    		visit(elseStatement);
    	}
    	else if(node instanceof ForStatement) {
    		ForStatement forstatement = (ForStatement) node;
    		System.out.println("for statement:");
    		List<? extends ASTNode> init = forstatement.initializers(); 
    		System.out.println("init: ");
    		for(ASTNode s: init) {
    			visit(s);
    		}
    		System.out.println();
    		System.out.println();
    		System.out.println();
    	}
    	else if(node instanceof ExpressionStatement) {
 //   		System.out.println("node: " + node);
    		ExpressionStatement xs = (ExpressionStatement) node;
    		Expression ex = xs.getExpression();
    		if(ex instanceof Assignment) {
    			System.out.println("assignment: ");
    			System.out.print("left: ");
    			visit(((Assignment) ex).getLeftHandSide());
 //   			System.out.println("right: " + ((Assignment) ex).getRightHandSide().getClass());
    			System.out.print("right: ");
    			visit(((Assignment) ex).getRightHandSide());
    			System.out.println();
    		}
    		else if(ex instanceof InfixExpression) {
    			System.out.println("infix: " + ex);
    			visit(((InfixExpression) ex).getLeftOperand());
    			visit(((InfixExpression) ex).getRightOperand());
    			System.out.println("end infix");
    			List<?> list = ((InfixExpression) ex).extendedOperands();
    			if(list == null) {
    				System.out.println("list is null");
    			}
    			else {
    				System.out.println("list.size = " + list.size());
    			}
    			System.out.println("list extended Operands: ");
    			for(Object e: list) {
    				System.out.println(e);
    			}
    		}
    		else {
    			System.out.println("ex = " + ex);
    		}
    	}
    	else if(node instanceof SimpleName) {
    		System.out.println(node);
    	}
    	else if(node instanceof Expression) {
    		Expression ex = (Expression) node;
    		if(ex instanceof InfixExpression) {
    			System.out.println("infix: ");
    			visit(((InfixExpression) ex).getLeftOperand());
    			visit(((InfixExpression) ex).getRightOperand());
    		}
    		else {
    			System.out.println("ex = " + ex);
    		}

    	}
    	    	
    	
    }
    
    
    public static void foo(ASTNode node) {
    	String f = computeAbstract(node);
        System.out.println("---------------------");
        System.out.println("f = " + f);
        System.out.println("---------------------");
    }
    
    public static String computeAbstract(ASTNode node) {
    	if(node == null)
    		return "";
    	String f = "1";
    	
    	if(node instanceof IfStatement) {
    		
    		IfStatement ifstatement = (IfStatement)node;
    		String condition = ifstatement.getExpression().toString();
    		Statement thenStatement = ifstatement.getThenStatement();
    		if(thenStatement == null)
    			return "1";
    		String fthen = computeAbstract(thenStatement);
    		f = "(" + condition + "->" + fthen + ")";
    		
    		Statement elseStatement = ifstatement.getElseStatement();
    		if(elseStatement != null) {
    			String felse = computeAbstract(elseStatement);
    			f = f + "^" + "(" + condition + "->" + felse + ")";
    		}
    		
    	}
    	
    	return f;
    }

    //read file content into a string
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            //System.out.println(numRead);
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return  fileData.toString();
    }

    //loop directory to get file list
    public static void ParseFilesInDir() throws IOException{
        File dirs = new File(".");
        String dirPath = dirs.getCanonicalPath() + File.separator+"src"+File.separator;

        File root = new File(dirPath);
        //System.out.println(rootDir.listFiles());
        File[] files = root.listFiles ( );
        String filePath = null;

        for (File f : files ) {
            filePath = f.getAbsolutePath();
            if(f.isFile()){
                parse(readFileToString(filePath));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        parse(readFileToString("D:\\share\\nckh\\spoon\\test\\TestSpoon.java"));
    }
}