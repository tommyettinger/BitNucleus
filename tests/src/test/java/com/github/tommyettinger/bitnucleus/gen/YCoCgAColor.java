package com.github.tommyettinger.bitnucleus.gen;

/**
 * Bits used: 16 / 16
 * <br>  luma [0..5]
 * <br>  chromOrange [5..10]
 * <br>  chromGreen [10..15]
 * <br>  opaque [15..16]
 */
public final class YCoCgAColor {
  public static byte luma(short ycocgacolor) {
    return (byte)((ycocgacolor >>> 0) & 0b0000000000011111);
  }

  public static short luma(short ycocgacolor, byte value) {
    return (short)((ycocgacolor & 0b0000000000011111) | ((short)value << 0));
  }

  public static byte chromOrange(short ycocgacolor) {
    return (byte)((ycocgacolor >>> 5) & 0b0000000000011111);
  }

  public static short chromOrange(short ycocgacolor, byte value) {
    return (short)((ycocgacolor & 0b0000001111100000) | ((short)value << 5));
  }

  public static byte chromGreen(short ycocgacolor) {
    return (byte)((ycocgacolor >>> 10) & 0b0000000000011111);
  }

  public static short chromGreen(short ycocgacolor, byte value) {
    return (short)((ycocgacolor & 0b0111110000000000) | ((short)value << 10));
  }

  public static boolean opaque(short ycocgacolor) {
    return (ycocgacolor & (1L << 15)) != 0;
  }

  public static short opaque(short ycocgacolor, boolean value) {
    return value ? (short)((ycocgacolor | (1L << 15))) : (short)((ycocgacolor & ~(1L << 15)));
  }

  public static short get(byte luma, byte chromOrange, byte chromGreen, boolean opaque) {
    return (short)(((short)luma << 0) | ((short)chromOrange << 5) | ((short)chromGreen << 10) | (opaque ? 1L << 15 : 0));
  }
}
