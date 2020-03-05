package be.pxl.unionapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.Calendar;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.masterdetail.MemberListActivity;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

// In deze Activity staan de gegevens van het lid al ingevuld. Deze kunnen aangepast worden
public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    Button btnUpdate;
    EditText etFirstname, etLastname, etAddress, etPostalCode, etCity, etTelephone, etInstrument;
    TextView tvBirthdate;
    String memberId, firstname, lastname, address, postalCode, city, instrument;
    long telephone;
    Member member;
    boolean dateSelected;
    ImageView ivProfilePicture;
    StorageReference storageReference;
    static final int PICK_IMAGE_REQUEST = 124;
    Uri filePath;
    static final String TAG = "UpdateActivity";
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        Log.i(TAG, "Views initialized succesfully");

        fillFieldsWithData();

        // OnClickListeners declareren
        btnUpdate.setOnClickListener(this);
        tvBirthdate.setOnClickListener(this);
        ivProfilePicture.setOnClickListener(this);

        if (savedInstanceState != null) {
            restoreSavedInstanceStates(savedInstanceState);
        }
    }

    private void init() {
        btnUpdate = findViewById(R.id.btnUpdate);
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        tvBirthdate = findViewById(R.id.tvDateSelector);
        etAddress = findViewById(R.id.etAddress);
        etPostalCode = findViewById(R.id.etPostalCode);
        etCity = findViewById(R.id.etCity);
        etTelephone = findViewById(R.id.etTelephone);
        etInstrument = findViewById(R.id.etInstrument);
        dateSelected = true;
        ivProfilePicture = findViewById(R.id.ivUpdateProfilePicture);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    // Data opslaan bij draaien van scherm
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Gewijzigde geboortedatum
        outState.putString("my_birthdate", date);
    }

    // Data in variabelen zetten bij draaien van scherm
    private void restoreSavedInstanceStates(Bundle savedInstanceState) {
        // Gewijzigde geboortedatum
        date = savedInstanceState.getString("my_birthdate");
        String birthdateString = "Geboortedatum: " + date;
        tvBirthdate.setText(birthdateString);
    }

    private void fillFieldsWithData() {
        // Data verzamelen die aan de Intent (vanuit DetailActivity) waren meegegeven
        memberId = getIntent().getStringExtra("memberId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        date = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        postalCode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");
        telephone = getIntent().getLongExtra("telephone", 0);
        instrument = getIntent().getStringExtra("instrument");

        // Titel van ActionBar aanpassen
        String titleName = "Wijzig gegevens van " + firstname + " " + lastname;
        setTitle(titleName);

        // Verzamelde data in de tekstvakken zetten
        etFirstname.setText(firstname);
        etLastname.setText(lastname);
        String birthdateString = "Geboortedatum: " + date;
        tvBirthdate.setText(birthdateString);
        etAddress.setText(address);
        etPostalCode.setText(postalCode);
        etCity.setText(city);
        String telephoneString = "0" + telephone;
        etTelephone.setText(telephoneString);
        etInstrument.setText(instrument);

        // Foto tonen in de imageView
        fillImageView();
    }

    private void fillImageView() {
        String imageName = memberId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024; // Max formaat van foto
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfilePicture.setImageBitmap(bitmap);
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            // Gegevens van lid wijzigen
            updateMember();
        }
        else if (v.getId() == R.id.tvDateSelector) {
            // Dialoogvenster tonen om data te selecteren
            showDatePickerDialog();
        }
        else if (v.getId() == R.id.ivUpdateProfilePicture) {
            // Dialoogvenster tonen om afbeelding te selecteren
            showFileChooser();
        }
    }

    // Wanneer er op het pijltje (terug) in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMember() {
        if (checkUserInputValidity()) {
           InitializeMember();

           new FirebaseDatabaseHelper().updateMember(member);

           Toast.makeText(UpdateActivity.this, member.getFirstname() + " " + member.getLastname() + " is gewijzigd", Toast.LENGTH_LONG).show();
           Log.i(TAG, "Member updated successfully");

           goToMemberListActivity();
        }
    }

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
            tvBirthdate.setError("Selecteer uw geboortedatum");
            inputIsValid = false;
        }

        if (etAddress.getText().length() < 5) {
            String address = getResources().getString(R.string.address);
            etAddress.setError("\"" + address + "\" moet minstens 5 karakters hebben");
            inputIsValid = false;
        }

        boolean postalCodeIsNumber = true;
        try {
            Integer.parseInt(etPostalCode.getText().toString());
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

        if (inputIsValid) {
            Log.i(TAG, "Fields fileld in correctly");
        } else {
            Log.e(TAG, "Fields not filled in correctly");
        }

        return inputIsValid;
    }

    private void InitializeMember() {
        member = new Member();
        member.setMemberId(memberId);
        member.setFirstname(etFirstname.getText().toString().trim());
        member.setLastname(etLastname.getText().toString().trim());
        member.setAddress(etAddress.getText().toString().trim());
        String birthdateTextView = tvBirthdate.getText().toString().trim();
        date = birthdateTextView.substring(15);
        member.setBirthdate(date);
        member.setPostalCode(etPostalCode.getText().toString().trim());
        member.setCity(etCity.getText().toString().trim());
        member.setTelephone(Long.parseLong(etTelephone.getText().toString().trim()));
        member.setInstrument(etInstrument.getText().toString().trim());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(UpdateActivity.this, AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();
    }

    // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de DatePickerDialog()-methode
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(TAG, "Date selected successfully");
        dateSelected = true;
        tvBirthdate.setError(null);
        date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
        String result = "Geboortedatum: " + date;
        tvBirthdate.setText(result);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecteer een afbeelding"), PICK_IMAGE_REQUEST);
    }

    // Deze methode wordt uitgevoerd als de gebruiker een afbeelding geselecteerd heeft in showFileChooser()-methode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            // Afbeelding uploaden in database. Dit wordt hier al gedaan omdat er een beetje vertraging zit in het updaten van de foto in de storage zelf
            uploadPicture();

            // Toon geselecteerde afbeelding in ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivProfilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Foto in de storage zetten
    private void uploadPicture() {
        if (filePath != null) {
            String pictureName = memberId;
            storageReference = storageReference.child(pictureName);

            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "Image uploaded to Firebase Storage successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateActivity.this, "Er is iets fout gegaan bij het uploaden van de profielfoto. Probeer opnieuw", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Something went wrong uploading the picture to Firebase Storage");
                }
            });
        }
    }

    private void goToMemberListActivity() {
        Intent intentToDetailsActivity = new Intent(UpdateActivity.this, MemberListActivity.class);
        startActivity(intentToDetailsActivity);
    }
}
