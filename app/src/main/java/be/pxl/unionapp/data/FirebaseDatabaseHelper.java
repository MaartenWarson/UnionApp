package be.pxl.unionapp.data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import be.pxl.unionapp.domain.Member;

public class FirebaseDatabaseHelper {
    private DatabaseReference databaseReference;
    private long maxId = 0;

    public FirebaseDatabaseHelper(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Members");
    }

    public void AddMember(Member member) {
        databaseReference.child(member.getFirstname() + " " + member.getLastname()).setValue(member);
    }
}
