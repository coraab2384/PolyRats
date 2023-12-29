package algebra.sets;

import java.text.DecimalFormat;

public class Frac {
    //private int whole;
    private long num;
    private int den = 1;
    //private boolean negative;
    protected enum FracComparisons {
        AddInv,
        GreaterThan,
        LessThan,
        Equal
    }
    protected enum FracErrors {
        UnparsableInt,
        WrongFracCount,
        WrongDecCount,
        WrongSymCount,
        DivZero,
        None
    }
    public static final Frac zero = new Frac();

    public Frac() {}
    public Frac(int whole) {
        intFrac(whole, 0, 1, true);
    }
    public Frac(long whole) {
        intFrac(0, whole, 1, true);
    }
    public Frac(int num, int den) {
        intFrac(0, (long)num, den, true);
    }
    public Frac(long num, int den) {
        intFrac(0, num, den, true);
    }
    public Frac(int whole, int num, int den) {
        intFrac(whole, num, den, true);
    }
    public Frac(String input) {
        FracErrors error = parseFrac(input, false);
        if (error != FracErrors.None) fracErrorHandler(error, input);
    }
    private FracErrors intFrac(int whole, long num, int den, boolean handleError) {
        FracErrors error;
        if (den == 0) {
            error = FracErrors.DivZero;
            if (handleError)
                fracErrorHandler(error, Integer.toString(den));
        }
        else {
            error = FracErrors.None;
            if (den < 0) {
                num *= -1;
                den *= -1;
            }
            reduce(num, den);
        }
        return error;
    }
    protected FracErrors parseFrac(String originalInput, boolean handleError) {
        String wholeS = "", numS = "", denS = "";
        FracErrors error = FracErrors.None;
        String input = originalInput.replaceAll("\\s{2,}", " ").trim();
        if (input.contains(".")) {
            String[] inputA = input.split(".");
            if (inputA.length != 2) error = FracErrors.WrongDecCount;
            else {
                boolean tempNeg = false;
                wholeS = inputA[0];
                numS = inputA[1].trim();
                if (numS.startsWith("-")) {
                    tempNeg = true;
                    numS = numS.substring(1);
                }
                denS = "1" + ("0".repeat(numS.length()));
                if (tempNeg) numS = "-" + numS;
            }
        }
        else if (input.contains("/")) {
            String[] inputD = input.split("/");
            if (inputD.length != 2) error = FracErrors.WrongFracCount;
            else {
                denS = inputD[1];
                inputD[0].trim();
                if (inputD[0].contains("&") || inputD[0].contains(" ")) {
                    String[] inputC;
                    if (inputD[0].contains("&")) inputC = inputD[0].split("&");
                    else inputC = inputD[0].split(" ");
                    if (inputC.length != 2) error = FracErrors.WrongSymCount;
                    else {
                        wholeS = inputC[0];
                        numS = inputC[1];
                    }
                }
                else {
                    wholeS = "0";
                    numS = inputD[0];
                }
            }
        }
        else if (input.contains(",")) {
            String[] inputA = input.split(",");
            if (inputA.length == 2) {
                if (inputA[1].contains("/")) {
                    String[] inputB = inputA[1].split("/");
                    wholeS = inputA[0];
                    numS = inputB[0];
                    denS = inputB[1];
                }
                else {
                    wholeS = "0";
                    numS = inputA[0];
                    denS = inputA[1];
                }
            }
            else if (inputA.length == 3) {
                wholeS = inputA[0];
                numS = inputA[1];
                denS = inputA[2];
            }
            else error = FracErrors.WrongSymCount;
        }
        else if (input.contains(" ")) {
            String[] inputA = input.split(" ");
            if (inputA.length == 2) {
                boolean tempNeg = false;
                wholeS = inputA[0];
                numS = inputA[1].trim();
                if (numS.startsWith("-")) {
                    tempNeg = true;
                    numS = numS.substring(1);
                }
                denS = "1" + ("0".repeat(numS.length()));
                if (tempNeg) numS = "-" + numS;
            }
            else if (inputA.length == 3) {
                wholeS = inputA[0];
                numS = inputA[1];
                denS = inputA[2];
            }
            else error = FracErrors.WrongSymCount;
        }
        try {
            error = intFrac(Integer.parseInt(wholeS),
                    Long.parseLong(numS),
                    Integer.parseInt(denS), false);
        }
        catch (NumberFormatException e) {
            error = FracErrors.UnparsableInt;
        }
        return error;
    }

