import algebra.sets.Frac;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");
        System.out.println(2000000000 * 4);
        Frac a = new Frac(1,5,3);
        Frac b = new Frac("3 & 1 / 2");
        System.out.println(b.getSignedWhole());
        System.out.println(a.intoString());
        System.out.println(b.intoString());
        System.out.println((a.divide(b)).intoString());
    }
}