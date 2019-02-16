package com.github.tommyettinger.bitnucleus.sub;

import com.github.tommyettinger.bitnucleus.annotations.Annotations.Struct;
import com.github.tommyettinger.bitnucleus.annotations.Annotations.StructField;

/**
 * Created by Tommy Ettinger on 2/15/2019.
 */
@Struct 
class CharTileStruct {
    @StructField(16)
    public char symbol;

    @StructField(8)
    public int backY;
    @StructField(8)
    public int backCo;
    @StructField(8)
    public int backCg;
    @StructField(8)
    public int frontY;
    @StructField(8)
    public int frontCo;
    @StructField(8)
    public int frontCg;
}
