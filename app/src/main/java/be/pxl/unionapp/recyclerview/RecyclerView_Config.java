package be.pxl.unionapp.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.DetailActivity;
import be.pxl.unionapp.domain.Member;

public class RecyclerView_Config {
    private Context context;
    private MembersAdapter membersAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Member> members, List<String> keys) {
        this.context = context;
        membersAdapter = new MembersAdapter(members, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(membersAdapter);
    }

    public Context getContext() {
        return context;
    }


    class MemberItemView extends RecyclerView.ViewHolder{
        private TextView tvName, tvInstrument;
        private LinearLayout linearLayout;
        String key;

        public MemberItemView(ViewGroup parent) {
            super(LayoutInflater.from(context).inflate(R.layout.member_list_item, parent, false));

            tvName = itemView.findViewById(R.id.item_name);
            tvInstrument = itemView.findViewById(R.id.item_instrument);
            linearLayout = itemView.findViewById(R.id.member_list_item);
        }

        public void bind(Member member, String key) {
            String name = member.getFirstname() + " " + member.getLastname();
            tvName.setText(name);
            tvInstrument.setText(member.getInstrument());
            this.key = key;
        }
    }


    // Deze klasse creëert de member_item_view
    class MembersAdapter extends RecyclerView.Adapter<MemberItemView> {
        private List<Member> members;
        private List<String> keys;

        public MembersAdapter(List<Member> members, List<String> keys) {
            this.members = members;
            this.keys = keys;
        }

        @NonNull
        @Override
        public MemberItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MemberItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MemberItemView holder, final int position) {
            holder.bind(members.get(position), keys.get(position));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Member member = members.get(position);
                    Intent intentToDetailsActivity = new Intent(context, DetailActivity.class);
                    intentToDetailsActivity.putExtra("memberId", member.getMemberId());
                    intentToDetailsActivity.putExtra("firstname", member.getFirstname());
                    intentToDetailsActivity.putExtra("lastname", member.getLastname());
                    intentToDetailsActivity.putExtra("birthdate", member.getBirthdate());
                    intentToDetailsActivity.putExtra("address", member.getAddress());
                    intentToDetailsActivity.putExtra("postalCode", member.getPostalCode());
                    intentToDetailsActivity.putExtra("city", member.getCity());
                    intentToDetailsActivity.putExtra("telephone", member.getTelephone());
                    intentToDetailsActivity.putExtra("instrument", member.getInstrument());

                    context.startActivity(intentToDetailsActivity);
                }
            });
        }

        @Override
        public int getItemCount() {
            return members.size();
        }
    }
}
