package com.simsun.yasp.samples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.simsun.yasp.YASPContext;

public class SingleThreadActivity extends AppCompatActivity {
  public static final String TAG = "SingleThreadActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_single_thread);

    findViewById(R.id.btn_get).setOnClickListener(v -> {
      SharedPreferences sp = YASPContext.with(this).getSharedPreferences("single_thread", 0);
      for (int i = 0; i < 10000; ++i) {
        String value = sp.getString(String.format("Key%d", i), "");
        Log.d(TAG, value);
      }
    });

    findViewById(R.id.btn_put).setOnClickListener(v -> {
      SharedPreferences sp = YASPContext.with(this).getSharedPreferences("single_thread", 0);
      for (int i = 0; i < 10000; ++i) {
        sp.edit().putString(String.format("Key%d", i), String.format("Value #%d", i)).commit();
      }
    });
  }
}
