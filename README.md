# Yet Another SharedPreference
[ ![Download](https://api.bintray.com/packages/simsun/maven/yasp/images/download.svg) ](https://bintray.com/simsun/maven/yasp/_latestVersion)
[![Build Status](https://travis-ci.org/simpleton/Yasp.svg?branch=master)](https://travis-ci.org/simpleton/Yasp)

This library is wrapped [LevelDB in java](https://github.com/dain/leveldb) with [SharedPreference](https://developer.android.com/reference/android/content/SharedPreferences.html) interface. 

## Background
If you only need to persist simple values and your application runs in a single process SharedPreferences is probably enough for you. It is a good default option.

There are some situations where SharedPreferences are not suitable for store KV data:

1. Performance: Your data is complex or there is a lot of it
2. Multiple thread accessing the data: invoke `editor.appy()` or `editor.commit()` multiple time, even [`apply`](http://aosp.opersys.com/xref/android-8.1.0_r18/xref/frameworks/base/core/java/android/app/SharedPreferencesImpl.java#410) will submit work to other thread.
3. Multiple processes accessing the data: You have widgets or remote services that run in their own processes and require synchronized data

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
**Do NOT support multiple processes**

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

## Reference
1. [Best practices in Android development](https://github.com/futurice/android-best-practices#data-storage)
2. [请不要滥用SharedPreference](http://weishu.me/2016/10/13/sharedpreference-advices/)