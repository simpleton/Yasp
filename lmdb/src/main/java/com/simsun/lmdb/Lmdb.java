package com.simsun.lmdb;

import android.util.Log;
import java.io.Closeable;
import java.io.IOException;

public class Lmdb implements Closeable {
  public static final String TAG = "LMDB";

  public void open() {
    LmdbJni jni = new LmdbJni();
    Log.d(TAG, jni.getArch());
  }

  @Override
  public void close() throws IOException {

  }
}
