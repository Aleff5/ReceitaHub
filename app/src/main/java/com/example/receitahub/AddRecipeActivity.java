package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.AppDatabase;
import com.example.receitahub.db.dao.ReceitaDao;
import com.example.receitahub.util.SessionManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText etRecipeName, etIngredients;
    private RadioGroup rgMealType;
    private Button btnAddRecipe;
    private ImageView ivBackButton;

    private ReceitaDao receitaDao;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addrecipeactivity);

        // Inicializa DAO e SessionManager
        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();
        sessionManager = new SessionManager(getApplicationContext());

        // Mapeia componentes da UI
        etRecipeName = findViewById(R.id.et_recipe_name);
        rgMealType = findViewById(R.id.rg_meal_type);
        etIngredients = findViewById(R.id.et_ingredients);
        btnAddRecipe = findViewById(R.id.btn_add_recipe);
        ivBackButton = findViewById(R.id.iv_back_button);

        // Listeners
        ivBackButton.setOnClickListener(v -> finish());
        btnAddRecipe.setOnClickListener(v -> addRecipe());
    }

    private void addRecipe() {
        String recipeName = etRecipeName.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String mealType = "";
        int selectedId = rgMealType.getCheckedRadioButtonId();
        if (selectedId != -1) {
            mealType = ((RadioButton) findViewById(selectedId)).getText().toString();
        }

        if (recipeName.isEmpty() || ingredients.isEmpty() || mealType.isEmpty()) {
            Toast.makeText(this, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se o usuário está logado antes de salvar
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Você precisa estar logado para adicionar uma receita.", Toast.LENGTH_LONG).show();
            // Opcional: Redirecionar para a tela de login
            // startActivity(new Intent(this, ProfileEditActivity.class));
            return;
        }

        long userId = sessionManager.getUserId();
        String modoDePreparo = "Modo de preparo a ser adicionado."; // Você pode adicionar um campo para isso

        // Constrói o objeto Receita com o tipo "CRIADA"
        Receita novaReceita = new Receita(userId, recipeName, ingredients, modoDePreparo, "CRIADA");

        executor.execute(() -> {
            receitaDao.salvarReceita(novaReceita);
            runOnUiThread(() -> {
                Toast.makeText(this, "Receita adicionada com sucesso!", Toast.LENGTH_LONG).show();
                finish(); // Fecha a tela e volta para a anterior
            });
        });
    }
}