package com.example.receitahub;

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

    // MODIFICADO: Adicionado EditText para modo de preparo
    private EditText etRecipeName, etIngredients, etModoDePreparo;
    private RadioGroup rgMealType;
    private Button btnAddRecipe;
    private ImageView ivBackButton;

    private ReceitaDao receitaDao;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Supondo que o nome do seu layout é activity_add_recipe.xml
        setContentView(R.layout.activity_add_recipe);

        // Inicializa DAO e SessionManager
        receitaDao = AppDatabase.getDatabase(getApplicationContext()).receitaDao();
        sessionManager = new SessionManager(getApplicationContext());

        // Mapeia componentes da UI
        etRecipeName = findViewById(R.id.et_recipe_name);
        rgMealType = findViewById(R.id.rg_meal_type);
        etIngredients = findViewById(R.id.et_ingredients);
        etModoDePreparo = findViewById(R.id.et_modo_de_preparo); // Mapeia o novo campo
        btnAddRecipe = findViewById(R.id.btn_add_recipe);
        ivBackButton = findViewById(R.id.iv_back_button);

        // Listeners
        ivBackButton.setOnClickListener(v -> finish());
        btnAddRecipe.setOnClickListener(v -> addRecipe());
    }

    private void addRecipe() {
        String recipeName = etRecipeName.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String modoDePreparo = etModoDePreparo.getText().toString().trim(); // Lê o modo de preparo
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
            Toast.makeText(this, "Você precisa estar logado para adicionar uma receita.", Toast.LENGTH_LONG).show();
            return;
        }

        long userId = sessionManager.getUserId();

        // Constrói o objeto Receita com o status "CRIADA"
        Receita novaReceita = new Receita(userId, recipeName, ingredients, modoDePreparo, "CRIADA");

        // ALTERAÇÃO PRINCIPAL: Atribui o tipo de refeição ao objeto antes de salvar
        novaReceita.mealType = mealType;

        executor.execute(() -> {
            receitaDao.salvarReceita(novaReceita);
            runOnUiThread(() -> {
                Toast.makeText(this, "Receita adicionada com sucesso!", Toast.LENGTH_LONG).show();
                finish(); // Fecha a tela e volta para a anterior
            });
        });
    }
}