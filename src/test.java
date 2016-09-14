
public class test {
	
	public static int max(int a, int b) {
		if(a > b)
			return a;
		else 
			return b;
	}
	
	public void testSwitch() {
		int a = 2;
		
		
		switch(a) {
			case 1:
				System.out.println("one");
				break;
			case 2:
				System.out.println("two");
			default:
				System.out.println("default");
				break;
			case 10:
				System.out.println("ten");
				break;
		}
	}
	
	public static void testForAndSwitch() {
		for(int i = 0; i < 10; i++) {
			switch(i) {
			case 1:
				continue;
			case 3:
				break;
			}
			
			System.out.println("i = " + i);
		}
	}


	public static void main(String[] args) {
		
		testForAndSwitch();

	}

}
