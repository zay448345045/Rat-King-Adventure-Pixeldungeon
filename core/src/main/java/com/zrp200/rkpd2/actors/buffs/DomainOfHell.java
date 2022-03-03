package com.zrp200.rkpd2.actors.buffs;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.blobs.Blob;
import com.zrp200.rkpd2.actors.blobs.Fire;
import com.zrp200.rkpd2.levels.Terrain;
import com.zrp200.rkpd2.scenes.GameScene;

public class DomainOfHell extends Buff{

    {
        actPriority = HERO_PRIO + 60;
    }

    @Override
    public boolean act() {
        if (target.isAlive()){
            for (int i = 0; i < Dungeon.level.map.length; i++){
                if (Dungeon.level.map[i] == Terrain.GRASS ||
                        Dungeon.level.map[i] == Terrain.FURROWED_GRASS ||
                        Dungeon.level.map[i] == Terrain.HIGH_GRASS){
                    GameScene.add(Blob.seed(i, 2, Fire.class));
                }
            }
        } else {
            detach();
        }
        spend(TICK/5);

        return true;
    }
}
