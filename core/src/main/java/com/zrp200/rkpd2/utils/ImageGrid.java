package com.zrp200.rkpd2.utils;

public interface ImageGrid {

    default int gridWidth(){
        return 16;
    }

    default int imageAt(int x, int y){
        return x + y * gridWidth();
    }

}
