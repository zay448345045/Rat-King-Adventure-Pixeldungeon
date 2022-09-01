package com.zrp200.rkpd2.actors.buffs;

import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.ui.BuffIndicator;
import com.zrp200.rkpd2.utils.WarpPile;

public class Warp extends Buff {
    {
        revivePersists = true;
    }

    private float stacks = 0;
    private float decay = 0;

    @Override
    public int icon() {
        return BuffIndicator.WARP;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", getStacks(), getDecay());
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(Math.round(getStacks()) * Math.round(getDecay()) + Math.round(cooldown()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean act() {
        if (target.isAlive()) {

            spend( decay );
            if (Random.Int(WarpPile.EFFECT_CHANCE) == 0){
                float[] category = WarpPile.getChanceCat(Math.round(getStacks()));
                int categoryID = Random.chances(category);
                if (Random.Int(100) < category[categoryID]){
                    WarpPile.WarpEffect effect = (WarpPile.WarpEffect) Random.chances(WarpPile.effectTypes[categoryID]);
                    effect.call();
                }
            }
            if (--stacks <= 0) {
                detach();
            }

        } else {

            detach();

        }
        return true;
    }

    public static final String STACKS = "enemy_stacks";
    public static final String DAMAGE = "damage_inc";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STACKS, getStacks());
        bundle.put(DAMAGE, getDecay());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        setStacks(bundle.getFloat(STACKS));
        setDecay(bundle.getFloat(DAMAGE));
    }

    public static Warp inflict(float stacks, float decay){
        Warp effect = Buff.affect(Dungeon.hero, Warp.class);
        effect.setStacks(stacks);
        effect.setDecay(decay);
        effect.postpone(WarpPile.DECAY_DELAY*decay);
        return effect;
    }

    public static Warp modify(float stacks){
        Warp effect = Buff.affect(Dungeon.hero, Warp.class);
        float initialStacks = effect.stacks;
        effect.setStacks(effect.stacks + stacks);
        if (initialStacks < effect.stacks)
            effect.postpone(WarpPile.DECAY_DELAY*effect.decay/2);
        return effect;
    }

    public static float stacks(){
        return Buff.affect(Dungeon.hero, Warp.class).stacks;
    }

    public float getStacks() {
        return stacks;
    }

    public void setStacks(float stacks) {
        this.stacks = Math.min(WarpPile.MAX_WARP, stacks);
    }

    public float getDecay() {
        return decay;
    }

    public void setDecay(float decay) {
        this.decay = decay;
    }
}
