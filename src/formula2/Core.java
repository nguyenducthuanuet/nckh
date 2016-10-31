package formula2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import solver.SMTInput;
import solver.Z3Runner;
import spoon.compiler.ModelBuildingException;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import util.InfixToPrefix;
import util.Variable;

public class Core {
	
	public Core() {
		smtInput = new SMTInput();
		modelBuilder = createCompiler(factory);
	}
	
	public Core(String pathFile)
			throws ModelBuildingException, FileNotFoundException {
		this();
		
		this.pathFile = pathFile;
		
		create();
	}
	
	public String[] getMethodSignatures() {
		return methodSignatures;
	}
	
	
	public Factory createFactory() {
		return new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
	}
	
	public SpoonCompiler createCompiler() {
		return createCompiler(factory);
	}
	
	public SpoonCompiler createCompiler(Factory factory) {
		SpoonCompiler comp = new JDTBasedSpoonCompiler(factory);
		return comp;
	}
	
	public void addInputResource(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			addInputResource(new FileSystemFolder(file));
		} else {
			addInputResource(new FileSystemFile(file));
		}
	}
	
	private void addInputResource(SpoonResource resource) {
		modelBuilder.addInputSource(resource);
	}
	
	private void addInputResource(File resource) {
		modelBuilder.addInputSource(resource);
	}
	
	public void buildModel() {
		modelBuilder.build();
	}
	
	private void create() 
			throws ModelBuildingException, FileNotFoundException {
		addInputResource(pathFile);
		
		buildModel();
		
		CtModel model = factory.getModel();
		Filter filter = new TypeFilter(CtMethod.class);
		List<CtMethod> methodList = model.getElements(filter);
		int nMethods = methodList.size();
		methodSignatures = new String[nMethods];
		methods = new ArrayList<>();
		
		SMTInput smtInput = new SMTInput();
		for(int i = 0; i < nMethods; i++) {
			methodSignatures[i] = methodList.get(i).getSignature();
			methods.add( new MethodFormularization(methodList.get(i)) );
		}
	}
	
	public List<String> runSolver(String methodSignature, String constraint) 
				throws Exception {
		
		int index = find(methodSignatures, methodSignature);
		
		MethodFormularization mf = methods.get(index);
		
		String fileDir = "output.smt";
	    FileOutputStream fo = new FileOutputStream(fileDir);
	    if (mf == null) {
	    	System.out.println(mf);
	    	System.exit(-1);
	    }
	    if (smtInput == null) {
	    	System.out.println("smtInput is null");
	    	System.exit(-1);
	    }
	    smtInput.setFormula(mf.getFormula());
		smtInput.setListVariables(mf.getVariables());
		
		constraint = new InfixToPrefix(mf.getParametersList()).getOutput(constraint);
		constraint = "(not " + constraint + ")";
		
		List<String> constraints = new ArrayList<>();
		constraints.add(constraint);
		smtInput.setConstraints(constraints );
	    smtInput.printInputToOutputStream(fo);
	    List<String> result = Z3Runner.runZ3(fileDir);
//	    result.forEach(System.out::println);
	    
	    List<String> result1 = new ArrayList<String>();
	    List<Variable> parameters = mf.getParametersList();
	    result1.add(result.get(0));
	    for (Variable v: parameters) {
	    	String varName = v.getName() + "_0"; 
	    	for (int i = 1; i < result.size(); i++) {
	   
	    		if (result.get(i).indexOf(varName) >= 0) {
	    			String valueLine = result.get(i+1);
	    			System.out.println("value: " + valueLine);
	    			System.out.println("indexof(\"(\"): " + valueLine.indexOf("("));
	    			valueLine = valueLine.replace('(', ' ');
	    			valueLine = valueLine.replace(')', ' ');
	    			valueLine = valueLine.replace(" ", "");
	    			System.out.println("value: " + valueLine);
	    			result1.add(v.getName() + " = " + valueLine);
	    			break;
	    		}
	    	}
	    }
	    
	    for (int i = 1; i < result.size(); i++) {
	 	   
    		if (result.get(i).indexOf("return") >= 0) {
    			String valueLine = result.get(i+1);
    			System.out.println("value: " + valueLine);
    			System.out.println("indexof(\"(\"): " + valueLine.indexOf("("));
    			valueLine = valueLine.replace('(', ' ');
    			valueLine = valueLine.replace(')', ' ');
    			valueLine = valueLine.replace(" ", "");
    			System.out.println("value: " + valueLine);
    			result1.add("return = " + valueLine);
    			break;
    		}
    	}
	    
	    return result1;
//	    return result;
	}
	
	private int find(String[] arr, String value) {
		if (arr == null || value == null) 
			return -1;
		
		for (int i = 0; i < arr.length; i++) {
			if (value.equals(arr[i]))
				return i;
		}
		
		return -1;
	}
	
	
	private String pathFile;
	private List<MethodFormularization> methods;
	private SMTInput smtInput;
	private String[] methodSignatures;

	private Factory factory = createFactory();
	private SpoonCompiler modelBuilder;
	private Filter<CtType<?>> typeFilter;
}
