package net.vadamdev.goatsbot.poll.system;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashSet;
import java.util.Set;

/**
 * @author VadamDev
 * @since 18/03/2024
 */
public class PollEntry {
    private final String title;
    private final Emoji icon;
    private final Set<User> users;

    public PollEntry(String title, Emoji icon) {
        this.title = title;
        this.icon = icon;
        this.users = new HashSet<>();
    }

    public Button toButton(int index) {
        return Button.secondary("GoatsBot-Poll-Entry-" + index, icon);
    }

    public String getTitle() {
        return title;
    }

    public String getFormattedIcon() {
        return icon.getFormatted();
    }

    public Set<User> getUsers() {
        return users;
    }
}
