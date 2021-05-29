package com.m1zark.discordbot.config;

import com.m1zark.discordbot.DiscordBot;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Wiki {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    private static CommentedConfigurationNode main;

    public Wiki() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(DiscordBot.getInstance().getConfigDir() + "/wiki.conf");
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
            CommentedConfigurationNode wiki = main.getNode(new Object[]{"wiki"});
            wiki.setComment("Add new info using \"[phrase]\"=[Text to Display] ");
            wiki.getNode("money").getString("Looking for this? https://www.miragecraft.xyz/wiki/m/38451738/page/Getting_Money");
            wiki.getNode("gyms").getString("Looking for this? https://www.miragecraft.xyz/wiki/m/38451738/page/Gyms");
            loader.save(main);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            loader.save(main);
        }
        catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = loader.load();
        }
        catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public static String getInfo(String phrase) {
        return main.getNode("wiki", phrase).isVirtual() ? "empty" : main.getNode("wiki", phrase).getString();
    }
}
