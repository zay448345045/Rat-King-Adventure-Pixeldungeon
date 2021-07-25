package com.zrp200.rkpd2.actors.mobs;

import com.zrp200.rkpd2.Challenges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.RatKingBossSprite;

public class RatKingBoss extends Mob {

    public int phase = 0;
    public static final int EMPEROR = 0;
    public static final int GLADIATOR = 1;
    public static final int BATTLEMAGE = 2;
    public static final int ASSASSIN = 3;
    public static final int SNIPER = 4;

    {
        HP = HT = 2000 + Challenges.activeChallenges()*111;
        spriteClass = RatKingBossSprite.class;

        HUNTING = new Hunting();
        state = HUNTING;

        viewDistance = 12;

        properties.add(Property.BOSS);
        properties.add(Property.MINIBOSS);
    }

    public void changePhase(){
        if (++phase > 4) phase = 0;
        ((RatKingBossSprite)sprite).changeSprite(phase);
    }

    public static class PhaseTracker extends FlavourBuff{
        @Override
        public void detach() {
            super.detach();
            ((RatKingBoss)target).changePhase();
            GameScene.flash(0xffc61a);
            Buff.affect(target, PhaseTracker.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 35 : 20);
        }
    }

    //rat king is always hunting
    private class Hunting extends Mob.Hunting{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {

            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

                target = enemy.pos;

                return doAttack( enemy );

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else {
                    chooseEnemy();
                    if (enemy == null){
                        //if nothing else can be targeted, target hero
                        enemy = Dungeon.hero;
                    }
                    target = enemy.pos;
                }

                spend( TICK );
                return true;

            }
        }
    }


}
