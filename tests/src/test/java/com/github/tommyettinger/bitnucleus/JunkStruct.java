package com.github.tommyettinger.bitnucleus;

import com.github.tommyettinger.bitnucleus.annotations.Annotations.Struct;
import com.github.tommyettinger.bitnucleus.annotations.Annotations.StructField;

/**
 * Created by Tommy Ettinger on 2/19/2019.
 */
@Struct
public class JunkStruct {
    @StructField(2)
    public JunkEnum response;
    @StructField(6)
    public int count;
}
