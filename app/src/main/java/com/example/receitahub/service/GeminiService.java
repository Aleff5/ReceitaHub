package com.example.receitahub.service;

import com.example.receitahub.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;



public class GeminiService {
    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }


    private final GenerativeModelFutures generativeModel;

    public GeminiService() {
        // Inicializa o modelo com a chave de API do BuildConfig
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                BuildConfig.GEMINI_API_KEY
        );
        this.generativeModel = GenerativeModelFutures.from(gm);
    }

    public void gerarConteudo(String prompt, Executor executor, GeminiCallback callback) {

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {

                String responseText = result.getText();
                if (responseText != null) {
                    callback.onSuccess(responseText);
                } else {
                    callback.onError(new Exception("Resposta vazia da API."));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(new Exception(t));
            }
        }, executor);
    }
}