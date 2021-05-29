package com.m1zark.discordbot.config;

import com.google.common.collect.Lists;
import com.m1zark.discordbot.DiscordBot;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Messages {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;

    private List<String> randomSpeech = Lists.newArrayList("Dunno why I take orders from you.","Sure why not... I got nothing better todo.","Yawn... what? Oh fine here... ZzZzzzzzz");
    private List<String> getRandomSpeech_2 = Lists.newArrayList("Sorry I had my airpods in... did you say something?","I don't take orders from you.");

    public Messages() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(DiscordBot.getInstance().getConfigDir() + "/messages.conf");
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
            CommentedConfigurationNode messages = main.getNode("Messages");
            messages.getNode("random-responses").setValue(randomSpeech);
            messages.getNode("random-no-response").setValue(getRandomSpeech_2);

            loader.save(main);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.load();
    }

    public static void saveConfig() {
        try {
            loader.save(main);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = loader.load();
            this.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {

    }
}
