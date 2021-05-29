package com.m1zark.discordbot.utils;

import com.google.common.collect.Lists;
import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.config.Config;
import com.m1zark.discordbot.utils.SpongeUtils;
import java.lang.invoke.LambdaMetafactory;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class DiscordUtils {
    public static boolean isValidChannel(String channel) {
        if (DiscordBot.getInstance().getJda() == null) {
            return false;
        }
        if (DiscordBot.getInstance().getJda().getStatus() != JDA.Status.CONNECTED) {
            return false;
        }
        if (DiscordBot.getInstance().getJda().getTextChannelById(channel) == null) {
            DiscordBot.getInstance().getLogger().error("The channel " + channel + " defined in the config isn't a valid Discord Channel ID!");
            DiscordBot.getInstance().getLogger().error("Replace it with a valid one then reload the plugin!");
            return false;
        }
        return true;
    }

    public static boolean checkMemberRole(Member m, String role) {
        return m.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(role));
    }

    public static void sendMessageToChannel(String channel, String message) {
        if (!DiscordUtils.isValidChannel(channel)) {
            return;
        }
        ArrayList<String> usersMentioned = Lists.newArrayList();
        Arrays.stream(message.split(" ")).filter(word -> word.startsWith("@")).forEach(mention -> usersMentioned.add(mention.substring(1)));
        if (!usersMentioned.isEmpty()) {
            for (String user : usersMentioned) {
                List users = DiscordBot.getInstance().getJda().getUsersByName(user, true);
                if (users.isEmpty()) continue;
                message = message.replaceAll("@" + user, "<@" + ((User)users.get(0)).getId() + ">");
            }
        }
        DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage(message.replaceAll("&([0-9a-fA-FlLkKrR])", "")).queue();
    }

    public static void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(content).queue());
    }

    public static void sendMessageToDiscord(String channel, Map<String, String> placeholders, long deleteTime) {
        String message = ReplacerUtil.replaceEach(Config.CHAT_FORMAT, placeholders);

        if(deleteTime == 0) {
            DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage("`" + message + "`").queue();
        } else {
            DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage("`" + message + "`").queue(m -> m.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        }
    }

    public static void dispatchList(Message m, MessageChannel c) {
        String msg;
        StringBuilder players = new StringBuilder();
        ArrayList<Player> cplayers = new ArrayList<>();
        Sponge.getServer().getOnlinePlayers().forEach(p -> {
            if (!p.get(Keys.VANISH).orElse(false)) {
                cplayers.add(p);
            }
        });
        if (cplayers.size() == 0) {
            msg = "'There are no players online!'";
        } else {
            String listformat = "%player%";
            cplayers.sort(Comparator.comparing(Player::getName));
            for (Player player : cplayers) {
                players.append(listformat.replace("%player%", SpongeUtils.getNick(player)).replace("%topgroup%", SpongeUtils.getHighestGroup(player)).replace("%prefix%", player.getOption("prefix").orElse(""))).append(", ");
            }
            players = new StringBuilder(players.substring(0, players.length() - 2));
            msg = "**Players online (" + Sponge.getServer().getOnlinePlayers().size() + "/" + Sponge.getServer().getMaxPlayers() + "):** " + "```" + players + "```";
        }
        DiscordUtils.sendMessageToChannel(c.getId(), msg);
    }
}
