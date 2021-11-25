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

package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.items.quest.Kromer;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;

public class NoDeath extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
		
		announced = true;
	}
	
	public static final float DURATION	= 10f;
	
	@Override
	public int icon() {
		return BuffIndicator.RAGE;
	}

	@Override
	public float iconFadePercent() {
		float duration = 20;
		return Math.max(0, (duration - visualcooldown()) / duration);
	}

	@Override
	public boolean act() {
		if (target.HP <= 0) {
			target.HP = 0;
			target.die(Kromer.class);
			Dungeon.fail(Kromer.class);
			GLog.n( Messages.get(Kromer.class, "on_death") );
		}
		return super.act();
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
	
}
