package be.pxl.unionapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.ui.home.ReadFragment;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvFirstname, tvLastname, tvBirthdate, tvAddress, tvPostalCode, tvCity, tvTelephone, tvInstrument;
    Button btnBack, btnUpdate, btnDelete;
    String memberId, firstname, lastname, birthdate, address, postalCode, city, instrument;
    long telephone;
    Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
        fillFields();

        btnUpdate.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            Intent intentToUpdateActivity = new Intent(DetailActivity.this, UpdateActivity.class);
            intentToUpdateActivity.putExtra("memberId", memberId);
            intentToUpdateActivity.putExtra("firstname", firstname);
            intentToUpdateActivity.putExtra("lastname", lastname);
            intentToUpdateActivity.putExtra("birthdate", birthdate);
            intentToUpdateActivity.putExtra("address", address);
            intentToUpdateActivity.putExtra("postalCode", postalCode);
            intentToUpdateActivity.putExtra("city", city);
            intentToUpdateActivity.putExtra("telephone", telephone);
            intentToUpdateActivity.putExtra("instrument", instrument);

           startActivity(intentToUpdateActivity);
        }
        else if (v.getId() == R.id.btnBack) {
            finish();
            goToReadFragment();
        }
        else if (v.getId() == R.id.btnDelete) {
            createMember();
            createDialog(member.getMemberId());
        }
    }

    private void init() {
        member = new Member();
        tvFirstname = findViewById(R.id.firstname_database);
        tvLastname = findViewById(R.id.lastname_database);
        tvBirthdate = findViewById(R.id.birthdate_database);
        tvAddress = findViewById(R.id.address_database);
        tvPostalCode = findViewById(R.id.postalCode_database);
        tvCity = findViewById(R.id.city_database);
        tvTelephone = findViewById(R.id.telephone_database);
        tvInstrument = findViewById(R.id.instrument_database);
        btnBack = findViewById(R.id.btnBack);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void createMember() {
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

    private void fillFields() {
        memberId = getIntent().getStringExtra("memberId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        birthdate = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");
        postalCode = getIntent().getStringExtra("postalCode");
        city = getIntent().getStringExtra("city");
        telephone = getIntent().getLongExtra("telephone", 0);
        instrument = getIntent().getStringExtra("instrument");

        tvFirstname.setText(firstname);
        tvLastname.setText(lastname);
        tvBirthdate.setText(birthdate);
        tvAddress.setText(address);
        tvPostalCode.setText(postalCode);
        tvCity.setText(city);
        String telephoneString = "0" + telephone;
        tvTelephone.setText(telephoneString);
        tvInstrument.setText(instrument);
    }

    private void goToReadFragment() {
        ReadFragment fragment = new ReadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.detail_activity, fragment);
        transaction.commit();
    }

    private void deleteMember(String memberId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(memberId);
        databaseHelper.deleteMember();

        String toastMessage = member.getFirstname() + " " + member.getLastname() + " is verwijderd";
        Toast.makeText(DetailActivity.this, toastMessage, Toast.LENGTH_LONG).show();

        startActivity(new Intent(DetailActivity.this, MainActivity.class));
    }

    private void createDialog(final String memberId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Weet je zeker dat je " + member.getFirstname() + " " + member.getLastname() + " wilt verwijderen?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember(memberId);
            }
        });

        alertDialog.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create().show();
    }
}
