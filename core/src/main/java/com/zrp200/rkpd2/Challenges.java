/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.rkpd2;

import com.badlogic.gdx.utils.OrderedMap;
import com.watabou.utils.DeviceCompat;
import com.zrp200.rkpd2.items.Dewdrop;
import com.zrp200.rkpd2.items.Item;

public class Challenges {

	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;
	public static final int CHAMPION_ENEMIES	= 128;
	public static final int STRONGER_BOSSES 	= 256;

	public static final int FATIQUE             = 512;
	public static final int NO_ACCURACY         = 1024;
	public static final int NO_HP               = 2048;
	public static final int BURN                = 4096;
	public static final int HERO_PATHING        = 8192;
	public static final int FORGET_PATH         = 16384;


	public static OrderedMap<String, Integer> defaultChals = new OrderedMap<>();
	static {
		defaultChals.put("champion_enemies", CHAMPION_ENEMIES);
		defaultChals.put("stronger_bosses", STRONGER_BOSSES);
		defaultChals.put("no_food", NO_FOOD);
		defaultChals.put("no_armor", NO_ARMOR);
		defaultChals.put("no_healing", NO_HEALING);
		defaultChals.put("no_herbalism", NO_HERBALISM);
		defaultChals.put("swarm_intelligence", SWARM_INTELLIGENCE);
		defaultChals.put("darkness", DARKNESS);
		defaultChals.put("no_scrolls", NO_SCROLLS);
	}

	//summoning's solution would be better, but cmon, conduct refactoring for meme dlc?
	public static OrderedMap<String, Integer> availableChallenges(){
		OrderedMap<String, Integer> chals = new OrderedMap<>(defaultChals);
		if (DeviceCompat.isDebug()){
			chals.put("fatique", FATIQUE);
			chals.put("no_accuracy", NO_ACCURACY);
			chals.put("no_hp", NO_HP);
			chals.put("burn", BURN);
			chals.put("hero_pathing", HERO_PATHING);
			chals.put("forget_path", FORGET_PATH);
		} else {
			if (Badges.isUnlocked(Badges.Badge.CHAMPED_NO_FOOD)) chals.put("fatique", FATIQUE);
			if (Badges.isUnlocked(Badges.Badge.CHAMPED_NO_ARMOR)) chals.put("no_accuracy", NO_ACCURACY);
			if (Badges.isUnlocked(Badges.Badge.CHAMPED_NO_HEALING)) chals.put("no_hp", NO_HP);
			if (Badges.isUnlocked(Badges.Badge.CHAMPED_NO_HERBALISM)) chals.put("burn", BURN);
			if (Badges.isUnlocked(Badges.Badge.CHAMPED_SWARM)) chals.put("hero_pathing", HERO_PATHING);
			if (Badges.isUnlocked(Badges.Badge.CHAMPED_DARKNESS)) chals.put("forget_path", FORGET_PATH);
		}
		return chals;
	}

	public static int activeChallenges(){
		int chCount = 0;
		for (int ch : Challenges.availableChallenges().values().toArray()){
			if ((Dungeon.challenges & ch) != 0) chCount++;
		}
		return chCount;
	}

	public static boolean isItemBlocked( Item item ){

		if (Dungeon.isChallenged(NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

		return false;

	}

}