import java.util.Arrays;

public class QuickSelect {
	public static void main(String[] args) {

		// println(quickSelect(Array(1,2, 3, 5, 7), 2))
		int[] arr1 = { 1, 2, 3, 5, 7, 1 };
		System.out.println("testing with " + Arrays.toString(arr1));

		System.out.println(quickSelect(arr1, 0, arr1.length - 1, 2));
		// System.out.println(quickSelect(arr1, 0, 6, 2));
		// System.out.println(quickSelect(arr1, 0, 6, 3));
		// System.out.println(quickSelect(arr1, 0, 6, 4));
		// System.out.println(quickSelect(arr1, 0, 6, 5));
		// System.out.println(quickSelect(arr1, 0, 6, 6));
		// System.out.println(quickSelect(arr1, 0, 6, 7));

		// int[] arr2 = { 5, 5, 1, 7 };
		// System.out.println("testing with " + Arrays.toString(arr2));
		// System.out.println(quickSelect(arr2, 0, 3, 1));
		// System.out.println(quickSelect(arr2, 0, 3, 2));
		// System.out.println(quickSelect(arr2, 0, 3, 3));
		// System.out.println(quickSelect(arr2, 0, 3, 4));

	}

	public static int quickSelect(int[] arr, int start, int stop, int k) {
		if (start > stop)
			return arr[start];

		int p = getMedianIndex(arr, start, stop);
		swap(arr, p, stop);

		int i = start;
		int j = stop - 1;

		while (true) {
			while (i <= j && arr[i] < arr[stop])
				i++;
			while (i <= j && arr[j] > arr[stop])
				j--;
			if (i <= j) {
				swap(arr, i++, j--);
			} else
				break;
		}

		swap(arr, i, stop); // swap with the bigger one only

		int sizeL = i - start;
		int sizeE = 1;
		int sizeG = stop - i;
		if (k > sizeL && k <= sizeL + sizeE) {
			return arr[i];
		} else if (k <= sizeL) {
			return quickSelect(arr, start, i - 1, k);
		} else {
			return quickSelect(arr, i + 1, stop, k - sizeL - sizeE);
		}
	}

	/*
	 * Swap 2 elements in an array
	 */
	public static void swap(int[] a, int i, int j) {
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	/*
	 * The below is to get the median with maximum comparison of 2. We may change it
	 * to a logic easier to understand.
	 */
	public static int getMedianIndex(int[] arr, int start, int stop) {
		int x = arr[start];

		int m = (start + stop) / 2;
		int y = arr[m];

		int z = arr[stop];
		if ((x - y) * (z - x) >= 0) // x >= y and x <= z OR x <= y and x >= z
			return start;
		else if ((y - x) * (z - y) >= 0) // y >= x and y <= z OR y <= x and y >= z
			return m;
		else
			return stop;
	}
}
