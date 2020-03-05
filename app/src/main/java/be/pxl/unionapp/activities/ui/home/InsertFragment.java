package be.pxl.unionapp.activities.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Calendar;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.MainActivity;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

// Fragment om een nieuw lid toe te voegen
public class InsertFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private View root;
    private EditText etFirstname, etLastname, etAddress, etPostalCode, etCity, etTelephone, etInstrument;
    private TextView tvDisplayDate, tvImageSelector;
    private ImageView ivProfilePicture;
    private Button btnRegister;
    private FirebaseDatabaseHelper databaseHelper;
    private Member member;
    private String date;
    private boolean dateSelected, imageSelected;
    private static final int PICK_IMAGE_REQUEST = 123; // random nummer
    private Uri image;
    private StorageReference storageReference;
    private static final String TAG = "InsertFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_insert, container, false);

        init();
        Log.i(TAG, "Initialized successfully");

        // OnClickListeners declareren
        tvDisplayDate.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        ivProfilePicture.setOnClickListener(this);

        // Als er een savedInstanceState is, worden de opgeslagen waardes hiervan in de juiste variabelen gezet
        if (savedInstanceState != null) {
            restoreSavedInstanceStates(savedInstanceState);

            Log.i(TAG, "All instance states restored successfully");
        }

        return root;
    }

    private void init() {
        etFirstname = root.findViewById(R.id.etFirstname);
        etLastname = root.findViewById(R.id.etLastname);
        tvDisplayDate = root.findViewById(R.id.tvDateSelector);
        etAddress = root.findViewById(R.id.etAddress);
        etPostalCode = root.findViewById(R.id.etPostcalCode);
        etCity = root.findViewById(R.id.etCity);
        etTelephone = root.findViewById(R.id.etTelephone);
        etInstrument = root.findViewById(R.id.etInstrument);
        ivProfilePicture = root.findViewById(R.id.ivProfilePicture);
        btnRegister = root.findViewById(R.id.btnRegister);
        databaseHelper = new FirebaseDatabaseHelper();
        member = new Member();
        tvImageSelector = root.findViewById(R.id.tvImageSelector);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    // Sla instance states automatisch op bij het wijzigen van orientation
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Geboortedatum
        outState.putString("my_birthdate_string", tvDisplayDate.getText().toString());
        outState.putBoolean("my_birthdate_boolean", dateSelected);
        outState.putString("my_birthdate", date);

        // Profielfoto
        outState.putBoolean("my_profilepicture_boolean", imageSelected);
        outState.putParcelable("my_profilepicture", image);

        Log.i(TAG, "All instance states saved successfully");
    }

    // Laad opgeslagen instance states in
    private void restoreSavedInstanceStates(Bundle savedInstanceState) {
        // Geboortedatum
        tvDisplayDate.setText(savedInstanceState.getString("my_birthdate_string"));
        dateSelected = savedInstanceState.getBoolean("my_birthdate_boolean");
        date = savedInstanceState.getString("my_birthdate");

        // Profielfoto
        imageSelected = savedInstanceState.getBoolean("my_profilepicture_boolean");
        image = savedInstanceState.getParcelable("my_profilepicture");
        if (image != null) {
            ivProfilePicture.setImageURI(image);
            tvImageSelector.setVisibility(View.INVISIBLE); // De tekst om een foto te selecteren
        }

        Log.i(TAG, "All instances restored successfully");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tvDateSelector) {
            // Datum selecteren
            Log.i(TAG, "Date selector clicked");

            showDatePickerDialog();
        }
        else if (v.getId() == R.id.btnRegister) {
            // Lid toevoegen aan de database
            Log.i(TAG, "Register button clicked");

            addMember();
        }
        else if (v.getId() == R.id.ivProfilePicture) {
            // Dialoogvenster openen om afbeelding te selecteren
            Log.i(TAG, "Profile picture selector clicked");

            showFileChooser();
        }
    }

    // Dialoogvenster tonen om een datum te selecteren
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();

        Log.i(TAG, "DatePicker opened successfully.");
    }

    // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de showDatePickerDialog()-methode
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(TAG, "Date selected successfully");

        dateSelected = true;
        tvDisplayDate.setError(null); // ALS er een error-message was, wordt deze verwijderd wanneer een datum geselecteerd is

        date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;

        String result = "Geboortedatum: " + date;
        tvDisplayDate.setText(result);
    }

    private void addMember() {
        if (checkUserInputValidity()) {
            InitializeMember();
            Log.i(TAG, "Member initialized successfully");

            // Lid toevoegen aan database (d.m.v. FirebaseDatabaseHelper)
            databaseHelper.addMember(member);
            Log.i(TAG, "Member added to Firebase Database successfully");

            // Profielfoto niet in de database zetten (zoals stap hierboven), maar aan Firebase storage toevoegen
            uploadProfilePicture();

            // Wanneer lid succesvol is toegevoegd aan database...
            Toast.makeText(getActivity(), member.getFirstname() + " " + member.getLastname() + " is toegevoegd", Toast.LENGTH_LONG).show();
            goToMainActivity();
        }
    }

    // Wanneer een input niet geldig is, wordt een foutmelding getoond
    private boolean checkUserInputValidity() {
        boolean inputIsValid = true;

        if (etFirstname.getText().length() < 2) {
            String firstname = getResources().getString(R.string.firstname);
            etFirstname.setError("\"" + firstname + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        if (etLastname.getText().length() < 2) {
            String lastname = getResources().getString(R.string.lastname);
            etLastname.setError("\"" + lastname + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        if (!dateSelected) {
            tvDisplayDate.setError("Selecteer uw geboortedatum");
            inputIsValid = false;
        }

        if (etAddress.getText().length() < 5) {
            String address = getResources().getString(R.string.address);
            etAddress.setError("\"" + address + "\" moet minstens 5 karakters hebben");
            inputIsValid = false;
        }

        boolean postalCodeIsNumber = true;
        try {
            Integer.parseInt(etPostalCode.getText().toString()); // Als de postcode niet omgezet kan worden naar een int, bevat deze ongeldige tekens
        } catch (NumberFormatException e) {
            postalCodeIsNumber = false;
        }
        if (etPostalCode.getText().length() != 4 || !postalCodeIsNumber) {
            String postalCode = getResources().getString(R.string.postcalCode);
            etPostalCode.setError("\"" + postalCode + "\" moet een getal bestaande uit 4 cijfers zijn");
            inputIsValid = false;
        }

        if (etCity.getText().length() < 2) {
            String city = getResources().getString(R.string.city);
            etCity.setError("\"" + city + "\" moet minstens 2 karakters hebben");
            inputIsValid = false;
        }

        boolean telephoneNumberIsNumber = true;
        try {
            Long.parseLong(etTelephone.getText().toString());
        } catch (NumberFormatException e) {
            telephoneNumberIsNumber = false;
        }
        if (!etTelephone.getText().toString().startsWith("0") || etTelephone.getText().length() < 9 || etTelephone.getText().length() > 10 || !telephoneNumberIsNumber) {
            String telephoneNumber = getResources().getString(R.string.telephone);
            etTelephone.setError("\"" + telephoneNumber + "\" is niet geldig");
            inputIsValid = false;
        }

        if (etInstrument.getText().length() == 0) {
            String instrument = getResources().getString(R.string.instrument);
            etInstrument.setError("\"" + instrument + "\" moet ingevuld zijn");
            inputIsValid = false;
        }

        if (!imageSelected) {
            tvImageSelector.setError("Selecteer een afbeelding");
            inputIsValid = false;
        }

        if (inputIsValid) {
            Log.i(TAG, "Input is valid");
        } else {
            Log.w(TAG, "Input is not valid");
        }

        return inputIsValid;
    }

    private void goToMainActivity() {
        Intent intentToMainActivity = new Intent(getActivity(), MainActivity.class);
        startActivity(intentToMainActivity);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecteer een afbeelding"), PICK_IMAGE_REQUEST);
        Log.i(TAG, "Filechooser opened successfully");
    }

    // Deze methode wordt uitgevoerd als de gebruiker een afbeelding geselecteerd heeft in showFileChooser()-methode
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            Log.i(TAG, "Image selected successfully");
            image = data.getData();
            tvImageSelector.setVisibility(View.INVISIBLE);
            ivProfilePicture.setImageURI(image);
            tvImageSelector.setError(null);
            imageSelected = true;
        }
    }

    private void InitializeMember() {
        member.setFirstname(etFirstname.getText().toString().trim());
        member.setLastname(etLastname.getText().toString().trim());
        member.setBirthdate(date);
        member.setAddress(etAddress.getText().toString().trim());
        member.setPostalCode(etPostalCode.getText().toString().trim());
        member.setCity(etCity.getText().toString().trim());
        member.setTelephone(Long.parseLong(etTelephone.getText().toString().trim()));
        member.setInstrument(etInstrument.getText().toString().trim());
    }

    // Profielfoto uploaden naar Firebase Storage
    private void uploadProfilePicture() {
        if (image != null) {
            String pictureName = member.getMemberId(); // Fotonaam = id van member
            storageReference = storageReference.child(pictureName);

            storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "Image selected successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Foto toevoegen aan storage = mislukt
                    Toast.makeText(getContext(), "Er is iets fout gegaan bij het uploaden van de profielfoto. Probeer opnieuw", Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "Something went wrong uploading the image to Firebase Storage");
                }
            });
        }
    }
}