    private static String fracErrorHandler(FracErrors error, String input) {
        return "lol";
    }
    //region getset
    public int getUnsignedWhole() {
        return (int)(Math.abs(num) / Integer.toUnsignedLong(den));
    }
    public String stringUnsignedWhole() {
        return Long.toString(Math.abs(num) / Integer.toUnsignedLong(den));
    }
    public long getSignedWhole() {
        return (num / Integer.toUnsignedLong(den));
    }
    public String stringSignedWhole() {
        return Long.toString(num / Integer.toUnsignedLong(den));
    }
    public int getUnsignedTop() {
        return (int)(Math.abs(num) % Integer.toUnsignedLong(den));
    }
    public String stringUnsignedTop() {
        return Long.toString(Math.abs(num) % Integer.toUnsignedLong(den));
    }
    public long getSignedTop() {
        return num % Integer.toUnsignedLong(den);
    }
    public String stringSignedTop() {
        return Long.toString(num % Integer.toUnsignedLong(den));
    }
    public long getNum() {
        return num;
    }
    public String stringNum() {
        return Long.toString(num);
    }
    public int getDen() {
        return den;
    }
    public String stringDen() {
        return Integer.toUnsignedString(den);
    }
    public boolean isNegative() {
        return num < 0;
    }
    public boolean isPositive() {
        return num >= 0;
    }
    public String intoMixedNum() {
        return (num / Integer.toUnsignedLong(den)) +
                " & " + (Math.abs(num) % Integer.toUnsignedLong(den)) +
                " / " + Integer.toUnsignedString(den);
    }
    public String intoImpFrac() {
        return num + " / " + Integer.toUnsignedString(den);
    }
    public String intoDec(int decNumber) {
        decNumber = Math.max(decNumber, 1);
        DecimalFormat df = new DecimalFormat("#." + "0".repeat(decNumber));
        return df.format(num / (double)Integer.toUnsignedLong(den));
    }
    //endregion
    private void reduce(long bigNum, long bigDen) {
        if (bigNum == 0) {
            num = 0;
            den = 1;
        }
        else {
            if (bigNum > Integer.MAX_VALUE || bigNum < Integer.MAX_VALUE ||
                    bigDen > (long)Integer.MAX_VALUE * 2 + 1) {
                long reducer = GCD(Math.abs(bigNum), bigDen);
                num = bigNum / reducer;
                den = (int)(bigDen / reducer);
            }
            else {
                den = (int)bigDen;
                int reducer = GCD((int)bigNum, den);
                num = bigNum / reducer;
                den = Integer.divideUnsigned(den, reducer);
            }
        }
    }
    protected static long GCD(long a, long b) {
        return (b == 0) ? a : GCD(b, a % b);
    }
    protected static int GCD(int a, int b) {
        return (b == 0) ? a : GCD(b, Integer.remainderUnsigned(a, b));
    }
    protected static FracComparisons compare(Frac a, Frac b) {
        if ((a.num < 0 ^ b.num < 0) && a.den == b.den && a.num == b.num * -1)
            return FracComparisons.AddInv;
        //else
        if (a.num == b.num && a.den == b.den)
            return FracComparisons.Equal;
        //else
        if (a.num * b.den < b.num * a.den)
            return FracComparisons.LessThan;
        //else
        return FracComparisons.GreaterThan;
    }
    public boolean equalsTo(Frac oth) {
        return (compare(this, oth) == FracComparisons.Equal);
    }
    public boolean greaterThan(Frac oth) {
        FracComparisons comp = compare(this, oth);
        return (comp == FracComparisons.GreaterThan ||
                (comp == FracComparisons.AddInv && num < 0));
    }
    public boolean lessThan(Frac oth) {
        FracComparisons comp = compare(this, oth);
        return (comp == FracComparisons.LessThan ||
                (comp == FracComparisons.AddInv && oth.num < 0));
    }
    public Frac copy() {
        Frac ans = new Frac();
        ans.num = num;
        ans.den = den;
        return ans;
    }
    public Frac negate() {
        Frac ans = new Frac();
        ans.num = num * -1;
        ans.den = den;
        return ans;
    }
    public Frac invert() {
        Frac ans = new Frac();
        ans.num = den;
        ans.den = (int)num;
        return ans;
    }

    public Frac add(Frac oth) {
        Frac ans = new Frac();
        ans.arith(this, oth, false);
        return ans;
    }
    public Frac add(int number) {
        Frac ans = new Frac();
        ans.arith(this, new Frac(number), false);
        return ans;
    }
    public Frac subtract(Frac oth) {
        Frac ans = new Frac();
        ans.arith(this, oth, true);
        return ans;
    }
    public Frac subtract(int number) {
        Frac ans = new Frac();
        ans.arith(this, new Frac(number), true);
        return ans;
    }
    protected void arith(Frac a, Frac b, boolean subtract) {
        long bigNum = (subtract) ? (a.num * b.den - b.num * a.den) :
                (a.num * b.den + b.num * a.den);
        long bigDen = (long)a.den * b.den;
        reduce(bigNum, bigDen);
    }
    public Frac multiply(Frac oth) {
        Frac ans = new Frac();
        ans.mult(this, oth, false);
        return ans;
    }
    public Frac multiply(int number) {
        Frac ans = new Frac();
        ans.mult(this, new Frac(number), false);
        return ans;
    }
    public Frac divide(Frac oth) {
        Frac ans = new Frac();
        ans.mult(this, oth, true);
        return ans;
    }
    public Frac divide(int number) {
        Frac ans = new Frac();
        ans.mult(this, new Frac(number), true);
        return ans;
    }
    protected void mult(Frac a, Frac b, boolean divide) {
        long bigNum, bigDen;
        FracErrors error = FracErrors.None;
        if (divide) {
            bigNum = Integer.toUnsignedLong(b.den);
            bigDen = b.num;
            if (bigDen < 0) {
                bigDen *= -1;
                bigNum *= -1;
            }
            else if (bigDen == 0)
                error = FracErrors.DivZero;
        }
        else {
            bigNum = b.num;
            bigDen = Integer.toUnsignedLong(b.den);
        }
        if (error == FracErrors.None) {
            bigNum *= a.num;
            bigDen *= a.den;
            reduce(bigNum, bigDen);
        }
        else {
            fracErrorHandler(error, b.intoMixedNum());
            num = a.num;
            den = a.den;
        }
    }
}
