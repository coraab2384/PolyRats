package algebra.sets;

public class Frac {
    private long num; // = 0
    private int den = 1;
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
        this.intFrac(whole, 0, 1, true);
    }
    public Frac(long whole) {
        this.intFrac(0, whole, 1, true);
    }
    public Frac(int num, int den) {
        this.intFrac(0, (long)num, den, true);
    }
    public Frac(long num, int den) {
        this.intFrac(0, num, den, true);
    }
    public Frac(int whole, int num, int den) {
        this.intFrac(whole, num, den, true);
    }
    public Frac(String input) {
        FracErrors error = this.parseFrac(input, false);
        if (error != FracErrors.None) fracErrorHandler(error, input);
    }
    //9 decimals corresponds to 10 denominator digits
    //which is the max length of the digit of the denominator
    //we can go to intFrac, which is shorter, if we can round directly into a long
    //which is when the int input is 0 (also negative, because that makes no sense)
    //otherwise we can turn it into a string with the desired decimal length
    //and feed it to the string parser
    public Frac(double inputToApprox, int decimalPrecisionForApprox) {
        String inputS;
        decimalPrecisionForApprox = Math.min(decimalPrecisionForApprox, 9);
        if (decimalPrecisionForApprox <= 0)
            this.intFrac(0, Math.round(inputToApprox), 1, false);
        else {
            inputS = String.format(("%." + decimalPrecisionForApprox + "f"),
                    inputToApprox);
            this.parseFrac(inputS, false);
        }
    }
    //we check for dividing by 0 and deal with some ambiguity
    //with mixed negative numbers
    //then turn it into an improper fraction and run it through the reducer
    private FracErrors intFrac(int whole, long num, int den, boolean handleError) {
        FracErrors error;
        if (den == 0) {
            error = FracErrors.DivZero;
            if (handleError)
                fracErrorHandler(error, Integer.toString(den));
        }
        else {
            error = FracErrors.None;
            if (whole < 0 && num >= 0)
                num *= -1;
            num += whole * Integer.toUnsignedLong(den);
            this.reduce(num, den);
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
            error = this.intFrac(Integer.parseInt(wholeS),
                    Long.parseLong(numS),
                    Integer.parseUnsignedInt(denS), false);
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
        long denL = Integer.toUnsignedLong(den);
        return (num / denL) + " & " + (Math.abs(num) % denL) +
                " / " + denL;
    }
    public String intoImpFrac() {
        return num + " / " + Integer.toUnsignedString(den);
    }
    public String intoDec(int decNumber) {
        decNumber = Math.max(decNumber, 1);
        java.text.DecimalFormat df = new java.text.DecimalFormat(
                "#." + "0".repeat(decNumber));
        return df.format(num / (double)Integer.toUnsignedLong(den));
    }
    public double intoDouble() {
        return num / (double)Integer.toUnsignedLong(den);
    }
    //endregion

    //reduce the fraction to lowest form
    //set denominator to 1 if the numerator is 0
    private void reduce(long bigNum, long bigDen) {
        if (bigNum == 0) {
            num = 0;
            den = 1;
        }
        //it seems wasteful to run the gcd function with longs when that is unneccessary
        //ideally, the numbers are within the range of ints, the primary reason
        //to have the numerator be stored as a long is because it also contains
        //within itself the data of the whole number, and this
        //means that in operations, the value can quickly balloon before ultimately
        //being simplified
        else if (bigNum > Integer.MAX_VALUE || bigNum < Integer.MIN_VALUE ||
                bigDen > (long)Integer.MAX_VALUE * 2 + 1) {
            long reducer = GCD(Math.abs(bigNum), bigDen);
            num = bigNum / reducer;
            den = (int)(bigDen / reducer);
        }
        else {
            den = (int)bigDen;
            int reducer = GCD((int)Math.abs(bigNum), den);
            num = bigNum / Integer.toUnsignedLong(reducer);
            den = Integer.divideUnsigned(den, reducer);
        }
    }
    //recursive Euclidian
    protected static long GCD(long a, long b) {
        return (b == 0) ? a : GCD(b, a % b);
    }
    //recursive Euclidian, but using unsigned ints
    protected static int GCD(int a, int b) {
        return (b == 0) ? a : GCD(b, Integer.remainderUnsigned(a, b));
    }
    //compare fractions for the bool functions, also looks for additive inverses
    protected static FracComparisons compare(Frac a, Frac b) {
        if (a.den == b.den && a.num == b.num)
            return FracComparisons.Equal;
        //else
        if (a.den == b.den && a.num == b.num * -1)
            return FracComparisons.AddInv;
        //else
        if (a.num * b.den < b.num * a.den)
            return FracComparisons.LessThan;
        //else
        return FracComparisons.GreaterThan;
    }
    public boolean equalsTo(Frac oth) {
        return (compare(this, oth) == FracComparisons.Equal);
    }
    //if it gets that two are additive inverses, returns true if this one
    //is positive
    public boolean greaterThan(Frac oth) {
        FracComparisons comp = compare(this, oth);
        return (comp == FracComparisons.GreaterThan ||
                (comp == FracComparisons.AddInv && this.num < 0));
    }
    //if it finds that two are additive inverses, returns true if the other one
    //is positive
    public boolean lessThan(Frac oth) {
        FracComparisons comp = compare(this, oth);
        return (comp == FracComparisons.LessThan ||
                (comp == FracComparisons.AddInv && oth.num < 0));
    }
    //copies the fraction to a new object
    public Frac copy() {
        Frac ans = new Frac();
        ans.num = this.num;
        ans.den = this.den;
        return ans;
    }
    //copies the fraction to a new object, but negated
    public Frac negate() {
        Frac ans = new Frac();
        ans.num = this.num * -1;
        ans.den = this.den;
        return ans;
    }
    //creates a new fraction with that is flipped, along with
    //reworking the negatives to account for the flip
    public Frac invert() {
        Frac ans = new Frac();
        ans.num = this.den;
        if (this.num < 0) {
            ans.num *= -1;
            ans.den = (int)(this.num * -1);
        }
        else
            ans.den = (int)this.num;
        return ans;
    }

    //essentially a wrapper for the arithmetic function, that locks the add/subtract
    //flag to add
    public Frac add(Frac oth) {
        Frac ans = new Frac();
        ans.arith(this, oth, false);
        return ans;
    }
    //does the same as the above function, but also turns the int input into a fraction
    public Frac add(int number) {
        Frac ans = new Frac();
        ans.arith(this, new Frac(number), false);
        return ans;
    }
    //wrapper for the arith function, but with subtraction flag set true
    public Frac subtract(Frac oth) {
        Frac ans = new Frac();
        ans.arith(this, oth, true);
        return ans;
    }
    //the above but for an int input instead of a frac
    public Frac subtract(int number) {
        Frac ans = new Frac();
        ans.arith(this, new Frac(number), true);
        return ans;
    }
    //first we put the denominators into longs
    // we do this explicitly to account for them being unsigned
    //To add, you get a common denominator, but since we
    // have to simplify it at the end anyway, we'll just
    // use the product as a guaranteed denominator and just
    // simplify once
    //Because since the multiplication that happens before
    // the simplification can possibly balloon quite large, we do this all in long
    //then reduce
    //This function returns nothing; rather, it takes the answer as the primary object
    // and assigns the appropriate values into it
    protected void arith(Frac a, Frac b, boolean subtract) {
        long aDen = Integer.toUnsignedLong(a.den);
        long bDen = Integer.toUnsignedLong(b.den);
        long bigNum = (subtract) ? (a.num * bDen - b.num * aDen) :
                (a.num * bDen + b.num * aDen);
        long bigDen = aDen * bDen;
        this.reduce(bigNum, bigDen);
    }
    //wrapper for the mult/divide function
    public Frac multiply(Frac oth) {
        Frac ans = new Frac();
        ans.mult(this, oth, false);
        return ans;
    }
    //wrapper for multiplication with an integer
    public Frac multiply(int number) {
        Frac ans = new Frac();
        ans.mult(this, new Frac(number), false);
        return ans;
    }
    //wrapper for division with an integer
    public Frac divide(Frac oth) {
        Frac ans = new Frac();
        ans.mult(this, oth, true);
        return ans;
    }
    //wrapper for division but with an int instead
    public Frac divide(int number) {
        Frac ans = new Frac();
        ans.mult(this, new Frac(number), true);
        return ans;
    }
    //since fraction multiplication and division are so similar
    // (all that changes is that the second fraction is upside down
    // it can be done with the same function with a boolean flag and an if
    //this function also does the checking for division by 0, and it returns the same answer
    //an error in such a case
    //because it returns as error, the actual answer is the primary object
    //create the longs to do the math in, then assign to them the appropriate values from
    //the second fraction.
    //Use the flag to flip if it is division
    //if division, we check for division by 0
    //then multiply the num and den by the first fraction directly
    // and reduce
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
            this.reduce(bigNum, bigDen);
        }
        else {
            fracErrorHandler(error, b.intoMixedNum());
            this.num = a.num;
            this.den = a.den;
        }
    }

    //basically copies, but squares as well
    public Frac squared() {
        Frac ans = new Frac();
        ans.num = this.num * this.num;
        ans.den = this.den * this.den;
        return ans;
    }
    //returns integer power of fractions, including negative power support
    public Frac power(int power) {
        //we already have a function for this
        if (power == 2)
            return this.squared();
        //else
        Frac ans = new Frac();
        //if it is 0, the answer is always 1
        if (power == 0)
            ans.num = 1;
        else {
            boolean invert;
            //if the power is negative, set the invert flag
            // and make the power be the absolute value
            if (power < 0) {
                invert = true;
                power *= -1;
            }
            else
                invert = false;
            //stick them into longs before calculating, this also helps for
            // if we have to invert
            long bigNum = this.num;
            long den = Integer.toUnsignedLong(this.den);
            long bigDen = den;
            //treating exponentiation as chained multiplication
            for (int i = 1; i <= power; i++) {
                bigNum *= this.num;
                bigDen *= den;
            }
            //if inverted, flip them, else put them in the intuitive spot
            if (invert) {
                ans.num = bigDen;
                ans.den = (int)bigNum;
            }
            else {
                ans.num = bigNum;
                ans.den = (int)bigDen;
            }
        }
        return ans;
    }
    //if someone wanted to wreck these nice rationals with
    // transcendental powers, well, here we go
    //turn the fraction to a double (divide num by the den, but the den must be
    // properly longed as it is unsigned
    //then there's the built-in power function
    public double powerApprox(double power) {
        return Math.pow(num / (double)Integer.toUnsignedLong(den), power);
    }
    //this one is for if we want to get our answer back as a fraction
    //essentially, we call the above function for the power, then
    //force that float into a fraction using the given level of precision
    public Frac powerApprox(double power, int decimalPrecisionForApprox) {
        double powered = this.powerApprox(power);
        return new Frac(powered, decimalPrecisionForApprox);
    }
    //this function is the above, but with the power being a rational instead of
    // a double
    //all that changes is turning the rational into a double anyway though
    public Frac powerApprox(Frac power, int decimalPrecisionForApprox) {
        return this.powerApprox(power.intoDouble(), decimalPrecisionForApprox);
    }
}
