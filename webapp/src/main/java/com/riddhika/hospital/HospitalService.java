package com.riddhika.hospital;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HospitalService {

    private static final RowMapper<Patient> PATIENT_MAPPER = (rs, rowNum) -> mapPatient(rs);
    private static final RowMapper<Doctor> DOCTOR_MAPPER = (rs, rowNum) -> new Doctor(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getInt("patients_treated"));

    private final JdbcTemplate jdbc;

    public HospitalService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Patient> findPatients(String search, boolean sortByName) {
        String order = sortByName ? "LOWER(name), id" : "id";
        if (search == null || search.isBlank()) {
            return jdbc.query("SELECT * FROM patients ORDER BY " + order, PATIENT_MAPPER);
        }
        String term = "%" + search.trim() + "%";
        return jdbc.query(
                "SELECT * FROM patients WHERE name LIKE ? OR disease LIKE ? ORDER BY " + order,
                PATIENT_MAPPER,
                term,
                term);
    }

    public List<Patient> findQueue() {
        return jdbc.query("""
                SELECT p.*
                FROM treatment_queue q
                JOIN patients p ON p.id = q.patient_id
                ORDER BY p.severity, q.queued_at, p.id
                """, PATIENT_MAPPER);
    }

    public List<Doctor> findDoctors() {
        return jdbc.query(
                "SELECT * FROM doctors ORDER BY department, patients_treated, name",
                DOCTOR_MAPPER);
    }

    public int patientCount() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM patients", Integer.class);
    }

    public int criticalPatientCount() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM patients WHERE severity = 1", Integer.class);
    }

    @Transactional
    public void addPatient(PatientForm form) {
        jdbc.update(
                "INSERT INTO patients (name, age, disease, severity) VALUES (?, ?, ?, ?)",
                form.getName().trim(),
                form.getAge(),
                form.getDisease().trim(),
                form.getSeverity());
    }

    @Transactional
    public void updatePatient(int id, String disease, int severity) {
        if (disease == null || disease.isBlank() || disease.length() > 50
                || severity < 1 || severity > 3) {
            throw new IllegalArgumentException("Enter a condition and a valid severity.");
        }
        if (jdbc.queryForObject(
                "SELECT COUNT(*) FROM patients WHERE id = ?",
                Integer.class,
                id) == 0) {
            throw new IllegalArgumentException("Patient not found.");
        }
        jdbc.update(
                "UPDATE patients SET disease = ?, severity = ? WHERE id = ?",
                disease.trim(),
                severity,
                id);
    }

    @Transactional
    public void addToQueue(int patientId) {
        if (jdbc.queryForObject(
                "SELECT COUNT(*) FROM patients WHERE id = ?",
                Integer.class,
                patientId) == 0) {
            throw new IllegalArgumentException("Patient not found.");
        }
        if (jdbc.queryForObject(
                "SELECT COUNT(*) FROM treatment_queue WHERE patient_id = ?",
                Integer.class,
                patientId) > 0) {
            throw new IllegalArgumentException("This patient is already in the treatment queue.");
        }
        jdbc.update(
                "INSERT INTO treatment_queue (patient_id) VALUES (?)",
                patientId);
    }

    @Transactional
    public Patient treatNext() {
        List<Patient> waiting = jdbc.query("""
                SELECT p.*
                FROM treatment_queue q
                JOIN patients p ON p.id = q.patient_id
                ORDER BY p.severity, q.queued_at, p.id
                LIMIT 1
                FOR UPDATE
                """, PATIENT_MAPPER);
        if (waiting.isEmpty()) {
            throw new IllegalArgumentException("There are no patients waiting in the queue.");
        }

        Patient patient = waiting.get(0);
        String department = findDepartment(patient.disease());
        List<Doctor> available = jdbc.query(
                "SELECT * FROM doctors WHERE department = ? ORDER BY patients_treated, id LIMIT 1 FOR UPDATE",
                DOCTOR_MAPPER,
                department);
        if (available.isEmpty() && !department.equals("General Medicine")) {
            available = jdbc.query("""
                    SELECT * FROM doctors
                    WHERE department = 'General Medicine'
                    ORDER BY patients_treated, id
                    LIMIT 1
                    FOR UPDATE
                    """, DOCTOR_MAPPER);
        }
        if (available.isEmpty()) {
            throw new IllegalStateException("No doctor is available to treat this patient.");
        }

        jdbc.update(
                "UPDATE doctors SET patients_treated = patients_treated + 1 WHERE id = ?",
                available.get(0).id());
        jdbc.update("DELETE FROM treatment_queue WHERE patient_id = ?", patient.id());
        return patient;
    }

    @Transactional
    public void dischargePatient(int id) {
        List<Patient> found = jdbc.query(
                "SELECT * FROM patients WHERE id = ? FOR UPDATE",
                PATIENT_MAPPER,
                id);
        if (found.isEmpty()) {
            throw new IllegalArgumentException("Patient not found.");
        }
        Patient patient = found.get(0);
        jdbc.update("""
                INSERT INTO discharge_history (patient_id, name, age, disease, severity)
                VALUES (?, ?, ?, ?, ?)
                """,
                patient.id(),
                patient.name(),
                patient.age(),
                patient.disease(),
                patient.severity());
        jdbc.update("DELETE FROM treatment_queue WHERE patient_id = ?", id);
        jdbc.update("DELETE FROM patients WHERE id = ?", id);
    }

    @Transactional
    public void undoDischarge() {
        List<Map<String, Object>> history = jdbc.queryForList("""
                SELECT * FROM discharge_history
                ORDER BY history_id DESC
                LIMIT 1
                FOR UPDATE
                """);
        if (history.isEmpty()) {
            throw new IllegalArgumentException("There is no discharge to undo.");
        }
        Map<String, Object> entry = history.get(0);
        jdbc.update("""
                INSERT INTO patients (id, name, age, disease, severity)
                VALUES (?, ?, ?, ?, ?)
                """,
                entry.get("patient_id"),
                entry.get("name"),
                entry.get("age"),
                entry.get("disease"),
                entry.get("severity"));
        jdbc.update("DELETE FROM discharge_history WHERE history_id = ?", entry.get("history_id"));
    }

    private static Patient mapPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("disease"),
                rs.getInt("severity"));
    }

    private static String findDepartment(String disease) {
        String value = disease.toLowerCase(Locale.ROOT);
        if (containsAny(value, "heart", "chest", "bp")) {
            return "Cardiology";
        }
        if (containsAny(value, "fracture", "bone", "accident")) {
            return "Orthopedics";
        }
        if (containsAny(value, "skin", "rash", "allergy")) {
            return "Dermatology";
        }
        return "General Medicine";
    }

    private static boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
