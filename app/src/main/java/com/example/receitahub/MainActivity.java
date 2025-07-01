package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Group homeContentGroup;
    private Button btnPopularRecipes, btnQuickRecipes, btnHealthyRecipes, btnVeganRecipes, btnDailyRecipe, btnNews,
            btnBreakfast, btnLunch, btnDinner, btnDessert;
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
        homeContentGroup = findViewById(R.id.home_content_group);
        btnPopularRecipes = findViewById(R.id.btn_popular_recipes);
        btnQuickRecipes = findViewById(R.id.btn_quick_recipes);
        btnHealthyRecipes = findViewById(R.id.btn_healthy_recipes);
        btnVeganRecipes = findViewById(R.id.btn_vegan_recipes);
        btnDailyRecipe = findViewById(R.id.btn_daily_recipe);
        btnNews = findViewById(R.id.btn_news);
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

        chatAdapter.setOnFavoriteClickListener((mensagem, position) -> {
            if (sessionManager.isLoggedIn()) {
                String mealType = "Não especificado";
                if (position > 0) {
                    Mensagem userMessage = mensagemList.get(position - 1);
                    mealType = extractMealTypeFromPrompt(userMessage.getTexto());
                }
                salvarReceitaFavorita(mensagem.getTexto(), sessionManager.getUserId(), mealType);
            } else {
                Toast.makeText(this, "Faça login para salvar receitas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String extractMealTypeFromPrompt(String prompt) {
        prompt = prompt.toLowerCase();
        if (prompt.contains("café da manhã")) return "Café da Manhã";
        if (prompt.contains("almoço")) return "Almoço";
        if (prompt.contains("jantar")) return "Jantar";
        if (prompt.contains("sobremesa")) return "Sobremesa";
        if (prompt.contains("lanche")) return "Lanche";
        return "Geral";
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

                String lowerCaseResponse = response.toLowerCase();
                boolean isRecipeMessage = (lowerCaseResponse.contains("título") || lowerCaseResponse.contains("ingredientes"))
                        && (lowerCaseResponse.contains("modo de preparo") || lowerCaseResponse.contains("modopreparo"));


                String finalResponse = response
                        .replace("###TÍTULO###", "Título:")
                        .replace("### Ingredientes ###", "\n\nIngredientes:")
                        .replace("###INGREDIENTES###", "\n\nIngredientes:")
                        .replace("### MODOPREPARO ###", "\n\nModo de Preparo:")
                        .replace("###MODOPREPARO###", "\n\nModo de Preparo:")
                        .replace("###FIM###", ""); // Remove o marcador de fim

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

    private void salvarReceitaFavorita(String rawResponse, long userId, String mealType) {
        try {
            String titulo = "";
            String ingredientes = "";
            String modoPreparo = "";

            Pattern titlePattern = Pattern.compile("Título:(.*?)(?=\\n\\nIngredientes:)", Pattern.DOTALL);
            Matcher titleMatcher = titlePattern.matcher(rawResponse);
            if (titleMatcher.find()) {
                titulo = titleMatcher.group(1).trim();
            } else {
                Pattern altTitlePattern = Pattern.compile("#{2,}(.*?)#{2,}");
                Matcher altTitleMatcher = altTitlePattern.matcher(rawResponse);
                if (altTitleMatcher.find()) {
                    titulo = altTitleMatcher.group(1).trim();
                }
            }

            String ingredientesDelim = "Ingredientes:";
            String modoPreparoDelim = "Modo de Preparo:";

            int idxIngredientes = rawResponse.indexOf(ingredientesDelim);
            int idxModoPreparo = rawResponse.indexOf(modoPreparoDelim);

            if (idxIngredientes != -1 && idxModoPreparo != -1) {
                ingredientes = rawResponse.substring(idxIngredientes + ingredientesDelim.length(), idxModoPreparo).trim();
                modoPreparo = rawResponse.substring(idxModoPreparo + modoPreparoDelim.length()).trim();
            }

            if (titulo.isEmpty() || ingredientes.isEmpty() || modoPreparo.isEmpty()) {
                throw new IllegalArgumentException("Falha ao extrair uma ou mais seções da receita.");
            }

            Receita receitaFavorita = new Receita(userId, titulo, ingredientes, modoPreparo, "GERADA");
            receitaFavorita.isFavorita = true;
            receitaFavorita.mealType = mealType;
            executor.execute(() -> {
                receitaDao.salvarReceita(receitaFavorita);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Receita salva nos favoritos!", Toast.LENGTH_SHORT).show());
            });
        } catch (Exception e) {
            Log.e("DEBUG_RECEITA", "Erro ao processar a receita.", e);
            Toast.makeText(this, "Erro ao processar a receita. O formato é inválido.", Toast.LENGTH_LONG).show();
        }
    }

    private void showChatMode() {
        if (isChatModeActive) return;
        isChatModeActive = true;
        onBackPressedCallback.setEnabled(true);
        bottomNavigation.setSelectedItemId(R.id.navigation_ai_chat);

        homeContentGroup.animate()
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    homeContentGroup.setVisibility(View.GONE);
                    aiChatRecyclerView.setAlpha(0f);
                    aiChatRecyclerView.setVisibility(View.VISIBLE);
                    aiChatRecyclerView.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setStartDelay(50)
                            .setInterpolator(new AccelerateDecelerateInterpolator());
                });

        if (mensagemList != null && mensagemList.isEmpty()) {
            Mensagem welcomeMessage = new Mensagem("Com que receita posso te ajudar hoje?", false);
            mensagemList.add(welcomeMessage);
            chatAdapter.notifyItemInserted(0);
        }
    }

    private void showHomeMode() {
        if (!isChatModeActive) return;
        isChatModeActive = false;
        onBackPressedCallback.setEnabled(false);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        aiMessageEditText.clearFocus();

        aiChatRecyclerView.animate()
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    aiChatRecyclerView.setVisibility(View.GONE);
                    homeContentGroup.setAlpha(0f);
                    homeContentGroup.setVisibility(View.VISIBLE);
                    homeContentGroup.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setStartDelay(50)
                            .setInterpolator(new AccelerateDecelerateInterpolator());
                });
    }
}