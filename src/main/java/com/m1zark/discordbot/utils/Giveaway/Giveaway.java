package com.m1zark.discordbot.utils.Giveaway;

import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Setter
public class Giveaway {
    public long messageId;
    public long channelId;
    public Instant end;
    public int winners;
    public String prize;
    public Status status;

    public Giveaway(long messageId, long channelId, Instant end, int winners, String prize, Status status) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.end = end;
        this.winners = winners;
        this.prize = prize==null ? null : prize.isEmpty() ? null : prize;
        this.status = status;
    }

    public Message render(Color color, Instant now) {
        MessageBuilder mb = new MessageBuilder();
        boolean close = now.plusSeconds(9).isAfter(end);
        mb.append(close ? " **P I X E L M O N  G I V E A W A Y** " : "   **PIXELMON GIVEAWAY**   ");
        EmbedBuilder eb = new EmbedBuilder();
        if(close)
            eb.setColor(Color.RED);
        else if(color==null)
            eb.setColor(Constants.BLURPLE);
        else
            eb.setColor(color);
        eb.setFooter((winners==1 ? "" : winners+" winners | ")+"Ends at",null);
        eb.setTimestamp(end);
        eb.setDescription("React with " + Constants.TADA + " to enter!\nTime remaining: " + Utils.secondsToTime(now.until(end, ChronoUnit.SECONDS)));
        if(prize!=null)
            eb.setAuthor(prize, null, null);
        if(close)
            eb.setTitle("Last chance to enter!!!", null);
        mb.setEmbed(eb.build());
        return mb.build();
    }

    public void update() {

    }

    public void end() {

    }
}
