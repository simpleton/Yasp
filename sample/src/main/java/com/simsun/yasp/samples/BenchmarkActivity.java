package com.simsun.yasp.samples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.simsun.yasp.YASPContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import hugo.weaving.DebugLog;

public class BenchmarkActivity extends AppCompatActivity {
  public static final String TAG = "BenchmarkActivity";
  private static int TEST_THRESHOLD = 10000;
  private HashMap<String, String> testData = new HashMap<>();
  private TEST_TYPE currentType = TEST_TYPE.ORIGINAL_SP;

  private static String randString(int length) {
    return UUID.randomUUID().toString().replace("-", "").substring(0, Math.min(length, 32)) + (
        length > 32 ? randString(length - 32) : "");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_benchmark);
    TextView console = findViewById(R.id.console);

    RadioGroup radioGroup = findViewById(R.id.radio_group);
    radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
      switch (checkedId) {
        case R.id.radio_normal_sp:
          currentType = TEST_TYPE.ORIGINAL_SP;
          break;
        case R.id.radio_yasp:
          currentType = TEST_TYPE.YASP;
          break;
      }
    });

    findViewById(R.id.btn_prepare_data).setOnClickListener(v -> {
      for (int i = 0; i < TEST_THRESHOLD; ++i) {
        testData.put(
            String.format("Key:%d", i),
            String.format("#%d: content is %s", i, randString(32))
        );
      }
    });

    findViewById(R.id.btn_put).setOnClickListener(v -> {
      handleInsert(console);
    });

    findViewById(R.id.btn_get).setOnClickListener(v -> {
      handleGet(console);
    });
  }

  @DebugLog
  private void handleGet(TextView console) {
    SharedPreferences sp = null;
    long startTime = System.currentTimeMillis();
    long endTime;
    console.setText(String.format("Start getting...%s\n", startTime));
    switch (this.currentType) {
      case YASP:
        sp = YASPContext.with(this).getSharedPreferences("benchmark", MODE_PRIVATE);
        break;
      case ORIGINAL_SP:
        sp = this.getSharedPreferences("benchmark", MODE_PRIVATE);
        break;
    }
    for (int i = 0; i < TEST_THRESHOLD; ++i) {
      String value = sp.getString(String.format("Key:%d", i), "");
      Log.d(TAG, value);
    }
    endTime = System.currentTimeMillis();
    console.append(String.format("End getting...%s. Delta: %s ms\n", endTime, endTime - startTime));
  }

  @DebugLog
  private void handleInsert(TextView console) {
    SharedPreferences sp = null;
    long startTime = System.currentTimeMillis();
    long endTime;
    console.setText(String.format("Start writing ...%s\n", System.currentTimeMillis()));
    switch (this.currentType) {
      case YASP:
        sp = YASPContext.with(this).getSharedPreferences("benchmark", MODE_PRIVATE);
        break;
      case ORIGINAL_SP:
        sp = this.getSharedPreferences("benchmark", MODE_PRIVATE);
        break;
    }

    for (Map.Entry<String, String> entry : testData.entrySet()) {
      sp.edit().putString(entry.getKey(), entry.getValue()).apply();
    }
    endTime = System.currentTimeMillis();
    console.append(String.format("End writing...%s. Delta: %s ms\n", endTime, endTime - startTime));
  }

  public enum TEST_TYPE {
    ORIGINAL_SP, YASP;
  }
}
