package com.example.receitahub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.receitahub.data.model.User;
import com.example.receitahub.db.AppDatabase;
import com.example.receitahub.db.dao.UserDao;
import com.example.receitahub.util.SessionManager;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword, etName;
    private Button btnAuthAction;
    private TextView tvToggleAuthMode, tvAuthTitle;
    private ImageView ivBackButton;
    private ShapeableImageView ivProfilePicture;
    private LinearLayout layoutPasswordFields;
    private Uri selectedImageUri;

    private UserDao userDao;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private User currentUser;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProfilePicture.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();
        sessionManager = new SessionManager(getApplicationContext());

        iniciarComponentes();

        ivBackButton.setOnClickListener(v -> finish());

        if (sessionManager.isLoggedIn()) {
            setupProfileView();
        } else {
            setupLoginView();
        }
    }

    private void iniciarComponentes() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etName = findViewById(R.id.et_name);
        btnAuthAction = findViewById(R.id.btn_auth_action);
        tvToggleAuthMode = findViewById(R.id.tv_toggle_auth_mode);
        tvAuthTitle = findViewById(R.id.tv_auth_title);
        ivBackButton = findViewById(R.id.iv_back_button);
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        layoutPasswordFields = findViewById(R.id.layout_password_fields);
    }

    private void setupProfileView() {
        tvAuthTitle.setText("Editar Perfil");
        btnAuthAction.setText("Salvar Alterações");
        etPassword.setHint("Nova Senha (opcional)");

        ivProfilePicture.setVisibility(View.VISIBLE);
        etName.setVisibility(View.VISIBLE);
        layoutPasswordFields.setVisibility(View.VISIBLE);

        tvToggleAuthMode.setText("Sair (Logout)");

        long userId = sessionManager.getUserId();
        executor.execute(() -> {
            currentUser = userDao.getUserById(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    // ALTERADO: Acessando o campo diretamente
                    etName.setText(currentUser.nome);
                    etEmail.setText(currentUser.email);
                    etEmail.setEnabled(false);
                    // ALTERADO: Acessando o campo diretamente
                    if (currentUser.profilePictureUri != null) {
                        selectedImageUri = Uri.parse(currentUser.profilePictureUri);
                        ivProfilePicture.setImageURI(selectedImageUri);
                    }
                }
            });
        });

        ivProfilePicture.setOnClickListener(v -> getContent.launch("image/*"));
        btnAuthAction.setOnClickListener(v -> saveChanges());
        tvToggleAuthMode.setOnClickListener(v -> logoutUser());
    }

    private void saveChanges() {
        if (currentUser == null) return;

        String nome = etName.getText().toString().trim();
        String novaSenha = etPassword.getText().toString();
        String confirmaSenha = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(nome)) {
            Toast.makeText(this, "O nome não pode ficar em branco.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ALTERADO: Acessando os campos diretamente
        currentUser.nome = nome;
        if (selectedImageUri != null) {
            currentUser.profilePictureUri = selectedImageUri.toString();
        }

        if (!novaSenha.isEmpty()) {
            if (!novaSenha.equals(confirmaSenha)) {
                Toast.makeText(this, "As novas senhas não coincidem.", Toast.LENGTH_SHORT).show();
                return;
            }
            currentUser.password = novaSenha;
        }

        executor.execute(() -> {
            userDao.updateUser(currentUser);
            runOnUiThread(() -> {
                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void setupLoginView() {
        // ... (este método permanece o mesmo)
    }

    private void logoutUser() {
        // ... (este método permanece o mesmo)
    }

    private void loginUser() {
        // ... (sua lógica de login existente permanece a mesma)
    }
}