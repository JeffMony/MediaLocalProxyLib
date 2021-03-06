package com.android.media;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.baselib.utils.LogUtils;
import com.media.cache.model.Video;
import com.media.cache.VideoDownloadManager;
import com.media.cache.listener.IDownloadListener;
import com.media.cache.model.VideoTaskItem;
import com.media.cache.model.VideoTaskMode;

public class DownloadBaseListActivity extends AppCompatActivity {

    private ListView mDownloadListView;
    private TextView mFilePath;
    private Button mClearBtn;
    private Button mPauseBtn;

    private VideoListAdapter mAdapter;
    private VideoTaskItem[] items = new VideoTaskItem[8];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);

        VideoDownloadManager.getInstance().setGlobalDownloadListener(mListener);
        initViews();
        initDatas();
    }

    private void initViews() {
        mDownloadListView = (ListView) findViewById(R.id.download_listview);
        mFilePath = (TextView) findViewById(R.id.file_path);
        mClearBtn = (Button) findViewById(R.id.clear_cache_btn);
        mPauseBtn = (Button) findViewById(R.id.pause_task_btn);
        mFilePath.setText(VideoDownloadManager.getInstance().getCacheFilePath());
    }

    private void initDatas() {
        VideoTaskItem item1 = new VideoTaskItem("https://tv.youkutv.cc/2019/10/28/6MSVuLec4zbpYFlj/playlist.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item2 = new VideoTaskItem("https://kuku.zuida-youku.com/20170616/cBIBaYMJ/index.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item3 = new VideoTaskItem("https://tv.youkutv.cc/2020/01/15/SZpLQDUmJZKF9O0D/playlist.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item4 = new VideoTaskItem("https://tv.youkutv.cc/2020/01/15/3d97sO5xQUYB5bvY/playlist.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item5 = new VideoTaskItem("https://hls.aoxtv.com/v3.szjal.cn/20200122/TIj9Ekt9/index.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item6 = new VideoTaskItem("https://hls.aoxtv.com/v3.szjal.cn/20200114/dtOHlPFE/index.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item7 = new VideoTaskItem("https://hls.aoxtv.com/v3.szjal.cn/20200115/qNIba0qo/index.m3u8", VideoTaskMode.DOWNLOAD_MODE);
        VideoTaskItem item8 = new VideoTaskItem("https://hls.aoxtv.com/v3.szjal.cn/20200114/2KwuUDMK/index.m3u8", VideoTaskMode.DOWNLOAD_MODE);

        items[0] = item1;
        items[1] = item2;
        items[2] = item3;
        items[3] = item4;
        items[4] = item5;
        items[5] = item6;
        items[6] = item7;
        items[7] = item8;

        mAdapter = new VideoListAdapter(this, R.layout.download_item, items);
        mDownloadListView.setAdapter(mAdapter);

        mDownloadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d("jeffmony onItemClick url="+items[position].getUrl());
                VideoTaskItem item = items[position];
                if (item.isRunningTask()) {
                    LogUtils.d("jeffmony pause downloading.");
                    VideoDownloadManager.getInstance().pauseDownloadTask(item);
                } else if (item.isSlientTask()) {
                    LogUtils.d("jeffmony start downloading.");
                    VideoDownloadManager.getInstance().startDownload(item);
                }
            }
        });

        mDownloadListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.w("jeffmony long click");
                return true;
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDownloadManager.getInstance().deleteVideoTasks(items);
            }
        });

        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoDownloadManager.getInstance().pauseDownloadTasks(items);
            }
        });
    }

    private IDownloadListener mListener = new IDownloadListener() {


        @Override
        public void onDownloadDefault(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadDefault: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadPending(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadPending: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadPrepare(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadPrepare: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadStart(VideoTaskItem item) {
            LogUtils.d("onDownloadStart: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadProxyReady(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadProxyReady: " + item.getProxyUrl());
        }

        @Override
        public void onDownloadProgress(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadProgress: " + item.getPercentString());
            notifyChanged(item);
        }

        @Override
        public void onDownloadSpeed(VideoTaskItem item) {
            notifyChanged(item);
        }

        @Override
        public void onDownloadPause(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadPause: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadError(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadError: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadProxyForbidden(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadForbidden: " + item.getUrl());
            notifyChanged(item);
        }

        @Override
        public void onDownloadSuccess(VideoTaskItem item) {
            LogUtils.d("jeffmony onDownloadSuccess: " + item.getUrl());
            notifyChanged(item);
        }
    };

    private void notifyChanged(VideoTaskItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyChanged(items, item);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.w("jeffmony onDestroy");
    }
}
