import { createClient } from "@supabase/supabase-js";
import "./styles.css";

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL;
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY;
const authView = document.querySelector("#auth-view");
const appView = document.querySelector("#app-view");
const authError = document.querySelector("#auth-error");
const pageError = document.querySelector("#page-error");
const successMessage = document.querySelector("#success-message");
const configHint = document.querySelector("#config-hint");
const loginForm = document.querySelector("#login-form");
const patientForm = document.querySelector("#patient-form");
const patientsBody = document.querySelector("#patients-body");
const searchInput = document.querySelector("#search");
const sortButton = document.querySelector("#sort-button");

let supabase;
let alphabetical = false;
let searchTimer;

function showMessage(element, message) {
  element.textContent = message;
  element.hidden = false;
}

function clearMessages() {
  pageError.hidden = true;
  successMessage.hidden = true;
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;",
  })[char]);
}

function severityLabel(severity) {
  return ["", "Critical", "Moderate", "Normal"][severity] ?? "Normal";
}

function severityClass(severity) {
  return ["", "critical", "moderate", "normal"][severity] ?? "normal";
}

async function runAction(action) {
  clearMessages();
  try {
    await action();
  } catch (error) {
    showMessage(pageError, error.message || "The request failed. Please try again.");
  }
}

async function loadPatients() {
  const term = searchInput.value.trim();
  let query = supabase.from("patients").select("id,name,age,disease,severity");
  if (term) {
    const escaped = term.replaceAll("%", "\\%").replaceAll("_", "\\_");
    query = query.or(`name.ilike.%${escaped}%,disease.ilike.%${escaped}%`);
  }
  query = alphabetical
    ? query.order("name", { ascending: true }).order("id", { ascending: true })
    : query.order("id", { ascending: true });
  const [
    { data, error },
    { count: totalCount, error: totalError },
    { count: criticalCount, error: criticalError },
  ] = await Promise.all([
    query,
    supabase.from("patients").select("*", { count: "exact", head: true }),
    supabase.from("patients").select("*", { count: "exact", head: true }).eq("severity", 1),
  ]);
  if (error) throw error;
  if (totalError) throw totalError;
  if (criticalError) throw criticalError;

  document.querySelector("#patient-count").textContent = String(totalCount);
  document.querySelector("#critical-count").textContent = String(criticalCount);
  patientsBody.innerHTML = data.length
    ? data.map((patient) => `
      <tr>
        <td class="muted">#${patient.id}</td>
        <td class="patient-name">${escapeHtml(patient.name)}</td>
        <td>${patient.age}</td>
        <td>${escapeHtml(patient.disease)}</td>
        <td><span class="badge ${severityClass(patient.severity)}">${severityLabel(patient.severity)}</span></td>
        <td class="actions">
          <details class="edit-details">
            <summary>Edit</summary>
            <form class="inline-edit" data-update="${patient.id}">
              <input name="disease" value="${escapeHtml(patient.disease)}" maxlength="50" aria-label="Condition" required>
              <select name="severity" aria-label="Severity">
                ${[1, 2, 3].map((value) => `<option value="${value}" ${patient.severity === value ? "selected" : ""}>${severityLabel(value)}</option>`).join("")}
              </select>
              <button class="button secondary small" type="submit">Save</button>
            </form>
          </details>
          <button class="text-button" data-queue="${patient.id}" type="button">Add to queue</button>
          <button class="text-button danger-text" data-discharge="${patient.id}" type="button">Discharge</button>
        </td>
      </tr>`).join("")
    : '<tr><td class="empty" colspan="6">No patients found. Register a patient above to get started.</td></tr>';
}

async function loadQueue() {
  const { data, error } = await supabase
    .from("treatment_queue")
    .select("patient_id,queued_at,patients(id,name,disease,severity)");
  if (error) throw error;
  const queue = data.filter((entry) => entry.patients).sort((a, b) =>
    a.patients.severity - b.patients.severity
    || new Date(a.queued_at) - new Date(b.queued_at)
    || a.patient_id - b.patient_id);
  document.querySelector("#queue-count").textContent = String(queue.length);
  document.querySelector("#treat-next").hidden = queue.length === 0;
  document.querySelector("#queue-list").innerHTML = queue.length
    ? queue.map((entry, index) => `
      <li>
        <span class="queue-number">${index + 1}</span>
        <span class="queue-patient"><strong>${escapeHtml(entry.patients.name)}</strong><small>${escapeHtml(entry.patients.disease)}</small></span>
        <span class="badge ${severityClass(entry.patients.severity)}">${severityLabel(entry.patients.severity)}</span>
      </li>`).join("")
    : '<li class="empty">No patients are waiting for treatment.</li>';
}

async function loadDoctors() {
  const { data, error } = await supabase
    .from("doctors")
    .select("id,name,department,patients_treated")
    .order("department")
    .order("patients_treated")
    .order("name");
  if (error) throw error;
  document.querySelector("#doctor-count").textContent = String(data.length);
  document.querySelector("#doctor-list").innerHTML = data.map((doctor) => `
    <article class="doctor-row">
      <span class="doctor-avatar">${escapeHtml(doctor.name.slice(4, 5))}</span>
      <span class="doctor-info"><strong>${escapeHtml(doctor.name)}</strong><small>${escapeHtml(doctor.department)}</small></span>
      <span class="workload"><strong>${doctor.patients_treated}</strong><small>treated</small></span>
    </article>`).join("");
}

