package com.zrp200.rkpd2.actors.buffs;

import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.sprites.HeroSprite;
import com.zrp200.rkpd2.ui.ActionIndicator;

public class RobotBuff extends Buff implements ActionIndicator.Action {
    {
        immunities.add(Sleep.class);
        immunities.add(Drowsy.class);
        properties.add(Char.Property.INORGANIC);
    }

    @Override
    public Image getIcon() {
        if (target != null && target.buff(RobotTransform.class) != null)
            return HeroSprite.avatar(HeroClass.ROGUE, 7);
        else
            return HeroSprite.avatar(HeroClass.ROGUE, 8);
    }

    @Override
    public void doAction() {
        if (target.buff(RobotTransform.class) == null){
            Buff.affect(target, RobotTransform.class);
        } else {
            Buff.detach(target, RobotTransform.class);
        }
        ((HeroSprite)(target.sprite)).updateArmor();
        target.sprite.bottomEmitter().burst(Speck.factory(Speck.WOOL), 12);
        Sample.INSTANCE.play(Assets.Sounds.MASTERY, 1f, 0.8f);
        target.sprite.operate(target.pos, target.sprite::idle);
        ActionIndicator.updateIcon();
        Dungeon.hero.spendAndNext(Actor.TICK*3);
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public boolean act() {
        ActionIndicator.setAction(this);

        spend(TICK);
        return true;
    }
}
