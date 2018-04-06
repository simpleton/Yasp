package com.simsun.yasp.samples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.simsun.yasp.YASPContext;
import com.simsun.yasp.YASharedPreferences;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    TextView textView = findViewById(R.id.content);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> Snackbar.make(
        view,
        "Replace with your own action",
        Snackbar.LENGTH_LONG
    ).setAction("Action", null).show());
    File file = new File(this.getFilesDir(), "tmp");
    findViewById(R.id.get_btn).setOnClickListener(v -> {
      try {

        SharedPreferences sp = YASPContext.getSharedPreferences(file.getAbsolutePath(), 0);
        sp.edit().putInt("int", 1).commit();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    findViewById(R.id.put_btn).setOnClickListener(v -> {
      try {
        SharedPreferences sp = YASPContext.getSharedPreferences(file.getAbsolutePath(), 0);
        int value = sp.getInt("int", 0);
        textView.setText(String.format("%s", value));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}
