package formula;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
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
		}

		returnType = method.getType().toString();
		
		init();
		
		formula = formularize(method.getBody(), lastReturnFlag, null, null);
	}
	
	public void printFormula() {
		formula.print(0);
	}

	@Override
	public Formula getFormula() {
		return formula;
	}
	
	
	public static void main(String[] args) {

		LauncherSpoon launcher = new LauncherSpoon();
		String pathFile = "test_spoon.java";
//		pathFile = "test.java";
		File resource = new File(pathFile);
		if(!resource.exists()) {
			System.err.println("file does not exist");
			System.exit(1);
		}
		
		launcher.addInputResource(pathFile);
		launcher.buildModel();
		launcher.foo2();
	}

	private CtMethod method;
	
	private String returnType;
}
