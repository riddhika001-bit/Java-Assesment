class Selection {
    public static void main(String[] args) {
        int[] a = {6, 2, 8, 3, 5};

        for(int i=0;i<a.length-1;i++) {
            int m=i;
            for(int j=i+1;j<a.length;j++)
                if(a[j]<a[m]) m=j;

            int t=a[i];
            a[i]=a[m];
            a[m]=t;
        }

        for(int x:a) System.out.print(x+" ");
    }
}
