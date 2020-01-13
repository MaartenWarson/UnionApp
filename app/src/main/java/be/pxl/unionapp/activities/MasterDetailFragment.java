package be.pxl.unionapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.masterdetail.MemberListActivity;

// Dit Fragment wordt heel even(!) getoond wanneer naar MemberListActivity gegaan wordt.
// Dit is omdat vanuit het sandwichmenu enkel naar een Fragment gegaan kan worden, en ik wil naar een Activity gaan
public class MasterDetailFragment extends Fragment {
    public MasterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startActivity(new Intent(this.getContext(), MemberListActivity.class));
        return inflater.inflate(R.layout.fragment_master_detail, container, false);
    }
}
