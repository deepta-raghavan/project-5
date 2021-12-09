/**
 * HeftyInteger for CS1501 Project 5
 *
 * @author Dr. Farnan
 */
package cs1501_p5;

import java.util.Random;

public class HeftyInteger {

    private final byte[] ONE = {(byte) 1};

    private byte[] val;

    /**
     * Construct the HeftyInteger from a given byte array
     * @param b the byte array that this HeftyInteger should represent
     */
    public HeftyInteger(byte[] b) {
        val = b;
    }

    /**
     * Return this HeftyInteger's val
     * @return val
     */
    public byte[] getVal() {
        return val;
    }

    /**
     * Return the number of bytes in val
     * @return length of the val byte array
     */
    public int length() {
        return val.length;
    }

    /**
     * Add a new byte as the most significant in this
     * @param extension the byte to place as most significant
     */
    public void extend(byte extension) {
        byte[] newv = new byte[val.length + 1];
        newv[0] = extension;
        for (int i = 0; i < val.length; i++) {
            newv[i + 1] = val[i];
        }
        val = newv;
    }

    /**
     * If this is negative, most significant bit will be 1 meaning most
     * significant byte will be a negative signed number
     * @return true if this is negative, false if positive
     */
    public boolean isNegative() {
        return (val[0] < 0);
    }

    /**
     * Computes the sum of this and other
     * @param other the other HeftyInteger to sum with this
     */
    public HeftyInteger add(HeftyInteger other) {
        byte[] a, b;
        // If operands are of different sizes, put larger first ...
        if (val.length < other.length()) {
            a = other.getVal();
            b = val;
        } else {
            a = val;
            b = other.getVal();
        }

        // ... and normalize size for convenience
        if (b.length < a.length) {
            int diff = a.length - b.length;

            byte pad = (byte) 0;
            if (b[0] < 0) {
                pad = (byte) 0xFF;
            }

            byte[] newb = new byte[a.length];
            for (int i = 0; i < diff; i++) {
                newb[i] = pad;
            }

            for (int i = 0; i < b.length; i++) {
                newb[i + diff] = b[i];
            }

            b = newb;
        }

        // Actually compute the add
        int carry = 0;
        byte[] res = new byte[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            // Be sure to bitmask so that cast of negative bytes does not
            //  introduce spurious 1 bits into result of cast
            carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

            // Assign to next byte
            res[i] = (byte) (carry & 0xFF);

            // Carry remainder over to next byte (always want to shift in 0s)
            carry = carry >>> 8;
        }

        HeftyInteger res_li = new HeftyInteger(res);

        // If both operands are positive, magnitude could increase as a result
        //  of addition
        if (!this.isNegative() && !other.isNegative()) {
            // If we have either a leftover carry value or we used the last
            //  bit in the most significant byte, we need to extend the result
            if (res_li.isNegative()) {
                res_li.extend((byte) carry);
            }
        }
        // Magnitude could also increase if both operands are negative
        else if (this.isNegative() && other.isNegative()) {
            if (!res_li.isNegative()) {
                res_li.extend((byte) 0xFF);
            }
        }

        // Note that result will always be the same size as biggest input
        //  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
        return res_li;
    }

    /**
     * Negate val using two's complement representation
     * @return negation of this
     */
    public HeftyInteger negate() {
        byte[] neg = new byte[val.length];
        int offset = 0;

        // Check to ensure we can represent negation in same length
        //  (e.g., -128 can be represented in 8 bits using two's
        //  complement, +128 requires 9)
        if (val[0] == (byte) 0x80) { // 0x80 is 10000000
            boolean needs_ex = true;
            for (int i = 1; i < val.length; i++) {
                if (val[i] != (byte) 0) {
                    needs_ex = false;
                    break;
                }
            }
            // if first byte is 0x80 and all others are 0, must extend
            if (needs_ex) {
                neg = new byte[val.length + 1];
                neg[0] = (byte) 0;
                offset = 1;
            }
        }

        // flip all bits
        for (int i = 0; i < val.length; i++) {
            neg[i + offset] = (byte) ~val[i];
        }

        HeftyInteger neg_li = new HeftyInteger(neg);

        // add 1 to complete two's complement negation
        return neg_li.add(new HeftyInteger(ONE));
    }

    /**
     * Implement subtraction as simply negation and addition
     * @param other HeftyInteger to subtract from this
     * @return difference of this and other
     */
    public HeftyInteger subtract(HeftyInteger other) {
        return this.add(other.negate());
    }

