package com.m1zark.discordbot.tasks;

import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.config.Config;
import com.m1zark.discordbot.utils.SpongeUtils;
import org.spongepowered.api.scheduler.Task;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Consumer;

public class VoteCheck implements Consumer<Task> {
    @Override public void accept(Task task) {
        DiscordBot.getInstance().getAsync().execute(() -> { if(checkDate()) SpongeUtils.topVoters(Config.STAFF_CHANNEL); });
    }

    private boolean checkDate() {
        ZonedDateTime estTime = ZonedDateTime.now(ZoneId.of("US/Eastern"));
        ZonedDateTime lastDayofCurrentMonth = estTime.with(TemporalAdjusters.lastDayOfMonth());
        return estTime.isEqual(lastDayofCurrentMonth) && (estTime.getHour() == 23 && estTime.getMinute() == 59);
    }
}
