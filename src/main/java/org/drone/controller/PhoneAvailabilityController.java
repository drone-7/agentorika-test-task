package org.drone.controller;

import lombok.RequiredArgsConstructor;
import org.drone.model.AvailabilityResponse;
import org.drone.service.PhoneAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PhoneAvailabilityController {

    private final PhoneAvailabilityService service;

    @GetMapping("/providers/{providerId}/subscribers/{phone}")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @PathVariable String providerId,
            @PathVariable String phone) {
        boolean available = service.isAvailable(providerId, phone);
        return ResponseEntity.ok(new AvailabilityResponse(available));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleProviderNotFound(IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    }
}
