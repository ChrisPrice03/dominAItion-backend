package com.dominAItionBackend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Service
public class AIService {
    private final RestTemplate restTemplate;

    public AIService() {
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void startPythonAgent() {
        try {
            String command = "python src/main/java/com/dominAItionBackend/agents/agent_api.py";
            Process process = Runtime.getRuntime().exec(command);

//             Read the standard output of the process
            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("OUTPUT: " + line);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading process output: " + e.getMessage());
                }
            }).start();

//             Read the error output of the process
            new Thread(() -> {
                try (var errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        System.err.println("ERROR: " + errorLine);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading process error output: " + e.getMessage());
                }
            }).start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                process.destroy();
                System.out.println("Python agent API terminated.");
            }));

            System.out.println("Python agent API started successfully.");
        } catch (IOException e) {
            System.err.println("Failed to start Python agent API: " + e.getMessage());
        }
    }

    public String storyPromptRespond(String request) {
        return "This is a response to the story prompt: " + request;
    }

    public String callOrchestrationAgent(String input) {
        String url = "http://localhost:5000/orchestrate";
        var request = new java.util.HashMap<String, String>();
        request.put("input", input);

        var response = restTemplate.postForObject(url, request, java.util.Map.class);
        return response != null ? response.get("response").toString() : "No response from agent";
    }

    public String callWorldDefiningAgent(String input) {
        String url = "http://localhost:5000/world";
        var request = new java.util.HashMap<String, String>();
        request.put("input", input);

        var response = restTemplate.postForObject(url, request, java.util.Map.class);
        return response != null ? response.get("response").toString() : "No response from agent";
    }

    public String callCharacterDefiningAgent(String input) {
        String url = "http://localhost:5000/character";
        var request = new java.util.HashMap<String, String>();
        request.put("input", input);

        var response = restTemplate.postForObject(url, request, java.util.Map.class);
        return response != null ? response.get("response").toString() : "No response from agent";
    }
}
