package com.m1zark.discordbot.listeners;

import com.m1zark.discordbot.config.Config;
import com.m1zark.discordbot.utils.DiscordUtils;
import com.m1zark.discordbot.utils.SpongeUtils;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.chat.NucleusChatChannel;
import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

public class ChatListener {
    @Listener(order=Order.LAST)
    public void onSpongeMessage(MessageChannelEvent.Chat e, @First Player p) {
        if(Config.ENABLE_SERVER_CHAT) {
            if (!Sponge.getServer().getOnlinePlayers().contains(p) || e.isMessageCancelled()) {
                return;
            }

            if(Sponge.getPluginManager().getPlugin("nucleus").isPresent()) {
                if(NucleusAPI.getStaffChatService().isPresent()) {
                    if (e.getChannel().get() instanceof NucleusChatChannel.StaffChat && !Config.ENABLE_STAFF_CHAT) return;
                }
            }

            if (e.getChannel().isPresent()) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("{unity}", checkUnity(p).toPlain());
                placeholders.put("{team}", SpongeUtils.getTeams(p).replaceAll("&[0-9a-z]",""));
                placeholders.put("{prefix}", p.getOption("prefix").orElse("").replaceAll("&[0-9a-z]",""));
                placeholders.put("{suffix}", p.getOption("suffix").orElse("").replaceAll("&[0-9a-z]",""));
                placeholders.put("{player}", p.getName());
                placeholders.put("{message}", e.getFormatter().getBody().toText().toPlain());
                placeholders.put("{topgroup}", SpongeUtils.getHighestGroup(p).replaceAll("&[0-9a-z]",""));
                placeholders.put("{nick}", SpongeUtils.getNick(p));

                DiscordUtils.sendMessageToDiscord(Config.CHAT_CHANNEL, placeholders, Config.CHAT_DISPLAY_TIME);
            }
        }
    }

    private Text checkUnity(Player player) {
        if(Sponge.getPluginManager().getPlugin("nucleus").isPresent()) {
            NucleusMessageTokenService messageService = NucleusAPI.getMessageTokenService();
            return messageService.parseToken("{{pl:unity:marry}}", player).orElse(Text.of(""));
        }

        return Text.of("");
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join event, @First Player p) {
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event, @First Player p) {
    }
}
