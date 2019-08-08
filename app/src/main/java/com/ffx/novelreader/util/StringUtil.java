package com.ffx.novelreader.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public final class StringUtil {

    /**
     * 匹配字符中第一符合的分组中的第一个子匹配内容
     * @param input 输入字符串
     * @param pattern 匹配Pattern
     * @return input中匹配pattern第一个符合的第一个子匹配内容
     */
    public static String match(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        String result = "";
        if (matcher.find()) {
            result = matcher.group(1);
        }
        matcher.reset();
        return result;
    }

    /**
     * 将字符串中匹配的字串提取出来
     * @param input 输入字符串
     * @param pattern 匹配Pattern
     * @return 匹配的字串
     */
    public static String matchTotal(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        String result = "";
        if (matcher.find()) {
            result = matcher.group(0);
        }
        matcher.reset();
        return result;
    }

    /**
     * 将字符串中html转移字符替换为对应的字符
     * @param htmlText html字符串
     * @return 移除后的字符串
     */
    public static String htmtlRemoveEscape(String htmlText) {
        htmlText = htmlText.replaceAll("&nbsp;", " "); //移除空白字符
        htmlText = htmlText.replaceAll("<br\\s?/>", "\n");
        htmlText = htmlText.replaceAll("<p>([\\s\\S]+?)</p>", "$1\n");
        return htmlText;
    }

    /**
     * 移除一个字符串中某个字串之前的所有内容（包含字串）
     * @param inputText 输入字符串
     * @param toRemove 要移除的子串
     * @return 返回移除后的字符串
     */
    public static String removeBefore(String inputText, String toRemove) {
        int index = inputText.indexOf(toRemove);
        if (index != -1) {
            inputText = inputText.substring(index + toRemove.length());
        }
        return inputText;
    }
}
