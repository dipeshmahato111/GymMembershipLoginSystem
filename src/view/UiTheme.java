package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared visual identity for the system: a purple brand palette and a
 * clean sans-serif type scale, applied to the Login screen (SRS 4.6
 * Usability: "a simple, consistent, and intuitive design utilizing a
 * standard color theme").
 *
 * <p>Fonts are resolved defensively against whatever is actually
 * installed on the machine (no bundled font file is required), falling
 * back to the platform's logical sans-serif font if none of the
 * preferred families are available.</p>
 */
public final class UiTheme {

    public static final Color PURPLE_DARK = new Color(0x3B1E63);
    public static final Color PURPLE_PRIMARY = new Color(0x5E2CA5);
    public static final Color PURPLE_LIGHT = new Color(0xC9B8E8);
    public static final Color ACCENT_GOLD = new Color(0xF2B705);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_LIGHT_MUTED = new Color(0xE6DCFA);
    public static final Color CARD_BACKGROUND = Color.WHITE;

    private static final String FAMILY = resolveFamily();

    private UiTheme() {
    }

    private static String resolveFamily() {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidate : new String[]{"Segoe UI", "Helvetica Neue", "Arial"}) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font heading(int size) {
        return new Font(FAMILY, Font.BOLD, size);
    }

    public static Font body(int size) {
        return new Font(FAMILY, Font.PLAIN, size);
    }

    public static Font bodyBold(int size) {
        return new Font(FAMILY, Font.BOLD, size);
    }
}
