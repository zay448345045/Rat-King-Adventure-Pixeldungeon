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

package com.zrp200.rkpd2.actors.mobs;

import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.blobs.Electricity;
import com.zrp200.rkpd2.actors.blobs.ToxicGas;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.particles.ShadowParticle;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.WraithSprite;

import java.util.ArrayList;
import java.util.HashSet;

public class Wraith extends Mob {

	private static final float SPAWN_DELAY	= 2f;
	
	private int level;
	private int blinkCooldown = 0;
	
	{
		spriteClass = WraithSprite.class;
		
		HP = HT = 1;
		EXP = 0;

		maxLvl = -2;
		
		flying = true;

		properties.add(Property.UNDEAD);
	}
	
	private static final String LEVEL = "level";
	private static final String BLINK_CD = "blink_cd";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
		bundle.put(BLINK_CD, blinkCooldown);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getInt( LEVEL );
		blinkCooldown = bundle.getInt(BLINK_CD);
		adjustStats( level );
	}
	
	@Override
	public int damageRoll() {
		int dmg = Random.NormalIntRange(1 + level / 2, 2 + level);
		if (Dungeon.hero.pointsInTalent(Talent.POWER_IN_NUMBERS) > 1 && buff(AllyBuff.class) != null){
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
				if (mob instanceof Wraith && mob != this && fieldOfView[mob.pos]){
					dmg += 4;
					break;
				}
			}
		}
		return dmg;
	}


	
	@Override
	public int attackSkill( Char target ) {
		return 10 + level;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int) (super.defenseSkill(enemy)*
				(buff(AllyBuff.class) != null ? (1f + 0.2f*Dungeon.hero.pointsInTalent(Talent.ECTOTOUGHNESS)) : 1f));
	}

	public void adjustStats(int level ) {
		this.level = level;
		defenseSkill = attackSkill( null ) * 5;
		enemySeen = true;
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (buff(AllyBuff.class) != null && Random.Int(15) < Dungeon.hero.pointsInTalent(Talent.MIND_BREAKER)){
			destroy();
			CellEmitter.bottom(pos).burst(ShadowParticle.MISSILE, 30);
			sprite.killAndErase();
			Corruption.corrupt(enemy);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1f, 0.75f);
			return -1;
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	public void die(Object cause) {
		if (Dungeon.hero.pointsInTalent(Talent.POWER_IN_NUMBERS) > 2 && buff(AllyBuff.class) != null && buff(Shrink.class) == null){
			int wraiths = Random.IntRange(1, 3);
			for (int i = 0; i < wraiths; i++){
				int pos;
				int tries = 20;
				do{
					pos = this.pos + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)];
					tries --;
				} while (tries > 0 && (Dungeon.level.solid[pos] || Actor.findChar( pos ) != null));
				if ((!Dungeon.level.solid[pos] || Dungeon.level.passable[pos]) && Actor.findChar( pos ) == null) {

					Wraith w = new Wraith();
					w.adjustStats(Dungeon.getDepth());
					w.pos = pos;
					w.state = w.HUNTING;
					GameScene.add( w, 1f);
					Dungeon.level.occupyCell(w);

					w.sprite.alpha( 0 );
					w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );

					w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
					Buff.affect(w, AllyBuff.class);
					Buff.affect(w, Shrink.class);
				}
			}
		}
		super.die(cause);
	}

	public static void spawnAround(int pos ) {
		for (int n : PathFinder.NEIGHBOURS4) {
			spawnAt( pos + n );
		}
	}

	@Override
	public boolean isImmune(Class effect) {
		if (Dungeon.hero.hasTalent(Talent.I_HATE_ALL_ELEMENTS) && alignment == Alignment.ALLY){
			HashSet<Class> immunes = new HashSet<>();
			if (Dungeon.hero.pointsInTalent(Talent.I_HATE_ALL_ELEMENTS) > 0){
				immunes.add(Burning.class);
				immunes.add(Poison.class);
			}
			if (Dungeon.hero.pointsInTalent(Talent.I_HATE_ALL_ELEMENTS) > 1){
				immunes.add(ToxicGas.class);
				immunes.add(Electricity.class);
			}
			if (Dungeon.hero.pointsInTalent(Talent.I_HATE_ALL_ELEMENTS) > 2){
				immunes.add(Corrosion.class);
				immunes.add(Paralysis.class);
			}
			for (Class c : immunes){
				if (c.isAssignableFrom(effect)){
					return true;
				}
			}
		}
		return super.isImmune(effect);
	}

	@Override
	protected boolean getCloser( int target ) {
		if (fieldOfView[target] && Dungeon.level.distance( pos, target ) <= Math.min(6, Dungeon.hero.pointsInTalent(Talent.STAB_FROM_NOWHERE)*3) && blinkCooldown <= 0) {

			blink( target );
			spend( (Dungeon.hero.pointsInTalent(Talent.STAB_FROM_NOWHERE) == 3 ? -1 : 0) / speed() );
			return true;

		} else {
			if (Dungeon.hero.hasTalent(Talent.STAB_FROM_NOWHERE))
				blinkCooldown--;

			return super.getCloser( target );

		}
	}

	private void blink( int target ) {

		Ballistica route = new Ballistica( pos, target, Ballistica.PROJECTILE);
		int cell = route.collisionPos;

		//can't occupy the same cell as another char, so move back one.
		if (Actor.findChar( cell ) != null && cell != this.pos)
			cell = route.path.get(route.dist-1);

		if (Dungeon.level.avoid[ cell ] && (!properties().contains(Property.LARGE) || Dungeon.level.openSpace[cell])){
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				cell = route.collisionPos + n;
				if (Dungeon.level.passable[cell]
						&& Actor.findChar( cell ) == null
						&& (!properties().contains(Property.LARGE) || Dungeon.level.openSpace[cell])) {
					candidates.add( cell );
				}
			}
			if (candidates.size() > 0)
				cell = Random.element(candidates);
			else {
				blinkCooldown = Random.IntRange(4, 6);
				return;
			}
		}

		ScrollOfTeleportation.appear( this, cell );
		blinkCooldown = Random.IntRange(4, 6);
	}

	public static Wraith spawnAt(int pos ) {
		if ((!Dungeon.level.solid[pos] || Dungeon.level.passable[pos]) && Actor.findChar( pos ) == null) {
			
			Wraith w = new Wraith();
			w.adjustStats(Dungeon.getDepth());
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add( w, SPAWN_DELAY );
			Dungeon.level.occupyCell(w);

			w.sprite.alpha( 0 );
			w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );
			
			w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
			
			return w;
		} else {
			return null;
		}
	}

}
