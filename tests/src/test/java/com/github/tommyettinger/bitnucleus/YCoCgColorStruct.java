package com.github.tommyettinger.bitnucleus;

import com.github.tommyettinger.bitnucleus.annotations.Annotations.Struct;
import com.github.tommyettinger.bitnucleus.annotations.Annotations.StructField;

/**
 * Created by Tommy Ettinger on 2/13/2019.
 */
@Struct
public class YCoCgColorStruct {
    @StructField(6)
    public byte luma;
    @StructField(5)
    public byte chromOrange;
    @StructField(5)
    public byte chromGreen;
    
    public YCoCgColorStruct()
    {
        luma = 63;
        chromOrange = 16;
        chromGreen = 16;
    }
    public YCoCgColorStruct(byte y, byte co, byte cg)
    {
        luma = y;
        chromOrange = co;
        chromGreen = cg;
    }
}
