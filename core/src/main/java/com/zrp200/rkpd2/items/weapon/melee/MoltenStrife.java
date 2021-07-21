package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.items.bombs.Bomb;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class MoltenStrife extends MeleeWeapon {
    {
        image = ItemSpriteSheet.MOLTEN_STRIFE;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.6f;

        tier = 6;
    }

    @Override
    public int max(int lvl) {
        return 4*(tier+1) + tier*lvl;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (Random.Int(5+buffedLvl()) < (buffedLvl()+1)) {
            Bomb.doNotDamageHero = true;
            new Bomb().explode(defender.pos);
            Bomb.doNotDamageHero = false;
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String statsInfo() {
        if (isIdentified())
            return Messages.get(this, "stats_desc", new DecimalFormat("#.#").
                    format(100 * ((buffedLvl()+1f) / (5f+buffedLvl()))));
        return Messages.get(this, "stats_desc", new DecimalFormat("#.#").format(20));
    }
}
