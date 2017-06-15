package shantanu.docmate.Data;

/**
 * Created by SHAAN on 16-06-17.
 */

public class Patient {
    private String profilePic;
    private String gender;
    private String name;
    private String age;
    private String disease;
    private String phone;
    private String bloodGroup;

    public Patient(String profilePic, String gender, String name, String age, String disease, String phone, String bloodGroup) {
        this.profilePic = profilePic;
        this.gender = gender;
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}
