package com.zrp200.rkpd2.items.quest;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.ChampionEnemy;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.buffs.Warp;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.effects.Splash;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.wands.Wand;
import com.zrp200.rkpd2.items.weapon.Weapon;
import com.zrp200.rkpd2.items.weapon.missiles.MissileWeapon;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.CellSelector;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.MissileSprite;
import com.zrp200.rkpd2.ui.QuickSlotButton;
import com.zrp200.rkpd2.utils.GLog;

import java.util.ArrayList;

public class NerfGun extends Weapon {

    public NerfMode mode = NerfMode.NORMAL;

    public static final String AC_SHOOT	    = "SHOOT";
    public static final String AC_RESTOCK	= "RESTOCK";

    public int curCharges = maxCharges();

    {
        image = ItemSpriteSheet.NERF_GUN;
        defaultAction = AC_SHOOT;
        usesTargeting = true;
    }

    @Override
    public int STRReq(int lvl) {
        return 0;
    }

    public enum NerfMode {
        NORMAL(Dart.class),
        RAPID(SmallDart.class),
        DISC(Disc.class);

        final Class<? extends NerfAmmo> ammoType;

        NerfMode(Class<? extends NerfAmmo> ammoType) {
            this.ammoType = ammoType;
        }

        @Override
        public String toString() {
            return Messages.get(this, name() + ".name");
        }

        public String desc() {
            return Messages.get(this, name() + ".desc");
        }
    }

    public int min(int lvl){
        switch (mode){
            case NORMAL: default:
                return Math.round(1 + lvl*1.5f);
            case RAPID:
                return 1 + lvl / 2;
            case DISC:
                return 3 + lvl*3;
        }
    }

    public int max(int lvl){
        switch (mode){
            case NORMAL: default:
                return 10 + lvl*3;
            case RAPID:
                return Math.round(6 + lvl*0.75f);
            case DISC:
                return 16 + lvl*5;
        }
    }

    public int maxCharges(){
        switch (mode){
            case NORMAL: default:
                return 6 + level()/3;
            case RAPID:
                return 12 + level();
            case DISC:
                return 3 + level()/5;
        }
    }

    @Override
    public String status() {
        return curCharges + "/" + maxCharges();
    }

    @Override
    public String info() {
        String info = desc();

        info += "\n\n" + Messages.get( NerfGun.class, "stats",
                Math.round(augment.damageFactor(min())),
                Math.round(augment.damageFactor(max())),
                maxCharges());

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        info += "\n\n" + mode.desc();
        info += "\n\n" + Messages.get( NerfGun.class, "exp", maxExp() - exp, level()+1);

        if (enchantment != null && (cursedKnown || !enchantment.curse())){
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursed && isEquipped( Dungeon.hero )) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown){
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

        return info;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_EQUIP);
        actions.add(AC_SHOOT);
        actions.add(AC_RESTOCK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {

            curUser = hero;
            curItem = this;
            GameScene.selectCell( zapper );

        } else if (action.equals(AC_RESTOCK)){
            switch (mode){
                case NORMAL: mode = NerfMode.RAPID; break;
                case RAPID: mode = NerfMode.DISC; break;
                case DISC: mode = NerfMode.NORMAL; break;
            }
            GLog.i(Messages.get(this, "restock", mode.toString()));
            hero.spendAndNext(5f);
            Sample.INSTANCE.play(Assets.Sounds.PLANT);
            hero.sprite.operate(hero.pos, () -> {
                hero.sprite.idle();
                curCharges = maxCharges();
                Warp.inflict(15, 2);
                updateQuickslot();
                Sample.INSTANCE.play(Assets.Sounds.ATK_CROSSBOW);
            });
        }
    }

    public boolean tryToZap(Hero owner, int target ){
        if ( curCharges >= 1){
            return true;
        } else {
            GLog.w(Messages.get(this, "fizzles"));
            return false;
        }
    }

    protected void wandUsed() {
        curCharges--;
        Invisibility.dispel();
        updateQuickslot();
        curUser.spendAndNext(baseDelay(curUser));
    }

    int exp;

    public int maxExp(){
        return 10 + (level())*20;
    }

    @Override
    public void onHeroGainExp(int expAmount, Hero hero) {
        exp += expAmount;
        while (exp >= maxExp()){
            exp -= maxExp();
            level(level()+1);
            GLog.h(Messages.get(this, "upgrade"));
            updateQuickslot();
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        }
    }

