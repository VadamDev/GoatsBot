package net.vadamdev.goatsbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.vadamdev.goatsbot.Main;

import java.awt.*;

/**
 * @author VadamDev
 * @since 19/03/2024
 */
public class GoatsEmbed extends EmbedBuilder {
    public static final Color SUCCESS_COLOR = Color.GREEN;
    public static final Color NEUTRAL_COLOR = Color.WHITE;
    public static final Color ERROR_COLOR = Color.RED;

    public GoatsEmbed() {
        setFooter("G.O.A.T.S Helper - By VadamDev", Main.goatsBot.getAvatarURL());
    }
}
