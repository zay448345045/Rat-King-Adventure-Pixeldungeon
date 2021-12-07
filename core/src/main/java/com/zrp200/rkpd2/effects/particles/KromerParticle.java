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

package com.zrp200.rkpd2.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

import java.util.Arrays;

public class KromerParticle extends PixelParticle {

	public static final Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			emitter.recycle( KromerParticle.class ).reset( x, y );
		}
		@Override
		public boolean lightMode() {
			return true;
		}
	};

	public KromerParticle() {
		super();
		
		lifespan = 1f;
		color( Random.element(Arrays.asList(
				0x00ff54, 0xf6e316, 0xff51c2, 0x4f4573)) );

		acc.set( 0, +30 );
	}
	
	public void reset( float x, float y ) {
		revive();

		left = lifespan;

		size = 10;
		this.x = x;
		this.y = y;

		speed.polar( -Random.Float( 3.1415926f ), Random.Float( 6 ) );
	}
	
	@Override
	public void update() {
		super.update();
		
		float p = left / lifespan;
		am = p < 0.5f ? p * p * 4 : (1 - p) * 2;
		size( Random.Float( 6 * (left / lifespan) ) );
	}
}