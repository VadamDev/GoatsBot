package net.vadamdev.goatsbot.utils;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author VadamDev
 * @since 20/03/2024
 */
public final class Utils {
    private Utils() {}

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";
    public static SimpleDateFormat createdDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN);
    }

    @Nullable
    public static Date parseDate(String source) {
        try {
            return createdDateFormat().parse(source);
        } catch (ParseException ignored) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        return createdDateFormat().format(date);
    }

    public static boolean isURL(String str) {
        try {
            new URL(str);
            return true;
        }catch (Exception ignored) {
            return false;
        }
    }
}
