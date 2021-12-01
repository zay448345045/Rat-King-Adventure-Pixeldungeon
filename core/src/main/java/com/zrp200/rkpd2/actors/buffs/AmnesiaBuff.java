package com.zrp200.rkpd2.actors.buffs;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.scenes.GameScene;

public class AmnesiaBuff extends Buff{
    @Override
    public boolean act() {
        if (target.isAlive()) {
            spend(TICK);
            for (int i = 0; i < Dungeon.level.visited.length; i++){
                if (Random.Int(6) == 0 && Dungeon.level.visited[i]){
                    Dungeon.level.visited[i] = false;
                }
            }
            for (int i = 0; i < Dungeon.level.mapped.length; i++){
                if (Random.Int(6) == 0 && Dungeon.level.mapped[i]){
                    Dungeon.level.mapped[i] = false;
                }
            }
            GameScene.updateFog();
        } else {
            detach();
        }
        return true;
    }
}
