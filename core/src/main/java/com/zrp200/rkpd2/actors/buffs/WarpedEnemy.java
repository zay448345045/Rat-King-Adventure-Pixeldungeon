package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.ui.BuffIndicator;

public class WarpedEnemy extends Buff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.WARP;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.WARPED);
        else target.sprite.remove(CharSprite.State.WARPED);
    }

    {
        immunities.add(Charm.class);
        immunities.add(Vertigo.class);
        immunities.add(Terror.class);
    }

    //special variant, used for boss buffs
    public static class BossEffect extends Buff {
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.WARPED);
            else target.sprite.remove(CharSprite.State.WARPED);
        }

        {
            immunities.add(Charm.class);
            immunities.add(Vertigo.class);
            immunities.add(Terror.class);
        }
    }
}
