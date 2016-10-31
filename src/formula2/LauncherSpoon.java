package formula2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import solver.SMTInput;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.reflect.CtModel;
import spoon.reflect.cu.SourcePosition;
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
import tester.Test1;

import util.Helper;
import util.StringBox;
import util.Variable;

public class LauncherSpoon {
	
	private Factory factory = createFactory();
	
	private SpoonCompiler modelBuilder;
	
	private Filter<CtType<?>> typeFilter;
	
	public LauncherSpoon() {
		modelBuilder = createCompiler(factory);
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
	
	
	public void foo() {
		System.out.println("\n\nfoo 2\n\n");
		CtModel model = factory.getModel();
		Filter filter = new TypeFilter(CtMethod.class);
		List<CtMethod> methodList = model.getElements(filter);
		MethodFormularization test;
		SMTInput smtInput = new SMTInput();
//		CtMethod temp = methodList.get(0);
//		methodList.clear();
//		methodList.add(temp);
		
		for(CtMethod m: methodList) {
			System.out.println(m);
			System.out.println("m.getSignature: " + m.getSignature());
			System.out.println("m.position: " + m.getPosition());
			SourcePosition sp = m.getPosition();
			sp.getLine();

			test = new MethodFormularization(m);
//			System.out.println("f: " + test.methodAbstraction());
			List<String> f = test.getFormula();
			for(String s: f) {
				System.out.println(s);
				if(Test1.checkBracket(s) == false)
					System.out.println("check bracket false" + "/n-------------------------------");
				if( Test1.checkDoubleBracket(s) == false)
					System.out.println("check double bracket false" + "/n-------------------------------");

			}
/*			
			try {
				String fileDir = "output.smt";
			    FileOutputStream fo = new FileOutputStream(fileDir);
			    smtInput.setFormula(test.getFormula());
				smtInput.setListVariables(test.getVariables());
				List<String> constraints = new ArrayList<String>();
				constraints.add("(= return 10)");
				smtInput.setConstraints(constraints);
			    smtInput.printInputToOutputStream(fo);
			    List<String> result = runZ3(fileDir);
			    result.forEach(System.out::println);
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
*/
//			System.out.println("SMT input:");
//			smtInput.setFormula(test.getFormula());
//			smtInput.setListVariables(test.getVariables());
//			smtInput.printInput();
		}

		
	}
		
	public static List<String> runZ3(String filename) throws IOException {
		List<String> result = new ArrayList<String>();
		String pathToZ3 = "z3\\bin\\z3.exe";
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", pathToZ3 + " -smt2 " + filename);
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			result.add(line);
		}
		return result;
	}
		
	
	public static void main(String[] args) {
		
		System.out.println("hello world");
		LauncherSpoon launcher = new LauncherSpoon();
		String pathFile = "test_spoon.java";
		File resource = new File(pathFile);
		if(!resource.exists()) {
			System.err.println("cannot open file");
		//	System.exit(1);
		}
		launcher.addInputResource(resource);
		
		try {
			launcher.buildModel();
		} catch(Exception e) {
			System.out.println("exception: " + e);
		}
		
		launcher.foo();

	}

}
