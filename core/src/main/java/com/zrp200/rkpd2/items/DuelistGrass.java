package com.zrp200.rkpd2.items;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.ShatteredPixelDungeon;
import com.zrp200.rkpd2.actors.hero.Belongings;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.particles.LeafParticle;
import com.zrp200.rkpd2.items.bags.Bag;
import com.zrp200.rkpd2.items.weapon.melee.MeleeWeapon;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.plants.Plant;
import com.zrp200.rkpd2.plants.Rotberry;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.utils.GLog;
import com.zrp200.rkpd2.windows.WndBag;

import java.util.ArrayList;

import static com.zrp200.rkpd2.Dungeon.hero;

public class DuelistGrass extends Item {

    {
        image = ItemSpriteSheet.GRASS;

        stackable = true;

        // I just said I don't want any more grass content!
        bones = false;
    }

    public static String AC_IMBUE = "IMBUE";
    public static final int CHARGE_FROM_GRASS = 3;

    public static int getAbilityGrassCost() {
        return 7 - hero.pointsInTalent(Talent.GRASSY_OFFENSE);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.hasTalent(Talent.GRASSY_OFFENSE))
            actions.add(AC_IMBUE);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals(AC_IMBUE)) {

            curUser = hero;
            GameScene.selectItem( itemSelector );

        }
    }

    private void imbue( MeleeWeapon weapon ) {

        if (!weapon.isIdentified() ){
            GLog.w( Messages.get(this, "identify"));
            return;
        } else if (weapon.cursed || weapon.hasCurseEnchant()){
            GLog.w( Messages.get(this, "cursed"));
            return;
        }

        int imbueQuantity = Math.min(quantity(), 15);

        if (quantity() <= imbueQuantity){
            detachAll(curUser.belongings.backpack);
        } else {
            quantity(quantity() - imbueQuantity);
        }

        GLog.w( Messages.get(this, "imbued", imbueQuantity*CHARGE_FROM_GRASS));

        weapon.grass += imbueQuantity*CHARGE_FROM_GRASS;

        curUser.sprite.operate(curUser.pos);
        curUser.sprite.centerEmitter().start(LeafParticle.LEVEL_SPECIFIC, 0.05f, 20);
        Sample.INSTANCE.play(Assets.Sounds.PLANT);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);

        curUser.spend(2f);
        curUser.busy();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    //being a lawnmower is very profitable
    @Override
    public int value() {
        return 15 * quantity;
    }

    @Override
    protected void onThrow( int cell ) {
        if (Dungeon.level.pit[cell]
                || Dungeon.level.traps.get(cell) != null) {
            super.onThrow(cell);
        } else {
            int c = Dungeon.level.map[cell];
            if ( c == Terrain.EMPTY || c == Terrain.EMPTY_DECO
                    || c == Terrain.EMBERS || c == Terrain.GRASS){
                Level.set(cell, Terrain.HIGH_GRASS);
                GameScene.updateMap(cell);
                CellEmitter.get( cell).burst( LeafParticle.LEVEL_SPECIFIC, 4 );
                if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
                    Sample.INSTANCE.play(Assets.Sounds.PLANT);
                }
            }
        }
    }

    private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(DuelistGrass.class, "imbue_prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof MeleeWeapon;
        }

        @Override
        public void onSelect( Item item ) {
            if (item != null) {
                DuelistGrass.this.imbue((MeleeWeapon) item);
            }
        }
    };

    public static class SeedDuplicationRecipe extends Recipe {

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            if (ingredients.size() != 2) return false;

            int grassQuantity = 0;
            boolean seedPresent = false;
            for (Item item: ingredients){
                if (item instanceof DuelistGrass) grassQuantity = item.quantity();
                if (item instanceof Plant.Seed){
                    seedPresent = !(item instanceof Rotberry.Seed);
                }
            }

            return seedPresent && grassQuantity <= 5;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 3;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!testIngredients(ingredients)) return null;

            Class<? extends Plant.Seed> plantClass = null;

            for (Item item: ingredients){
                if (item instanceof DuelistGrass) {
                    item.quantity(item.quantity() - 5);
                } else if (item instanceof Plant.Seed){
                    item.quantity(0);
                    plantClass = (Class<? extends Plant.Seed>) item.getClass();
                }
            }

            return Reflection.newInstance(plantClass).quantity(2);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            for (Item item : ingredients){
                if (item instanceof Plant.Seed){
                    try {
                        return Reflection.newInstance(item.getClass());
                    } catch (Exception e) {
                        ShatteredPixelDungeon.reportException( e );
                        return null;
                    }
                }
            }
            return null;
        }
    }

}
