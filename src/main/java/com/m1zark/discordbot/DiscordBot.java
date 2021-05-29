package com.m1zark.discordbot;

import com.google.inject.Inject;
import com.m1zark.discordbot.commands.BotAI;
import com.m1zark.discordbot.commands.DiscordCommands;
import com.m1zark.discordbot.config.Config;
import com.m1zark.discordbot.config.Data;
import com.m1zark.discordbot.config.Wiki;
import com.m1zark.discordbot.listeners.ChatListener;
import com.m1zark.discordbot.listeners.PixelmonListeners;
import com.m1zark.discordbot.listeners.PluginListener;
import com.m1zark.discordbot.tasks.VoteCheck;
import com.m1zark.discordbot.utils.Giveaway.Giveaway;
import com.pixelmonmod.pixelmon.Pixelmon;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

@Getter
@Plugin(id="discordbot", name="DiscordBot", description="server end of a discord bot", authors={"m1zark"})
public class DiscordBot {
    @Inject private Logger logger;
    @Inject @ConfigDir(sharedRoot=false)
    private Path configDir;
    private Config config;
    private Data accounts;
    private Wiki wikiConfig;
    //private Messages messages;
    private static DiscordBot instance;
    private JDA jda;
    private SpongeExecutorService sync;
    private SpongeExecutorService async;
    public Map<Player, Member> verifyAccount = new HashedMap<>();
    @Setter public Giveaway giveaway =  null;

    @Listener
    public void onServerStart(GamePreInitializationEvent event) {
        instance = this;
        this.config = new Config();
        this.wikiConfig = new Wiki();
        this.accounts = new Data();
        //this.messages = new Messages();
        this.sync = Sponge.getScheduler().createSyncExecutor(this);
        this.async = Sponge.getScheduler().createAsyncExecutor(this);
        Pixelmon.EVENT_BUS.register(new PixelmonListeners());
        Sponge.getEventManager().registerListeners(this, new PluginListener());
        Sponge.getEventManager().registerListeners(this, new ChatListener());
        try {
            this.jda = JDABuilder.createDefault(Config.BOT_TOKEN)
                    .addEventListeners(new DiscordCommands(), new BotAI())
                    .setActivity(Activity.playing(Config.BOT_GAME_STATUS))
                    .build();
        }
        catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Listener(order = Order.POST)
    public void postGameStart(GameStartedServerEvent event) {
        if(this.jda != null) {
            getConsole().ifPresent(console -> console.sendMessages(Text.of(DiscordBot.getInstance().getJda().getSelfUser().getName() + " is up and running!")));
            Task.builder().execute(new VoteCheck()).interval(1, TimeUnit.MINUTES).name("Vote Check Timer").async().submit(this);
        }
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        this.config.reload();
        this.wikiConfig.reload();
        this.accounts.reload();
        //this.messages.reload();
    }

    @Listener
    public void onServerStop(GameStoppingEvent event) {
        if (this.jda != null) {
            this.jda.getTextChannelById(Config.MAIN_CHANNEL).sendMessage(Config.getMessages("messages.server-restarting")).queue();
            this.jda.shutdown();
        }
        this.logger.info("Disconnecting from Discord...");
        if (this.jda != null && this.jda.getStatus() != JDA.Status.SHUTDOWN && this.jda.getStatus() != JDA.Status.SHUTTING_DOWN) {
            this.jda.shutdownNow();
        }
    }

    public static DiscordBot getInstance() {
        return instance;
    }

    public Optional<ConsoleSource> getConsole() {
        return Optional.ofNullable(Sponge.isServerAvailable() ? Sponge.getServer().getConsole() : null);
    }
}


