package com.example.receitahub;

import android.content.Intent; // IMPORTE ADICIONADO
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receitahub.adapter.RecipeAdapter;
import com.example.receitahub.data.model.Receita; // IMPORTE ADICIONADO
import com.example.receitahub.db.AppDatabase;

public class FavoritedRecipesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final RecipeAdapter adapter = new RecipeAdapter();
        recyclerView.setAdapter(adapter);

        // Busca e observa as receitas favoritadas (geradas pela IA)
        AppDatabase db = AppDatabase.getDatabase(getContext());
        db.receitaDao().findByStatus("GERADA").observe(getViewLifecycleOwner(), receitas -> {
            adapter.submitList(receitas);
        });

        // ADICIONADO: Lógica de clique para cada item da lista
        adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Receita receita) {
                // Cria uma Intent (intenção) para abrir a tela de detalhes
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                // Passa o ID da receita clicada para a próxima tela
                intent.putExtra("RECIPE_ID", receita.id);
                // Inicia a nova tela
                startActivity(intent);
            }
        });
    }
}