package company.brandmore.com.mypooja.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import company.brandmore.com.mypooja.R;

public class messageListAdapter extends RecyclerView.Adapter<messageListAdapter.MyViewHolder>{

    public List<String> msg, utype;
    public Context context;

    public messageListAdapter(List<String> msg, List<String> utype, Context context) {
        this.msg = msg;
        this.utype = utype;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item, viewGroup, false);
        return new messageListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        if(utype.get(i).equals("r")){
            ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) myViewHolder.cardView1.getLayoutParams();
            cardViewMarginParams.setMargins(0, 5, 50, 5);
            myViewHolder.cardView1.requestLayout();  //Dont forget this line
            myViewHolder.msgTV.setTextColor(context.getResources().getColor(R.color.white));
            myViewHolder.msgTV.setBackgroundColor(context.getResources().getColor(R.color.reg_theme_person));
        }else{
            ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) myViewHolder.cardView1.getLayoutParams();
            cardViewMarginParams.setMargins(50, 5, 0, 5);
            myViewHolder.cardView1.requestLayout();  //Dont forget this line
            myViewHolder.msgTV.setTextColor(context.getResources().getColor(R.color.reg_theme_person));
            myViewHolder.msgTV.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        myViewHolder.msgTV.setText(msg.get(i));
    }

    @Override
    public int getItemCount() {
        return msg.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView msgTV;
        public CardView cardView1;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            msgTV = itemView.findViewById(R.id.msg);
            cardView1 = itemView.findViewById(R.id.cardview1);
        }
    }
}
