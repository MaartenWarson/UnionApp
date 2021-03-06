package be.pxl.unionapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

// In deze activity wordt een account aangemaakt als de gebruiker zelf nog geen account heeft
public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener {
    EditText etEmail, etPassword;
    Button btnSignUp;
    TextView tvAlreadyAccount;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    String email, password;
    static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle(R.string.signUp);

        init();
        Log.i(TAG, "Views initialized successfully");

        // OnClickListeners declareren voor Button en TextView
        btnSignUp.setOnClickListener(this);
        tvAlreadyAccount.setOnClickListener(this);
    }

    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvAlreadyAccount = findViewById(R.id.tvAlreadyAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
    }

    // OnClickListeners initialiseren
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignUp) {
            // Nieuwe gebruiker aanmaken
            startSignUpProcess();
        }
        else if (v.getId() == R.id.tvAlreadyAccount) {
            // Wanneer de gebruiker al een account heeft, wordt deze naar de LoginActivity gestuurd
            goToLoginActivity();
        }
    }

    private void startSignUpProcess() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Vul beide velden in", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Fields not filled in correctly");
        }
        else if (email.isEmpty()) {
            etEmail.setError("Geef een e-mailadres in");
            etEmail.requestFocus(); // Focus leggen op deze EditText
            Log.e(TAG, "Fields not filled in correctly");
        }
        else if (password.isEmpty()) {
            etPassword.setError("Geef een wachtwoord in");
            etPassword.requestFocus();
            Log.e(TAG, "Fields not filled in correctly");
        }
        else {
            // Wanneer alles goed is ingevuld, wordt de BackgroundExecuter uitgevoerd om ervoor te zorgen dat de Progressbar goed getoond wordt
            new BackgroundExecuter().execute();
        }
    }

    private void signUp() {
        // Gebruiker toevoegen in FireBase
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Account kan niet aangemaakt worden. Probeer opnieuw", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "Something went wrong creating the account");
                }
                else {
                    // Als gebruiker toegevoegd is, word je verder gestuurd naar HomeActivity
                    Log.i(TAG, "Account created successfully");
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intentToMainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intentToMainActivity);
                }
            }
        });
    }

    private void goToLoginActivity() {
        Intent intentToLoginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intentToLoginActivity);
    }


    public class BackgroundExecuter extends AsyncTask<String, Void, Void> {
        // VOOR het uivoeren van de taak
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        // Het uitvoeren van de taak
        @Override
        protected Void doInBackground(String... strings) {
            signUp();
            return null;
        }

        // NA het uivoeren van de taak
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
