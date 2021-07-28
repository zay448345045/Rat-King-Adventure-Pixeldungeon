package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class ElementalDirk extends AssassinsBlade {
    {
        image = ItemSpriteSheet.ELEMENTAL_DIRK;
        tier = 6;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (Random.Float() < (buffedLvl()+1f/buffedLvl()+2f)) {
            switch (Random.Int(4)) {
                case 0:
                    Buff.affect(defender, Burning.class).reignite(defender, 5);
                    break;
                case 1:
                    Buff.affect(defender, Chill.class, 5f);
                    break;
                case 2:
                    Buff.affect(defender, Ooze.class);
                    break;
                case 3:
                    Buff.affect(defender, Poison.class).set(12);
                    break;
            }
        }
        return super.proc(attacker, defender, damage);
    }
}
