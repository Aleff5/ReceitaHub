package com.example.receitahub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.receitahub.R;
import com.example.receitahub.data.model.Receita;

public class RecipeAdapter extends ListAdapter<Receita, RecipeAdapter.RecipeViewHolder> {

    public RecipeAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Receita> DIFF_CALLBACK = new DiffUtil.ItemCallback<Receita>() {
        @Override
        public boolean areItemsTheSame(@NonNull Receita oldItem, @NonNull Receita newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Receita oldItem, @NonNull Receita newItem) {
            return oldItem.titulo.equals(newItem.titulo);
        }
    };

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Receita currentRecipe = getItem(position);
        holder.textViewTitle.setText(currentRecipe.titulo);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tv_recipe_title);
            // Futuramente, você pode adicionar um listener de clique aqui para
            // abrir os detalhes de uma receita quando o usuário clicar nela.
            // itemView.setOnClickListener(v -> ... );
        }
    }
}