package be.pxl.unionapp.activities.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import be.pxl.unionapp.R;

public class ReadFragment extends Fragment{

    private View root;
    private Toolbar toolbar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_read, container, false);

        init();

        getActivity().setTitle("TEST");


        return root;
    }

    private void init() {
        toolbar = root.findViewById(R.id.toolbar);
    }
}
