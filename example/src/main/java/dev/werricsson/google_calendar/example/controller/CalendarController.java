package dev.werricsson.google_calendar.example.controller;

import dev.werricsson.google_calendar.example.model.request.EventRequest;
import dev.werricsson.google_calendar.example.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/create-event")
    public ResponseEntity<String> createEvent(@RequestBody EventRequest eventRequest) {
        try {
            String eventLink = calendarService.createEvent(
                    eventRequest.getSummary(),
                    eventRequest.getLocation(),
                    eventRequest.getDescription(),
                    eventRequest.getStartDateTime(),
                    eventRequest.getEndDateTime()
            );
            return ResponseEntity.ok("Event created: " + eventLink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating event: " + e.getMessage());
        }
    }
}

