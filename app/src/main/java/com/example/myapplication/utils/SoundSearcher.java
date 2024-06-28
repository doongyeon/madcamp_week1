package com.example.myapplication.utils;

public class SoundSearcher {
    private static final char[] INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    public static String getInitialSound(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= '가' && c <= '힣') {
                int unicode = c - 0xAC00;
                int initialSoundIndex = unicode / (21 * 28);
                result.append(INITIAL_SOUND[initialSoundIndex]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
