package com.github.tommyettinger.bitnucleus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations{

    /** Marks a class as a special value type struct. Class name must end in 'Struct'.*/
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Struct{

    }

    /**Marks a field of a struct. Optional.*/
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface StructField{
        /**Size of a struct field in bits. Not valid on booleans or floating point numbers.*/
        int value();
    }
}
