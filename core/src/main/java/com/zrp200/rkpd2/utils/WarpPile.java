package com.zrp200.rkpd2.utils;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.buffs.Warp;

import java.util.HashMap;

public class WarpPile {

    public static final int COLOR = 0xc22ec9;

    //initial wait in turns before warp will start decaying
    public static final float DECAY_DELAY = 8;
    //the (1/X) portion of warp that will be used to use an effect
    public static final float EFFECT_BASE = 5;
    //the cap for how much warp you can have
    public static final float MAX_WARP = 150;
    //the (1/X) chance for effect to come out every time the warp decays
    public static final int EFFECT_CHANCE = 10;

    public interface WarpEffect extends Callback {
        void doEffect();

        @Override
        default void call(){
            Warp.modify(Warp.stacks()/EFFECT_BASE);
            Sample.INSTANCE.play(Assets.Sounds.CURSED);
            doEffect();
        }
    }

    public static float[][] categoryChances = {
            {90, 60, 20},
            {8, 30, 45},
            {2, 10, 35}
    };

    public static float[] getChanceCat(int warp){
        if (warp >= COMMON_THRESHOLD && warp < UNCOMMON_THRESHOLD) return categoryChances[0];
        else if (warp >= UNCOMMON_THRESHOLD && warp < RARE_THRESHOLD) return categoryChances[1];
        else return categoryChances[2];
    }

    public static int COMMON_THRESHOLD = 10;
    public static HashMap<WarpEffect, Float> commonEffects = new HashMap<>();

    public static int UNCOMMON_THRESHOLD = 50;
    public static HashMap<WarpEffect, Float> uncommonEffects = new HashMap<>();

    public static int RARE_THRESHOLD = 100;
    public static HashMap<WarpEffect, Float> rareEffects = new HashMap<>();

    public static HashMap[] effectTypes = new HashMap[]{commonEffects, uncommonEffects, rareEffects};
}
