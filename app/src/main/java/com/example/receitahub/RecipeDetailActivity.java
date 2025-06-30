package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.AppDatabase;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvMealType, tvIngredients, tvInstructions, tvToolbarTitle;
    private ImageView ivBackButton;
    private ImageView ivEditButton; // 1. Variável para o novo botão de editar

    private final Executor executor = Executors.newSingleThreadExecutor();
    private int recipeId = -1; // 2. Variável para guardar o ID da receita atual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Mapeia os componentes do layout
        ivBackButton = findViewById(R.id.iv_detail_back_button);
        ivEditButton = findViewById(R.id.iv_detail_edit_button); // Mapeia o novo botão
        tvToolbarTitle = findViewById(R.id.tv_detail_recipe_title_toolbar);
        tvMealType = findViewById(R.id.tv_detail_meal_type);
        tvIngredients = findViewById(R.id.tv_detail_ingredients);
        tvInstructions = findViewById(R.id.tv_detail_instructions);

        ivBackButton.setOnClickListener(v -> finish());

        // 3. Pega o ID da receita e guarda na variável da classe
        this.recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        if (this.recipeId != -1) {
            // Se o ID é válido, carrega os detalhes
            loadRecipeDetails(this.recipeId);

            // 4. Adiciona a lógica de clique ao botão de editar
            ivEditButton.setOnClickListener(v -> {
                // Cria uma intenção para abrir a tela de formulário
                Intent intent = new Intent(RecipeDetailActivity.this, AddRecipeActivity.class);
                // Anexa o ID da receita atual a essa intenção
                intent.putExtra("RECIPE_ID", this.recipeId);
                // Inicia a tela de formulário, que agora saberá que está em modo de edição
                startActivity(intent);
            });

        } else {
            // Se o ID for inválido, mostra um erro e fecha a tela
            Toast.makeText(this, "Erro ao carregar receita.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadRecipeDetails(int recipeId) {
        AppDatabase db = AppDatabase.getDatabase(this);
        executor.execute(() -> {
            Receita receita = db.receitaDao().getReceitaById(recipeId);

            runOnUiThread(() -> {
                if (receita != null) {
                    tvToolbarTitle.setText(receita.titulo);
                    tvMealType.setText(receita.mealType != null ? receita.mealType : "Não especificado");
                    tvIngredients.setText(receita.ingredientes);
                    tvInstructions.setText(receita.modoDePreparo);
                } else {
                    Toast.makeText(this, "Receita não encontrada.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
}