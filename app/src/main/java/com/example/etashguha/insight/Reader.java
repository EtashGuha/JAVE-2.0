package com.example.etashguha.insight;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;

public class Reader extends AppCompatActivity {

    PDFView pdfView;
    int pageNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Created", "true");
        super.onCreate(savedInstanceState);
        Log.d("Created", "true");
        setContentView(R.layout.activity_reader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pageNumber = 0;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        pdfView = findViewById(R.id.pdfView);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        final File file = new File(getIntent().getStringExtra("File Name"));

        pdfView.fromUri(Uri.fromFile(file)).pages(pageNumber).load();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mainMenu:
                        Intent intent = new Intent(getBaseContext(), MainMenu.class);
                        startActivity(intent);
                        break;
                    case R.id.ttsbutton:
                        Log.d("Hi", "hi");
                        GoogleCloudTTS tts = new GoogleCloudTTS("I will suck a dick for a T-shirt.");
                        String song = tts.get64String();
                        String url = "data:audio/wave;base64," + song;
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case R.id.forward:
                        pdfView.fromUri(Uri.fromFile(file)).pages(pageNumber + 1).load();
                        pageNumber += 1;
                        break;
                    case R.id.backward:
                        pdfView.fromUri(Uri.fromFile(file)).pages(pageNumber - 1).load();
                        pageNumber -= 1;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            File file = new File(filePath);
            pdfView.fromUri(Uri.fromFile(file)).load();
        }
    }



}
