/* This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.DynSurround.data.xface;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BlockConfig {
	@SerializedName("blocks")
	public List<String> blocks = new ArrayList<String>();
	@SerializedName("soundReset")
	public Boolean soundReset = null;
	@SerializedName("effectReset")
	public Boolean effectReset = null;
	@SerializedName("stepSoundReset")
	public Boolean stepSoundReset = null;
	@SerializedName("chance")
	public Integer chance = null;
	@SerializedName("stepChance")
	public Integer stepChance = null;
	@SerializedName("sounds")
	public List<SoundConfig> sounds = new ArrayList<SoundConfig>();
	@SerializedName("effects")
	public List<EffectConfig> effects = new ArrayList<EffectConfig>();

	public BlockConfig() {

	}

	public BlockConfig(final String... blocks) {
		if (blocks != null)
			for (final String b : blocks)
				this.addBlock(b);
	}

	public BlockConfig addBlock(final String block) {
		if (blocks != null)
			this.blocks.add(block);
		return this;
	}

	public BlockConfig setResetSounds(final boolean flag) {
		this.soundReset = flag;
		return this;
	}

	public BlockConfig setResetEffects(final boolean flag) {
		this.effectReset = flag;
		return this;
	}

	public BlockConfig setResetStepSounds(final boolean flag) {
		this.stepSoundReset = flag;
		return this;
	}

	public BlockConfig setStepSoundChance(final int chance) {
		this.stepChance = chance;
		return this;
	}

	public BlockConfig setSoundChance(final int chance) {
		this.chance = chance;
		return this;
	}

	public BlockConfig addSound(final SoundConfig sound) {
		if (sounds != null)
			this.sounds.add(sound);
		return this;
	}

	public BlockConfig addEffect(final EffectConfig effect) {
		if (effects != null)
			this.effects.add(effect);
		return this;
	}

	public void register() {
		Blocks.register(this);
	}
}
