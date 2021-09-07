package utils;

public class PrimeChecker {

	public static int nextPrime(int n) {
		int res = n + 1;
		while (!isPrime(res)) {
			res++;
		}
		return res;
	}

	private static boolean isPrime(int n) {
		if (n <= 1) {
			return false;
		}
		if (n == 2) {
			return true;
		}
		if ((n % 2) == 0) {
			return false;
		}
		int boundary = (int) Math.floor(Math.sqrt(n));
		for (int i = 3; i <= boundary; i += 2) {
			if ((n % i) == 0) {
				return false;
			}
		}
		return true;
	}
}
