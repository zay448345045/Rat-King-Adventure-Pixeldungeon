package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.GodSlayerBurning;
import com.zrp200.rkpd2.actors.buffs.PowerfulDegrade;
import com.zrp200.rkpd2.actors.buffs.Scam;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.particles.ElmoParticle;
import com.zrp200.rkpd2.items.quest.Chaosstone;
import com.zrp200.rkpd2.items.quest.Kromer;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.CharSprite;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

public class TerminusBlade extends MeleeWeapon {

    {
        image = ItemSpriteSheet.TERMINUS;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 0.75f;

        tier = 7;
        DLY = 2.0f;

        defaultAction = "NONE";
    }

    public int hitCount;

    private static final String HITS = "hits";

    @Override
    public int max(int lvl) {
        return  8*(tier+1) +    //base
                lvl*(tier+5);   //level scaling
    }

    private static final ItemSprite.Glowing CHAOTIC = new ItemSprite.Glowing( 0.2f  );

    @Override
    public ItemSprite.Glowing glowing() {
        return CHAOTIC;
    }

    @Override
    public String status() {
        if (isEquipped(Dungeon.hero)) {
            return hitCount * 2 + "%";
        }
        return super.status();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        defender.sprite.emitter().burst(ElmoParticle.FACTORY, 30);
        Camera.main.shake(2f, 0.175f);
        Buff.affect(defender, GodSlayerBurning.class).reignite(defender, 4f);
        Buff.affect(defender, PowerfulDegrade.class, 4f);
        Buff.affect(defender, Scam.class, 4f);
        Buff.affect(defender, Talent.AntiMagicBuff.class, 4f);
        instaKill(defender);
        return super.proc(attacker, defender, damage);
    }

    public void instaKill(Char enemy) {
        if (++hitCount >= 50){
            enemy.sprite.showStatus(CharSprite.NEGATIVE, "9999999999999999999999\n9999999999999999999999\n9999999999999999999999\n9999999999999999999999");
            enemy.die(Dungeon.hero);
            GameScene.flash(0xAAAAAA);
            enemy.sprite.emitter().burst(ElmoParticle.FACTORY, 100);
            Sample.INSTANCE.play(Assets.Sounds.DEGRADE, 0.75f, 0.88f);
            Dungeon.hero.damage(Dungeon.hero.HP / 2, this);
            hitCount = 0;
        }
    }

    @Override
    public int warriorAttack(int damage, Char enemy) {
        hitCount += 9;
        instaKill(enemy);
        return super.warriorAttack(damage, enemy);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HITS, hitCount);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        hitCount = bundle.getInt(HITS);
    }

    public static class Recipe extends com.zrp200.rkpd2.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Kromer.class, Chaosstone.class};
            inQuantity = new int[]{2, 1};

            cost = 100;

            output = TerminusBlade.class;
            outQuantity = 1;
        }

    }
}
