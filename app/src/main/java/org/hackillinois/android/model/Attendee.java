package org.hackillinois.android.model;

public class Attendee {
    private String firstName;
    private String lastName;
    private String email;
    private String[] diet;
    private String school;
    private String major;

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getEmail() {
        return email;
    }

    public String getDiet() {
        if (diet.length == 0) {
            return "No Dietary Restrictions";
        }

        String list = diet[0];
        if (diet.length > 2) {
            list += ",";
        }
        for (int i = 1; i < diet.length - 1; i++) {
            list += String.format(" %s,", diet[i]);
        }
        if (diet.length > 1) {
            list += String.format(" and %s", diet[diet.length - 1]);
        }
        return list;
    }

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }
}
