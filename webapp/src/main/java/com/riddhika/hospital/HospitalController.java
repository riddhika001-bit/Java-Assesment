package com.riddhika.hospital;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HospitalController {

    private final HospitalService hospital;

    public HospitalController(HospitalService hospital) {
        this.hospital = hospital;
    }

    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "false") boolean sort,
            Model model) {
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("patientCount", hospital.patientCount());
        model.addAttribute("criticalCount", hospital.criticalPatientCount());
        model.addAttribute("patients", hospital.findPatients(search, sort));
        model.addAttribute("queue", hospital.findQueue());
        model.addAttribute("doctors", hospital.findDoctors());
        if (!model.containsAttribute("patientForm")) {
            model.addAttribute("patientForm", new PatientForm());
        }
        return "dashboard";
    }

    @PostMapping("/patients")
    public String addPatient(
            @Valid @ModelAttribute("patientForm") PatientForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("search", "");
            model.addAttribute("sort", false);
            model.addAttribute("patientCount", hospital.patientCount());
            model.addAttribute("criticalCount", hospital.criticalPatientCount());
            model.addAttribute("patients", hospital.findPatients("", false));
            model.addAttribute("queue", hospital.findQueue());
            model.addAttribute("doctors", hospital.findDoctors());
            return "dashboard";
        }
        hospital.addPatient(form);
        redirect.addFlashAttribute("successMessage", "Patient added successfully.");
        return "redirect:/";
    }

    @PostMapping("/patients/{id}/update")
    public String updatePatient(
            @PathVariable int id,
            @RequestParam @NotBlank @Size(max = 50) String disease,
            @RequestParam @Min(1) @Max(3) int severity,
            RedirectAttributes redirect) {
        hospital.updatePatient(id, disease, severity);
        redirect.addFlashAttribute("successMessage", "Patient details updated.");
        return "redirect:/";
    }

    @PostMapping("/patients/{id}/discharge")
    public String dischargePatient(@PathVariable int id, RedirectAttributes redirect) {
        hospital.dischargePatient(id);
        redirect.addFlashAttribute("successMessage", "Patient discharged.");
        return "redirect:/";
    }

    @PostMapping("/patients/{id}/queue")
    public String addToQueue(@PathVariable int id, RedirectAttributes redirect) {
        hospital.addToQueue(id);
        redirect.addFlashAttribute("successMessage", "Patient added to the treatment queue.");
        return "redirect:/";
    }

    @PostMapping("/queue/treat-next")
    public String treatNext(RedirectAttributes redirect) {
        Patient treated = hospital.treatNext();
        redirect.addFlashAttribute(
                "successMessage",
                treated.name() + " was treated and assigned to a doctor.");
        return "redirect:/";
    }

    @PostMapping("/discharges/undo")
    public String undoDischarge(RedirectAttributes redirect) {
        hospital.undoDischarge();
        redirect.addFlashAttribute("successMessage", "The last discharge was undone.");
        return "redirect:/";
    }
}
