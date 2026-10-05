CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    disease VARCHAR(50) NOT NULL,
    severity INT NOT NULL
) AUTO_INCREMENT = 101;

CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    patients_treated INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS treatment_queue (
    patient_id INT PRIMARY KEY,
    queued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_treatment_queue_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS discharge_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    disease VARCHAR(50) NOT NULL,
    severity INT NOT NULL,
    discharged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO doctors (name, department, patients_treated)
SELECT 'Dr. Sharma', 'General Medicine', 0
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name = 'Dr. Sharma' AND department = 'General Medicine');

INSERT INTO doctors (name, department, patients_treated)
SELECT 'Dr. Patil', 'General Medicine', 0
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name = 'Dr. Patil' AND department = 'General Medicine');

INSERT INTO doctors (name, department, patients_treated)
SELECT 'Dr. Rao', 'Cardiology', 0
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name = 'Dr. Rao' AND department = 'Cardiology');

INSERT INTO doctors (name, department, patients_treated)
SELECT 'Dr. Khan', 'Orthopedics', 0
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name = 'Dr. Khan' AND department = 'Orthopedics');

INSERT INTO doctors (name, department, patients_treated)
SELECT 'Dr. Mehta', 'Orthopedics', 0
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name = 'Dr. Mehta' AND department = 'Orthopedics');

INSERT INTO doctors (name, department, patients_treated)
SELECT 'Dr. Nair', 'Dermatology', 0
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name = 'Dr. Nair' AND department = 'Dermatology');
