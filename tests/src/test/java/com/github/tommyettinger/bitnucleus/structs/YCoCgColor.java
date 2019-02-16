package com.github.tommyettinger.bitnucleus.structs;

/**
 * Bits used: 16 / 16
 * <br>  luma [0..6]
 * <br>  chromOrange [6..11]
 * <br>  chromGreen [11..16]
 */
public final class YCoCgColor {
  public static byte luma(short ycocgcolor) {
    return (byte)(ycocgcolor & 0b0000000000111111);
  }

  public static short luma(short ycocgcolor, byte value) {
    return (short)((ycocgcolor & 0b0000000000111111) | ((short)value));
  }

  public static byte chromOrange(short ycocgcolor) {
    return (byte)((ycocgcolor >>> 6) & 0b0000000000011111);
  }

  public static short chromOrange(short ycocgcolor, byte value) {
    return (short)((ycocgcolor & 0b0000011111000000) | ((short)value << 6));
  }

  public static byte chromGreen(short ycocgcolor) {
    return (byte)((ycocgcolor >>> 11) & 0b0000000000011111);
  }

  public static short chromGreen(short ycocgcolor, byte value) {
    return (short)((ycocgcolor & 0b1111100000000000) | ((short)value << 11));
  }

  public static short get(byte luma, byte chromOrange, byte chromGreen) {
    return (short)(((short)luma) | ((short)chromOrange << 6) | ((short)chromGreen << 11));
  }
}
