/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.zrp200.rkpd2.items.rings;

import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.MagicImmune;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.curses.Wayward;
import com.zrp200.rkpd2.items.weapon.enchantments.Projecting;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class RingOfForce extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_FORCE;
	}

	public Weapon.Enchantment enchantment;

	@Override
	protected RingBuff buff( ) {
		return new Force();
	}
	
	public static int armedDamageBonus( Char ch ) {
		return Math.round(getBuffedBonus( ch, Force.class)*1.66f);
	}
	
	
	// *** Weapon-like properties ***

	private static float tier(int str){
		float tier = Math.max(1, (str - 8)/2f);
		//each str point after 18 is half as effective
		if (tier > 5){
			tier = 5 + (tier - 5) / 2f;
		}
		return tier;
	}

	public static int damageRoll( Hero hero ){
		if (hero.buff(Force.class) != null) {
			int level = getBuffedBonus(hero, Force.class);
			float tier = tier(hero.STR());
			return Random.NormalIntRange(min(level, tier), max(level, tier));
		} else {
			//attack without any ring of force influence
			return Random.NormalIntRange(1, Math.max(hero.STR()-8, 1));
		}
	}

	@Override
	public Item upgrade() {
		return upgrade(false);
	}

	public Item upgrade(boolean enchant ) {

		if (enchant){
			if (enchantment == null){
				enchant(Weapon.Enchantment.random());
			}
		} else {
			if (hasCurseEnchant()){
				if (Random.Int(3) == 0) enchant(null);
			} else if (level() >= 4 && Random.Float(10) < Math.pow(2, level()-4)){
				enchant(null);
			}
		}

		cursed = false;

		return super.upgrade();
	}

	public float accuracyFactor( Char owner ) {
		int encumbrance = 0;

		if (hasEnchant(Wayward.class, owner))
			encumbrance = 2;

		float ACC = 1;

		return encumbrance > 0 ? (float)(ACC / Math.pow( 1.5, encumbrance )) : ACC;
	}

	public int reachFactor( Char owner ){
		int reach = 1;
		if(hasEnchant(Projecting.class, owner)) reach++;
		return reach;
	}

	@Override
	public String name() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( super.name() ) : super.name();
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.glowing() : null;
	}

	@Override
	public String info() {
		String info = super.info();
		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}
		return info;
	}

	//same as equivalent tier weapon
	private static int min(int lvl, float tier){
		return Math.max( 0, Math.round(
				tier-1 +  //base
				lvl     //level scaling
		));
	}

	//same as equivalent tier weapon
	private static int max(int lvl, float tier){
		return Math.max( 0, Math.round(
				4*(tier+1) +    //base
				lvl*(tier)    //level scaling
		));
	}

	public RingOfForce enchant( Weapon.Enchantment ench ) {
		enchantment = ench;
		updateQuickslot();
		return this;
	}

	public RingOfForce enchant() {

		Class<? extends Weapon.Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
		Weapon.Enchantment ench = Weapon.Enchantment.random( oldEnchantment );

		return enchant( ench );
	}

	public boolean hasEnchant(Class<?extends Weapon.Enchantment> type, Char owner) {
		return enchantment != null && enchantment.getClass() == type && owner.buff(MagicImmune.class) == null;
	}

	//these are not used to process specific enchant effects, so magic immune doesn't affect them
	public boolean hasGoodEnchant(){
		return enchantment != null && !enchantment.curse();
	}

	public boolean hasCurseEnchant(){
		return enchantment != null && enchantment.curse();
	}

	@Override
	public String statsInfo() {
		float tier = tier(Dungeon.hero.STR());
		int level = level();
		if(!isIdentified()) level(0);
		String res = Messages.get(this, isIdentified()?"stats":"typical_stats", min(soloBuffedBonus(), tier), max(soloBuffedBonus(), tier), Math.round(soloBuffedBonus()*1.66f));
		level(level);
		return res;
	}

	private static final String ENCHANTMENT	    = "enchantment";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( ENCHANTMENT, enchantment );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		enchantment = (Weapon.Enchantment)bundle.get( ENCHANTMENT );
	}

	public class Force extends RingBuff {
		public Weapon.Enchantment getEnchant(){
			return enchantment;
		}
		public int reachFactor(){
			return RingOfForce.this.reachFactor(target);
		}
		public float accuracyFactor(){
			return RingOfForce.this.accuracyFactor(target);
		}
	}
}

