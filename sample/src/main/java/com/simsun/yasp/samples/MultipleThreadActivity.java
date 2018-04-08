package com.simsun.yasp.samples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.simsun.yasp.YASPContext;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultipleThreadActivity extends AppCompatActivity {
  public static final String TAG = "MultipleThreadActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mutiple_thread);
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16,
        16,
        10,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>()
    );

    findViewById(R.id.btn_get).setOnClickListener(v -> {
      threadPoolExecutor.execute(() -> {
        SharedPreferences sp = YASPContext.with(this).getSharedPreferences("multi_thread", 0);
        for (int i = 0; i < 100; ++i) {
          String value = sp.getString(String.format("Key%d", i), "");
          Log.d(TAG, String.format("%s", value));
        }
      });
    });

    findViewById(R.id.btn_put).setOnClickListener(v -> {
      threadPoolExecutor.execute(() -> {
        SharedPreferences sp = YASPContext.with(this).getSharedPreferences("multi_thread", 0);
        for (int i = 0; i < 100; ++i) {
          sp.edit().putString(String.format("Key%d", i), String.format("Value #%d", i)).commit();
        }
      });
    });
  }
}
