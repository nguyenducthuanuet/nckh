package formula2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import util.Helper;
import util.StringBox;
import util.Variable;

public class MethodFormularization extends Formularization {
	
	
	
	public MethodFormularization(CtMethod method) {
		this.method = method;
		
		List<CtParameter> parameters = method.getParameters();
		listVariables = new ArrayList<Variable>();
		
		Variable varTemp;
		for(CtParameter p: parameters) {
			varTemp = new Variable(p.getSimpleName(), p.getType().toString());
			varTemp.initialize();
			listVariables.add(varTemp);
			parametersList.add(varTemp);
		}

		returnType = method.getType().toString();
		
		init();
	}
	
	public List<Variable> getParametersList() {
		return parametersList;
	}
	
	public void printFormula() {
		for(String s: formula) {
			System.out.println(s);
		}
	}

	@Override
	public List<String> getFormula() {
		if (formula == null) {
			formula = formularize(method.getBody(), lastReturnFlag, null, null);
		}
		return formula;
	}
	
	public List<Variable> getVariables() {
		if ( !hasGetVariables ) {
			listVariables.addAll(flagVariables);
			if ( !returnType.equals("void") ) {
				Variable returnVar = new Variable("return", returnType);
				returnVar.initialize();
				returnVar.setIndex(-1);
				listVariables.add(returnVar);
			}
			hasGetVariables = true;
		}
		return listVariables;
	}
	
	public static void main(String[] args) {

		LauncherSpoon launcher = new LauncherSpoon();
		String pathFile = "test_spoon.java";
//		pathFile = "test.java";
		File resource = new File(pathFile);
		if(!resource.exists()) {
			System.err.println("cannot open file");
			System.exit(1);
		}
		launcher.addInputResource(pathFile);
		launcher.buildModel();
		launcher.foo();

	}

	private CtMethod method;
	
	private String returnType;
	
	private boolean hasGetVariables = false;
	
	List<Variable> parametersList = new ArrayList<Variable>();
}
