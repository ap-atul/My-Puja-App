package company.brandmore.com.mypooja.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pooja;
import company.brandmore.com.mypooja.utils.mapActivity;
import company.brandmore.com.mypooja.utils.validationInput;

public class addPooja extends AppCompatActivity {

    public static TextView time, date;
    private EditText pincode, details;
    private Spinner name;
    private TextView location;
    private ProgressBar pb_addr;
    private Button submit;

    public double lat, lng;
    public String address, pincodeTxt, city;

    public validationInput validator;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pooja);

        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        name = findViewById(R.id.name);
        pincode = findViewById(R.id.pincode);
        details = findViewById(R.id.details);
        location = findViewById(R.id.location);
        pb_addr = findViewById(R.id.pb_addr);
        submit = findViewById(R.id.submitPooja);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(addPooja.this, R.array.skill, R.layout.spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name.setAdapter(adapter1);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("pooja/"+FirebaseAuth.getInstance().getCurrentUser().getUid());

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(addPooja.this, mapActivity.class), 10);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    addDataToFirebase();
                }
            }
        });

    }

    private void addDataToFirebase() {
        pooja obj = new pooja();
        obj.setTitle(name.getSelectedItem().toString());
        obj.setPlace(address);
        obj.setPincode(pincodeTxt);
        obj.setTime(time.getText().toString());
        obj.setDate(date.getText().toString());
        obj.setDetails(details.getText().toString());
        obj.setBid("0");
        obj.setPayment("f");

        String key = myRef.push().getKey();
        obj.setRid(key);

        myRef.child(key).setValue(obj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(addPooja.this, "Pooja successfully added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addPooja.this, "Pooja adding failed try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateData() {
        validator = new validationInput();

        if(!validator.checkFields("Please enter Pooja Location", location))
            return false;
        if(!validator.checkFields("Please enter Pooja Pincode", pincode))
            return false;
        if(!validator.checkFields("Please enter Pooja Date", date))
            return false;
        if(!validator.checkFields("Please enter Pooja Time", time))
            return false;
        if(!validator.checkFields("Please enter Pooja Details", details))
            return false;

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 1) {
            lng = data.getDoubleExtra("longitude", 0);
            lat = data.getDoubleExtra("latitude", 0);
            Log.d("pooja", "onActivityResult: result received from map");
            LoadAddressAsyncTask task = new LoadAddressAsyncTask(this);
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
            Log.d("pooja", "LoadAddressAsyncTask: "+ Geocoder.isPresent());
        }

        @Override
        protected void onPreExecute() {
            pb_addr.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("pooja", "LoadAddressAsyncTask: "+ lat+" "+lng);
                List<Address> al = geocoder.getFromLocation(lat,lng,5);
                myLoc = al.get(0);
                for(Address a:al){
                    Log.d("pooja", "doInBackground: "+a.toString());
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
            pb_addr.setVisibility(View.GONE);
            if(address != null) {
                location.setText(address);
                pincode.setText(pincodeTxt);
            }
        }
    }

//for time
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time.setText(hourOfDay + ":" + minute);
        }
    }

    //for calender
    public static class  DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date.setText(day + "/" + month + "/" + year);
        }
    }
}
