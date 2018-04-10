package com.simsun.yasp.samples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.simsun.yasp.YASPContext;
import hugo.weaving.DebugLog;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BenchmarkActivity extends AppCompatActivity {
  public static final String TAG = "BenchmarkActivity";
  public static int TEST_THRESHOLD = 10000;
  public static int TEST_SINGLE_THRESHOLD = 100;
  private HashMap<String, String> testData = new HashMap<>();
  private TEST_TYPE currentType = TEST_TYPE.ORIGINAL_SP;
  private boolean enableMultipleThread;
  private ThreadPoolExecutor threadPoolExecutor;

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
    SwitchCompat switchCompat = findViewById(R.id.switch_multi_thread);
    switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
      this.enableMultipleThread = isChecked;
    });

    findViewById(R.id.btn_prepare_data).setOnClickListener(v -> {
      for (int i = 0; i < TEST_THRESHOLD; ++i) {
        testData.put(
            String.format("Key:%s", i),
            String.format("#%s: content is %s", i, randString(32))
        );
      }
    });

    findViewById(R.id.btn_put).setOnClickListener(v -> handleInsert(console));
    findViewById(R.id.btn_get).setOnClickListener(v -> handleGet(console));

    threadPoolExecutor = new ThreadPoolExecutor(16,
        16,
        10,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>()
    );
  }

  @Override
  protected void onResume() {
    super.onResume();
    enableDetailedLogcat();
  }

  private void enableDetailedLogcat() {
    int pid = android.os.Process.myPid();
    String whiteList = "logcat -P '" + pid + "'";
    try {
      Runtime.getRuntime().exec(whiteList).waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @DebugLog
  private void handleGet(TextView console) {
    long startTime = System.currentTimeMillis();
    long endTime;
    final SharedPreferences sp = createSP();
    if (enableMultipleThread) {
      for (int i = 0; i < 50; ++i) {
        threadPoolExecutor.execute(new WorkingThread(i) {
          @Override
          public void run() {
            for (int j = 0; j < BenchmarkActivity.TEST_SINGLE_THRESHOLD; ++j) {
              String value = sp.getString(String.format(
                  "MKey%s",
                  this.input * BenchmarkActivity.TEST_SINGLE_THRESHOLD + j
              ), "");
              Log.d(TAG, String.format("%s", value));
            }
          }
        });

      }
    } else {
      console.setText(String.format("Start getting...%s\n", startTime));
      for (int i = 0; i < TEST_THRESHOLD; ++i) {
        String value = sp.getString(String.format("Key:%s", i), "");
        Log.d(TAG, value);
      }
      endTime = System.currentTimeMillis();
      console.append(String.format("End getting...%s. Delta: %s ms\n",
          endTime,
          endTime - startTime
      ));
    }
  }

  @DebugLog
  private void handleInsert(TextView console) {
    long startTime = System.currentTimeMillis();
    long endTime;
    final SharedPreferences sp = createSP();
    if (enableMultipleThread) {
      for (int i = 0; i < 50; ++i) {
        threadPoolExecutor.execute(new WorkingThread(i) {
          @Override
          public void run() {
            for (int j = 0; j < BenchmarkActivity.TEST_SINGLE_THRESHOLD; ++j) {
              sp.edit().putString(String.format(
                  "MKey%s",
                  this.input * BenchmarkActivity.TEST_SINGLE_THRESHOLD + j
              ), "").apply();
            }
          }
        });
      }
    } else {
      console.setText(String.format("Start writing ...%s\n", System.currentTimeMillis()));
      for (Map.Entry<String, String> entry : testData.entrySet()) {
        sp.edit().putString(entry.getKey(), entry.getValue()).apply();
      }
      endTime = System.currentTimeMillis();
      console.append(String.format("End writing...%s. Delta: %s ms\n",
          endTime,
          endTime - startTime
      ));
    }
  }

  private SharedPreferences createSP() {
    SharedPreferences sp = null;
    switch (this.currentType) {
      case YASP:
        sp = YASPContext.with(this).getSharedPreferences("benchmark", MODE_PRIVATE);
        break;
      case ORIGINAL_SP:
        sp = this.getSharedPreferences("benchmark", MODE_PRIVATE);
        break;
    }
    return sp;
  }

  public enum TEST_TYPE {
    ORIGINAL_SP, YASP;
  }

  public static abstract class WorkingThread implements Runnable {
    int input;

    public WorkingThread(int input) {
      this.input = input;
    }
  }
}
