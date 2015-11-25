public class LiveUniverseAndEverything{

	public static void main(String[] args) {


		for (int i = -2; i<10;i++)
			System.out.println("" + i + "\t" + ProcUnnec(i));
				


		System.out.println("" + 100 + "\t" + ProcUnnec(100));
	}



	private static int ProcUnnec(int n){
		if (n<0) return 23;

		return (ProcUnnec(n-1) * power(-1,n)) + (n + 42);
	}

	private static int power(int i, int n){

		if  (n == 0)
			return 1;

		else return power(i,n-1)*i;
	}
}