class SecondLargest {
    public static void main(String[] args) {
        int[] a = {10, 25, 15, 40, 30};
        int max = a[0], second = a[1];

        for (int i = 2; i < a.length; i++) {
            if (a[i] > max) {
                second = max;
                max = a[i];
            } else if (a[i] > second)
                second = a[i];
        }

        System.out.println(second);
    }
}
