package com.simsun.yasp;

import android.content.SharedPreferences;
import android.util.Log;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * YASP Context
 */
public class YASPContext {

  public static final String TAG = "YASPContext";
  private static ConcurrentHashMap<String, YASharedPreferences> dbMap = new ConcurrentHashMap<>();

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
   * @throws IOException
   */
  public static SharedPreferences getSharedPreferences(String name, int mode) throws IOException {
    if (dbMap.containsKey(name)) {
      return dbMap.get(name);
    } else {
      DB db = openDB(name);
      YASharedPreferences sp = new YASharedPreferences(db);
      dbMap.put(name, sp);
      return sp;
    }
  }

  /**
   * Close target shared preference
   * @param name Desired preferences file. If a preferences file by this name
   * does not exist, it will be created.
   * @throws IOException
   */
  public static void close(String name) throws IOException {
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
  public static void closeAll() throws IOException {
    for (Iterator<Map.Entry<String, YASharedPreferences>> it = dbMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<String, YASharedPreferences> entry = it.next();
      entry.getValue().close();
      it.remove();
    }
  }

  /**
   * Open a new level db instance
   * @param name Desired file name
   * @return {@link DB} level db instancen
   * @throws IOException
   */
  private static DB openDB(String name) throws IOException {
    Options options = new Options();
    options.createIfMissing(true);
    return Iq80DBFactory.factory.open(new File(name), options);
  }
}
