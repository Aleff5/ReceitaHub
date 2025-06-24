package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receitahub.data.model.User;
import com.example.receitahub.db.AppDatabase;
import com.example.receitahub.db.dao.UserDao;
import com.example.receitahub.util.SessionManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnAuthAction;
    private TextView tvToggleAuthMode;
    private ImageView ivBackButton;

    private UserDao userDao;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Inicializa DAO e SessionManager
        userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();
        sessionManager = new SessionManager(getApplicationContext());

        // Se o usuário já estiver logado, vai direto para a MainActivity
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return; // Impede que o resto do código de login seja executado
        }

        // Mapeia os componentes da UI
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnAuthAction = findViewById(R.id.btn_auth_action); // Este é o botão de "Entrar"
        tvToggleAuthMode = findViewById(R.id.tv_toggle_auth_mode);
        ivBackButton = findViewById(R.id.iv_back_button);

        // Configura listeners
        setupListeners();
    }

    private void setupListeners() {
        btnAuthAction.setOnClickListener(v -> loginUser());

        tvToggleAuthMode.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        ivBackButton.setOnClickListener(v -> finish());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email e senha são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            // AVISO: Em um app real, a senha NUNCA deve ser comparada em texto puro.
            User user = userDao.getUserByEmailAndPassword(email, password);

            runOnUiThread(() -> {
                if (user != null) {
                    // Sucesso no login
                    sessionManager.saveSession(user.id);
                    Toast.makeText(ProfileEditActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    // Falha no login
                    Toast.makeText(ProfileEditActivity.this, "Email ou senha inválidos.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finaliza a tela de login para que o usuário não possa voltar a ela
    }
}