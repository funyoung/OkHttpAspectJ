package com.sogou.bu.basic.theme;

@SuppressWarnings("PMD")
public final class StringTokenizer {

    private static final String TAG = "StringTokenizer";
//    private static final String EMPTY_TOKEN = "";

    private String string;
    private int delimiter;
    private int position;

    public StringTokenizer() {
    }

    public StringTokenizer(String string, int delimiter) {
        init(string, delimiter);
    }

    public StringTokenizer init(String string, int delimiter) {
        this.string = string;
        this.delimiter = delimiter;
        this.position = 0;
        return this;
    }

    public int countTokens() {
        int count = 0, i = 0;
        while ((i = string.indexOf(delimiter, i)) != -1) {
            count++;
            i++;
        }
        count++;
        return count;
    }

    public boolean hasMoreTokens() {
        return (position < string.length());
    }

    public String nextToken() {
        final int length = string.length();
        int i = position;
        if (i == length) return null;
        position = string.indexOf(delimiter, i);
        if (position == -1) position = length;
        String ret = (i == position) ? null : string.substring(i, position);
        position++;
        return ret;
    }
}