    @Override
    public Item random() {
        exp += Random.IntRange(Dungeon.depth * 5, 10 + Dungeon.depth * 15);
        while (exp >= maxExp()) {
            exp -= maxExp();
            level(level() + 1);
        }
        return this;
    }

    @Override
    protected float baseDelay(Char owner) {
        switch (mode){
            case NORMAL: this.DLY = 1f; break;
            case RAPID: this.DLY = 0.5f; break;
            case DISC: this.DLY = 2f; break;
        }
        float delay = augment.delayFactor(this.DLY);
        if (owner instanceof Hero) {
            int encumbrance = STRReq() - ((Hero)owner).STR();
            if (encumbrance > 0){
                delay *= Math.pow( 1.2, encumbrance );
            }
        }

        return delay;
    }

    public static final String MODE = "mode";
    public static final String CHARGES = "curCharges";
    public static final String EXP = "exp";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MODE, mode);
        bundle.put(CHARGES, curCharges);
        bundle.put(EXP, exp);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        mode = bundle.getEnum(MODE, NerfMode.class);
        curCharges = bundle.getInt(CHARGES);
        exp = bundle.getInt(EXP);
    }

    public static NerfGun gun;

    public abstract static class NerfAmmo extends MissileWeapon{

        {
            hitSound = Assets.Sounds.HIT_ARROW;
            hitSoundPitch = 1.3f;
        }

        @Override
        public int min() {
            return gun.min();
        }

        @Override
        public int max() {
            return gun.max();
        }

        @Override
        public int damageRoll(Char owner) {
            return gun.damageRoll(owner);
        }

        @Override
        public void onRangedAttack(Char enemy, int cell, boolean hit) {}

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
        }
    }

    public static class Dart extends NerfAmmo {
        {
            image = ItemSpriteSheet.NERF_AMMO_1;
        }
    }

    public static class SmallDart extends NerfAmmo {
        {
            image = ItemSpriteSheet.NERF_AMMO_2;
            hitSoundPitch = 2f;
        }
    }

    public static class Disc extends NerfAmmo {
        {
            hitSound = Assets.Sounds.HIT_CRUSH;
            hitSoundPitch = 1f;
        }

        {
            image = ItemSpriteSheet.NERF_AMMO_3;
        }
    }

    public NerfAmmo sampleAmmo(){
        return Reflection.newInstance(mode.ammoType);
    }

    protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                //FIXME this safety check shouldn't be necessary
                //it would be better to eliminate the curItem static variable.
                final NerfGun curWand;
                if (curItem instanceof NerfGun) {
                    curWand = (NerfGun) curItem;
                } else {
                    return;
                }

                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.PROJECTILE,
                        curUser.buff(ChampionEnemy.Projecting.class) != null && curUser.pointsInTalent(Talent.RK_PROJECT) == 3);
                int cell = shot.collisionPos;
                gun = curWand;

                NerfAmmo shotItem = curWand.sampleAmmo();
                shotItem.throwSound();

                curUser.sprite.zap(cell);
                Char enemy = Actor.findChar( cell );

                //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(enemy);

                if (curWand.tryToZap(curUser, target)) {

                    curUser.busy();

                    if (enemy != null) {
                        curUser.sprite.parent.recycle(MissileSprite.class).
                                reset(curUser.sprite,
                                        enemy.sprite,
                                        shotItem,
                                        new Callback() {
                                            public void call() {
                                                boolean hit = curUser.shoot(enemy, shotItem);
                                                curWand.wandUsed();
                                            }
                                        });
                    } else {
                        curUser.sprite.parent.recycle(MissileSprite.class).
                                reset(curUser.sprite,
                                        cell,
                                        shotItem,
                                        new Callback() {
                                            public void call() {
                                                Splash.at(cell, 0x225ab4, 4);
                                                curWand.wandUsed();
                                            }
                                        });
                    }

                } else {
                    Sample.INSTANCE.play(Assets.Sounds.PLANT);
                    curUser.sprite.operate(target, () -> {
                        curUser.spendAndNext(curWand.baseDelay(curUser)*2);
                        curUser.sprite.idle();
                        curWand.curCharges = curWand.maxCharges();
                        Warp.inflict(10, 1.5f);
                        updateQuickslot();
                        Sample.INSTANCE.play(Assets.Sounds.ATK_CROSSBOW);
                    });

                }

            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };
}
