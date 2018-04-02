package com.simsun.yasp;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import org.iq80.leveldb.DB;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class YASharedPreferences implements SharedPreferences {

  private final DB db;
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
    return null;
  }

  @Nullable
  @Override
  public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
    return null;
  }

  @Override
  public int getInt(String key, int defValue) {
    return 0;
  }

  @Override
  public long getLong(String key, long defValue) {
    return 0;
  }

  @Override
  public float getFloat(String key, float defValue) {
    return 0;
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return false;
  }

  @Override
  public boolean contains(String key) {
    return false;
  }

  @Override
  public Editor edit() {
    return null;
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

  }

  @Override
  public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

  }

  public class Editor implements SharedPreferences.Editor {

    @Override
    public SharedPreferences.Editor putString(String key, @Nullable String value) {
      return null;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
      return null;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
      return null;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
      return null;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
      return null;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
      return null;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
      return null;
    }

    @Override
    public SharedPreferences.Editor clear() {
      return null;
    }

    @Override
    public boolean commit() {
      return false;
    }

    @Override
    public void apply() {

    }
  }
}
