package com.github.tommyettinger.bitnucleus.annotations;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Utils{
    public static Types typeUtils;
    public static Elements elementUtils;
    public static Filer filer;
    public static Messager messager;

    public static String getMethodName(Element element){
        return ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString() + "." + element.getSimpleName();
    }

    public static boolean isPrimitive(String type){
        return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
                || type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
    }
    /**
     * Return the least power of two greater than or equal to the specified value.
     * <br>Note that this function will return 0 when the argument is 0.
     *
     * @param x an integer smaller than or equal to 2<sup>30</sup>.
     * @return the least power of two greater than or equal to the specified value.
     */
    public static int nextPowerOfTwo(int x) {
        if (x == 0) return 0;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return (x | x >> 16) + 1;
    }
    
    public static int bitSize(int n) {
        if(n == 0) return 0;
        return 32 - Integer.numberOfLeadingZeros(n - 1);
    }

}
