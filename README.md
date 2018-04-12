# Yet Another SharedPreference
[ ![Download](https://api.bintray.com/packages/simsun/maven/yasp/images/download.svg) ](https://bintray.com/simsun/maven/yasp/_latestVersion)
[![Build Status](https://travis-ci.org/simpleton/Yasp.svg?branch=master)](https://travis-ci.org/simpleton/Yasp)

This library is wrapped [LevelDB in java](https://github.com/dain/leveldb) with [SharedPreference](https://developer.android.com/reference/android/content/SharedPreferences.html) interface. 

## Download
**Use Gradle:**
```gradle
implementation 'com.simsun.yasp:yasp:0.0.1'
```

**Or Maven:**
```xml
<dependency>
  <groupId>com.simsun.yasp</groupId>
  <artifactId>yasp</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```

## API Usage
Almost compat with Android [SharedPreference](https://developer.android.com/reference/android/content/SharedPreferences.html), but there is a little different during initializing. 
### Init
```java
SharedPreferences sp = YASPContext.with(Context ct).getSharedPreferences(String name, int mode);
```

### Get
As same as Android [SharedPreference](https://developer.android.com/reference/android/content/SharedPreferences.html)
```java
sp.getString(String key, String defaultVal);
```

### Editor
As same as Android [SharedPreference](https://developer.android.com/reference/android/content/SharedPreferences.html)
```java
sp.edit()
  .putString(String key, String value)
  .putInt(String key, int value)
  .apply();
```

## Benchmark
// TODO
