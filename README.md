# My-Puja-App
A service provider app which accept user requirements for certain ritual (hindu puja) and lets pandits bid on it and contacted to get the task done.

# Technologies Used: 
 1. Firebase ( Authentication, Realtime Database & Cloud Storage (Profile Image saving))
 2. Picasso (Profile Image fetching)
 3. RazorPay API :  Checkout SDK (Accepting Payment) 
 4. Google Maps (location acceptance) Check manifest for API keys.

# Database Structure:
 ## Users:  Two user types 
    * Yajman(Common person)
    * Pandit Ji.

 ## JSON for User object: 
  1. This will be used only for Yajman 
  
  ~~~
		"users" : {
    "firebase_auth_uid(user.getUid())" : {
      "address" : "",
      "city" : "",
      "email" : "",
      "mobile" : "",
      "name" : "",
      "profilePic" : "image_upload_link",
      "userType" : "Yajman" or Pandit Ji
    }
  ~~~
    
 * PanditJI has only two attributes: email and userType.

 ~~~
		"users" : {
    "firebase_auth_uid(user.getUid())" : {
      "email" : "",
      "userType" : "Yajman" or Pandit Ji
    }
 ~~~

2.  Pooja: requirements added by Yajman:

```
		"pooja" : {
    "firebase_auth_uid" : {
      "random_push_id" : {
        "bid" : "no_of_bids",
        "date" : "date_posted",
        "details" : "",
        "payment" : "status done or not",
        "pincode" : "",
        "place" : "",
        "rid" : "same id as parent for ease of fetching data of booking",
        "time" : "",
        "title" : "one of the 41pujas mentioned in strings.xml"
      }
```

3.  Pandits: Storing profile data of pandits
      
    ~~~
    "pandits" : {
      "firebase_auth_uid" : {
        "address" : "",
        "city" : "",
        "email" :",
        "experience" : "”,
        "mobile" : "",
        "name" : "",
        "profilePic" :”",
        "rating" : "",
        "skills" : "26,2,5,6,20,0" //index of puja mention in strings.xml
      }
    ~~~

4. Bookings: Storing all the bids of Pandit Ji on certain puja

     ~~~
     "booking" : {
      "puja_random_id" : {
        "pandit_uid_firebase_auth" : {
          "alloc" : "f", //status if pandit ji booked
          "fees" : "3300"  //fees with commission
      }
    }
     ~~~

5.  Chats : communication between pandit ji and yajman

    ~~~
    "chats" : {
       "Yajman_auth_id + Pandit JI _auth_id" : {
                "20190219203306" : {   //date and time: yyyyMMddhhMMss
                    "message" : "Hi, let's get to details",
                    "receiver" : "firebase_auth_id"
                },
    ~~~


# File Structure (Java Activities):

 ```
Packages: 
  Common: similar activities
      Chats  //chat list of users
      Chat Messages  //messages
      ContactUs  
      
  Models
      Message //message and receiver
      Pandit  //pandit details
      Pooja   //puja details
      UserPerson   //yajman details

  Pandit
      Pandit home page
      Profile Page
      Search Pooja List // contains skill matching methods
      Selected Pooja // one selected from list and bid is performed
      
  User
      Add Puja  // add the requirements
      Home page
      Pandit List //nids of pandits
      Payment Result // payment api calls 
      Pooja list // all the poojas of the user
      Profile
      Selected Pandit  //details of the most favourable pandit
      Selected Pooja  //details of the current pooja
      
  Utils  // all the adapters
      chatList //for chats 
      Map //for location
      Message List  //for messages sent and received 
      PanditList  //for users’ bids list
      Pooja List  //common adapter for both user and pandit
      Skill Adapter  //for pandit ji profile
      Validation input //validator for edittext fields
      
  Login Activity  //firebase auth and usertype checking  // also has password reset method

  Registration  //create firebase user, email verification link sending

  Splash 

 ```
 
# Notes:
 * Before changing themes make sure to check styles.xml , two themes are declared for each user.
 * Make sure not to change Firebase Listeners, it will create conflicts and can create null data.
 * Firebase UI is not used, need to make some changes if used
 * Firebase rules are in test mode.
 * Razor Pay account is not yet verified with bank details.
 * Enable Firebase offline support. In Splash or somewhere else.
