/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.DynSurround.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect;
import org.blockartistry.mod.DynSurround.data.xface.BiomeConfig;
import org.blockartistry.mod.DynSurround.data.xface.SoundConfig;
import org.blockartistry.mod.DynSurround.data.xface.SoundType;
import org.blockartistry.mod.DynSurround.event.RegistryReloadEvent;
import org.blockartistry.mod.DynSurround.registry.DataScripts.IDependent;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.MyUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;

public final class BiomeRegistry implements IDependent {

	private final static BiomeRegistry INSTANCE = new BiomeRegistry();

	private BiomeRegistry() {
		DataScripts.registerDependent(this);
	}

	@Override
	public void preInit() {
		biomeAliases.clear();
		registry.clear();

		for (final String entry : ModOptions.biomeAliases) {
			final String[] parts = StringUtils.split(entry, "=");
			if (parts.length == 2) {
				biomeAliases.put(parts[0], parts[1]);
			}
		}

		for (Iterator<Biome> itr = Biome.REGISTRY.iterator(); itr.hasNext();) {
			final BiomeInfo e = new BiomeInfo(itr.next());
			registry.put(e.getBiomeId(), e);
		}

		// Add our fake biomes
		registry.put(UNDERGROUND.getBiomeId(), UNDERGROUND);
		registry.put(UNDERWATER.getBiomeId(), UNDERWATER);
		registry.put(UNDEROCEAN.getBiomeId(), UNDEROCEAN);
		registry.put(UNDERDEEPOCEAN.getBiomeId(), UNDERDEEPOCEAN);
		registry.put(UNDERRIVER.getBiomeId(), UNDERRIVER);
		registry.put(OUTERSPACE.getBiomeId(), OUTERSPACE);
		registry.put(CLOUDS.getBiomeId(), CLOUDS);
		registry.put(PLAYER.getBiomeId(), PLAYER);
		registry.put(WTF.getBiomeId(), WTF);
	}

	@Override
	public void postInit() {
		if (ModOptions.enableDebugLogging) {
			ModLog.info("*** BIOME REGISTRY ***");
			for (final BiomeInfo entry : registry.valueCollection())
				ModLog.info(entry.toString());
		}

		// Free memory because we no longer need
		biomeAliases.clear();
		
		MinecraftForge.EVENT_BUS.post(new RegistryReloadEvent.Biome());
	}

	private static final TIntObjectHashMap<BiomeInfo> registry = new TIntObjectHashMap<BiomeInfo>();
	private static final Map<String, String> biomeAliases = new HashMap<String, String>();

	public static final BiomeInfo UNDERGROUND = new BiomeInfo(-1, "Underground");
	public static final BiomeInfo PLAYER = new BiomeInfo(-2, "Player");
	public static final BiomeInfo UNDERWATER = new BiomeInfo(-3, "Underwater");
	public static final BiomeInfo UNDEROCEAN = new BiomeInfo(-4, "UnderOCN");
	public static final BiomeInfo UNDERDEEPOCEAN = new BiomeInfo(-5, "UnderDOCN");
	public static final BiomeInfo UNDERRIVER = new BiomeInfo(-6, "UnderRVR");
	public static final BiomeInfo OUTERSPACE = new BiomeInfo(-7, "OuterSpace");
	public static final BiomeInfo CLOUDS = new BiomeInfo(-8, "Clouds");

	// This is for cases when the biome coming in doesn't make sense
	// and should default to something to avoid crap.
	private static final BiomeInfo WTF = new BiomeInfo(-256, "(FooBar)");

	public static String resolveName(final Biome biome) {
		if (biome == null)
			return "(Bad Biomes)";
		return biome.getBiomeName();
	}

	public static void initialize() {
		INSTANCE.preInit();
	}

	public static BiomeInfo get(final Biome biome) {
		BiomeInfo entry = registry.get(biome == null ? WTF.getBiomeId() : Biome.REGISTRY.getIDForObject(biome));
		if (entry == null) {
			ModLog.warn("Biomes [%s] was not detected during initial scan! Reloading config...", resolveName(biome));
			initialize();
			entry = registry.get(Biome.REGISTRY.getIDForObject(biome));
			if (entry == null) {
				ModLog.warn("Still can't find biome [%s]! Explicitly adding at defaults", resolveName(biome));
				entry = new BiomeInfo(biome);
				registry.put(entry.getBiomeId(), entry);
			}
		}
		return entry;
	}

	final static boolean isBiomeMatch(final BiomeConfig entry, final String biomeName) {
		if (Pattern.matches(entry.biomeName, biomeName))
			return true;
		final String alias = biomeAliases.get(biomeName);
		return alias == null ? false : Pattern.matches(entry.biomeName, alias);
	}

	public static void register(final BiomeConfig entry) {
		for (final BiomeInfo biomeEntry : registry.valueCollection()) {
			if (isBiomeMatch(entry, biomeEntry.getBiomeName())) {
				if (entry.hasPrecipitation != null)
					biomeEntry.setHasPrecipitation(entry.hasPrecipitation.booleanValue());
				if (entry.hasAurora != null)
					biomeEntry.setHasAurora(entry.hasAurora.booleanValue());
				if (entry.hasDust != null)
					biomeEntry.setHasDust(entry.hasDust.booleanValue());
				if (entry.hasFog != null)
					biomeEntry.setHasFog(entry.hasFog.booleanValue());
				if (entry.fogDensity != null)
					biomeEntry.setFogDensity(entry.fogDensity.floatValue());
				if (entry.fogColor != null) {
					final int[] rgb = MyUtils.splitToInts(entry.fogColor, ',');
					if (rgb.length == 3)
						biomeEntry.setFogColor(new Color(rgb[0], rgb[1], rgb[2]));
				}
				if (entry.dustColor != null) {
					final int[] rgb = MyUtils.splitToInts(entry.dustColor, ',');
					if (rgb.length == 3)
						biomeEntry.setDustColor(new Color(rgb[0], rgb[1], rgb[2]));
				}
				if (entry.soundReset != null && entry.soundReset.booleanValue()) {
					biomeEntry.getSounds().clear();
					biomeEntry.getSpotSounds().clear();
				}

				if (entry.spotSoundChance != null)
					biomeEntry.setSpotSoundChance(entry.spotSoundChance.intValue());

				for (final SoundConfig sr : entry.sounds) {
					if (SoundRegistry.isSoundBlocked(sr.sound))
						continue;
					final SoundEffect s = new SoundEffect(sr);
					if (s.type == SoundType.SPOT)
						biomeEntry.getSpotSounds().add(s);
					else
						biomeEntry.getSounds().add(s);
				}
			}
		}
	}
}
