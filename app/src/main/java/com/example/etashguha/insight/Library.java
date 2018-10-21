package com.example.etashguha.insight;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.ArrayList;

public class Library extends AppCompatActivity {

    Button bookA;
    LinearLayout linearLayout;
    ArrayList<String> filePaths;
    ScrollView scrollView;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        //added LInearLayout
        linearLayout = (LinearLayout) findViewById(R.id.activity_linear_layout);
        //added LayoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //add textView
        TextView textView = new TextView(this);
        textView.setText("The developer world is yours");
        textView.setId(1);
        textView.setLayoutParams(params);

        // added Button
        Button button = new Button(this);
        button.setText("thedeveloperworldisyours");
        button.setId(2);

        //added the textView and the Button to LinearLayout
        linearLayout.addView(textView);


    }


    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Opt","hi");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            new MaterialFilePicker()
                    .withActivity(this)
                    .withRequestCode(1)
                    .withHiddenFiles(true) // Show hidden files and folders
                    .start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Button button = new Button(this);
            button.setText(filePath.substring(filePath.lastIndexOf("/") + 1 ));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), Reader.class);
                    intent.putExtra("File Name", filePath);
                    Log.d("Filename", filePath);
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }
    }
    }

