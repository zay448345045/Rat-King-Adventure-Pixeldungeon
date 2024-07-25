/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.zrp200.rkpd2.levels;

import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Bones;
import com.zrp200.rkpd2.Challenges;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.Bat;
import com.zrp200.rkpd2.actors.mobs.CrystalWisp;
import com.zrp200.rkpd2.actors.mobs.FungalSpinner;
import com.zrp200.rkpd2.actors.mobs.GnollGuard;
import com.zrp200.rkpd2.actors.mobs.Mob;
import com.zrp200.rkpd2.actors.mobs.npcs.Blacksmith;
import com.zrp200.rkpd2.items.Generator;
import com.zrp200.rkpd2.items.Heap;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.Torch;
import com.zrp200.rkpd2.items.quest.DarkGold;
import com.zrp200.rkpd2.items.quest.Pickaxe;
import com.zrp200.rkpd2.levels.builders.Builder;
import com.zrp200.rkpd2.levels.builders.FigureEightBuilder;
import com.zrp200.rkpd2.levels.features.LevelTransition;
import com.zrp200.rkpd2.levels.painters.MiningLevelPainter;
import com.zrp200.rkpd2.levels.painters.Painter;
import com.zrp200.rkpd2.levels.rooms.Room;
import com.zrp200.rkpd2.levels.rooms.quest.MineEntrance;
import com.zrp200.rkpd2.levels.rooms.quest.MineGiantRoom;
import com.zrp200.rkpd2.levels.rooms.quest.MineLargeRoom;
import com.zrp200.rkpd2.levels.rooms.quest.MineSecretRoom;
import com.zrp200.rkpd2.levels.rooms.quest.MineSmallRoom;
import com.zrp200.rkpd2.levels.rooms.standard.StandardRoom;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.BlacksmithSprite;
import com.zrp200.rkpd2.tiles.CustomTilemap;
import com.zrp200.rkpd2.windows.WndOptions;
import com.zrp200.rkpd2.windows.WndTitledMessage;

import java.util.ArrayList;
import java.util.Arrays;

import static com.zrp200.rkpd2.actors.mobs.npcs.Blacksmith.Quest.MAX_GOLD;

public class MiningLevel extends CavesLevel {

	@Override
	public String tilesTex() {
		switch (Blacksmith.Quest.Type()){
			default:
				return Assets.Environment.TILES_CAVES;
			case Blacksmith.Quest.CRYSTAL:
				return Assets.Environment.TILES_CAVES_CRYSTAL;
			case Blacksmith.Quest.GNOLL:
				return Assets.Environment.TILES_CAVES_GNOLL;
		}

	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.CAVES_TENSE, true);
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = new ArrayList<>();
		initRooms.add ( roomEntrance = new MineEntrance());

		//spawns 1 giant, 3 large, 6-8 small, and 1-2 secret cave rooms
		StandardRoom s;
		s = new MineGiantRoom();
		s.setSizeCat();
		initRooms.add(s);

		// -33%
		int rooms = 2;
		for (int i = 0; i < rooms; i++){
			s = new MineLargeRoom();
			s.setSizeCat();
			initRooms.add(s);
		}

		// -33%
		rooms = Random.NormalIntRange(4, 6);
		for (int i = 0; i < rooms; i++){
			s = new MineSmallRoom();
			s.setSizeCat();
			initRooms.add(s);
		}

		// unchanged
		rooms = Random.NormalIntRange(1, 2);
		for (int i = 0; i < rooms; i++){
			initRooms.add(new MineSecretRoom());
		}

