class Vowels {
    public static void main(String[] args) {
        String s = "Hello World";
        int c = 0;

        for (char x : s.toCharArray())
            if ("aeiouAEIOU".indexOf(x) >= 0) c++;

        System.out.println(c);
    }
}
