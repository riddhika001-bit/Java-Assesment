class Palindrome {
    public static void main(String[] args) {
        String s = "madam";
        String r = new StringBuilder(s).reverse().toString();

        System.out.println(s.equals(r) ? "Palindrome" : "Not Palindrome");
    }
}
