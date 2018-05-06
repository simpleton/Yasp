package com.simsun.lmdb;

final class Preconditions {
  public static void checkErrCode(int retCode) {
    if (retCode != 0) {
      throw new LmdbException(retCode);
    }
  }

  public static void checkArgNotNull(Object value, String name) {
    if (value == null) {
      throw new IllegalArgumentException("The " + name + " argument cannot be null");
    }
  }
}
