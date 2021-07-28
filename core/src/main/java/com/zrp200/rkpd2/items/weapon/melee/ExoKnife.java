package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PathFinder;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.effects.particles.ExoParticle;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class ExoKnife extends MeleeWeapon{
    {
        image = ItemSpriteSheet.EXO_KNIFE;
        tier = 6;
        RCH = 12;
    }

    @Override
    public int min(int lvl) {
        return 8 + lvl;
    }

    public static class RunicMissile extends Item {
        {
            image = ItemSpriteSheet.EXO_KNIFE;
        }

        @Override
        public Emitter emitter() {
            Emitter e = new Emitter();
            e.pos(7.5f, 7.5f);
            e.fillTarget = true;
            e.autoKill = false;
            e.pour(ExoParticle.FACTORY, 0.002f);
            return e;
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        for (int i : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(defender.pos+i);
            if (ch != null && ch != attacker && ch.alignment != Char.Alignment.ALLY && ch.isAlive()){
                ch.damage(damageRoll(attacker), Dungeon.hero);
                proc(ch, ch, damage);
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int max(int lvl) {
        return  4*(tier-2) +    //16 base, down from 35
                lvl*(tier-3);   //scaling changed to +3
    }
}
