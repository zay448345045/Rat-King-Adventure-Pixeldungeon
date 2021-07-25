/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
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

import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.mobs.Statue;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.StatueSprite;

public class ConstructWand extends MeleeWeapon {

	{
		image = ItemSpriteSheet.CONSTRUCT_WAND;
		hitSound = Assets.Sounds.HIT_STRONG;
		hitSoundPitch = 0.85f;

		tier = 6;
	}

    @Override
    public int max(int lvl) {
        return 4*(tier+1) + (tier)*lvl;
    }

    @Override
    public String statsInfo() {
        return Messages.get(this, "stats_desc", 5 + Dungeon.getDepth() * 2);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        for (int i : PathFinder.NEIGHBOURS9){

            if (!Dungeon.level.solid[attacker.pos + i]
                    && !Dungeon.level.pit[attacker.pos + i]
                    && Actor.findChar(attacker.pos + i) == null
                    && attacker == Dungeon.hero) {

                GuardianKnight guardianKnight = new GuardianKnight();
                guardianKnight.weapon = this;
                guardianKnight.pos = attacker.pos + i;
                guardianKnight.aggro(defender);
                GameScene.add(guardianKnight);
                Dungeon.level.occupyCell(guardianKnight);

                CellEmitter.get(guardianKnight.pos).burst(Speck.factory(Speck.EVOKE), 4);
                break;
            }
        }
        return super.proc(attacker, defender, damage);
    }

    public static class GuardianKnight extends Statue {
        {
            state = WANDERING;
            spriteClass = GuardianSprite.class;
            alignment = Alignment.ALLY;
        }

        public GuardianKnight() {
            HP = HT = 5 + Dungeon.getDepth() *2;
            defenseSkill = Dungeon.getDepth();
        }

        @Override
        public int damageRoll() {
            return super.damageRoll()/2;
        }

        @Override
        public void die(Object cause) {
            weapon = null;
            super.die(cause);
        }

        @Override
        public int drRoll() {
            return Random.Int(0, Dungeon.getDepth());
        }
    }

    public static class GuardianSprite extends StatueSprite {

        public GuardianSprite(){
            super();
            tint(0x84d4f6, 0.4f);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            tint(0x84d4f6, 0.4f);
        }
    }
}
