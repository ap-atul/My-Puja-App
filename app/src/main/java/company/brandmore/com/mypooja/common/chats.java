package company.brandmore.com.mypooja.common;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.loginActivity;
import company.brandmore.com.mypooja.models.pandit;
import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.utils.chatListAdapter;

public class chats extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public userPerson user = loginActivity.getUserObject();
    public DatabaseReference database;
    public String userID;
    public List<String> userName = new ArrayList<>();
    public List<String> userPic = new ArrayList<>();
    public List<String> userIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        recyclerView = findViewById(R.id.chatRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        database = FirebaseDatabase.getInstance().getReference("chats/");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        userName.clear();
        userPic.clear();

        if(user.getUserType().equals("Yajman")){
            database.endAt(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dsp2 : dataSnapshot.getChildren()){
                        if(dsp2 != null){
                            getUserProfile(new String[]{dsp2.getKey().replace(userID, "")}, "p");
//                            for(DataSnapshot dsp : dsp2.getChildren()) {
//                                //TODO: save all chats
//                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else{
            database.endAt(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dsp2 : dataSnapshot.getChildren()){
                        getUserProfile(dsp2.getKey().split(userID), "y");
//                        for(DataSnapshot dsp : dsp2.getChildren()) {
//                            //TODO: save all chats
//                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    private void getUserProfile(final String[] userID, String uType) {
        if(uType.equals("y")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"+ userID[0]);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userPerson object = dataSnapshot.getValue(userPerson.class);
                    if(object != null){
                        userName.add(object.getName());
                        userPic.add(object.getProfilePic());
                        userIds.add(userID[0]);
                        setAdapter();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pandits/"+ userID[0]);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        pandit object = dataSnapshot.getValue(pandit.class);
                        if(object != null){
                            userName.add(object.getName());
                            userPic.add(object.getProfilePic());
                            userIds.add(userID[0]);
                            setAdapter();
                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void setAdapter(){
        adapter = new chatListAdapter(userIds, userName, userPic, chats.this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}
