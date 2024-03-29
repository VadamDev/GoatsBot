package net.vadamdev.goatsbot;

import net.vadamdev.jdautils.application.JDAApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VadamDev
 * @since 17/03/2024
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(GoatsBot.class);
    public static final GoatsBot goatsBot = new GoatsBot();

    public static void main(String[] args) {
        final JDAApplication<GoatsBot> application = new JDAApplication<>(goatsBot, logger);
        application.start();
    }
}
