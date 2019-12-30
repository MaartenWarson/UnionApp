package be.pxl.unionapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import be.pxl.unionapp.R;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener {
    // DECLARATIES
    EditText etEmail, etPassword;
    Button btnSignUp;
    TextView tvAlreadyAccount;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        // OnClickListeners declareren
        btnSignUp.setOnClickListener(this);
        tvAlreadyAccount.setOnClickListener(this);
    }

    // OnClickListeners initialiseren
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignUp) {
            startSignUpProcess();
        }
        else if (v.getId() == R.id.tvAlreadyAccount) {
            goToLoginScreen();
        }
    }

    private void startSignUpProcess() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

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
            new BackgroundExecuter().execute();
        }
    }

    private void signUp() {
        // Gebruiker toevoegen
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Account kan niet aangemaakt worden. Probeer opnieuw", Toast.LENGTH_LONG).show();
                }
                else {
                    // Als gebruiker toegevoegd is, word je verder gestuurd naar HomeActivity
                    Intent intentToHome = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intentToHome);
                }
            }
        });
    }

    private void goToLoginScreen() {
        Intent intentToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intentToLogin);
    }

    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
    }

    public class BackgroundExecuter extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            signUp();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
