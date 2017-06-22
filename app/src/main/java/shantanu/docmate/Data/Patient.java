package shantanu.docmate.Data;

/**
 * Created by SHAAN on 16-06-17.
 */

public class Patient {
    private String profilepic;
    private String gender;
    private String name;
    private String age;
    private String diseases;
    private String phone;
    private String bloodgroup;
    private String email;
    private String password;
    private String uid;

    public Patient() {
    }

    public Patient(String profilePic, String gender, String name, String age, String diseases, String phone, String bloodGroup, String email, String password, String uid) {
        this.profilepic = profilePic;
        this.gender = gender;
        this.name = name;
        this.age = age;
        this.diseases = diseases;
        this.phone = phone;
        this.bloodgroup = bloodGroup;
        this.email = email;
        this.password = password;
        this.uid = uid;
    }

    public Patient(String profilepic, String gender, String name, String age, String diseases, String bloodgroup) {
        this.profilepic = profilepic;
        this.gender = gender;
        this.name = name;
        this.age = age;
        this.diseases = diseases;
        this.bloodgroup = bloodgroup;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }
}
