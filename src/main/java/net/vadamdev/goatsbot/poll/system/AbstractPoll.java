package net.vadamdev.goatsbot.poll.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.goatsbot.GoatsBot;
import net.vadamdev.goatsbot.Main;
import net.vadamdev.goatsbot.utils.GoatsEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author VadamDev
 * @since 17/03/2024
 */
public abstract class AbstractPoll {
    protected final PollEntry[] entries;

    protected String guildId, channelId, messageId;

    public AbstractPoll(PollEntry[] entries) {
        this.entries = entries;
    }

    /*
       Open / Close
     */

    public void open(IReplyCallback callback) {
        createMessage(callback);
    }

    public void close(JDA jda) {
        if(guildId == null || channelId == null || messageId == null)
            return;

        final Message message = jda.getGuildById(guildId).getChannelById(TextChannel.class, channelId).retrieveMessageById(messageId).complete();

        final MessageEmbed closeEmbed = createCloseEmbed();
        if(closeEmbed != null)
            message.editMessageEmbeds(closeEmbed).setComponents().queue();
        else
            message.editMessageComponents().queue();

        Main.goatsBot.getPollManager().unregisterPoll(messageId);
    }

    /*
       Handle Events
     */

    public void handleButtonInteractionEvent(@Nonnull ButtonInteractionEvent event) {
        final String componentId = event.getComponentId();

        if(componentId.equals("GoatsBot-Poll-Settings"))
            onOpenSettingsButtonClicked(event);
        else if(componentId.startsWith("GoatsBot-Poll-Entry-")) {
            event.deferEdit().queue();

            computeVote(componentId.split("-")[3], event.getUser());
            updateMessage(event.getChannel().asTextChannel());
        }
    }

    private void computeVote(String buttonId, User user) {
        try {
            final int index = Integer.parseInt(buttonId);
            final boolean containedBefore = entries[index].getUsers().contains(user);

            for(PollEntry entry : entries)
                entry.getUsers().remove(user);

            if(!containedBefore)
                entries[index].getUsers().add(user);
        }catch(NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /*
       Poll Message
     */

    protected void createMessage(IReplyCallback callback) {
        if(messageId != null)
            throw new IllegalStateException("Attempted to create an already created message !");

        callback.replyEmbeds(createEmbed()).setComponents(getComponents()).queue(hook -> hook.retrieveOriginal().queue(message -> {
            guildId = message.getGuildId();
            channelId = message.getChannelId();
            messageId = message.getId();

            Main.goatsBot.getPollManager().registerPoll(this);
        }));
    }

    protected void updateMessage(TextChannel channel) {
        if(messageId == null)
            throw new IllegalStateException("Attempted to update a non existant message");

        channel.retrieveMessageById(messageId).queue(message -> message.editMessageEmbeds(createEmbed()).queue());
    }

    @Nonnull
    protected abstract MessageEmbed createEmbed();

    @Nullable
    protected MessageEmbed createCloseEmbed() {
        return null;
    }

    protected LayoutComponent[] getComponents() {
        int rows = entries.length / 4 + (entries.length % 4 == 0 ? 0 : 1);
        if(rows > 4)
            rows = 4;

        final LayoutComponent[] components = new LayoutComponent[rows];
        for (int i = 0; i < rows; i++) {
            final List<ItemComponent> itemComponents = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                final int index = i * 4 + j;

                if(index < entries.length)
                    itemComponents.add(entries[index].toButton(index));
            }

            if(i == rows - 1)
                itemComponents.add(Button.secondary("GoatsBot-Poll-Settings", Emoji.fromUnicode("⚙️")));

            components[i] = ActionRow.of(itemComponents);
        }

        return components;
    }

    protected void addResultFields(EmbedBuilder embed) {
        for(int i = 0; i < entries.length; i++) {
            final PollEntry entry = entries[i];
            final Set<User> users = entry.getUsers();

            final StringBuilder value = new StringBuilder();
            if(users.isEmpty())
                value.append("> \u200E");
            else
                users.forEach(user -> value.append("> " + user.getAsMention() + "\n"));

            embed.addField(entry.getFormattedIcon() + " " + entry.getTitle() + " (" + users.size() + ")", value.toString(), true);

            if(i != entries.length - 1)
                embed.addBlankField(true);
        }
    }

    /*
       Settings
     */

    protected void onOpenSettingsButtonClicked(@Nonnull ButtonInteractionEvent event) {
        event.replyEmbeds(new GoatsEmbed()
                .setTitle("Sondage - Paramètres")
                .setDescription(
                        "**Boutons:**\n" +
                        "> \uD83D\uDD12️ *: Ferme le sondage*"
                )
                .setColor(GoatsEmbed.NEUTRAL_COLOR).build()).setEphemeral(true).setActionRow(
                        Button.danger("GoatsBot-Poll-Settings-Delete-" + messageId, Emoji.fromUnicode("\uD83D\uDD12"))
                ).queue();
    }

    /**
     * Only work with this layout: GoatsBot-Poll-Settings-BUTTON_NAME-messageId
     */
    protected void onSettingsButtonClicked(@Nonnull ButtonInteractionEvent event, String componentId) {
        if(!componentId.equals("GoatsBot-Poll-Settings-Delete"))
            return;

        event.editMessageEmbeds(new GoatsEmbed()
                .setTitle("Sondage - Paramètres")
                .setDescription("Le sondage a été fermé !")
                .setColor(GoatsEmbed.NEUTRAL_COLOR)
                .build()).setComponents().queue();

        close(event.getJDA());
    }
}
