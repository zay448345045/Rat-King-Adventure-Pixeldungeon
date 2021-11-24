package com.zrp200.rkpd2.items.quest;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class Kromer extends Item {

    {
        image = ItemSpriteSheet.KROMER;
        stackable = true;
        cursed = true;
        cursedKnown = true;
    }

    private static final ItemSprite.Glowing CHAOTIC = new ItemSprite.Glowing( 0.2f  );

    @Override
    public ItemSprite.Glowing glowing() {
        return CHAOTIC;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return quantity * Random.Int(8, 43412);
    }
}
