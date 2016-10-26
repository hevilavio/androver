package com.hevilavio.ardurover.util;

/**
 * Created by hevilavio on 10/26/16.
 */

public class Mapper {

    /**
     *
     * Y = (X-A)/(B-A) * (D-C) + C
     * reference: http://stackoverflow.com/questions/345187/math-mapping-numbers
     * */
    public int linearMapping(int value, int a, double b, double c, double d) {
        int x = value;
        return (int) ((x-a)/(b -a) * (d - c) + c);
    }
}
