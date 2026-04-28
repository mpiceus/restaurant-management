package util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class MoneyUtils {
    private MoneyUtils() {
    }

    private static final DecimalFormat DF = new DecimalFormat("#,##0");

    public static String formatVnd(BigDecimal v) {
        if (v == null) {
            return "0";
        }
        return DF.format(v);
    }
}

