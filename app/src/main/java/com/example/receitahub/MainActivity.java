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

    // Componentes da UI
    private SearchView mainSearchView;
    private Button btnPopularRecipes, btnQuickRecipes, btnHealthyRecipes, btnVeganRecipes, btnDailyRecipe, btnNews;
    private RecyclerView aiChatRecyclerView;
    private EditText aiMessageEditText;
    private ImageButton aiSendButton;
    private ProgressBar aiLoadingIndicator;
    private BottomNavigationView bottomNavigation;

    // Variáveis do Chat
    private List<Mensagem> mensagemList;
    private ChatAdapter chatAdapter;
    private GeminiService geminiService;

    // Componentes de Dados e Sessão
    private SessionManager sessionManager;
    private ReceitaDao receitaDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializações
        geminiService = new GeminiService();
        sessionManager = new SessionManager(getApplicationContext());
        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();

        iniciarComponentes();
        configurarListeners();
        configurarChatRecyclerView();
    }

    private void iniciarComponentes() {
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
        // Listeners para os botões de categoria de receita
        View.OnClickListener categoryClickListener = v -> {
            Button b = (Button) v;
            Toast.makeText(MainActivity.this, "Clicou em: " + b.getText().toString(), Toast.LENGTH_SHORT).show();
        };

        btnPopularRecipes.setOnClickListener(categoryClickListener);
        btnQuickRecipes.setOnClickListener(categoryClickListener);
        btnHealthyRecipes.setOnClickListener(categoryClickListener);
        btnVeganRecipes.setOnClickListener(categoryClickListener);
        btnDailyRecipe.setOnClickListener(categoryClickListener);
        btnNews.setOnClickListener(categoryClickListener);

        // Listener para o botão de enviar mensagem para a IA
        aiSendButton.setOnClickListener(v -> {
            String mensagemTexto = aiMessageEditText.getText().toString().trim();
            if (!mensagemTexto.isEmpty()) {
                enviarMensagem(mensagemTexto);
            }
        });

        // Listener para a barra de navegação inferior
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Toast.makeText(this, "Home Clicado", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_favorites) {
                Toast.makeText(this, "Favoritos Clicado", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileEditActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_add_recipe) {
                Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_ai_chat) {
                Toast.makeText(this, "Chat IA já está visível.", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void configurarChatRecyclerView() {
        mensagemList = new ArrayList<>();
        chatAdapter = new ChatAdapter(mensagemList);
        aiChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        aiChatRecyclerView.setAdapter(chatAdapter);
    }

    private void enviarMensagem(String texto) {
        // Lógica para limitar o chat de convidados
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

        // Prompt estruturado para a IA
        String prompt = "Aja como um assistente de culinária. Crie uma receita completa baseada no seguinte pedido do usuário: '" + texto + "'. " +
                "Formate sua resposta da seguinte maneira, e nada mais: " +
                "###TÍTULO### [Título da Receita] " +
                "###INGREDIENTES### [Lista de ingredientes, um por linha] " +
                "###MODOPREPARO### [Passos do modo de preparo]";

        geminiService.gerarConteudo(prompt, executor, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (!sessionManager.isLoggedIn()) {
                    sessionManager.incrementChatInteractionCount();
                } else {
                    salvarReceitaGerada(response, sessionManager.getUserId());
                }

                runOnUiThread(() -> {
                    aiLoadingIndicator.setVisibility(View.GONE);
                    // Formata a resposta para melhor visualização no chat
                    String formattedResponse = response.replace("###TÍTULO###", "Título:")
                            .replace("###INGREDIENTES###", "\n\nIngredientes:")
                            .replace("###MODOPREPARO###", "\n\nModo de Preparo:");
                    Mensagem mensagemIA = new Mensagem(formattedResponse, false);
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
        try {
            // Extrai as partes da resposta baseada na formatação
            String titulo = rawResponse.split("###TÍTULO###")[1].split("###INGREDIENTES###")[0].trim();
            String ingredientes = rawResponse.split("###INGREDIENTES###")[1].split("###MODOPREPARO###")[0].trim();
            String modoPreparo = rawResponse.split("###MODOPREPARO###")[1].trim();

            if (!titulo.isEmpty() && !ingredientes.isEmpty() && !modoPreparo.isEmpty()) {
                Receita receitaGerada = new Receita(userId, titulo, ingredientes, modoPreparo, "GERADA");
                executor.execute(() -> receitaDao.salvarReceita(receitaGerada));
            }
        } catch (Exception e) {
            // Se a formatação da IA falhar, a receita não é salva, mas o erro é logado.
            // O usuário ainda recebe a resposta no chat.
            e.printStackTrace();
        }
    }
}