async function refreshDashboard() {
  await Promise.all([loadPatients(), loadQueue(), loadDoctors()]);
}

async function showSession(session) {
  const signedIn = Boolean(session);
  authView.hidden = signedIn;
  appView.hidden = !signedIn;
  if (!signedIn) return;
  document.querySelector("#staff-email").textContent = session.user.email;
  await runAction(refreshDashboard);
}

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  authError.hidden = true;
  if (!supabase) {
    configHint.hidden = false;
    return;
  }
  const form = new FormData(loginForm);
  const { error } = await supabase.auth.signInWithPassword({
    email: form.get("email"),
    password: form.get("password"),
  });
  if (error) showMessage(authError, "Sign-in failed. Check your email, password, and Supabase staff account.");
});

document.querySelector("#sign-out").addEventListener("click", async () => {
  await runAction(async () => {
    const { error } = await supabase.auth.signOut();
    if (error) throw error;
  });
});

patientForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  await runAction(async () => {
    const form = new FormData(patientForm);
    const patient = {
      name: String(form.get("name")).trim(),
      age: Number(form.get("age")),
      disease: String(form.get("disease")).trim(),
      severity: Number(form.get("severity")),
    };
    if (!patient.name || !patient.disease || patient.age < 1 || patient.age > 119
      || patient.severity < 1 || patient.severity > 3) {
      throw new Error("Enter a valid name, condition, age, and severity.");
    }
    const { error } = await supabase.from("patients").insert(patient);
    if (error) throw error;
    patientForm.reset();
    patientForm.elements.severity.value = "3";
    showMessage(successMessage, "Patient added successfully.");
    await refreshDashboard();
  });
});

patientsBody.addEventListener("submit", async (event) => {
  const form = event.target.closest("[data-update]");
  if (!form) return;
  event.preventDefault();
  await runAction(async () => {
    const values = new FormData(form);
    const disease = String(values.get("disease")).trim();
    const severity = Number(values.get("severity"));
    if (!disease || disease.length > 50 || severity < 1 || severity > 3) {
      throw new Error("Enter a valid condition and severity.");
    }
    const { error } = await supabase.from("patients")
      .update({ disease, severity })
      .eq("id", Number(form.dataset.update));
    if (error) throw error;
    showMessage(successMessage, "Patient details updated.");
    await refreshDashboard();
  });
});

patientsBody.addEventListener("click", async (event) => {
  const queueButton = event.target.closest("[data-queue]");
  const dischargeButton = event.target.closest("[data-discharge]");
  if (queueButton) {
    await runAction(async () => {
      const { error } = await supabase.from("treatment_queue")
        .insert({ patient_id: Number(queueButton.dataset.queue) });
      if (error?.code === "23505") throw new Error("This patient is already in the treatment queue.");
      if (error) throw error;
      showMessage(successMessage, "Patient added to the treatment queue.");
      await refreshDashboard();
    });
  }
  if (dischargeButton && window.confirm("Discharge this patient?")) {
    await runAction(async () => {
      const { error } = await supabase.rpc("discharge_patient", {
        p_patient_id: Number(dischargeButton.dataset.discharge),
      });
      if (error) throw error;
      showMessage(successMessage, "Patient discharged.");
      await refreshDashboard();
    });
  }
});

document.querySelector("#treat-next").addEventListener("click", async () => {
  await runAction(async () => {
    const { data, error } = await supabase.rpc("treat_next_patient");
    if (error) throw error;
    showMessage(successMessage, `${data.patient_name} was treated and assigned to ${data.doctor_name}.`);
    await refreshDashboard();
  });
});

document.querySelector("#undo-discharge").addEventListener("click", async () => {
  await runAction(async () => {
    const { error } = await supabase.rpc("undo_last_discharge");
    if (error) throw error;
    showMessage(successMessage, "The last discharge was undone.");
    await refreshDashboard();
  });
});

searchInput.addEventListener("input", () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => runAction(loadPatients), 200);
});

sortButton.addEventListener("click", async () => {
  alphabetical = !alphabetical;
  sortButton.textContent = alphabetical ? "Show registration order" : "Sort by name";
  await runAction(loadPatients);
});

if (!supabaseUrl || !supabaseAnonKey) {
  configHint.hidden = false;
  loginForm.querySelector("button[type=submit]").disabled = true;
} else {
  supabase = createClient(supabaseUrl, supabaseAnonKey, {
    auth: { persistSession: true, autoRefreshToken: true, detectSessionInUrl: true },
  });
  supabase.auth.onAuthStateChange((_event, session) => {
    window.setTimeout(() => showSession(session), 0);
  });
  supabase.auth.getSession()
    .then(({ data: { session } }) => showSession(session))
    .catch((error) => showMessage(authError, error.message));
}
