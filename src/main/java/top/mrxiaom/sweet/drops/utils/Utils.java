package top.mrxiaom.sweet.drops.utils;

public class Utils {
    public static Integer decode(String nm) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        Integer result;

        if (nm == null || nm.isEmpty()) return null;
        char firstChar = nm.charAt(0);
        // Handle sign, if present
        if (firstChar == '-') {
            negative = true;
            index++;
        } else if (firstChar == '+')
            index++;

        // Handle radix specifier, if present
        if (nm.startsWith("0x", index) || nm.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        }
        else if (nm.startsWith("#", index)) {
            index ++;
            radix = 16;
        }
        else if (nm.startsWith("0", index) && nm.length() > 1 + index) {
            index ++;
            radix = 8;
        }

        if (nm.startsWith("-", index) || nm.startsWith("+", index))
            return null;

        try {
            result = Integer.parseUnsignedInt(nm.substring(index), radix);
            result = negative ? Integer.valueOf(-result) : result;
        } catch (NumberFormatException e) {
            try {
                String constant = negative ? ("-" + nm.substring(index)) : nm.substring(index);
                result = Integer.parseUnsignedInt(constant, radix);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return result;
    }
}
