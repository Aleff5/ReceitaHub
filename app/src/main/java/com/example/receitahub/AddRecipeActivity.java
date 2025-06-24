package com.example.receitahub;

import android.content.Intent; // Import Intent for explicit navigation
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast; // For simple messages

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddRecipeActivity extends AppCompatActivity {

    // Declare UI elements as member variables so they can be accessed throughout the Activity
    private EditText etRecipeName;
    private RadioGroup rgMealType;
    private RadioButton rbBreakfast, rbLunch, rbSnack, rbDinner;
    private EditText etIngredients;
    private Button btnAddRecipe;
    private ImageView ivBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.addrecipeactivity); // Link this Activity to its XML layout

        // Apply window insets to handle system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_recipe_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements by finding them by their IDs from the layout
        etRecipeName = findViewById(R.id.et_recipe_name);
        rgMealType = findViewById(R.id.rg_meal_type);
        rbBreakfast = findViewById(R.id.rb_breakfast);
        rbLunch = findViewById(R.id.rb_lunch);
        rbSnack = findViewById(R.id.rb_snack);
        rbDinner = findViewById(R.id.rb_dinner);
        etIngredients = findViewById(R.id.et_ingredients);
        btnAddRecipe = findViewById(R.id.btn_add_recipe);
        ivBackButton = findViewById(R.id.iv_back_button);

        // Set up the back button click listener to explicitly go to MainActivity
        ivBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddRecipeActivity.this, MainActivity.class); // Create an Intent to go to MainActivity
            startActivity(intent); // Start MainActivity
            finish(); // Finish the current activity
        });

        // Set up the "Adicionar Receita" button click listener
        btnAddRecipe.setOnClickListener(v -> {
            addRecipe(); // Call a method to handle recipe addition logic
        });
    }

    /**
     * This method handles the logic for adding a new recipe.
     * It retrieves data from the UI elements and can then process it (e.g., save to a database).
     */
    private void addRecipe() {
        // 1. Get the recipe name
        String recipeName = etRecipeName.getText().toString().trim();

        // 2. Get the selected meal type
        String mealType = "";
        int selectedId = rgMealType.getCheckedRadioButtonId();
        if (selectedId != -1) { // Check if a radio button is selected
            RadioButton selectedRadioButton = findViewById(selectedId);
            mealType = selectedRadioButton.getText().toString();
        }

        // 3. Get the ingredients list
        String ingredients = etIngredients.getText().toString().trim();

        // Basic validation: Check if essential fields are not empty
        if (recipeName.isEmpty()) {
            etRecipeName.setError("Nome da receita é obrigatório");
            return; // Stop execution if validation fails
        }
        if (mealType.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um tipo de refeição", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ingredients.isEmpty()) {
            etIngredients.setError("A lista de ingredientes é obrigatória");
            return;
        }

        // At this point, you have all the data: recipeName, mealType, ingredients
        // You would typically save this data to a database (e.g., Firebase Firestore, SQLite)
        // For now, let's just show a Toast message with the collected data.
        String message = "Receita Adicionada!\n" +
                "Nome: " + recipeName + "\n" +
                "Tipo: " + mealType + "\n" +
                "Ingredientes:\n" + ingredients;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Optionally, clear the fields after adding or navigate back to the main screen
        // finish(); // Uncomment to close this activity after adding recipe
    }
}