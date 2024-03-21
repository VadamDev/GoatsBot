package net.vadamdev.goatsbot.listeners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.goatsbot.GoatsBot;
import net.vadamdev.goatsbot.Main;
import net.vadamdev.goatsbot.poll.CustomPoll;

import javax.annotation.Nonnull;

/**
 * @author VadamDev
 * @since 18/03/2024
 */
public class EventListener extends ListenerAdapter {
    private final GoatsBot goatsBot;

    public EventListener() {
        this.goatsBot = Main.goatsBot;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        goatsBot.getPollManager().handleButtonInteractionEvent(event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        CustomPoll.onModalInteraction(event);
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        goatsBot.getPollManager().unregisterPoll(event.getMessageId());
    }

    @Override
    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(messageId -> goatsBot.getPollManager().unregisterPoll(messageId));
    }
}
