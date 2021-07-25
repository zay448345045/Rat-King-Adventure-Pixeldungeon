package com.zrp200.rkpd2.sprites;

import com.watabou.noosa.TextureFilm;
import com.zrp200.rkpd2.Assets;

public class RatKingBossSprite extends CharSprite {

    public RatKingBossSprite() {
        super();

        texture( Assets.Sprites.RATBOSS );

        changeSprite(0);
    }

    public void changeSprite(int phase) {
        TextureFilm film = new TextureFilm( texture, 16, 17 );

        int offset = phase*16;

        idle = new Animation( 2, true );
        idle.frames( film, offset+0, offset+0, offset+0, offset+1 );

        run = new Animation( 10, true );
        run.frames( film, offset+6, offset+7, offset+8, offset+9, offset+10 );

        attack = new Animation( 15, false );
        attack.frames( film, offset+2, offset+3, offset+4, offset+5, offset+0 );

        die = new Animation( 10, false );
        die.frames( film, offset+11,offset+12,offset+13,offset+14 );

        zap = attack.clone();

        operate = new Animation( 8, false );
        operate.frames( film, offset+2,offset+6,offset+2,offset+6);

        play( idle );
    }


}