    /**
     * Compute the product of this and other
     * @param other HeftyInteger to multiply by this
     * @return product of this and other
     */
    public HeftyInteger multiply(HeftyInteger other) {
        HeftyInteger hefty1;
        HeftyInteger hefty2;
        byte[] ZERO = {(byte) 0};

        if (this.isZero() || other.isZero()) return new HeftyInteger(ZERO);
        if (val.length < other.length()) {
            hefty1 = other;
            hefty2 = this;
        } else {
            hefty1 = this;
            hefty2 = other;
        }

        boolean is_neg = (hefty1.isNegative() && !hefty2.isNegative()) || (!hefty1.isNegative() && hefty2.isNegative());

        if (hefty1.isNegative() && hefty2.isNegative()) {
            hefty1 = hefty1.negate();
            hefty2 = hefty2.negate();
        } else if (hefty1.isNegative() && !hefty2.isNegative()) {
            hefty1 = hefty1.negate();
        } else if (!hefty1.isNegative() && hefty2.isNegative()) {
            hefty2 = hefty2.negate();
        }

        int shift = 0;
        HeftyInteger res = new HeftyInteger(ZERO);
        byte[] vals = hefty2.getVal();

        // do grade school algorithm
        for (int current = vals.length - 1; current >= 0; current--) {
            byte b_digit = vals[current];
            for (int i = 0; i < 8; i++) {
                int current_bit = (b_digit >> i) & 1;
                if (current_bit == 1) {
                    res = res.add(hefty1.leftShift(shift));
                }
                shift++;
            }
        }

        if (is_neg) {
            res = res.negate();
        }

        return res.trimLeadingBytes();
    }

    public boolean isZero() {
        for (byte b : val) if (b != 0) return false;
        return true;
    }


    private HeftyInteger leftShift(int amount, boolean shiftWithOne) {
        if (amount == 0) return this;

        int padding_amount = getPaddingAmount() % 8;
        int left_over_shift = amount % 8;
        int shifted_bytes = amount / 8 + ((amount % 8 == 0 || left_over_shift < padding_amount) ? 0 : 1);
        if (left_over_shift == 0) left_over_shift = 8;
        int right_shift_amount = 8 - left_over_shift;

        byte[] shifted = new byte[val.length + shifted_bytes];

        byte[] onesShiftMask = {0, 0b00000001, 0b00000011, 0b00000111, 0b00001111, 0b00011111, 0b00111111, 0b01111111, -1};
        byte[] leftBitMask = {-1, -2, -4, -8, -16, -32, -64, -128, 0};
        byte[] rightBitMask = {-1, 127, 63, 31, 15, 7, 3, 1};
        if (left_over_shift < padding_amount) {
            for (int i = 0; i < val.length - 1; i++) {
                byte left_piece = (byte) ((val[i] << left_over_shift) & leftBitMask[left_over_shift]);
                byte right_piece = (byte) ((val[i + 1] >> right_shift_amount) & rightBitMask[right_shift_amount]);
                shifted[i] = (byte) (left_piece | right_piece);
            }
            shifted[val.length - 1] = (byte) (val[val.length - 1] << left_over_shift);
            if (shiftWithOne) {
                shifted[val.length - 1] = (byte) (shifted[val.length - 1] | onesShiftMask[left_over_shift]);
                for (int i = val.length; i < shifted.length; i++) shifted[i] = (byte) (0xFF);
            }

        } else {
            shifted[0] = (byte) (val[0] >>> right_shift_amount);
            for (int i = 1; i < val.length; i++) {
                byte left_piece = (byte) ((val[i - 1] << left_over_shift) & leftBitMask[left_over_shift]);
                byte right_piece = ((byte) ((val[i] >>> right_shift_amount) & rightBitMask[right_shift_amount]));
                shifted[i] = (byte) (left_piece | right_piece);
            }
            shifted[val.length] = (byte) (val[val.length - 1] << left_over_shift);
            if (shiftWithOne) {
                shifted[val.length] = (byte) (shifted[val.length] | onesShiftMask[left_over_shift]);
                for (int i = val.length + 1; i < shifted.length; i++) shifted[i] = (byte) (0xFF);
            }
        }

        return new HeftyInteger(shifted);
    }

    public HeftyInteger leftShift(int amount) {
        return leftShift(amount, false);
    }

    public int getPaddingAmount() {
        int i = 0;
        byte curr = val[i];
        int firstbit = curr >> 7 & 1;
        byte padded = (byte) (firstbit == 1 ? 0xFF : 0);
        int padding_amount = 0;
        // Add 8 bits for every byte that is padding
        while (curr == padded && i + 1 < val.length) {
            curr = val[++i];
            padding_amount += 8;
        }
        int current_bit = 7;
        while ((curr >> current_bit & 0x01) == firstbit && current_bit >= 0) {
            current_bit--;
            padding_amount++;
        }
        return padding_amount;
    }


    public HeftyInteger trimLeadingBytes() {
        int pad = getPaddingAmount() - 1;
        int lead = pad / 8;
        if (pad > 8) {
            byte[] newbyte = new byte[val.length - lead];
            System.arraycopy(val, lead, newbyte, 0, val.length - lead);
            return new HeftyInteger(newbyte);
        }
        return this;
    }


