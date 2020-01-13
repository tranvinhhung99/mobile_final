package hcmus.selab.tvhung;

public class User {
    String email;
    String userID;
    String password;

    public User(String Email, String UserID, String Password)
    {
        email = Email;
        userID = UserID;
        password = Password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserID() {
        return userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
