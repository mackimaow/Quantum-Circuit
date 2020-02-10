package testLib;

public class Test {
	
	public static void main(String[] args) {
		Object[] test = new Test[1];
		test[0] = new Test();
		Test[] test2 = (Test[]) test;
		System.out.println(test2[0]);
	}

	@Override
	public String toString() {
		return "yo";
	}
	
}
