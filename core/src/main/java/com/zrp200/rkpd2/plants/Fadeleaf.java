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

package com.zrp200.rkpd2.plants;

import com.watabou.noosa.Game;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroSubClass;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.artifacts.TimekeepersHourglass;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.InterlevelScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.utils.GLog;

public class Fadeleaf extends Plant {
	
	{
		image = 10;
		seedClass = Seed.class;
	}

	@Override
	public void affectHero(Char ch, boolean isWarden) {
		if (isWarden){
			if (Dungeon.bossLevel()) {
				GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
				return;

			}

			TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
			if (timeFreeze != null) timeFreeze.disarmPressedTraps();
			Swiftthistle.TimeBubble timeBubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
			if (timeBubble != null) timeBubble.disarmPressedTraps();

			InterlevelScene.mode = InterlevelScene.Mode.RETURN;
			InterlevelScene.returnDepth = Math.max(1, (Dungeon.getDepth() - 1));
			InterlevelScene.returnPos = -2;
			Game.switchScene( InterlevelScene.class );
		} else {
			ScrollOfTeleportation.teleportHero((Hero) ch);
		}
	}

	@Override
	public void affectMob(Mob mob) {
		if (!mob.properties().contains(Char.Property.IMMOVABLE)){
			if (!Dungeon.bossLevel()) {

				int count = 20;
				int newPos;
				do {
					newPos = Dungeon.level.randomRespawnCell(mob);
					if (count-- <= 0) {
						break;
					}
				} while (newPos == -1 || Dungeon.level.secret[newPos]);

				if (newPos != -1) {

					mob.pos = newPos;
					if (mob.state == mob.HUNTING)
						mob.state = mob.WANDERING;
					mob.sprite.place(mob.pos);
					mob.sprite.visible = Dungeon.level.heroFOV[mob.pos];

				}
			}
		}
	}

	@Override
	public void activateMisc(Char ch) {
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}
	}

	@Override
	public String wardenDesc(HeroSubClass subClass) {
		return wardenDesc(subClass, subClass == HeroSubClass.WARDEN ? "her" : "him");
	}

	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SEED_FADELEAF;

			plantClass = Fadeleaf.class;
		}
	}
}
