package com.simsun.yasp.lmdb;

import android.content.Context;
import android.content.SharedPreferences;
import com.simsun.lmdb.Lmdb;
import java.io.File;

public class YASPContext {
  public static final String TAG = "YASPContext.lmdb";

  private final File baseDir;

  public static YASPContext with(Context context) {
    return new YASPContext(context.getFilesDir());
  }

  private YASPContext(File baseDir) {
    this.baseDir = baseDir;
  }

  /**
   * Retrieve and hold the contents of the preferences file 'name', returning
   * a SharedPreferences through which you can retrieve and modify its
   * values.
   * @param name Desired preferences file.
   * @param mode Operating mode. **NOT IMPLEMENTED**
   *
   * @return The single {@link SharedPreferences} instance that can be used
   *         to retrieve and modify the preference values.
   *
   */
  public SharedPreferences getSharedPreferences(String name, int mode) {
    Lmdb lmdb = new Lmdb();
    lmdb.open();
    return null;
  }
}
