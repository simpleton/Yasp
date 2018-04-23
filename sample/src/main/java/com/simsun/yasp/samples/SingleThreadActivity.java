package com.simsun.yasp.samples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.simsun.yasp.leveldb.YASPContext;
import java.util.HashSet;
import java.util.Set;

public class SingleThreadActivity extends AppCompatActivity {
  public static final String TAG = "SingleThreadActivity";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_single_thread);
    final Set<String> strSet = new HashSet<>();
    strSet.add("hello");
    strSet.add("world");
    strSet.add("this");
    strSet.add("is");
    strSet.add("a");
    strSet.add("just so so");
    strSet.add("sp implementation");

    findViewById(R.id.btn_get).setOnClickListener(v -> {
      SharedPreferences sp = YASPContext.with(this).getSharedPreferences("single_thread", 0);
      for (int i = 0; i < 1000; ++i) {
        Set<String> values = sp.getStringSet(String.format("KeyNew%s", i), new HashSet<>());
        StringBuilder sb = new StringBuilder();
        for (String str : values) {
          sb.append(str).append(" ");
        }
        Log.d(TAG, sb.toString());
      }
    });

    findViewById(R.id.btn_put).setOnClickListener(v -> {
      SharedPreferences sp = YASPContext.with(this).getSharedPreferences("single_thread", 0);
      for (int i = 0; i < 1000; ++i) {
        sp.edit().putStringSet(String.format("KeyNew%s", i), strSet).apply();
      }
    });
  }
}
