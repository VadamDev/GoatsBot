package net.vadamdev.goatsbot.poll;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.vadamdev.goatsbot.Main;
import net.vadamdev.goatsbot.poll.system.AbstractPoll;
import net.vadamdev.goatsbot.poll.system.ITimeLimitedPoll;
import net.vadamdev.goatsbot.poll.system.PollEntry;
import net.vadamdev.goatsbot.utils.GoatsEmbed;
import net.vadamdev.goatsbot.utils.Utils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Set;

/**
 * @author VadamDev
 * @since 19/03/2024
 */
public class CustomPoll extends AbstractPoll implements ITimeLimitedPoll {
    private static final int BAR_LENGTH = 18;

    private String title, description;

    private Date date;
    private long dateTime;

    public CustomPoll(String title, String description, @Nullable Date date) {
        super(new PollEntry[] {
                new PollEntry("Oui", Emoji.fromUnicode("✅")),
                new PollEntry("Non", Emoji.fromUnicode("❌"))
        });

        setupVariables(title, description, date);
    }

    private void updatePoll(JDA jda, String title, String description, @Nullable Date date) {
        if(guildId == null || channelId == null)
            return;

        setupVariables(title, description, date);
        updateMessage(jda.getGuildById(guildId).getChannelById(TextChannel.class, channelId));
    }

    private void setupVariables(String title, String description, @Nullable Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.dateTime = date != null ? date.getTime() / 1000 : 0;
    }

    @Nonnull
    @Override
    protected MessageEmbed createEmbed() {
        final StringBuilder description = new StringBuilder(this.description);

        if(dateTime != 0) {
            description.append("\n\n");
            description.append("**Date:**\n");
            description.append("<t:" + dateTime + ":F> (<t:" + dateTime + ":R>)");
        }

        final EmbedBuilder embed = new GoatsEmbed()
                .setTitle("\uD83D\uDCCA | " + title)
                .setDescription(description + "\n\u200E")
                .setColor(GoatsEmbed.NEUTRAL_COLOR);

        addResultFields(embed);

        return embed.build();
    }

    @Override
    protected void addResultFields(EmbedBuilder embed) {
        int totalVoters = 0;
        for(PollEntry entry : entries)
            totalVoters += entry.getUsers().size();

        for (PollEntry entry : entries) {
            final int voters = entry.getUsers().size();

            embed.addField(entry.getFormattedIcon() + " " + entry.getTitle() + " (" + voters + ")", generatePollBar(totalVoters, voters), false);
        }
    }

    private String generatePollBar(int totalVoters, int voters) {
        double percentage = 100D * voters / totalVoters;
        int barLength = (int) Math.round(percentage * BAR_LENGTH / 100);

        if(totalVoters == 0 || voters == 0) {
            percentage = 0;
            barLength = 0;
        }

        final StringBuilder bar = new StringBuilder("``");

        for(int i = 0; i < BAR_LENGTH; i++) {
            if(i < barLength)
                bar.append("█");
            else
                bar.append(" ");
        }

        bar.append("`` | (" + percentage + "%)");

        return bar.toString();
    }

    /*
       Settings
     */

    @Override
    protected void onOpenSettingsButtonClicked(@Nonnull ButtonInteractionEvent event) {
        event.replyEmbeds(new GoatsEmbed()
                .setTitle("Sondage - Paramètres")
                .setDescription(
                        "**Boutons:**\n" +
                        "> \uD83D\uDCCB *: Voir les résultats*\n" +
                        "> \uD83D\uDD8A *: Modifier le sondage*\n" +
                        "> \uD83D\uDD12️ *: Ferme le sondage*"
                )
                .setColor(GoatsEmbed.NEUTRAL_COLOR).build()).setEphemeral(true).setActionRow(
                        Button.secondary("GoatsBot-Poll-Settings-Results-" + messageId, Emoji.fromUnicode("\uD83D\uDCCB")),
                        Button.secondary("GoatsBot-Poll-Settings-Edit-" + messageId, Emoji.fromUnicode("\uD83D\uDD8A")),
                        Button.danger("GoatsBot-Poll-Settings-Delete-" + messageId, Emoji.fromUnicode("\uD83D\uDD12"))
                ).queue();
    }

    @Override
    protected void onSettingsButtonClicked(@Nonnull ButtonInteractionEvent event, String componentId) {
        switch(componentId) {
            case "GoatsBot-Poll-Settings-Results":
                final StringBuilder description = new StringBuilder();

                for(PollEntry entry : entries) {
                    final Set<User> voters = entry.getUsers();
                    final int votersSize = voters.size();

                    description.append("> " + entry.getFormattedIcon() + " " + entry.getTitle() + " (" + votersSize + ")" + "\n");

                    if(!voters.isEmpty()) {
                        description.append("> " + Utils.displayCollection(voters,
                                (builder, user) -> builder.append(user.getAsMention() + ", "),
                                (builder, user) -> builder.append(user.getAsMention()))
                        );
                    }else
                        description.append("> \u200E");

                    description.append("\n\n");
                }

                event.replyEmbeds(new GoatsEmbed()
                        .setTitle("\uD83D\uDCCA | " + title + " - Résultats")
                        .setDescription(description.toString())
                        .setColor(GoatsEmbed.NEUTRAL_COLOR).build()).setEphemeral(true).queue();

                break;
            case "GoatsBot-Poll-Settings-Edit":
                event.replyModal(
                        Modal.create("GoatsBot-EditCustomPollModal-" + messageId, "Sondage Custom")
                                .addComponents(
                                        ActionRow.of(TextInput.create("title", "Nom", TextInputStyle.SHORT)
                                                .setRequiredRange(1, 48)
                                                .setValue(title)
                                                .build()),

                                        ActionRow.of(TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                                                .setRequiredRange(1, 2048)
                                                .setValue(this.description)
                                                .build()),

                                        ActionRow.of(TextInput.create("date", "Date", TextInputStyle.SHORT)
                                                .setRequiredRange(16, 16)
                                                .setPlaceholder(Utils.formatDate(date != null ? date : new Date()))
                                                .setValue(date != null ? Utils.formatDate(date) : null)
                                                .setRequired(false)
                                                .build())
                                ).build()
                ).queue();

                break;
            default:
                super.onSettingsButtonClicked(event, componentId);
                break;
        }
    }

    @Nullable
    @Override
    public Date getDeadline() {
        return date;
    }

    /*
       Handle events
     */

    public static void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        final String modalId = event.getModalId();

        final boolean create = modalId.equals("GoatsBot-CustomPollModal");
        final boolean edit = modalId.startsWith("GoatsBot-EditCustomPollModal-");

        if(create || edit) {
            final String title = event.getValue("title").getAsString();
            final String description = event.getValue("description").getAsString();
            final ModalMapping dateMapping = event.getValue("date");

            final Date date = dateMapping != null ? Utils.parseDate(dateMapping.getAsString()) : null;

            if(edit) {
                final String[] split = modalId.split("-");
                if(split.length != 3)
                    return;

                final String messageId = split[2];
                Main.goatsBot.getPollManager().getPollByMessageId(messageId).filter(CustomPoll.class::isInstance).ifPresent(p -> {
                    ((CustomPoll) p).updatePoll(event.getJDA(), title, description, date);
                    event.deferEdit().queue();
                });
            }else
                new CustomPoll(title, description, date).open(event);
        }
    }
}
