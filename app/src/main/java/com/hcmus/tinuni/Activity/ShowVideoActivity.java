package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
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

public class ShowVideoActivity extends Activity {

    private ImageView btnGoBack, btnDownload;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private VideoView videoView;
    String video_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        btnGoBack = findViewById(R.id.btnGoBack);
        btnDownload = findViewById(R.id.btnDownload);
        videoView = findViewById(R.id.videoView);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Intent intent = getIntent();
//        video_link = intent.getStringExtra("video_link");

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowVideoActivity.super.onBackPressed();
            }
        });
//        String music_link = "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/songs%2F1618162806194-sample4.mp3?alt=media&token=9e9b218b-cae5-4ae7-bed2-8ba68a9a1459";
        video_link = "https://firebasestorage.googleapis.com/v0/b/tinuni.appspot.com/o/files%2Fchats%2Ffile_example_MP4_480_1_5MG.mp4?alt=media&token=79ab6f18-bc68-45c4-8f7b-190bd79f2a3e";

        videoView.setVideoURI(Uri.parse(video_link));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
//        videoView.start();

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("********* download");
                new AlertDialog.Builder(ShowVideoActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to download ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filename = URLUtil.guessFileName(video_link, null, null);
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
                downloadFile(ShowVideoActivity.this, filename, Environment.DIRECTORY_DOWNLOADS, video_link);
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