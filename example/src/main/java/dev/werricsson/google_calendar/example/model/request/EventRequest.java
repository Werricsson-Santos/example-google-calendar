package dev.werricsson.google_calendar.example.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    private String summary;
    private String location;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}

