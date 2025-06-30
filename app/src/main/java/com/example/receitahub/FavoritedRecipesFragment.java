package com.example.receitahub;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receitahub.adapter.RecipeAdapter;
import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.AppDatabase;

public class FavoritedRecipesFragment extends Fragment implements RecipeAdapter.OnItemClickListener {

    private AppDatabase db;
    private RecipeAdapter adapter;

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

        adapter = new RecipeAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        db = AppDatabase.getDatabase(getContext());

        db.receitaDao().getFavoritas().observe(getViewLifecycleOwner(), receitas -> {
            adapter.submitList(receitas);
        });
    }

    @Override
    public void onItemClick(Receita receita) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("RECIPE_ID", receita.id);
        startActivity(intent);
    }

    // O MÉTODO onEditClick FOI REMOVIDO DESTA ÁREA

    @Override
    public void onDeleteClick(Receita receita) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir a receita \"" + receita.titulo + "\"?")
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.receitaDao().delete(receita);
                    });
                    Toast.makeText(getContext(), "Receita excluída", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}