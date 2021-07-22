package com.zrp200.rkpd2.actors.buffs;

import com.watabou.noosa.Image;

public class PowerfulDegrade extends Degrade {
    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0xff654a);
    }
}
