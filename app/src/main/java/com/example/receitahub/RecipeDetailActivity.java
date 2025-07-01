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
    private ImageView ivEditButton;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private int recipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ivBackButton = findViewById(R.id.iv_detail_back_button);
        ivEditButton = findViewById(R.id.iv_detail_edit_button);
        tvToolbarTitle = findViewById(R.id.tv_detail_recipe_title_toolbar);
        tvMealType = findViewById(R.id.tv_detail_meal_type);
        tvIngredients = findViewById(R.id.tv_detail_ingredients);
        tvInstructions = findViewById(R.id.tv_detail_instructions);

        ivBackButton.setOnClickListener(v -> finish());

        this.recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        if (this.recipeId != -1) {
            loadRecipeDetails(this.recipeId);

            ivEditButton.setOnClickListener(v -> {
                Intent intent = new Intent(RecipeDetailActivity.this, AddRecipeActivity.class);
                intent.putExtra("RECIPE_ID", this.recipeId);
                startActivity(intent);
            });

        } else {
            Toast.makeText(this, "Erro ao carregar receita.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
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