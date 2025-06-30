package com.example.receitahub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.receitahub.R;
import com.example.receitahub.data.model.Receita;

public class RecipeAdapter extends ListAdapter<Receita, RecipeAdapter.RecipeViewHolder> {

    // --- ALTERAÇÕES APLICADAS AQUI ---
    public interface OnItemClickListener {
        // A LINHA "void onEditClick(Receita receita);" FOI REMOVIDA DAQUI
        void onDeleteClick(Receita receita);
        void onItemClick(Receita receita);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // --- FIM DAS ALTERAÇÕES ---

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

    // --- ALTERAÇÕES APLICADAS AQUI ---
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        // A VARIÁVEL "buttonEditar" FOI REMOVIDA
        private final ImageButton buttonDeletar;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tv_recipe_title);
            // O CÓDIGO DO "buttonEditar" (findViewById e setOnClickListener) FOI REMOVIDO
            buttonDeletar = itemView.findViewById(R.id.button_deletar);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            buttonDeletar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });
        }
    }
}