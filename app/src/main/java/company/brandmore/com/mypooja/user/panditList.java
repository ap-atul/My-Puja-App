package company.brandmore.com.mypooja.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pandit;
import company.brandmore.com.mypooja.models.pooja;
import company.brandmore.com.mypooja.utils.panditListAdapter;
import company.brandmore.com.mypooja.utils.poojaListAdapter;

public class panditList extends AppCompatActivity {

    private EditText searchPanditBox;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public int id;
    public pooja selectedPooja;
    public static List<String> keys = new ArrayList<>();
    public static List<String> newkeys = new ArrayList<>();
    public List<pandit> pandits = new ArrayList<>();
    public List<pandit> newList = new ArrayList<>();

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandit_list);

        recyclerView = findViewById(R.id.panditList_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int id  = getIntent().getIntExtra("id", -1);
        selectedPooja = poojaListAdapter.getPooja(id);

        keys.clear();newkeys.clear();pandits.clear();newList.clear();
        database = FirebaseDatabase.getInstance().getReference("booking/"+ selectedPooja.getRid());
        getAllPanditFromDB();
        
        searchPanditBox = findViewById(R.id.searchPanditBox);
        searchPanditBox.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(""))
                    getAllPanditFromDB();
                else
                    getDataFromSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable mEdit)
            {
            }
        });
    }

    private void getDataFromSearchQuery(String searchString) {
        newList.clear();
        for(int i = 0; i< pandits.size(); i++){
            pandit object = pandits.get(i);
            if(object.getName().toLowerCase().trim().contains(searchString) || object.getCity().toLowerCase().trim().contains(searchString)){
                newList.add(object);
                newkeys.add(keys.get(i));
            }
        }
        keys = newkeys;
        adapter = new panditListAdapter(panditList.this, newList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void getAllPanditFromDB() {
        pandits.clear();
        keys.clear();
        if(selectedPooja.getBid().equals("0")){
            Toast.makeText(panditList.this, "No Pandit have bid on your pooja, Try again later", Toast.LENGTH_SHORT).show();
        }else{
            database.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                keys.add(dsp.getKey());
                                getPanditName(dsp.getKey(), dsp);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(panditList.this, "Server Error! Check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void getPanditName(String uid, final DataSnapshot dsp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pandits/"+uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pandit object = dataSnapshot.getValue(pandit.class);
                object.setAlloc((String) dsp.child("alloc").getValue());
                object.setFees((String) dsp.child("fees").getValue());
                pandits.add(object);
                adapter = new panditListAdapter(panditList.this, pandits);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(panditList.this, "Server Error! Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getSelectedPanditKey(int id){
        return keys.get(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        keys.clear();newkeys.clear();pandits.clear();newList.clear();
    }
}
