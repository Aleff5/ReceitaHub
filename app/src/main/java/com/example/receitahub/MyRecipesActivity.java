package com.example.receitahub;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Importe a Toolbar
import androidx.viewpager2.widget.ViewPager2;

import com.example.receitahub.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        // --- ADICIONADO: Lógica da Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_my_recipes);
        // Configura o listener de clique para o ícone de navegação (a seta de voltar)
        toolbar.setNavigationOnClickListener(v -> {
            // finish() fecha a tela atual e volta para a anterior (MainActivity)
            finish();
        });
        // --- FIM DA LÓGICA DA TOOLBAR ---

        // Encontra os componentes do layout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // Cria e configura o adapter para as abas
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Conecta as abas ao ViewPager e define os títulos de cada uma
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 1) {
                    tab.setText("Favoritas");
                } else {
                    tab.setText("Minhas Criações");
                }
            }
        }).attach();
    }
}