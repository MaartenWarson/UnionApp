package be.pxl.unionapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import be.pxl.unionapp.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvNoAccount;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        // OnClickListeners declareren
        btnLogin.setOnClickListener(this);
        tvNoAccount.setOnClickListener(this);
    }

    // OnClickListeners initialiseren
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            login();
        }
        else if (v.getId() == R.id.tvNoAccount) {
            goToSignUpScreen();
        }
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Vul beide velden in", Toast.LENGTH_LONG).show();
        }
        else if (email.isEmpty()) {
            etEmail.setError("Geef een e-mailadres in");
            etEmail.requestFocus(); // Focus leggen op deze EditText
        }
        else if (password.isEmpty()) {
            etPassword.setError("Geef een wachtwoord in");
            etPassword.requestFocus();
        }
        else {
            // Gebruiker toevoegen
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Gebruikersnaam en/of wachtwoord zijn incorrect. Probeer opnieuw", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intentToHome = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intentToHome);
                    }
                }
            });
        }
    }

    private void goToSignUpScreen() {
        Intent intentToSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intentToSignUp);
    }
    
    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvNoAccount = findViewById(R.id.tvNoAccount);
        firebaseAuth = FirebaseAuth.getInstance();

        initializeAuthStateListener();
    }

    private void initializeAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
