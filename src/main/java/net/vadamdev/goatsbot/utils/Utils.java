package net.vadamdev.goatsbot.utils;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * @author VadamDev
 * @since 20/03/2024
 */
public final class Utils {
    private Utils() {}

    public static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";

    public static SimpleDateFormat createdDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    @Nullable
    public static Date parseDate(String source) {
        try {
            return createdDateFormat(DATE_FORMAT_PATTERN).parse(source);
        } catch (ParseException ignored) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        return createdDateFormat(DATE_FORMAT_PATTERN).format(date);
    }

    public static <T> String displayCollection(Collection<T> collection, BiConsumer<StringBuilder, T> lineAction, BiConsumer<StringBuilder, T> lastAction) {
        final StringBuilder builder = new StringBuilder();
        final int size = collection.size() - 1;

        int i = 0;
        for(T t : collection) {
            if(i < size)
                lineAction.accept(builder, t);
            else
                lastAction.accept(builder, t);

            i++;
        }

        return builder.toString();
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
