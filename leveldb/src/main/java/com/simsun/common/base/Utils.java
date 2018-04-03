package com.simsun.common.base;

public class Utils {
  public static <T> T requireNonNull(T obj) {
    if (obj == null)
      throw new NullPointerException();
    return obj;
  }

  public static <T> T requireNonNull(T obj, String msg) {
    if (obj == null)
      throw new NullPointerException(msg);
    return obj;
  }
}
