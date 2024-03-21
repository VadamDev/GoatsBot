package net.vadamdev.goatsbot;

import net.vadamdev.goatsbot.commands.ActivityCommand;
import net.vadamdev.goatsbot.commands.PollCommand;
import net.vadamdev.goatsbot.config.MainConfig;
import net.vadamdev.goatsbot.listeners.EventListener;
import net.vadamdev.goatsbot.poll.system.PollManager;
import net.vadamdev.jdautils.application.JDABot;
import net.vadamdev.jdautils.configuration.ConfigurationLoader;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 17/03/2024
 */
public class GoatsBot extends JDABot {
    public final MainConfig mainConfig;

    private PollManager pollManager;

    public GoatsBot() {
        super(BotToken.RELEASE.getToken(), null);

        this.mainConfig = new MainConfig();
    }

    @Override
    public void onEnable() {
        initFiles();

        jda.getPresence().setActivity(mainConfig.formatActivity());

        pollManager = new PollManager();

        registerListeners(
                new EventListener()
        );

        registerCommands(
                new ActivityCommand(),
                new PollCommand()
        );
    }

    @Override
    public void onDisable() {
        pollManager.onDisable();
    }

    private void initFiles() {
        try {
            ConfigurationLoader.loadConfiguration(mainConfig);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public PollManager getPollManager() {
        return pollManager;
    }
}
