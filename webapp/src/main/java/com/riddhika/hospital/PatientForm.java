package com.riddhika.hospital;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatientForm {

    @NotBlank
    @Size(max = 50)
    private String name;

    @Min(1)
    @Max(119)
    private int age;

    @NotBlank
    @Size(max = 50)
    private String disease;

    @Min(1)
    @Max(3)
    private int severity = 3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
