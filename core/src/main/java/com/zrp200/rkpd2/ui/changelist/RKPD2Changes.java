/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.rkpd2.ui.changelist;

import com.watabou.noosa.Image;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.actors.hero.abilities.rat_king.Wrath;
import com.zrp200.rkpd2.items.armor.RatKingArmor;
import com.zrp200.rkpd2.items.armor.WarriorArmor;
import com.zrp200.rkpd2.items.bags.VelvetPouch;
import com.zrp200.rkpd2.items.wands.WandOfFirebolt;
import com.zrp200.rkpd2.items.weapon.SpiritBow;
import com.zrp200.rkpd2.items.weapon.enchantments.Explosive;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.ChangesScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.sprites.KingSprite;
import com.zrp200.rkpd2.sprites.RatKingBossSprite;
import com.zrp200.rkpd2.ui.Icons;

import java.util.ArrayList;

import static com.zrp200.rkpd2.Assets.Interfaces.TALENT_ICONS;
import static com.zrp200.rkpd2.actors.hero.HeroClass.*;
import static com.zrp200.rkpd2.actors.hero.HeroSubClass.*;
import static com.zrp200.rkpd2.actors.hero.Talent.*;
import static com.zrp200.rkpd2.messages.Messages.get;
import static com.zrp200.rkpd2.sprites.CharSprite.*;
import static com.zrp200.rkpd2.sprites.HeroSprite.avatar;
import static com.zrp200.rkpd2.sprites.ItemSpriteSheet.*;
import static com.zrp200.rkpd2.ui.Icons.DEPTH;
import static com.zrp200.rkpd2.ui.Icons.INFO;
import static com.zrp200.rkpd2.ui.Icons.TARGET;
import static com.zrp200.rkpd2.ui.Icons.get;
import static com.zrp200.rkpd2.ui.Window.SHPX_COLOR;
import static com.zrp200.rkpd2.ui.Window.TITLE_COLOR;
import static java.util.Arrays.asList;

// TODO should I have a separate section for shattered changes?
public class RKPD2Changes {

