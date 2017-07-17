package com.example.foolish_guy.nds;

import android.net.nsd.NsdServiceInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NDSHELPER ndshelper;
    Button btn;
    AppCompatTextView result_tv;
    ProgressBar progressBar;
    ListView listView;
    ListViewAdapter adapter;

    final Long COUNT_DOWN_LIMIT = 20000L;
    final Long COUNT_DOWN_INCREMENT = 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        result_tv = (AppCompatTextView) findViewById(R.id.result_tv);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        listView = (ListView) findViewById(R.id.list_view);


        listView.setAdapter(adapter);


        final CountDownTimer timer =  new CountDownTimer(COUNT_DOWN_LIMIT, COUNT_DOWN_INCREMENT) {
            @Override
            public void onTick(long l) {
                result_tv.setText("Time remaining : " + (l / 1000) + " sec.");
            }

            @Override
            public void onFinish() {

                if (ndshelper != null) {
                    progressBar.setVisibility(View.GONE);
                    ndshelper.stopDiscovery();

                    if (ndshelper.getServiceListCount() > 0) {

                        List<String> infolist = new ArrayList<>();

                        for (NsdServiceInfo info : ndshelper.getServiceInfoList()) {

                            String infoString = "Name \t: " + info.getServiceName() + "\n" +
                                    "Type \t\t: " + info.getServiceType() + "\n" +
                                    "Host \t\t: " + info.getHost().toString().replace("/", "").trim() + "\n" +
                                    "Port \t\t: " + info.getPort();
                            infolist.add(infoString);
                        }
                        adapter = new ListViewAdapter(MainActivity.this,
                                android.R.layout.simple_list_item_1, infolist);
                        listView.setAdapter(adapter);
                        result_tv.setText(ndshelper.getServiceListCount() + " device(s) found.");
                    } else {
                        result_tv.setText("We could not detect your device, try again !!");
                    }
                }
                btn.setText("Discover");
                btn.setEnabled(true);
            }
        };
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                if (ndshelper != null && !NDSHELPER.isAlreadyDiscovered) {
                    progressBar.setVisibility(View.VISIBLE);
                    ndshelper.discoverServices();
                   timer.start();
                }
            }
        });
        ndshelper = new NDSHELPER(this, new NDSHELPER.OnResolveListener() {
            @Override
            public void onStopped(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailed(String errString, int code) {

            }
        });
        ndshelper.initializeNSD();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        if (ndshelper != null) {
            progressBar.setVisibility(View.GONE);
            ndshelper.stopDiscovery();
        }
        super.onPause();
    }

}
