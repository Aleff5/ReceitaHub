package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import Button
import android.widget.EditText; // Import EditText (if you add logic for them)
import android.widget.ImageView; // Import ImageView (for back button)
import android.widget.TextView; // Import TextView (for back to login text)
import android.widget.Toast; // Import Toast for messages

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivRegisterBackButton; // Back button on register screen
    private TextView tvBackToLogin; // Text view to go back to login
    private Button btnRegisterAction; // The "Cadastrar" button

    // You might also declare EditTexts here if you plan to get user input:
    // private EditText etRegisterEmail;
    // private EditText etRegisterPassword;
    // private EditText etRegisterConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register); // Make sure this is your register layout

        // Apply window insets (assuming your layout has a root with ID 'register_root_layout')
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        ivRegisterBackButton = findViewById(R.id.iv_register_back_button);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
        btnRegisterAction = findViewById(R.id.btn_register_action); // Initialize the Cadastrar button

        // Initialize EditTexts if you declared them
        // etRegisterEmail = findViewById(R.id.et_register_email);
        // etRegisterPassword = findViewById(R.id.et_register_password);
        // etRegisterConfirmPassword = findViewById(R.id.et_register_confirm_password);


        // Set up click listener for the back button (top-left)
        ivRegisterBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Option 1: Go back to the previous activity (likely ProfileEditActivity)
                finish();
                // Option 2: Explicitly go back to ProfileEditActivity (login screen)
                // Intent intent = new Intent(RegisterActivity.this, ProfileEditActivity.class);
                // startActivity(intent);
                // finish();
            }
        });

        // Set up click listener for "Já tem uma conta? Faça Login."
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the login screen (ProfileEditActivity)
                Intent intent = new Intent(RegisterActivity.this, ProfileEditActivity.class);
                startActivity(intent);
                finish(); // Finish this RegisterActivity
            }
        });


        // --- THIS IS THE NEW PART FOR THE "CADASTRAR" BUTTON ---
        btnRegisterAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here you'll add the logic for creating the account.
                // For now, let's just show a Toast message and then go back to the login screen.

                // Example: Get input from EditTexts (uncomment if you use them)
                // String email = etRegisterEmail.getText().toString().trim();
                // String password = etRegisterPassword.getText().toString().trim();
                // String confirmPassword = etRegisterConfirmPassword.getText().toString().trim();

                // Add your account creation logic here (e.g., Firebase, API call, local storage)
                // For demonstration, let's just show a toast.
                Toast.makeText(RegisterActivity.this, "Conta Cadastrada com Sucesso!", Toast.LENGTH_SHORT).show();

                // After successful registration, you typically want to send the user back to the login screen
                // or directly to the main app screen if they are automatically logged in.
                Intent intent = new Intent(RegisterActivity.this, ProfileEditActivity.class); // Go back to login
                // Or to MainActivity: Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the RegisterActivity
            }
        });
    }
}