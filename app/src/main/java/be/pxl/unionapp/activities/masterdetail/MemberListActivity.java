package be.pxl.unionapp.activities.masterdetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.MainActivity;
import be.pxl.unionapp.data.DataStatus;
import be.pxl.unionapp.data.FirebaseDatabaseHelper;
import be.pxl.unionapp.domain.Member;

/*
 * Deze activity is de lijst met members en neemt 2 'versies' aan:
 * Portait => MemberDetailActivity
 * Landscape => MemberDetaiLFragment
 */
public class MemberListActivity extends AppCompatActivity {
    private boolean twoPanes;
    private static final String TAG = "MemberListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back-knop

        // Titel van Actionbar instellen
        setTitle(R.string.showMembers);

        if (checkLandscapeMode()) {
            Log.i(TAG, "In landscape mode");
            twoPanes = true;
        }

        // Recyclerview declareren en implementeren om lijst te maken
        View recyclerView = findViewById(R.id.rv_member_list);
        assert recyclerView != null;
        Log.i(TAG, "Recyclerview declared");

        readMembers((RecyclerView) recyclerView);
    }

    // Wanneer er op het pijltje in de ActionBar wordt geklikt ...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.i(TAG, "Back-button clicked");

            // Intent terug naar MainActivity
            startActivity(new Intent(MemberListActivity.this, MainActivity.class));
            Log.i(TAG, "Went back to previous activity successfully");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkLandscapeMode() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "In Landscape mode");

            return true;
        } else {
            Log.i(TAG, "In Portait mode");

            return false;
        }
    }

    // Members uit Firebase Database halen
    private void readMembers(final RecyclerView recyclerView) {
        new FirebaseDatabaseHelper().readMembers(new DataStatus() {
            @Override
            public void DataIsLoaded(List<Member> members, List<String> keys) {
                Log.i(TAG, "Members loaded from Firebase Database successfully");

                findViewById(R.id.progressBar_read).setVisibility(View.GONE);

                setupRecyclerView(recyclerView, members, keys);
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Member> members, List<String> keys) {
        recyclerView.setAdapter(new MemberRecyclerViewAdapter(this, members, keys, twoPanes ));

        Log.i(TAG, "Adapter set to RecyclerView successfully");
    }


    public static class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder> {
        private final MemberListActivity parentActivity;
        private final List<Member> members;
        private final List<String> keys;
        private final boolean twoPanes;

        MemberRecyclerViewAdapter(MemberListActivity parent, List<Member> members, List<String> keys, boolean twoPanes) {
            this.parentActivity = parent;
            this.members = members;
            this.keys = keys;
            this.twoPanes = twoPanes;

            Log.i(TAG, "Adapter constructed successfully");
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
           // Per MemberItem in de RecyclerView worden foto, naam en instrument getoond
           // Naam
           String name = members.get(position).getFirstname() + " " + members.get(position).getLastname();
           holder.tvName.setText(name);

           // Instrument
           holder.tvInstrument.setText(members.get(position).getInstrument());

           // Profielfoto
            final String imageName = members.get(position).getMemberId();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.ivProfilePicture.setImageBitmap(bitmap);
                }
            });

            // OnClickListener op het volledig item zetten
            holder.itemView.setTag(members.get(position));
            holder.itemView.setOnClickListener(onClickListener);

            Log.i(TAG, "View within RecyclerViewList created successfully");
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        private final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member member = (Member) v.getTag();

                if (twoPanes) {
                    // LANDSCAPE
                    Bundle arguments = new Bundle();

                    // Gegegens meegeven aan fragment a.d.h.v. arguments
                    arguments.putString("memberId", member.getMemberId());
                    arguments.putString("firstname", member.getFirstname());
                    arguments.putString("lastname", member.getLastname());
                    arguments.putString("birthdate", member.getBirthdate());
                    arguments.putString("address", member.getAddress());
                    arguments.putString("postalCode", member.getPostalCode());
                    arguments.putString("city", member.getCity());
                    arguments.putLong("telephone", member.getTelephone());
                    arguments.putString("instrument", member.getInstrument());

                    // MemberDetailFragment openen
                    MemberDetailFragment fragment = new MemberDetailFragment();
                    fragment.setArguments(arguments);
                    parentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.member_detail_landscape, fragment)
                            .commit();
                } else {
                    // PORTRAIT
                    Context context = v.getContext();
                    Intent intentToMemberDetailActivity = new Intent(context, MemberDetailActivity.class);

                    // Gegevens meegeven aan de intent
                    intentToMemberDetailActivity.putExtra("memberId", member.getMemberId());
                    intentToMemberDetailActivity.putExtra("firstname", member.getFirstname());
                    intentToMemberDetailActivity.putExtra("lastname", member.getLastname());
                    intentToMemberDetailActivity.putExtra("birthdate", member.getBirthdate());
                    intentToMemberDetailActivity.putExtra("address", member.getAddress());
                    intentToMemberDetailActivity.putExtra("postalCode", member.getPostalCode());
                    intentToMemberDetailActivity.putExtra("city", member.getCity());
                    intentToMemberDetailActivity.putExtra("telephone", member.getTelephone());
                    intentToMemberDetailActivity.putExtra("instrument", member.getInstrument());

                    // DetailActivity openen
                    context.startActivity(intentToMemberDetailActivity);
                }
            }
        };

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProfilePicture;
            TextView tvName, tvInstrument;

            ViewHolder(View view) {
                super(view);
                ivProfilePicture = view.findViewById(R.id.ivPicture);
                tvName = view.findViewById(R.id.tv_member_name);
                tvInstrument = view.findViewById(R.id.tv_instrument);
            }
        }
    }
}
