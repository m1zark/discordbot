package com.m1zark.discordbot.config;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.m1zark.discordbot.DiscordBot;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Config {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;
    public static boolean ENABLE_SPAWN_LEGENDARY;
    public static boolean ENABLE_SPAWN_MEGA;
    public static boolean ENABLE_SPAWN_AURA;
    public static boolean ENABLE_WONDERTRADE;
    public static boolean ENABLE_BREED_EVENT;
    public static boolean ENABLE_SPAWN_ULTRA;
    public static boolean ENABLE_SERVER_CHAT;
    public static String BOT_TOKEN;
    public static String BOT_GAME_STATUS;

    public static boolean ENABLE_STAFF_CHAT;
    public static long CHAT_DISPLAY_TIME;
    public static String CHAT_FORMAT;

    public static String MAIN_CHANNEL;
    public static String CHAT_CHANNEL;
    public static String SPAWN_CHANNEL;
    public static String EVENTS_CHANNEL;
    public static String BOT_CHANNEL;
    public static String SUPPORT_CHANNEL;
    public static String TRADE_CHANNEL;
    public static String STAFF_CHANNEL;

    public Config() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(DiscordBot.getInstance().getConfigDir() + "/settings.conf");
        loader = (HoconConfigurationLoader.builder().setPath(configFile)).build();
        try {
            if (!Files.exists(DiscordBot.getInstance().getConfigDir())) {
                Files.createDirectory(DiscordBot.getInstance().getConfigDir());
            }
            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
            }
            if (main == null) {
                main = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            }
            CommentedConfigurationNode notifications = main.getNode("notifications");
            CommentedConfigurationNode settings = main.getNode("settings");
            CommentedConfigurationNode messages = main.getNode("messages");
            settings.getNode("bot", "token").getString("NDgxMTY5NTgxNzkzMjE0NDc0.Dl36AA.qDN9VtUjwrcVJ0ezkL2ulSnnPVE");
            settings.getNode("bot", "game-status").getString("with depression");
            settings.getNode("server-chat", "display-format").getString("{team}{topgroup}{prefix} {nick} : {message}");
            settings.getNode("server-chat", "enable").getBoolean(true);
            settings.getNode("server-chat", "display-time").getLong(0);
            settings.getNode("server-chat", "enable-staff-chat").getBoolean(false);
            settings.getNode("channels", "main-channel").getString("n/a");
            settings.getNode("channels", "spawn-channel").getString("n/a");
            settings.getNode("channels", "events-channel").getString("n/a");
            settings.getNode("channels", "bot-commands").getString("n/a");
            settings.getNode("channels", "support-channel").getString("n/a");
            settings.getNode("channels", "announcements-channel").getString("n/a");
            settings.getNode("channels", "trade-channel").getString("n/a");
            settings.getNode("channels", "server-chat-channel").getString("n/a");
            settings.getNode("channels", "staff-channel").getString("n/a");
            settings.getNode("verify", "required-ranks").getList(TypeToken.of(String.class), Lists.newArrayList("rookie", "novice", "expert", "ace", "elite", "master", "mystic", "legendary"));
            settings.getNode("verify", "discord-role").getString("485548445206315020");
            messages.getNode("server-restarting").getString("Restarting server... don't freak out!");
            messages.getNode("legend-spawn-command").getString("The last legend to spawn was a {pokemon}, which spawned {time} ago.");
            messages.getNode("trainer-rank").getString("I would suggest you check out [this wiki page](https://www.miragecraft.xyz/wiki/m/38451738/page/Upgrading_to_Trainer).");
            messages.getNode("verify", "invalid-name").getString("I wasn't able to find a player with that name... maybe you typed it in wrong.");
            messages.getNode("verify", "already-linked").getString("You already linked your account... going back to sleep now.");
            messages.getNode("verify", "dm-message").getString("I sent you a DM with more information");
            messages.getNode("verify", "invalid-rank").getString("You don't seem to have the required rank. You need to have at least the {rank} rank.");
            messages.getNode("verify", "dms", "ingame-message").getString("I sent you a message in game. You need to click that in order to fully verify your account.");
            messages.getNode("verify", "dms", "logged-into-server").getString("You need to be logged onto the server before you can verify your account.");
            messages.getNode("verify", "dms", "link-success").getString("Your account has been linked... enjoy your new {role} role!");
            messages.getNode("verify", "dms", "already-linked").getString("Your account has already been verified.");
            notifications.getNode("legendary-spawns", "enable").getBoolean(true);
            notifications.getNode("legendary-spawns", "message", "color").getString("#AA00AA");
            notifications.getNode("mega-spawns", "enable").getBoolean(true);
            notifications.getNode("mega-spawns", "message", "color").getString("#AA00AA");
            notifications.getNode("ultra-spawns", "enable").getBoolean(true);
            notifications.getNode("ultra-spawns", "message", "color").getString("#AA00AA");
            notifications.getNode("aura-spawns", "enable").getBoolean(true);
            notifications.getNode("aura-spawns", "message", "color").getString("#FFFF00");
            notifications.getNode("wondertrade", "enable").getBoolean(true);
            notifications.getNode("wondertrade", "message", "color").getString("#29cc5f");
            notifications.getNode("breed-event", "enable").getBoolean(true);
            notifications.getNode("breed-event", "start", "message", "color").getString("#29cc5f");
            notifications.getNode("breed-event", "end", "message", "color").getString("#29cc5f");
            loader.save(main);
        }
        catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
            return;
        }
        this.load();
    }

    public static void saveConfig() {
        try {
            loader.save((ConfigurationNode)main);
        }
        catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = (CommentedConfigurationNode)loader.load();
            this.load();
        }
        catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    private void load() {
        BOT_TOKEN = main.getNode("settings", "bot", "token").getString();
        BOT_GAME_STATUS = main.getNode("settings", "bot", "game-status").getString();
        MAIN_CHANNEL = main.getNode("settings", "channels", "main-channel").getString();
        SPAWN_CHANNEL = main.getNode("settings", "channels", "spawn-channel").getString();
        EVENTS_CHANNEL = main.getNode("settings", "channels", "events-channel").getString();
        BOT_CHANNEL = main.getNode("settings", "channels", "bot-commands").getString();
        SUPPORT_CHANNEL = main.getNode("settings", "channels", "support-channel").getString();
        TRADE_CHANNEL = main.getNode("settings", "channels", "trade-channel").getString();
        CHAT_CHANNEL = main.getNode("settings", "channels", "server-chat-channel").getString();
        STAFF_CHANNEL = main.getNode("settings", "channels", "staff-channel").getString();

        ENABLE_STAFF_CHAT = main.getNode("settings", "server-chat", "enable-staff-chat").getBoolean();
        CHAT_DISPLAY_TIME = main.getNode("settings", "server-chat", "display-time").getLong();
        ENABLE_SERVER_CHAT = main.getNode("settings", "server-chat", "enable").getBoolean();
        CHAT_FORMAT = main.getNode("settings", "server-chat", "display-format").getString();

        ENABLE_SPAWN_LEGENDARY = main.getNode("notifications", "legendary-spawns", "enable").getBoolean();
        ENABLE_SPAWN_ULTRA = main.getNode("notifications", "ultra-spawns", "enable").getBoolean();
        ENABLE_SPAWN_MEGA = main.getNode("notifications", "mega-spawns", "enable").getBoolean();
        ENABLE_SPAWN_AURA = main.getNode("notifications", "aura-spawns", "enable").getBoolean();
        ENABLE_WONDERTRADE = main.getNode("notifications", "wondertrade", "enable").getBoolean();
        ENABLE_BREED_EVENT = main.getNode("notifications", "breed-event", "enable").getBoolean();
    }

    public static MessageEmbed spawnMessage(String id, String pokemon, String form, String biome, String aura) {
        EmbedBuilder eb = new EmbedBuilder();

        if (!main.getNode("notifications", id, "message", "color").isVirtual()) {
            eb.setColor(Color.decode(main.getNode("notifications", id, "message", "color").getString()));
        }
        if (!main.getNode("notifications", id, "message", "title").isVirtual()) {
            eb.setTitle(main.getNode("notifications", id, "message", "title").getString().replace("{pokemon}", pokemon).replace("{biome}", biome).replace("{aura}", aura));
        }
        if (!main.getNode("notifications", id, "message", "description").isVirtual()) {
            eb.setDescription(main.getNode("notifications", id, "message", "description").getString().replace("{pokemon}", pokemon).replace("{biome}", biome).replace("{aura}", aura));
        }
        if (!main.getNode("notifications", id, "message", "thumbnail").isVirtual()) {
            eb.setThumbnail(main.getNode("notifications", id, "message", "thumbnail").getString().replace("{pokemon}", pokemon.toLowerCase()).replace("{form}", form));
        }
        if (!main.getNode("notifications", id, "message", "image").isVirtual()) {
            eb.setImage(main.getNode("notifications", id, "message", "image").getString().replace("{pokemon}", pokemon.toLowerCase()).replace("{form}", form));
        }
        if (!main.getNode("notifications", id, "message", "timestamp").isVirtual() && main.getNode(new Object[]{"notifications", id, "message", "timestamp"}).getBoolean()) {
            eb.setTimestamp(Instant.now());
        }
        if (!main.getNode("notifications", id, "message", "fields").isVirtual()) {
            for (int i = 0; i < main.getNode("notifications", id, "message", "fields").getChildrenList().size(); ++i) {
                CommentedConfigurationNode field = main.getNode("notifications", id, "message", "fields").getChildrenList().get(i);
                eb.addField(field.getNode("name").getString().replace("{pokemon}", pokemon).replace("{biome}", biome).replace("{aura}", aura), field.getNode("value").getString().replace("{pokemon}", pokemon).replace("{biome}", biome).replace("{aura}", aura), !(field = main.getNode("notifications", id, "message", "fields").getChildrenList().get(i)).getNode("inline").isVirtual() && field.getNode("inline").getBoolean());
            }
        }
        if (!main.getNode("notifications", id, "footer").isVirtual()) {
            eb.setFooter(main.getNode("notifications", id, "message", "footer", "text").getString(), main.getNode("notifications", id, "message", "footer", "icon").getString());
        }
        return eb.build();
    }

    public static MessageEmbed wtMessage(String id, String player, String type, String pokemon) {
        EmbedBuilder eb = new EmbedBuilder();
        if (!main.getNode("notifications", id, "message", "color").isVirtual()) {
            eb.setColor(Color.decode(main.getNode("notifications", id, "message", "color").getString()));
        }
        if (!main.getNode("notifications", id, "message", "title").isVirtual()) {
            eb.setTitle(main.getNode("notifications", id, "message", "title").getString().replace("{pokemon}", pokemon).replace("{type}", type).replace("{player}", player));
        }
        if (!main.getNode("notifications", id, "message", "description").isVirtual()) {
            eb.setDescription(main.getNode("notifications", id, "message", "description").getString().replace("{pokemon}", pokemon).replace("{type}", type).replace("{player}", player));
        }
        if (!main.getNode("notifications", id, "message", "thumbnail").isVirtual()) {
            eb.setThumbnail(main.getNode("notifications", id, "message", "thumbnail").getString().replace("{pokemon}", pokemon.toLowerCase()));
        }
        if (!main.getNode("notifications", id, "message", "image").isVirtual()) {
            eb.setImage(main.getNode("notifications", id, "message", "image").getString().replace("{pokemon}", pokemon.toLowerCase()));
        }
        if (!main.getNode("notifications", id, "message", "timestamp").isVirtual() && main.getNode("notifications", id, "message", "timestamp").getBoolean()) {
            eb.setTimestamp(Instant.now());
        }
        if (!main.getNode("notifications", id, "message", "fields").isVirtual()) {
            for (int i = 0; i < main.getNode("notifications", id, "message", "fields").getChildrenList().size(); ++i) {
                CommentedConfigurationNode field = main.getNode("notifications", id, "message", "fields").getChildrenList().get(i);
                eb.addField(field.getNode("name").getString().replace("{pokemon}", pokemon).replace("{type}", type).replace("{player}", player), field.getNode("value").getString().replace("{pokemon}", pokemon).replace("{type}", type).replace("{player}", player), !(field = main.getNode("notifications", id, "message", "fields").getChildrenList().get(i)).getNode("inline").isVirtual() && field.getNode("inline").getBoolean());
            }
        }
        if (!main.getNode("notifications", id, "footer").isVirtual()) {
            eb.setFooter(main.getNode("notifications", id, "message", "footer", "text").getString(), main.getNode("notifications", id, "message", "footer", "icon").getString());
        }
        return eb.build();
    }

    public static MessageEmbed breedMessage(String id, String ends) {
        EmbedBuilder eb = new EmbedBuilder();
        if (!main.getNode("notifications", "breed-event", id, "message", "color").isVirtual()) {
            eb.setColor(Color.decode(main.getNode("notifications", "breed-event", id, "message", "color").getString()));
        }
        if (!main.getNode("notifications", "breed-event", id, "message", "title").isVirtual()) {
            eb.setTitle(main.getNode("notifications", "breed-event", id, "message", "title").getString().replace("{ends}", ends));
        }
        if (!main.getNode("notifications", "breed-event", id, "message", "description").isVirtual()) {
            eb.setDescription(main.getNode("notifications", "breed-event", id, "message", "description").getString().replace("{ends}", ends));
        }
        if (!main.getNode("notifications", "breed-event", id, "message", "thumbnail").isVirtual()) {
            eb.setThumbnail(main.getNode(new Object[]{"notifications", "breed-event", id, "message", "thumbnail"}).getString());
        }
        if (!main.getNode("notifications", "breed-event", id, "message", "image").isVirtual()) {
            eb.setImage(main.getNode(new Object[]{"notifications", "breed-event", id, "message", "image"}).getString());
        }
        if (!main.getNode("notifications", "breed-event", id, "message", "timestamp").isVirtual() && main.getNode("notifications", "breed-event", id, "message", "timestamp").getBoolean()) {
            eb.setTimestamp(Instant.now());
        }
        if (!main.getNode("notifications", "breed-event", id, "message", "fields").isVirtual()) {
            for (int i = 0; i < main.getNode("notifications", "breed-event", id, "message", "fields").getChildrenList().size(); ++i) {
                CommentedConfigurationNode field = main.getNode("notifications", "breed-event", id, "message", "fields").getChildrenList().get(i);
                eb.addField(field.getNode(new Object[]{"name"}).getString().replace("{ends}", ends), field.getNode(new Object[]{"value"}).getString().replace("{ends}", ends), !(field = main.getNode("notifications", "breed-event", id, "message", "fields").getChildrenList().get(i)).getNode("inline").isVirtual() && field.getNode("inline").getBoolean());
            }
        }
        if (!main.getNode("notifications", "breed-event", id, "footer").isVirtual()) {
            eb.setFooter(main.getNode(new Object[]{"notifications", "breed-event", id, "message", "footer", "text"}).getString(), main.getNode(new Object[]{"notifications", "breed-event", id, "message", "footer", "icon"}).getString());
        }
        return eb.build();
    }

    public static String getMessages(String path) {
        return main.getNode((Object[])path.split("\\.")).getString();
    }

    public static String verifyInfo(String type) {
        return main.getNode(new Object[]{"settings", "verify", type}).getString();
    }

    public static List<String> verifyRanks() {
        try {
            return main.getNode("settings", "verify", "required-ranks").getList(TypeToken.of(String.class));
        }
        catch (ObjectMappingException e) {
            return Lists.newArrayList();
        }
    }
}


