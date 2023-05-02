package co.aegis.ares.utils;

import co.aegis.ares.Ares;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.aegis.ares.utils.Nullables.isNullOrEmpty;
import static java.util.stream.Collectors.joining;

public class StringUtils {

    @Getter
    private static final String colorChar = "§";
    @Getter
    private static final String altColorChar = "&";
    @Getter
    private static final String colorCharsRegex = "[" + colorChar + altColorChar + "]";
    @Getter
    private static final Pattern colorPattern = Pattern.compile(colorCharsRegex + "[\\da-fA-F]");
    @Getter
    private static final Pattern formatPattern = Pattern.compile(colorCharsRegex + "[k-orK-OR]");
    @Getter
    private static final Pattern hexPattern = Pattern.compile(colorCharsRegex + "#[a-fA-F\\d]{6}");
    @Getter
    private static final Pattern hexColorizedPattern = Pattern.compile(colorCharsRegex + "x(" + colorCharsRegex + "[a-fA-F\\d]){6}");
    @Getter
    private static final Pattern colorGroupPattern = Pattern.compile("(" + colorPattern + "|(" + hexPattern + "|" + hexColorizedPattern + "))((" + formatPattern + ")+)?");


    public static String getPrefix(String prefix) {
        return colorize("&8&l[" + Ares.colorPrimary + prefix + "&8&l]" + Ares.colorSecondary + " ");
    }

    public static String colorize(String input) {
        if (input == null)
            return null;

        while (true) {
            Matcher matcher = hexPattern.matcher(input);
            if (!matcher.find()) break;

            String color = matcher.group();
            input = input.replace(color, ChatColor.of(color.replaceFirst(colorCharsRegex, "")).toString());
        }

        return ChatColor.translateAlternateColorCodes(altColorChar.charAt(0), input);
    }

    public static String stripColor(String input) {
        return ChatColor.stripColor(colorize(input));
    }

    public static String stripFormat(String input) {
        return formatPattern.matcher(colorize(input)).replaceAll("");
    }

    private static final int APPROX_LORE_LINE_LENGTH = 40;

    public static List<String> loreize(String string) {
        return loreize(string, APPROX_LORE_LINE_LENGTH);
    }

    public static List<String> loreize(String string, int length) {
        return new ArrayList<>() {{
            final String[] split = string.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : split) {
                final int oldLength = stripColor(line.toString()).length();
                final int newLength = oldLength + stripColor(word).length();

                boolean append = Math.abs(length - oldLength) >= Math.abs(length - newLength);
                if (!append) {
                    String newline = line.toString().trim();
                    add(line.toString().trim());
                    line = new StringBuilder(getLastColor(newline));
                }

                line.append(word).append(" ");
            }

            add(line.toString().trim());
        }};
    }

    public static String getLastColor(String text) {
        Matcher matcher = colorGroupPattern.matcher(text);
        String last = "";
        while (matcher.find())
            last = matcher.group();
        return last.toLowerCase();
    }

    public static String plural(String label, Number number) {
        return label + (number.doubleValue() == 1 ? "" : "s");
    }

    public static String plural(String labelSingle, String labelPlural, Number number) {
        return number.doubleValue() == 1 ? labelSingle : labelPlural;
    }

    public static @NotNull String camelCase(Enum<?> _enum) {
        if (_enum == null) return "null";
        return camelCase(_enum.name());
    }

    @Contract("null -> null; !null -> !null")
    public static String camelCase(String text) {
        if (isNullOrEmpty(text))
            return text;

        return Arrays.stream(text.replaceAll("_", " ").split(" "))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(joining(" "));
    }

    public static String camelCaseWithUnderscores(String text) {
        if (isNullOrEmpty(text))
            return text;

        return Arrays.stream(text.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(joining("_"));
    }

    public static String getNumberWithSuffix(int number) {
        String text = String.valueOf(number);
        if (text.endsWith("1"))
            if (text.endsWith("11"))
                return number + "th";
            else
                return number + "st";
        else if (text.endsWith("2"))
            if (text.endsWith("12"))
                return number + "th";
            else
                return number + "nd";
        else if (text.endsWith("3"))
            if (text.endsWith("13"))
                return number + "th";
            else
                return number + "rd";
        else
            return number + "th";
    }

}
