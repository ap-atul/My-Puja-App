package company.brandmore.com.mypooja.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.common.chatMessages;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.MyViewHolder> {

    public List<String> userNames, userPics, userIds;
    public Context context;

    public chatListAdapter(List<String> userIds, List<String> userNames, List<String> userPics, Context context) {
        this.userIds = userIds;
        this.userNames = userNames;
        this.userPics = userPics;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list_item, viewGroup, false);
        return new chatListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.chatName.setText(userNames.get(i));
        Picasso.get().load(userPics.get(i)).into(myViewHolder.chatPic);
        final int ind = i;
        myViewHolder.chatListRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, chatMessages.class)
                        .putExtra("userID", userIds.get(ind))
                        .putExtra("userName", userNames.get(i))
                        .putExtra("userImage", userPics.get(i)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout chatListRL;
        public TextView chatName;
        public ImageView chatPic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            chatListRL = itemView.findViewById(R.id.chatListRL);
            chatName = itemView.findViewById(R.id.chatName);
            chatPic = itemView.findViewById(R.id.chatPic);
        }
    }
}
