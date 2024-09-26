package dev.werricsson.google_calendar.example;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class CustomLocalServerReceiver implements VerificationCodeReceiver {
    private static final String REDIRECT_URI = "http://localhost:8080";
    private ServerSocket serverSocket;

    @Override
    public String waitForCode() throws IOException {
        serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("localhost"));
        System.out.println("Waiting for authorization code...");

        try (Socket socket = serverSocket.accept()) {
            InputStreamReader reader = new InputStreamReader(socket.getInputStream());
            StringBuilder request = new StringBuilder();
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                request.append(buffer, 0, length);
                if (request.indexOf("\r\n\r\n") != -1) {
                    break;
                }
            }

            String code = extractCodeFromRequest(request.toString());
            sendResponse(socket);
            return code;
        } finally {
            stop();
        }
    }

    private String extractCodeFromRequest(String request) {
        // Aqui você deve extrair o código de verificação do request
        // Exemplo simplificado:
        String[] lines = request.split("\n");
        for (String line : lines) {
            if (line.contains("code=")) {
                return line.split("code=")[1].split(" ")[0]; // Extrai o código
            }
        }
        return null;
    }

    private void sendResponse(Socket socket) throws IOException {
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" +
                "<html><body><h1>Authorization Successful</h1>" +
                "<p>You can close this window.</p></body></html>";
        socket.getOutputStream().write(httpResponse.getBytes());
    }

    @Override
    public void stop() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    @Override
    public String getRedirectUri() {
        return REDIRECT_URI;
    }
}
