package com.example.receitahub;

import android.content.Intent; // Importe a classe Intent para iniciar novas Activities
import android.os.Bundle;
import android.view.MenuItem; // Importe a classe MenuItem para lidar com os itens do menu

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull; // Importe a anotação NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importe a classe BottomNavigationView do Material Design
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Declare uma variável para a BottomNavigationView para que ela possa ser acessada em onCreate
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ativa o modo EdgeToEdge para o app
        setContentView(R.layout.activity_main); // Define o layout da Activity

        // Configura o listener para aplicar insets de janela (para lidar com barras de sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Encontra a BottomNavigationView no layout usando seu ID
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configura o listener para os cliques nos itens da BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtém o ID do item do menu que foi selecionado
                int itemId = item.getItemId();

                // Usa um switch-case (ou if-else if) para lidar com cada item do menu
                if (itemId == R.id.navigation_home) {
                    // Lógica para o item "Início"
                    // Como esta é a MainActivity, talvez você não precise fazer muito aqui,
                    // a menos que queira resetar o estado da tela principal.
                    return true; // Retorna true para indicar que o item foi tratado
                } else if (itemId == R.id.navigation_ai_chat) {
                    // Lógica para o item "IA" (chat com a IA)
                    // Aqui você provavelmente vai querer alternar a visibilidade de views
                    // ou carregar um Fragment de chat.
                    // Exemplo:
                    // findViewById(R.id.scroll_view_container).setVisibility(View.GONE);
                    // findViewById(R.id.ai_chat_recycler_view).setVisibility(View.VISIBLE);
                    // findViewById(R.id.ai_input_container).setVisibility(View.VISIBLE);
                    return true;
                } else if (itemId == R.id.navigation_add_recipe) {
                    // Lógica para o item "Adicionar Receita"
                    // Você pode iniciar uma nova Activity aqui ou abrir um diálogo/fragmento de adição.
                    // Exemplo: startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_favorites) {
                    // Lógica para o item "Favoritos"
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    // Lógica para o item "Perfil"
                    // Cria uma Intent para iniciar a ProfileEditActivity
                    Intent intent = new Intent(MainActivity.this, ProfileEditActivity.class);
                    startActivity(intent); // Inicia a nova Activity
                    return true;
                }
                // Se nenhum dos itens conhecidos for selecionado, retorna false
                return false;
            }
        });

        // Opcional: Define o item "Início" como selecionado por padrão quando a Activity é criada
        // bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}