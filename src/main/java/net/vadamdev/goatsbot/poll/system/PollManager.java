package net.vadamdev.goatsbot.poll.system;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.vadamdev.goatsbot.Main;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author VadamDev
 * @since 17/03/2024
 */
public class PollManager {
    private final Map<String, AbstractPoll> polls;

    private final ScheduledExecutorService executorService;

    public PollManager() {
        this.polls = new HashMap<>();

        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(this::checkTimeLimitedPolls, 5, 5, TimeUnit.MINUTES);
    }

    /*
       Handle Events
     */

    public void handleButtonInteractionEvent(@Nonnull ButtonInteractionEvent event) {
        final String componentId = event.getComponentId();
        if(componentId.startsWith("GoatsBot-Poll-Settings-")) {
            final String[] split = componentId.split("-");
            if(split.length != 5)
                return;

            getPollByMessageId(split[4]).ifPresent(poll -> {
                final StringBuilder strippedComponentId = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    if(i == 4)
                        continue;

                    if(i != split.length - 2)
                        strippedComponentId.append(split[i] + "-");
                    else
                        strippedComponentId.append(split[i]);
                }

                poll.onSettingsButtonClicked(event, strippedComponentId.toString());
            });

            return;
        }

        getPollByMessageId(event.getMessageId())
                .ifPresent(poll -> poll.handleButtonInteractionEvent(event));
    }

    /*
       Register / Get
     */

    public void registerPoll(AbstractPoll poll) {
        if(polls.containsKey(poll.messageId))
            return;

        polls.put(poll.messageId, poll);
    }

    public void unregisterPoll(String messageId) {
        if(!polls.containsKey(messageId))
            return;

        polls.remove(messageId);
    }

    public Optional<AbstractPoll> getPollByMessageId(String messageId) {
        return Optional.ofNullable(polls.get(messageId));
    }

    /*
       Time-Limited Polls
     */

    private void checkTimeLimitedPolls() {
        if(polls.isEmpty())
            return;

        final Date currentDate = new Date();
        final JDA jda = Main.goatsBot.getJda();

        polls.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof ITimeLimitedPoll)
                .forEach(entry -> {
                    final AbstractPoll poll = entry.getValue();
                    final Date deadline = ((ITimeLimitedPoll) poll).getDeadline();

                    if(deadline != null && currentDate.after(deadline))
                        poll.close(jda);
                });
    }

    public void onDisable() {
        executorService.shutdownNow();
    }
}
