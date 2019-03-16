package company.brandmore.com.mypooja.pandit;

import android.content.Intent;
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
import company.brandmore.com.mypooja.utils.poojaListAdapter;

public class searchPoojaList extends AppCompatActivity {

    private EditText searchBox;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public List<pooja> list = new ArrayList<>();
    public List<pooja> newList = new ArrayList<>();
    public static Iterable<DataSnapshot> poojaChildren;
    public static String classPath = "selectedPoojaPandit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pooja_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pooja");
        getDataFromFirebase(databaseReference);
        searchBox = findViewById(R.id.searchBox);
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

    }

    private void getDataFromSearchQuery(String searchString) {
        newList.clear();
        for(int i = 0; i < list.size(); i++){
            pooja object = list.get(i);
            if(object.getTitle().toLowerCase().trim().contains(searchString) || object.getPincode().toLowerCase().trim().contains(searchString)){
                newList.add(object);
            }
        }
        adapter = new poojaListAdapter(searchPoojaList.this, newList, classPath);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromFirebase(DatabaseReference databaseReference) {
        list.clear();
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
//                            Toast.makeText(searchPoojaList.this, Long.toString(dsp.getChildrenCount()), Toast.LENGTH_LONG).show();
                            poojaChildren = dsp.getChildren();
//                            pooja obj = dsp.getValue(pooja.class);
//                            Toast.makeText(searchPoojaList.this, dsp.toString(), Toast.LENGTH_LONG).show();
//                            if(checkForMatching(obj))
//                                list.add(obj);
                            addtoList(poojaChildren);
                        }
                        if(list != null){
                            adapter = new poojaListAdapter(searchPoojaList.this, list, classPath);
                            recyclerView.setAdapter(adapter);
                        }else{
                            Toast.makeText(searchPoojaList.this, "No matching Pujas found! Update your skills for a perfect match", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(searchPoojaList.this, "Server Error! Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addtoList(Iterable<DataSnapshot> poojaChildren) {
        for (DataSnapshot poojaObject : poojaChildren) {
            pooja obj = poojaObject.getValue(pooja.class);
            if(checkForMatching(obj))
                list.add(obj);
        }
    }

    private boolean checkForMatching(pooja obj) {
        pandit object = panditHomePage.getPanditObject();
        String[] skills = getResources().getStringArray(R.array.skill);
        String name = obj.getTitle();
        String[] panditSkill = object.getSkills().split(",");
        int ind = -1;

        for(int i = 0; i < 41; i++){
            if(name.equals(skills[i])){
                ind = i;
            }
        }

        for(int i = 0; i < panditSkill.length ; i++){
            if(panditSkill[i].equals(Integer.toString(ind))){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(searchPoojaList.this, panditHomePage.class);
        finish();
        startActivity(intent);
    }
}
