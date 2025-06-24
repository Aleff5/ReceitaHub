package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageView ivBackButton;
    private TextView tvToggleAuthMode; // Declare the TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBackButton = findViewById(R.id.iv_back_button);
        tvToggleAuthMode = findViewById(R.id.tv_toggle_auth_mode); // Initialize the TextView

        // Set up the back button click listener
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up the click listener for "Não tem uma conta? Cadastre-se."
        tvToggleAuthMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to go to RegisterActivity
                Intent intent = new Intent(ProfileEditActivity.this, RegisterActivity.class);
                startActivity(intent);
                // Optionally, you might want to finish() ProfileEditActivity here
                // if you don't want the user to go back to the login screen using the back button
                // finish();
            }
        });

        // ... rest of your ProfileEditActivity onCreate logic (e.g., for login fields/button) ...
    }
}