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

import com.watabou.utils.Bundle;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.blobs.Blob;
import com.zrp200.rkpd2.actors.blobs.ConfusionGas;
import com.zrp200.rkpd2.actors.blobs.CorrosiveGas;
import com.zrp200.rkpd2.actors.blobs.StenchGas;
import com.zrp200.rkpd2.actors.blobs.ToxicGas;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class NuclearHatchet extends MeleeWeapon {

	{
		image = ItemSpriteSheet.NUCLEAR_HATCHET;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 6;
		ACC = 1.5f; //50% boost to accuracy
	}

	@Override
	public int max(int lvl) {
		return  5*(tier-2) +    //20 base, down from 30
				lvl*(tier-2);   //scaling unchanged
	}

	@Override
	public int warriorAttack(int damage, Char enemy) {
		GameScene.add( Blob.seed( enemy.pos, 700, CorrosiveGas.class ).setStrength((int) (2 + Dungeon.scalingDepth() /2.5f)));
		return super.warriorAttack(damage, enemy);
	}

	public static class Effect extends Buff {

		{
			type = buffType.POSITIVE;
		}

		public static final float DURATION	= 50f;

		protected float left;

		private static final String LEFT	= "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			left = bundle.getFloat( LEFT );
		}

		public void set( float duration ) {
			this.left = duration;
		}

		@Override
		public boolean act() {
			GameScene.add(Blob.seed(target.pos, 75, ToxicGas.class));
			GameScene.add(Blob.seed(target.pos, 75, ConfusionGas.class));
			GameScene.add(Blob.seed(target.pos, 17, StenchGas.class));

			spend(TICK);
			left -= TICK;
			if (left <= 0){
				detach();
			}

			return true;
		}

		{
			immunities.add( ToxicGas.class );
			immunities.add( ConfusionGas.class );
			immunities.add( StenchGas.class );
		}
	}
}
