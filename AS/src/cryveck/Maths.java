package cryveck;

public class Maths {
	
	public static int[] multMatInt(int[] tab, int k) {
		int[] result = new int[tab.length];
		for (int i = 0; i < tab.length; i++)
			result[i] = tab[i]*k;
		return result;
	}
	
	public static int[] subMatMat(int[] a, int[] b) {
		int[] result = new int[a.length];
		for (int i = 0; i < a.length; i++)
			result[i] = a[i] - b[i];
		return result;
	}
	
	public static int max(int[] configuration) {
		int max = 0;
		for (int i = 0; i<configuration.length; i++) {
			if(configuration[i]>max) {
				max = configuration[i];
			}
		}
		return max;
	}
	
}
