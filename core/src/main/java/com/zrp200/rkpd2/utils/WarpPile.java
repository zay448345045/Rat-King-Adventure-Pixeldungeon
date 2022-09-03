package com.zrp200.rkpd2.utils;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.blobs.Blob;
import com.zrp200.rkpd2.actors.blobs.Regrowth;
import com.zrp200.rkpd2.actors.buffs.*;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.actors.mobs.npcs.PrismaticImage;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.Heap;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.scrolls.ScrollOfTeleportation;
import com.zrp200.rkpd2.levels.traps.Trap;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;

import java.util.ArrayList;
import java.util.HashMap;

public class WarpPile {

    public static final int COLOR = 0xc22ec9;

    //initial wait in turns before warp will start decaying
    public static final float DECAY_DELAY = 5;
    //the (1/X) portion of warp that will be used to use an effect
    public static final float EFFECT_BASE = 15;
    //the cap for how much warp you can have
    public static final float MAX_WARP = 150;

    //the (1/X) chance for effect to come out every time the warp decays
    public static int effectTimer(float warpAmount){
        return (int) (25 - warpAmount / 10f);
    }

    public interface WarpEffect extends Callback {
        void doEffect(Hero target, float warpAmount);

        @Override
        default void call(){
            float warpAmount = Warp.stacks();
            Warp.modify(-warpAmount/EFFECT_BASE);
            Sample.INSTANCE.play(Assets.Sounds.CURSED);
            GLog.d(Messages.get(this, "message"));
            doEffect(Dungeon.hero, warpAmount);
        }
    }

    public static float[][] categoryChances = {
            {20, 5, 1},
            {10, 7, 1},
            {3, 7, 2}
    };

    public static float[] getChanceCat(int warp){
        if (warp < UNCOMMON_THRESHOLD) return categoryChances[0];
        else if (warp < RARE_THRESHOLD) return categoryChances[1];
        else return categoryChances[2];
    }

    /** Common effects **/

    public static HashMap<WarpEffect, Float> commonEffects = new HashMap<>();
    static {
        commonEffects.put(new VulnerableEffect(), 15f);
        commonEffects.put(new ScamEffect(), 12f);
        commonEffects.put(new VertigoEffect(), 10f);
        commonEffects.put(new BlindnessEffect(), 8f);
        commonEffects.put(new SlowEffect(), 8f);
        commonEffects.put(new AdrenalineEffect(), 9f);
        commonEffects.put(new FireEffect(), 7f);
        commonEffects.put(new AntimagicEffect(), 7f);
        commonEffects.put(new DegradeEffect(), 6f);
    }

