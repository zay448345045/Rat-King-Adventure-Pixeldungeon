package com.zrp200.rkpd2.actors.buffs;

import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.effects.SpellSprite;
import com.zrp200.rkpd2.items.BrokenSeal;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.ActionIndicator;
import com.zrp200.rkpd2.ui.BuffIndicator;

import java.text.DecimalFormat;

public class WarriorParry extends CounterBuff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    public float maxCharge(){
        BrokenSeal.WarriorShield buff = target.buff(BrokenSeal.WarriorShield.class);
        if (buff != null){
            return buff.maxShield();
        }
        return BrokenSeal.maxShieldFromTalents(false);
    }

    public float getInc() {
        BrokenSeal.WarriorShield shield = Dungeon.hero.buff(BrokenSeal.WarriorShield.class);
        if (shield != null){
            return shield.getRechargeRate() * 0.5f;
        }
        return 1f / (30f / 0.5f);
    }

    @Override
    public boolean act() {
        if (count() < maxCharge() && target.buff(BlockTrock.class) == null){
            countUp(getInc());
        }
        if (count() >= 1){
            ActionIndicator.setAction(this);
        }
        if (count() > maxCharge()){
            countDown(count()-maxCharge());
        }

        spend(TICK);
        return true;
    }

    @Override
    public Image actionIcon() {
        Image actionIco = new Image(Assets.Sprites.ITEM_ICONS);
        actionIco.frame(ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_SHIELDING));
        actionIco.scale.set(2f);
        actionIco.hardlight(2, 2, 2);
        return actionIco;
    }

    public static class BlockTrock extends Buff{

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        private int pos;

        @Override
        public boolean attachTo( Char target ) {
            pos = target.pos;
            return super.attachTo( target );
        }

        @Override
        public void detach() {
            super.detach();
            target.sprite.idle();
        }

        @Override
        public boolean act() {
            if (target.pos != pos) {
                detach();
            }
            spend( TICK );
            return true;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(2,2,2);
        }

        private static final String POS		= "pos";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( POS, pos );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            pos = bundle.getInt( POS );
        }
    }

    @Override
    public void doAction() {
        SpellSprite.show(target, SpellSprite.MAP, (count()/maxCharge())*2, (count()/maxCharge())*2, (count()/maxCharge())*2);
        Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 1f);
        countDown(1f);
        BuffIndicator.refreshHero();
        ActionIndicator.clearAction(this);
        target.sprite.operate(target.pos, () -> {
            Buff.affect(target, BlockTrock.class);
            ((Hero)target).spendAndNext(Actor.TICK);
            Item.updateQuickslot();
        });
    }

    @Override
    public boolean usable() {
        return count() >= 1f;
    }

    @Override
    public int icon() {
        return BuffIndicator.ARMOR;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", new DecimalFormat("#.##").format(count()), Math.round(maxCharge()));
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (maxCharge() - count()) / maxCharge());
    }

    @Override
    public String iconTextDisplay() {
        return new DecimalFormat("#.##").format(count());
    }

    @Override
    public void tintIcon(Image icon) {
        float r,g,b;
        r = g = b = count()/maxCharge()*2f;
        icon.hardlight(r,g,b);
    }
}
