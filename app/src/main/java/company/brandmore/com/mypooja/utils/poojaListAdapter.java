package company.brandmore.com.mypooja.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pooja;
import company.brandmore.com.mypooja.pandit.selectedPoojaPandit;
import company.brandmore.com.mypooja.user.selectedPooja;

public class poojaListAdapter extends RecyclerView.Adapter<poojaListAdapter.MyViewHolder> {

    Context context;
    String classPath;
    static List<pooja> poojas;

    public poojaListAdapter(Context context, List<pooja> poojas, String classPath) {
        this.context = context;
        this.poojas = poojas;
        this.classPath = classPath;
    }

    public static pooja getPooja(int id) {
        return poojas.get(id);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pooja_list_items, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        final int id = i;
        viewHolder.poojaName.setText(poojas.get(i).getTitle());
        viewHolder.detailsText.setText(poojas.get(i).getDate() + " . " + poojas.get(i).getTime() + " . " + poojas.get(i).getBid() + " bids");

        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classPath.equals("selectedPooja"))
                    context.startActivity(new Intent(context, selectedPooja.class).putExtra("id", id));
                else
                    context.startActivity(new Intent(context, selectedPoojaPandit.class).putExtra("id", id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return poojas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView poojaName, detailsText;
        public RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            poojaName = itemView.findViewById(R.id.poojaName);
            detailsText = itemView.findViewById(R.id.detailsText);
            relativeLayout = itemView.findViewById(R.id.itemComponent);
        }
    }
}
