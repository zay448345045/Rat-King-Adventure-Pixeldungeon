package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
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

    @Override
    public int warriorAttack(int damage, Char enemy) {
        Buff.affect(enemy, Ooze.class).set(30);
        Buff.affect(enemy, Chill.class, 15);
        Buff.affect(enemy, Burning.class).reignite(enemy, 10);
        Buff.affect(enemy, Poison.class).set(15);
        Buff.affect(enemy, Hex.class, 15);
        Buff.affect(enemy, Vulnerable.class, 15);
        Buff.affect(enemy, Weakness.class, 15);
        Buff.affect(enemy, Vertigo.class, 15);
        Buff.affect(enemy, Terror.class, 15).object = Dungeon.hero.id();
        return super.warriorAttack(damage, enemy);
    }
}
