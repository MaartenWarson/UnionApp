package be.pxl.unionapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import be.pxl.unionapp.R;

// Activity om als bestaande gebruiker aan te melden | OnClickListener gaat luisteren wanneer er ergens op geklikt wordt
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etPassword;
    String email, password;
    Button btnLogin;
    TextView tvNoAccount;
    CheckBox cbRememberMe;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences preferences;
    SharedPreferences.Editor editor; // Om de sharedPreferences op te slaan
    static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);

        init();
        Log.i(TAG, "Initialized successfully");

        // OnClickListeners declareren voor Button en TextView
        btnLogin.setOnClickListener(this);
        tvNoAccount.setOnClickListener(this);
    }

    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvNoAccount = findViewById(R.id.tvNoAccount);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        progressBar = findViewById(R.id.progressBar);

        // FirebaseAuth initialiseren is nodig voor de authenticatie (= aanmelden) van de gebruiker
        firebaseAuth = FirebaseAuth.getInstance();
        initializeAuthStateListener();

        // Shared preferences ("myApp" is de naam van de Preference File | Private: kan enkel door deze applicatie gebruikt worden)
        preferences = getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = preferences.edit(); // Editor is nodig om gegevens op te slaan in de Shared Preference
        checkSharedPreferences();
    }

    // Gaat 'luisteren' naar veranderingen in de credentials
    private void initializeAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent intentToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentToMainActivity);
                }
            }
        };
    }

    private void checkSharedPreferences() {
        // De getString() neemt de string van strings.xml (dit zijn de keys)
        String prCheckbox = preferences.getString(getString(R.string.preferencesCheckbox), "False");
        String prEmail = preferences.getString(getString(R.string.preferencesEmail), "");
        String prPassword = preferences.getString(getString(R.string.preferencesPassword), "");

        // De (default) waardes in de EditTextViews zetten
        etEmail.setText(prEmail);
        etPassword.setText(prPassword);

        if (prCheckbox.equals("True")) {
            cbRememberMe.setChecked(true);
        } else {
            cbRememberMe.setChecked(false);
        }
    }

    // OnClickListeners initialiseren
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            // Inloggen + sharedPreferences opslaan
            startLoginProcess();
        }
        else if (v.getId() == R.id.tvNoAccount) {
            // Naar activity gaan om nieuwe gebruiker aan te maken
            goToSignUpActivity();
        }
    }

    // Loginproces starten
    private void startLoginProcess() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Vul beide velden in", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Fields not filled in correctly");
        }
        else if (email.isEmpty()) {
            etEmail.setError("Geef een e-mailadres in"); // ErrorMessage bij dit tekstvak
            etEmail.requestFocus(); // Focus leggen op deze EditText
            Log.e(TAG, "Fields not filled in correctly");
        }
        else if (password.isEmpty()) {
            etPassword.setError("Geef een wachtwoord in");
            etPassword.requestFocus();
            Log.e(TAG, "Fields not filled in correctly");
        }
        else {
            // SharedPreferences opslaan (wanneer credentials juist zijn ingegeven)
            saveValuesForSharedPreferences(email, password);

            // Wanneer alles goed is ingevuld, wordt de BackgroundExecuter (klasse in deze Activity-klasse) uitgevoerd (= Background Thread) om ervoor te zorgen dat de Progressbar goed getoond wordt
            new BackgroundExecuter().execute();
        }
    }

    // Slaat de credentials op in de SharedPreference
    private void saveValuesForSharedPreferences(String email, String password) {
        if (cbRememberMe.isChecked()) {
            // Sla waarde 'true' van CheckBox op
            editor.putString(getString(R.string.preferencesCheckbox), "True");
            editor.commit();

            // Sla emailadres op
            editor.putString(getString(R.string.preferencesEmail), email);
            editor.commit();

            // Sla passwoord op
            editor.putString(getString(R.string.preferencesPassword), password);
            editor.commit();
        } else {
            // Sla waarde 'false' van CheckBox op
            editor.putString(getString(R.string.preferencesCheckbox), "False");
            editor.commit();

            // Sla emailadres op
            editor.putString(getString(R.string.preferencesEmail), "");
            editor.commit();

            // Sla passwoord op
            editor.putString(getString(R.string.preferencesPassword), "");
            editor.commit();
        }

        Log.i(TAG, "Shared Preferences saves successfully");
    }

    private void login() {
        // Aanmelden van gebruiker | CompleteListener gaat de onComplete-methode uitvoeren wanneer de signIn-methode uitgevoerd is
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Login NIET succesvol
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Gebruikersnaam en/of wachtwoord zijn incorrect. Probeer opnieuw", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "Something went wrong logging in");
                }
                // Login succesvol
                else {
                    Log.i(TAG, "Logged in successfully");
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intentToMainActivityToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentToMainActivityToMainActivity);
                }
            }
        });
    }

    private void goToSignUpActivity() {
        Intent intentToSignUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intentToSignUpActivity);
    }

    @Override // Wordt opgeroepen na OnCreate (wanneer de Activity zichtbaar wordt voor de gebruiker)
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // Deze voert een achtergrondtaak uit (Background Thread)
    public class BackgroundExecuter extends AsyncTask<String, Void, Void> {
        // VOOR het uitvoeren van de taak (uitgevoerd in Main Thread)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        // UITVOEREN van de taak (uitgevoerd in Background Thread = op de achtergrond)
        @Override
        protected Void doInBackground(String... strings) {
            login();
            return null;
        }

        // NA het uitvoeren van de taak (geen speciale implementatie voor deze app) (uitgevoerd in Main Thread)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
