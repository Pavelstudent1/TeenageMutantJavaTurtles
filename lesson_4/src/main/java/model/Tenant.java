package model;

import java.util.Objects;

public class Tenant {
    private Gender gender;
    private String firstName;
    private String lastName;
    private int age;

    public Tenant(Gender gender, String firstName, String lastName, int age) {
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return age == tenant.age &&
                gender == tenant.gender &&
                Objects.equals(firstName, tenant.firstName) &&
                Objects.equals(lastName, tenant.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gender, firstName, lastName, age);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "gender=" + gender +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }
}
