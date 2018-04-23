package com.simsun.yasp.leveldb;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

/**
 * YASP Context
 */
public class YASPContext {

  public static final String TAG = "YASPContext";
  private static final ConcurrentHashMap<String, YASharedPreferences> dbMap = new ConcurrentHashMap<>();

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
    synchronized (dbMap) {
      if (dbMap.containsKey(name)) {
        return dbMap.get(name);
      } else {
        DB db = openDB(this.baseDir, name);
        YASharedPreferences sp = new YASharedPreferences(db);
        dbMap.put(name, sp);
        return sp;
      }
    }
  }

  /**
   * Close target shared preference
   * @param name Desired preferences file. If a preferences file by this name
   * does not exist, it will be created.
   * @throws IOException
   */
  public void close(String name) throws IOException {
    if (dbMap.containsKey(name)) {
      dbMap.get(name).close();
      dbMap.remove(name);
    } else {
      Log.e(TAG, String.format("DB(%s) doesn't exist", name));
    }
  }

  /**
   * Close all cached instance
   * @throws IOException
   */
  public void closeAll() throws IOException {
    for (Iterator<Map.Entry<String, YASharedPreferences>> it = dbMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<String, YASharedPreferences> entry = it.next();
      entry.getValue().close();
      it.remove();
    }
  }

  /**
   * Open a new level db instance
   * @param dir Desired file directory
   * @param name Desired file name
   * @return {@link DB} level db instance
   */
  private static DB openDB(File dir, String name) {
    Options options = new Options();
    options.createIfMissing(true);
    return Iq80DBFactory.factory.open(new File(dir, name), options);
  }
}