		return initRooms;
	}

	@Override
	protected Builder builder() {
		return new FigureEightBuilder().setPathLength(0.8f, new float[]{1}).setTunnelLength(new float[]{1}, new float[]{1});
	}

	@Override
	protected boolean build() {
		if (super.build()){
			CustomTilemap vis = new BorderTopDarken();
			vis.setRect(0, 0, width, 1);
			customTiles.add(vis);

			vis = new BorderWallsDarken();
			vis.setRect(0, 0, width, height);
			customWalls.add(vis);

			return true;
		}
		return false;
	}

	@Override
	protected Painter painter() {
		return new MiningLevelPainter()
				// 33 - 39
				.setGold(Random.NormalIntRange((int)(MAX_GOLD*1.1), (int)(MAX_GOLD*1.3)))
				.setWater(Blacksmith.Quest.Type() == Blacksmith.Quest.FUNGI ? 0.1f : 0.35f, 6)
				.setGrass(Blacksmith.Quest.Type() == Blacksmith.Quest.FUNGI ? 0.65f : 0.10f, 3);
	}

	@Override
	public int mobLimit() {
		//1 fewer than usual
		return super.mobLimit()-1;
	}

	@Override
	public Mob createMob() {
		switch (Blacksmith.Quest.Type()){
			default:
				return new Bat();
			case Blacksmith.Quest.CRYSTAL:
				return new CrystalWisp();
			case Blacksmith.Quest.GNOLL:
				return new GnollGuard();
			case Blacksmith.Quest.FUNGI:
				return new FungalSpinner();
		}
	}

	@Override
	public float respawnCooldown() {
		//normal enemies respawn much more slowly here
		return 3*TIME_TO_RESPAWN;
	}

	@Override
	protected void createItems() {
		Random.pushGenerator(Random.Long());
			ArrayList<Item> bonesItems = Bones.get();
			if (bonesItems != null) {
				int cell = randomDropCell();
				if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
					map[cell] = Terrain.GRASS;
					losBlocking[cell] = false;
				}
				for (Item i : bonesItems) {
					drop(i, cell).setHauntedIfCursed().type = Heap.Type.REMAINS;
				}
			}
		Random.popGenerator();

		int cell = randomDropCell();
		if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
			map[cell] = Terrain.GRASS;
			losBlocking[cell] = false;
		}
		drop( Generator.randomUsingDefaults(Generator.Category.FOOD), cell );
		if (Blacksmith.Quest.Type() == Blacksmith.Quest.GNOLL){
			//drop a second ration for the gnoll quest type, more mining required!
			cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( Generator.randomUsingDefaults(Generator.Category.FOOD), cell );
		}

		if (Dungeon.isChallenged(Challenges.DARKNESS)){
			cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( new Torch(), cell );
		}
	}

	@Override
	protected int randomDropCell() {
		//avoid placing random items next to hazards
		return randomDropCell(MineSmallRoom.class);
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.MINE_CRYSTAL:
				return Messages.get(MiningLevel.class, "crystal_name");
			case Terrain.MINE_BOULDER:
				return Messages.get(MiningLevel.class, "boulder_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.BRANCH_ENTRANCE
				&& !Blacksmith.Quest.completed() && Blacksmith.Quest.Type() != Blacksmith.Quest.OLD) {

			if (hero.belongings.getItem(Pickaxe.class) == null){
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndTitledMessage( new BlacksmithSprite(),
								Messages.titleCase(Messages.get(Blacksmith.class, "name")),
								Messages.get(Blacksmith.class, "lost_pick")));
					}
				});
				return false;
			}

			String warnText;
			DarkGold gold = hero.belongings.getItem(DarkGold.class);
			int goldAmount = gold == null ? 0 : (gold.quantity() * 100)/MAX_GOLD;
			if (goldAmount < 25){
				warnText = Messages.get(Blacksmith.class, "exit_warn_none");
			} else if (goldAmount < 50){
				warnText = Messages.get(Blacksmith.class, "exit_warn_low");
			} else if (goldAmount < 75){
				warnText = Messages.get(Blacksmith.class, "exit_warn_med");
			} else if (goldAmount < 100){
				warnText = Messages.get(Blacksmith.class, "exit_warn_high");
			} else {
				warnText = Messages.get(Blacksmith.class, "exit_warn_full");
			}

			if (!Blacksmith.Quest.bossBeaten()){
				switch (Blacksmith.Quest.Type()){
					case Blacksmith.Quest.CRYSTAL: warnText += "\n\n" + Messages.get(Blacksmith.class, "exit_warn_crystal"); break;
					case Blacksmith.Quest.GNOLL: warnText += "\n\n" + Messages.get(Blacksmith.class, "exit_warn_gnoll"); break;
					case Blacksmith.Quest.FUNGI: warnText += "\n\n" + Messages.get(Blacksmith.class, "exit_warn_fungi"); break;
				}
			}

			String finalWarnText = warnText;
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndOptions( new BlacksmithSprite(),
							Messages.titleCase(Messages.get(Blacksmith.class, "name")),
							finalWarnText,
							Messages.get(Blacksmith.class, "exit_yes"),
							Messages.get(Blacksmith.class, "exit_no")){
						@Override
						protected void onSelect(int index) {
							if (index == 0){
								Blacksmith.Quest.complete();
								MiningLevel.super.activateTransition(hero, transition);
							}
						}
					} );
				}
			});
			return false;

		} else {
			return super.activateTransition(hero, transition);
		}
	}

	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.WALL:
				return Messages.get(MiningLevel.class, "wall_desc");
			case Terrain.WALL_DECO:
				return super.tileDesc(tile) + "\n\n" +  Messages.get(MiningLevel.class, "gold_extra_desc");
			case Terrain.MINE_CRYSTAL:
				return Messages.get(MiningLevel.class, "crystal_desc");
			case Terrain.MINE_BOULDER:
				return Messages.get(MiningLevel.class, "boulder_desc");
			case Terrain.BARRICADE:
				return Messages.get(MiningLevel.class, "barricade_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		visuals.clear(); //we re-add these in wall visuals
		return visuals;
	}

	@Override
	public Group addWallVisuals() {
		super.addWallVisuals();
		CavesLevel.addCavesVisuals(this, wallVisuals, true);
		return wallVisuals;
	}

	public static class BorderTopDarken extends CustomTilemap {

		{
			texture = Assets.Environment.CAVES_QUEST;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			Arrays.fill(data, 1);
			v.map( data, tileW );
			return v;
		}

		@Override
		public Image image(int tileX, int tileY) {
			return null;
		}
	}

	public static class BorderWallsDarken extends CustomTilemap {

		{
			texture = Assets.Environment.CAVES_QUEST;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			for (int i = 0; i < data.length; i++){
				if (i % tileW == 0 || i % tileW == tileW-1){
					data[i] = 1;
				} else if (i + 2*tileW > data.length) {
					data[i] = 2;
				} else {
					data[i] = -1;
				}
			}
			v.map( data, tileW );
			return v;
		}

		@Override
		public Image image(int tileX, int tileY) {
			return null;
		}
	}
}
