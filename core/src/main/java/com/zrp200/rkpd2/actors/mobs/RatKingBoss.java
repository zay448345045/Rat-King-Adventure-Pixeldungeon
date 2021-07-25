package com.zrp200.rkpd2.actors.mobs;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Challenges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.artifacts.DriedRose;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.CharSprite;
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
        defenseSkill = 35;

        properties.add(Property.BOSS);
        properties.add(Property.MINIBOSS);
    }

    @Override
    public boolean canAttack(Char enemy) {
        if (phase == GLADIATOR) return super.canAttack(enemy);
        return super.canAttack(enemy);
    }

    @Override
    public float speed() {
        if (phase == GLADIATOR){
            return super.speed()*1.5f;
        }
        return super.speed();
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (phase == GLADIATOR){
            if (Dungeon.isChallenged(Challenges.NO_SCROLLS) && Random.Int(2) == 0)
                Buff.prolong(enemy, PowerfulDegrade.class, 3f);
            if (Dungeon.isChallenged(Challenges.NO_ARMOR) && Random.Int(2) == 0) {
                Buff.prolong(enemy, Vulnerable.class, 3f);
                Buff.prolong(enemy, Charm.class, 2f);
            }
            if (Dungeon.isChallenged(Challenges.NO_FOOD) && Random.Int(2) == 0){
                if (enemy instanceof Hero){
                    enemy.buff(Hunger.class).affectHunger(-30);
                }
                int healAmt = Math.round(damage*0.75f);
                healAmt = Math.min( healAmt, enemy.HT - enemy.HP );

                if (healAmt > 0 && isAlive()) {
                    HP += healAmt;
                    sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 1 );
                    sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmt ) );
                }
            }
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public int damageRoll() {
        if (phase == GLADIATOR){
            return Random.NormalIntRange(24, 64);
        }
        return super.damageRoll();
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

    @Override
    public int attackSkill(Char target) {
        return 45;
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
    private static final String PHASE = "phase";
    private static final String HAVESEEN = "haveseen";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PHASE, phase);
        bundle.put(HAVESEEN, haventSeen);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        phase = bundle.getInt(PHASE);
        haventSeen = bundle.getBoolean(HAVESEEN);
    }

    //rat king is always hunting
    private class Hunting extends Mob.Hunting{

        public boolean doCharging(){
            int oldPos = pos;
            if (target != -1 && getCloser( target )) {

                if (Dungeon.level.water[pos] && buff(ChampionEnemy.Flowing.class) != null){
                    spend(0.01f / speed());
                }
                else spend( 1 / speed() );
                return moveSprite( oldPos,  pos );

            } else {

                spend( TICK );
                return true;
            }
        }

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

                if (phase == GLADIATOR) return doCharging();

                spend( TICK );
                return true;

            }
        }
    }


}
