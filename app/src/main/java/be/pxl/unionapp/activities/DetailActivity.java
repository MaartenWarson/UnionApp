package be.pxl.unionapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import be.pxl.unionapp.R;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

// In deze activity worden alle gegevens van de leden getoond
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvBirthdate, tvAddress, tvPostalCode, tvCity, tvTelephone, tvInstrument;
    Button btnUpdate, btnDelete;
    String memberId, firstname, lastname, birthdate, address, postalCode, city, instrument;
    long telephone;
    Member member;
    StorageReference storageReference;
    ImageView ivProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        fillFieldsWithData();

        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void init() {
        member = new Member();
        tvBirthdate = findViewById(R.id.birthdate_database);
        tvAddress = findViewById(R.id.address_database);
        tvPostalCode = findViewById(R.id.postalCode_database);
        tvCity = findViewById(R.id.city_database);
        tvTelephone = findViewById(R.id.telephone_database);
        tvInstrument = findViewById(R.id.instrument_database);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        storageReference = FirebaseStorage.getInstance().getReference();
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    private void fillFieldsWithData() {
        // Data verzamelen die aan de Intent (vanuit RecyclerView en/of UpdateActivity) waren meegegeven
        memberId = getIntent().getStringExtra("memberId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        birthdate = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        postalCode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");
        telephone = getIntent().getLongExtra("telephone", 0);
        instrument = getIntent().getStringExtra("instrument");

        // Titel van ActionBar aanpassen
        String name = firstname + " " + lastname;
        setTitle(name);

        // Verzamelde data in de tekstvakken zetten
        tvBirthdate.setText(birthdate);
        tvAddress.setText(address);
        tvPostalCode.setText(postalCode);
        tvCity.setText(city);
        String telephoneString = "0" + telephone;
        tvTelephone.setText(telephoneString);
        tvInstrument.setText(instrument);

        // Foto tonen in de ImageView
        fillImageView();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            // Naar activity gaan om gegevens van lid te wijzigen
            goToUpdateActivity();
        }
        else if (v.getId() == R.id.btnDelete) {
            // Lid verwijderen
            initializeMember();
            createDeleteDialog(member.getMemberId());
        }
    }

    // Wanneer er op het pijltje in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillImageView() {
        String imageName = memberId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024; // Zo groot mag de foto zijn die opgeslagen wordt in de applicatie
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Wanneer de foto succesvol gedownload is van de Firebase Storage, wordt deze in de ImageView gezet
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfilePicture.setImageBitmap(bitmap);
            }
        });
    }

    private void goToUpdateActivity() {
        // Intent maken
        Intent intentToUpdateActivity = new Intent(DetailActivity.this, UpdateActivity.class);

        // Gegevens meegeven aan de intent die getoond moeten worden in UpdateActivity
        intentToUpdateActivity.putExtra("memberId", memberId);
        intentToUpdateActivity.putExtra("firstname", firstname);
        intentToUpdateActivity.putExtra("lastname", lastname);
        intentToUpdateActivity.putExtra("birthdate", birthdate);
        intentToUpdateActivity.putExtra("address", address);
        intentToUpdateActivity.putExtra("postalCode", postalCode);
        intentToUpdateActivity.putExtra("city", city);
        intentToUpdateActivity.putExtra("telephone", telephone);
        intentToUpdateActivity.putExtra("instrument", instrument);

        // Naar UpdateActivity gaan
        startActivity(intentToUpdateActivity);
    }

    private void initializeMember() {
        member.setMemberId(memberId);
        member.setFirstname(firstname);
        member.setLastname(lastname);
        member.setBirthdate(birthdate);
        member.setAddress(address);
        member.setPostalCode(postalCode);
        member.setCity(city);
        member.setTelephone(telephone);
        member.setInstrument(instrument);
    }

    // Popup-venster om bevestiging te vragen om lid te verijwderen
    private void createDeleteDialog(final String memberId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Weet je zeker dat je " + member.getFirstname() + " " + member.getLastname() + " wilt verwijderen?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember(memberId); // Lid verwijderen uit database
            }
        });

        alertDialog.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create().show();
    }

    // Lid verwijderen uit database
    private void deleteMember(String memberId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(memberId);
        databaseHelper.deleteMember();

        String toastMessage = member.getFirstname() + " " + member.getLastname() + " is verwijderd";
        Toast.makeText(DetailActivity.this, toastMessage, Toast.LENGTH_LONG).show();

        deleteProfilePicture();

        // Naar MainActivity gaan
        Intent intentToMainActivity = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intentToMainActivity);
    }

    // Profielfoto van desbetreffend lid uit de storage verwijderen
    private void deleteProfilePicture() {
        StorageReference pictureRef = storageReference.child(memberId);

        pictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
}
