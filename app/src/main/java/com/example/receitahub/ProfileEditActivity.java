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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
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
                    Uri internalUri = copyImageToInternalStorage(uri);
                    if (internalUri != null) {
                        selectedImageUri = internalUri;
                        ivProfilePicture.setImageURI(selectedImageUri);
                        ivProfilePicture.setImageTintList(null);
                    } else {
                        Toast.makeText(this, "Falha ao carregar a imagem.", Toast.LENGTH_SHORT).show();
                    }
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

    private Uri copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            File destinationFile = new File(getFilesDir(), "profile_" + UUID.randomUUID().toString() + ".jpg");
            OutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return Uri.fromFile(destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                    etName.setText(currentUser.nome);
                    etEmail.setText(currentUser.email);
                    etEmail.setEnabled(false);
                    if (currentUser.profilePictureUri != null) {
                        Uri imageUri = Uri.parse(currentUser.profilePictureUri);
                        selectedImageUri = imageUri;
                        ivProfilePicture.setImageURI(imageUri);
                        ivProfilePicture.setImageTintList(null);
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
        tvAuthTitle.setText("Login");
        btnAuthAction.setText("Entrar");
        etEmail.setVisibility(View.VISIBLE);
        layoutPasswordFields.setVisibility(View.VISIBLE);
        etPassword.setHint("Senha");
        etConfirmPassword.setVisibility(View.GONE);
        ivProfilePicture.setVisibility(View.GONE);
        etName.setVisibility(View.GONE);
        btnAuthAction.setOnClickListener(v -> loginUser());
        tvToggleAuthMode.setText("Não tem uma conta? Cadastre-se.");
        tvToggleAuthMode.setOnClickListener(v -> startActivity(new Intent(ProfileEditActivity.this, RegisterActivity.class)));
    }

    private void logoutUser() {
        sessionManager.clearSession();
        Toast.makeText(this, "Você saiu.", Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email e senha são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }
        executor.execute(() -> {
            User user = userDao.getUserByEmailAndPassword(email, password);
            runOnUiThread(() -> {
                if (user != null) {
                    sessionManager.saveSession(user.id);
                    Toast.makeText(ProfileEditActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(ProfileEditActivity.this, "Email ou senha inválidos.", Toast.LENGTH_SHORT).show();
                }
            });
        });
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
}