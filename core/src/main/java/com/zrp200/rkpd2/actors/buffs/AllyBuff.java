package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.actors.Char;

public class AllyBuff extends Buff{
    @Override
    public boolean attachTo(Char target) {
        target.alignment = Char.Alignment.ALLY;
        return super.attachTo(target);
    }
}
