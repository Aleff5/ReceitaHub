package com.example.receitahub;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa os componentes da UI
        iniciarComponentes();

        // Configura os listeners de clique
        configurarListeners();

        // Configura o RecyclerView para o chat
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

        // Torna o RecyclerView visível para o chat
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
            if (itemId == R.id.nav_home) {
                Toast.makeText(this, "Home Clicado", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Toast.makeText(this, "Favoritos Clicado", Toast.LENGTH_SHORT).show();
                // Exemplo de como iniciar uma nova Activity
                // Intent intent = new Intent(MainActivity.this, FavoritosActivity.class);
                // startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Perfil Clicado", Toast.LENGTH_SHORT).show();
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
        // Adiciona a mensagem do usuário à lista e notifica o adapter
        Mensagem mensagemUsuario = new Mensagem(texto, true);
        mensagemList.add(mensagemUsuario);
        chatAdapter.notifyItemInserted(mensagemList.size() - 1);
        aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);

        // Limpa o campo de texto
        aiMessageEditText.setText("");

        // Mostra o indicador de carregamento e simula a resposta da IA
        aiLoadingIndicator.setVisibility(View.VISIBLE);

        // Simulação de uma chamada de API ou processamento da IA
        // Substitua este trecho pela sua lógica real de integração com a IA
        new android.os.Handler().postDelayed(() -> {
            aiLoadingIndicator.setVisibility(View.GONE);
            String respostaIA = "Olá! Eu sou sua assistente de receitas. Como posso ajudar com '" + texto + "' hoje?";
            Mensagem mensagemIA = new Mensagem(respostaIA, false);
            mensagemList.add(mensagemIA);
            chatAdapter.notifyItemInserted(mensagemList.size() - 1);
            aiChatRecyclerView.scrollToPosition(mensagemList.size() - 1);
        }, 2000); // Atraso de 2 segundos para simular a resposta
    }
}