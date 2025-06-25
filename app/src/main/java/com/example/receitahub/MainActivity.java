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

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

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
    private ConstraintLayout mainLayout;
    private Group categoryButtonsGroup;
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

    private boolean isChatModeActive = false;
    private OnBackPressedCallback onBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa os serviços
        sessionManager = new SessionManager(getApplicationContext());
        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();
        geminiService = new GeminiService();
        geminiService.iniciarChat(); // Inicia o chat com as instruções do sistema

        // Configura a UI
        iniciarComponentes();
        configurarListeners();
        configurarChatRecyclerView();

        // Configura o botão "Voltar" do sistema
        onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                showHomeMode();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void iniciarComponentes() {
        mainLayout = findViewById(R.id.main_layout);
        categoryButtonsGroup = findViewById(R.id.category_buttons_group);
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
    }

    private void configurarListeners() {
        // Listener para os botões de categoria
        View.OnClickListener categoryClickListener = v -> {
            if (!isChatModeActive) {
                showChatMode(); // Entra no modo de chat se não estiver nele
            }
            Button b = (Button) v;
            String categoria = b.getText().toString();
            String promptParaIA = "Me dê uma receita de " + categoria.toLowerCase();
            enviarMensagem(promptParaIA);
        };

        btnPopularRecipes.setOnClickListener(categoryClickListener);
        btnQuickRecipes.setOnClickListener(categoryClickListener);
        btnHealthyRecipes.setOnClickListener(categoryClickListener);
        btnVeganRecipes.setOnClickListener(categoryClickListener);
        btnDailyRecipe.setOnClickListener(categoryClickListener);
        btnNews.setOnClickListener(categoryClickListener);

        // Listener para quando o campo de texto da IA ganha foco
        aiMessageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !isChatModeActive) {
                showChatMode();
            }
        });

        // Listener para o botão de enviar
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
                if (isChatModeActive) showHomeMode();
                return true;
            } else if (itemId == R.id.navigation_favorites) {
                startActivity(new Intent(MainActivity.this, MyRecipesActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(MainActivity.this, ProfileEditActivity.class));
                return true;
            } else if (itemId == R.id.navigation_add_recipe) {
                startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
                return true;
            } else if (itemId == R.id.navigation_ai_chat) {
                if (!isChatModeActive) showChatMode();
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
        if (!sessionManager.isLoggedIn() && sessionManager.getChatInteractionCount() >= 5) {
            Toast.makeText(this, "Você atingiu o limite de 5 mensagens. Faça login para continuar.", Toast.LENGTH_LONG).show();
            return;
        }

        // Adiciona a mensagem do usuário à lista e notifica o adapter
        Mensagem mensagemUsuario = new Mensagem(texto, true);
        mensagemList.add(mensagemUsuario);
        chatAdapter.notifyItemInserted(mensagemList.size() - 1);
        aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);

        aiMessageEditText.setText("");
        aiLoadingIndicator.setVisibility(View.VISIBLE);

        // Chama o método correto do serviço de chat
        geminiService.enviarMensagemNoChat(texto, executor, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (!sessionManager.isLoggedIn()) {
                    sessionManager.incrementChatInteractionCount();
                }

                // Se a resposta contiver o formato de receita e o usuário estiver logado, salva no banco
                if (response.contains("###TÍTULO###") && sessionManager.isLoggedIn()) {
                    salvarReceitaGerada(response, sessionManager.getUserId());
                }

                // Formata a resposta para exibição
                String finalResponse = response.replace("###TÍTULO###", "Título:")
                        .replace("###INGREDIENTES###", "\n\nIngredientes:")
                        .replace("###MODOPREPARO###", "\n\nModo de Preparo:");

                runOnUiThread(() -> {
                    aiLoadingIndicator.setVisibility(View.GONE);
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
                    Toast.makeText(MainActivity.this, "Falha ao contatar a IA: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void salvarReceitaGerada(String rawResponse, long userId) {
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

    private void showChatMode() {
        if (isChatModeActive) return;
        isChatModeActive = true;
        onBackPressedCallback.setEnabled(true);

        bottomNavigation.setSelectedItemId(R.id.navigation_ai_chat);
        categoryButtonsGroup.setVisibility(View.GONE);
        aiChatRecyclerView.setVisibility(View.VISIBLE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.connect(R.id.ai_chat_recycler_view, ConstraintSet.TOP, R.id.tv_app_title, ConstraintSet.BOTTOM, 16);
        TransitionManager.beginDelayedTransition(mainLayout);
        constraintSet.applyTo(mainLayout);
    }

    private void showHomeMode() {
        if (!isChatModeActive) return;
        isChatModeActive = false;
        onBackPressedCallback.setEnabled(false);

        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        categoryButtonsGroup.setVisibility(View.VISIBLE);
        aiChatRecyclerView.setVisibility(View.GONE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.connect(R.id.ai_chat_recycler_view, ConstraintSet.TOP, R.id.btn_news, ConstraintSet.BOTTOM, 8);
        aiMessageEditText.clearFocus();
        TransitionManager.beginDelayedTransition(mainLayout);
        constraintSet.applyTo(mainLayout);
    }
}