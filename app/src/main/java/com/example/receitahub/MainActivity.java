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

    // Flag para saber se a próxima resposta da IA é uma lista de sugestões
    private boolean isAwaitingSuggestionList = false;

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
        // MUDANÇA 1: Novo listener para os botões de categoria
        View.OnClickListener categoryClickListener = v -> {
            Button b = (Button) v;
            String categoria = b.getText().toString();
            // Inicia a conversa com a IA pedindo sugestões para a categoria clicada
            pedirSugestoesParaCategoria(categoria);
        };

        btnPopularRecipes.setOnClickListener(categoryClickListener);
        btnQuickRecipes.setOnClickListener(categoryClickListener);
        btnHealthyRecipes.setOnClickListener(categoryClickListener);
        btnVeganRecipes.setOnClickListener(categoryClickListener);
        btnDailyRecipe.setOnClickListener(categoryClickListener);
        btnNews.setOnClickListener(categoryClickListener);

        // Listener para o botão de enviar mensagem (sem alteração)
        aiSendButton.setOnClickListener(v -> {
            String mensagemTexto = aiMessageEditText.getText().toString().trim();
            if (!mensagemTexto.isEmpty()) {
                // Ao enviar uma mensagem, assumimos que não estamos mais esperando uma lista.
                isAwaitingSuggestionList = false;
                enviarMensagem(mensagemTexto);
            }
        });

        // Listener para a barra de navegação inferior (sem alteração)
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

    // MUDANÇA 2: Novo método para iniciar a conversa a partir de um botão de categoria
    private void pedirSugestoesParaCategoria(String categoria) {
        // Define que a próxima resposta da IA deve ser tratada como uma lista de sugestões
        isAwaitingSuggestionList = true;

        // Exibe a ação do usuário no chat
        String textoUsuario = "Me dê sugestões de " + categoria.toLowerCase();
        Mensagem mensagemUsuario = new Mensagem(textoUsuario, true);
        mensagemList.add(mensagemUsuario);
        chatAdapter.notifyItemInserted(mensagemList.size() - 1);
        aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);

        // Chama o método `enviarMensagem` para fazer a requisição à IA
        enviarMensagem(textoUsuario);
    }

    private void enviarMensagem(String texto) {
        if (!sessionManager.isLoggedIn() && sessionManager.getChatInteractionCount() >= 5) {
            Toast.makeText(this, "Você atingiu o limite de 5 mensagens. Faça login para continuar.", Toast.LENGTH_LONG).show();
            return;
        }

        // Se a mensagem não for de um clique de botão, adiciona ao chat.
        // Se for de um clique, já foi adicionada em `pedirSugestoesParaCategoria`.
        boolean isFromCategoryClick = isAwaitingSuggestionList;
        if (!isFromCategoryClick) {
            Mensagem mensagemUsuario = new Mensagem(texto, true);
            mensagemList.add(mensagemUsuario);
            chatAdapter.notifyItemInserted(mensagemList.size() - 1);
            aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);
        }

        aiMessageEditText.setText("");
        aiLoadingIndicator.setVisibility(View.VISIBLE);

        // MUDANÇA 3: O prompt agora depende se estamos pedindo uma lista ou uma receita completa
        String prompt;
        if (isAwaitingSuggestionList) {
            prompt = "Aja como um assistente de culinária. Liste 5 nomes de '" + texto + "', um por linha, e nada mais. Exemplo: 'Nome da Receita 1\\nNome da Receita 2'";
        } else {
            prompt = "Aja como um assistente de culinária. Crie uma receita completa baseada no seguinte pedido do usuário: '" + texto + "'. " +
                    "Formate sua resposta da seguinte maneira, e nada mais: " +
                    "###TÍTULO### [Título da Receita] " +
                    "###INGREDIENTES### [Lista de ingredientes, um por linha] " +
                    "###MODOPREPARO### [Passos do modo de preparo]";
        }

        geminiService.gerarConteudo(prompt, executor, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                if (!sessionManager.isLoggedIn()) {
                    sessionManager.incrementChatInteractionCount();
                }

                String finalResponse;
                // MUDANÇA 4: Trata a resposta de forma diferente se for uma lista
                if (isAwaitingSuggestionList) {
                    finalResponse = "Claro! Aqui estão algumas sugestões:\n\n" + response;
                    // Depois de receber a lista, a próxima mensagem será uma receita completa
                    isAwaitingSuggestionList = false;
                } else {
                    if (sessionManager.isLoggedIn()) {
                        salvarReceitaGerada(response, sessionManager.getUserId());
                    }
                    finalResponse = response.replace("###TÍTULO###", "Título:")
                            .replace("###INGREDIENTES###", "\n\nIngredientes:")
                            .replace("###MODOPREPARO###", "\n\nModo de Preparo:");
                }

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
                    isAwaitingSuggestionList = false; // Reseta a flag em caso de erro
                    Toast.makeText(MainActivity.this, "Falha ao contatar a IA. Tente novamente.", Toast.LENGTH_LONG).show();
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
}