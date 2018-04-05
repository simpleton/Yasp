package com.simsun.common.base;

public class Utils {
  public static <T> T requireNonNull(T obj) {
    if (obj == null) throw new NullPointerException();
    return obj;
  }

  public static <T> T requireNonNull(T obj, String msg) {
    if (obj == null) throw new NullPointerException(msg);
    return obj;
  }

  /**
   * Compares two {@code long} values numerically.
   * The value returned is identical to what would be returned by:
   * <pre>
   *    Long.valueOf(x).compareTo(Long.valueOf(y))
   * </pre>
   *
   * @param  x the first {@code long} to compare
   * @param  y the second {@code long} to compare
   * @return the value {@code 0} if {@code x == y};
   *         a value less than {@code 0} if {@code x < y}; and
   *         a value greater than {@code 0} if {@code x > y}
   * @since 1.7
   */
  public static int compare(long x, long y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  public static int compare(byte[] left, byte[] right) {
    for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
      int a = (left[i] & 0xff);
      int b = (right[j] & 0xff);
      if (a != b) {
        return a - b;
      }
    }
    return left.length - right.length;
  }
}
