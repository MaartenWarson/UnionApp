package be.pxl.unionapp.activities.masterdetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.UpdateActivity;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

// DetailActivity - enkel in LANDSCAPE (MemberDetaiLFragment is equivalent voor MemberDetailActivity)
public class MemberDetailFragment extends Fragment implements  View.OnClickListener {
    private Member member;
    private TextView tvFirstname, tvLastname, tvBirthdate, tvAddress, tvPostalCode, tvCity, tvTelephone, tvInstrument;
    private ImageView ivProfilePicture, ivGoogleMaps, ivPhoneCall;
    private Button btnUpdate, btnDelete;
    private static final String TAG = "MemberDetailFragment";
    private String address, city, telephone;

    public MemberDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.member_detail, container, false);

        init(rootView);
        Log.i(TAG, "Initialized successfully");

        initializeMember();
        Log.i(TAG, "Member initialized successfully");

        if (member != null) {
            fillFieldsWithData();
            Log.i(TAG, "Fields filled successfully");
        }

        // OnClickListeners declareren
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        ivGoogleMaps.setOnClickListener(this);
        ivPhoneCall.setOnClickListener(this);

        return rootView;
    }

    private void init(View root) {
        tvFirstname = root.findViewById(R.id.firstname_database);
        tvLastname = root.findViewById(R.id.lastname_database);
        tvBirthdate = root.findViewById(R.id.birthdate_database);
        tvAddress = root.findViewById(R.id.address_database);
        tvPostalCode = root.findViewById(R.id.postalCode_database);
        tvCity = root.findViewById(R.id.city_database);
        tvTelephone = root.findViewById(R.id.telephone_database);
        tvInstrument = root.findViewById(R.id.instrument_database);
        ivProfilePicture = root.findViewById(R.id.ivProfilePicture);
        ivGoogleMaps = root.findViewById(R.id.ivGoogleMaps);
        ivPhoneCall = root.findViewById(R.id.ivPhoneCall);
        btnUpdate = root.findViewById(R.id.btnUpdate);
        btnDelete = root.findViewById(R.id.btnDelete);
    }

    private void initializeMember() {
        member = new Member();

        // Member initializeren met gegevens afkomstig van MemberListActivity
        member.setMemberId(getArguments().getString("memberId"));
        member.setFirstname(getArguments().getString("firstname"));
        member.setLastname(getArguments().getString("lastname"));
        member.setBirthdate(getArguments().getString("birthdate"));
        member.setAddress(getArguments().getString("address"));
        member.setPostalCode(getArguments().getString("postalCode"));
        member.setCity(getArguments().getString("city"));
        member.setTelephone(getArguments().getLong("telephone"));
        member.setInstrument(getArguments().getString("instrument"));
    }

    private void fillFieldsWithData() {
        // Velden vullen met de (geïnitialiseerde) waarden van de aangemaakte member hier
        tvFirstname.setText(member.getFirstname());
        tvLastname.setText(member.getLastname());
        tvBirthdate.setText(member.getBirthdate());
        tvAddress.setText(member.getAddress());
        tvPostalCode.setText(member.getPostalCode());
        tvCity.setText(member.getCity());
        String telephoneString = "0" + member.getTelephone();
        tvTelephone.setText(telephoneString);
        tvInstrument.setText(member.getInstrument());

        // Variabelen initialiseren voor Google Maps-link
        address = member.getAddress();
        city = member.getCity();
        telephone = "0" + member.getTelephone();

        fillImageView();
    }

    private void fillImageView() {
        String imageName = member.getMemberId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i(TAG, "Image downloaded from Firebase Storage successfully");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfilePicture.setImageBitmap(bitmap);

                Log.i(TAG, "Image posted in ImageView successfully");
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            // Naar activity gaan om gegevens van lid te wijzigen
            Log.i(TAG, "Update-button clicked");

            goToUpdateActivity();
        }
        else if (v.getId() == R.id.btnDelete) {
            // Lid verwijderen
            Log.i(TAG, "Delete-button clicked");

            createDeleteDialog(member.getMemberId());
        } else if (v.getId() == R.id.ivGoogleMaps) {
            // Google Maps openen
            openGoogleMaps();
        } else if (v.getId() == R.id.ivPhoneCall) {
            // Telefoneren
            makePhoneCall();
        }
    }

    private void goToUpdateActivity() {
        // Intent maken
        Intent intentToUpdateActivity = new Intent(this.getContext(), UpdateActivity.class);

        // Gegevens meegeven aan de intent die getoond moeten worden in UpdateActivity
        intentToUpdateActivity.putExtra("memberId", member.getMemberId());
        intentToUpdateActivity.putExtra("firstname", member.getFirstname());
        intentToUpdateActivity.putExtra("lastname", member.getLastname());
        intentToUpdateActivity.putExtra("birthdate", member.getBirthdate());
        intentToUpdateActivity.putExtra("address", member.getAddress());
        intentToUpdateActivity.putExtra("postalCode", member.getPostalCode());
        intentToUpdateActivity.putExtra("city", member.getCity());
        intentToUpdateActivity.putExtra("telephone", member.getTelephone());
        intentToUpdateActivity.putExtra("instrument", member.getInstrument());

        // Naar UpdateActivity gaan
        startActivity(intentToUpdateActivity);
    }

    // Popup-venster om bevestiging te vragen om lid te verijwderen
    private void createDeleteDialog(final String memberId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Weet je zeker dat je " + member.getFirstname() + " " + member.getLastname() + " wilt verwijderen?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Delete member confirmed");

                deleteMember(memberId); // Lid verwijderen uit database
            }
        });

        alertDialog.setNegativeButton("Nee", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Delete member denied");
            }
        });

        alertDialog.create().show();
    }

    // Lid verwijderen uit database
    private void deleteMember(String memberId) {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(memberId);
        databaseHelper.deleteMember();

        String toastMessage = member.getFirstname() + " " + member.getLastname() + " is verwijderd";
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        Log.i(TAG, "Member deleted succesfully");

        deleteProfilePicture();

        // Naar MemberListActivity gaan
        Intent intentToMemberListActivity = new Intent(getContext(), MemberListActivity.class);
        startActivity(intentToMemberListActivity);
    }

    // Profielfoto van desbetreffend lid uit de storage verwijderen
    private void deleteProfilePicture() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(member.getMemberId());

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Profilepicture deleted successfully");
            }
        });
    }

    // Google Maps openen
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
        Intent intentToMakePhoneCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone));

        startActivity(intentToMakePhoneCall);
    }
}
