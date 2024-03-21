package net.vadamdev.goatsbot.poll.system;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author VadamDev
 * @since 19/03/2024
 */
public interface ITimeLimitedPoll {
    @Nullable
    Date getDeadline();
}
