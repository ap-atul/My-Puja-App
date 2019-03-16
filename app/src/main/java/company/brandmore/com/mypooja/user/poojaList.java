package company.brandmore.com.mypooja.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pooja;
import company.brandmore.com.mypooja.utils.poojaListAdapter;

public class poojaList extends AppCompatActivity {

    private EditText searchBox;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public List<pooja> list = new ArrayList<>();
    public List<pooja> newList = new ArrayList<>();
    public static String classPath = "selectedPooja";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pooja_list);

        searchBox = findViewById(R.id.searchBox);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pooja/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        getDataFromFirebase(databaseReference);

        searchBox.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(""))
                    getDataFromFirebase(databaseReference);
                else
                    getDataFromSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable mEdit)
            {
            }
        });

//        getDataFromFirebase(databaseReference);

    }

    private void getDataFromFirebase(DatabaseReference databaseReference) {
        list.clear();
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot == null)
                            Toast.makeText(poojaList.this, "No data found! Add a Puja on home page", Toast.LENGTH_SHORT).show();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            list.add(dsp.getValue(pooja.class));
                        }
                        adapter = new poojaListAdapter(poojaList.this, list, classPath);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(poojaList.this, "Server Error! Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDataFromSearchQuery(String searchString) {
        newList.clear();
        for(int i = 0; i< list.size(); i++){
            pooja object = list.get(i);
            if(object.getTitle().toLowerCase().trim().contains(searchString) || object.getPincode().toLowerCase().trim().contains(searchString)){
                newList.add(object);
            }
        }
        adapter = new poojaListAdapter(poojaList.this, newList, classPath);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}
