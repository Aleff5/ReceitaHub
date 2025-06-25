package com.example.receitahub; // ADICIONE ESTA LINHA

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
import com.example.receitahub.db.AppDatabase;

public class CreatedRecipesFragment extends Fragment {

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

        // Busca e observa as receitas criadas pelo usuário
        AppDatabase db = AppDatabase.getDatabase(getContext());
        // Supondo que receitas criadas por usuários tenham o status "CRIADA"
        // Se for diferente, ajuste a string aqui.
        db.receitaDao().findByStatus("CRIADA").observe(getViewLifecycleOwner(), receitas -> {
            adapter.submitList(receitas);
        });
    }
}