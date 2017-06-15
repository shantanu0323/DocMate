package shantanu.docmate.Data;

/**
 * Created by SHAAN on 16-06-17.
 */

public class Doctor {
    private String profilePic;
    private String gender;
    private String name;
    private String degree;
    private String address;
    private String phone;
    private String specializtion;

    public Doctor(String profilePic, String gender, String name, String degree, String address, String phone, String specializtion) {
        this.profilePic = profilePic;
        this.gender = gender;
        this.name = name;
        this.degree = degree;
        this.address = address;
        this.phone = phone;
        this.specializtion = specializtion;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecializtion() {
        return specializtion;
    }

    public void setSpecializtion(String specializtion) {
        this.specializtion = specializtion;
    }
}
