package com.zrp200.rkpd2.items;

import com.watabou.noosa.audio.Sample;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.particles.LeafParticle;
import com.zrp200.rkpd2.levels.Level;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class DuelistGrass extends Item {

    {
        image = ItemSpriteSheet.GRASS;

        stackable = true;

        // I just said I don't want any more grass content!
        bones = false;
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

}