    private RKPD2Changes() {} // singleton
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        for(ChangeInfo[] section : new RKPD2Changes().changes) changeInfos.addAll(asList(section));
    }

    // utility
    private static ChangeButton bugFixes(String message) {
        return new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), get(ChangesScene.class, "bugfixes"), message);
    }
    private static ChangeButton misc(String message) {
        return new ChangeButton(get(Icons.PREFS), get(ChangesScene.class,"misc"), message);
    }

    // section types
    private static ChangeInfo NewContent(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "new"),
                false, TITLE_COLOR,
                "",
                buttons);
    }
    private static ChangeInfo Buffs(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "buffs"),
                false, POSITIVE,
                "",
                buttons);
    }
    private static ChangeInfo Changes(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "changes"),
                false, WARNING, "",
                buttons);
    }
    private static ChangeInfo Nerfs(ChangeButton... buttons) {
        return new ChangeInfo(
                Messages.get(ChangesScene.class, "nerfs"),
                false, NEGATIVE,
                "",
                buttons);
    }

    // more utils

    /** makes a list in the standard PD style.
     * [lineSpace] determines the number of spaces between each list item.
     * If you want to append extra spaces, you should do it at the end of the previous item, rather than at the start of that item.*/
    private static String list(String... items) { return list(1, items); }
    private static String list(int lineSpace, String... items) {
        StringBuilder builder = new StringBuilder();
        for (int j=0; j < lineSpace; j++) builder.append('\n');
        for (String item : items) {
            builder.append("_-_ ").append( item );
            for (int j = 0; j < lineSpace; j++) builder.append('\n');
        }
        return builder.toString();
    }

    // yet another attempt at formatting changes. not sure if this is any better than the last one, but one thing is certain: I HATE the way it's done in Shattered.
    // in this case I made it so you could add buttons in the ChangeInfo constructor; this is 'lustrous' style

    final ChangeInfo[][] changes = {
        {
            new ChangeInfo("DLC-1.3", true, TITLE_COLOR, "Warning! This build is unstable (like really). If you got a problem, let me know."),
            NewContent(
                    new ChangeButton(new ItemSprite(MASK), "Secret Subs",
                            list(2,
                                "Added secret subclasses for each hero! They can be accessed in same way as huntress's one.",
                                    "You can permanently unlock secret subclass by winning with it or defeating Rat King boss as any subclass.")),
                    new ChangeButton(FUN, "Rat King's Wrath",
                            "_-_ Added new talent for Wrath, which makes ability more fun.\n\n" +
                                    "_-_ Old Wrath have been renamed to _Divine Fury_."),
                    new ChangeButton(new ItemSprite(RING_RUBY), "Ring of Force rework",
                            "_-_ Can hold enchantments and proc them.\n\n" +
                                    "_-_ Reduced direct unarmed damage, but armed damage pierces armor and is buffed by 66%."),
                    new ChangeButton(ASSASSIN,
                            list(2,
                                    "All 3 Shattered's assassin talents got replaced by new ones.",
                                    "_Bloodbath_ allows for AoE damage from assassinations.",
                                    "_Thinking with Portals_ gives Assassin an ability to teleport and gain preparation instantly.",
                                    "_Adapt and Overcome_ patches Assassin's weaknesses such as no synergy with wands, bad damage rolls and unsuccessful attacks.",
                                    "Old Enhanced Lethality and Assassin's Reach are now innate abilities."))
            ),
            Changes(
                    new ChangeButton(new RatKingBossSprite(), "Rat King Boss",
                            "_-_ RK boss will teleport if he is stuck.\n\n" +
                                    "_-_ RK boss will move in emperor phase 3 state.\n\n" +
                                    "_-_ Reduced amount of statues and water on level 0.\n\n" +
                                    "_-_ Fixed issues with Tengu's tricks used by RK.\n\n" +
                                    "_-_ RK boss is resistant to corrosion.\n\n" +
                                    "_-_ Any rat will attack you during the fight.\n\n" +
                                    "_-_ RK boss has far better accuracy against wraiths and kills all of them while switching to new phase.\n\n" +
                                    "_-_ Rat King (class) can fight Rat King (boss) now. Throw something very important on the ground."),
                    new ChangeButton(BERSERKER, "_-_ Buffed rage damage boost to up +100% on 90% rage."),
                    new ChangeButton(KING, "Reintoduced certain perks for Rat King subclass:\n\n" +
                            "_-_ Berserker's rage\n" +
                            "_-_ Warlock's soulmark\n" +
                            "_-_ Assassin's preparation\n" +
                            "_-_ Sniper's armor penetration\n" +
                            "_-_ Warden's seed effects"),
                    new ChangeButton(SOUL_SIPHON, "_-_ Added unique icons for Huntress's secret subclass."),
                    new ChangeButton(HEROIC_WIZARDRY, "_-_ Changed Heroic Wizardry to properly act as additional max charges for every possible occasion."),
                    new ChangeButton(SEER_SHOT, "Previously was Seer Shot.\n\n" +
                            "_-_ Removed and replaced by _Like a Bullet_. Point Blank's Super-shot buffs were moved here, with very mild nerf.\n" +
                            "_-_ Seer Shot is now Huntress's innate ability.\n" +
                            "_-_ Natural Dominance still has Seer Shot part."),
                    new ChangeButton(new ItemSprite(RING_AMETHYST), "Ring of Wealth change",
                            "_-_ Upgrades affect drop rates, at +16 it maxes out at 1 item per kill."),
                    bugFixes("_-_ Fixed Elemental Blast doing not as much damage as intended.\n" +
                            "_-_ Fixed Heroic Endurance not working at all.\n" +
                            "_-_ Fixed Burning and Red Burning conflicting between each other.\n" +
                            "_-_ Fixed Big Rush killing quest givers and shopkeepers.\n" +
                            "_-_ Fixed Eldritch Blessing not working properly.\n" +
                            "_-_ Fixed DK getting stuck if his progression damage is affected by some modifier.\n\n" +
                            "RKPD2 bugs:\n\n" +
                            "_-_ Fixed talent window description having extra whitespace in title.\n" +
                            "_-_ Fixed miscolored icons for heroes and talents.")
            ),
            new ChangeInfo("v0.2", true, TITLE_COLOR, ""),
            new ChangeInfo("v0.2.1", false, "",
                    new ChangeButton(get(INFO), "Developer Commentary", "I'm shifting to Shattered's new major.minor.patch versioning system, so even though this is listed as v0.2.1, it's really the equivalent of v0.2.0a.\n\nThis patch has many internal changes to the talent system, so beware of any bugs that may result from this."),
                    new ChangeButton(MULTISHOT, "Adjusted the way multiple sniper's marks are handled internally, to increase consistency of the mechanic:" + list(2,"Using specials with multiple free targets stored will now use them in order of highest level to lowest level.", "When too many snipers marks are stored, the one storing the lowest shared upgrades level is now removed. If all are the same level, 'standard' sniper's marks will be removed before free-targeted marks. Previously level was not considered at all.") + "\nAlso fixed a bunch of issues with multi-shot:" + list(2,"Crash when cancelling targeting a single free-targeted sniper's mark.", "Rare cases where cancelling a special would incorrectly leave the targeting system active.", "Killing a marked enemy with a thrown weapon generating two free-targeted sniper's marks.", "Not being able to use sniper specials if there are less possible targets than available marks.", "Sniper's marks sometimes not detaching when their targets are killed.")),
                    new ChangeButton(KINGS_WISDOM, "Improved King's Wisdom icon, hopefully it should feel more professional now with a proper background and less outlines."),
                    bugFixes(list(2,"fixed soul eater working incorrectly and sometimes yielding NaN hunger.", "Fixed rare cases of incorrect character-specific text.", "Fixed color of Rat King's eyes in his subclass icon."))),
            new ChangeInfo("From SHPD v1.0.1", false, SHPX_COLOR, "",
                    // alchemy stuff once it's added in.
                    new ChangeButton(new ItemSprite(CROWN), "Armor Ability Changes", ""
                        + "_Buffs:_\n"
                        + list("_Endure_ bonus damage conversion rate up to 1/3 from 1/4.")
                        + list("_Striking Wave_ effectiveness increased by 20%."/*,"_Shock Force_ now actually adds 20% damage per level as stated. Previously it only added 15%."*/, "Relatedly, Striking Force is no longer level shifted with regards to damage, but its boost is now +25/+50/+75/+100% damage.")
                        + list("_Wild Magic_ now boosts wand levels, instead of overriding them.","_Conserved Magic_ now has a chance to give each wand a 3rd shot.","_Conserved Magic_ charge cost reduction down to 33/55/70/80% from 44/69/82/90%.")
                        + list("_Elemental Blast_ base damage increased to 15-25 from 10-20.")
                        + list("_Remote Beacon_ range per level increased to 4, from 3.")
                        + list("_Shadow Clone_ now follows the hero at 2x speed.","_Shadow Blade_ unshifted, damage per level increased to 7.5% from 6.25%.","_Cloned Armor_ unshifted, armor per level increased to 15% from 12.5%.")
                        + list("_Spirit Hawk_ evasion, accuracy, and duration increased by 20%.","_Swift Spirit_ now gives 2/3/4/5 dodges, up from 1/2/3/4.","_Go for the Eyes_ now gives 2/4/6/8 turns of blind, up from 2/3/4/5.")
                        + list("_Spirit Blades_ effectiveness increased by 20%.")
                        + "\n\nNerfs:\n"
                        + list("_Double Jump_ charge cost reduction down to 20/36/50/60%, from 24/42/56/67%.")
                        + list("_Telefrag_ self damage increased to a flat 5 per level.")
                        + "\nSmoke bomb nerfs are only applied to Wrath. Standard smoke bomb is left intact."
                        + list("_Smoke Bomb_ max range reduced to 6 tiles from 8.", "_Body Replacement_ armor reduced to 1-3 per level, from 1-5.", "_Hasty Retreat_ turns of haste/invis reduced to 1/2/3/4 from 2/3/4/5","_Shadow Step_ charge cost reduction down to 20/36/50/60%, from 24/42/56/67%.")
                        + list("_Double Mark_ balance changed in response to SHPD changes; charge cost reduction down to 16/40/58/70/79% (which is still up from shpd's 30/50/65/70), from 33/55/70/80%/87.")
                        + list("_13th armor ability_ now only lasts for 6 turns, but also no longer prevents EXP or item drops.",
                        "_resistance talent_ damage reduction, in a compromise, reduced to 15/28/39/48%, which is still well above shattered levels.")),
                    new ChangeButton(get(DEPTH), "SHPD Additions and Changes", "Implemented:"
                        + list("New music",
                            "Geyser and Gateway traps",
                            "Spectral Necromancers",
                            "Liquid Metal and Arcane Resin alchemy recipes",
                            "Unblessed Ankh rework",
                            "Blessed Ankhs now give 3 turns of invulnerability",
                            "Guidebook rework")
                        +"\nRunestone buffs:"+list("All Scrolls now produce 2 runestones.","_Stone of Intuition_ can now be used a second time if the guess was correct.","_Stone of Flock_ AOE up to 5x5 from 3x3, sheep duration increased slightly.","_Stone of Deepened Sleep_ is now stone of deep sleep, instantly puts one enemy into magical sleep.","_Stone of Clairvoyance_ AOE up to 20x20, from 12x12.","_Stone of Aggression_ duration against enemies up 5, now works on bosses, and always forces attacking.","_Stone of Affection_ is now stone of fear, it fears one target for 20 turns.")),
                    misc("Implemented:\n"
                            + list("Various tech and stability improvements.", "Increased the minimum supported Android version to 4.0, from 2.3.", "Game versions that use github for update checking can now opt-in to beta updates within the game.")
                            + list("Various minor UI improvements to the intro, welcome and about scenes.","Adjusted settings windows, removed some unnecessary elements.","Armor with the warrior's seal on it now states max shielding.","Bonus strength is now shown separately from base strength.", "Added info buttons to the scroll of enchantment window.")
                            + list("'Improved' the exit visuals on floor 10.","Becoming magic immune now also cleanses existing magical buffs and debuffs.","Traps that spawn visible or that never deactivate can no longer appear in enclosed spaces")
                            + list("Added info buttons to the scroll of enchantment window.")),
                    bugFixes(""
                        + list("Various rare crash bugs", "Various minor visual and text errors", "damage warn triggering when hero gains HP from being hit", "various rare bugs involving pitfall traps")
                        + list("statues not becoming aggressive when debuffed", "swapping places with allies reducing momentum", "DK minions dropping imp quest tokens", "giant succubi teleporting into enclosed spaces", "spectral blades being blocked by allies", "Spirit Hawk and Shadow Clone being corruptible")
                        + list("wands losing max charge on save/load in rare cases", "magical infusion clearing curses", "dewdrops stacking on each other in rare cases", "exploding skeletons not being blocked by transfusion shield in rare cases", "rare incorrect interactions between swiftthistle and golden lotus")
                        + list("various minor errors with electricity effects", "soul mark not working properly on low HP enemies with shielding", "various rare errors with shadows buff", "errors with time freeze and inter-floor teleportation mechanics", "rooted characters not being immune to knockback effects")
                        + list("gladiator combos dealing much more damage than intended in certain cases", "magical charge and scroll empower interacting incorrectly", "magical sight not working with farsight talent", "perfect copy talent giving very slightly more HP than intended", "wild magic using cursed wands as if they're normal") + list("Disarming traps opening chests.", "Body replacement ally being vulnerable to various AI-related debuffs.") + list("Disarming traps opening chests", "Body replacement ally being vulnerable to various AI-related debuffs", "Some ranged enemies becoming frozen if they were attacked from out of their vision"))),
            NewContent(
                new ChangeButton(new Wrath(), "Rat King's Wrath Redesign!", "I've finally gotten around to updating Rat King's Wrath to reflect v0.9.3 reworks to armor abilities!"
                        + "\n\nWhile the previous Wrath was a combination of all armor abilities, the prospect of combining 13 different abilities into one isn't possible under the Wrath design, so I have instead decided to adapt the ones that have similar functionality to each part of the previous Wrath: _Smoke Bomb, Shockwave, Elemental Blast, and Spectral Blades._"
                        + "\n\nNote, however, that Wrath is not a perfect mirror of these abilities, though all their mechanics are there in some form." + "\n"
                        + list("Energy cost increased to 60 from 35.") + list("Added four new talents to Wrath.", "Each new talent corresponds as closely as possible to the talents of the respective armor ability.", "Wrath does not have Heroic Energy.") + list("Smoke Bomb no longer grants invisibility, mechanic instead moved to corresponding talent.", "Range is reduced to 6, from 8.") + list("Molten Earth effect replaced with Elemental Blast.") + list("Wrath's leap no longer stuns adjacent foes, instead sends out a 360 degree AOE Shockwave that covers a 3x3 area.", "Aftershock's Striking Wave and Shock Force are less effective than the real thing.", "Stun inflicted through through Aftershock cannot be broken during Wrath itself.") + list("Spectral Blades retains the ability to hit all targets in sight (removing the need to target it).", "Spectral Blades instead has damage and proc penalties when attacking multiple targets, though upgrading its respective talent lowers the degree to which this occurs.")
                        + "\nWrath should be much more powerful now, but also much less cheesy; the consistent stun and root are gone, and it's much more bound to set ranges than before, but upgrading its talents can hugely increase Wrath's power output and flexibility."),
                new ChangeButton(HUNTRESS, list(2,
                        "Added a _secret subclass_ to Huntress, accessible by a secret interaction while choosing a subclass.",
                        "_Restored Nature_ root duration reverted to 2/3, down from 4/6, but it now also causes health potions and related alchemy products to be used instantly.")
                        + "_Multi-Shot:_" + list("Now uses multiple buffs to show that more than one target is marked.",
                        "Allows stacking of free-targeted marks instead of overriding them when a new target is marked.",
                        "Has changed free-targeting logic (thanks to smart-targeting) to make these new interactions smoother; enemies that are already targeted will be highlighted while manually targeting.")
                        + "\nMulti-shot should now be more complex, but in exchange it should (somewhat ironically) be easier to use and understand. It's also much more flexible with its free-targeted sniper special functionality."),
                    new ChangeButton(KINGS_WISDOM, "New Talent Icons!", "Most of my added talents now have unique icons! Some credit to _Trashbox Bobylev_ is needed."
                            + "\n\nAlso, the new music and UI changes from SHPD v1.0.0 have been implemented into the game.")),
            Changes(
                new ChangeButton(WARLOCKS_TOUCH, "Warlock's Touch is currently extremely situational and often requires giving up warlock's other gimmicks to work at its best. At the same time, when exploited it's incredibly overpowered. These changes are intended to instead generalize its use, increasing its versatility and amount of situations in which it is applicable."
                    + list(2,
                        "Proc chance on melee attacks is now a fixed 15/25/35% chance at +1/+2/+3 respectively.",
                        "Proc chance on thrown weapons is now a fixed 25/40/55% chance at +1/+2/+3.",
                        "Allies can now inflict soul mark via Warlock's Touch using melee mark chances.",
                        "Proc duration of mark is now a fixed 6 turns, instead of being 10 + weapon level",
                        "Chance for proccing soul mark with the attack that inflicts it is 20/30/40%, down from 25/50/75%, but now applies to wands if Soul Siphon is upgraded.")),
                new ChangeButton(new ItemSprite(ItemSpriteSheet.STONE_ENCHANT), "Enchanting Logic",
                        list("The chance for rare weapon enchantments to appear has been increased by ~50%.")
                                + "_\n\nSpirit Bow only:_" + list(2,
                                "Stones of Enchantment can no longer roll Lucky or Blocking.",
                                "Explosive Enchantment can now be rolled by Stones of Enchantment.",
                                "Explosive is now exactly as common as a standard uncommon enchantment in Shattered Pixel Dungeon. "
                                    + "Other uncommon enchants are now slightly more common to compensate.",
                                "Grim no longer has specifically boosted chances to appear.")),
                misc(list(2,
                        //"TODO _Energizing Meal I_ now adds new recharging buffs instead of stacking on existing ones.",
                        "Talents that identify curses now declare whether an item is cursed when activated.",
                        // ui
                        "Most windows now scroll if they would not fit on the screen.",
                        "Changed commentary on Rat King's tier 3 talents.",
                        "Changed capitalization logic, hyphenated titles now have both words capitalized.")),
                bugFixes(list(2,
                        "Lethal Momentum not working with Death Mark.",
                        "bm staff on-hit effects not responding to proc chance modifiers like Enraged Catalyst and Spirit Blades.",
                        "Rat King's light cloak now is 13/27/40 instead of 10/20/30",
                        "Fixed a mistake when updating Ranger to Multi-Shot talent.",
                        "Typo in Restoration description",
                        "Rat King's Wrath sometimes freezing Rat King's sprite after use."))),
            Nerfs(
                new ChangeButton(ROGUE, ""
                        + "_Mending Shadows_ turned out to have exploits, so it's (very unfortunately) being largely scrapped, though the name remains for now:"
                        + list("Now provides shielding every 2/1 turns, up to 3/5 max shielding (Shattered Protective Shadows).",
                        "Healing rate reduced to every 4/2 turns, and it no longer works while starving.")
                        + "\nIn addition, I'm making these changes based on player feedback:"
                        + list("_Cached Rations_ now gives 3/5 rations, down from 4/6.", "_Light Cloak_ effectiveness now 20%/40%/60%, down from 25/50/75.")),
                new ChangeButton(RAT_KING, list(2,
                        "The recent Strongman buff has turned Tactics into a monster, while Imperial Wrath is still rather niche in comparison. Thus, Imperial Wrath now has Strongman instead of Tactics.",
                        "Royal Intuition's +1 effect is now additive with the SHPD Survialist's Intuition rather than multiplicative. It is now 2.75x/3.75x id speed, down from 3.5x/5.25x.",
                        "Rat King is also affected by the v1.0.0 staff nerf.")),
                new ChangeButton(RATFORCEMENTS, "I've successfully made Ratforcements the best talent Ratmogrify has. That said, it's so powerful now that it's making the other aspects of Ratmogrify much less useful." + list(2, "Ratforcements stats reduced by ~20% across the board.") + "Don't be fooled though into thinking it's bad now, it is still VERY superior to Shattered's Ratforcements."))
        },

        {
                new ChangeInfo("DLC (Abyssal 1.2)", true, TITLE_COLOR, "From this update, the mod is called RKPD2 DLC.",
                        new ChangeButton(avatar(RAT_KING,6), "Another attempt at Rat King Nerfs (rat king nerfs)",
                                "_-_ Reverted HP nerf.\n\n" +
                                        "_-_ Subclass powers are acquired via talents instead of being given by default.\n\n" +
                                        "_-_ Restoration works with drinking from waterskin."),
                        new ChangeButton(new RatKingBossSprite(), "Rat King Boss",
                                "Added new extremely hard boss, which can be fought, when you try to talk with RK while having an amulet.\n\nHis attacks are based on hero classes and some bosses, and Rat King will frequently switch between them.\n\n_Beware of his power while doing challenge run._")
                ),
                new ChangeInfo("Talents", false, 0x44d1d3, "",
                        new ChangeButton(NOBLE_CAUSE, "RK",
                                "_-_ Changed every Rat King talent to have unique sprite.\n" +
                                        "_-_ Imperial Wrath now get both _Hold Fast_ and _Strongman_, to make it more relevant.\n" +
                                        "_-_ Added _Advanced Education_ talent as additional T4: allows to get bonus points for previous tiers.\n" +
                                        "_-_ Ratmogrify talents are level-shifted for Rat King.\n"+
                                        "_-_ Added _Drratedon_ talent for Ratmogrify: gives buffs to allied ratmogrified enemies and summoned rats.\n" +
                                        "_-_ Revamped Wrath's talents to have more rounded distribution of powers.\n" +
                                        "_-_ Added _Avalon Power-Up_ talent for Wrath: enhances wrath with powers related to Ratmogrify, Heroic Leap, Elemental Blast.\n\n" +
                                        "_-_ Added _Mus Rex Ira_ armor ability, combining the powers of _Spirit Hawk_, _Shadow Clone_, _Remote Beacon_, _Wild Magic_, _Endure_ and _Nature's Power_.\n" +
                                        "_-_ It has four talents:\n" +
                                        "_-_ _Bloodflare Skin_ buffs enduring and enhances clone and hawk's survivability.\n" +
                                        "_-_ _Astral Charge_ buffs beacon and wild magic.\n" +
                                        "_-_ _Shadowspec Blade_ buffs clone and hawk's offensive capabilities.\n" +
                                        "_-_ _Silva Range_ buffs nature's power and gives ranged attack to clone."
                        ),
                        new ChangeButton(IRON_WILL, "Warrior",
                                "_-_ Changed One Man Army and Skill to have unique sprite.\n" +
                                        "_-_ Replaced _Restored Willpower_ with _Willpower of Injured_: makes seal's shielding regenerate faster on low HP.\n" +
                                        "_-_ Added _Weapon Mastery_ talent as 5th T1: increases the minimum amount of weapons.\n"+
                                        "_-_ Added _Big Rush_ talent as 6th T2: causes warrior to ram enemies instead of going around them, with bonus damage from seal's shielding.\n" +
                                        "_-_ Changed _Hold Fast_: now gives minimal armor value.\n" +
                                        "_-_ Reworked _Endless Rage_: now gives ability to gain rage from DoT, magic spells and Viscosity.\n" +
                                        "_-_ Changed _Berserkering Stamina_: now also makes shielding decay slower, but gives slighty less of it; level-shifting for bonus shielding is removed.\n" +
                                        "_-_ Gladiator gets 25 turns of combo after killing an enemy, from 15.\n" +
                                        "_-_ Replaced _Cleave_ with _Battle Tendency_: causes combo to decay instead of resetting.\n" +
                                        "_-_ Added _Heroic Endurance_ talent as additional T4: adds bonus upgrades to any armor.\n" +
                                        "_-_ Added _Alice Gambit_ to Heroic Leap: causes enemies around landing place to shrink after leap.\n" +
                                        "_-_ Reworked _Double Jump_: gives second jump for free at +2 and adjusts cost for third jump starting from +3.\n" +
                                        "_-_ Added _Cockatriocious_ to Shockwave: turns enemies caught in shockwave into stone.\n" +
                                        "_-_ Added _Demonshader_ to Endure: imbues endure-empowered attacks with fire damage and ability to generate rage and combo."),
                        new ChangeButton(SORCERY, "Mage",
                                "_-_ Changed Sorcery and Warlock's Touch to have unique sprite.\n" +
                                        "_-_ Added _Arcane Boost_ talent as 5th T1: slightly increases recharge speed on wands not in staff.\n" +
                                        "_-_ Nerfed _Shield Battery_ by 17.3%.\n" +
                                        "_-_ Added _Pyromaniac_ talent as 6th T2: increases damage from all fire in the game.\n" +
                                        "_-_ Nerfed _Excess Charge_: 50% reduced shielding.\n" +
                                        "_-_ Added _Heroic Wizardry_ talent as additional T4: allows to use wand charges beyond 0.\n" +
                                        "_-_ Buffed _Elemental Blast_ to have its base damage based on staff's melee damage.\n" +
                                        "_-_ Added _Empowered Strike II_ to Elemental Blast: doubles the damage and effects of ability for 2x charge cost.\n" +
                                        "_-_ Added _Eldritch Blessing_ to Wild Magic: makes all zaps cursed, cursed effects are safer and stronger while using the ability.\n" +
                                        "_-_ Added _Chrono Screw_ to Remote Beacon: gives time bubble after successful teleportation."),
                        new ChangeButton(MYSTICAL_UPGRADE, "Rogue",
                                "_-_ Changed Lethal Momentum and Marathon Runner to have unique sprite.\n" +
                                        "_-_ Added _Faraday Cage_ talent as 5th T1: protects hero from electricity.\n"+
                                        "_-_ Replaced _Rogue's Foresight_ with _Protein Infusion_: gives bonus speed and evasion depending on satiety.\n" +
                                        "_-_ Added _Efficient Shadows_ talent as 6th T2: removes cloak's charge speed boost for more invisibility time per charge.\n" +
                                        "_-_ Fixed _Dual Wielding_: no longer causes softlocks.\n" +
                                        "_-_ Added _Heroic Stamina_ talent as additional T4: gives movespeed boost for using artifacts.\n" +
                                        "_-_ Added _Frigid Touch_ to Smoke Bomb: sets the hero's FOV on frost fire after blinking.\n" +
                                        "_-_ Reworked _Double Mark_: gives second use for free at +2 and adjusts cost for third use starting from +3.\n" +
                                        "_-_ Added _Cataclysmic Energy_ to Death Mark: increases the duration of death mark and removes damage boost, but marked enemies at 0 HP will combust each turn.\n" +
                                        "_-_ Added _Dar Magic_ to Shadow Clone: gives various ranged capabilities to shadow clone."),
                        new ChangeButton(NATURES_AID, "Huntress",
                                "_-_ Changed Nature's Better Aid to have unique sprite.\n" +
                                        "_-_ Added _Greenfields_ talent as 5th T1: increases regeneration while in furrowed grass.\n"+
                                        "_-_ Replaced _Nature's Aid_ with _Scout's Barrier_: gives shielding for successful Super-Shots.\n" +
                                        "_-_ Added _Scout's Agility_ talent as 6th T2: makes heroine dodge ranged attacks better.\n" +
                                        "_-_ Armored Cloak is preserved when using DK's crown.\n" +
                                        "_-_ Added _Heroic Archery_ talent as additional T4: adds bonus upgrades to thrown weapons.\n" +
                                        "_-_ Added _Spectral Shot_ to Spectral Blades: makes blades behave like throwing weapons and summon arrows from her bow.\n" +
                                        "_-_ Added _Primal Awakening_ to Nature's Power: with small chance turns enemies into gnoll trickster while buff is active.\n" +
                                        "_-_ Added _Beak of Power_ to Spirit Hawk: increases utility power of hawk and gives it ranged attack at +4.")
                ),
                NewContent(new ChangeButton(new ItemSprite(ROYAL_SWORD), "Tier 6 weapons",
                                "Added 10 melee and 4 thrown exceptionally rare tier 6 weapons.\n\n" +
                                        "_-_ They appear in ebony chest and have many special abilities.\n" +
                                        "_-_ T6 weapons also omni-melee weapon and omni-thrown weapon, called _Royal Brand_ and _Steel Axe_, respectively."),
                        new ChangeButton(new ItemSprite(WAND_UNSTABLE), "New wand",
                                "Added wand of His Ratiness, which will create random wand zaps and use random wand effects for Battlemage and Elemental Blast."),
                        new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "New champions",
                                "Added a lot of champion types, increasing their number to 15.")),
                new ChangeInfo("", false, 0x000000, "",
                        bugFixes("_-_ Fixed light cloak charge speed being wrong (was higher than intended for Rogue and lower for RK)\n\n" +
                                "_-_ Fixed Wealth and Might using wrong percentages in their descriptions.\n" +
                                "_-_ Fixed Shrinking being only cosmetic effect.\n" +
                                "_-_ Fixed shop appearing on D21."),
                        misc("_-_ Added some additional lore and sprite edits for dungeon monsters.\n\n" +
                                "_-_ Added missing loot into abyssal shops.")
                )
        },
            // v0.1.0
        {
            new ChangeInfo("v0.1.0", true, TITLE_COLOR, ""),
            NewContent(
                new ChangeButton(Icons.get(INFO), "Developer Commentary",
                    "I regret the relatively long wait for a v0.9.3 implementation, but RKPD2 is now updated to Shattered v0.9.3c! Enjoy smaller levels, some quality of life improvements, and even more buffs to your favorite heroes."
                    + "\n\nI'm currently messing with a new method with which to buff talents and heroes: _point shifting_. Point shifting means that I make +1 effects +0 effects, +2 effects +1 effects, and so on. This results in an immediate power bump when the particular talents are obtained, though it can be slightly confusing on the first time (or with tier 2 talents)."
                    + "\n\nThere's probably going to be a v0.1.1 that will add more thematic changes that I can make building off of this update."
                ),
                new ChangeButton(new ItemSprite(CROWN), "T4 Talents Implemented!", ""
                    +"Now bringing you a RKPD2 with FULL talents!!!"
                    +list(2,
                        "Epic armors and T4 talents have been directly implemented.",
                        "The 12 points for t3 talents remain, causing the hero to uniquely gain two talent points per level for levels 21-24.",
                        "You can still supercharge your armor if you have too low charge, don't worry, though I doubt it'll be as notable as before with the charging mechanic changes and Heroic Energy.",
                        "Rat King has two \"special\" abilities to choose from, currently, and one more will hopefully be added in future updates.")
                    + "_Ability Buffs_:" + list("Heroic Energy, Elemental Power, Growing Power, and Reactive Barrier are now more potent.", "Nature's Wrath is now 10%/20%/30%/40% prc.", "Ratforcements spawns scaled rats that are much stronger than standard rats. It can also summon scaled loyal albinos now.", "Ratlomancy now gives 50% more adrenaline for 50% more fun.", "Death mark now gives +33% damage, up from +25%.","Blast Radius, Long Range Warp, and Telefrag have been level-shifted", "Projecting blades penetration is now 1/3/5/7/9, down from 2/4/6/8/10, and its accuracy boost is 0/33/67/100/133 instead of 25/50/75/100/125.", "Fan of blades now affects 1/2-3/4/5-6/7 targets in a 30/45/90/135/180 degree AOE cone.")),
                new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "New Challenge!", "Badder bosses has been implemented into RKPD2, enjoy teaching those bosses that no matter what they do, they will still lose."),
                new ChangeButton(Icons.get(TARGET), "Special Action Targeting", "Added smart targeting for Combo, Preparation, and Sniper's Mark's free shot via Multishot."
                    + list(2,
                        "When there is only one target for the action, the action will skip the prompt and simply execute.", "When there is more than one possible target (or no targets), the valid targets will be highlighted.")
                    + "This should make using these abilities much more smooth when fighting a single enemy as well as making them more intuitive in general.")),

            Changes(
                new ChangeButton(RAT_KING, ""
                        +"Shattered balance changes have been directly implemented to Rat King's mechanics, for better or for worse:"
                        +list(2,
                            "Noble Cause nerfed, gives less shielding when the staff runs out of charge.",
                            "Imperial Wrath buffed, now gives more shielding on berserk (yay?)",
                            "Tactics buffed, its strongman now uses the v0.9.3 version and its description has been updated to indicate that cleave makes combo last for 15/30/45 turns. This talent is probably the biggest winner of this update.",
                            "Ranged Terror buffed, now gives a greater damage boost to specials when using thrown weapons.",
                            "Royal Presence changed, now has an increased chance to spawn wraiths but decreased indirect damage soul mark effectiveness and ally warp range",
                            "Natural Dominance nerfed, now gives 50/100/150% Rat King's level in barkskin that fades every turn instead of previous barkskin effect.")),
                new ChangeButton(Icons.get(DEPTH), "Levelgen", "Implementing SHPD v0.9.3's levelgen changes has resulted in an even smaller RKPD2!"
                        +list(2,
                            "Levels should be slightly smaller than before in terms of room amounts",
                            "There also should be less tunnels, which should significantly cut down further on level sizes. Up until now RKPD2 was plagued by very long connection room generation. No longer!",
                            "Entrance and Exits can no longer connect to each other.",
                            "Pit room size minimum increased.",
                            "Hordes are somewhat less likely to spawn on floor 1 despite the smaller size.")
                ),
                misc(list(2,
                        "Implemented virtually all misc changes up to SHPD v0.9.3c, including the addition of quick-use bags, stone behavior, etc.",
                        "Sorcery talent moved from 3rd slot to 6th slot for consistency with other RKPD2-exclusive talents.",
                        "Updated some talent descriptions to be clearer or otherwise add commentary.",
                        "Added unique dialogue for Rat King-Wandmaker interactions",
                        "Dwarf King has a new snide comment for you in badder bosses!",
                        "Dwarf King now bellows on certain quotes",
                        "Added differing descriptions for loyal rats and hostile rats, might wanna look at their descriptions when you get the chance.")
                ),
                bugFixes(list("Bugfixes up to SHPD v0.9.3c have been implemented.",
                        "King's Vision now correctly updates Rat King's field of view on the turn it is upgraded.",
                        "Fixed some talent description typos.")
                        // v0.1.0a
                        + list("Rogue's cloak boost to weapons being incorrectly displayed in some cases", "Missing text for certain Rat King's Wrath interactions", "String formatting failure in Enhanced Scrolls", "Various typos in talent and subclass descriptions.")
                        // v0.1.0b
                        + list("Sorcery not giving BM effects on non-staff melee attacks", "Berserking Stamina not level-shifted in terms of berserk recovery speed.", "Confusing typo in Berserking Stamina description", "Spectral blades being able to attack NPCs (actually a shattered bug)", "Fan of Blades dealing more damage to additional targets than intended.", "Projecting Blades giving more wall penetration for the initial target than additional targets."))),

            Buffs(
                new ChangeButton(HUNTRESS, "I'm leaning harder on giving subclasses access to talents without upgrading them, and _Warden_ is an excellent place to start:"
                    +list("Tipped Darts shifted, now gives 2x/3x/4x/5x durability.",
                        "v0.9.3 Barkskin implemented, but Barkskin now degrades every two turns instead of one and gets barkskin equal to 50/100/150/200% of her level at +0/1/2/3 respectively. Thus instead of nerfing it, I have buffed it ;)")
                    +"\nIt's also clear that _Sniper needs buffs._"
                    +list("Farsight shifted, now gives 25/50/75/100% bonus sight.",
                        "Shared Upgrades shifted (now 10/20/30/40), and the increasing duration aspect is given at +0.",
                        "New Sniper perk: +33 accuracy.")
                    //+"\n"
                    //+"\n_DEFERRED_ Multishot shifted, +3 now is 4x duration+4x target"
                    // TODO see if I can make the new targeting make this a smooth addition for future
                    //+"\n_TODO_ Multishot free shot can stack with standard marking."
                    //+"\n"
                ),
                new ChangeButton(WARRIOR, list(2,
                        "Cleave buffed from 0/10/20/30 (was bugged) to 15/30/45/60.",
                        "Endless Rage buffed from +15%/30%/45% to +20%/40%/60%",
                        "Enraged Catalyst buffed from 17%/33%/50% to 20%/40%/60%.",
                        "Berserking Stamina level-shifted.",
                        "Improvised projectiles is not nerfed.")),
                new ChangeButton(BATTLEMAGE, "Battlemage is currently a bit 'weaker' than warlock, and thus it's getting a power spike after subclassing."
                        + list(2,
                            "Mystical Charge recharging now .5/1/1.5/2 instead of 0/.75/1.5/2.25 at +0/1/2/3 respectively.",
                            "Excess Charge proc chance is now 20/40/60/80 at +0/1/2/3, up from 0/25/50/75.")
                ),
                misc(list(2,
                        "Assassin's Enhanced Lethality is buffed to be in line with SHPD",
                        "Studded gloves damage now 1-6, up from 1-5.",
                        "Empowering scrolls talent no longer is time-limited, boost increased to +2/+4/+6.",
                        "Shops sell upgraded items for cheaper (again)."
                ))
            ),
            Nerfs(
                new ChangeButton(IRON_WILL,
                    "I've decided to take the opportunity to yank Iron Will's bonus upgrade mechanics. It's really unfun to use and it kinda warps the game to a large extent."
                            +list(2,
                                "Iron Will no longer gives bonus upgrades to the seal.",
                                "Iron Will now increases the seal's maximum shield by 1/3/5 and reduces the time it takes to fully recharge all shield by 30/60/90 turns."
                            )
                            +"This change makes iron will recharge the seal super fast, even when not upgraded. At the very start of the game, the time to fully recharge two shield is halved!"
                ),
                new ChangeButton(STRONGMAN, "I've implemented and (obviously) buffed Shattered's Strongman (13%/21%/29% up from 8%/13%/18%), now that Evan has finally gotten his act together and realized that I was right all along."
                        +"\n\n_+3:_"+list(":14-:17 strength -> +4 -> 18/19/20/21 down from 19/20/21/22",
                            "12-13 strength -> +3 -> 15/16 down from 17/18",
                            "10-11 strength -> +2 -> 12/13 down from 15/16",
                            "It is effectively around the same starting around 15 strength (16 if you were using a greataxe), but is increasingly worse prior to that."
                        ) + "\n_+2_:"+list("Gives a worse boost (2 down from 3) before the hero naturally attains 15 strength.",
                            "Gives a better boost once the hero has natural 20 strength"
                        )
                        +"\n_+1_:"+list("Gives +2 strength, up from its effective previous value of +1, once the hero naturally attains 16 strength."
                        )
                        +"\nOverall Strongman is a bit worse at +3 and a bit better at +2 and +1. Its ability to be exploited is down due to now being reliant on having strength, but in return it also gives true strength bonuses (thus opening up synergies with rings of force and might...)"
                ),
                new ChangeButton(BACKUP_BARRIER, "Backup barrier now generates 5/8 shield, down from 6/9, to reflect the Shattered nerf to Backup Barrier."))
        },

        {
                new ChangeInfo("Abyssal", true, TITLE_COLOR, "",
                        new ChangeButton(get(Icons.DEPTH),"New chapter!",
                                "Added endless abyss chapter from Summoning PD, excluding exclusive traps"),
                        new ChangeButton(avatar(RAT_KING,6), "Rat King Nerfs (rat king nerfs)",
                                "Rat King's incomprehensible power bugged me during development of abyss too much.\n\n" +
                                        "_-_ Reduced HP by 60%"),
                        new ChangeButton(new Image(Assets.Sprites.HUNTRESS, 0, 15, 12, 15), "Huntress changes",
                                "The Huntress recieved brand new starting armor to make her more stylish and strong.\n\n" +
                                        "_-_ With new armor, she can use _Super-Shots_ to shoot stronger arrow which does more damage with distance.\n\n" +
                                        "_-_ This ability has 35 turns cooldown.\n\n" +
                                        "_-_ The armor works as +1 cloak armor, but still can be viable in endgame."),
                        new ChangeButton(new ItemSprite(new RatKingArmor()), "Rat King's Wrath",
                                "Reworked Rat King's Wrath into full armor ability with 3 exclusive talents:\n\n" +
                                        "_-_ _Auric Tesla Ability_ twists Wrath's components to have properties of 0.9.3 Shattered armor abilities.\n\n" +
                                        "_-_ _Quantum Positioning_ significantly extends Wrath's range.\n\n" +
                                        "_-_ _Rat Age Origins_ turns Smoke Bomb component into gas spreader which can freeze, confuse and damage enemies.\n\n" +
                                        "Molten Earth damage, Heroic Leap paralysis, Smoke Bomb invis and Spectral Blades range have been signficantly adjusted, cost increased from _35_ to _100_."),
                        new ChangeButton(new ItemSprite(RING_AGATE), "Ring caps",
                                "Most of the rings have been capped at 2x of their effect (2.5x for Furor, 60% damage reduction for defense rings and no cap for SS and Wealth)."),

                        new ChangeButton(new Image(TALENT_ICONS, 16*5,16,16,16), "Talent changes",
                                "_-_ Reworked _Energized Upgrade_ to recharge auxiliary wands when they are fully used.\n\n" +
                                        "_-_ Replaced _Mystical Upgrade_ with _Dual Wielding_, allowing player to use wands and missiles at same time.\n\n" +
                                        "_-_ Healing potion talents work on drinking from waterskin.\n\n" +
                                        "_-_ Replaced _Restored Nature_ with _Ivylash_, which augments super-shots with Rooting.\n\n" +
                                        "_-_ Huntress T3 and T4 talents boost the damage potential of Super-Shots."
                        )
                )
        },
            // v0.0.1
        {
            new ChangeInfo("v0.0.1", true, TITLE_COLOR, "") {{
                addButton(new ChangeButton(get(Icons.INFO), "Developer Commentary", "This update is mostly just bugfixes and balance adjustments. More substantial changes should come when SHPD v0.9.3 is released.\n\nDo note that while things are intended to be broken, I'm aiming for a state where things are 'evenly' overpowered such that you can play any class or do any build and be like 'that's really damn good' for everything, rather than resetting (or just choosing talents!) for that same broken build every time."));
                addButton(new ChangeButton(WARRIOR, "This is intended to make Warrior (and Berserker) a little more balanced powerwise compared to other stuff in the game."
                    + "\n\nGeneral:" + list("Implemented buffed runic transference.",
                        "Nerfed Iron Stomach to be in line with SHPD.",
                        "Removed Iron Will charge speed increase and bonus shielding.",
                        "Strongman buffed to 1/3/5 from 1/2/3. It's good now I swear!")
                    + "\nBerserker:" + list("Fixed a bug that made it MUCH easier to get rage while recovering than intended.",
                        "Berserking Stamina recovery nerfed from 1.5/1/.5 to 1.67/1.33/1. Shielding unchanged.",
                        "Enraged Catalyst was bugged to be at shpd levels (17/33/50), but now that's intended.",
                        "One Man Army now only gives boost if you're in combat, rather than simply relying on line of sight.",
                        "Berserker no longer gets rage from non-melee sources. (this was an unintentional addition)",
                        "Current HP weighting now refers to actual current HP, instead of current HP after damage is dealt.")
                ));
                addButton(new ChangeButton(ROGUE, "Of all the classes, Rogue has the most exploits and uneven gameplay. These changes are intended to make him seem less repetitive/unbalanced.\n"
                    + list("Removed Mystical Meal-Horn of Plenty exploit, it was WAY more potent than I'd thought it would be.",
                        "Sucker punch now only works on the first surprise attack, numbers unchanged.",
                        "Marathon runner potency halved (is now 17%/33%/50%).",
                        "Mystical Upgrade is now more potent, gives instant 8/12 recharge to non-cloak artifacts, up from 5/8.",
                        "Enhanced Rings now stacks.")
                ));
                addButton(new ChangeButton(avatar(HUNTRESS, 6), "Multishot and Warden", ""
                        + "\nMultishot:" + list("now has a unique icon.",
                        "levels for targets are now evaluated independently for the purposes of shared upgrades.",
                        "the total duration of sniper's mark is now the sum of the durations of each marked target.",
                        "free shot is now kept for 1/2/3x duration at +1/2/3.",
                        "Improved target handling between floors and when some targets cannot be shot.")
                        + "\nWarden's power seems to currently be concentrated in Nature's Better Aid:"
                        + list("NBA seed boost now +17/33/50% down from +33/66/100%",
                        "NBA dew boost now +8/17/25% down from +33/66/100%",
                        "Shielding Dew now also gives upfront shielding equal to 25%/50%/75% of the heal when not at full HP.")));
                addButton(new ChangeButton(get(Icons.TALENT), "General Talents", ""
                        + list("Scholar's Intuition +2 now has a 50% chance to identify consumables when picking them up, down from 2/3",
                        "Scholar's Intuition +1 now identifies wand levels on the first zap.")
                        + list("Restored Nature now roots for 4/6 turns, up from 3/5")
                        + list("Seer Shot cooldown reduced from 20 to 5. (It was bugged to be 20 instead of 10)",
                        "Natural Dominance's Seer Shot cooldown increased to 20 from 10")
                        + "\n_-_ Fixed an oversight that caused Soul Siphon to cause wands to trigger soul mark at 200/300/400% effectiveness instead of 20/30/40%"
                ));
                addButton(new ChangeButton(new ItemSprite(KIT), "Epic Armor", ""
                    + list(2,
                        "Overcharging is now standardized to +100% per overcharge, down from 200% for everyone but Rat King.",
                        "Rogue Garb now has no limit on the distance it can jump.",
                        "Huntress Spectral Blades now has no distance limit.")
                    + "Rat King's Wrath:" + list("jump takes an extra turn.",
                        "Changed effects to make them more distinct (and fixed a couple omitted visual effects)",
                        "Changed the priority of the invisibility to actually cover the last turn of delay.")
                ));
                addButton(misc(list(2,
                        "Changed Rat King's hero description in Hero Select and the descriptions of many of his t3 talents.", "Added a note about how to toggle special actions for Rat King's subclass, and added a hotkey for toggling special action for desktop users.", "Added unique dialog for Ambitious Imp.")
                        + list("Huntress spirit bow now scales to +10, down from +12.", "Empowering Scrolls talent's effect now lasts for 40 turns, up from 30.", "Shopkeepers now offer reasonable deals for both buying and selling...", "ID VFX is now used for on-equip identification.", "Adjusted VFX for Hearty Meal and Test Subject.", "Added a shatter vfx effect when 'skipping' DK.", "Added a sfx for fully recovering, since apparently it's needed.",
                    "Troll Blacksmith now transitions directly into the reforge window after initially talking to him.")
                        + list("The level at which enemies stop giving exp has been increased again by 1 (now +2 compared to shattered)", "The level at which enemies stop dropping stuff has been decreased by 1 to match this new value. (now same as shattered)").trim() + list("explosive arrows can now proc on miss regardless of where they are shot.", "Adrenaline and Sniper's Mark visual fades now match their initial durations", "Dwarf King now gloats if he manages to kill you.")));
                addButton(bugFixes("Fixed many bugs involving soul marking that were directly impacting the power of soul mark:"
                    + list("Warlock's Touch can now actually provide its benefit immediately.",
                        "Fixed melee attacks proccing soul mark twice.",
                        "Fixed melee attacks coming from the hero being considered indirect damage and thus requiring Soul Siphon or Presence to work with soul mark at all.",
                        "Rare crashes when zapping the shopkeeper with a wand that can soul-mark.")
                    + "\nOther fixes:" + list("Huntress and Rat King getting more berries at +2 from their respective talents than intended.", "Being able to apply sniper's mark to multiple foes when it shouldn't be possible.",
                        "Typos")
                        + list("natural dominance incorrectly giving ally swap instead of king's presence", "Rat King getting Assassin's reach buffs instead of rogue", "Rogue not actually recieving innate 2x ring id speed.", "display bugs with rings, especially might and cursed rings.", "Multishot not checking if a given character is already marked when marking an enemy with sniper's mark", "'Hang' bug when attempting to target an out-of-range character with free shot from multi-shot", "Shattered bug where dk shows his alert during the whole second phase.")));
            }}
        },
            // v0.0.0
        {
            new ChangeInfo("v0.0.0",true, TITLE_COLOR, "This is a list of all the stuff I've changed from SHPD, for those who are curious."),
            new ChangeInfo("Classes",false,"") {{
                    addButton(new ChangeButton(avatar(RAT_KING,6), "New Class!",
                            "Added a new class! Rat King is supposed to be a sort of 'omniclass', with the perks of all the shattered classes combined.\n\n"
                                    + "He also has his subclass implemented, which, in line with the above, is of course all subclasses in one. I'm pretty proud of this one, give it a shot!"));
                    addButton(new ChangeButton(WARRIOR,""
                            + list("Iron Will now increases the amount of upgrades the seal can store.",
                                "All t1 talents buffed.")
                            + "\n_Berserker_:" + list("Rage gain now scales with HP as talents are upgraded",
                                "Added a new talent that makes him attack faster with more enemies around")
                            + "\n_Gladiator_:" + list("All finishers get an extra damage roll check for increased consistency via Skill talent")));
                    addButton(new ChangeButton(MAGE, list(2,
                            "Mage now has intrinsic +2 level to all wands for purposes of power and recharge",
                            "Battlemage now has a new talent that lets him spread his staff effects to all his attacks",
                            "Battlemage gets +2 effect on his staff.",
                            "Warlock can now soul mark with weapons and all damage can now trigger soul mark through new Warlock's Touch talent",
                            "Most talents buffed.",
                            "Empowering meal has been removed (for mage at least) and replaced with Energizing Meal, t2 meal replaced with something else.")));
                    addButton(new ChangeButton(ROGUE, list(2,
                            "Now gets an invisible +1 to weapons when cloak is active",
                            "Subclasses get more invisible upgrades to various item types.",
                            "Subclasses have their t3s buffed.",
                            "Cloak recharges faster",
                            "Talents buffed",
                            "Protective Shadows replaced by mending shadows.")));
                    addButton(new ChangeButton(HUNTRESS,
                        "Huntress has also recieved 'tweaks'!" + list(2,
                                "Spirit Bow now scales more.",
                                "The Spirit bow can be enchanted simply by using a scroll of upgrade on it.",
                                "Huntress has her innate heightened vision and missile durability back!",
                                "Huntress talents have been buffed across the board by ~50%.",
                                "Replaced invigorating meal with adrenalizing meal. ;)",
                                "Added new talents to both subclasses")));
                    addButton(new ChangeButton(new ItemSprite(new VelvetPouch()), "Bags",
                            "All heroes start with all bags. This should reduce the stress of inventory management throughout the game and also make gold more plentiful."));
                }},
            new ChangeInfo("Misc", false, "") {{
                    addButton(new ChangeButton(new WandOfFirebolt(), "Added wand of firebolt."
                            + '\n' + list("Does 0-9 damage with +1/+6 scaling instead of its vanilla/early shattered 1-8 with exponential scaling.",
                                "Is otherwise unchanged.")
                            + "\nThis should make it more consistently overpowered instead of requiring 10+ upgrades to reach actually astounding performance. I can probably call this my greatest mistake of all time. Oh well..."));
                    addButton(new ChangeButton(get(Icons.DEPTH),"Level gen changes", list(2,
                            "Amount of standard rooms reduced by 30%-50% in honor of precedent set by Rat King Dungeon.",
                            "Gold yield from Rat King's room increased by ~10x, general gold yield increased by 50%",
                            "Hero takes 1/3 damage from Grim traps.",
                            "Rat King room has a sign in it if playing as rat king as a homage to RKPD",
                            "Enemy level caps increased by 1")));
                    addButton(new ChangeButton(new KingSprite(),"Boss Changes","Certain bosses have recieved adjustments:" +
                            list(2,"Dwarf King fight start altered.",
                                "Dwarf King's phases can now be skipped with enough burst damage.",
                                "Dwarf King and DM-300 have altered quotes for Rat King",
                                "DM-300 fight starts directly upon moving initially, instead of when you see a pylon. Better know what you are doing!")));
                    addButton(new ChangeButton(new ItemSprite(new SpiritBow().enchant(new Explosive())),"Spirit Bow enchantments",
                            "The idea here is to lower the chance of 'misfiring' when enchanting the spirit bow dramatically."
                                    + list(2,
                                        "Added a new spirit-bow exclusive enchantment.",
                                        "It, along with grim, will replace blocking and lucky when using a scroll of enchantment on the bow (or a scroll of upgrade in the case of huntress).")));
                    addButton(new ChangeButton(new ItemSprite(new WarriorArmor()),"Class Armor","Who liked limits anyway? You can trade current HP for a full charge."));
                    addButton(new ChangeButton(get(Icons.LANGS),"Text Changes",
                            "I've changed some of the text in the game, including:"
                            + list("Class descriptions both ingame and in the select screen",
                                "Some enemy descriptions",
                                "Some boss quotes",
                                "This changelog")
                            + "\nHowever, since I only know English and do not have an active translation project, I've set the language to English. Sorry!"));
                }}
        },
    };
}
