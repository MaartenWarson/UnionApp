package be.pxl.unionapp.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
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
    private List<Member> members = new ArrayList<>();

    public FirebaseDatabaseHelper(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Members");
    }

    public FirebaseDatabaseHelper(String memberId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Members").child(memberId);
    }

    public void addMember(Member member) {
        String id = databaseReference.push().getKey(); // push() creëert een unieke string in 'Members' in Firebase
        member.setMemberId(id);

        databaseReference.child(id).setValue(member);
    }

    public void updateMember(Member member) {
        databaseReference.child(member.getMemberId()).setValue(member);
    }

    public void readMembers(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            // onDataChange wordt altijd aangeroepen als er iets verandert in de database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                members.clear();
                List<String> keys = new ArrayList<>();

                // DataSnapshot bevat key en value van een specifieke node (dataSnapshot.getChildren bevat de key en value van members)
                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Member member = keyNode.getValue(Member.class);
                    members.add(member);
                }

                dataStatus.DataIsLoaded(members, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteMember() {
        databaseReference.removeValue();
    }
}
