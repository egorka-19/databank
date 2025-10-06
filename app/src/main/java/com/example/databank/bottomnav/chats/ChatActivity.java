package com.example.databank.bottomnav.chats;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.databank.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    public ImageButton send_button;
    public TextView answer, my_message;
    public EditText edit;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        my_message = findViewById(R.id.you_message);
        send_button = findViewById(R.id.send);
        answer = findViewById(R.id.answer);
        edit = findViewById(R.id.enter);

        handler = new Handler(Looper.getMainLooper());

        send_button.setOnClickListener(v -> {
            my_message.setVisibility(View.VISIBLE);
            String my_mess = edit.getText().toString();
            my_message.setText(my_mess);
            String prompt = edit.getText().toString();
            edit.setText("");
            if (!prompt.isEmpty()) {
                // Запускаем сетевой запрос в фоновом потоке
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    String response = chatGPT("Ты консультант в гипермаркете. Ответь на вопрос простым языком: " + prompt +
                            "напиши небольшой ответ").toLowerCase();

                    // После получения ответа выполняем действия в главном потоке
                    handler.post(() -> {
                        answer.setVisibility(View.VISIBLE);
                        answer.setText(response);
                        // Передаем категорию товара в следующее Activity
                    });
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static String chatGPT(String prompt) {
        String url = "https://api.naga.ac/v1/chat/completions";
        String apiKey = "ng-AUBPLAbnJiRBzvplwVzA8OiKgwGw8";
        String model = "gemini-pro";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 10;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
}