package net.vadamdev.goatsbot.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.vadamdev.goatsbot.poll.system.AbstractPoll;
import net.vadamdev.goatsbot.poll.system.ITimeLimitedPoll;
import net.vadamdev.goatsbot.poll.system.PollEntry;
import net.vadamdev.goatsbot.utils.GoatsEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author VadamDev
 * @since 18/03/2024
 */
public class EventPoll extends AbstractPoll implements ITimeLimitedPoll {
    private final String link;
    private final Date date;
    private final long dateTime;

    public EventPoll(String link, @Nullable Date date) {
        super(new PollEntry[] {
                new PollEntry("Présent", Emoji.fromUnicode("✅")),
                new PollEntry("Absent", Emoji.fromUnicode("❌"))
        });

        this.link = link;
        this.date = date;
        this.dateTime = date != null ? date.getTime() / 1000 : 0;
    }

    @Nonnull
    @Override
    protected MessageEmbed createEmbed() {
        final StringBuilder description = new StringBuilder(
                "**Lien:**\n" +
                link
        );

        if(dateTime != 0) {
            description.append("\n\n");
            description.append("**Date:**\n");
            description.append("<t:" + dateTime + ":F> (<t:" + dateTime + ":R>)");
        }

        final EmbedBuilder embed = new GoatsEmbed()
                .setTitle("\uD83D\uDE80 | Activité")
                .setDescription(description + "\n\u200E")
                .setColor(GoatsEmbed.NEUTRAL_COLOR);

        addResultFields(embed);

        return embed.build();
    }

    @Nullable
    @Override
    public Date getDeadline() {
        return date;
    }
}
