package com.example.receitahub;

import android.content.Intent;
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
import com.example.receitahub.data.model.Receita;
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

        AppDatabase db = AppDatabase.getDatabase(getContext());

        // ALTERADO: Usa a nova consulta para buscar apenas receitas marcadas como favoritas
        db.receitaDao().getFavoritas().observe(getViewLifecycleOwner(), receitas -> {
            adapter.submitList(receitas);
        });

        // Lógica de clique para cada item da lista (já estava correta)
        adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Receita receita) {
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra("RECIPE_ID", receita.id);
                startActivity(intent);
            }
        });
    }
}