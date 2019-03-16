package company.brandmore.com.mypooja.pandit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.loginActivity;
import company.brandmore.com.mypooja.models.pandit;
import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.user.homePage;
import company.brandmore.com.mypooja.user.profile;
import company.brandmore.com.mypooja.utils.mapActivity;
import company.brandmore.com.mypooja.utils.skillAdapter;
import company.brandmore.com.mypooja.utils.validationInput;

public class profilePandit extends AppCompatActivity {

    private TextView panditEmail, panditAddress, addSkills;
    private EditText panditMobile, panditCity, panditName, panditExperience;
    private Button edit_save;
    private ImageView panditPic, imageEdit;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public DatabaseReference database;
    public pandit panditObject;
    public String skillArray = "";
    public List<String> skills = new ArrayList<>();
    private FirebaseUser user;
    public Uri uploadUri = null;
    public String address, city, pincodeTxt;
    public double lat, lng;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pandit);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        panditEmail = findViewById(R.id.panditEmail);
        panditName = findViewById(R.id.panditName);
        panditMobile = findViewById(R.id.panditMobile);
        panditAddress = findViewById(R.id.panditAddress);
        panditCity = findViewById(R.id.panditCity);
        panditPic = findViewById(R.id.panditPic);
        imageEdit = findViewById(R.id.imageEdit1);
        edit_save = findViewById(R.id.edit_save2);
        progressBar = findViewById(R.id.panditProgress);
        addSkills = findViewById(R.id.addSkills);
        panditExperience = findViewById(R.id.panditExperience);

        recyclerView = findViewById(R.id.panditSkills);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        panditEmail.setText(user.getEmail());
        database = FirebaseDatabase.getInstance().getReference("pandits/"+ uid);

        panditObject = panditHomePage.getPanditObject();
        if(panditObject != null){
            enableEditText(true);
            progressBar.setVisibility(View.VISIBLE);
            panditName.setText(panditObject.getName());
            panditMobile.setText(panditObject.getMobile());
            panditAddress.setText(panditObject.getAddress());
            panditCity.setText(panditObject.getCity());
            Picasso.get().load(panditObject.getProfilePic()).into(panditPic);
            panditExperience.setText(panditObject.getExperience());

            List<String> list = Arrays.asList(panditObject.getSkills().split(","));
            int count = list.size();
            String[] array = getResources().getStringArray(R.array.skill);
            for(int i = 0; i < count; i++){
                skills.add(array[Integer.parseInt(list.get(i))]);
            }
            skillArray = panditObject.getSkills();
            adapter = new skillAdapter(skills, profilePandit.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            enableEditText(false);
            progressBar.setVisibility(View.GONE);
        }

        //location
        panditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                startActivityForResult(new Intent(profilePandit.this, mapActivity.class), 10);
                progressBar.setVisibility(View.GONE);
            }
        });

        edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(edit_save.getText().toString().equals("Edit")){
                    imageEdit.setVisibility(View.VISIBLE);
                    edit_save.setText("Save");
                    enableEditText(true);
                }else{
                    if(validateData()){
                        getUploadURL();
                        enableEditText(false);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        addSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSkillsFromArray();
                recyclerView.smoothScrollToPosition(skills.size());
            }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });
    }

    private boolean validateData() {
        validationInput validator = new validationInput();
        if(!validator.checkFields("Enter your Full Name", panditName))
            return false;
        if(!validator.checkFields("Enter your Mobile Number", panditMobile))
            return false;
        if(!validator.checkFields("Enter your City", panditCity))
            return false;
        if(!validator.checkFields("Enter your Experience in years", panditExperience))
            return false;
        return true;
    }


    //TODO: here
    private void addSkillsFromArray() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(profilePandit.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_skill, null);

        mBuilder.setView(view);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final Spinner spinner = view.findViewById(R.id.skillList);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(view.getContext(), R.array.skill, R.layout.spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        Button add = view.findViewById(R.id.add);
        Button cancel = view.findViewById(R.id.cancel);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skills.contains(spinner.getSelectedItem().toString())){
                    skills.add(spinner.getSelectedItem().toString());
                    skillArray = Integer.toString(spinner.getSelectedItemPosition())  + "," + skillArray;
                    edit_save.setText("Save");
                    dialog.dismiss();
                    adapter = new skillAdapter(skills, view.getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }else
                    Toast.makeText(view.getContext(), "Skill already added", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void enableEditText(Boolean b) {
        panditName.setEnabled(b);
        panditMobile.setEnabled(b);
        panditCity.setEnabled(b);
        panditAddress.setEnabled(b);
        panditExperience.setEnabled(b);
    }

    private void addDataToFirebase() {
        pandit person1 = new pandit();
        person1.setName(panditName.getText().toString().trim());
        person1.setMobile(panditMobile.getText().toString().trim());
        person1.setAddress(panditAddress.getText().toString().trim());
        person1.setCity(panditCity.getText().toString().trim());
        person1.setEmail(panditEmail.getText().toString().trim());
        if(uploadUri != null)
            person1.setProfilePic(uploadUri.toString());
        else{
            if(panditObject != null)
                person1.setProfilePic(panditObject.getProfilePic());
        }
        person1.setExperience(panditExperience.getText().toString());
        person1.setSkills(skillArray);


        database.setValue(person1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(profilePandit.this, "Profile updated", Toast.LENGTH_SHORT).show();
                imageEdit.setVisibility(View.GONE);
                edit_save.setText("Edit");
            }
        });
    }

    private void getUploadURL() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        if(uploadUri != null){
            storageRef.child(user.getUid()).putFile(uploadUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setProgress((int)taskSnapshot.getBytesTransferred());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            uploadUri = taskSnapshot.getDownloadUrl();
                            Toast.makeText(profilePandit.this, uploadUri.toString(), Toast.LENGTH_SHORT).show();
                            addDataToFirebase();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(profilePandit.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            edit_save.setText("Edit");
                        }
                    });
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            addDataToFirebase();
        }
    }

    //get map details
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == PICK_IMAGE){
            if(resultCode == RESULT_OK){
                uploadUri = data.getData();
                String[] projecttion = {MediaStore.Images.Media.DATA}; //fetch media path

                Cursor cursor = getApplicationContext().getContentResolver().query(uploadUri, projecttion, null, null, null);
                cursor.moveToFirst();

                int column_index = cursor.getColumnIndex(projecttion[0]);
                String filepath = cursor.getString(column_index);
                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                panditPic.setImageBitmap(bitmap);
            }
        }//if (requestCode == 10 && resultCode == 1)
        else  if (requestCode == 10 && resultCode == 1) {
            lng = data.getDoubleExtra("longitude", 0);
            lat = data.getDoubleExtra("latitude", 0);
            Log.d("profile", "onActivityResult: result received from map");
            profilePandit.LoadAddressAsyncTask task = new profilePandit.LoadAddressAsyncTask(this);
            task.execute();
        }
    }

    //for map details
    class LoadAddressAsyncTask extends AsyncTask<Void,Void,Void> {

        Context context;
        Geocoder geocoder;
        Address myLoc;

        public LoadAddressAsyncTask(Context context) {
            this.context = context;
            geocoder=new Geocoder(context);
            Log.d("profile", "LoadAddressAsyncTask: "+ Geocoder.isPresent());
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("profile", "LoadAddressAsyncTask: "+ lat+" "+lng);
                List<Address> al = geocoder.getFromLocation(lat,lng,5);
                myLoc = al.get(0);
                for(Address a:al){
                    Log.d("profile", "doInBackground: "+a.toString());
                }
                address = myLoc.getAddressLine(0);
                city = myLoc.getLocality();
                pincodeTxt = myLoc.getPostalCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            if(address != null) {
                panditAddress.setText(address);
                panditAddress.refreshDrawableState();
                panditCity.setText(city + " " +pincodeTxt);
            }
        }
    }
}
