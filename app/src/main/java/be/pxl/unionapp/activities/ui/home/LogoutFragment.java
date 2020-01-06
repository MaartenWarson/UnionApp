package be.pxl.unionapp.activities.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.LoginActivity;

// Dit scherm wordt heel even(!) getoond wanneer een gebruik afmeldt
public class LogoutFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        logout();

        return root;
    }

    private void logout() {
        // Afmelden
        FirebaseAuth.getInstance().signOut();

        // Naar LoginActivity gaan
        Intent intentToLoginActivity = new Intent(getActivity(), LoginActivity.class);
        startActivity(intentToLoginActivity);
    }
}
