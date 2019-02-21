package com.github.tommyettinger.bitnucleus.structs;

import com.github.tommyettinger.bitnucleus.JunkEnum;

/**
 * Bits used: 8 / 8
 * <br>  response [0..2]
 * <br>  count [2..8]
 */
public final class Junk {
  private static final JunkEnum[] $VALUES$response = JunkEnum.values();

  public static JunkEnum response(byte junk) {
    return $VALUES$response[(int)(junk & 0b00000011)];
  }

  public static byte response(byte junk, JunkEnum value) {
    return (byte)((junk & 0b00000011) | ((byte)value.ordinal()));
  }

  public static int count(byte junk) {
    return (int)((junk >>> 2) & 0b00111111);
  }

  public static byte count(byte junk, int value) {
    return (byte)((junk & 0b11111100) | ((byte)value << 2));
  }

  public static byte get(JunkEnum response, int count) {
    return (byte)(((byte)response.ordinal()) | ((byte)count << 2));
  }
}
