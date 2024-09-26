package dev.werricsson.google_calendar.example.controller;

import com.google.api.services.calendar.model.Event;
import dev.werricsson.google_calendar.example.model.request.EventRequest;
import dev.werricsson.google_calendar.example.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PutMapping("/events/{eventId}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable String eventId,
            @RequestBody EventRequest eventRequest) {
        try {
            Event updatedEvent = calendarService.updateEvent(eventId, eventRequest);
            return ResponseEntity.ok(updatedEvent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable String eventId) {
        try {
            Event event = calendarService.getEventById(eventId);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

