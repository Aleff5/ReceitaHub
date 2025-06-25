package com.example.receitahub.service;

import com.example.receitahub.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class GeminiService {
    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    private final GenerativeModelFutures generativeModel;
    private ChatFutures chat;

    public GeminiService() {
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                BuildConfig.GEMINI_API_KEY
        );
        this.generativeModel = GenerativeModelFutures.from(gm);
    }

    public void iniciarChat() {
        String systemInstruction = "Você é o ReceitaHub, um assistente de culinária amigável e prestativo. " +
                "Seja sempre educado e cordial. Responda a saudações como 'olá' de forma natural. " +
                "Quando pedirem uma receita, forneça uma receita completa. " +
                "Quando uma receita completa for solicitada, formate sua resposta da seguinte maneira, e nada mais: " +
                "###TÍTULO### [Título da Receita] ###INGREDIENTES### [Lista de ingredientes] ###MODOPREPARO### [Passos do modo de preparo]. " +
                "Para outras perguntas sobre culinária ou follow-ups, responda de forma conversacional.";

        Content instructionContent = new Content.Builder()
                .addText(systemInstruction)
                .build();

        List<Content> chatHistory = new ArrayList<>();
        chatHistory.add(instructionContent);

        this.chat = generativeModel.startChat(chatHistory);
    }

<<<<<<< HEAD

=======
    /**
     * MÉTODO CORRIGIDO
     */
>>>>>>> 210fafdd5e78a39d3b2fe57ad04e2fa74d032c4a
    public void enviarMensagemNoChat(String prompt, Executor executor, GeminiCallback callback) {
        if (chat == null) {
            callback.onError(new IllegalStateException("O chat não foi iniciado. Chame iniciarChat() primeiro."));
            return;
        }

<<<<<<< HEAD

        Content promptContent = new Content.Builder().addText(prompt).build();

=======
        // 1. Converte a String do prompt em um objeto Content
        Content promptContent = new Content.Builder().addText(prompt).build();

        // 2. Envia o objeto Content para o método sendMessage
>>>>>>> 210fafdd5e78a39d3b2fe57ad04e2fa74d032c4a
        ListenableFuture<GenerateContentResponse> future = chat.sendMessage(promptContent);

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
