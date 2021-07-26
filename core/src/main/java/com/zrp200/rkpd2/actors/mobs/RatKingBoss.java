package com.zrp200.rkpd2.actors.mobs;

import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Challenges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.blobs.*;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.rat_king.Wrath;
import com.zrp200.rkpd2.effects.*;
import com.zrp200.rkpd2.effects.particles.GodfireParticle;
import com.zrp200.rkpd2.effects.particles.SparkParticle;
import com.zrp200.rkpd2.items.artifacts.DriedRose;
import com.zrp200.rkpd2.items.wands.*;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.RatKingBossSprite;
import com.zrp200.rkpd2.tiles.DungeonTilemap;
import com.zrp200.rkpd2.ui.BossHealthBar;

import java.util.ArrayList;
import java.util.Arrays;

public class RatKingBoss extends Mob {

    public int phase = 1;
    public static final int EMPEROR = 0;
    public static final int GLADIATOR = 1;
    public static final int BATTLEMAGE = 2;
    public static final int ASSASSIN = 3;
    public static final int SNIPER = 4;

    enum MagicAttack{
        MAGIC_MISSILE(0xFFFFFF, MagicMissile.MAGIC_MISSILE),
        FIREBLAST(0xff7f00, MagicMissile.FIRE_CONE),
        FROST(0x66b3ff, MagicMissile.FROST),
        CORROSION(0xae7b49, MagicMissile.CORROSION),
        POISON(0x57135d, MagicMissile.WARD),
        LIGHTNING(0x87e6e6, MagicMissile.FROGGIT),
        BLAST_WAVE(0xb3a284, MagicMissile.FORCE),
        PRISMATIC(0xcccccc, MagicMissile.RAINBOW),
        RED_FIRE(0xff0000, MagicMissile.BLOOD_CONE),
        RAT_KING(0xffc61a, MagicMissile.ELMO);

        int color;
        int boltType;

        MagicAttack(int color, int boltType){
            this.color = color;
            this.boltType = boltType;
        }
    }
    public MagicAttack attack;
    public int magicCastPos = -1;

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

