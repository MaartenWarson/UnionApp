package be.pxl.unionapp.activities.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import be.pxl.unionapp.R;

// Fragment met het Union-logo
public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "HomeFragment loaded successfully");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}