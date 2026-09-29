class Array {
    public static void main(String[] args) {
        int a[] = {10, 20, 30, 40, 50}, sum = 0;

        for(int x : a) sum += x;

        System.out.println("Sum = " + sum);
        System.out.println("Average = " + (double)sum/a.length);
    }
}
