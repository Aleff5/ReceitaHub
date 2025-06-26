package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // Componentes da UI
    private ConstraintLayout mainLayout;
    private Group categoryButtonsGroup;
    private SearchView mainSearchView;
    private Button btnPopularRecipes, btnQuickRecipes, btnHealthyRecipes, btnVeganRecipes, btnDailyRecipe, btnNews,
            btnBreakfast, btnLunch, btnDinner, btnDessert; // Botões adicionados
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

        sessionManager = new SessionManager(getApplicationContext());
        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();
        geminiService = new GeminiService();
        geminiService.iniciarChat();

        iniciarComponentes();
        configurarListeners();
        configurarChatRecyclerView();

        onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                showHomeMode();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            if (isChatModeActive) {
                bottomNavigation.setSelectedItemId(R.id.navigation_ai_chat);
            } else {
                bottomNavigation.setSelectedItemId(R.id.navigation_home);
            }
        }
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
        // ADICIONADO: Mapeamento dos novos botões
        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnLunch = findViewById(R.id.btn_lunch);
        btnDinner = findViewById(R.id.btn_dinner);
        btnDessert = findViewById(R.id.btn_dessert);

        aiChatRecyclerView = findViewById(R.id.ai_chat_recycler_view);
        aiMessageEditText = findViewById(R.id.ai_message_edit_text);
        aiSendButton = findViewById(R.id.ai_send_button);
        aiLoadingIndicator = findViewById(R.id.ai_loading_indicator);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void configurarListeners() {
        View.OnClickListener categoryClickListener = v -> {
            if (!isChatModeActive) {
                showChatMode();
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
        // ADICIONADO: Configuração dos listeners para os novos botões
        btnBreakfast.setOnClickListener(categoryClickListener);
        btnLunch.setOnClickListener(categoryClickListener);
        btnDinner.setOnClickListener(categoryClickListener);
        btnDessert.setOnClickListener(categoryClickListener);

        aiMessageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !isChatModeActive) {
                showChatMode();
            }
        });

        aiSendButton.setOnClickListener(v -> {
            String mensagemTexto = aiMessageEditText.getText().toString().trim();
            if (!mensagemTexto.isEmpty()) {
                enviarMensagem(mensagemTexto);
            }
        });

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

        chatAdapter.setOnFavoriteClickListener(mensagem -> {
            if (sessionManager.isLoggedIn()) {
                salvarReceitaFavorita(mensagem.getTexto(), sessionManager.getUserId());
            } else {
                Toast.makeText(this, "Faça login para salvar receitas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarMensagem(String texto) {
        if (!sessionManager.isLoggedIn() && sessionManager.getChatInteractionCount() >= 5) {
            Toast.makeText(this, "Você atingiu o limite de 5 mensagens. Faça login para continuar.", Toast.LENGTH_LONG).show();
            return;
        }

        Mensagem mensagemUsuario = new Mensagem(texto, true);
        mensagemList.add(mensagemUsuario);
        chatAdapter.notifyItemInserted(mensagemList.size() - 1);
        aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);

        aiMessageEditText.setText("");
        aiLoadingIndicator.setVisibility(View.VISIBLE);

        geminiService.enviarMensagemNoChat(texto, executor, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (!sessionManager.isLoggedIn()) {
                    sessionManager.incrementChatInteractionCount();
                }

                boolean isRecipeMessage = response.contains("###TÍTULO###") || response.contains("##TÍTULO##")
                        || response.contains("###INGREDIENTES###") || response.contains("##INGREDIENTES##")
                        || response.contains("###MODOPREPARO###") || response.contains("##MODOPREPARO##");

                String finalResponse = response.replace("##TÍTULO##", "Título:")
                        .replace("###TÍTULO###", "Título:")
                        .replace("##INGREDIENTES##", "\n\nIngredientes:")
                        .replace("###INGREDIENTES###", "\n\nIngredientes:")
                        .replace("##MODOPREPARO##", "\n\nModo de Preparo:")
                        .replace("###MODOPREPARO###", "\n\nModo de Preparo:");

                runOnUiThread(() -> {
                    aiLoadingIndicator.setVisibility(View.GONE);
                    Mensagem mensagemIA = new Mensagem(finalResponse, false, isRecipeMessage);
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

    // ALTERADO: Lógica de extração de texto reescrita para ser mais robusta
    private void salvarReceitaFavorita(String rawResponse, long userId) {
        try {
            // Primeiro, removemos os marcadores de formatação para limpar o texto
            String cleanedResponse = rawResponse.replace("###", "").replace("##", "").replace("#", "");

            String titulo = "";
            String ingredientes = "";
            String modoPreparo = "";

            // Define os delimitadores que separam as seções da receita
            String tituloDelim = "Título:";
            String ingredientesDelim = "Ingredientes:";
            String modoPreparoDelim = "Modo de Preparo:";

            // A IA pode não enviar a palavra "Título:", então tratamos os dois casos
            if (cleanedResponse.contains(tituloDelim)) {
                // Se tiver "Título:", extrai a partir daí
                int idxTitulo = cleanedResponse.indexOf(tituloDelim);
                int idxIngredientes = cleanedResponse.indexOf(ingredientesDelim);
                if (idxTitulo != -1 && idxIngredientes != -1) {
                    titulo = cleanedResponse.substring(idxTitulo + tituloDelim.length(), idxIngredientes).trim();
                }
            } else {
                // Se não, assume que a primeira linha é o título
                int firstLineBreak = cleanedResponse.indexOf("\n");
                if (firstLineBreak != -1) {
                    titulo = cleanedResponse.substring(0, firstLineBreak).trim();
                } else {
                    // Caso de emergência se não houver quebra de linha
                    titulo = cleanedResponse.split(ingredientesDelim)[0].trim();
                }
            }

            // Extrai ingredientes e modo de preparo
            int idxIngredientes = cleanedResponse.indexOf(ingredientesDelim);
            int idxModoPreparo = cleanedResponse.indexOf(modoPreparoDelim);

            if (idxIngredientes != -1 && idxModoPreparo != -1) {
                ingredientes = cleanedResponse.substring(idxIngredientes + ingredientesDelim.length(), idxModoPreparo).trim();
                modoPreparo = cleanedResponse.substring(idxModoPreparo + modoPreparoDelim.length()).trim();
            }

            // Validação final
            if (titulo.isEmpty() || ingredientes.isEmpty() || modoPreparo.isEmpty()) {
                throw new IllegalArgumentException("Falha ao extrair uma ou mais seções da receita.");
            }

            Receita receitaFavorita = new Receita(userId, titulo, ingredientes, modoPreparo, "GERADA");
            receitaFavorita.isFavorita = true;
            executor.execute(() -> {
                receitaDao.salvarReceita(receitaFavorita);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Receita salva nos favoritos!", Toast.LENGTH_SHORT).show());
            });

        } catch (Exception e) {
            Log.e("DEBUG_RECEITA", "Erro ao extrair dados da receita.", e);
            Toast.makeText(this, "Erro ao processar a receita. O formato é inválido.", Toast.LENGTH_LONG).show();
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
        constraintSet.connect(R.id.ai_chat_recycler_view, ConstraintSet.TOP, R.id.btn_dessert, ConstraintSet.BOTTOM, 8); // Ajustado para o último botão
        aiMessageEditText.clearFocus();
        TransitionManager.beginDelayedTransition(mainLayout);
        constraintSet.applyTo(mainLayout);
    }
}