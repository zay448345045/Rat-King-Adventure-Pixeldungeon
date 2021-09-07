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

import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class Whip extends MeleeWeapon {

	{
		image = ItemSpriteSheet.WHIP;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;

		tier = 3;
		RCH = 3;    //lots of extra reach
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) +    //12 base, down from 20
				lvl*(tier);     //+3 per level, down from +4
	}

	static int numberOfHits = 0;

	@Override
	public int warriorAttack(int damage, Char enemy) {
		numberOfHits = Random.Int(4, 10);
		attack(damage, enemy);
		return 0;
	}

	public void attack(int damage, Char enemy){
		Dungeon.hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				Dungeon.hero.sprite.showStatus(CharSprite.DEFAULT, String.valueOf(numberOfHits+1));
				Dungeon.hero.attack(enemy);
				if (numberOfHits > 0){
					numberOfHits--;
					Dungeon.hero.spend(-Dungeon.hero.cooldown());
					attack(damage, enemy);
				} else {
					Dungeon.hero.spendAndNext(1.5f);
				}
			}
		});
	}

	@Override
	public int damageRoll(Char owner) {
		if (numberOfHits > 0) return super.damageRoll(owner) / 2;
		return super.damageRoll(owner);
	}

	@Override
	public float warriorDelay() {
		return 0f;
	}

}
