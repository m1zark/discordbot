package com.m1zark.discordbot.listeners;

import com.m1zark.discordbot.DiscordBot;
import com.m1zark.discordbot.config.Config;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import java.lang.reflect.Field;

import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumMega;
import com.pixelmonmod.pixelmon.enums.forms.EnumPrimal;
import net.minecraft.entity.Entity;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PixelmonListeners {
    @SubscribeEvent
    public void onCatchPokemonEvent(CaptureEvent.SuccessfulCapture event) {
    }

    @SubscribeEvent
    public void onBattleEnd(BeatWildPixelmonEvent event) {
    }

    @SubscribeEvent
    public void onPokemonSpawn(SpawnEvent event) {
        EntityPixelmon pokemon;
        Entity spawnedEntity = event.action.getOrCreateEntity();
        if (!(!(spawnedEntity instanceof EntityPixelmon) || (pokemon = (EntityPixelmon)spawnedEntity).hasOwner()) && pokemon.getTrainer() == null) {
            if (EnumSpecies.legendaries.contains(pokemon.getSpecies().getPokemonName()) && pokemon.getFormEnum() != EnumMega.Mega && !pokemon.isBossPokemon() && Config.ENABLE_SPAWN_LEGENDARY) {
                DiscordBot.getInstance().getJda().getTextChannelById(Config.SPAWN_CHANNEL).sendMessage(Config.spawnMessage("legendary-spawns", pokemon.getPokemonName(), "", PixelmonListeners.getBiomeName(event.action.spawnLocation.biome), "")).queue();
            } else if (EnumSpecies.ultrabeasts.contains(pokemon.getSpecies().getPokemonName()) && pokemon.getFormEnum() != EnumMega.Mega && !pokemon.isBossPokemon() && Config.ENABLE_SPAWN_ULTRA) {
                DiscordBot.getInstance().getJda().getTextChannelById(Config.SPAWN_CHANNEL).sendMessage(Config.spawnMessage("ultra-spawns", pokemon.getPokemonName(), "", PixelmonListeners.getBiomeName(event.action.spawnLocation.biome), "")).queue();
            } else if (pokemon.getFormEnum() == EnumMega.Mega || pokemon.getFormEnum() == EnumPrimal.PRIMAL && pokemon.isBossPokemon() && Config.ENABLE_SPAWN_MEGA) {
                String megaForm = "";
                if (EnumSpecies.getFromNameAnyCase(pokemon.getPokemonName()).getNumForms(true) > 2) {
                    megaForm = pokemon.getPokemonData().getForm() == 1 ? "x" : "y";
                }
                DiscordBot.getInstance().getJda().getTextChannelById(Config.SPAWN_CHANNEL).sendMessage(Config.spawnMessage("mega-spawns", pokemon.getPokemonName(), megaForm, PixelmonListeners.getBiomeName(event.action.spawnLocation.biome), "")).queue();
            }
        }
    }

    private static String getBiomeName(Biome biome) {
        String name;
        try {
            Field f = ReflectionHelper.findField(Biome.class, (String[])new String[]{"biomeName", "field_185412_a", "field_76791_y"});
            name = (String)f.get(biome);
        }
        catch (Exception e) {
            return "Error getting biome name";
        }
        return name;
    }
}
