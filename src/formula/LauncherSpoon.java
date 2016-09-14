package formula;

import java.io.File;
import java.util.List;

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
import tester.Test1;

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
/*	
	public void foo() {
		System.out.println("\n\nfoo\n\n");
		CtModel model = factory.getModel();
		Filter filter = new TypeFilter(CtMethod.class);
		List<CtMethod> methodList = model.getElements(filter);
		Abstraction test;
		for(CtMethod m: methodList) {
			System.out.println(m);
			test = new Abstraction(m);
//			System.out.println("f: " + test.methodAbstraction());
			List<String> f = test.methodAbstraction();
			for(String s: f) {
				System.out.println(s);
				if(Test1.checkBracket(s) == false)
					System.out.println("check bracket false" + "/n-------------------------------");
	//			if( Test1.checkDoubleBracket(s) == false)
	//				System.out.println("check double bracket false" + "/n-------------------------------");
			}
					
		}
		
		CtMethod firstMethod = methodList.get(0);
//		firstMethod.get
//		System.out.println(firstMethod);
		
//		Abstraction test = new Abstraction(firstMethod);
//		System.out.println("f: " + test.methodAbstraction());
		
	}
*/
	
	public void foo2() {
		System.out.println("\n\nfoo 2\n\n");
		CtModel model = factory.getModel();
		Filter filter = new TypeFilter(CtMethod.class);
		List<CtMethod> methodList = model.getElements(filter);
		MethodFormularization test;
		for(CtMethod m: methodList) {
			System.out.println(m);
			test = new MethodFormularization(m);
//			System.out.println("f: " + test.methodAbstraction());
			Formula f = test.getFormula();
			
			System.out.println("formula:");
			f.print(0);
					
		}
		
//		CtMethod firstMethod = methodList.get(0);
	}
	
	
	public static void main(String[] args) {
		
		System.out.println("hello world");
		LauncherSpoon launcher = new LauncherSpoon();
		String pathFile = "TestSpoon.java";
		File resource = new File(pathFile);
		if(!resource.exists()) {
			System.err.println("cannot open file");
			System.exit(1);
		}
		launcher.addInputResource(resource);
		launcher.buildModel();
		launcher.foo2();

	}

}
