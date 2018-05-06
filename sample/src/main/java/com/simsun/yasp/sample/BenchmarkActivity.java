package com.simsun.yasp.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.firebase.perf.metrics.AddTrace;
import com.simsun.yasp.leveldb.YASPContext;
import com.simsun.yasp.samples.R;
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
  public static int TEST_THRESHOLD = 1024;
  public static int TEST_SINGLE_THRESHOLD = 128;
  public static int THREAD_NUM = 16;
  public static int STRING_LENGTH = 2048;

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
            String.format("#%s: content is %s", i, randString(STRING_LENGTH))
        );
      }
      console.setText("Data Ready");
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
  @AddTrace(name = "handleGet", enabled = true)
  private void handleGet(TextView console) {
    Handler mainHandler = new Handler(getMainLooper());
    long startTime = System.currentTimeMillis();
    console.setText(String.format("Start getting...%s\n", startTime));
    if (enableMultipleThread) {
      for (int i = 0; i < THREAD_NUM; ++i) {
        threadPoolExecutor.execute(new WorkingThread(i) {
          @Override
          public void run() {
            final SharedPreferences sp = createSP(String.format("T %s", this.input));
            for (int j = 0; j < BenchmarkActivity.TEST_SINGLE_THRESHOLD; ++j) {
              String value = sp.getString(String.format(
                  "MKey%s",
                  this.input * BenchmarkActivity.TEST_SINGLE_THRESHOLD + j
              ), "");
              //Log.d(TAG, String.format("%s", value));
            }
            final long endTime = System.currentTimeMillis();
            mainHandler.post(() -> console.append(String.format("End writing...%s. Delta: %s ms\n",
                endTime,
                endTime - startTime
            )));
          }
        });
      }
    } else {
      final SharedPreferences sp = createSP();
      for (int i = 0; i < TEST_THRESHOLD; ++i) {
        sp.getString(String.format("Key:%s", i), "");
        //Log.d(TAG, value);
      }
      final long endTime = System.currentTimeMillis();
      console.append(String.format("End getting...%s. Delta: %s ms\n",
          endTime,
          endTime - startTime
      ));
    }
  }

  @DebugLog
  @AddTrace(name = "handleInsert", enabled = true)
  private void handleInsert(TextView console) {
    Handler mainHandler = new Handler(getMainLooper());
    long startTime = System.currentTimeMillis();
    console.setText(String.format("Start writing ...%s\n", System.currentTimeMillis()));
    if (enableMultipleThread) {
      for (int i = 0; i < THREAD_NUM; ++i) {
        threadPoolExecutor.execute(new WorkingThread(i) {
          @Override
          public void run() {
            final SharedPreferences sp = createSP(String.format("T %s", this.input));
            for (int j = 0; j < BenchmarkActivity.TEST_SINGLE_THRESHOLD; ++j) {
              sp.edit().putString(String.format(
                  "MKey%s",
                  this.input * BenchmarkActivity.TEST_SINGLE_THRESHOLD + j
              ), "").apply();
            }
            long endTime = System.currentTimeMillis();
            mainHandler.post(() -> console.append(String.format("End writing...%s. Delta: %s ms\n",
                endTime,
                endTime - startTime
            )));
          }
        });
      }
    } else {
      final SharedPreferences sp = createSP();
      for (Map.Entry<String, String> entry : testData.entrySet()) {
        sp.edit().putString(entry.getKey(), entry.getValue()).apply();
      }
      long endTime = System.currentTimeMillis();
      console.append(String.format("End writing...%s. Delta: %s ms\n",
          endTime,
          endTime - startTime
      ));
    }
  }

  SharedPreferences createSP() {
    return createSP("");
  }

  SharedPreferences createSP(String name) {
    SharedPreferences sp = null;
    switch (this.currentType) {
      case YASP:
        sp = YASPContext.with(this).getSharedPreferences("benchmark" + name, MODE_PRIVATE);
        break;
      case ORIGINAL_SP:
        sp = this.getSharedPreferences("benchmark" + name, MODE_PRIVATE);
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
