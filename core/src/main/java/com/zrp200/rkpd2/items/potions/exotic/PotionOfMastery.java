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

package com.zrp200.rkpd2.items.potions.exotic;

import com.watabou.noosa.audio.Sample;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.Armor;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.utils.GLog;
import com.zrp200.rkpd2.windows.WndBag;
import com.zrp200.rkpd2.windows.WndOptions;

public class PotionOfMastery extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_MASTERY;

		unique = true;

		talentFactor = 2f;
	}

	protected static boolean identifiedByUse = false;

	@Override
	//need to override drink so that time isn't spent right away
	protected void drink(final Hero hero) {

		if (!isKnown()) {
			identify();
			curItem = detach( hero.belongings.backpack );
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}

		GameScene.selectItem(itemSelector);
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(PotionOfMastery.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return
					(item instanceof MeleeWeapon && !((MeleeWeapon) item).masteryPotionBonus)
					|| (item instanceof Armor && !((Armor) item).masteryPotionBonus);
		}

		@Override
		public void onSelect(Item item) {

			if (item == null && identifiedByUse){
				GameScene.show( new WndOptions(new ItemSprite(PotionOfMastery.this),
						Messages.titleCase(name()),
						Messages.get(ExoticPotion.class, "warning"),
						Messages.get(ExoticPotion.class, "yes"),
						Messages.get(ExoticPotion.class, "no") ) {
					@Override
					protected void onSelect( int index ) {
						switch (index) {
							case 0:
								curUser.spendAndNext(1f);
								identifiedByUse = false;
								break;
							case 1:
								GameScene.selectItem(itemSelector);
								break;
						}
					}
					public void onBackPressed() {}
				} );
			} else if (item != null) {

				if (item instanceof Weapon) {
					((Weapon) item).masteryPotionBonus = true;
					GLog.p( Messages.get(PotionOfMastery.class, "weapon_easier") );
				} else if (item instanceof Armor) {
					((Armor) item).masteryPotionBonus = true;
					GLog.p( Messages.get(PotionOfMastery.class, "armor_easier") );
				}
				updateQuickslot();

				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				curUser.sprite.operate(curUser.pos);

				if (!identifiedByUse) {
					curItem.detach(curUser.belongings.backpack);
				}
				identifiedByUse = false;

				if (!anonymous){
					Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
				}
			}

		}
	};
	
}
