package com.example.etashguha.insight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static android.os.StrictMode.setThreadPolicy;

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
                        String [] path = takeScreenshot();
                        String jsonStrong;
                        try {
                           jsonStrong = jpegToString(path[0],path[1]);
                        } catch (Exception e) {
                            jsonStrong = null;
                            e.printStackTrace();
                        }
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        setThreadPolicy(policy);
                        String exba = executePost(jsonStrong);
                        GoogleCloudTTS tts = new GoogleCloudTTS(exba);
                        exba = exba.substring(120,1000);
                        String song = GoogleCloudTTS.executePost(exba);
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

    private String[] takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            String [] strings = new String[2];
            strings[0] = Environment.getExternalStorageDirectory().toString() +  "/";
            strings[1] = (now + ".jpg");
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            System.out.println(mPath);
            return strings;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            return null;
        }
    }
    public static String jpegToString(String path, String name) throws Exception{
        try {
            byte[] input_file = Files.readAllBytes(Paths.get(path+name));
            // byte[] encodedBytes = Base64.getEncoder().encode(input_file);
            String encodedString = Base64.encodeToString(input_file, Base64.DEFAULT);
            //String encodedString =  new String(encodedBytes);
            return encodedString;
        }
        catch(Exception e) {
            // e.printStackTrace();
            return null;
        }

    }
    public static String API_KEY = "AIzaSyBOETr-ORKqzyip2GEsRHDJ0h2kDWrmetg";
    public static String targetURL = "https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY;

    /**
     * Demonstrates using the Text-to-Speech API.
     */
    public static String executePost(String encodedImage) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);
        String body = "{\n" +
                "  \"requests\":[\n" +
                "    {\n" +
                "      \"image\":{\n" +
                "        \"content\":\""+encodedImage+"\""+"\n" +
                "      },\n" +
                "      \"features\":[\n" +
                "        {\n" +
                "          \"type\":\"DOCUMENT_TEXT_DETECTION\",\n" +
                "          \"maxResults\":4\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(body);
            wr.close();


            //getting response

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();


/*
            String jsonLine = response.toString();
            JsonElement jelement = new JsonParser().parse(jsonLine);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonArray jarray = jobject.getAsJsonArray("responses");
            JsonArray jarrayTwo = jarray.get(0).getAsJsonArray();
            JsonObject jsonObjectTwo = jarrayTwo.get(1).getAsJsonObject();
            String result = jsonObjectTwo.get("description").getAsString();
            return result;
*/

            //System.out.println("");
            //System.out.println("!!" + response.toString() + "==");

            // decode json
            // decode base64
            // output is a wave file
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail";

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


    }


}
