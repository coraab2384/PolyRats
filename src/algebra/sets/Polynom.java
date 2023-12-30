package algebra.sets;

public class Polynom {
    //for future planned referencing operations on polynomials by their gui names,
    //which would be stored here
    private String name;
    //the real meat of the thing; if this were python I'd be tempted
    //to just make the polynomial a list with the first part being the name
    private final Frac[] polynom;
    //is this necessary to have, or does <obj>.polynom.length do the same?
    //private final int length;
    //more descriptive than just using int flags
    private enum PolyErrors {
        UnparsablePower,
        FracUnparsableInt,
        FracWrongFracCount,
        FracWrongDecCount,
        FracWrongSymCount,
        FracDivZero,
        None
    }
    private enum DivFlags {
        wBothNewNames,
        wBothNoNames,
        oQuotNewName,
        oQuotNoName
    }
    //constant polynomial zero, for comparisons mostly
    public static final Polynom zero = new Polynom(1, "zero");
    //to determine the zero spacing for the auto-names for the factors
    public static int digitsPolyFactorNames = 3;
    public static int decimalPrecisionForApprox = 3;

    public Polynom() {
        polynom = new Frac[1];
        polynom[0] = new Frac();
    }
    public Polynom(int length) {
        length = Math.max(length, 1);
        polynom = new Frac[length];
        for (int i = 0; i < length; i++)
            polynom[i] = new Frac();
    }
    public Polynom(int length, String name) {
        length = Math.max(length, 1);
        polynom = new Frac[length];
        for (int i = 0; i < length; i++)
            polynom[i] = new Frac();
        this.name = name;
    }
    public Polynom(Frac val, int degree) {
        polynom = monombuild(val, degree);
    }
    public Polynom(int val, int degree) {
        polynom = monombuild(new Frac(val), degree);
    }
    public Polynom(Frac val, int degree, String name) {
        polynom = monombuild(val, degree);
        this.name = name;
    }
    public Polynom(int val, int degree, String name) {
        polynom = monombuild(new Frac(val), degree);
        this.name = name;
    }
    private static Frac[] monombuild(Frac val, int degree) {
        degree = Math.max(degree, 0);
        Frac[] polynom = new Frac[degree + 1];
        polynom[degree--] = val;
        for (int i = 0; i < degree; i++)
            polynom[i] = new Frac();
        return polynom;
    }
    public Polynom(String originalInput) {
        polynom = polyParser(originalInput);
    }
    public Polynom(String originalInput, String name) {
        polynom = polyParser(originalInput);
        this.name = name;
    }
    private static Frac[] polyParser(String originalInput) {
        Frac[] polynom;
        int i, j, termLength;
        PolyErrors errorP = PolyErrors.None;
        Frac.FracErrors errorF = Frac.FracErrors.None;
        String input = originalInput.replaceAll("\\^", "");
        input = input.replaceAll("-", "+-");
        String[] inputS = input.split("\\+");
        int length = inputS.length;
        int[] powers = new int[length];
        String tempS = "0";
        for (j = 0; j < length; j++) {
            inputS[j] = inputS[j].trim();
            termLength = inputS[j].length();
            for (i = termLength - 1; i >= 0; i--)
                if (Character.isLetter(inputS[j].charAt(i))) {
                    tempS = inputS[j].substring(i + 1, termLength);
                    inputS[j] = inputS[j].substring(0, i);
                    break;
            }
            try {
                powers[j] = Integer.parseUnsignedInt(tempS);
            }
            catch (NumberFormatException e) {
                errorP = PolyErrors.UnparsablePower;
            }
        }
        if (errorP != PolyErrors.None) polynom = zero.polynom;
        else {
            int degree = length;
            for (int power : powers)
                if (Integer.compareUnsigned(power, degree) > 0) degree = power;
            polynom = new Frac[degree];
            boolean first;
            for (j = 0; j < degree; j++) {
                first = true;
                for (i = 0; i < length; i++) {
                    if (j == powers[i]) {
                        if (first) {
                            errorF = polynom[j].parseFrac(inputS[i], false);
                            first = false;
                        }
                        else {
                            Frac tempF = new Frac();
                            errorF = tempF.parseFrac(inputS[i], false);
                            polynom[j].arith(polynom[j], tempF, false);
                        }
                    }
                    if (first) polynom[j] = new Frac();
                }
                errorP = switch (errorF) {
                    case DivZero -> PolyErrors.FracDivZero;
                    case WrongSymCount -> PolyErrors.FracWrongSymCount;
                    case WrongDecCount -> PolyErrors.FracWrongDecCount;
                    case WrongFracCount -> PolyErrors.FracWrongFracCount;
                    case UnparsableInt -> PolyErrors.FracUnparsableInt;
                    default -> PolyErrors.None;
                };
            }
        }
        if (errorP != PolyErrors.None) polyError(errorP, originalInput);
        return polynom;
    }
    private static String polyError(PolyErrors error, String input) {
        return "lol";
    }
    public int getLength() {
        return polynom.length;
    }
    public Frac getTerm(int indexFromTop) {
        return polynom[polynom.length - indexFromTop].copy();
    }
    public Frac getTermDeg(int degree) {
        return polynom[degree].copy();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Polynom copy() {
        return copier(false, name);
    }
    public Polynom copy(String name) {
        return copier(false, name);
    }
    public Polynom sliceDesc(int start, int end) {
        return slicer(end, start, name);
    }
    public Polynom sliceDesc(int start, int end, String name) {
        return slicer(end, start, name);
    }
    public Polynom sliceAsc(int start, int end) {
        return slicer(start, end, name);
    }
    public Polynom sliceAsc(int start, int end, String name) {
        return slicer(start, end, name);
    }
    private Polynom slicer(int start, int end, String name) {
        Polynom Ans;
        if (start == end)
            Ans = new Polynom();
        else {
            int newLength = this.polynom.length - end;
            Ans = new Polynom(newLength);
            for (int i = this.polynom.length - start; i < newLength; i++)
                Ans.polynom[i] = this.polynom[i].copy();
        }
        Ans.name = name;
        return Ans;
    }
    public boolean equalsTo(Polynom oth) {
        if (this.polynom.length != oth.polynom.length)
            return false;
        //else
        for (int i = 0; i < this.polynom.length; i++)
            if (Frac.compare(this.polynom[i], oth.polynom[i]) !=
                    Frac.FracComparisons.Equal)
                return false;
        //else
        return true;
    }
    public Polynom negate() {
        return copier(true, name);
    }
    public Polynom negate(String name) {
        return copier(true, name);
    }
    private Polynom copier(boolean negate, String name) {
        Polynom Ans = new Polynom(this.polynom.length);
        for (int i = 0; i < this.polynom.length; i++)
            Ans.polynom[i] = (negate) ?
                    this.polynom[i].negate() :
                    this.polynom[i].copy();
        Ans.name = name;
        return Ans;
    }
    public Polynom add(Polynom oth) {
        return arithP(this, oth, false, name);
    }
    public Polynom add(Frac number, int degree) {
        return arithP(this, new Polynom(number, degree),
                false, name);
    }
    public Polynom add(int number, int degree) {
        return arithP(this, new Polynom(number, degree),
                false, name);
    }
    public Polynom add(Polynom oth, String name) {
        return arithP(this, oth, false, name);
    }
    public Polynom add(Frac number, int degree, String name) {
        return arithP(this, new Polynom(number, degree),
                false, name);
    }
    public Polynom add(int number, int degree, String name) {
        return arithP(this, new Polynom(number, degree),
                false, name);
    }
    public Polynom subtract(Polynom oth) {
        return arithP(this, oth, true, name);
    }
    public Polynom subtract(Frac number, int degree) {
        return arithP(this, new Polynom(number, degree),
                true, name);
    }
    public Polynom subtract(int number, int degree) {
        return arithP(this, new Polynom(number, degree),
                true,name);
    }
    public Polynom subtract(Polynom oth, String name) {
        return arithP(this, oth, true, name);
    }
    public Polynom subtract(Frac number, int degree, String name) {
        return arithP(this, new Polynom(number, degree),
                true, name);
    }
    public Polynom subtract(int number, int degree, String name) {
        return arithP(this, new Polynom(number, degree),
                true, name);
    }
    //we break this up into 3 cases; either the first is bigger, or the seconcd is bigger,
    //    or they are equal sizes.
    //The first is the same, add or subtract
    //The second is only different in subtraction in that
    //    the terms before the subtraction starts are negated
    //The third, we have to look out for cancellation possibilities.
    //This is important because the length of the array must be predeterminded,
    //    but the length is tied to the leading nonzero term
    private static Polynom arithP(Polynom Px, Polynom Qx,
                                  boolean subtract, String name) {
        Polynom Rx;
        if (Px.polynom.length == Qx.polynom.length) {
            int degreeR = Px.polynom.length - 1;
            //so the two are additive inverses when they cancel when subtracted, meaning
            //one is the negation of the other.
            //check if they will cancel to determine the degree
            while ((!subtract && Frac.compare(Px.polynom[degreeR],
                    Qx.polynom[degreeR]) == Frac.FracComparisons.AddInv) ||
                    (subtract && Frac.compare(Px.polynom[degreeR],
                    Qx.polynom[degreeR]) == Frac.FracComparisons.Equal))
                    --degreeR;
            //iterate through the while until finding a nonzero term, knocking
            //down degree each time
            if (degreeR >= 0) {
                //degreeR starts being lengthR here, hence the ++
                Rx = new Polynom(++degreeR);
                for (int i = 0; i < degreeR; i++)
                    Rx.polynom[i].arith(Px.polynom[i], Qx.polynom[i], subtract);
            }
            else Rx = new Polynom();
        }
        else {
            int i;
            if (Px.polynom.length > Qx.polynom.length) {
                //for the overlapping parts, do the operation
                //then copy the parts where the first is longer
                Rx = new Polynom(Px.polynom.length);
                for (i = 0; i < Qx.polynom.length; i++)
                    Rx.polynom[i].arith(Px.polynom[i], Qx.polynom[i], subtract);
                for (i = Qx.polynom.length; i < Px.polynom.length; i++)
                    Rx.polynom[i] = Px.polynom[i].copy();
            }
            else {
                //for the overlapping parts, do the operation
                //then copy the parts where the second is longer,
                //or negate if subtracting
                Rx = new Polynom(Qx.polynom.length);
                for (i = 0; i < Px.polynom.length; i++)
                    Rx.polynom[i].arith(Px.polynom[i], Qx.polynom[i], subtract);
                if (subtract)
                    for (i = Px.polynom.length; i < Qx.polynom.length; i++)
                        Rx.polynom[i] = Px.polynom[i].negate();
                else
                    for (i = Px.polynom.length; i < Qx.polynom.length; i++)
                        Rx.polynom[i] = Px.polynom[i].copy();
            }
        }
        Rx.name = name;
        return Rx;
    }
    //iterates through the polynomial to scale (multiply or divide)
    //each term
    //checks if the scalar is 0 to return 0 first
    private Polynom scalar(Frac scalar, boolean divide, String name) {
        Polynom Ans;
        if (Frac.compare(scalar, Frac.zero) == Frac.FracComparisons.Equal)
            Ans = new Polynom();
        else {
            Ans = new Polynom(this.polynom.length);
            for (int i = 0; i < this.polynom.length; i++)
                Ans.polynom[i].mult(this.polynom[i], scalar, divide);
        }
        Ans.name = name;
        return Ans;
    }
    public Polynom multiply(Polynom oth) {
        return multP(this, oth, name);
    }
    public Polynom multiply(Frac number, int degree) {
        if (degree == 0)
            return scalar(number, false, name);
        //else
        return multP(this, new Polynom(number, degree), name);
    }
    public Polynom multiply(int number, int degree) {
        if (degree == 0)
            return scalar(new Frac(number), false, name);
        //else
        return multP(this, new Polynom(number, degree), name);
    }
    public Polynom multiply(Polynom oth, String name) {
        return multP(this, oth, name);
    }
    public Polynom multiply(Frac number, int degree, String name) {
        if (degree == 0)
            return scalar(number, false, name);
        //else
        return multP(this, new Polynom(number, degree), name);
    }
    public Polynom multiply(int number, int degree, String name) {
        if (degree == 0)
            return scalar(new Frac(number), false, name);
        //else
        return multP(this, new Polynom(number, degree), name);
    }
    //If you look at the source of each term of the Answer, it will come from
    //the diagonals of a box pattern, with one polynomial being length
    //and the other being width
    //this can be done using a length loop, and a column loop, nested
    //prod is the result of one particular 'box', which is then added to the final
    //Answer
    private static Polynom multP(Polynom Px, Polynom Qx, String name) {
        int i, j;
        Frac prod = new Frac();
        Polynom Rx = new Polynom(Px.polynom.length + Qx.polynom.length - 1);
        for (i = 0; i < Px.polynom.length; i++)
            for (j = 0; j < Qx.polynom.length; j++) {
                prod.mult(Px.polynom[i], Qx.polynom[j], false);
                Rx.polynom[i + j].arith(Rx.polynom[i + j], prod, false);
        }
        Rx.name = name;
        return Rx;
    }
    public Polynom[] divide(Polynom oth) {
        return divP(this, oth, DivFlags.wBothNoNames, null);
    }
    public Polynom[] divide(Frac number, int degree) {
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNoNames, null);
    }
    public Polynom[] divide(int number, int degree) {
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNoNames, null);
    }
    public Polynom[] divide(Polynom oth, String[] names) {
        return divP(this, oth, DivFlags.wBothNewNames, names);
    }
    public Polynom[] divide(Frac number, int degree, String[] names) {
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNewNames, names);
    }
    public Polynom[] divide(int number, int degree, String[] names) {
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNewNames, names);
    }
    public Polynom quotient(Polynom oth) {
        return divP(this, oth, DivFlags.oQuotNoName, null)[0];
    }
    public Polynom quotient(Frac number, int degree) {
        if (degree <= 0)
            return scalar(number, true, name);
        //else
        return divP(this, new Polynom(number, degree),
                DivFlags.oQuotNoName, null)[0];
    }
    public Polynom quotient(int number, int degree) {
        if (degree <= 0)
            return scalar(new Frac(number), true, name);
        //else
        return divP(this, new Polynom(number, degree),
                DivFlags.oQuotNoName, null)[0];
    }
    public Polynom quotient(Polynom oth, String name) {
        String[] names = {name};
        return divP(this, oth, DivFlags.oQuotNewName, names)[0];
    }
    public Polynom quotient(Frac number, int degree, String name) {
        if (degree <= 0)
            return scalar(number, true, name);
        //else
        String[] names = {name};
        return divP(this, new Polynom(number, degree),
                DivFlags.oQuotNewName, names)[0];
    }
    public Polynom quotient(int number, int degree, String name) {
        if (degree <= 0)
            return scalar(new Frac(number), true, name);
        //else
        String[] names = {name};
        return divP(this, new Polynom(number, degree),
                DivFlags.oQuotNewName, names)[0];
    }
    public Polynom remainder(Polynom oth) {
        return divP(this, oth, DivFlags.wBothNoNames, null)[1];
    }
    public Polynom remainder(Frac number, int degree) {
        if (degree <= 0)
            return new Polynom(0, name);
        //else
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNoNames, null)[1];
    }
    public Polynom remainder(int number, int degree) {
        if (degree <= 0)
            return new Polynom(0, name);
        //else
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNoNames, null)[1];
    }
    public Polynom remainder(Polynom oth, String name) {
        String[] names = {null, name};
        return divP(this, oth, DivFlags.wBothNewNames, names)[1];
    }
    public Polynom remainder(Frac number, int degree, String name) {
        if (degree <= 0)
            return new Polynom(1, name);
        //else
        String[] names = {null, name};
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNewNames, names)[1];
    }
    public Polynom remainder(int number, int degree, String name) {
        if (degree <= 0)
            return new Polynom(1, name);
        //else
        String[] names = {null, name};
        return divP(this, new Polynom(number, degree),
                DivFlags.wBothNewNames, names)[1];
    }
    private static Polynom[] divP(Polynom Px, Polynom Qx,
                                  DivFlags flag, String[] names) {
        Polynom[] Ans = new Polynom[2];
        if (Qx.polynom.length == 1) {
            Ans[0] = Px.scalar(Qx.polynom[0], true, null);
            Ans[1] = new Polynom();
        }
        else if (Px.polynom.length <= Qx.polynom.length) {
            Ans[0] = new Polynom();
            Ans[1] = Px.copy();
            if (Px.polynom.length == Qx.polynom.length) {
                Ans[0].polynom[0].mult(Px.polynom[Px.polynom.length - 1],
                        Qx.polynom[Qx.polynom.length - 1], true);
                Ans[1] = Qx.scalar(Qx.polynom[Qx.polynom.length - 1],
                        true, null);
            }
        }
        //synthetic division
//        else if (Qx.polynom.length == 2) {
//            Ans[0] = new Polynom(Px.polynom.length - 1);
//            Frac remainder = new Frac();
//            Frac divisor = new Frac();
//            divisor.mult(Qx.polynom[0].negate(), Qx.polynom[1], true);
//            int i = Px.polynom.length;
//            while (i > 0) {
//                remainder.arith(Px.polynom[i--], remainder, false);
//                Ans[0].polynom[i] = remainder.copy();
//                remainder.mult(remainder, divisor, false);
//            }
//            if (flag == DivFlags.wBothNoNames || flag == DivFlags.wBothNewNames) {
//                remainder.arith(Px.polynom[0], remainder, false);
//                Ans[1] = new Polynom(remainder, 0);
//            }
//        }
        else {
            int h, i;
            int j = Px.polynom.length - Qx.polynom.length + 1;
            int lengthR = Qx.polynom.length - 1;
            Ans[0] = new Polynom(j);
            Frac tempTerm = new Frac();
            while (j > 0) {
                h = j--;
                Ans[0].polynom[j] = Px.polynom[lengthR + j].copy();
                i = lengthR;
                while (i > 0 && h < Ans[0].polynom.length) {
                    tempTerm.mult(Qx.polynom[--i], Ans[0].polynom[h++], false);
                    Ans[0].polynom[j].arith(Ans[0].polynom[j], tempTerm, true);
                }
                Ans[0].polynom[j].mult(Ans[0].polynom[j],
                        Qx.polynom[lengthR], true);
            }
            switch (flag) {
                case wBothNewNames, wBothNoNames:
                    boolean first = true;
                    j = lengthR;
                    Frac remain = new Frac();
                    while (j > 0) {
                        h = --j;
                        i = 0;
                        while (h > 0) {
                            if (h-- < Ans[0].polynom.length) {
                                tempTerm.mult(Qx.polynom[i],
                                        Ans[0].polynom[h], false);
                                if (first) {
                                    remain = Px.polynom[j].copy();
                                    remain.arith(remain, tempTerm, true);
                                }
                                else
                                    Ans[1].polynom[j].arith(Ans[1].polynom[j],
                                            tempTerm, true);
                            }
                            i++;
                        }
                        if (first) {
                            if (j == 0)
                                Ans[1] = new Polynom();
                            else {
                                if (Frac.compare(remain, Frac.zero) !=
                                        Frac.FracComparisons.Equal) {
                                    Ans[1] = Px.slicer(0, j, null);
                                    Ans[1].polynom[j] = remain.copy();
                                    first = false;
                                }
                            }
                        }
                    }
            }
        }
        switch (flag) {
            case wBothNewNames:
                Ans[1].name = names[1];
            case oQuotNewName:
                Ans[0].name = names[0];
                break;
            case wBothNoNames:
                Ans[1].name = Px.name;
            case oQuotNoName:
                Ans[0].name = Px.name;
        }
        return Ans;
    }
    public Polynom power(int power) {
        return power(power, name);
    }
    public Polynom power(int power, String name) {
        if (power <= 0)
            return new Polynom(1, 0, name);
        //else
        Polynom ans = this.copy(null);
        while (power > 1) {
            ans = multP(ans, this, name);
            power--;
        }
        return ans;
    }
    private String nameIterator(int index) {
        return name + "_" + String.format(
                ("%0" + digitsPolyFactorNames + "d"), index);
    }
    public java.util.ArrayList<Polynom> rationalFactors() {
        Polynom Wx = this.copy();
        int last = this.polynom.length;
        int curLength = last--;
        java.util.ArrayList<Polynom> ratFactors =
                new java.util.ArrayList<>();
        if (curLength > 1) {
            long longP, denLCM, numGCD, pDen, qDen, l;
            int intQ, posFactorCount, i, j, k;
            int begin = 0;
            int ratFactorCount = 0;
            java.util.ArrayList<Frac> posFactors =
                    new java.util.ArrayList<>();
            int factorsPCount = 1;
            java.util.ArrayList<Long> factorsP =
                    new java.util.ArrayList<>();
            factorsP.add(1L);
            int factorsQCount = 0;
            java.util.ArrayList<Integer> factorsQ =
                    new java.util.ArrayList<>();
            Frac scalar, prospect;
            for (Frac term : Wx.polynom) {
                if (term.getNum() == 0)
                    begin++;
                else break;
            }
            if (begin != 0) {
                ratFactors.add(new Polynom(1, begin,
                        Wx.nameIterator(++ratFactorCount)));
                Wx = slicer(begin, curLength, Wx.name);
                curLength -= begin;
                last -= begin;
            }
            pDen = Integer.toUnsignedLong(Wx.polynom[0].getDen());
            qDen = Integer.toUnsignedLong(Wx.polynom[last].getDen());
            denLCM = pDen * qDen / Frac.GCD(pDen, qDen);
            scalar = (Wx.polynom[last].isNegative()) ?
                    new Frac(-1 * denLCM) :
                    new Frac(denLCM);
            Frac term = new Frac();
            term.mult(Wx.polynom[last], scalar, false);
            numGCD = term.getNum();
            i = last;
            while (i > 0 && numGCD != 1) {
                term.mult(Wx.polynom[--i], scalar, false);
                numGCD = Frac.GCD(numGCD, Math.abs(term.getNum()));
            }
            if (numGCD != denLCM) {
                scalar.mult(new Frac(numGCD), scalar, true);
                Wx = Wx.scalar(scalar, true, Wx.name);
                if (begin != 0)
                    ratFactors.get(0).polynom[begin] = scalar;
                else ratFactors.add(new Polynom(scalar, 0,
                        Wx.nameIterator(++ratFactorCount)));
            }
            longP = Math.abs(Wx.polynom[0].getNum());
            intQ = (int)Math.abs(Wx.polynom[last].getNum());
            l = 1;
            if (longP != 1) {
                while (l * l < longP) {
                    if (longP % ++l == 0) {
                        factorsP.add(l);
                        factorsPCount++;
                    }
                }
                for (i = factorsPCount - 1; i > 0; i--) {
                    l = factorsP.get(i);
                    if (l * l != longP) {
                        factorsP.add(longP / l);
                        factorsPCount++;
                    }
                }
                factorsP.add(longP);
                factorsPCount++;
            }
            if (Integer.compareUnsigned(intQ, 1) > 0) {
                i = 1;
                while (Integer.compareUnsigned(i * i, intQ) < 0) {
                    if (Integer.remainderUnsigned(intQ, ++i) == 0) {
                        factorsQ.add(i);
                        factorsQCount++;
                        if (Integer.compareUnsigned(i * i, intQ) != 0) {
                            factorsQ.add(Integer.divideUnsigned(intQ, i));
                            factorsQCount++;
                        }
                    }
                }
                factorsQ.add(intQ);
                factorsQCount++;
            }
            posFactorCount = factorsPCount * 2;
            for (i = 0; i < factorsPCount; i++) {
                posFactors.add(new Frac(factorsP.get(i)));
                for (j = 0; j < factorsQCount; j++) {
                    prospect = new Frac(factorsP.get(i), factorsQ.get(j));
                    for (k = 0; k < posFactorCount; k++) {
                        Frac.FracComparisons compResult =
                                Frac.compare(posFactors.get(k), prospect);
                        if (compResult == Frac.FracComparisons.LessThan) {
                            posFactors.add(k - 1, prospect);
                            posFactorCount += 2;
                            break;
                        }
                        //else
                        if (compResult == Frac.FracComparisons.Equal)
                            break;
                    }
                }
            }
            for (i = posFactorCount / 2 - 1; i >= 0; i--)
                posFactors.add(posFactors.get(i).negate());
            Polynom tryDivide = new Polynom(Frac.zero, 1);
            i = 0;
            while (curLength > 2 && i < posFactorCount) {
                tryDivide.polynom[0] = new Frac(posFactors.get(i).getNum());
                tryDivide.polynom[1] = new Frac(posFactors.get(i).getDen());
                Polynom[] divResults = divP(this, tryDivide,
                        DivFlags.wBothNoNames, null);
                if (divResults[1].equalsTo(zero)) {
                    Wx = divResults[0].copy();
                    ratFactors.add(tryDivide.copy(Wx.nameIterator(++ratFactorCount)));
                    curLength--;
                }
                else ++i;
            }
            Wx.name = Wx.nameIterator(++ratFactorCount);
            ratFactors.add(Wx);
        }
        else {
            Wx.name += ", which is a constant";
            ratFactors.add(Wx);
        }
        return ratFactors;
    }
    public Polynom[] quadraticEquationApprox() {
        Polynom[] factors;
        if (this.polynom.length == 3) {
            int i;
            java.util.ArrayList<Polynom> ratFactors = this.rationalFactors();
            int ratFactorLength = ratFactors.size();
            Frac scalar;
            boolean isScaled;
            if (ratFactors.get(0).polynom.length == 1) {
                isScaled = true;
                scalar = ratFactors.get(0).polynom[0];
            }
            else {
                isScaled = false;
                scalar = new Frac();
            }
            if (ratFactorLength == 3 || (ratFactorLength == 2 && !isScaled)) {
                factors = new Polynom[ratFactorLength];
                for (i = 0; i < ratFactorLength; i++)
                    factors[i] = ratFactors.get(i);
                return factors;
            }
            //else
            Frac determinant = new Frac(4);
            determinant.mult(this.polynom[2], determinant, false);
            determinant.mult(this.polynom[0], determinant, false);
            determinant.arith(this.polynom[1].squared(), determinant, true);
            if (determinant.isPositive()) {
                scalar = this.polynom[2];
                determinant.mult(determinant, new Frac(4), true);
                Frac scaledB = new Frac(-1, 2);
                scaledB.mult(this.polynom[1], scaledB, false);
                if (Frac.compare(scalar, new Frac(1)) == Frac.FracComparisons.Equal) {
                    isScaled = false;
                    factors = new Polynom[2];
                    i = 0;
                }
                else {
                    isScaled = true;
                    scaledB.mult(scaledB, scalar, true);
                    determinant.mult(determinant, scalar.squared(), true);
                    factors = new Polynom[3];
                    factors[0] = new Polynom(scalar, 0, this.nameIterator(1));
                    i = 1;
                }
                double b = scaledB.intoDouble();
                double rootTwo = determinant.powerApprox(0.5);
                double rootOne = b - rootTwo;
                rootTwo += b;
                while (i < factors.length) {
                    factors[i] = new Polynom(new Frac(1), 1,
                            this.nameIterator(i + 1));
                    factors[i].polynom[0] = new Frac(
                            ((i == factors.length - 1) ? rootTwo : rootOne),
                            decimalPrecisionForApprox);
                    i++;
                }
                return factors;
            }
            //else
            factors = new Polynom[ratFactorLength];
            for (i = 0; i < ratFactorLength; i++)
                factors[i] = ratFactors.get(i);
            return factors;
        }
        //else
        factors = new Polynom[1];
        factors[0] = this.copy(this.name + ", which is not a quadratic");
        return factors;
    }
}
