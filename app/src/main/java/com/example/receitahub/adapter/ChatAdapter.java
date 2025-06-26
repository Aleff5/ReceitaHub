package com.example.receitahub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.receitahub.R;
import com.example.receitahub.data.model.Mensagem;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Mensagem> mensagemList;
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Mensagem mensagem);
    }
    private OnFavoriteClickListener favoriteClickListener;

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public ChatAdapter(List<Mensagem> mensagemList) {
        this.mensagemList = mensagemList;
    }

    @Override
    public int getItemViewType(int position) {
        return mensagemList.get(position).isEnviadoPeloUsuario() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_ai, parent, false);
            return new AiMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensagem mensagem = mensagemList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserMessageViewHolder) holder).bind(mensagem);
        } else {
            ((AiMessageViewHolder) holder).bind(mensagem);
        }
    }

    @Override
    public int getItemCount() {
        return mensagemList.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_chat_message);
        }
        void bind(Mensagem mensagem) {
            messageText.setText(mensagem.getTexto());
        }
    }

    class AiMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageButton favoriteButton;
        AiMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_chat_message);
            favoriteButton = itemView.findViewById(R.id.btn_favorite_recipe);
        }

        void bind(Mensagem mensagem) {
            String textoMensagem = mensagem.getTexto();
            messageText.setText(textoMensagem);

            // LINHA ALTERADA: Lógica de verificação agora é case-insensitive e usa a pontuação correta (ponto e vírgula),
            // alinhando-se perfeitamente com a lógica de salvamento no MainActivity.
            String lowerCaseText = textoMensagem.toLowerCase();
            if (lowerCaseText.contains("ingredientes:") && lowerCaseText.contains("modo de preparo;")) {
                favoriteButton.setVisibility(View.VISIBLE);
                favoriteButton.setImageResource(R.drawable.ic_favorite);
                favoriteButton.setEnabled(true);

                favoriteButton.setOnClickListener(v -> {
                    if (favoriteClickListener != null) {
                        favoriteClickListener.onFavoriteClick(mensagem);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
                        favoriteButton.setEnabled(false);
                    }
                });
            } else {
                favoriteButton.setVisibility(View.GONE);
            }
        }
    }
}