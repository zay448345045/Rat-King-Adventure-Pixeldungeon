package com.zrp200.rkpd2.actors.mobs;

import com.watabou.noosa.audio.Sample;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Challenges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.FlavourBuff;
import com.zrp200.rkpd2.items.artifacts.DriedRose;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.RatKingBossSprite;
import com.zrp200.rkpd2.ui.BossHealthBar;

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
        alignment = Alignment.ENEMY;

        viewDistance = 12;

        properties.add(Property.BOSS);
        properties.add(Property.MINIBOSS);
    }

    @Override
    public void notice() {
        super.notice();
        Buff.affect(this, PhaseTracker.class, 0);
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            if (HP <= HT/2) BossHealthBar.bleed(true);
            if (HP == HT) {
                for (Char ch : Actor.chars()){
                    if (ch instanceof DriedRose.GhostHero){
                        ((DriedRose.GhostHero) ch).sayBoss();
                    }
                }
            } else {
                yell(Messages.get(this, "notice_have", Dungeon.hero.name()));
            }
        }
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
            Sample.INSTANCE.play(Assets.Sounds.CHALLENGE, 2f, 0.85f);
            Buff.affect(target, PhaseTracker.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 35 : 20);
        }
    }

    public boolean haventSeen = true;

    @Override
    protected boolean act() {
        if (Dungeon.level.heroFOV[pos] && haventSeen) {
            notice();
            haventSeen = false;
        }
        return super.act();
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
