package com.m1zark.discordbot.config;

import com.m1zark.discordbot.DiscordBot;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Data {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;

    public Data() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(DiscordBot.getInstance().getConfigDir() + "/accounts.conf");
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
            CommentedConfigurationNode accounts = main.getNode("Accounts");

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

    public static boolean addAccount(String discord, String minecraft) {
        if(main.getNode("Accounts", minecraft).isVirtual()) {
            main.getNode("Accounts", minecraft).setValue(discord);
            saveConfig();

            return true;
        }

        return false;
    }

    public static HashMap<String,String> getAllAccounts() {
        HashMap<String,String> accounts = new HashMap<>();
        if(!main.getNode("PlayerData").isVirtual()) {
            main.getNode("Accounts").getChildrenMap().forEach((minecraft, discord) -> accounts.put((String) minecraft, discord.getString()));
        }

        return accounts;
    }
}
