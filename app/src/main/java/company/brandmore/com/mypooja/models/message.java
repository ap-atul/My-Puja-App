package company.brandmore.com.mypooja.models;

public class message {
    String receiver, message;

    public message(){}

    public message(String receiver, String message){
        this.receiver = receiver;
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
