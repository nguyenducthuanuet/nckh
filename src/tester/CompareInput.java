package tester;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CompareInput {
	public static void main(String[] args) {
		
		try {
			boolean result = compare("abstraction.txt", "formula.txt");
			if(result)
				System.out.println("indentity");
			else
				System.out.println("different");
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static boolean compare(String filename1, String filename2) throws IOException
	{
		
		Scanner inFile1 = new Scanner(new File(filename1));
		Scanner inFile2 = new Scanner(new File(filename1));
		
		String l1 = null, l2 = null;
		while(inFile1.hasNextLine() && inFile2.hasNextLine()) {
			l1 = inFile1.nextLine();
			l2 = inFile2.nextLine();
			
			if( !l1.equals(l2))
				return false;
		}
		
		if(inFile2.hasNextLine() || inFile1.hasNextLine() )
			return false;
		
		inFile1.close();
		inFile2.close();
		
		return true;
	}
}
