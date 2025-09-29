package bfst22.vector.Util;

import java.text.DecimalFormat;

public final class NumberFormatter {
    public static String formatDecimal(int decimalCount, double number) {
        String pattern = "0";
        if (decimalCount > 0) {
            pattern += ".";
        }
        for (int i = 0; i < decimalCount; i++) {
            pattern += "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }

    public static String formatDistance(double distance) {
        if (distance < 1) {
            distance *= 1000;
            return Math.round(distance) + " m";
        }
        return formatDecimal(2, distance) + " km";
    }

}
