package com.m1zark.discordbot.tasks;

import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.utils.Giveaway.Giveaway;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

public class Giveaways implements Consumer<Task> {
    private int seconds = 60;

    @Override public void accept(Task task) {
        DiscordBot.getInstance().getAsync().execute(() -> {
            if(DiscordBot.getInstance().getGiveaway() != null) {

            }
        });
    }
}
