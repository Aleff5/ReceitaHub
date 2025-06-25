package com.example.receitahub;

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
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Mapeia os componentes do layout
        ivBackButton = findViewById(R.id.iv_detail_back_button);
        tvToolbarTitle = findViewById(R.id.tv_detail_recipe_title_toolbar);
        tvMealType = findViewById(R.id.tv_detail_meal_type);
        tvIngredients = findViewById(R.id.tv_detail_ingredients);
        tvInstructions = findViewById(R.id.tv_detail_instructions);

        // Configura o botão de voltar
        ivBackButton.setOnClickListener(v -> finish());

        // Pega o ID da receita que foi passado pela tela anterior
        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        } else {
            Toast.makeText(this, "Erro ao carregar receita.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadRecipeDetails(int recipeId) {
        AppDatabase db = AppDatabase.getDatabase(this);
        executor.execute(() -> {
            // Busca a receita no banco de dados
            Receita receita = db.receitaDao().getReceitaPorId(recipeId);

            // Volta para a thread principal para atualizar a UI
            runOnUiThread(() -> {
                if (receita != null) {
                    // Define o título no nosso novo TextView
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