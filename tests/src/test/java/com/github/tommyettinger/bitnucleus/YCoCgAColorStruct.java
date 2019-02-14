package com.github.tommyettinger.bitnucleus;

import com.github.tommyettinger.bitnucleus.annotations.Annotations.Struct;
import com.github.tommyettinger.bitnucleus.annotations.Annotations.StructField;

/**
 * Created by Tommy Ettinger on 2/13/2019.
 */
@Struct
public class YCoCgAColorStruct {
    @StructField(5)
    public byte luma;
    @StructField(5)
    public byte chromOrange;
    @StructField(5)
    public byte chromGreen;
    @StructField(1)
    public boolean opaque;
    
    public YCoCgAColorStruct()
    {
        luma = 31;
        chromOrange = 16;
        chromGreen = 16;
        opaque = true;
    }
    public YCoCgAColorStruct(byte y, byte co, byte cg, boolean opaque)
    {
        luma = y;
        chromOrange = co;
        chromGreen = cg;
        this.opaque = opaque;
    }
}
