package org.example.teamspark.webRtc;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoomManager {
    Map<String, Set<String>> roomClients = new HashMap<>();
    Map<String, String> roomCaller = new HashMap<>();

    // Add client to room
    public void addClientToRoom(String clientId, String roomId) {
        roomClients.computeIfAbsent(roomId, k -> new HashSet<>()).add(clientId);
    }

    public void setRoomCaller(String callerClientId, String roomId) {
        roomCaller.put(roomId, callerClientId);
    }

    public String getRoomCaller(String roomId) {
        return roomCaller.get(roomId);
    }

    // Remove client from room
    public void removeClientFromRoom(String roomId, String clientId) {
        Set<String> clients = roomClients.get(roomId);
        if (clients != null) {
            clients.remove(clientId);
            if (clients.isEmpty()) {
                roomClients.remove(roomId);
            }
        }
    }

    // Get all clients in room
    public Set<String> getClientsInRoom(String roomId) {
        return roomClients.getOrDefault(roomId, Collections.emptySet());
    }
}
