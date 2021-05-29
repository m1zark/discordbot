package com.m1zark.discordbot.listeners;

import com.m1zark.aurapokemon.events.AuraEvent;
import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.config.Config;
import com.m1zark.pixelmoncommands.events.WondertradeEvent;
import com.m1zark.pokehunt.events.BreedEventEnd;
import com.m1zark.pokehunt.events.BreedEventStart;

import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.api.event.Listener;

import java.util.List;

public class PluginListener {
    @Listener
    public void WonderTradeEvent(WondertradeEvent event) {
        List<String> legendaries = EnumSpecies.legendaries;
        legendaries.removeIf(name -> name.equals("Phione"));

        String pokemon = event.getGivingPokemon().getPokemon().getSpecies().getPokemonName();

        if(Config.ENABLE_WONDERTRADE) {
            if(legendaries.contains(pokemon)) {
                DiscordBot.getInstance().getJda().getTextChannelById(Config.EVENTS_CHANNEL).sendMessage(Config.wtMessage("wondertrade", event.player.getName(), "Legendary", event.getGivingPokemon().getPokemon().getSpecies().name())).queue();
            } else if(EnumSpecies.ultrabeasts.contains(pokemon)) {
                DiscordBot.getInstance().getJda().getTextChannelById(Config.EVENTS_CHANNEL).sendMessage(Config.wtMessage("wondertrade", event.player.getName(), "Ultra Beast", event.getGivingPokemon().getPokemon().getSpecies().name())).queue();
            }
        }
    }

    @Listener
    public void AuraPokemonSpawn(AuraEvent event) {
        if (Config.ENABLE_SPAWN_AURA) {
            String type = event.pokemonName.substring(2, event.pokemonName.length() - 2);
            String aura = event.aura.substring(2, event.aura.length() - 2);
            DiscordBot.getInstance().getJda().getTextChannelById(Config.SPAWN_CHANNEL).sendMessage(Config.spawnMessage("aura-spawns", type, "", event.biomeName, aura)).queue();
        }
    }

    @Listener
    public void BreedEventStart(BreedEventStart event) {
        if (Config.ENABLE_BREED_EVENT) {
            DiscordBot.getInstance().getJda().getTextChannelById(Config.EVENTS_CHANNEL).sendMessage(Config.breedMessage("start", event.ends)).queue();
        }
    }

    @Listener
    public void BreedEventEnd(BreedEventEnd event) {
        if (Config.ENABLE_BREED_EVENT) {
            DiscordBot.getInstance().getJda().getTextChannelById(Config.EVENTS_CHANNEL).sendMessage(Config.breedMessage("end", "")).queue();
        }
    }
}
