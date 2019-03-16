package company.brandmore.com.mypooja.common;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.loginActivity;
import company.brandmore.com.mypooja.models.message;
import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.utils.messageListAdapter;

//message push format
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//String currentDateandTime = sdf.format(new Date());

public class chatMessages extends AppCompatActivity {

    public String userId, currentUserId, currentDateandTime;
    public String userType;
    public List<String> msgList = new ArrayList<>();
    public List<String> utype = new ArrayList<>();
    public userPerson currentUser = loginActivity.getUserObject();

    private EditText messageText;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ImageView messagePic;
    private TextView messageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        messageText = findViewById(R.id.messageText);
        messagePic = findViewById(R.id.messagePic);
        messageName = findViewById(R.id.messageName);
        recyclerView = findViewById(R.id.messagesRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);;
        recyclerView.setNestedScrollingEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        messageText.clearFocus();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("chats");

        userId = getIntent().getStringExtra("userID");
        messageName.setText(getIntent().getStringExtra("userName"));
        Picasso.get().load(getIntent().getStringExtra("userImage")).into(messagePic);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        currentDateandTime = sdf.format(new Date());
        userType = currentUser.getUserType();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        messageText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (messageText.getRight() - messageText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(messageText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        sendMessageToTheUser(messageText.getText().toString(), database);
                        messageText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });


        if(userType.equals("Yajman")){
            msgList.clear();utype.clear();
            database.child(currentUserId + userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dsp : dataSnapshot.getChildren()){
                        if(dsp != null){
                            message object = dsp.getValue(message.class);
                            addToTheView(object, userId);
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            database.child(userId + currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dsp : dataSnapshot.getChildren()){
                        if(dsp != null){
                            message object = dsp.getValue(message.class);
                            addToTheView(object, userId);
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendMessageToTheUser(String msg, DatabaseReference myRef) {
        msgList.clear();utype.clear();
        if(userType.equals("Yajman")){
            message obj = new message();
            obj.setMessage(msg);
            obj.setReceiver(userId);
//            msgList.add(msg);
//            utype.add("s");
//            adapter = new messageListAdapter(msgList, utype, chatMessages.this);
//            recyclerView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//            recyclerView.scrollToPosition(msgList.size() - 1);

            myRef.child(currentUserId + userId).child(currentDateandTime).setValue(obj);
        }else{
            message obj = new message();
            obj.setMessage(msg);
            obj.setReceiver(userId);
//            msgList.add(msg);
//            utype.add("s");
//            adapter = new messageListAdapter(msgList, utype, chatMessages.this);
//            recyclerView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//            recyclerView.scrollToPosition(msgList.size() - 1);

            myRef.child(userId + currentUserId).child(currentDateandTime).setValue(obj);
        }
    }

    //method to show messages send or received by reading receiver field in message object
    private void addToTheView(message object, String userID) {
        msgList.add(object.getMessage());
        if(currentUserId.equals(object.getReceiver())){
            utype.add("r");
        }
        else{
            utype.add("s");
        }
        adapter = new messageListAdapter(msgList, utype, chatMessages.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyItemInserted(msgList.size());
    }
}
