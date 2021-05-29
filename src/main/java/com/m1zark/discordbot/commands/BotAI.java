package com.m1zark.discordbot.commands;

import com.google.common.collect.Lists;
import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.utils.DiscordUtils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotAI extends ListenerAdapter {
    //List<String> randomSpeech = Lists.newArrayList("Dunno why I take orders from you.","Sure why not... I got nothing better todo.","Yawn... what? Oh fine here... ZzZzzzzzz");

    public void onMessageReceived(MessageReceivedEvent e) {
        Task.builder().execute(task -> this.process(e)).submit(DiscordBot.getInstance());
    }

    private void process(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            Message message = e.getMessage();
            MessageChannel channel = e.getChannel();
            String content = message.getContentDisplay();

            if (channel.getType() == ChannelType.TEXT) {
                if (content.toLowerCase().startsWith(DiscordBot.getInstance().getJda().getSelfUser().getName().toLowerCase())) {
                    if(content.toLowerCase().contains("chatot") && this.checkPattern(content.toLowerCase(), "love|better")) {
                        //DiscordUtils.sendMessageToChannel(channel.getId(), randomSpeech.get(new Random().nextInt(randomSpeech.size()-1)));
                    }
                }
            }
        }
    }

    private boolean checkPattern(String message, String pattern) {
        Pattern p = Pattern.compile(pattern, 2);
        Matcher m = p.matcher(message);
        int count = 0;
        while (m.find()) ++count;

        return count >= 2;
    }
}