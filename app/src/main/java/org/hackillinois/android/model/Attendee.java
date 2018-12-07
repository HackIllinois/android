package org.hackillinois.android.model;

public class Attendee {
    private String firstName;
    private String lastName;
    private String email;
    private String diet;
    private String school;
    private String major;

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getEmail() {
        return email;
    }

    public String getDiet() {
        return diet;
    }

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }
}
