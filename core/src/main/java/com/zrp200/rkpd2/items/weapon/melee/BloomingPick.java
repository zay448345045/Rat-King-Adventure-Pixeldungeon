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

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.utils.GLog;

import java.util.ArrayList;

public class BloomingPick extends MeleeWeapon {

	public static final String AC_MINE	= "MINE";

	public static final float TIME_TO_MINE = 5;
	
	{
		image = ItemSpriteSheet.BLOOMING_PICK;
		hitSound = Assets.Sounds.EVOKE;
		hitSoundPitch = 1.2f;
		defaultAction = AC_MINE;
		
		tier = 6;
	}
	
	@Override
	public int max(int lvl) {
		return  Math.round(2*(tier+1)) +     //12 base, down from 30
				lvl*Math.round((tier-4));  //+3.5 per level, down from +7
	}

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_MINE );
		return actions;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int healAmt = Math.round(damage);
		healAmt = Math.min( healAmt, attacker.HT - attacker.HP );

		if (healAmt > 0 && attacker.isAlive()) {

			attacker.HP += healAmt;
			attacker.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 1 );
			attacker.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmt ) );

		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_MINE)) {

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {

				final int pos = hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Dungeon.level.map[pos] == Terrain.WALL || Dungeon.level.map[pos] == Terrain.DOOR) {

					hero.spend( TIME_TO_MINE );
					hero.busy();

					hero.sprite.attack( pos, new Callback() {

						@Override
						public void call() {

							CellEmitter.center( pos ).burst( Speck.factory( Speck.STAR ), 7 );
							Sample.INSTANCE.play( Assets.Sounds.EVOKE );
							Sample.INSTANCE.play( Assets.Sounds.ROCKS);

							Level.set( pos, Terrain.EMBERS );
							GameScene.updateMap( pos );

							hero.onOperateComplete();
						}
					} );

					return;
				}
			}

			GLog.w( Messages.get(this, "no_vein") );

		}
	}

}
