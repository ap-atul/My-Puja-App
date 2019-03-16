package company.brandmore.com.mypooja.utils;

import android.support.v4.view.KeyEventDispatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class validationInput {
    public boolean checkEmail(EditText component, String data, String msg){
        if(Patterns.EMAIL_ADDRESS.matcher(data).matches() && (!data.isEmpty())){
            return true;
        }
        else {
            component.setError(msg);
            return false;
        }
    }

    public boolean checkPassword(String pass, String repass, EditText rePass){
        if(pass.equals(repass))
            return true;
        else{
            rePass.setError("Password does not match");
            return false;
        }
    }

    public boolean checkFields(String msg, TextView comp){
        if(comp.getText().toString().trim().length() == 0){
            comp.setError(msg);
            return false;
        }
        return true;
    }
}
