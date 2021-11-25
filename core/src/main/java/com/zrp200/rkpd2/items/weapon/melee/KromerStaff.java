package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Doom;
import com.zrp200.rkpd2.actors.buffs.Scam;
import com.zrp200.rkpd2.items.bombs.Bomb;
import com.zrp200.rkpd2.items.wands.CursedWand;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.wands.WandOfUnstable;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class KromerStaff extends MagesStaff {

    {
        image = ItemSpriteSheet.KROMER_STAFF;
        tier = 2;
    }

    public KromerStaff(){
        wand = null;
    }

    @Override
    public int level() {
        return (int) (super.level()*1.5f);
    }

    public KromerStaff(Wand wand){
        super(wand);
    }

    @Override
    public void updateWand(boolean levelled){
        if (wand != null) {
            int curCharges = curCharges();
            wand.level(level());
            //gives the wand one additional max charge
            wand.maxCharges = Math.min(wand.maxCharges*2, 20);
            wand.curCharges = Math.min(curCharges + (levelled ? 2 : 0), wand.maxCharges);
            updateQuickslot();
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        kromerProc(attacker, defender);
        return super.proc(attacker, defender, damage);
    }

    public void kromerProc(Char attacker, Char defender) {
        switch (Random.Int(5)){
            case 0:
                new Bomb().explode(defender.pos); break;
            case 1:
                Buff.prolong(attacker, Scam.class, 2); break;
            case 2:
                Buff.affect(defender, Doom.class); break;
            case 3:
                CursedWand.cursedZap(null, attacker,
                        new Ballistica(attacker.pos, defender.pos, Ballistica.PROJECTILE), () -> {}); break;
            case 4:
                WandOfUnstable wand = new WandOfUnstable();
                wand.upgrade(level());
                wand.fx(new Ballistica(Dungeon.hero.pos, defender.pos, Ballistica.STOP_TARGET), () -> {
                    wand.onZap(new Ballistica(Dungeon.hero.pos, defender.pos, Ballistica.STOP_TARGET));
                }); break;
        }
    }

    public void procWand(Char defender, int damage) {
        wand.onHit(this, Dungeon.hero,defender, (int) (damage*1.5f));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (wand != null) {
            wand.maxCharges = Math.min(wand.maxCharges*2, 20);
        }
    }
}
