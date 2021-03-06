package com.zhouwei.customview.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouwei on 2017/4/24.
 */

public class HighLightTextUtil {
    private HighLightTextUtil() {
    }

    /**
     * 关键字高亮显示
     *
     * @param text   需要显示的文字
     * @param target 需要高亮的关键字
     * @param color  高亮的颜色
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder highLightText(String text, String target, int color) {
        if (TextUtils.isEmpty(text)) {
            Log.i("AAAA", "text is empty");
            return null;
        }
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            spannable.setSpan(new ForegroundColorSpan(color), m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }


    public static void highLightText(String text, String target, int color, int maxWidth,TextView tv) {
        //int maxWidth = tv.getWidth();
        String ss = getSuitableString(text, target, maxWidth, tv);
        if (TextUtils.isEmpty(ss)) {
            Log.i("AAAA", "得到的合适字符串为空");
        } else {
            SpannableStringBuilder spannable = new SpannableStringBuilder(ss);

            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(ss);
            while (m.find()) {
                spannable.setSpan(new ForegroundColorSpan(color), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv.setText(spannable);
        }

    }


    static int threePointWidth = 0;
    final static String threePoint = "…";

    //根据给出的最大长度 找到将要显示的合适的字符串
    private static String getSuitableString(String text, String target, int maxWidth, TextView tv) {
        if (getTextWidth(tv, target) >= maxWidth) {
            return target;
        }

        if(getTextWidth(tv,text)<=maxWidth){
            return text;
        }

        threePointWidth = getTextWidth(tv, "…");

        String res = null;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        if (m.find()) {
            /**
             * 哈哈
             * 哈哈AAAAAAAAA
             * AAAAAAAAAA哈哈
             * AAAAAA哈哈AAAA
             */
            int start = m.start();
            int end = m.end();
            // Log.i("AAAA", "start: " + start);
            // Log.i("AAAA", "end: " + end);
            if (target.equals(text)) {
                return target;
            }

