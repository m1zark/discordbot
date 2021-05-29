package com.m1zark.discordbot.utils;

import com.enjin.rpc.mappings.mappings.plugin.statistics.TopVoter;
import com.enjin.sponge.managers.StatSignManager;
import com.google.common.collect.Lists;
import com.m1zark.discordbot.DiscordBot;
import io.github.tsecho.poketeams.apis.PokeTeamsAPI;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

public class SpongeUtils {
    public static String getHighestGroup(Player player) {
        try {
            if (!Sponge.getGame().getServiceManager().getRegistration(PermissionService.class).isPresent()) return "";
            PermissionService ps = Sponge.getGame().getServiceManager().getRegistration(PermissionService.class).get().getProvider();
            HashMap<Integer, Subject> subs = new HashMap<>();
            for (SubjectReference sub : player.getParents()) {
                if (sub.getCollectionIdentifier().equals(ps.getGroupSubjects().getIdentifier()) && (sub.getSubjectIdentifier() != null)) {
                    Subject subj = sub.resolve().get();
                    subs.put(subj.getParents().size(), subj);
                }
            }
            return subs.isEmpty() ? "" : subs.get(Collections.max(subs.keySet())).getFriendlyIdentifier().isPresent() ? subs.get(Collections.max(subs.keySet())).getFriendlyIdentifier().get() : "";
        } catch (InterruptedException | ExecutionException e) {
            //
        }
        return "";
    }

    public static List<String> getAllGroups(Player player) {
        ArrayList<String> groups = Lists.newArrayList();
        try {
            for (SubjectReference sub : player.getParents()) {
                Subject subj = sub.resolve().get();
                groups.add(subj.getIdentifier());
            }
        }
        catch (InterruptedException | ExecutionException exception) {
            // empty catch block
        }
        return groups;
    }

    public static String getNick(Player p) {
        if (!Sponge.getPluginManager().getPlugin("nucleus").isPresent()) return p.getName();
        return NucleusAPI.getNicknameService()
                .map(s -> s.getNickname(p).map(Text::toPlain).orElse(null))
                .orElse(p.getName());
    }

    public static String getTeams(Player p) {
        PokeTeamsAPI teams = new PokeTeamsAPI(p);
        return teams.inTeam() ? teams.getFormattedTeamTag() : "";
    }

    public static void topVoters(String channel) {
        DiscordBot.getInstance().getAsync().execute(() -> {
            if(Sponge.getPluginManager().isLoaded("enjin-minecraft-plugin")) {
                if (StatSignManager.fetchStats()) {
                    List<TopVoter> voters = StatSignManager.getStats().getTopVotersMonth();

                    try {
                        DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage("Top Voters for " + ZonedDateTime.now(ZoneId.of("US/Eastern")).getMonth().name()).queue();
                        for (int i = 0; i < 10; ++i) {
                            DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage(((voters.get(i)).getName() + ": " + (voters.get(i)).getCount() + " Votes")).queue();
                        }
                    } catch (NullPointerException i) {
                        DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage("Sorry, but I was unable to grab the voter data...").queue();
                    }
                }
            } else {
                DiscordBot.getInstance().getJda().getTextChannelById(channel).sendMessage("Enjin plugin doesn't seem to be loaded... unable to run this command.").queue();
            }
        });
    }
}
