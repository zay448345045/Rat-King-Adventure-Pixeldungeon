package com.zrp200.rkpd2.items.quest;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.hero.abilities.Ratmogrify;
import com.zrp200.rkpd2.actors.hero.abilities.rat_king.LegacyWrath;
import com.zrp200.rkpd2.actors.hero.abilities.rat_king.MusRexIra;
import com.zrp200.rkpd2.actors.hero.abilities.rat_king.Wrath;
import com.zrp200.rkpd2.effects.Pushing;
import com.zrp200.rkpd2.effects.Splash;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.RatKingArmor;
import com.zrp200.rkpd2.scenes.GameScene;
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
    protected void onThrow(int cell) {
        RatKingArmor armor = new RatKingArmor();
        armor.charge = 100f;
        Splash.at(cell, 0x0bd74e, 60);
        Actor.addDelayed(new Pushing(Dungeon.hero, Dungeon.hero.pos, Dungeon.hero.pos, () -> {
            GameScene.flash(0xfdfa31);
            Splash.at(cell, 0xfdfa31, 60);
            new LegacyWrath().activate(armor, Dungeon.hero, cell);
            new Wrath().activate(armor, Dungeon.hero, cell);
            new MusRexIra().activate(armor, Dungeon.hero, cell);
            new Ratmogrify().activate(armor, Dungeon.hero, cell);
        }), -1);
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
