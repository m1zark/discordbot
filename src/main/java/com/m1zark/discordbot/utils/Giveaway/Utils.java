package com.m1zark.discordbot.utils.Giveaway;

import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public class Utils {
    public static int parseShortTime(String timestr) {
        timestr = timestr.toLowerCase();
        if(!timestr.matches("\\d{1,8}[smhd]?")) return -1;
        int multiplier = 1;
        switch(timestr.charAt(timestr.length()-1)) {
            case 'd':
                multiplier *= 24;
            case 'h':
                multiplier *= 60;
            case 'm':
                multiplier *= 60;
            case 's':
                timestr = timestr.substring(0, timestr.length()-1);
            default:
        }
        return multiplier * Integer.parseInt(timestr);
    }

    public static int parseTime(String timestr) {
        timestr = timestr.replaceAll("(?i)(\\s|,|and)","").replaceAll("(?is)(-?\\d+|[a-z]+)", "$1 ").trim();
        String[] vals = timestr.split("\\s+");
        int timeinseconds = 0;
        try {
            for(int j=0; j<vals.length; j+=2) {
                int num = Integer.parseInt(vals[j]);
                if(vals[j+1].toLowerCase().startsWith("m"))
                    num*=60;
                else if(vals[j+1].toLowerCase().startsWith("h"))
                    num*=60*60;
                else if(vals[j+1].toLowerCase().startsWith("d"))
                    num*=60*60*24;
                timeinseconds+=num;
            }
        }
        catch(Exception ex)
        {
            return 0;
        }
        return timeinseconds;
    }

    public static int parseWinners(String winstr) {
        if(!winstr.toLowerCase().matches("\\d{1,3}w")) return -1;
        return Integer.parseInt(winstr.substring(0, winstr.length()-1));
    }

    public static String pluralise(long x, String singular, String plural) {
        return x == 1 ? singular : plural;
    }

    public static String secondsToTime(long timeseconds) {
        StringBuilder builder = new StringBuilder();
        int years = (int)(timeseconds / (60*60*24*365));
        if(years>0){
            builder.append("**").append(years).append("** ").append(pluralise(years, "year", "years")).append(", ");
            timeseconds = timeseconds % (60*60*24*365);
        }
        int weeks = (int)(timeseconds / (60*60*24*7));
        if(weeks>0){
            builder.append("**").append(weeks).append("** ").append(pluralise(weeks, "week", "weeks")).append(", ");
            timeseconds = timeseconds % (60*60*24*7);
        }
        int days = (int)(timeseconds / (60*60*24));
        if(days>0){
            builder.append("**").append(days).append("** ").append(pluralise(days, "day", "days")).append(", ");
            timeseconds = timeseconds % (60*60*24);
        }
        int hours = (int)(timeseconds / (60*60));
        if(hours>0){
            builder.append("**").append(hours).append("** ").append(pluralise(hours, "hour", "hours")).append(", ");
            timeseconds = timeseconds % (60*60);
        }
        int minutes = (int)(timeseconds / (60));
        if(minutes>0){
            builder.append("**").append(minutes).append("** ").append(pluralise(minutes, "minute", "minutes")).append(", ");
            timeseconds = timeseconds % (60);
        }
        if(timeseconds>0) {
            builder.append("**").append(timeseconds).append("** ").append(pluralise(timeseconds, "second", "seconds"));
        }
        String str = builder.toString();
        if(str.endsWith(", ")) str = str.substring(0,str.length()-2);
        if(str.equals("")) str="**No time**";

        return str;
    }


    public static <T> List<T> selectWinners(List<T> list, int winners) {
        List<T> winlist = new LinkedList<>();
        List<T> pullist = new LinkedList<>(list);
        for(int i = 0; i < winners && !pullist.isEmpty(); i++) {
            winlist.add(pullist.remove((int)(Math.random()*pullist.size())));
        }

        return winlist;
    }

    public static void getSingleWinner(Message message, Consumer<User> success, Runnable failure, ExecutorService threadpool) {
        threadpool.submit(() -> {
            try {
                MessageReaction mr = message.getReactions().stream().filter(r -> r.getReactionEmote().getName().equals(Constants.TADA)).findAny().orElse(null);
                List<User> users = new LinkedList<>();



                mr.retrieveUsers().stream().distinct().filter(u -> !u.isBot()).forEach(u -> users.add(u));
                if(users.isEmpty())
                    failure.run();
                else
                    success.accept(users.get((int)(Math.random()*users.size())));
            } catch(Exception e) {
                failure.run();
            }
        });
    }
}
