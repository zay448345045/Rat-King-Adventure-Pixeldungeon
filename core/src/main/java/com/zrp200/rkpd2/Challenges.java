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

	public static final int MAX_VALUE           = 511;

	public static OrderedMap<String, Integer> defaultChals = new OrderedMap<>();
	static {
		defaultChals.put("champion_enemies", 128);
		defaultChals.put("stronger_bosses", 256);
		defaultChals.put("no_food", 1);
		defaultChals.put("no_armor", 2);
		defaultChals.put("no_healing", 4);
		defaultChals.put("no_herbalism", 8);
		defaultChals.put("swarm_intelligence", 16);
		defaultChals.put("darkness", 32);
		defaultChals.put("no_scrolls", 64);
	}

	//summoning's solution would be better, but cmon, conduct refactoring for meme dlc?
	public static OrderedMap<String, Integer> availableChallenges(){
		OrderedMap<String, Integer> chals = new OrderedMap<>(defaultChals);
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