package org.example.teamspark.webRtc;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RoomManager {
    Map<String, Set<ClientIdentity>> roomClients = new HashMap<>();

    // Add client to room
    public void addClientToRoom(ClientIdentity clientIdentity, String roomId) {
        roomClients.computeIfAbsent(roomId, k -> new HashSet<>()).add(clientIdentity);
    }

    // Remove client from room
    public void removeClientFromRoom(String roomId, String clientId) {
        Set<ClientIdentity> clients = roomClients.get(roomId);
        if (clients != null) {
            Set<ClientIdentity> updatedClients = clients.stream()
                    .filter(client -> !client.getId().equals(clientId))
                    .collect(Collectors.toSet());
            if (clients.isEmpty()) {
                roomClients.remove(roomId);
            }
        }
    }

    // Get all clients in room
    public Set<ClientIdentity> getClientsInRoom(String roomId) {
        return roomClients.getOrDefault(roomId, Collections.emptySet());
    }
}
