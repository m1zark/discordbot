package com.m1zark.discordbot.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.config.Config;
import com.m1zark.discordbot.config.Data;
import com.m1zark.discordbot.config.Wiki;
import com.m1zark.discordbot.tasks.Giveaways;
import com.m1zark.discordbot.utils.DiscordUtils;
import com.m1zark.discordbot.utils.Giveaway.Constants;
import com.m1zark.discordbot.utils.Giveaway.Giveaway;
import com.m1zark.discordbot.utils.Giveaway.Status;
import com.m1zark.discordbot.utils.SpongeUtils;
import com.m1zark.discordbot.utils.Giveaway.Utils;
import com.m1zark.pixelmoncommands.Config.ConfigManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class DiscordCommands extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {
        Task.builder().execute(task -> this.process(e)).submit(DiscordBot.getInstance());
    }

    private void process(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            Message message = e.getMessage();
            MessageChannel channel = e.getChannel();
            String content = message.getContentDisplay();

            if (channel.getType() == ChannelType.TEXT) {
                if(channel.getId().equals(Config.STAFF_CHANNEL)) {
                    if(DiscordUtils.checkMemberRole(e.getMember(), "admin") || DiscordUtils.checkMemberRole(e.getMember(), "assistant")) {
                        /*
                        if(content.startsWith("!gstart")) {
                            String[] parts = content.replace("!gstart ", "").split("\\s+", 2);

                            int seconds = Utils.parseShortTime(parts[0]);
                            if(seconds==-1) {
                                e.getMessage().editMessage("Failed to parse time from `"+parts[0]+"`"+EXAMPLE);
                                return;
                            }

                            int winners = 1;
                            String prize = null;

                            if(parts.length > 1) {
                                String[] parts2 = parts[1].split("\\s+", 2);
                                winners = Utils.parseWinners(parts2[0]);

                                if(winners == -1) {
                                    winners = 1;
                                    prize = parts[1];
                                } else {
                                   prize = parts2.length > 1 ? parts2[1] : null;
                                }
                            }

                            if(DiscordBot.getInstance().getGiveaway() != null) {
                                e.getMessage().editMessage("There is already a giveaway running.");
                                return;
                            }

                            try {
                                e.getMessage().delete().queue();
                            } catch(PermissionException ignore) {}

                            MessageChannel events = DiscordBot.getInstance().getJda().getTextChannelById(Config.EVENTS_CHANNEL);

                            Instant now = e.getMessage().getCreationTime().toInstant();
                            Instant end = now.plusSeconds(seconds);

                            Giveaway giveaway = new Giveaway(0, events.getIdLong(), end, winners, prize, Status.RUN);
                            Message msg = giveaway.render(Constants.BLURPLE, now);

                            events.sendMessage(msg).queue(m -> {
                                giveaway.setMessageId(m.getIdLong());
                                DiscordBot.getInstance().setGiveaway(giveaway);
                                m.addReaction(Constants.TADA).queue();
                            });

                            Task.builder().execute(new Giveaways()).interval(1, TimeUnit.SECONDS).name("Discord Giveaway Task").submit(DiscordBot.getInstance());
                        }

                         */

                        if(content.startsWith("!topvoters")) SpongeUtils.topVoters(Config.STAFF_CHANNEL);
                        if(content.startsWith("!play")) {
                            DiscordBot.getInstance().getJda().getPresence().setActivity(Activity.playing(content.replace("!play","")));
                        }
                        if(content.startsWith("!watch")) {
                            DiscordBot.getInstance().getJda().getPresence().setActivity(Activity.watching(content.replace("!watch","")));
                        }
                        if(content.startsWith("!listen")) {
                            DiscordBot.getInstance().getJda().getPresence().setActivity(Activity.listening(content.replace("!listen","")));
                        }
                    }
                }

                if(channel.getId().equals(Config.MAIN_CHANNEL)) {
                    if (content.toLowerCase().startsWith(DiscordBot.getInstance().getJda().getSelfUser().getName().toLowerCase())) {
                        if (this.checkPattern(content, "last|legend|spawn")) {
                            String msg;
                            if (Strings.isNullOrEmpty(ConfigManager.getLegendSpawn())) {
                                msg = "It doesn't look like any legends have spawned recently.";
                            } else {
                                String[] spawn = ConfigManager.getLegendSpawn().split(":");
                                long millis = System.currentTimeMillis() - Long.parseLong(spawn[1]);
                                long days = TimeUnit.MILLISECONDS.toDays(millis);
                                long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24L;
                                long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60L;
                                msg = Config.getMessages("messages.legend-spawn-command").replace("{pokemon}", spawn[0]).replace("{time}", String.format("%dd %dh %dm", days, hours, minutes));
                            }
                            DiscordUtils.sendMessageToChannel(channel.getId(), msg);
                        } else if (this.checkPattern(content, "how|trainer|rank")) {
                            DiscordUtils.sendMessageToChannel(channel.getId(), Config.getMessages("messages.trainer-rank"));
                        } else {
                            DiscordUtils.sendMessageToChannel(channel.getId(), "I don't take orders from you.");
                        }
                    }
                }

                if(channel.getId().equals(Config.TRADE_CHANNEL)) {
                    EmbedBuilder eb = new EmbedBuilder();

                    if(content.toLowerCase().startsWith("!lf") || content.toLowerCase().startsWith("!lookingfor")) {
                        eb.setColor(Color.CYAN);
                        String[] params = content.split(" ");

                        if(EnumSpecies.hasPokemonAnyCase(params[1])) {
                            eb.setTitle(e.getAuthor().getName() + " is looking for:", null);

                            String name = EnumSpecies.getFromNameAnyCase(params[1]).name.replace("-","");

                            String alolan = "";
                            //if(content.contains("alolan") && EnumSpecies.getFromNameAnyCase(params[1]).getFormEnum(1) == EnumFo.ALOLAN) alolan = "-alola";

                            if(content.contains("shiny")) {
                                eb.setThumbnail("http://play.pokemonshowdown.com/sprites/xyani-shiny/" + name.toLowerCase() + alolan + ".gif");
                            } else {
                                eb.setThumbnail("http://play.pokemonshowdown.com/sprites/xyani/" + name.toLowerCase() + alolan + ".gif");
                            }

                            eb.addField("Pok\u00E9mon", (alolan.equals("") ? "" : "Alolan ") + StringUtils.capitalize(params[1]) + (content.contains("shiny") ? " (Shiny)" : ""), true);

                            if (params.length > 2) {
                                for (int i = 2; i < params.length; i++) {
                                    if (params[i].startsWith("level:")) {
                                        String level = params[i].replace("level:", "");

                                        try {
                                            if (Integer.parseInt(level) > 100) level = "100";
                                            if (Integer.parseInt(level) < 0) level = "1";

                                            eb.addField("Level", level, true);
                                        } catch (NumberFormatException e1) {
                                            //
                                        }
                                    }
                                    if (params[i].startsWith("gender:")) {
                                        String gender = params[i].replace("gender:", "").toLowerCase();

                                        int malepercent = (int) EnumSpecies.getFromNameAnyCase(params[1]).getBaseStats().getMalePercent();
                                        if(between(malepercent, 1, 99)) {
                                            List genders = Arrays.asList("m","M","male","Male","f","F","female","Female");
                                            if(genders.contains(gender)) eb.addField("Gender", StringUtils.capitalize(gender), true);
                                        } else if(malepercent == 0) {
                                            eb.addField("Gender", "Female", true);
                                        } else if(malepercent == 100) {
                                            eb.addField("Gender", "Male", true);
                                        } else if(malepercent == -1) {
                                            eb.addField("Gender", "Genderless", true);
                                        }
                                    }
                                    if (params[i].startsWith("nature:")) {
                                        String nature = params[i].replace("nature:", "");

                                        if(EnumNature.hasNature(nature)) {
                                            eb.addField("Nature", StringUtils.capitalize(nature), true);
                                        }
                                    }
                                    if (params[i].startsWith("ability:")) {
                                        String ability = params[i].replace("ability:", "");

                                        if(Arrays.asList(EnumSpecies.getFromNameAnyCase(params[1]).getBaseStats().abilities).contains(ability)) {
                                            eb.addField("Ability", StringUtils.capitalize(ability), true);
                                        }
                                    }
                                    if (params[i].startsWith("growth:")) {
                                        String growth = params[i].replace("growth:", "");
                                        if(EnumGrowth.hasGrowth(growth)) {
                                            eb.addField("Growth", StringUtils.capitalize(growth), true);
                                        }
                                    }
                                    if (params[i].startsWith("ivs:")) {
                                        String ivs = params[i].replace("ivs:", "").replace("%", "");

                                        try {
                                            if (Integer.getInteger(ivs) > 100) ivs = "100";
                                            if (Integer.getInteger(ivs) < 0) ivs = "0";

                                            eb.addField("IV%", ivs + "%", true);
                                        } catch (NumberFormatException e1) {
                                            //
                                        }
                                    }
                                }

                                channel.sendMessage(eb.build()).queue();
                                //DiscordBot.getInstance().getJda().getTextChannelById(Config.TRADE_CHANNEL).sendMessage(eb.build()).queue();
                            }
                        } else {
                            DiscordUtils.sendMessageToChannel(Config.TRADE_CHANNEL, "Sorry, but there doesn't seem to be a Pok\u00E9mon named " + params[1] + ". Maybe double check the spelling.");
                        }
                    }

                    if(content.toLowerCase().startsWith("!ft") || content.toLowerCase().startsWith("!fortrade")) {
                        eb.setColor(Color.GREEN);
                        String[] params = content.split(" ");

                        if(EnumSpecies.hasPokemonAnyCase(params[1])) {
                            eb.setTitle(e.getAuthor().getName() + " is looking to trade:");

                            String name = EnumSpecies.getFromNameAnyCase(params[1]).name.replace("-","");

                            String alolan = "";
                            //if(content.contains("alolan") && EnumSpecies.getFromNameAnyCase(params[1]).getFormEnum(1) == EnumAlolan.ALOLAN) alolan = "-alola";

                            if(content.contains("shiny")) {
                                eb.setThumbnail("http://play.pokemonshowdown.com/sprites/xyani-shiny/" + name.toLowerCase() + alolan + ".gif");
                            } else {
                                eb.setThumbnail("http://play.pokemonshowdown.com/sprites/xyani/" + name.toLowerCase() + alolan + ".gif");
                            }

                            eb.addField("Pok\u00E9mon", (alolan.equals("") ? "" : "Alolan ") + StringUtils.capitalize(params[1]) + (content.contains("shiny") ? " (Shiny)" : ""), true);

                            if (params.length > 2) {
                                for (int i = 2; i < params.length; i++) {
                                    if (params[i].startsWith("level:")) {
                                        String level = params[i].replace("level:", "");

                                        try {
                                            if (Integer.parseInt(level) > 100) level = "100";
                                            if (Integer.parseInt(level) < 0) level = "1";

                                            eb.addField("Level", level, true);
                                        } catch (NumberFormatException e1) {
                                            //
                                        }
                                    }
                                    if (params[i].startsWith("gender:")) {
                                        String gender = params[i].replace("gender:", "").toLowerCase();

                                        int malepercent = (int)EnumSpecies.getFromNameAnyCase(params[1]).getBaseStats().getMalePercent();
                                        if(between(malepercent, 1, 99)) {
                                            List genders = Arrays.asList("m","M","male","Male","f","F","female","Female");
                                            if(genders.contains(gender)) eb.addField("Gender", StringUtils.capitalize(gender), true);
                                        } else if(malepercent == 0) {
                                            eb.addField("Gender", "Female", true);
                                        } else if(malepercent == 100) {
                                            eb.addField("Gender", "Male", true);
                                        } else if(malepercent == -1) {
                                            eb.addField("Gender", "Genderless", true);
                                        }
                                    }
                                    if (params[i].startsWith("nature:")) {
                                        String nature = params[i].replace("nature:", "");

                                        if(EnumNature.hasNature(nature)) {
                                            eb.addField("Nature", StringUtils.capitalize(nature), true);
                                        }
                                    }
                                    if (params[i].startsWith("ability:")) {
                                        String ability = params[i].replace("ability:", "");

                                        if(Arrays.asList(EnumSpecies.getFromNameAnyCase(params[1]).getBaseStats().abilities).contains(ability)) {
                                            eb.addField("Ability", StringUtils.capitalize(ability), true);
                                        }
                                    }
                                    if (params[i].startsWith("growth:")) {
                                        String growth = params[i].replace("growth:", "");
                                        if(EnumGrowth.hasGrowth(growth)) {
                                            eb.addField("Growth", StringUtils.capitalize(growth), true);
                                        }
                                    }
                                    if (params[i].startsWith("ivs:")) {
                                        String ivs = params[i].replace("ivs:", "").replace("%", "");

                                        try {
                                            if (Integer.parseInt(ivs) > 100) ivs = "100";
                                            if (Integer.parseInt(ivs) < 0) ivs = "0";

                                            eb.addField("IV%", ivs + "%", true);
                                        } catch (NumberFormatException e1) {
                                            //
                                        }
                                    }
                                }

                                channel.sendMessage(eb.build()).queue();
                                //DiscordBot.getInstance().getJda().getTextChannelById(Config.TRADE_CHANNEL).sendMessage(eb.build()).queue();
                            }
                        } else {
                            DiscordUtils.sendMessageToChannel(Config.TRADE_CHANNEL, "Sorry, but there doesn't seem to be a Pok\u00E9mon named " + params[1] + ". Maybe double check the spelling.");
                        }
                    }
                }

                if(channel.getId().equals(Config.BOT_CHANNEL)) {
                    if (content.startsWith("!link")) {
                        String player = content.replace("!link ", "");
                        org.spongepowered.api.entity.living.player.User user = this.getUser(player);
                        if (user == null) {
                            channel.sendMessage(Config.getMessages("messages.verify.invalid-name")).queue();
                        } else {
                            Role role = DiscordBot.getInstance().getJda().getRoleById(Config.verifyInfo("discord-role"));
                            if (e.getMember().getRoles().contains(role) && Data.getAllAccounts().containsValue(user.getPlayer().get().getName())) {
                                channel.sendMessage(Config.getMessages("messages.verify.already-linked")).queue();
                            } else if (user.isOnline() && user.getPlayer().isPresent()) {
                                Player p = user.getPlayer().get();
                                Object[] arrobject = new Object[8];
                                arrobject[0] = TextColors.GRAY;
                                arrobject[1] = TextActions.executeCallback(src -> this.verifyUser(p));
                                arrobject[2] = TextActions.showText(Text.of(TextColors.GRAY, "Click to verify account!"));
                                arrobject[3] = "Click here to ";
                                arrobject[4] = TextColors.AQUA;
                                arrobject[5] = "verify";
                                arrobject[6] = TextColors.GRAY;
                                arrobject[7] = " your account!";
                                p.sendMessage(Text.of(arrobject));
                                channel.sendMessage(Config.getMessages("messages.verify.dm-message")).queue();
                                DiscordUtils.sendPrivateMessage(e.getAuthor(), Config.getMessages("messages.verify.dms.ingame-message"));
                                if (!DiscordBot.getInstance().verifyAccount.containsKey(p)) DiscordBot.getInstance().verifyAccount.put(p, e.getMember());
                            } else {
                                DiscordUtils.sendPrivateMessage(e.getAuthor(), Config.getMessages("messages.verify.dms.logged-into-server"));
                            }
                        }
                    }

                    if (content.startsWith("!online")) {
                        DiscordUtils.dispatchList(e.getMessage(), e.getChannel());
                    }
                }

                if(channel.getId().equals(Config.SUPPORT_CHANNEL)) {
                    String info;

                    if (content.startsWith("!help")) {
                        ArrayList help = Lists.newArrayList();
                        help.add("");
                        for (int i = 0; i <= help.size(); ++i) {
                            DiscordUtils.sendPrivateMessage(e.getAuthor(), (String) help.get(i));
                        }
                    }

                    if (content.startsWith("!wiki") && !(info = Wiki.getInfo(content.replace("!wiki ", ""))).equalsIgnoreCase("empty")) {
                        channel.sendMessage(info).queue();
                    }
                }
            }
        }
    }

    private boolean checkPattern(String message, String pattern) {
        Pattern p = Pattern.compile(pattern, 2);
        Matcher m = p.matcher(message);
        int count = 0;
        while (m.find()) {
            ++count;
        }
        return count >= 2;
    }

    private org.spongepowered.api.entity.living.player.User getUser(String name) {
        Optional userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        Optional oUser = ((UserStorageService)userStorage.get()).get(name);
        return (org.spongepowered.api.entity.living.player.User) oUser.orElse(null);
    }

    private void verifyUser(Player player) {
        if (DiscordBot.getInstance().verifyAccount.containsKey(player)) {
            List<String> groups = Config.verifyRanks();
            if (!Collections.disjoint(groups, SpongeUtils.getAllGroups(player)) && !Data.getAllAccounts().containsKey(player.getUniqueId().toString())) {
                Role role = DiscordBot.getInstance().getJda().getRoleById(Config.verifyInfo("discord-role"));
                Member user = DiscordBot.getInstance().verifyAccount.get(player);
                String discordName = user.getUser().getName() + "#" + user.getUser().getDiscriminator();
                if (!user.getRoles().contains(role) || !Data.getAllAccounts().containsKey(player.getUniqueId().toString())) {
                    if (Data.addAccount(discordName, player.getUniqueId().toString())) {

                        role.getGuild().addRoleToMember(user, role).queue();
                        DiscordUtils.sendPrivateMessage(DiscordBot.getInstance().verifyAccount.get(player).getUser(), Config.getMessages("messages.verify.dms.link-success").replace("{role}", role.getName()).replace("{player}", player.getName()));
                    } else {
                        DiscordUtils.sendPrivateMessage(DiscordBot.getInstance().verifyAccount.get(player).getUser(), "There was an error saving your information. Contact staff and let them know.");
                    }
                } else {
                    DiscordUtils.sendPrivateMessage(DiscordBot.getInstance().verifyAccount.get(player).getUser(), Config.getMessages("messages.verify.dms.already-linked").replace("{player}", player.getName()));
                }
            } else {
                player.sendMessage(Text.of(TextColors.RED, Config.getMessages("messages.verify.invalid-rank").replace("{rank}", groups.get(0))));
                DiscordUtils.sendPrivateMessage(DiscordBot.getInstance().verifyAccount.get(player).getUser(), Config.getMessages("messages.verify.invalid-rank").replace("{rank}", groups.get(0)));
            }

            DiscordBot.getInstance().verifyAccount.remove(player);
        }
    }

    private static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }
}
