package be.pxl.unionapp.activities.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import be.pxl.unionapp.R;
import be.pxl.unionapp.data.DataStatus;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;
import be.pxl.unionapp.recyclerview.RecyclerView_Config;


// DEZE FRAGMENT WORDT NIET MEER GEBRUIKT! => MemberListActivity
public class ReadFragment extends Fragment{

    private View root;
    private RecyclerView recyclerView;
    private static final String TAG = "ReadFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_read, container, false);

        init();

        return root;
    }

    private void init() {
        recyclerView = root.findViewById(R.id.recyclerview_members);
        new FirebaseDatabaseHelper().readMembers(new DataStatus() {
            @Override // Wanneer de data geladen is...
            public void DataIsLoaded(List<Member> members, List<String> keys) {
                root.findViewById(R.id.progressBar_read).setVisibility(View.GONE); // ProgressBar verwijderen
                new RecyclerView_Config().setConfig(recyclerView, getContext(), members, keys);
                Log.i(TAG, "Data loaded from Firebase Database successfully");
            }
        });
    }
}
