package com.zrp200.rkpd2.items.weapon.melee;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.ArcaneArmor;
import com.zrp200.rkpd2.actors.buffs.Barrier;
import com.zrp200.rkpd2.actors.buffs.Bleeding;
import com.zrp200.rkpd2.actors.buffs.Blindness;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Cripple;
import com.zrp200.rkpd2.actors.buffs.MagicImmune;
import com.zrp200.rkpd2.actors.buffs.Paralysis;
import com.zrp200.rkpd2.actors.buffs.Vertigo;
import com.zrp200.rkpd2.actors.buffs.Vulnerable;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.effects.CellEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.items.armor.glyphs.Viscosity;
import com.zrp200.rkpd2.items.stones.StoneOfAggression;
import com.zrp200.rkpd2.items.wands.WandOfBlastWave;
import com.zrp200.rkpd2.items.weapon.enchantments.Kinetic;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.mechanics.ConeAOE;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class RoyalBrand extends Crossbow implements Talent.SpellbladeForgeryWeapon {
    {
        image = ItemSpriteSheet.ROYAL_SWORD;
        hitSound = Assets.Sounds.HIT_STRONG;
        hitSoundPitch = 1.4f;

        tier = 6;
        ACC = 1.24f;
        DLY = 0.8f;
        RCH = 3;
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(lvl)+1;
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +    //21 damage,
                lvl*(tier+2);     //+8 per level, down from +7
    }

    @Override
    public int defenseFactor( Char owner ) {
        return 2+2*buffedLvl();    //6 extra defence, plus 3 per level;
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 2+2*buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 2);
        }
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals 50% toward max to max on surprise, instead of min to max.
                int diff = max() - min();
                int damage = augment.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff*0.50f),
                        max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }

    static int targetNum = 0;

    //this will be a really fun one
    @Override
    public int warriorAttack(int damage, Char enemy) {
        int conservedDamage = 0;
        if (Dungeon.hero.buff(Kinetic.ConservedDamage.class) != null) {
            conservedDamage = Dungeon.hero.buff(Kinetic.ConservedDamage.class).damageBonus();
            Dungeon.hero.buff(Kinetic.ConservedDamage.class).detach();
        }

        if (damage > enemy.HP){
            int extraDamage = (damage - enemy.HP)*2;

            Buff.affect(Dungeon.hero, Kinetic.ConservedDamage.class).setBonus(extraDamage);
        }
        damage += conservedDamage;
        damage += damage * 2 * (1 - (Dungeon.hero.HP / Dungeon.hero.HT));
        if (enchantment != null && Dungeon.hero.buff(MagicImmune.class) == null) {
            damage = enchantment.proc( this, Dungeon.hero, enemy, damage );
        }
        int lvl = Math.max(0, Dungeon.hero.STR() - STRReq());
        if (Random.Int(lvl + 12) >= 10) {
            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC);
            damage*=2;
        }
        if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(Dungeon.hero)){
            Buff.prolong(enemy, Blindness.class, 4f);
            Buff.prolong(enemy, Cripple.class, 4f);
        }
        Buff.affect(enemy, Bleeding.class).set(damage);
        ArrayList<Char> affectedChars = new ArrayList<>();
        Ballistica trajectory = new Ballistica(Dungeon.hero.pos, enemy.pos, Ballistica.STOP_TARGET);
        ConeAOE cone = new ConeAOE(
                trajectory,
                5,
                90,
                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET
        );
        for (int cell : cone.cells){
            CellEmitter.bottom(cell).burst(Speck.factory(Speck.STEAM), 10);
            Char ch = Actor.findChar( cell );
            if (ch != null && !ch.equals(enemy)) {
                affectedChars.add(ch);
            }
        }
        for (Char ch : affectedChars){
            int dmg = Dungeon.hero.attackProc(ch, damage);
            switch (Dungeon.level.distance(ch.pos, Dungeon.hero.pos)){
                case 2: dmg *= 0.66f; break;
                case 3: dmg *= 0.33f; break;
                case 4: dmg *= 0.16f; break;
                case 5: dmg *= 0.1f; break;
            }
            dmg -= ch.drRoll();
            dmg = ch.defenseProc(Dungeon.hero, dmg);
            ch.damage(dmg, Dungeon.hero);
        }
        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
        Camera.main.shake( 3, 0.7f );
        Buff.affect(enemy, StoneOfAggression.Aggression.class, StoneOfAggression.Aggression.DURATION / 5);
        Buff.affect(Dungeon.hero, ArcaneArmor.class).set(damage/3, 40);
        Buff.prolong(enemy, Vulnerable.class, damage);
        Dungeon.hero.sprite.centerEmitter().start( Speck.factory( Speck.CROWN ), 0.03f, 8 );
        Sample.INSTANCE.play(Assets.Sounds.CHAINS, 3);
        if (enemy.isAlive()){
            //trace a ballistica to our target (which will also extend past them
            trajectory = new Ballistica(Dungeon.hero.pos, enemy.pos, Ballistica.STOP_TARGET);
            //trim it to just be the part that goes past them
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
            //knock them back along that ballistica
            WandOfBlastWave.throwChar(enemy, trajectory, 2, true, false, getClass());
            Buff.prolong(enemy, Vertigo.class, Random.NormalIntRange(1, 4));
        }
        Buff.affect(Dungeon.hero, Barrier.class).setShield(damage / 6 + 1 + Dungeon.hero.drRoll()/2);
        if (Dungeon.hero.lastMovPos != -1 &&
                Dungeon.level.distance(Dungeon.hero.lastMovPos, enemy.pos) >
                        Dungeon.level.distance(Dungeon.hero.pos, enemy.pos) && Dungeon.hero.buff(RoundShield.Block.class) == null){
            Dungeon.hero.lastMovPos = -1;
            //knock out target and get blocking
            trajectory = new Ballistica(Dungeon.hero.pos, enemy.pos, Ballistica.STOP_TARGET);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
            WandOfBlastWave.throwChar(enemy, trajectory, 1, true, true, getClass());
            Buff.affect(Dungeon.hero, RoundShield.Block.class, 8f);
            damage*=1.5;
        }
        Buff.affect(enemy, Viscosity.DeferedDamage.class).prolong(damage*2);
        Buff.affect(enemy, Paralysis.class, 3.5f);
        return super.warriorAttack(damage, enemy);
    }

    @Override
    public float warriorDelay() {
        return 3;
    }
}
