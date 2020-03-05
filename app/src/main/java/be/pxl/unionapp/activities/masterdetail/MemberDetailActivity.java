package be.pxl.unionapp.activities.masterdetail;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import be.pxl.unionapp.activities.MainActivity;
import be.pxl.unionapp.activities.UpdateActivity;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

// DetailActivity - enkel in PORTRAIT (MemberDetaiLFragment is equivalent voor MemberDetailFragment)
public class MemberDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvBirthdate, tvAddress, tvPostalCode, tvCity, tvTelephone, tvInstrument;
    Button btnUpdate, btnDelete;
    String memberId, firstname, lastname, birthdate, address, postalCode, city, instrument;
    long telephone;
    Member member;
    StorageReference storageReference;
    ImageView ivProfilePicture, ivGoogleMaps, ivPhoneCall;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back-knop aan
        Log.i(TAG, "Views created successfully");

        // Als de oriëntatie van deze Activity wijzigt naar LANDSCAPE, wordt de gebruiker naar MemberListActivity gestuurd
        // => als de oriëntatie wijzigt, wordt deze Activity helemaal opnieuw opgebouwd en komt deze sowieso opnieuw in deze
        // onCreate()-methode => er wordt gecheckt wat de oriëntatie is
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "Activity put in landscape");

            Intent intentToMemberListActivity = new Intent(MemberDetailActivity.this, MemberListActivity.class);
            startActivity(intentToMemberListActivity);
        }

        init();
        Log.i(TAG, "Initialized successfully");

        fillFieldsWithData();
        Log.i(TAG, "Fields filled successfully");

        // OnClickListeners declareren
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        ivGoogleMaps.setOnClickListener(this);
        ivPhoneCall.setOnClickListener(this);
    }

    // Wanneer er op het pijltje in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            Log.i(TAG, "Went back to previous activity successfully");
            return true;
        }

        Log.e(TAG, "Something went wrong going back to the previous activity");
        return super.onOptionsItemSelected(item);
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
        ivGoogleMaps = findViewById(R.id.ivGoogleMaps);
        ivPhoneCall = findViewById(R.id.ivPhoneCall);
        storageReference = FirebaseStorage.getInstance().getReference();
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    private void fillFieldsWithData() {
        // Data verzamelen die aan de Intent naar de huidige Activity (vanuit MemberListActivity) is meegegeven
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

        // Verzamelde data in de bijbehorende tekstvakken zetten
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

    private void fillImageView() {
        String imageName = memberId;
        StorageReference storageRef = storageReference.child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024; // Zo groot mag de foto zijn die opgeslagen wordt in de applicatie
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i(TAG, "Image downloaded from Firebase Storage successfully");

                // Foto in ImageView zetten
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfilePicture.setImageBitmap(bitmap);

                Log.i(TAG, "Image posted in ImageView successfully");
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            // Naar Activity gaan om gegevens van lid te wijzigen
            Log.i(TAG, "Update-button clicked");

            goToUpdateActivity();
        }
        else if (v.getId() == R.id.btnDelete) {
            // Lid verwijderen
            Log.i(TAG, "Delete-button clicked");

            initializeMember();
            Log.i(TAG, "Delete: Member initialized successfully");

            createDeleteDialog(member.getMemberId());
        } else if (v.getId() == R.id.ivGoogleMaps) {
            openGoogleMaps();
        } else if (v.getId() == R.id.ivPhoneCall) {
            makePhoneCall();
        }
    }

    private void goToUpdateActivity() {
        Intent intentToUpdateActivity = new Intent(MemberDetailActivity.this, UpdateActivity.class);

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

        // Mogelijke knoppen (JA/NEE) | Als er op 'Ja' geklikt wordt, wordt onderstaande OnClick()-methode uitgevoerd
        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Delete member confirmed");

                deleteMember(memberId); // Lid verwijderen uit database
            }
        });

        // Als er op 'Nee' geklikt wordt, gebeurt niks
        alertDialog.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Delete member denied");
            }
        });

        alertDialog.create().show(); // Hier wordt het popup-venster écht getoond zoals hierboven geconfigureerd
    }

    // Lid verwijderen uit database
    private void deleteMember(String memberId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(memberId);
        databaseHelper.deleteMember();

        String toastMessage = member.getFirstname() + " " + member.getLastname() + " is verwijderd";
        Toast.makeText(MemberDetailActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        Log.i(TAG, "Member deleted succesfully");

        deleteProfilePicture();

        // Naar MemberListActivity gaan
        Intent intentToMemberListActivity = new Intent(MemberDetailActivity.this, MemberListActivity.class);
        startActivity(intentToMemberListActivity);
    }

    // Profielfoto van desbetreffend lid uit de storage verwijderen
    private void deleteProfilePicture() {
        StorageReference pictureRef = storageReference.child(memberId);

        pictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Profilepicture deleted successfully");
            }
        });
    }

    // Google Maps openen op adres van het lid
    private void openGoogleMaps() {
        String locationUri = "https://www.google.com/maps/search/?api=1&query=";
        locationUri += address + "+";
        locationUri += city;

        Uri uri = Uri.parse(locationUri);
        Intent intentToGoogleMaps = new Intent(android.content.Intent.ACTION_VIEW, uri);

        startActivity(intentToGoogleMaps);
    }

    // Telefoneren
    private void makePhoneCall() {
        String phoneNumber = "0" + telephone;
        Intent intentToMakePhoneCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

        startActivity(intentToMakePhoneCall);
    }
}
