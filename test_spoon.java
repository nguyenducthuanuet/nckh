
public class test_spoon{
	
/*
	public static int minab(int a, int b){
		int d = 5, c = d;
		int e;
		e = 5;
		a = a - b + c +e;
	
		
//		for(int i = 0; i < 100; i++) {
//			a = a - b;
//			b = a*b;
//			if(b == a)
//				a = b + 1;
//		}
		
		if(a > b){
			int g = 100;
			int h = g;
			
			a = a+2;
			b = a-1;
			b = a*a;
			
			if( b == 0)
				a = 1;
			
		}
		else {
			a = 2;
			b = 1;
			
		}
		
		a = a/b;
		b = a*b;
		return a;
	}
	
	public int foo(int n) {
		int sum = 0;
		for(int i = 0; i < n; i++) {
			sum = sum + i;
		}
		
		return sum;
	}
	
	public int test(int a, int b) {
		if (b == 0)
			return 0;
		if (a == 0)
			return -1;
		return a/b;
	}
	
	public int test2(int a, int b) {
		double x = 0.0;
		float y = 1.0f;
		
		
		return a/b;
	}
	
	public void switchCase() {
		int n = 10;
		switch(n) {
		case 1:
			n = 1;
			break;
		case 2:
			n = 2;
			break;
		case 3: case 4: case 5: case 6:
			n = 6;
		case 7:
			n = 0;
		default:
			n = -1;
				
		}
	}
	
	public int testWhile(int a, int b) {
//		bool a;
		
		int n = 10;
		int i = 0;
		while (i < 10) {
			i++;
		}
		
		return a/b;
	}
	
*/
	
	public int ucln1(int a, int b) {
		if (a < 0)
			a = -a;
		if (b < 0)
			b = -b;
		if (a == 0 || b == 0)
			return a + b;
		while(a != b) {
			if(a > b)
				a = a - b;
			else
				b = b - a;
		}
		
		return a;
	}
	
	public int ucln2(int a, int b) {
		if (a < 0)
			a = -a;
		if (b < 0)
			b = -b;
		
		if (a == 0 || b == 0)
			return 1;
		
		while(0 != b) {
			b = a % b;
			a = b;
		}
		
		return a;
	}

	/*
	public static void main(String a[]){
		int x = 5, y = 6;
		int z = minab(x,y);
		System.out.println(z);
	}
	*/
	//1 & (d0=5) & (c0=d0) & (e0=5) & (a1 = a0 - b0 + c0 + e0) & ((a1 > b0) -> (g0=100) & (h0=g0) & (a2 = a1 + 2) & (b1 = a2 - 1) & (b2 = a2 * a2) & ((b2 == 0) -> (a3 = 1))) & (-(a1 > b0) -> (a2 = 2) & (b1 = 1) & (b2 = b1)) & (a3 = a2 / b2) & (b3 = a3 * b2) & (ret = 0)
	//1 ^ (d_0=5) ^ (c_0=d_0) ^ (e_0=5) ^ (a_1 = (((a_0-b_0)+c_0)+e_0)) ^ ((a_1>b_0)->1 ^ (g_0=100) ^ (h_0=g_0) ^ (a_2 = (a_1+2)) ^ (b_1 = (a_2-1)) ^ (b_2 = (a_2*a_2)) ^ ((b_2==0)->(a_3 = 1))) ^ ( ~(a_1>b_0)->1 ^ (a_2 = 2) ^ (b_1 = 1)^b_2=b_1) ^ (a_3 = (a_2/b_2)) ^ (b_3 = (a_3*b_2)) ^ ret = 0
	//1 ^ (d_0=5) ^ (c_0=d_0) ^ (e_0=5) ^ (a_1 = (((a_0-b_0)+c_0)+e_0)) ^ ((a_1>b_0)->1 ^ (g_0=100) ^ (h_0=g_0) ^ (a_2 = (a_1+2)) ^ (b_1 = (a_2-1)) ^ (b_2 = (a_2*a_2)) ^ ((b_2==0)->(a_3 = 1)) ^ ( ~(b_2==0)->null^a_3=a_2)) ^ ( ~(a_1>b_0)->1 ^ (a_2 = 2) ^ (b_1 = 1)^a_3=a_2^b_2=b_1) ^ (a_4 = (a_3/b_2)) ^ (b_3 = (a_4*b_2)) ^ ret = 0
	//1 & (d0=5) & (c0=d0) & (e0=5) & (a1 = a0 - b0 + c0 + e0) & ((a1 > b0) -> (g0=100) & (h0=g0) & (a2 = a1 + 2) & (b1 = a2 - 1) & (b2 = a2 * a2) & ((b2 == 0) -> (a2 = 1))) & (-(a1 > b0) -> (a1 = 2) & (b0 = 1) & (a2 = a1) & (b2 = b0)) & (a3 = a2 / b2) & (b3 = a3 * b2) & (ret = 0)
}