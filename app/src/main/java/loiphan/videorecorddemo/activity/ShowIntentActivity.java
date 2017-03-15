package loiphan.videorecorddemo.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

import loiphan.videorecorddemo.R;

/**
 * Copyright (c) 2017, Stacck Pte Ltd. All rights reserved.
 *
 * @author Lio <lphan@stacck.com>
 * @version 1.0
 * @since January 05, 2017
 */

public class ShowIntentActivity extends AppCompatActivity {

    private HttpProxyCacheServer mProxyCacheServer;

    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_intent);

        String message = getIntent().getStringExtra("message");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        TextView txtLog = (TextView) findViewById(R.id.txtLog);
        txtLog.setText(message);

        txtLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowIntentActivity.this, FinalActivity.class);
                startActivity(intent);
            }
        });

        videoView = (VideoView) findViewById(R.id.videoView);

        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        findViewById(R.id.btnDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopupVideoView();
                showPopupSample();
            }
        });

        mProxyCacheServer = MyApplication.getProxy(this);

        videoView.setVideoPath(mProxyCacheServer.getProxyUrl("https://stacck.s3.amazonaws.com/uploads/uploads/000/009/990/original/open-uri20170215-19627-sm18cb?1487147744"));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "ShowIntentActivity onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public void showPopupVideoView() {
        // custom dialog
        final Dialog dialog = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);

//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.popup_video_view);

//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;

        RelativeLayout videoViewContainer = (RelativeLayout) dialog.findViewById(R.id.videoViewContainer);
        final VideoView videoView = (VideoView) dialog.findViewById(R.id.videoView);

        videoView.setVideoPath(mProxyCacheServer.getProxyUrl("https://stacck.s3.amazonaws.com/uploads/uploads/000/009/990/original/open-uri20170215-19627-sm18cb?1487147744"));
//        videoView.start();

        videoViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                videoView.setVisibility(View.GONE);
            }
        });

        dialog.show();
    }

    public void showPopupSample() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);

//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.popup_sample);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;

        dialog.show();
    }
}
