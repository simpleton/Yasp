package com.simsun.yasp;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashSet;
import java.util.Set;

public class ParcelableUtil {
  /**
   * Marshall parceable object to byte[]
   *
   * @param parceable the object which implement Parcelable
   * @return marshalled byte array
   */
  public static byte[] marshall(Parcelable parceable) {
    Parcel parcel = null;
    try {
      parcel = Parcel.obtain();
      parceable.writeToParcel(parcel, 0);
      return parcel.marshall();
    } finally {
      if (parcel != null) {
        parcel.recycle();
      }
    }
  }

  /**
   * Unmarshall bytes to parceable object
   *
   * @param bytes input byte array
   * @param creator Parceable factory creator
   * @param <T> Custom type
   * @return parceable object
   */
  public static <T extends Parcelable> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
    Parcel parcel = unmarshall(bytes);
    return creator.createFromParcel(parcel);
  }

  private static Parcel unmarshall(byte[] bytes) {
    Parcel parcel = Parcel.obtain();
    parcel.unmarshall(bytes, 0, bytes.length);
    parcel.setDataPosition(0); // this is extremely important!
    return parcel;
  }

  /**
   * marshall string set to byte[]
   */
  public static byte[] marshallStringSet(Set<String> strSet) {
    Parcel parcel = null;
    try {
      parcel = Parcel.obtain();
      parcel.writeStringArray(strSet.toArray(new String[strSet.size()]));
      return parcel.marshall();
    } finally {
      if (parcel != null) {
        parcel.recycle();
      }
    }
  }

  /**
   * Unmarshall byte[] to string set
   */
  public static Set<String> unMarshallStringSet(byte[] bytes) {
    Parcel parcel = null;
    try {
      parcel = Parcel.obtain();
      parcel.unmarshall(bytes, 0, bytes.length);
      return new HashSet<>(parcel.createStringArrayList());
    } finally {
      if (parcel != null) {
        parcel.recycle();
      }
    }
  }
}