            if (start == 0 && end < text.length()) {
                Log.i("AAAA", "哈哈AAAAAAAAAAA");
                // 哈哈AAAAAAAAAAA
                int i = 0;
                for (i = end; i < text.length(); i++) {
                    res = text.substring(0, i + 1) + threePoint;
                    int w = getTextWidth(tv, res);
                    //  Log.i("AAAA", "动态测量的宽度：" + w);
                    if (w > maxWidth) {//加了一个字符和... 超界了 判断是舍去...还是字符
                        // 去掉刚刚加的字符 保留...
                        res = text.substring(0, i) + threePoint;
                        //  Log.i("AAAA", "res: " + res);
                        int ww = getTextWidth(tv, res);
                        if (ww <= maxWidth) {
                            return res;
                        } else {
                            // 再判断去掉... 保留字符
                            res = text.substring(0, i + 1);
                            //  Log.i("AAAA", "res: " + res);
                            int www = getTextWidth(tv, res);
                            if (www <= maxWidth) {
                                return res;
                            } else {
                                // 去掉两个字符 加个...
                                return text.substring(0, i - 1) + threePoint;
                            }
                        }
                    }
                }
            } else if (end == text.length() && start > 0) {
                // AAAAAAAAAA哈哈
                int i = 0;
                for (i = start; i >= 0; i--) {
                    res = threePoint + text.substring(i - 1, text.length());
                    int w = getTextWidth(tv, res);
                    if (w > maxWidth) {//加了一个字符和... 超界了 判断是舍去...还是字符
                        //   去掉刚刚加的字符 保留...
                        res = threePoint + text.substring(i, text.length());
                        int ww = getTextWidth(tv, res);
                        if (ww <= maxWidth) {
                            return res;
                        } else {
                            // 再判断去掉... 保留字符
                            res = text.substring(i - 1, text.length());
                            Log.i("AAAA", "res: " + res);
                            int www = getTextWidth(tv, res);
                            if (www <= maxWidth) {
                                return res;
                            } else {
                                // 去掉两个字符 加个...
                                return threePoint + text.substring(i + 1, text.length());
                            }
                        }
                    }
                }

            } else {
                // AAAAAA哈哈AAAA
                //判断 ...哈哈... 的长度
                res = threePoint + target + threePoint;
                int tw = getTextWidth(tv, res);

                if (tw > maxWidth) {
                    // 判断 ...哈哈 的长度
                    res = threePoint + target;
                    int tww = getTextWidth(tv, res);
                    if (tww > maxWidth) {
                        return target;
                    } else {
                        return res;
                    }
                }

                // 可以构成 ...哈哈...
                int i = 0;
                int j = 0;
                for (i = start - 1, j = end; i >= 0 && j < text.length(); i--, j++) {
                    res = threePoint + text.substring(i, j + 1) + threePoint;
                    int w = getTextWidth(tv, res);
                    if (w < maxWidth) {
                        continue;
                    } else if (w == maxWidth) {
                        return res;
                    } else {
                        // w>maxWidth
                        /**
                         * 去除规则
                         * 先去掉右边的一个字符 然后再去掉右边的...
                         * 再然后去掉左边的一个字符 最后去掉左边的...
                         * 还不行就去掉左右两边各一个字符
                         */
                        //去掉右边的一个字符
                        res = threePoint + text.substring(i, j) + threePoint;
                        int ww = getTextWidth(tv, res);
                        if (ww <= maxWidth) {
                            return res;
                        } else {
                            // 去掉右边的...
                            res = threePoint + text.substring(i, j + 1);
                            int www = getTextWidth(tv, res);
                            if (www <= maxWidth) {
                                return res;
                            } else {
                                // 去掉左边的一个字符
                                res = threePoint + text.substring(i + 1, j) + threePoint;
                                int wwww = getTextWidth(tv, res);
                                if (wwww <= maxWidth) {
                                    return res;
                                } else {
                                    // 去掉左边的...
                                    res = text.substring(i, j + 1) + threePoint;
                                    int wwwww = getTextWidth(tv, res);
                                    if (wwwww <= maxWidth) {
                                        return res;
                                    } else {
                                        return threePoint + text.substring(i + 1, j) + threePoint;
                                    }
                                }
                            }
                        }
                    }

//
//                    // 左边先加一个字符和...
//                    res = threePoint + text.substring(i, end);
//                    int w = getTextWidth(tv, res);
//                    if (w >= maxWidth) {
//                        //去掉刚刚加的字符 保留三个点
//                        res = threePoint + text.substring(i + 1, end);
//                        int ww = getTextWidth(tv, res);
//                        if (ww <=)
//                    }


                }
                if (i < 0 && j < text.length()) {
                    //左边先结束了,右边还需要接着判断
                    //  Log.i("AAAA","左边先结束了");
                    for (int k = j; k < text.length(); k++) {
                        res = text.substring(0, k + 1) + threePoint;
                        int w = getTextWidth(tv, res);
                        ////////////
                        // Log.i("AAAA", "动态测量的宽度：" + w);
                        if (w > maxWidth) {//加了一个字符和... 超界了 判断是舍去...还是字符
                            // 去掉刚刚加的字符 保留...
                            res = text.substring(0, k) + threePoint;
                            // Log.i("AAAA", "res: " + res);
                            int ww = getTextWidth(tv, res);
                            if (ww <= maxWidth) {
                                return res;
                            } else {
                                // 再判断去掉... 保留字符
                                res = text.substring(0, k + 1);
                                // Log.i("AAAA", "res: " + res);
                                int www = getTextWidth(tv, res);
                                if (www <= maxWidth) {
                                    return res;
                                } else {
                                    // 去掉两个字符 加个...
                                    return text.substring(0, k - 1) + threePoint;
                                }
                            }
                        }
                    }
//                    res = text.substring(0,j)+threePoint;
//                    return res;
                } else if (i > 0 && j >= text.length()) {
                    // 右边先结束了，左边还需要接着判断
                    // Log.i("AAAA","右边先结束了");

                    for (int k = i; k >= 0; k--) {
                        res = threePoint + text.substring(k, text.length());
                        int w = getTextWidth(tv, res);
                        if (w > maxWidth) {//加了一个字符和... 超界了 判断是舍去...还是字符
                            //   去掉刚刚加的字符 保留...
                            res = threePoint + text.substring(k + 1, text.length());
                            int ww = getTextWidth(tv, res);
                            if (ww <= maxWidth) {
                                return res;
                            } else {
                                // 再判断去掉... 保留字符
                                res = text.substring(k, text.length());
                                // Log.i("AAAA", "res: " + res);
                                int www = getTextWidth(tv, res);
                                if (www <= maxWidth) {
                                    return res;
                                } else {
                                    // 去掉两个字符 加个...
                                    return threePoint + text.substring(k + 1, text.length());
                                }
                            }
                        }
                    }
//                    res = threePoint+text.substring(i,text.length());
//                    return res;
                } else if (i == 0 && j == text.length()) {
                    // 两边同时结束
                    // Log.i("AAAA","两边同时结束");
                    return target;
                }

            }
        }

        return res;
    }


    private static int getTextWidth(TextView tv, String txt) {
        return (int) tv.getPaint().measureText(txt);
    }

    /**
     * 批量处理关键字高亮显示
     *
     * @param texts  需要显示的文字集合
     * @param target 需要高亮的关键字
     * @param color  高亮的颜色
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static List<SpannableStringBuilder> batchHighLightText(List<String> texts, String target, int color) {
        if (texts == null) {
            return null;
        }

        Pattern p = Pattern.compile(target);

        List<SpannableStringBuilder> highLightResults = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            SpannableStringBuilder spannable = new SpannableStringBuilder(text);

            Matcher m = p.matcher(text);
            while (m.find()) {
                spannable.setSpan(new ForegroundColorSpan(color), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            highLightResults.add(spannable);

        }
        return highLightResults;
    }

    /////////////////////////////////////////////
    private static final String MARK = "…";
    private static final int PREFIX_CHARACTER_LENGTH = 0;
    private static float markWidth;

    public static SpannableString highlightText(TextView textView, int maxWidth, String keyText, String content) {
        SpannableString spannableString = null;
        StringBuilder stringBuilder = new StringBuilder();
        if (markWidth == 0) {
            markWidth = measure(textView, MARK);
        }
        float keyTextWidth = measure(textView, keyText);

        if (content.indexOf(keyText) == 0) {
            if (measure(textView, content) <= maxWidth) {
                putIntoBuilder(content, "#00FF00", 0, keyText.length());
            } else {
                char[] chars = content.toCharArray();
                for (char c : chars) {
                    if (measure(textView, stringBuilder.toString() + c) <= maxWidth) {
                        stringBuilder.append(c);
                    } else {
                        stringBuilder.append(MARK);
                        break;
                    }
                }
                spannableString = putIntoBuilder(stringBuilder.toString(), "#00FF00", 0, keyText.length());
            }
        } else if (content.lastIndexOf(keyText) == content.length() - keyText.length()) {
            if (measure(textView, content) <= maxWidth) {
                spannableString = putIntoBuilder(content, "#00FF00", content.length() - keyText.length(), content.length());
            } else {
                char[] chars = content.toCharArray();
                for (int i = chars.length - 1; i >= 0; i--) {
                    if (measure(textView, stringBuilder.toString() + chars[i]) <= maxWidth - markWidth) {
                        stringBuilder.append(chars[i]);
                    } else {
                        stringBuilder.append(MARK);
                        break;
                    }
                }
                spannableString = putIntoBuilder(stringBuilder.reverse().toString(), "#00FF00", stringBuilder.length() - keyText.length(), stringBuilder.length());
            }
        } else {
            if (measure(textView, content) <= maxWidth) {
                spannableString = putIntoBuilder(content, "#00FF00", content.indexOf(keyText), content.indexOf(keyText) + keyText.length());
            } else {
                int start = content.indexOf(keyText);
                int end = start + keyText.length();
                if (start >= PREFIX_CHARACTER_LENGTH) {
                    char[] chars = content.toCharArray();
                    float prefixPlusWidth = measure(textView, content.substring(start - PREFIX_CHARACTER_LENGTH));
                    int i;
                    if (prefixPlusWidth < maxWidth) {
                        for (i = start - 1 - PREFIX_CHARACTER_LENGTH; i >= 0; i--) {
                            if (measure(textView, stringBuilder.toString() + chars[i]) <= maxWidth - prefixPlusWidth - markWidth) {
                                stringBuilder.append(chars[i]);
                            } else {
                                break;
                            }
                        }

                        if (i >= 0) {
                            stringBuilder.append(MARK);
                        }
                        stringBuilder = stringBuilder.reverse().append(content.substring(start - PREFIX_CHARACTER_LENGTH));

                        spannableString = putIntoBuilder(stringBuilder.toString(), "#00FF00", start - i, start - i + keyText.length());
                    } else {
                        float prefixWidth = measure(textView, content.substring(start - PREFIX_CHARACTER_LENGTH, start));
                        for (i = start - 1; i >= 0; i--) {
                            if (measure(textView, stringBuilder.toString() + chars[i]) <= maxWidth - prefixWidth - keyTextWidth - markWidth) {
                                stringBuilder.append(chars[i]);
                            } else {
                                stringBuilder.append(MARK);
                                break;
                            }
                        }

                        stringBuilder = new StringBuilder(stringBuilder.reverse());
                        stringBuilder.append(keyText);
                        for (int j = end; j < chars.length; j++) {
                            if (measure(textView, stringBuilder.toString() + chars[j]) <= maxWidth) {
                                stringBuilder.append(chars[j]);
                            } else {
                                stringBuilder.append(MARK);
                                break;
                            }
                        }

                        if (start > PREFIX_CHARACTER_LENGTH) {
                            spannableString = putIntoBuilder(MARK + stringBuilder, "#00FF00", PREFIX_CHARACTER_LENGTH + 1, PREFIX_CHARACTER_LENGTH + 1 + keyText.length());
                        } else {
                            spannableString = putIntoBuilder(stringBuilder.toString(), "#00FF00", start, end);
                        }
                    }
                } else {
                    char[] chars = content.toCharArray();
                    int i;
                    stringBuilder.append(content.substring(0, start));
                    for (i = start; i < content.length(); i++) {
                        if (measure(textView, stringBuilder.toString() + chars[i]) < maxWidth - markWidth) {
                            stringBuilder.append(chars[i]);
                        } else {
                            break;
                        }
                    }

                    if (i < content.length()) {
                        stringBuilder.append(MARK);
                    }
                    spannableString = putIntoBuilder(stringBuilder.toString(), "#00FF00", start, end);
                }
            }
        }
        return spannableString;
    }

    private static SpannableString putIntoBuilder(String text, String color, int start, int end) {
        SpannableString builder = new SpannableString(text);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private static float measure(TextView textView, String text) {
        return textView.getPaint().measureText(text);
    }
}
