/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.zrp200.rkpd2.actors.blobs;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.effects.BlobEmitter;
import com.zrp200.rkpd2.effects.Speck;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;

public class Inferno extends Blob {
	
	@Override
	protected void evolve() {
		super.evolve();
		
		int cell;
		boolean observe = false;
		
		Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		Freezing freeze = (Freezing)Dungeon.level.blobs.get( Freezing.class );
		
		for (int i = area.left-1; i <= area.right; i++) {
			for (int j = area.top-1; j <= area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0) {
					
					if (fire != null)   fire.clear(cell);
					if (freeze != null) freeze.clear(cell);
					
					Fire.burn(cell);

					if (Dungeon.level.flamable[cell]){
						Dungeon.level.destroy( cell );

						observe = true;
						GameScene.updateMap( cell );
					}
					
				} else if (Dungeon.level.flamable[cell]
						&& (cur[cell-1] > 0
						|| cur[cell+1] > 0
						|| cur[cell-Dungeon.level.width()] > 0
						|| cur[cell+Dungeon.level.width()] > 0)) {

					if (fire == null || fire.cur == null || fire.cur[cell] == 0) {
						GameScene.add(Fire.seed(cell, 4, Fire.class));
					}

				}
			}
		}
		
		if (observe) {
			Dungeon.observe();
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.pour( Speck.factory( Speck.INFERNO, true ), 0.4f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	
}
