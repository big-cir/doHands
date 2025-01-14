package com.be.dohands.sheet;

public class TypeConversionUtil {

    private TypeConversionUtil() {
    }

    /**
     * Object -> Integer 변환.
     * @param value 변환할 값
     * @return Integer로 변환된 값 (빈 문자열 또는 null은 null 반환)
     */
    public static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        String stringValue = value.toString();
        if (stringValue.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Integer 변환 실패 : " + value, e);
        }
    }

    /**
     * Object -> Integer 변환.
     * @param value 변환할 값
     * @return Integer로 변환된 값 (빈 문자열 또는 null은 null 반환)
     */
    public static Float toFloat(Object value) {
        if (value == null) {
            return null;
        }

        String stringValue = value.toString();
        if (stringValue.isEmpty()) {
            return null;
        }

        try {
            return Float.parseFloat(stringValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Float 변환 실패 : " + value, e);
        }
    }

    /**
     * Object(Float) -> 반올림해서 Integer 변환.
     * @param value 변환할 값
     * @return Integer로 변환된 값 (빈 문자열 또는 null은 null 반환)
     */
    public static Integer toFlatToInteger(Object value) {
        if (value == null) {
            return null;
        }

        String stringValue = value.toString();
        if (stringValue.isEmpty()) {
            return null;
        }

        try {
            float parsedFloat = Float.parseFloat(stringValue);
            return Math.round(parsedFloat);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Float 변환 실패 : " + value, e);
        }
    }
}