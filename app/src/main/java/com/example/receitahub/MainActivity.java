package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receitahub.adapter.ChatAdapter;
import com.example.receitahub.data.model.Mensagem;
import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.AppDatabase;
import com.example.receitahub.db.dao.ReceitaDao;
import com.example.receitahub.service.GeminiService;
import com.example.receitahub.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // ... (Componentes da UI e Variáveis - sem alterações)
    private SearchView mainSearchView;
    private Button btnPopularRecipes, btnQuickRecipes, btnHealthyRecipes, btnVeganRecipes, btnDailyRecipe, btnNews;
    private RecyclerView aiChatRecyclerView;
    private EditText aiMessageEditText;
    private ImageButton aiSendButton;
    private ProgressBar aiLoadingIndicator;
    private BottomNavigationView bottomNavigation;
    private List<Mensagem> mensagemList;
    private ChatAdapter chatAdapter;
    private GeminiService geminiService;
    private SessionManager sessionManager;
    private ReceitaDao receitaDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializações
        sessionManager = new SessionManager(getApplicationContext());
        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();

        // MUDANÇA 1: Inicia o serviço e a sessão de chat UMA VEZ
        geminiService = new GeminiService();
        geminiService.iniciarChat();

        iniciarComponentes();
        configurarListeners();
        configurarChatRecyclerView();
    }

    private void iniciarComponentes() {
        // ... (sem alterações)
        mainSearchView = findViewById(R.id.main_search_view);
        btnPopularRecipes = findViewById(R.id.btn_popular_recipes);
        btnQuickRecipes = findViewById(R.id.btn_quick_recipes);
        btnHealthyRecipes = findViewById(R.id.btn_healthy_recipes);
        btnVeganRecipes = findViewById(R.id.btn_vegan_recipes);
        btnDailyRecipe = findViewById(R.id.btn_daily_recipe);
        btnNews = findViewById(R.id.btn_news);
        aiChatRecyclerView = findViewById(R.id.ai_chat_recycler_view);
        aiMessageEditText = findViewById(R.id.ai_message_edit_text);
        aiSendButton = findViewById(R.id.ai_send_button);
        aiLoadingIndicator = findViewById(R.id.ai_loading_indicator);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        aiChatRecyclerView.setVisibility(View.VISIBLE);
    }

    private void configurarListeners() {
        View.OnClickListener categoryClickListener = v -> {
            Button b = (Button) v;
            String categoria = b.getText().toString();
            // MUDANÇA 2: O prompt agora é mais direto
            String promptParaIA = "Por favor, me dê 5 sugestões de " + categoria.toLowerCase() + ".";
            enviarMensagem(promptParaIA);
        };

        btnPopularRecipes.setOnClickListener(categoryClickListener);
        btnQuickRecipes.setOnClickListener(categoryClickListener);
        btnHealthyRecipes.setOnClickListener(categoryClickListener);
        btnVeganRecipes.setOnClickListener(categoryClickListener);
        btnDailyRecipe.setOnClickListener(categoryClickListener);
        btnNews.setOnClickListener(categoryClickListener);

        aiSendButton.setOnClickListener(v -> {
            String mensagemTexto = aiMessageEditText.getText().toString().trim();
            if (!mensagemTexto.isEmpty()) {
                enviarMensagem(mensagemTexto);
            }
        });

        // ... (Listener da bottomNavigation sem alterações)
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) { return true; }
            if (itemId == R.id.navigation_favorites) { return true; }
            if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(MainActivity.this, ProfileEditActivity.class));
                return true;
            }
            if (itemId == R.id.navigation_add_recipe) {
                startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
                return true;
            }
            if (itemId == R.id.navigation_ai_chat) { return true; }
            return false;
        });
    }

    private void configurarChatRecyclerView() {
        // ... (sem alterações)
        mensagemList = new ArrayList<>();
        chatAdapter = new ChatAdapter(mensagemList);
        aiChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        aiChatRecyclerView.setAdapter(chatAdapter);
    }

    // MUDANÇA 3: Método `enviarMensagem` ficou mais simples
    private void enviarMensagem(String texto) {
        if (!sessionManager.isLoggedIn() && sessionManager.getChatInteractionCount() >= 5) {
            Toast.makeText(this, "Você atingiu o limite de 5 mensagens. Faça login para continuar.", Toast.LENGTH_LONG).show();
            return;
        }

        // Adiciona a mensagem do usuário à UI
        Mensagem mensagemUsuario = new Mensagem(texto, true);
        mensagemList.add(mensagemUsuario);
        chatAdapter.notifyItemInserted(mensagemList.size() - 1);
        aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);
        aiMessageEditText.setText("");
        aiLoadingIndicator.setVisibility(View.VISIBLE);

        // Agora, simplesmente enviamos a mensagem do usuário para o chat
        geminiService.enviarMensagemNoChat(texto, executor, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (!sessionManager.isLoggedIn()) {
                    sessionManager.incrementChatInteractionCount();
                }

                // MUDANÇA 4: A lógica de salvar agora verifica se a resposta contém o formato de receita
                if (sessionManager.isLoggedIn() && response.contains("###TÍTULO###")) {
                    salvarReceitaGerada(response, sessionManager.getUserId());
                }

                runOnUiThread(() -> {
                    aiLoadingIndicator.setVisibility(View.GONE);
                    // Formata a resposta para melhor visualização (se contiver os marcadores)
                    String finalResponse = response.replace("###TÍTULO###", "Título:")
                            .replace("###INGREDIENTES###", "\n\nIngredientes:")
                            .replace("###MODOPREPARO###", "\n\nModo de Preparo:");

                    Mensagem mensagemIA = new Mensagem(finalResponse, false);
                    mensagemList.add(mensagemIA);
                    chatAdapter.notifyItemInserted(mensagemList.size() - 1);
                    aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    aiLoadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Falha ao contatar a IA. Tente novamente.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void salvarReceitaGerada(String rawResponse, long userId) {
        // ... (sem alterações)
        try {
            String titulo = rawResponse.split("###TÍTULO###")[1].split("###INGREDIENTES###")[0].trim();
            String ingredientes = rawResponse.split("###INGREDIENTES###")[1].split("###MODOPREPARO###")[0].trim();
            String modoPreparo = rawResponse.split("###MODOPREPARO###")[1].trim();
            if (!titulo.isEmpty() && !ingredientes.isEmpty() && !modoPreparo.isEmpty()) {
                Receita receitaGerada = new Receita(userId, titulo, ingredientes, modoPreparo, "GERADA");
                executor.execute(() -> receitaDao.salvarReceita(receitaGerada));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}