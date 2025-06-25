package com.example.receitahub.adapter; // Ou o seu pacote principal, ex: com.example.receitahub

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.receitahub.CreatedRecipesFragment;
import com.example.receitahub.FavoritedRecipesFragment;

// A classe precisa estender FragmentStateAdapter, que é o tipo de adapter moderno para ViewPager2
public class ViewPagerAdapter extends FragmentStateAdapter {

    // O construtor recebe a Activity que hospeda o ViewPager
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Este método é chamado pelo ViewPager2 para obter o fragmento de uma posição específica.
     * @param position A posição da aba (0 para a primeira, 1 para a segunda, etc.)
     * @return O Fragmento que deve ser exibido.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Usamos um if/else ou switch para decidir qual fragmento retornar
        if (position == 1) {
            // Se a posição for 1 (segunda aba), retornamos o fragmento de receitas favoritas.
            return new FavoritedRecipesFragment();
        }
        // Se a posição for 0 (primeira aba) ou qualquer outra, retornamos o fragmento de receitas criadas.
        return new CreatedRecipesFragment();
    }

    /**
     * Este método informa ao ViewPager2 quantas abas/páginas teremos no total.
     * @return O número total de abas.
     */
    @Override
    public int getItemCount() {
        return 2; // Temos 2 abas: "Minhas Criações" e "Favoritas".
    }
}