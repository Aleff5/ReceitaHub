package com.example.receitahub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.receitahub.R;
import com.example.receitahub.data.model.Mensagem;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<Mensagem> mensagemList;

    public ChatAdapter(List<Mensagem> mensagemList) {
        this.mensagemList = mensagemList;
    }

    @Override
    public int getItemViewType(int position) {
        if (mensagemList.get(position).isEnviadaPeloUsuario()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_AI;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_user, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_ai, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Mensagem mensagem = mensagemList.get(position);
        holder.messageTextView.setText(mensagem.getTexto());
    }

    @Override
    public int getItemCount() {
        return mensagemList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_message_text);
        }
    }
}