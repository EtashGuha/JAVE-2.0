package com.example.etashguha.insight;

import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutput;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Google Cloud TextToSpeech API sample application.
 * Example usage: mvn package exec:java
 *                    -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
 */
public class GoogleCloudOCR {
    public static String jpegToString(String path, String name) throws Exception{
        try {
            byte[] input_file = Files.readAllBytes(Paths.get(path+name));
            // byte[] encodedBytes = Base64.getEncoder().encode(input_file);
            String encodedString = Base64.encodeToString(input_file, Base64.DEFAULT);

            //String encodedString =  new String(encodedBytes);
            return encodedString;
        }
        catch(Exception e) {
            return null;
        }

    }

    public static String API_KEY = "AIzaSyBOETr-ORKqzyip2GEsRHDJ0h2kDWrmetg";
    public static String targetURL = "https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY;

    /**
     * Demonstrates using the Text-to-Speech API.
     */
    public static String executePost(String encodedImage) {
       // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        //setThreadPolicy(policy);
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
            //jobject = jobject.getAsJsonObject("data");
            //JsonArray jarray = jobject.getAsJsonArray("translations");
            // jobject = jarray.get(0).getAsJsonObject();
            String result = jobject.get("audioContent").getAsString();
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





    public static void main(String... args) throws Exception {
        // Instantiates a client

        executePost(jpegToString("C:\\Users\\alimi\\Downloads\\","OurVirginia3.jpg"));

        /*
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText("Hello, World!")
                    .build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                    audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
            }
        }
    bh */
    }

}