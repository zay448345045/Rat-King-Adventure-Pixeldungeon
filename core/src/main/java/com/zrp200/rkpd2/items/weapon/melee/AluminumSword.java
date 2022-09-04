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

package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.noosa.Image;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.AnkhInvulnerability;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;

public class AluminumSword extends MeleeWeapon {

	{
		image = ItemSpriteSheet.ALUMINUM_SWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 6;
		DLY = 0.5f;
	}

	@Override
	public int max(int lvl) {
		return (int) (4f*(tier-1) + ((tier-1)*lvl)); //20 (+5)
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage += Buff.affect(attacker, Combo.class).hit(defender, damage);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public int warriorAttack(int damage, Char enemy) {
		Buff.affect(Dungeon.hero, AnkhInvulnerability.class, delayFactor(Dungeon.hero)*2);
		return super.warriorAttack(damage, enemy);
	}

	public static class Combo extends Buff {

		public int count = 0;

		@Override
		public int icon() {
			return BuffIndicator.COMBO;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0xa6b9c8);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc",((Hero)target).heroClass.title(), count, visualcooldown(), Math.round((count) / 3f * 100 + 100));
		}

		@Override
		public String iconTextDisplay() {
			return String.valueOf(visualcooldown());
		}

		public int hit(Char enemy, int damage ) {

			count++;

			if (count >= 2) {
				GLog.p(Messages.get(this, "combo"), count );
				postpone( 3f - count / 10f );
				return (int)(damage * (count - 1) / 3f);

			} else {

				postpone( 2f );
				return 0;

			}
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}

	}
}
