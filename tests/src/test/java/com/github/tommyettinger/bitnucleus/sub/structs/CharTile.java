package com.github.tommyettinger.bitnucleus.sub.structs;

/**
 * Bits used: 64 / 64
 * <br>  symbol [0..16]
 * <br>  backY [16..24]
 * <br>  backCo [24..32]
 * <br>  backCg [32..40]
 * <br>  frontY [40..48]
 * <br>  frontCo [48..56]
 * <br>  frontCg [56..64]
 */
public final class CharTile {
  public static char symbol(long chartile) {
    return (char)(chartile & 0b0000000000000000000000000000000000000000000000001111111111111111L);
  }

  public static long symbol(long chartile, char value) {
    return (long)((chartile & 0b0000000000000000000000000000000000000000000000001111111111111111L) | ((long)value));
  }

  public static int backY(long chartile) {
    return (int)((chartile >>> 16) & 0b0000000000000000000000000000000000000000000000000000000011111111L);
  }

  public static long backY(long chartile, int value) {
    return (long)((chartile & 0b0000000000000000000000000000000000000000111111110000000000000000L) | ((long)value << 16));
  }

  public static int backCo(long chartile) {
    return (int)((chartile >>> 24) & 0b0000000000000000000000000000000000000000000000000000000011111111L);
  }

  public static long backCo(long chartile, int value) {
    return (long)((chartile & 0b0000000000000000000000000000000011111111000000000000000000000000L) | ((long)value << 24));
  }

  public static int backCg(long chartile) {
    return (int)((chartile >>> 32) & 0b0000000000000000000000000000000000000000000000000000000011111111L);
  }

  public static long backCg(long chartile, int value) {
    return (long)((chartile & 0b0000000000000000000000001111111100000000000000000000000000000000L) | ((long)value << 32));
  }

  public static int frontY(long chartile) {
    return (int)((chartile >>> 40) & 0b0000000000000000000000000000000000000000000000000000000011111111L);
  }

  public static long frontY(long chartile, int value) {
    return (long)((chartile & 0b0000000000000000111111110000000000000000000000000000000000000000L) | ((long)value << 40));
  }

  public static int frontCo(long chartile) {
    return (int)((chartile >>> 48) & 0b0000000000000000000000000000000000000000000000000000000011111111L);
  }

  public static long frontCo(long chartile, int value) {
    return (long)((chartile & 0b0000000011111111000000000000000000000000000000000000000000000000L) | ((long)value << 48));
  }

  public static int frontCg(long chartile) {
    return (int)((chartile >>> 56) & 0b0000000000000000000000000000000000000000000000000000000011111111L);
  }

  public static long frontCg(long chartile, int value) {
    return (long)((chartile & 0b1111111100000000000000000000000000000000000000000000000000000000L) | ((long)value << 56));
  }

  public static long get(char symbol, int backY, int backCo, int backCg, int frontY, int frontCo,
      int frontCg) {
    return (long)(((long)symbol) | ((long)backY << 16) | ((long)backCo << 24) | ((long)backCg << 32) | ((long)frontY << 40) | ((long)frontCo << 48) | ((long)frontCg << 56));
  }
}
