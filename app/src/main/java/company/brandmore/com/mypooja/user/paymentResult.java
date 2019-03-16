package company.brandmore.com.mypooja.user;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import company.brandmore.com.mypooja.R;

public class paymentResult extends Activity implements PaymentResultListener{

    int fees = Integer.parseInt(selectedPandit.totalFees);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);


        startPayment();
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             */
            options.put("name", "Brandmore");

            /**
             * Description can be anything
             */
            options.put("description", "Order#"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", fees*100);
            checkout.open(activity, options);
        } catch(Exception e) {
            Toast.makeText(paymentResult.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            setResult(2, new Intent().putExtra("result", "f"));
            finish();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(paymentResult.this, "Payment successful!", Toast.LENGTH_SHORT).show();
        setResult(2, new Intent().putExtra("result", "s"));
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        //Checkout.INVALID_OPTIONS  Checkout.PAYMENT_CANCELED Checkout.TLS_ERROR
        if(Checkout.NETWORK_ERROR == i)
            Toast.makeText(paymentResult.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        if(Checkout.INVALID_OPTIONS == i)
            Toast.makeText(paymentResult.this, "Check your credentials", Toast.LENGTH_SHORT).show();
        if(Checkout.PAYMENT_CANCELED == i)
            Toast.makeText(paymentResult.this, "Payment has been cancelled", Toast.LENGTH_SHORT).show();
        if(Checkout.TLS_ERROR == i)
            Toast.makeText(paymentResult.this, "Your device does not support TLS", Toast.LENGTH_SHORT).show();

        setResult(2, new Intent().putExtra("result", "f"));
        finish();
    }
}
