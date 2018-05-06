package com.simsun.lmdb;

public class LmdbJni {
  static {
    System.loadLibrary("liblmdb-jni");
  }

  public native String getArch();
  public native String open();
  public native String close();
}
