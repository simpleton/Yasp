package com.simsun.yasp.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.crashlytics.android.Crashlytics;
import com.simsun.yasp.samples.R;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> Snackbar.make(
        view,
        "Replace with your own action",
        Snackbar.LENGTH_LONG
    ).setAction("Action", null).show());

    findViewById(R.id.btn_single_thread).setOnClickListener(v -> {
      Intent intent = new Intent(this, SingleThreadActivity.class);
      //this.startActivity(intent);
      Crashlytics.getInstance().crash(); // Force a crash
    });



    findViewById(R.id.btn_mutiple_thread).setOnClickListener(v -> {
      Intent intent = new Intent(this, MultipleThreadActivity.class);
      this.startActivity(intent);
    });

    findViewById(R.id.btn_simple_benchmark).setOnClickListener(v -> {
      Intent intent = new Intent(this, BenchmarkActivity.class);
      this.startActivity(intent);
    });
  }
}
