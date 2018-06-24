package com.example.tblume.flashairphotobox;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Handler mHandler;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                getUpdateStatus();
            } finally {
                mHandler.postDelayed(mRunnable, 100);
            }
        }
    };

    private ViewPager viewPager;
    private ImagePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ImagePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        mHandler = new Handler();
        mRunnable.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    public void getUpdateStatus() {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return FlashAirRequest.getUpdateStatus();
            }
            @Override
            protected void onPostExecute(Boolean updateStatus) {
                if (updateStatus) {
                    update();
                }
            }
        }.execute();
    }

    public void update() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Images ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        new AsyncTask<Void, Void, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... params) {
                return FlashAirRequest.getLatestFileNames(pagerAdapter.NUM_PAGES);
            }

            @Override
            protected void onPostExecute(List<String> imageFiles) {

                progressDialog.dismiss();

                pagerAdapter.fileList = imageFiles;
                pagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(pagerAdapter.getCount() - 1);
            }
        }.execute();
    }
}
