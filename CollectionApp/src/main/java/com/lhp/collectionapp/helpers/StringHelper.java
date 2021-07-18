package com.lhp.collectionapp.helpers;

public class StringHelper {

    public static String addDecimal(String str) {
        int len = str.length();
        int position = len-2;
        char[] updatedArr = new char[len + 1];
        str.getChars(0, position, updatedArr, 0);
        updatedArr[position] = '.';
        str.getChars(position, len, updatedArr, position + 1);
        return new String(updatedArr);
    }

    public static String trimString(String str) {
        return str.substring(1, str.length());
    }
}