        public boolean doMagic(){
            if (magicCastPos == -1) {
                magicCastPos = enemy.pos;
                ArrayList<MagicAttack> possibleAttacks = new ArrayList<>(Arrays.asList(
                   MagicAttack.MAGIC_MISSILE, MagicAttack.FIREBLAST, MagicAttack.FROST,
                   MagicAttack.POISON, MagicAttack.BLAST_WAVE, MagicAttack.LIGHTNING
                ));
                if (Dungeon.isChallenged(Challenges.NO_HERBALISM))
                    possibleAttacks.add(MagicAttack.RED_FIRE);
                if (Dungeon.isChallenged(Challenges.NO_HEALING))
                    possibleAttacks.add(MagicAttack.CORROSION);
                if (Dungeon.isChallenged(Challenges.NO_SCROLLS))
                    possibleAttacks.add(MagicAttack.RAT_KING);
                attack = Random.element(possibleAttacks);
                Game.scene().addToFront(new TargetedCell(magicCastPos, attack.color));
                spend(TICK);
                return true;
            } else if (magicCastPos != -1) {
                sprite.zap(magicCastPos, () -> {
                            MagicMissile.boltFromChar(sprite.parent, attack.boltType, sprite, magicCastPos,
                                    () -> {
                                        next();
                                        Char ch = Actor.findChar(magicCastPos);
                                        switch (attack) {
                                            case MAGIC_MISSILE:
                                                if (ch != null) {
                                                    ch.damage(Random.NormalIntRange(15, 30), new WandOfMagicMissile());
                                                }
                                                break;
                                            case FIREBLAST:
                                                for (int i : PathFinder.NEIGHBOURS4) {
                                                    if (!Dungeon.level.solid[magicCastPos + i]) {
                                                        GameScene.add(Blob.seed(magicCastPos + i, 3, Fire.class));
                                                    }
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    GameScene.add(Blob.seed(magicCastPos, 40, Inferno.class));
                                                }
                                                if (ch != null) {
                                                    ch.damage((int) (Random.NormalIntRange(10, 23) *
                                                            (1 + 0.125f * Dungeon.hero.pointsInTalent(Talent.PYROMANIAC))), new WandOfFireblast());
                                                }
                                                break;
                                            case FROST:
                                                for (int k : PathFinder.NEIGHBOURS4) {
                                                    if (!Dungeon.level.solid[magicCastPos + k]) {
                                                        GameScene.add(Blob.seed(magicCastPos + k, 3, Freezing.class));
                                                    }
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    GameScene.add(Blob.seed(magicCastPos, 40, Blizzard.class));
                                                }
                                                if (ch != null) {
                                                    ch.damage(Random.NormalIntRange(7, 19), new WandOfFrost());
                                                    Buff.affect(ch, FrostBurn.class).reignite(ch);
                                                }
                                                break;
                                            case POISON:
                                                if (ch != null) {
                                                    ch.damage(Random.NormalIntRange(5, 21), new Poison());
                                                    Buff.affect(ch, Poison.class).set(20);
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    GameScene.add(Blob.seed(magicCastPos, 200, ToxicGas.class));
                                                }
                                                break;
                                            case LIGHTNING:
                                                if (ch != null) {
                                                    ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 5);
                                                    ch.damage(Random.NormalIntRange(24, 45), new WandOfLightning());
                                                    ch.sprite.parent.addToFront(new Lightning(ch.pos, ch.pos, null));
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    for (int k : PathFinder.NEIGHBOURS4) {
                                                        if (!Dungeon.level.solid[magicCastPos + k]) {
                                                            GameScene.add(Blob.seed(magicCastPos + k, 3, Electricity.class));
                                                        }
                                                    }
                                                }
                                                break;
                                            case CORROSION:
                                                if (ch != null) {
                                                    ch.damage(Random.NormalIntRange(8, 20), new WandOfCorrosion());
                                                    Buff.affect(ch, Corrosion.class).set(3, 6);
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    GameScene.add(Blob.seed(magicCastPos, 100, CorrosiveGas.class));
                                                }
                                                break;
                                            case PRISMATIC:
                                                if (ch != null) {
                                                    ch.damage(Random.NormalIntRange(10, 22), new WandOfPrismaticLight());
                                                    Buff.affect(ch, Blindness.class, 10f);
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    GameScene.add(Blob.seed(magicCastPos, 100, SmokeScreen.class));
                                                }
                                                break;
                                            case BLAST_WAVE:
                                                if (ch != null) {
                                                    Ballistica trajectory = new Ballistica(pos, ch.pos, Ballistica.STOP_TARGET);
                                                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                                                    WandOfBlastWave.throwChar(ch, trajectory, 3, true);
                                                    if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                        Buff.affect(ch, Paralysis.class, 2.5f);
                                                    }
                                                }
                                                break;
                                            case RED_FIRE:
                                                for (int i : PathFinder.NEIGHBOURS4) {
                                                    if (!Dungeon.level.solid[magicCastPos + i]) {
                                                        GameScene.add(Blob.seed(magicCastPos + i, 3, GodSlayerFire.class));
                                                    }
                                                }
                                                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
                                                    GameScene.add(Blob.seed(magicCastPos, 400, Inferno.class));
                                                }
                                                if (ch != null) {
                                                    ch.damage((int) (Random.NormalIntRange(15, 33) *
                                                            (1 + 0.125f * Dungeon.hero.pointsInTalent(Talent.PYROMANIAC))), new GodfireParticle());
                                                }
                                                break;
                                            case RAT_KING:
                                                sprite.parent.add(new Beam.RatRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(magicCastPos)));
                                                if (ch != null) {
                                                    ch.damage(Random.NormalIntRange(45, 80), new Wrath());
                                                    Buff.affect(ch, PowerfulDegrade.class, 20f);
                                                }
                                                break;
                                        }
                                        magicCastPos = -1;
                                    });
                        });
                Sample.INSTANCE.play(Assets.Sounds.ZAP);
                spend(TICK);

                return false;
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

                if (phase == GLADIATOR) return doCharging();

                if (phase == BATTLEMAGE) return doMagic();

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
                if (phase == BATTLEMAGE) return doMagic();

            }
            spend( TICK );
            return true;
        }

    }


}
