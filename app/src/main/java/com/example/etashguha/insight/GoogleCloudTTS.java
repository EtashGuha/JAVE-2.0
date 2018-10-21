package com.example.etashguha.insight;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.google.gson.*;

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

import android.media.MediaPlayer;
import android.util.Base64;


/**
 * Google Cloud TextToSpeech API sample application.
 * Example usage: mvn package exec:java
 *                    -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
 */
public class GoogleCloudTTS {

    public static String API_KEY = "AIzaSyBOETr-ORKqzyip2GEsRHDJ0h2kDWrmetg";
    public static String targetURL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + API_KEY;

    /**
     * Demonstrates using the Text-to-Speech API.
     */
    public static String executePost(String text) {
        // Gson g = new Gson();
        // JsonObject obj = new JsonParser().parse("{\"input\": {\"text\": \"I'll suck your dick for t-shirt\" },\"voice\": { \"languageCode\": \"en-US\"},\"audioConfig\": {\"audioEncoding\": \"MP3\" } }").getAsJsonObject();

        String body = "{\n" +
                "\t\"input\": {\n" +
                "\t\t\"text\": \"" + text + "\"\n" +
                "\t},\n" +
                "\t\"voice\": {\n" +
                "\t\t\"languageCode\": \"en-US\"\n" +
                "\t},\n" +
                "\t\"audioConfig\":  {\n" +
                "\t\t\"audioEncoding\": \"MP3\"\n" +
                "\t}\n" +
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

    public static void playAudioFromString(String base64String){
        try{
            String url = "data:audio/wave;base64,"+ base64String;
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String... args) throws Exception {
        // Instantiates a client

        playAudioFromString(executePost("Hello World"));

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

