package com.riddhika.hospital;

public record Patient(int id, String name, int age, String disease, int severity) {

    public String severityLabel() {
        return switch (severity) {
            case 1 -> "Critical";
            case 2 -> "Moderate";
            default -> "Normal";
        };
    }
}
