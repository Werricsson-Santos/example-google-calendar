package dev.werricsson.google_calendar.example.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import dev.werricsson.google_calendar.example.model.request.EventRequest;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.spi.LocaleServiceProvider;

@Service
@Data
public class CalendarService {
    private final Logger logger = LoggerFactory.getLogger(CalendarService.class);

    private static final String APPLICATION_NAME = "Google Calendar API Spring";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final InputStream CREDENTIALS_FILE_PATH;

    static {
        try {
            CREDENTIALS_FILE_PATH = new FileInputStream("C:\\Users\\werri\\OneDrive\\Documentos\\credentials.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final Calendar calendarService;

    @Autowired
    public CalendarService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        //FileReader credentialsStream = new FileReader(new File(CREDENTIALS_FILE_PATH));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(CREDENTIALS_FILE_PATH));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
        //CustomLocalServerReceiver receiver = new CustomLocalServerReceiver();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String createEvent(String summary, String location, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description);

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDate))
                .setTimeZone("America/New_York");
        event.setStart(start);

        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDate))
                .setTimeZone("America/New_York");
        event.setEnd(end);

        String calendarId = "primary";
        event = calendarService.events().insert(calendarId, event).execute();
        // Obtenha o eventId
        String eventId = event.getId();

        // Retorne o link e o id codificado
        return "Event link: " + event.getHtmlLink() + ", ID: " + eventId;
    }

    public Event getEventById(String eventId) throws IOException {
        try {
            // Buscar o evento pelo ID
            return calendarService.events().get("primary", eventId).execute();
        } catch (IOException e) {
            // Logar o erro e lançar a exceção para o controlador tratar
            throw new RuntimeException("Erro ao buscar o evento com ID: " + eventId, e);
        }
    }

    public Event updateEvent(String eventId, EventRequest eventRequest) throws IOException {
        try {
            // Buscar o evento pelo ID
            Event event = calendarService.events().get("primary", eventId).execute();

            // Atualizar o sumário se presente
            if (eventRequest.getSummary() != null) {
                event.setSummary(eventRequest.getSummary());
            }

            // Atualizar o local se presente
            if (eventRequest.getLocation() != null) {
                event.setLocation(eventRequest.getLocation());
            }

            // Atualizar a descrição se presente
            if (eventRequest.getDescription() != null) {
                event.setDescription(eventRequest.getDescription());
            }

            // Atualizar o horário de início se presente
            if (eventRequest.getStartDateTime() != null) {
                Date newStartDate = Date.from(eventRequest.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant());
                EventDateTime newStart = new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(newStartDate))
                        .setTimeZone("America/New_York");
                event.setStart(newStart);
            }

            // Atualizar o horário de fim se presente
            if (eventRequest.getEndDateTime() != null) {
                Date newEndDate = Date.from(eventRequest.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant());
                EventDateTime newEnd = new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(newEndDate))
                        .setTimeZone("America/New_York");
                event.setEnd(newEnd);
            }

            // Atualizar o evento no Google Calendar
            return calendarService.events().update("primary", event.getId(), event).execute();
        } catch (Exception e) {
            logger.info("Não foi possível atualizar o evento:" + eventId, e);
            throw e;
        }
    }
}

