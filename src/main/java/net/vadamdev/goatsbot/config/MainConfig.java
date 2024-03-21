package net.vadamdev.goatsbot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.vadamdev.jdautils.configuration.ConfigValue;
import net.vadamdev.jdautils.configuration.Configuration;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author VadamDev
 * @since 17/03/2024
 */
public class MainConfig extends Configuration {
    /*
       Activity
     */

    @ConfigValue(path = "activity.activityType")
    public String ACTIVITY_TYPE = null;

    @ConfigValue(path = "activity.activity")
    public String ACTIVITY = null;

    public MainConfig() {
        super("./config.yml");
    }

    @Nullable
    public Activity formatActivity() {
        if(ACTIVITY_TYPE == null || ACTIVITY == null)
            return null;

        return Activity.of(Activity.ActivityType.valueOf(ACTIVITY_TYPE), ACTIVITY);
    }

    public void updateActivity(JDA jda, @Nullable Activity.ActivityType activityType, @Nullable String activity) throws IOException {
        setValue("ACTIVITY_TYPE", activityType != null ? activityType.name() : null);
        setValue("ACTIVITY", activity);
        save();

        jda.getPresence().setActivity(formatActivity());
    }
}
