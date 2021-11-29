package com.zrp200.rkpd2.mechanics;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.mobs.npcs.AbstractMirrorImage;
import com.zrp200.rkpd2.levels.Level;

import java.util.ArrayList;

class PreciseBallistica extends Ballistica {

    private int stepA;
    private int stepB;
    private int dA;
    private int dB;

    private boolean hit;

    public PreciseBallistica(int from, int to, int params) {
        this(from, to, params, Dungeon.level);
    }

    public PreciseBallistica(int from, int to, int params, Level level) {
        super(from, to, params, level);
    }

    @Override
    protected void build(int from, int to, boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid) {
        cast(from, to, stopTarget, stopChars, stopTerrain, ignoreSoftSolid);
    }

    protected void cast(int from, int to, boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid) {

        int w = level.width();

        int x0 = from % w;
        int x1 = to % w;
        int y0 = from / w;
        int y1 = to / w;

        int dx = x1 - x0;
        int dy = y1 - y0;

        int stepX = dx > 0 ? +1 : -1;
        int stepY = dy > 0 ? +1 : -1;

        dx = Math.abs(dx);
        dy = Math.abs(dy);

        if (dx > dy) {

            stepA = stepX;
            stepB = stepY * w;
            dA = dx;
            dB = dy;

        } else {

            stepA = stepY * w;
            stepB = stepX;
            dA = dy;
            dB = dx;

        }

        int cell = calc(from, to, stopTarget, stopChars, stopTerrain, ignoreSoftSolid, dA / 2);

        if (!hit) {
            ArrayList<Integer> oldPath = new ArrayList<>(path);
            for (int err = 0; err <= dA; err++) {
                int calc = calc(from, to, stopTarget, stopChars, stopTerrain, ignoreSoftSolid, err);
                if (hit) {
                    cell = calc;
                    break;
                }
            }
            if(!hit){
                path = oldPath;
            }
        }

        collisionPos = cell;
    }

    private int calc(int from, int to, boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid, int err) {

        hit = false;
        dist = 0;
        path.clear();
        path.add(from);

        boolean collided = false;

        int cell = from;

        while (level.insideMap(cell)) {

            cell += stepA;

            err += dB;

            if (err >= dA) {
                err = err - dA;
                cell = cell + stepB;
            }


            path.add(cell);

            if (!collided) {
                if (stopTerrain && !soft(cell)) {
                    collided = true;
                } else {

                    dist++;

                    if (cell == to) {
                        hit = true;
                        if (stopTarget) {
                            collided = true;
                        }
                    }

                    if ((stopTerrain && level.solid[cell]) && !(ignoreSoftSolid && soft(cell))) {
                        collided = true;
                    } else if (cell != from && stopChars && Actor.findChar(cell) != null) {
                        if (!(Actor.findChar(cell) instanceof AbstractMirrorImage) || !Dungeon.hero.hasTalent(Talent.SPECTRE_ALLIES)) {
                            collided = true;
                        }
                    }
                }
            }
        }

        return path.get(dist);
    }

    private boolean soft(int cell) {
        return level.avoid[cell] || level.passable[cell];
    }
}