    public static class DegradeEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Degrade.class, 12 + warpAmount / 5);
        }
    }

    public static class VertigoEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Vertigo.class, 6 + warpAmount / 8);
        }
    }

    public static class VulnerableEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Vulnerable.class, 12 + warpAmount / 4);
        }
    }

    public static class BlindnessEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Blindness.class, 8 + warpAmount / 5);
        }
    }

    public static class ScamEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Scam.class, 20 + warpAmount / 3);
        }
    }

    public static class AdrenalineEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Adrenaline.class, 5 + warpAmount / 12);
        }
    }

    public static class FireEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.affect(target, Burning.class).reignite(target, 3);
        }
    }

    public static class AntimagicEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.affect(target, MagicImmune.class, 6 + warpAmount / 7);
        }
    }

    public static class SlowEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.affect(target, Slow.class, 4 + warpAmount / 9);
        }
    }

    /** Uncommon effects **/

    public static int UNCOMMON_THRESHOLD = 50;
    public static HashMap<WarpEffect, Float> uncommonEffects = new HashMap<>();
    static {
        uncommonEffects.put(new ColdEffect(), 12f);
        uncommonEffects.put(new RegrowthEffect(), 10f);
        uncommonEffects.put(new ShrinkEffect(), 9f);
        uncommonEffects.put(new SummonEffect(), 8f);
        uncommonEffects.put(new VisionEffect(), 8f);
        uncommonEffects.put(new HungerEffect(), 6f);
        uncommonEffects.put(new RetributionEffect(), 4f);
    }

    public static class ColdEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, Frost.class, 9 + warpAmount / 8);
            Buff.prolong(target, Chill.class, 12 + warpAmount / 6);
        }
    }

    public static class ShrinkEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, TimedShrink.class, 6 + warpAmount / 8);
        }
    }

    public static class VisionEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.prolong(target, MagicalSight.class, 5 + warpAmount / 12);
        }
    }

    public static class RegrowthEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            GameScene.add( Blob.seed(target.pos, Math.round(40 + warpAmount / 4), Regrowth.class));
        }
    }

    public static class HungerEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            Buff.affect(target, Hunger.class).affectHunger(-Hunger.STARVING*2);
        }
    }

    public static class SpawnEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            for (int i = 0; i < 1 + warpAmount / 30; i++)
                Dungeon.level.spawnMob(8);
        }
    }

    public static class RetributionEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            float hpPercent = (MAX_WARP - warpAmount)/(float)(MAX_WARP);
            float power = Math.min( 4f, 4.45f*hpPercent);

            Sample.INSTANCE.play( Assets.Sounds.BLAST );

            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (Dungeon.level.heroFOV[mob.pos]) {
                    //deals 5%HT, plus 0-45%HP based on scaling
                    mob.damage(Math.round(mob.HT/20f + (mob.HP * power * 0.1125f)), this);
                    if (mob.isAlive()) {
                        Buff.prolong(mob, Blindness.class, Blindness.DURATION);
                    }
                }
            }
            Buff.prolong(target, Blindness.class, Blindness.DURATION/3);
            Dungeon.observe();
        }
    }

    /** Rare effects **/

    public static int RARE_THRESHOLD = 100;
    public static HashMap<WarpEffect, Float> rareEffects = new HashMap<>();
    static {
        rareEffects.put(new SummonEffect(), 10f);
        rareEffects.put(new WarpingEffect(), 4f);
    }

    public static class SummonEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            int nMobs = 1;
            if (Random.Int( 2 ) == 0) {
                nMobs++;
                if (Random.Int( 2 ) == 0) {
                    nMobs++;
                }
            }

            ArrayList<Integer> candidates = new ArrayList<>();

            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = target.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                    candidates.add( p );
                }
            }

            ArrayList<Integer> respawnPoints = new ArrayList<>();

            while (nMobs > 0 && candidates.size() > 0) {
                int index = Random.index( candidates );

                respawnPoints.add( candidates.remove( index ) );
                nMobs--;
            }

            ArrayList<Mob> mobs = new ArrayList<>();

            for (Integer point : respawnPoints) {
                EnemyImage mob = new EnemyImage();
                mob.duplicate(target, (int) (warpAmount*2f));
                mob.state = mob.WANDERING;
                Buff.affect(mob, Doom.class);
                mob.pos = point;
                GameScene.add(mob, 1);
                mobs.add(mob);
            }

            //important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
            Trap t;
            for (Mob mob : mobs){
                //manually trigger traps first to avoid sfx spam
                if ((t = Dungeon.level.traps.get(mob.pos)) != null && t.active){
                    if (t.disarmedByActivation) t.disarm();
                    t.reveal();
                    t.activate();
                }
                ScrollOfTeleportation.appear(mob, mob.pos);
                Dungeon.level.occupyCell(mob);
            }

        }

        public static class EnemyImage extends PrismaticImage{

            {
                alignment = Alignment.ENEMY;
                WANDERING = new Wandering();
            }


            private class Wandering extends Mob.Wandering{

                @Override
                public boolean act(boolean enemyInFOV, boolean justAlerted) {
                    return super.act(enemyInFOV, justAlerted);
                }

            }
        }

    }

    public static class WarpingEffect implements WarpEffect {
        @Override
        public void doEffect(Hero target, float warpAmount) {
            CellEmitter.get(target.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
            Sample.INSTANCE.play( Assets.Sounds.TELEPORT );

            Char ch = Actor.findChar( target.pos);
            if (ch instanceof Hero){
                ScrollOfTeleportation.teleportHero( (Hero)ch);
                BArray.setFalse(Dungeon.level.visited);
                BArray.setFalse(Dungeon.level.mapped);
                Dungeon.observe();

            } else if (ch != null){
                int count = 10;
                int pos;
                do {
                    pos = Dungeon.level.randomRespawnCell(target);
                    if (count-- <= 0) {
                        break;
                    }
                } while (pos == -1);

                if (pos == -1) {

                    GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );

                } else {

                    ch.pos = pos;
                    if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).HUNTING){
                        ((Mob) ch).state = ((Mob) ch).WANDERING;
                    }
                    ch.sprite.place(ch.pos);
                    ch.sprite.visible = Dungeon.level.heroFOV[pos];

                }
            }

            Heap heap = Dungeon.level.heaps.get(target.pos);

            if (heap != null){
                int cell = Dungeon.level.randomRespawnCell(target);

                Item item = heap.pickUp();

                if (cell != -1) {
                    Dungeon.level.drop( item, cell );
                }
            }
        }
    }

    public static HashMap[] effectTypes = new HashMap[]{commonEffects, uncommonEffects, rareEffects};
}
