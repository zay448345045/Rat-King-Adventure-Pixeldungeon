package com.zrp200.rkpd2.items.weapon;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.items.quest.Kromer;
import com.zrp200.rkpd2.items.rings.RingOfSharpshooting;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class KromerBow extends SpiritBow {
    {
        image = ItemSpriteSheet.KROMER_BOW;
    }

    @Override
    public int level() {
        return (int) (super.level()*2f);
    }

    @Override
    public int STRReq(int lvl) {
        return Dungeon.hero.STR;
    }

    @Override
    public int min(int lvl) {
        return (1 + level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero));
    }

    @Override
    public int max(int lvl) {
        return 12 + (2*(level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)));
    }

    @Override
    public SpiritArrow knockArrow(){
        if (superShot){
            return new SuperShot();
        }
        return new KromerShot();
    }


    public class KromerShot extends SpiritArrow{
        {
            hitSound = Assets.Sounds.HIT_STRONG;
        }

        @Override
        public int image() {
            return ItemSpriteSheet.SR_RANGED;
        }

        @Override
        public void onThrow(int cell) {
            superShot = false;
            super.onThrow(cell);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            switch (Random.Int(7)){
                case 0: default:
                    Buff.prolong(attacker, TimedShrink.class, 2f); break;
                case 1:
                    Buff.prolong(attacker, Blindness.class, 2f); break;
                case 2:
                    Buff.prolong(attacker, Adrenaline.class, 1f); break;
                case 3:
                    Buff.prolong(attacker, Vertigo.class, 2f); break;
                case 4:
                    Buff.prolong(attacker, Scam.class, 2f); break;
                case 5:
                    Buff.prolong(attacker, Roots.class, 2f); break;
                case 6:
                    Buff.prolong(attacker, MagicImmune.class, 2f); break;
            }
            return super.proc(attacker, defender, damage);
        }
    }

    public static class Recipe extends com.zrp200.rkpd2.items.Recipe.SimpleRecipeBundled {

        {
            inputs =  new Class[]{SpiritBow.class, Kromer.class};
            inQuantity = new int[]{1, 1};

            cost = 20;

            output = KromerBow.class;
            outQuantity = 1;
        }

    }
}
