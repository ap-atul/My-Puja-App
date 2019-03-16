package company.brandmore.com.mypooja.user;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.loginActivity;
import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.utils.mapActivity;
import company.brandmore.com.mypooja.utils.validationInput;


public class profile extends AppCompatActivity {

    private TextView profileEmail, profileAddress;
    private EditText profileMobile, profileCity, profileName;
    private Button edit_save;
    private ImageView profilePic, imageEdit;
    private ProgressBar progressBar;

    public DatabaseReference database;
    private FirebaseUser user;
    public userPerson person;
    public Uri uploadUri;
    public String address, city, pincodeTxt;
    public double lat, lng;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        profileEmail = findViewById(R.id.profEmail);
        profileName = findViewById(R.id.profName);
        profileMobile = findViewById(R.id.profileMobile);
        profileAddress = findViewById(R.id.profileAddress);
        profileCity = findViewById(R.id.profileCity);
        profilePic = findViewById(R.id.profilePic);
        imageEdit = findViewById(R.id.imageEdit);
        edit_save = findViewById(R.id.edit_save);
        progressBar = findViewById(R.id.profileProgress);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference("users/"+ uid);

        person = homePage.person;
        Picasso.get().load(person.getProfilePic()).into(profilePic);
        if(person != null){
            enableEditText(true);
            profileName.setText(person.getName());
            profileMobile.setText(person.getMobile());
            profileEmail.setText(person.getEmail());
            profileCity.setText(person.getCity());
            profileAddress.setText(person.getAddress());
            profileEmail.setText(person.getEmail());

            progressBar.setVisibility(View.GONE);
            enableEditText(false);
        }

        //location
        profileAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(profile.this, mapActivity.class), 10);
            }
        });

        edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_save.getText().toString().equals("Edit")){
                    imageEdit.setVisibility(View.VISIBLE);
                    edit_save.setText("Save");
                    enableEditText(true);
                }else{
                    if(validateData()){
                        getUploadURL();
                        enableEditText(false);
                        startActivity(new Intent(profile.this, homePage.class));
                    }
                }
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
        if(!validator.checkFields("Enter your Full Name", profileName))
            return false;
        if(!validator.checkFields("Enter your Mobile Number", profileMobile))
            return false;
        if(!validator.checkFields("Enter your City", profileCity))
            return false;
        return true;
    }

    private void enableEditText(Boolean b) {
        profileName.setEnabled(b);
        profileMobile.setEnabled(b);
        profileCity.setEnabled(b);
        profileAddress.setEnabled(b);
    }

    private void addDataToFirebase() {
        userPerson person1 = new userPerson();
        person1.setName(profileName.getText().toString().trim());
        person1.setMobile(profileMobile.getText().toString().trim());
        person1.setAddress(profileAddress.getText().toString().trim());
        person1.setCity(profileCity.getText().toString().trim());
        person1.setEmail(profileEmail.getText().toString().trim());
        person1.setUserType(person.getUserType());
        if(uploadUri != null)
            person1.setProfilePic(uploadUri.toString());
        else
            person1.setProfilePic(person.getProfilePic());


        database.setValue(person1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(profile.this, "Profile updated", Toast.LENGTH_SHORT).show();
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
                            addDataToFirebase();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(profile.this, "Image upload failed", Toast.LENGTH_SHORT).show();
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
                        profilePic.setImageBitmap(bitmap);
                    }
        }//if (requestCode == 10 && resultCode == 1)
        else  if (requestCode == 10 && resultCode == 1) {
            lng = data.getDoubleExtra("longitude", 0);
            lat = data.getDoubleExtra("latitude", 0);
            Log.d("profile", "onActivityResult: result received from map");
            profile.LoadAddressAsyncTask task = new profile.LoadAddressAsyncTask(this);
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
                profileAddress.setText(address);
                profileAddress.refreshDrawableState();
                profileCity.setText(city + " " +pincodeTxt);
            }
        }
    }
}
