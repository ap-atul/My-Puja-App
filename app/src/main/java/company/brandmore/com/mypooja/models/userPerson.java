package company.brandmore.com.mypooja.models;

public class userPerson {
    String email = null, profilePic = null, name = null, mobile = null, city = null, address = null, userType = null;

    public userPerson(String email, String profilePic, String name, String mobile, String city, String userType) {
        this.email = email;
        this.profilePic = profilePic;
        this.name = name;
        this.mobile = mobile;
        this.city = city;
        this.userType = userType;
    }

    public userPerson(){ }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


}
