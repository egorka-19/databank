package com.example.databank.UI.Users;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.databank.databinding.ActivityFinancialAssistantBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class FinancialAssistantActivity extends AppCompatActivity {
    private ActivityFinancialAssistantBinding binding;
    private final ChatAdapter chatAdapter = new ChatAdapter();
    private final OkHttpClient http = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinancialAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Финансовый ассистент");

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(chatAdapter);

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        binding.messageInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        final String text = binding.messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        chatAdapter.add(ChatAdapter.Message.user(text));
        binding.messageInput.setText("");

        new Thread(() -> {
            try {
                String body = "{\n" +
                        "  \"model\": \"GigaChat:latest\",\n" +
                        "  \"stream\": false,\n" +
                        "  \"update_interval\": 0,\n" +
                        "  \"messages\": [\n" +
                        "    { \"role\": \"system\", \"content\": \"Ты учитель по финансовой грамотности, объясняй четко и понятно.\" },\n" +
                        "    { \"role\": \"user\", \"content\": " + quote(text) + " }\n" +
                        "  ],\n" +
                        "  \"n\": 1,\n" +
                        "  \"max_tokens\": 256,\n" +
                        "  \"repetition_penalty\": 1.0\n" +
                        "}";

                Request request = new Request.Builder()
                        .url("https://derendyaev.ru/api/gigachat/message")
                        .post(RequestBody.create(body, JSON))
                        .addHeader("Content-Type", "application/json")
                        .build();

                try (Response response = http.newCall(request).execute()) {
                    final String reply;
                    if (response.isSuccessful() && response.body() != null) {
                        String bodyStr = response.body().string();
                        String contentOnly = extractContent(bodyStr);
                        reply = contentOnly != null ? contentOnly : bodyStr;
                    } else {
                        String err = response.body() != null ? response.body().string() : "";
                        reply = "Ошибка " + response.code() + ": " + err;
                    }

                    runOnUiThread(() -> chatAdapter.add(ChatAdapter.Message.assistant(reply)));
                }
            } catch (IOException e) {
                runOnUiThread(() -> chatAdapter.add(ChatAdapter.Message.assistant("Ошибка сети")));
            } catch (Exception e) {
                runOnUiThread(() -> chatAdapter.add(ChatAdapter.Message.assistant("Произошла ошибка")));
            }
        }).start();
    }

    private String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private String extractContent(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("content")) {
                return obj.optString("content", null);
            }
            // fallback: some APIs return { data: { content: ... } }
            if (obj.has("data")) {
                JSONObject data = obj.optJSONObject("data");
                if (data != null && data.has("content")) return data.optString("content", null);
            }
        } catch (Exception ignored) {}
        return null;
    }
}


