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

import com.watabou.utils.Bundle;
import com.zrp200.rkpd2.Badges;
import com.zrp200.rkpd2.Challenges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.artifacts.Artifact;
import com.zrp200.rkpd2.items.artifacts.HornOfPlenty;
import com.zrp200.rkpd2.items.journal.Guidebook;
import com.zrp200.rkpd2.journal.Document;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;

public class Hunger extends Buff implements Hero.Doom {

	private static final float STEP	= 10f;

	public static final float HUNGRY	= 300f;
	public static final float STARVING	= 450f;

	private float level;
	private float partialDamage;
	public int accumulatingDamage;

	private static final String LEVEL			= "level";
	private static final String PARTIALDAMAGE 	= "partialDamage";
	private static final String ACCUMDAMAGE 	= "accumulatedDamage";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
		bundle.put( ACCUMDAMAGE, accumulatingDamage);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
		accumulatingDamage = bundle.getInt(ACCUMDAMAGE);
	}

	@Override
	public boolean act() {

		if (Dungeon.level.locked || target.buff(WellFed.class) != null){
			spend(STEP);
			return true;
		}

		if (target.isAlive() && target instanceof Hero) {

			Hero hero = (Hero)target;

			if (isStarving()) {

				partialDamage += STEP * target.HT/1000f;

				if (partialDamage > 1){
					target.damage( (int)partialDamage, this);
					if (Dungeon.isChallenged(Challenges.FATIQUE))
						accumulatingDamage += (int)partialDamage;
					partialDamage -= (int)partialDamage;
				}
				
			} else {

				float newLevel = level + STEP;
				if (newLevel >= STARVING) {

					GLog.n( Messages.get(this, "onstarving") );
					hero.resting = false;
					hero.damage( 1, this );

					hero.interrupt();

				} else if (newLevel >= HUNGRY && level < HUNGRY) {

					GLog.w( Messages.get(this, "onhungry") );

					if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_FOOD)){
						GLog.p(Messages.get(Guidebook.class, "hint"));
						GameScene.flashForDocument(Document.GUIDE_FOOD);
					}

				}
				level = newLevel;

			}
			
			spend( target.buff( Shadows.class ) == null ? STEP : STEP * 1.5f );

		} else {

			diactivate();

		}

		return true;
	}

	@Override
	public void spend(float time) {
		if (Dungeon.hero.hasTalent(Talent.NATURE_AID_2) && Dungeon.level.map[Dungeon.hero.pos] == Terrain.FURROWED_GRASS){
			super.spend( time * (0.85f + Dungeon.hero.pointsInTalent(Talent.NATURE_AID_2)*0.4f));
		}
		else super.spend(time);
	}

	public void satisfy(float energy ) {

		Artifact.ArtifactBuff buff = target.buff( HornOfPlenty.hornRecharge.class );
		if (buff != null && buff.isCursed()){
			energy *= 0.67f;
			GLog.n( Messages.get(this, "cursedhorn") );
		}

		affectHunger( energy, false );
	}

	public void affectHunger(float energy ){
		affectHunger( energy, false );
	}

	public void affectHunger(float energy, boolean overrideLimits ) {

		if (energy < 0 && target.buff(WellFed.class) != null){
			target.buff(WellFed.class).left += energy;
			BuffIndicator.refreshHero();
			return;
		}

		level -= energy;
		if (level < 0 && !overrideLimits) {
			level = 0;
		} else if (level > STARVING) {
			float excess = level - STARVING;
			level = STARVING;
			partialDamage += excess * (target.HT/1000f);
		}

		BuffIndicator.refreshHero();
	}

	public boolean isStarving() {
		return level >= STARVING;
	}

	public int hunger() {
		return (int)Math.ceil(level);
	}

	@Override
	public int icon() {
		if (level < HUNGRY) {
			return BuffIndicator.NONE;
		} else if (level < STARVING) {
			return BuffIndicator.HUNGER;
		} else {
			return BuffIndicator.STARVATION;
		}
	}

	@Override
	public String toString() {
		if (level < STARVING) {
			return Messages.get(this, "hungry");
		} else {
			return Messages.get(this, "starving");
		}
	}

	@Override
	public String desc() {
		String result;
		if (level < STARVING) {
			result = Messages.get(this, "desc_intro_hungry");
		} else {
			result = Messages.get(this, "desc_intro_starving");
		}

		result += Messages.get(this, "desc");

		return result;
	}

	@Override
	public void onDeath() {

		Badges.validateDeathFromHunger();

		Dungeon.fail( getClass() );
		GLog.n( Messages.get(this, "ondeath") );
	}
}
