package com.example.receitahub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.AppDatabase;
import com.example.receitahub.db.dao.ReceitaDao;
import com.example.receitahub.util.SessionManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText etRecipeName, etIngredients, etModoDePreparo;
    private RadioGroup rgMealType;
    private Button btnAddRecipe;
    private ImageView ivBackButton;
    private TextView tvAddRecipeTitle;

    private ReceitaDao receitaDao;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private int recipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();
        sessionManager = new SessionManager(getApplicationContext());

        etRecipeName = findViewById(R.id.et_recipe_name);
        rgMealType = findViewById(R.id.rg_meal_type);
        etIngredients = findViewById(R.id.et_ingredients);
        etModoDePreparo = findViewById(R.id.et_modo_de_preparo);
        btnAddRecipe = findViewById(R.id.btn_add_recipe);
        ivBackButton = findViewById(R.id.iv_back_button);
        tvAddRecipeTitle = findViewById(R.id.tv_add_recipe_title);

        this.recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        if (this.recipeId != -1) {
            tvAddRecipeTitle.setText("Editar Receita");
            btnAddRecipe.setText("Salvar Alterações");
            loadRecipeData();
        } else {
            tvAddRecipeTitle.setText("Adicionar Nova Receita");
        }


        ivBackButton.setOnClickListener(v -> finish());
        btnAddRecipe.setOnClickListener(v -> saveRecipe());
    }

    private void loadRecipeData() {
        executor.execute(() -> {
            Receita receitaExistente = receitaDao.getReceitaById(recipeId);

            if (receitaExistente != null) {
                runOnUiThread(() -> {
                    etRecipeName.setText(receitaExistente.titulo);
                    etIngredients.setText(receitaExistente.ingredientes);
                    etModoDePreparo.setText(receitaExistente.modoDePreparo);

                    if (receitaExistente.mealType != null) {
                        for (int i = 0; i < rgMealType.getChildCount(); i++) {
                            RadioButton rb = (RadioButton) rgMealType.getChildAt(i);
                            if (rb.getText().toString().equalsIgnoreCase(receitaExistente.mealType)) {
                                rb.setChecked(true);
                                break;
                            }
                        }
                    }
                });
            }
        });
    }

    private void saveRecipe() {
        String recipeName = etRecipeName.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String modoDePreparo = etModoDePreparo.getText().toString().trim();
        String mealType = "";

        int selectedId = rgMealType.getCheckedRadioButtonId();
        if (selectedId != -1) {
            mealType = ((RadioButton) findViewById(selectedId)).getText().toString();
        }

        if (recipeName.isEmpty() || ingredients.isEmpty() || mealType.isEmpty() || modoDePreparo.isEmpty()) {
            Toast.makeText(this, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Você precisa estar logado para salvar uma receita.", Toast.LENGTH_LONG).show();
            return;
        }

        long userId = sessionManager.getUserId();

        Receita receita = new Receita(userId, recipeName, ingredients, modoDePreparo, "CRIADA");
        receita.mealType = mealType;

        executor.execute(() -> {
            if (recipeId != -1) {
                receita.id = this.recipeId;

                receitaDao.update(receita);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Receita atualizada com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                });
            } else {
                receitaDao.salvarReceita(receita);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Receita adicionada com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
}