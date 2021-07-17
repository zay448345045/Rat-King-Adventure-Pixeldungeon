package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class RoyalBrand extends Crossbow {
    {
        image = ItemSpriteSheet.ROYAL_SWORD;
        hitSound = Assets.Sounds.HIT_STRONG;
        hitSoundPitch = 1.4f;

        tier = 6;
        ACC = 1.24f;
        DLY = 0.8f;
        RCH = 3;
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(lvl)+1;
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +    //21 damage,
                lvl*(tier+2);     //+8 per level, down from +7
    }

    @Override
    public int defenseFactor( Char owner ) {
        return 2+2*buffedLvl();    //6 extra defence, plus 3 per level;
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 2+2*buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 2);
        }
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals 50% toward max to max on surprise, instead of min to max.
                int diff = max() - min();
                int damage = augment.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff*0.50f),
                        max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }


}
