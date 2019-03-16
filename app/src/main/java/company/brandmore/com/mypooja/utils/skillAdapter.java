package company.brandmore.com.mypooja.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import company.brandmore.com.mypooja.R;

public class skillAdapter extends RecyclerView.Adapter<skillAdapter.MyViewHolder> {

    public List<String> skill;
    Context context;

    public skillAdapter(List<String> skill, Context context) {
        this.skill = skill;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.skillName.setText(skill.get(i));
    }

    @Override
    public int getItemCount() {
        return skill.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView skillName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            skillName = itemView.findViewById(R.id.textView55);
        }
    }
}
