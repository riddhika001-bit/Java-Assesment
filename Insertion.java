class Insertion {
    public static void main(String[] args) {
        int[] a = {7, 3, 5, 1, 4};

        for(int i=1;i<a.length;i++) {
            int x=a[i], j=i-1;
            while(j>=0 && a[j]>x) {
                a[j+1]=a[j];
                j--;
            }
            a[j+1]=x;
        }

        for(int x:a) System.out.print(x+" ");
    }
}
