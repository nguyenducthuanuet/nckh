
public class TestSpoon
{


	public static int testFor0() {
		int x = 1;
		int sum = 0;
		sum = x++;
		for(;;) {
			x++;
			if(x > 5)
				break;
		}
		
		for(int i = 0; i < 10; i++) {
			sum += i;
			if(sum == 2)
				return sum;
			if(sum == 4)
				break;
			if(sum == 6)
				continue;
			sum += sum;
		}
	
		return sum;
	}
/*	
	public static int testFor1() {
		int x = 1;
		int sum = 0;
		for(;;) {
			x++;
			if(x > 5)
				break;
		}
		
		for(int i = 0; i < 10; i++) {
			sum += i;
			if(sum == 2)
				return sum;
		}
	
		return sum;
	}

	public static void testFor2() {
		int x = 1;
		int sum = 0;
		for(;;) {
			x++;
			if(x > 5)
				break;
			sum += x;
		}
	}
	
	public static int testFor3() {
	
		int sum = 0;
		for(int i = 0; i < 10; i++) {
		
			if(sum == 6)
				continue;
			sum += sum;
		}
	
		return sum;
	}
*/
	
/*
	public static int testReturn1(int a, int b) {
		if(b == 0)
			return 0;
		
		return a / b;
	}
	
	public static int testReturn2(int a, int b) {
		if(b == 0) {
			a = b;
			a = a+b;
			return a;
		} else {
			b++;
		}
		
		if(a == 0) {
			a = a;
			a = a+b;
		}
		
		if(a == 0) {
			
		} else {
			a = b;
			a = a + b;
		}
		
		if(a == b) {
			a = b;
			a++;
			if(a == 0)
				return b;
		}
		else {
			a = b;
			b++;
		}
		
		
		
		return a / b;
	}
*/
	
	
	public static void switch1() {
		int n = 0;
		switch(n) {
		case 1:
			n++;
			break;
		case 2:
		case 3:
		case 4:
			n += 10;
			break;
		default:
			n = n * n;
			break;
		}
	}


	
/*
	public static int min(int a, int b)
	{
		int x = a + a +b;
		int y, t = 0;
		y = a + a + b;
		y = a + b + a;
		y = a + a*b;
		int min;
		
		if(a > b) {
			int i = 0;
			i++;
			if(a > b)
				a++;
		} else {
			a = b;
			a = -1;
//			a = +++1;
		}
		
		int i = 3;
		
		if(a > b) {
			a = b;
		} else {
			
		}

		if(a > b) {
			
		} else {
	
		}
			
		
		if( !(a >= b) ) 
			a = b;
		
		if(a >= b) {
			min = a;
			a = a * b;
		}
		else {
			min = b;
			b = a * b;
		}
		
		int c;
		c = a + b;
		
		if(a > b) {
			min = a;
			a = a * b;
		}
		else if (a == b) {
			min = b;
			b = a * b;
		}
		else 
			min = 0;		
		
			
		{
			{
				
			}
			
		}
		
		
		{
			int z = 0;
		}
		
		{
			int z = 0;
		}

		return min;	
	}
*/	

	
/*
	public static int sum(int n) {
		int sum = 0;
		for(int i = 0; i <= n; i++) {
			sum += i;
		}
		
		return sum;
	}
	
	public static void basic() {
		int i = 0;
		int a = i++;
		int b;
		b = i++;
		
	}
	
	public static void testSwitch(int number) 
	{
		int temp = 0;
		switch(number) {
			case 1: temp = number;
			case 2: temp = number*number;
			case 3:
			case 4:
			default:	
		}
		
		switch(number) {
		case 1:
			temp = temp;
			break;
		default:
			temp = temp;
			break;
//			temp = temp * 2;
		case 3:
			temp = temp;
			{
				System.out.println("break");
				break;
			}	
			
		case 4:
			System.out.println("hello world");
			return;
		}
		
		switch(number) {
			case 1: case 2:
				temp = number;
				temp = temp * 2 * number;
				break;
			case 3:
			{
				
			}
			case 4:
			{
				temp = number*2;
			}
			temp = temp;
		}
	}
	
	


	public static int max(int a, int b) {
		int max = a > b ? a : b;
		return max;
	}

	public static int foo1()
	{
		int a, b = 10, c = b * 2;
		int sum = 0;
		for(int i = 0, j = 3; j < 10 && i < 100; i++)
		{
			sum += i;		
		}	
		
		int count = 0;
		for(;;)
		{
			if(count > 10)
				break;
			else
				count++;
		}

		return sum;
	}
*/	


/*
	public static int foo2()
	{
		int i = 0, sum = 0;
		while(i < 100)
		{
			sum += i;
			i++;
		}

		return sum;
	}
	
	public static int foo3()
	{
		int i = 0;
		while(true) {
			if(i == 3)
				break;
			i++;
		}
		
		return i;
	}
	
	public static int foo4()
	{
		int i = 0;
		while(i < 10) {
			if(i == 5)
				return i;
			i++;
		}
		
		return i;
	}
	
	public static int foo5()
	{
		int i = 0;
		int j = i < 5 ? 4 : 5;
		
		return j;
	}
	
	public static void foo6(int a, int b) 
	{
		if(a < b) {
		//	int a = a * 2;	// compile error
		//	int a = 4;
			System.out.println("a = " + a);
		}
		else {
		//	int b = a * b;	// compile error
		//	int b = 5;
			System.out.println("b = " + b);
		}
		
		{
		//	int a;
		}
	}
	
	
	public static void main(String[] args)
	{
		testSwitch(3);
	}
*/	

	
}
