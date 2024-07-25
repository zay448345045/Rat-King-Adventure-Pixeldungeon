/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.MagicImmune;
import com.zrp200.rkpd2.actors.buffs.MonkEnergy;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.curses.Wayward;
import com.zrp200.rkpd2.items.weapon.enchantments.Projecting;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.AttackIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.GLog;

import java.util.ArrayList;

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

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (hero.buff(BrawlersStance.class) != null && hero.buff(Force.class) == null){
				//clear brawler's stance if no ring of force is equipped
				hero.buff(BrawlersStance.class).detach();
			}
			return true;
		} else {
			return false;
		}
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
		if (hero.buff(Force.class) != null
				&& hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) == null) {
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
		if (lvl <= 0) tier = 1; //tier is forced to 1 if cursed

		return Math.max( 0, Math.round(
				tier-1 +  //base
				lvl     //level scaling
		));
	}

	//same as equivalent tier weapon
	private static int max(int lvl, float tier){
		if (lvl <= 0) tier = 1; //tier is forced to 1 if cursed

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
		int level = isIdentified() ? soloBuffedBonus() : 1;
		String info = Messages.get(this, isIdentified()?"stats":"typical_stats", min(level, tier), max(level, tier), level);
		if (isIdentified()) {
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				level = combinedBuffedBonus(Dungeon.hero);
				info += "\n\n" + Messages.get(this, "combined_stats", min(level, tier), max(level, tier), level);
			}
		}

		return info;
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

	//Duelist stuff

	public static String AC_ABILITY = "ABILITY";

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.DUELIST){
			Buff.affect(ch, MeleeWeapon.Charger.class);
		}
	}

	@Override
	public String defaultAction() {
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST){
			return AC_ABILITY;
		} else {
			return super.defaultAction();
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && hero.heroClass == HeroClass.DUELIST){
			actions.add(AC_ABILITY);
		}
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_ABILITY)){
			return Messages.upperCase(Messages.get(this, "ability_name"));
		} else {
			return super.actionName(action, hero);
		}
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_ABILITY)){
			if (hero.buff(BrawlersStance.class) != null){
				hero.buff(BrawlersStance.class).detach();
				AttackIndicator.updateState();
			} else if (!isEquipped(hero)) {
				GLog.w(Messages.get(MeleeWeapon.class, "ability_need_equip"));

			} else if ((Buff.affect(hero, MeleeWeapon.Charger.class).charges[0])
					< BrawlersStance.HIT_CHARGE_USE){
				GLog.w(Messages.get(MeleeWeapon.class, "ability_no_charge"));

			} else {
				Buff.affect(hero, BrawlersStance.class);
				AttackIndicator.updateState();
			}
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public String info() {
		String info = super.info();

		if (Dungeon.hero.heroClass == HeroClass.DUELIST
			&& (anonymous || isIdentified() || isEquipped(Dungeon.hero))){
			info += "\n\n" + Messages.get(this, "ability_desc");
		}

		return info;
	}

	public static boolean fightingUnarmed( Hero hero ){
		if (hero.belongings.attackingWeapon() == null
			|| hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null){
			return true;
		}
		if (hero.belongings.thrownWeapon != null || hero.belongings.abilityWeapon != null){
			return false;
		}
		BrawlersStance stance = hero.buff(BrawlersStance.class);
		if (stance != null && stance.hitsLeft() > 0){
			//clear the buff if no ring of force is equipped
			if (hero.buff(RingOfForce.Force.class) == null){
				stance.detach();
				AttackIndicator.updateState();
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static boolean unarmedGetsWeaponEnchantment( Hero hero ){
		if (hero.belongings.attackingWeapon() == null){
			return false;
		}
		if (hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null){
			return hero.buff(MonkEnergy.MonkAbility.FlurryEmpowerTracker.class) != null;
		}
		BrawlersStance stance = hero.buff(BrawlersStance.class);
		if (stance != null && stance.hitsLeft() > 0){
			return true;
		}
		return false;
	}

	public static boolean unarmedGetsWeaponAugment(Hero hero ){
		if (hero.belongings.attackingWeapon() == null
			|| hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null){
			return false;
		}
		BrawlersStance stance = hero.buff(BrawlersStance.class);
		if (stance != null && stance.hitsLeft() > 0){
			return true;
		}
		return false;
	}

	public static class BrawlersStance extends Buff {

		public static float HIT_CHARGE_USE = 1/6f;

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		public int hitsLeft(){
			MeleeWeapon.Charger charger = Buff.affect(target, MeleeWeapon.Charger.class);
			return (int)(charger.charges[0]/HIT_CHARGE_USE);
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_BRAWL;
		}

		@Override
		public void tintIcon(Image icon) {
			if (hitsLeft() == 0){
				icon.brightness(0.25f);
			} else {
				icon.resetColor();
			}
		}

		@Override
		public float iconFadePercent() {
			float usableCharges = hitsLeft()*HIT_CHARGE_USE;

			return 1f - (usableCharges /  Buff.affect(target, MeleeWeapon.Charger.class).chargeCap());
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(hitsLeft());
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", hitsLeft());
		}
	}
}

