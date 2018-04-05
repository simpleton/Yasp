package com.simsun.yasp;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.util.Closeables;

import static org.iq80.leveldb.impl.Iq80DBFactory.asBoolean;
import static org.iq80.leveldb.impl.Iq80DBFactory.asFloat;
import static org.iq80.leveldb.impl.Iq80DBFactory.asInt;
import static org.iq80.leveldb.impl.Iq80DBFactory.asLong;
import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

public class YASharedPreferences implements SharedPreferences {

  public static final String TAG = "YASharedPreferences";

  final DB db;

  public YASharedPreferences(DB db) {
    this.db = db;
  }

  public void close() throws IOException {
    db.close();
  }

  @Override
  public Map<String, ?> getAll() {
    return null;
  }

  @Nullable
  @Override
  public String getString(String key, @Nullable String defValue) {
    String value = defValue;
    try {
      value = asString(db.get(bytes(key)));
    } catch (DBException e) {
      Log.e(TAG, "DB Exception", e);
    }
    return value;
  }

  @Nullable
  @Override
  public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
    return null;
  }

  @Override
  public int getInt(String key, int defValue) {
    int value = defValue;
    try {
      value = asInt(db.get(bytes(key)));
    } catch (DBException e) {
      Log.e(TAG, "DB Exception", e);
    }
    return value;
  }

  @Override
  public long getLong(String key, long defValue) {
    long value = defValue;
    try {
      value = asLong(db.get(bytes(key)));
    } catch (DBException e) {
      Log.e(TAG, "DB Exception", e);
    }
    return value;
  }

  @Override
  public float getFloat(String key, float defValue) {
    float value = defValue;
    try {
      value = asFloat(db.get(bytes(key)));
    } catch (DBException e) {
      Log.e(TAG, "DB Exception", e);
    }
    return value;
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    boolean value = defValue;
    try {
      value = asBoolean(db.get(bytes(key)));
    } catch (DBException e) {
      Log.e(TAG, "DB Exception", e);
    }
    return value;
  }

  @Override
  public boolean contains(String key) {
    try {
      byte[] value = db.get(bytes(key));
      return value.length > 0;
    } catch (DBException e) {
      return false;
    }
  }

  @Override
  public Editor edit() {
    return new Editor();
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    //FIXME:
  }

  @Override
  public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    //FIXME:
  }

  public class Editor implements SharedPreferences.Editor {

    final WriteBatch batch;

    Editor() {
      batch = db.createWriteBatch();
    }

    @Override
    public SharedPreferences.Editor putString(String key, @Nullable String value) {
      batch.put(bytes(key), bytes(value));
      return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
      return null;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
      batch.put(bytes(key), bytes(value));
      return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
      batch.put(bytes(key), bytes(value));
      return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
      batch.put(bytes(key), bytes(value));
      return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
      batch.put(bytes(key), bytes(value));
      return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
      batch.delete(bytes(key));
      return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
      Log.e(TAG, "NO IMPLEMENTATION");
      return this;
    }

    @Override
    public boolean commit() {
      try {
        db.write(batch);
        return true;
      } catch (DBException ignore) {
        return false;
      } finally {
        Closeables.closeQuietly(batch);
      }
    }

    @Override
    public void apply() {
      try {
        db.write(batch);
      } catch (DBException e) {
        Log.e(TAG, "", e);
      } finally {
        Closeables.closeQuietly(batch);
      }
    }
  }
}
