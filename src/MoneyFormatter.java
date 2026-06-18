package util;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormatter {

    private MoneyFormatter() {
    }

    public static String format(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.KOREA);
        return formatter.format(amount);
    }
}
