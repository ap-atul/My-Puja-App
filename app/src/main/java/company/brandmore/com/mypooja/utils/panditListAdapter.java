package company.brandmore.com.mypooja.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pandit;
import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.user.panditList;
import company.brandmore.com.mypooja.user.selectedPandit;

public class panditListAdapter extends RecyclerView.Adapter<panditListAdapter.MyViewHolder> {

    Context context;
    static List<pandit> pandits;

    public panditListAdapter(Context context, List<pandit> pandits) {
        this.context = context;
        this.pandits = pandits;
    }

    public static pandit getSelectedPandit(int id){
        return pandits.get(id);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pandit_list_item, viewGroup, false);
        return new panditListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final int id = i;
        myViewHolder.panditName.setText(pandits.get(i).getName());
        myViewHolder.detailsText.setText("Experience: " + pandits.get(i).getExperience() + "years . Bidded: " + pandits.get(i).getFees());
        myViewHolder.ratingBar.setRating(Float.valueOf(pandits.get(i).getRating()));
        Picasso.get().load(pandits.get(i).getProfilePic()).into(myViewHolder.ListPic);

        myViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, selectedPandit.class).putExtra("id", id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return pandits.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView panditName, detailsText;
        private ImageView ListPic;
        private RatingBar ratingBar;
        private RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            panditName = itemView.findViewById(R.id.panditName);
            relativeLayout = itemView.findViewById(R.id.panditListRL);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            detailsText = itemView.findViewById(R.id.detailsText);
            ListPic = itemView.findViewById(R.id.ListPic);
        }
    }
}
