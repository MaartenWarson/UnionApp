package be.pxl.unionapp.activities.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.Calendar;
import be.pxl.unionapp.R;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

public class InsertFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    // DECLARATIES
    private View root;
    private EditText etFirstname, etLastname, etAddress, etPostalCode, etCity, etTelephone, etInstrument;
    private TextView tvDisplayDate;
    private Button btnRegister;
    private FirebaseDatabaseHelper databaseHelper;
    private Member member;
    private boolean dateSelected;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_insert, container, false);

        init();

        // OnClickListeners declareren
        tvDisplayDate.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        return root;
    }

    // OnClickListeners initialiseren
    public void onClick(View v) {
        if (v.getId() == R.id.tvDateSelector) {
            showDatePickerDialog();
        }
        else if (v.getId() == R.id.btnRegister) {
            addMember();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        dialog.show();
    }

    private void addMember() {
        if (checkUserInputValidity()) {
            InitializeMember();
            databaseHelper.AddMember(member);

            Toast.makeText(getActivity(), member.getFirstname() + " " + member.getLastname() + " is toegevoegd.", Toast.LENGTH_LONG).show();

            goToHomeScreen();
        }
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
        btnRegister = root.findViewById(R.id.btnRegister);
        databaseHelper = new FirebaseDatabaseHelper();
        member = new Member();
    }

    private void InitializeMember() {
        member.setFirstname(etFirstname.getText().toString().trim());
        member.setLastname(etLastname.getText().toString().trim());
        member.setAddress(etAddress.getText().toString().trim());
        member.setPostalCode(etPostalCode.getText().toString().trim());
        member.setCity(etCity.getText().toString().trim());
        member.setTelephone(Long.parseLong(etTelephone.getText().toString().trim()));
        member.setInstrument(etInstrument.getText().toString().trim());
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

        return inputIsValid;
    }

    @Override // Deze methode wordt opgeroepen wanneer een datum geselecteerd wordt in de DatePickerDialog
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateSelected = true;
        tvDisplayDate.setError(null);
        String date = "" + dayOfMonth + "/" + (month + 1) + "/" + year;
        String result = "Geboortedatum: " + date;

        member.setBirthdate(date);
        tvDisplayDate.setText(result);
    }

    private void goToHomeScreen() {
        Intent intentToHomeSreen = new Intent(getActivity(), HomeFragment.class);
        startActivity(intentToHomeSreen);
    }
}
