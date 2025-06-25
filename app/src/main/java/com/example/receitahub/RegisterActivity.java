package com.example.receitahub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    // ADICIONADO: Campo para o nome
    private EditText etRegisterName, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    private Button btnRegisterAction;
    private TextView tvBackToLogin;
    private ImageView ivRegisterBackButton;

    private UserDao userDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();

        // ADICIONADO: Mapeamento do novo campo
        etRegisterName = findViewById(R.id.et_register_name);
        etRegisterEmail = findViewById(R.id.et_register_email);
        etRegisterPassword = findViewById(R.id.et_register_password);
        etRegisterConfirmPassword = findViewById(R.id.et_register_confirm_password);
        btnRegisterAction = findViewById(R.id.btn_register_action);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
        ivRegisterBackButton = findViewById(R.id.iv_register_back_button);

        setupListeners();
    }

    private void setupListeners() {
        btnRegisterAction.setOnClickListener(v -> registerUser());
        tvBackToLogin.setOnClickListener(v -> finish());
        ivRegisterBackButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        // ADICIONADO: Leitura do campo de nome
        String nome = etRegisterName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etRegisterConfirmPassword.getText().toString().trim();

        // ADICIONADO: Validação do nome
        if (TextUtils.isEmpty(nome)) {
            etRegisterName.setError("O nome é obrigatório.");
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Por favor, insira um email válido.");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etRegisterPassword.setError("A senha deve ter no mínimo 6 caracteres.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etRegisterConfirmPassword.setError("As senhas não coincidem.");
            return;
        }

        executor.execute(() -> {
            if (userDao.getUserByEmail(email) != null) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Este email já está cadastrado.", Toast.LENGTH_SHORT).show());
                return;
            }

            // ALTERADO: A chamada do construtor agora inclui o nome
            User newUser = new User(nome, email, password);
            userDao.insertUser(newUser);

            runOnUiThread(() -> {
                Toast.makeText(RegisterActivity.this, "Conta criada com sucesso! Faça o login.", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }
}