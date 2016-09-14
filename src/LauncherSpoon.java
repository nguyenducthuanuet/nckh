
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		System.out.println("\n\nfoo\n\n");
		CtModel model = factory.getModel();
		Filter filter = new TypeFilter(CtMethod.class);
		List<CtMethod> methodList = model.getElements(filter);
	//	for(CtMethod m: methodList) {
	//		System.out.println(m);
	//	}
		
		CtMethod firstMethod = methodList.get(0);
		System.out.println(firstMethod);
		
	}
	
	
	public static void main(String[] args) {
		LauncherSpoon launcher = new LauncherSpoon();
		String pathFile = "D:/share/nckh/spoon/test/TestSpoon.java";
		File resource = new File(pathFile);
		if( !resource.exists()) {
			System.out.println("file does not exit");
			System.exit(1);
		}
		launcher.addInputResource(resource);
		launcher.buildModel();
		launcher.foo();

	}

}
