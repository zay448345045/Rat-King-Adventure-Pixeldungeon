package com.zrp200.rkpd2.actors.buffs;

import com.watabou.noosa.Image;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.items.BrokenSeal;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.ui.BuffIndicator;

import java.text.DecimalFormat;

public class BrawlerBuff extends CounterBuff {

    public int maxCharge(){
        return 15 + Dungeon.hero.pointsInTalent(Talent.POWER_STABILIZER)*5;
    }

    public int damageFactor(int damage){
        return (int) (damage*(1f + Math.min(damageModifier()*maxCharge(), count()*damageModifier())));
    }

    public float damageModifier(){
        return 0.04f;
    }

    @Override
    public boolean act() {
        if (count() < maxCharge()){
            countUp(getInc());
        }

        spend(TICK);
        return true;
    }

    public float getInc() {
        if (Dungeon.hero.pointsInTalent(Talent.IN_MY_MEMORIES) == 3){
            BrokenSeal.WarriorShield shield = Dungeon.hero.buff(BrokenSeal.WarriorShield.class);
            if (shield.maxShield() == shield.shielding()){
                return 0.266f;
            }
        }
        return 0.334f;
    }

    @Override
    public int icon() {
        return BuffIndicator.UPGRADE;
    }

    @Override
    public void tintIcon(Image icon) {
        float r,g,b;
                r = 1;
                g = 0.54f * (1 - Math.max(0, count()/maxCharge() - 0.4f));
                b = 0.70f * (1 - count()/maxCharge()*1.2f);
        icon.hardlight(r,g,b);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int)count(), new DecimalFormat("#.#").format(Math.min(damageModifier()*maxCharge(), count()*damageModifier())*100));
    }
}
