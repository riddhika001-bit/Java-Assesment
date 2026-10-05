package com.riddhika.hospital;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class HospitalErrorHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleHospitalError(RuntimeException exception, RedirectAttributes redirect) {
        redirect.addFlashAttribute("errorMessage", exception.getMessage());
        return "redirect:/";
    }
}