    /**
     * Run the extended Euclidean algorithm on this and other
     * @param other another HeftyInteger
     * @return an array structured as follows:
     *   0:  the GCD of this and other
     *   1:  a valid x value
     *   2:  a valid y value
     * such that this * x + other * y == GCD in index 0
     */
    public HeftyInteger[] XGCD(HeftyInteger other) {
        byte[] ZERO = {(byte) 0};
        if (other.isZero()) return new HeftyInteger[]{this, new HeftyInteger(ONE), new HeftyInteger(ZERO)};

        HeftyInteger[] vals = other.XGCD(this.mod(other));
        HeftyInteger hefty1 = vals[0];
        HeftyInteger hefty2 = vals[2];
        HeftyInteger div = this.divide(other)[0];
        HeftyInteger hefty3 = vals[1].subtract(div.multiply(hefty2));
        return new HeftyInteger[]{hefty1, hefty2, hefty3};
    }

    public HeftyInteger mod(HeftyInteger divisor) {
        return this.divide(divisor)[1];
    }

    public HeftyInteger[] divide(HeftyInteger divisor) {
        byte[] ZERO = {(byte) 0};
        HeftyInteger dividend = this;
        boolean quotientneg = (this.isNegative() && !divisor.isNegative()) || (!this.isNegative() && divisor.isNegative());
        boolean remainderneg = dividend.isNegative();
        if (divisor.isNegative()) divisor = divisor.negate();
        if (dividend.isNegative()) dividend = dividend.negate();


        if (dividend.equals(divisor)) {
            HeftyInteger one = new HeftyInteger(ONE);
            if (quotientneg) one = one.negate();
            return new HeftyInteger[]{one, new HeftyInteger(ZERO)};
        }

        if (dividend.lt(divisor)) {
            if (remainderneg) dividend = dividend.negate();
            return new HeftyInteger[]{new HeftyInteger(ZERO), dividend};
        }


        int shift_amt = dividend.length() * 8 - 1;
        divisor = divisor.leftShift(shift_amt);
        int i = 0;
        HeftyInteger remainder = dividend;
        HeftyInteger quotient = new HeftyInteger(ZERO);

        while (i <= shift_amt) {
            HeftyInteger difference = remainder.subtract(divisor);
            if (difference.isNegative()) {
                quotient = quotient.leftShiftWithZero(1);
            } else {
                quotient = quotient.leftShiftWithOne(1);
                remainder = difference;
            }
            divisor = divisor.rightShiftByOne();
            i++;
        }

        quotient = quotient.trimLeadingBytes();
        remainder = remainder.trimLeadingBytes();
        if (remainderneg && !remainder.isZero()) remainder = remainder.negate();
        if (quotientneg && !quotient.isZero()) quotient = quotient.negate();

        return new HeftyInteger[]{quotient, remainder};
    }

    public HeftyInteger leftShiftWithZero(int amount) {
        return leftShift(amount, false);
    }

    public HeftyInteger leftShiftWithOne(int amount) {
        return leftShift(amount, true);
    }

    public HeftyInteger rightShiftByOne() {
        byte[] shifted = new byte[val.length];
        int mem = val[0] & 1;
        shifted[0] = (byte) (val[0] >> 1);
        for (int i = 1; i < val.length; i++) {
            byte current = val[i];
            int lsb = current & 1;
            byte mem_mask = (byte) (mem == 1 ? 0b10000000 : 0);
            shifted[i] = (byte) (current >> 1 & 0b01111111 | mem_mask);
            mem = lsb;
        }
        return new HeftyInteger(shifted);
    }

    public boolean lt(HeftyInteger other) {
        return this.compareTo(other) < 0;
    }

    public int compareTo(HeftyInteger other) {
        HeftyInteger a = this.trimLeadingBytes();
        HeftyInteger b = other.trimLeadingBytes();
        if (!a.isNegative() && b.isNegative()) return 1;
        if (a.isNegative() && !b.isNegative()) return -1;


        int if_negative_flipper = a.isNegative() ? -1 : 1;

        if (a.length() > b.length()) return 1 * if_negative_flipper;
        if (a.length() < b.length()) return -1 * if_negative_flipper;

        byte[] aarr = a.getVal();
        byte[] barr = b.getVal();

        if (a.isNegative()) {
            aarr = a.negate().getVal();
            barr = b.negate().getVal();
        }

        int ans = 0;

        for (int i = 0; i < aarr.length; i++) {
            int current_a_byte = aarr[i] & 0xFF;
            int current_b_byte = barr[i] & 0xFF;
            if (current_a_byte < current_b_byte) {
                ans = -1;
                break;
            } else if (current_a_byte > current_b_byte) {
                ans = 1;
                break;
            }
        }

        return ans * if_negative_flipper;
    }

}
