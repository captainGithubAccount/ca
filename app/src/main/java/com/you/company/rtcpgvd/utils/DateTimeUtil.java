package com.you.company.rtcpgvd.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public static Date formatTimeString(String str){
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
             date = sdf.parse(str);
            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public static String formatMillisString2(long timestamp) {
        Date date = new Date(timestamp);

        // 定义一个SimpleDateFormat对象，用于格式化时间
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");

        // 格式化时间
        String formattedTime = formatter.format(date);
        return formattedTime;
    }

    public static String formatMillisString(long timestamp) {
        Date date = new Date(timestamp);

        // 定义一个SimpleDateFormat对象，用于格式化时间
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        // 格式化时间
        String formattedTime = formatter.format(date);
        return formattedTime;
    }

    /**
     * 将时间戳（毫秒）转换为时:分:秒的格式
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的时间字符串
     */
    public static String convertTimestampToHMS(long timestamp) {
        // 将时间戳转换为小时、分钟和秒
        long hours = TimeUnit.MILLISECONDS.toHours(timestamp);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timestamp) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timestamp) - TimeUnit.MINUTES.toSeconds(minutes);

        // 格式化并返回时间字符串
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static long parseTimeToMilliseconds(String time) {
        // 校验时间格式是否正确
        if (!time.matches("\\d{2}:\\d{2}:\\d{2}$")) {
            throw new IllegalArgumentException("时间格式不正确，应为HH:MM:SS");
        }

        // 分割时间字符串
        String[] parts = time.split(":");

        // 解析小时、分钟和秒
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        // 计算毫秒数
        long milliseconds = (hours * 3600 + minutes * 60 + seconds) * 1000L;

        return milliseconds;
    }

    /**
     * 将时间格式mm:ss转换为毫秒数
     * @param timeStr 时间字符串，格式为"mm:ss"
     * @return 对应的毫秒数
     * @throws IllegalArgumentException 如果时间字符串格式不正确
     */
    public static long convertToMilliseconds(String timeStr) {
        // 检查时间字符串的格式
        if (timeStr == null || !timeStr.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("时间格式不正确，应为mm:ss");
        }

        // 分割时间字符串
        String[] parts = timeStr.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);

        // 转换为毫秒数
        long milliseconds = (minutes * 60 + seconds) * 1000;

        return milliseconds;
    }

    public static String formatMillisecondsToTime(long milliseconds) {
        // 计算总秒数
        long totalSeconds = milliseconds / 1000;

        // 计算小时数
        long hours = totalSeconds / 3600;

        // 计算剩余的分钟数
        long minutes = (totalSeconds % 3600) / 60;

        // 计算剩余的秒数
        long seconds = totalSeconds % 60;

        // 格式化为HH:mm:ss
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
