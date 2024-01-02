package algebra.sets;

import java.util.Collections;

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
    public static int getCurApproxPrec() {
        return decimalPrecisionForApprox;
    }
    public static String setCurApproxPrec(int val) {
        if (val < 0)
            return "too low";
        //else
        if (val > 9)
            return "too high";
        //else
        decimalPrecisionForApprox = val;
        return "accepted";
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
        int i;
        int lengthR = Math.max(Px.polynom.length, Qx.polynom.length);
        if (Px.polynom.length == Qx.polynom.length) {
            //so the two are additive inverses when they cancel when subtracted, meaning
            //one is the negation of the other.
            //check if they will cancel to determine the degree
            Frac.FracComparisons compResult;
            do {
                --lengthR;
                compResult = Frac.compare(Px.polynom[lengthR], Qx.polynom[lengthR]);
            } while (lengthR > 0 && (
                    (!subtract && compResult == Frac.FracComparisons.AddInv) ||
                    (subtract && compResult == Frac.FracComparisons.Equal)));
            //iterate through the while until finding a nonzero term, knocking
            //down degree each time
            //the first knockdown is the freebee for going from length to degree
            if (lengthR > 0) {
                //lengthR starts being lengthR here again, hence the ++
                Rx = new Polynom(++lengthR);
                for (i = 0; i < lengthR; i++)
                    Rx.polynom[i].arith(Px.polynom[i], Qx.polynom[i], subtract);
            }
            else Rx = new Polynom();
        }
        else {
            Rx = new Polynom(lengthR);
            int shortlength = Math.min(Px.polynom.length, Qx.polynom.length);
            //for the overlapping parts, do the operation
            for (i = 0; i < shortlength; i++)
                Rx.polynom[i].arith(Px.polynom[i], Qx.polynom[i], subtract);
            if (lengthR > Qx.polynom.length)
                //then copy the parts where the first is longer
                for (i = shortlength; i < lengthR; i++)
                    Rx.polynom[i] = Px.polynom[i].copy();
            else if (subtract)
                //then copy the parts where the second is longer,
                for (i = shortlength; i < lengthR; i++)
                    Rx.polynom[i] = Qx.polynom[i].negate();
            else   //or negate if subtracting
                for (i = shortlength; i < lengthR; i++)
                    Rx.polynom[i] = Qx.polynom[i].copy();
        }
        Rx.name = name;
        return Rx;
    }
    //iterates through the polynomial to scale (multiply or divide)
    //each term
    //checks if the scalar is 0 to return 0 first
    private Polynom scalar(Frac scalar, boolean divide, String name) {
        Polynom Ans;
        if (scalar.getNum() == 0)
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
            return this.scalar(number, false, name);
        //else
        return multP(this, new Polynom(number, degree), name);
    }
    public Polynom multiply(int number, int degree) {
        if (degree == 0)
            return this.scalar(new Frac(number), false, name);
        //else
        return multP(this, new Polynom(number, degree), name);
    }
    public Polynom multiply(Polynom oth, String name) {
        return multP(this, oth, name);
    }
    public Polynom multiply(Frac number, int degree, String name) {
        if (degree == 0)
            return this.scalar(number, false, name);
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
    // the diagonals of a box pattern, with one polynomial being length
    // and the other being width
    //this can be done using a length loop, and a column loop, nested
    //prod is the result of one particular 'box', which is then added to the final
    // answer
    private static Polynom multP(Polynom Px, Polynom Qx, String name) {
        int i, j;
        Frac prod = new Frac();
        Polynom Rx = new Polynom(Px.polynom.length + Qx.polynom.length - 1);
        for (i = 0; i < Px.polynom.length; i++)
            for (j = 0; j < Qx.polynom.length; j++) {
                //find the term for this row/column pair, then add it based on
                // its diagonal (i + j) to the answer
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
            return this.scalar(number, true, name);
        //else
        return divP(this, new Polynom(number, degree),
                DivFlags.oQuotNoName, null)[0];
    }
    public Polynom quotient(int number, int degree) {
        if (degree <= 0)
            return this.scalar(new Frac(number), true, name);
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
            return this.scalar(number, true, name);
        //else
        String[] names = {name};
        return divP(this, new Polynom(number, degree),
                DivFlags.oQuotNewName, names)[0];
    }
    public Polynom quotient(int number, int degree, String name) {
        if (degree <= 0)
            return this.scalar(new Frac(number), true, name);
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
    //it tooks some work to figure out the source of each of the terms
    // in both the quotient and the remainder
    //this function returns an array of the two, with the first being
    // the quotient and the second the remainder
    //the flag holds information on if we actually want the remainder
    // (otherwise we can save time)
    // and on if there are names in the names array to assign
    //we deal with some edge cases first
    //then synthetic,
    //and finally full long division
    private static Polynom[] divP(Polynom Px, Polynom Qx,
                                  DivFlags flag, String[] names) {
        Polynom[] Ans = new Polynom[2];
        //if Qx, our divisor, is a constant, this is just a scaling
        //with no (or empty) remainder
        if (Qx.polynom.length == 1) {
            Ans[0] = Px.scalar(Qx.polynom[0], true, null);
            Ans[1] = new Polynom();
        }
        //if the divisor is larger, then the remainder is just the numerator
        //and the quotient is 0
        else if (Px.polynom.length <= Qx.polynom.length) {
            Ans[0] = new Polynom();
            if (Px.polynom.length != Qx.polynom.length)
                Ans[1] = Px.copy();
            //but, if they are the same size, the quotient will be a number instead
            //we'll overwrite the quotient with the quotient of the leading terms,
            //and the remainder will be a scaled version of the numerator
            else {
                Ans[0].polynom[0].mult(Px.polynom[Px.polynom.length - 1],
                        Qx.polynom[Qx.polynom.length - 1], true);
                Ans[1] = Px.scalar(Qx.polynom[Qx.polynom.length - 1],
                        true, null);
            }
        }
        //synthetic division
        // gets its own optimized case because of how heavily it is used in factoring
        else if (Qx.polynom.length == 2) {
            //answer is one degree lower
            //i will be used to iterate down, so we're giving it a value now while
            // we have that value handy
            int i = Px.polynom.length - 1;
            Ans[0] = new Polynom(i);
            //the remainder is going to be the value that is added each time
            // to Px
            //in synthetic division, this is the value on the second line
            Frac remainder = new Frac();
            //the divisor is what we multiply in each diagonal step in synthetic division
            // which is the negation of the constant divided by the divisor's coeff.
            Frac divisor = new Frac();
            divisor.mult(Qx.polynom[0].negate(), Qx.polynom[1], true);
            //iterate through Px
            while (i > 0) {
                //the index of the terms is off by one between Px and the quotient
                //that's what the i-- accounts for, with the added bonus of also
                //deincrementing our loop variable too!
                remainder.arith(Px.polynom[i--], remainder, false);
                //first we add the remainder to Px--the remainder starts out as 0
                //but in the first line, it should be 0
                Ans[0].polynom[i] = remainder.copy();
                //now we do the diagonal step for in preparation for the next round
                remainder.mult(remainder, divisor, false);
            }
            //The flag could be called to check if the remainder is actually wanted
            //but this is just two lines, I don't think its worth it
            //especially since for factorization, the most common case for division,
            //we do in fact want the remainder
            //so we do the addition for Px's constant column, which will be given
            // to the remainder instead of the quotient as the quotient is done
            remainder.arith(Px.polynom[0], remainder, false);
            Ans[1] = new Polynom(remainder, 0);
        }
        //Polynomial long division, Yay!
        else {
            //h will be tracking if we are in the space belonging to our
            // answer, or to the remainder
            //h belongs to the answer
            //i belongs to the remainder
            int h, i;
            //j indicates which term in the quotient we are working on
            //the other two are for tracking the components from the two inputs
            //the length of the quotient is the difference of the two lengths, plus 1
            // since j starts at the length, lets assign that now
            int j = Px.polynom.length - Qx.polynom.length + 1;
            Ans[0] = new Polynom(j);
            //the remainder must be at least one degree less than the divisor
            //actually it will be two less, but we need to still start with the first
            int lengthR = Qx.polynom.length - 1;
            //tempTerm will hold the part that we subtract by each time round
            Frac tempTerm = new Frac();
            //so we iterate, starting from the highest degree, down through the terms
            //that will make up our quotient
            while (j > 0) {
                //currently j is the lenght, but we want to use it for the degree
                //we'll also want to start h at the length
                //so copy the length to h, then make j the degree (subtract 1)
                h = j--;
                //lengghtR might not be how long the remainder actually is, but it is
                // rather how much space we must set aside for it
                //it is the offset between degrees for corresponding terms
                // between Px and the quotient
                //we start by copying the corresponding Px into the quotient
                // so we can work on it
                Ans[0].polynom[j] = Px.polynom[lengthR + j].copy();
                i = lengthR;
                //instead of doing this row by row, we are doing it column by column
                //for each column, depending on the length of the divisor, and where
                // we are in the problem, we much subtract a tempTerm up to
                // Qx length times, but fewer near the beginning
                //that's the reason for the two conditions
                //i is tracking Qx, and will run length of Qx times if allowed
                //but h is looking at where we are in the quotient, to see if
                //we're near the beginning and should only do:
                //    once for first round, second for second,
                //    and so on until we reach the length of Qx
                //    this is because h is based off of j, where we are in the quotient
                //    right now
                while (i > 0 && h < Ans[0].polynom.length) {
                    //i is the lenght of R, but we actually want the corresponding degree
                    //so we deincrement i befoe it is used
                    //but h is already appropriate, so we increment it afterwards
                    tempTerm.mult(Qx.polynom[--i], Ans[0].polynom[h++], false);
                    Ans[0].polynom[j].arith(Ans[0].polynom[j], tempTerm, true);
                }
                //here, lengthR is being used for the degree of Q, since they are equal
                Ans[0].polynom[j].mult(Ans[0].polynom[j],
                        Qx.polynom[lengthR], true);
            }
            //only need to finish the remainder if we actually want it
            switch (flag) {
                case wBothNewNames, wBothNoNames:
                    //we need to do this stuff with first because while the remainder
                    // could be up to lengthR in length, it could be less if the division
                    // is cleaner than the possible worst case
                    // so we need to check if this term is nonzero
                    // before making it be the highest-degree term of the remainder
                    boolean first = true;
                    //this process mirrors the quotient finding process otherwise
                    j = lengthR;
                    //we just can't build the remainder until we know how long it is
                    //so we store its answer here until we know we can build it
                    Frac remain = new Frac();
                    while (j > 0) {
                        h = --j;
                        i = 0;
                        while (h > 0) {
                            //the check for i is no longer necessary,
                            //since by the time we get to the remaidner we
                            // must have already gotten to the length of Q
                            if (h-- < Ans[0].polynom.length) {
                                tempTerm.mult(Qx.polynom[i],
                                        Ans[0].polynom[h], false);
                                //these two are essentially the same, it
                                // is simply a difference of where the answer is stored
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
                        //we check if we can build our remaidner now
                        if (first) {
                            //well we reached the end with no remainder
                            //remainder is 0
                            if (j == 0)
                                Ans[1] = new Polynom();
                            else {
                                if (remain.getNum() != 0) {
                                    //if we can build the remainder, it starts out
                                    // as a copy of the slice of Px
                                    // that has the same power or less
                                    Ans[1] = Px.slicer(0, j, null);
                                    //we already found the highest term, copy it in
                                    Ans[1].polynom[j] = remain.copy();
                                    first = false;
                                }
                            }
                        }
                    }
            }
        }
        //dealing with naming
        switch (flag) {
            //want to name the remainder
            //well actually, want to name both, but the lack
            // of a break means we do the quotient part below as well
            case wBothNewNames:
                Ans[1].name = names[1];
            //want to name the quotient
            case oQuotNewName:
                Ans[0].name = names[0];
                break;
            //if we don't give names, we're going to copy the name of the
            // top polynomial, Px
            case wBothNoNames:
                Ans[1].name = Px.name;
            case oQuotNoName:
                Ans[0].name = Px.name;
        }
        return Ans;
    }
    public Polynom power(int power) {
        return this.power(power, name);
    }
    //this is the real power function
    //but it does it by just multiplying the polynomial
    //by itself int power times
    public Polynom power(int power, String name) {
        //anything with a power of 0 is 1
        //powers less than 0 will be assumed to be errors, and be treated like 0
        if (power <= 0)
            return new Polynom(1, 0, name);
        //else
        //copy
        //since the multP function is overwriting the answer,
        //not modifying it, we can change the length as we go
        Polynom ans = this.copy(name);
        while (power > 1) {
            ans = multP(ans, this, name);
            power--;
        }
        return ans;
    }
    //this just exists to deal with the name formatting for
    //the factoring functions
    private String nameIterator(int index) {
        return name + "_" + String.format(
                ("%0" + digitsPolyFactorNames + "d"), index);
    }
    //This uses the rational factorizationg "P's and Q's" method to
    // find possible rational factors as the quotients of possible P's over posisble
    // Q's
    //P represents factors of the constant term
    // and Q represents factors of the leading term coefficient
    public Polynom[] rationalFactors() {
        //copy the thing we are trying to factor to Wx, since
        // it will be shrinking as we find factors
        Polynom Wx = this.copy();
        //variable for the last term of Wx
        //it may be used to reassign Wx and must exist outside of it
        int curLast = this.polynom.length - 1;
        //number of rational factors, currently 0
        int ratFactorCount = 0;
        //arraylist for rational factors
        java.util.ArrayList<Polynom> ratFactors =
                new java.util.ArrayList<>();
        int i;
        if (curLast > 1) {
            //we're setting these up now because of how many of these
            //might be first assigned inside a loop but that we will want to
            // access later
            long longP, denLCM, numGCD, pDen, qDen;
            long termnum, termden, l;
            int intQ, posFactorCount, j, k;
            //this one, begin, is used to see how many terms of 0 are between
            // the lowest nonzero term and the constant
            int begin = 0;
            //arraylist for the possibly P/Q quotients
            //posFactorCount tracks its length
            java.util.ArrayList<Frac> posFactors =
                    new java.util.ArrayList<>();
            //arraylist and counter for factors for P
            //we start with 1 as a freebee;
            int factorsPCount = 1;
            java.util.ArrayList<Long> factorsP =
                    new java.util.ArrayList<>();
            factorsP.add(1L);
            //arraylist for possible factors for Q
            //we also sort of have 1 as a freebee, but we don't actually
            // use 1 because anything divided by 1 is itself
            //so we won't be including 1
            int factorsQCount = 0;
            java.util.ArrayList<Integer> factorsQ =
                    new java.util.ArrayList<>();
            //scalar will be used to make the first and last term be an integer,
            // in order for the idea of P's and Q's to work
            //scalar will also be used to factor out possible integer multiples
            //prospect will be a prospective rational factor to check
            Frac scalar, prospect;
            //this is used in checking the prospective factor, to see if it is a repeat
            Frac.FracComparisons compResult;
            //so first we check to make sure our constant term isn't 0
            // if it is, we will knock everything down and check again until
            // we have an appropriate constant term
            for (Frac term : Wx.polynom) {
                if (term.getNum() == 0)
                    begin++;
                else break;
            }
            //this is where we actually knock Wx down
            //essentially we are dividing by x^begin
            //but this will be way faster than calling the division function
            //since in this case we just copy the terms but to a lower index,
            // a lower degree
            if (begin != 0) {
                //we also add x^begin to our factors list
                ratFactors.add(new Polynom(1, begin,
                        Wx.nameIterator(++ratFactorCount)));
                Wx = slicer(begin, curLast + 1, Wx.name);
                curLast -= begin;
            }
            //stuff the denominators of the values in the place of P and Q
            // into longs, because of them being unsigned ints
            pDen = Integer.toUnsignedLong(Wx.polynom[0].getDen());
            qDen = Integer.toUnsignedLong(Wx.polynom[curLast].getDen());
            //the LCM is the product divided by the GCD
            denLCM = pDen * qDen / Frac.GCD(pDen, qDen);
            //while we're scaling things, lets make the polynomial positive
            scalar = (Wx.polynom[curLast].isNegative()) ?
                    new Frac(-1 * denLCM) :
                    new Frac(denLCM);
            //we could scale the whole polynomial now, but we might need to do so again
            // later
            //so we'll scale each term as we check it, but not in the original, yet
            //instead, term will hold the scaled term
            Frac term = new Frac();
            term.mult(Wx.polynom[curLast], scalar, false);
            numGCD = term.getNum();
            i = curLast;
            //i is currently the last term, but we already have that one
            //so we deincrement i before using
            //we can also stop if the gcd becomes 1
            while (i > 0 && numGCD != 1) {
                //scalaing first
                term.mult(Wx.polynom[--i], scalar, false);
                numGCD = Frac.GCD(numGCD, Math.abs(term.getNum()));
            }
            //if this is true, then there actually is something to scale
            if (numGCD != denLCM) {
                //scalar had been a number that we multiply by to remove some denominators
                //but now scalar will be the fraction that we factored out
                //so the new scalar is the numGCD divided by the old scalar
                scalar.mult(new Frac(numGCD), scalar, true);
                //and now we actually scale the polynomial
                Wx = Wx.scalar(scalar, true, Wx.name);
                //the reason to just scale the thing once is because of
                //where to put the scalar
                //if we scaled twice, numerator and denominator seperately,
                // it would be harder to figure out what to put in the rationalfactors
                //arraylist
                if (begin != 0)
                    //if begin is not 0, then begin is the power
                    // of the x's factored out
                    //it would currently be one, but now it will be scalar
                    ratFactors.get(0).polynom[begin] = scalar;
                else
                    ratFactors.add(new Polynom(scalar, 0,
                        Wx.nameIterator(++ratFactorCount)));
            }
            //now we assign P and Q, the large ones
            longP = Math.abs(Wx.polynom[0].getNum());
            intQ = (int)Wx.polynom[curLast].getNum();
            //l is an counter like i, but long
            //it is also negative, for reasons relating to the ordering
            l = 1;
            //if P = 1, then we already have that one and there's no others
            //the ordering might seem a bit weird
            //but this way factorP is already sorted!
            if (longP != 1) {
                //so now we iterate up from -1 until l^2 is or exceeds P
                while (l * l < longP) {
                    //increment here, we want to start our division with 2, not 1
                    if (longP % ++l == 0) {
                        factorsP.add(l);
                        factorsPCount++;
                    }
                }
                //now lets get the other member of each factor pair
                //lets mark the current last index
                j = factorsPCount - 1;
                for (i = j; i > 0; i--) {
                    l = factorsP.get(i);
                    //if l is the root, then we already have it, so don't add it
                    //we only need to check this the first time
                    //hence i != j
                    if (i != j || l * l != longP) {
                        factorsP.add(longP / l);
                        factorsPCount++;
                    }
                }
                //we need to add P itself
                factorsP.add(-1 * longP);
                factorsPCount++;
            }
            //if intQ == 1 then there's no point
            if (intQ != 1) {
                //the middle condition is (i^2 < intQ), but accounting for unsigned
                for (i = 2; Integer.compareUnsigned(i * i, intQ) < 0; i++) {
                    //if (intQ % i == 0) but again, unsigned ints in java
                    if (Integer.remainderUnsigned(intQ, i) == 0) {
                        factorsQ.add(i);
                        factorsQ.add(Integer.divideUnsigned(intQ, i));
                        factorsQCount += 2;
                    }
                }
                //must check the square root separately, since it has no separate
                // quotient to also add
                if (i * i == intQ) {
                    factorsQ.add(i);
                    factorsQCount++;
                }
                factorsQ.add(intQ);
                factorsQCount++;
            }
            //we have twice as many possible factors as there are in P, at least
            //because there's also all the negative possibilities
            posFactorCount = factorsPCount;
            posFactors.ensureCapacity(posFactorCount * 2);
            //factorsP is in ascending order
            //by adding it backwards, this makes posFactors be descending
            //if it is descending, it will be easier to copy
            // the negatives later
            for (i = posFactorCount - 1; i >= 0; i--)
                posFactors.add(new Frac(factorsP.get(i)));
            //now we need to deal with each possible factor P
            //and then each possible divisor
            for (i = 0; i < factorsPCount; i++) {
                for (j = 0; j < factorsQCount; j++) {
                    //this is our prospective factor
                    prospect = new Frac(factorsP.get(i), factorsQ.get(j));
                    //we need to put it in place, and also see if we have a copy already
                    for (k = posFactorCount - 1; k >= 0; k--) {
                        //holds the result of the comparison
                        compResult = Frac.compare(posFactors.get(k), prospect);
                        //if it is greater than, then we've gone to far
                        //it being the factor in the list already
                        // remember, the list is in descending order
                        if (compResult == Frac.FracComparisons.GreaterThan) {
                            posFactors.add(k + 1, prospect);
                            posFactorCount++;
                            break;
                        }
                        //else
                        //if we already have it, break
                        if (compResult == Frac.FracComparisons.Equal)
                            break;
                    }
                }
            }
            //now to copy but negate, so we have negative and positive
            //do it backwards to maintain order
            posFactorCount *= 2;
            posFactors.ensureCapacity(posFactorCount);
            for (i = posFactorCount / 2 - 1; i >= 0; i--)
                posFactors.add(posFactors.get(i).negate());
            //just initializing it, both terms will be reassigned in the loop
            Polynom tryDivide = new Polynom(Frac.zero, 1);
            i = 0;
            //while the degree is greater than 1 and we haven't tried all the factors yet
            while (curLast > 1 && i < posFactorCount) {
                //get the factor in question;
                term = posFactors.get(i);
                termnum = term.getNum();
                termden = Integer.toUnsignedLong(term.getDen());
                //make sure it is still valid
                //not an issue the first round, but in later rounds
                // we lose possible factors
                // however, we don't want to rebuild the list because then
                // we will lose which ones have already been tried
                if (Wx.polynom[0].getNum() % termnum == 0 &&
                        Wx.polynom[curLast].getNum() % termden == 0) {
                    //set the binomial to the appropriate values
                    tryDivide.polynom[0] = new Frac(termnum);
                    tryDivide.polynom[1] = new Frac(termden);
                    Polynom[] divResults = divP(this, tryDivide,
                            DivFlags.wBothNoNames, null);
                    //if the remainder is 0
                    //Wx is the new quotient
                    //add the factor that worked to the arraylist
                    if (divResults[1].polynom[0].getNum() == 0) {
                        Wx = divResults[0].copy();
                        ratFactors.add(tryDivide.copy(Wx.nameIterator(++ratFactorCount)));
                        curLast--;
                    }
                }
                //we do this after the first if, not the second
                // because factors can be there multiple times
                else ++i;
            }
            //add what is left of Wx to the list
            Wx.name = Wx.nameIterator(++ratFactorCount);
            ratFactors.add(Wx);
        }
        else {
            Wx.name += ", which is a constant";
            ratFactors.add(Wx);
            ratFactorCount = 1;
        }
        //stick the arraylist into a normal array to hand it off
        Polynom[] factors = new Polynom[ratFactorCount];
        for (i = 0; i < ratFactorCount; i++)
            factors[i] = ratFactors.get(i);
        return factors;
    }
    public Polynom[] quadraticEquationApprox() {
        if (this.polynom.length == 3) {
            int i;
            Polynom[] ratFactors = this.rationalFactors();
            Frac scalar;
            boolean isScaled;
            if (ratFactors[0].polynom.length == 1) {
                isScaled = true;
            }
            else {
                isScaled = false;
            }
            if (ratFactors.length == 3 || (ratFactors.length == 2 && !isScaled)) {
                return ratFactors;
            }
            //else
            Frac determinant = new Frac(4);
            determinant.mult(this.polynom[2], determinant, false);
            determinant.mult(this.polynom[0], determinant, false);
            determinant.arith(this.polynom[1].squared(), determinant, true);
            if (determinant.isPositive()) {
                Polynom[] factors;
                scalar = this.polynom[2];
                determinant.mult(determinant, new Frac(4), true);
                Frac scaledB = new Frac(-1, 2);
                scaledB.mult(this.polynom[1], scaledB, false);
                if (Frac.compare(scalar, new Frac(1)) == Frac.FracComparisons.Equal) {
                    factors = new Polynom[2];
                    i = 0;
                }
                else {
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
            return ratFactors;
        }
        //else
        Polynom[] factors = new Polynom[1];
        factors[0] = this.copy(this.name + ", which is not a quadratic");
        return factors;
    }
}
