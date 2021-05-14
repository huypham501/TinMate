package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hcmus.tinuni.R;

public class ShowMediaActivity extends Activity {

    private ImageView btnGoBack, btnDownload;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private VideoView videoView;
    String media_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_media);

        btnGoBack = findViewById(R.id.btnGoBack);
        btnDownload = findViewById(R.id.btnDownload);
        videoView = findViewById(R.id.videoView);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        media_link = intent.getStringExtra("media_link");

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMediaActivity.super.onBackPressed();
            }
        });

        videoView.setVideoURI(Uri.parse(media_link));
        if(URLUtil.guessFileName(media_link, null, null).contains("mp3")) {
            videoView.setBackgroundResource(R.drawable.bg_media);
        }
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("********* download");
                new AlertDialog.Builder(ShowMediaActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Download file")
                        .setMessage("Are you sure you want to download ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filename = URLUtil.guessFileName(media_link, null, null);
                                download(filename);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void download(String filename) {
        StorageReference storageRef = storageReference.child("files/chats/" + filename);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadFile(ShowMediaActivity.this, filename, Environment.DIRECTORY_DOWNLOADS, media_link);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFile(Context context, String fileName, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadManager.enqueue(request);
    }
}