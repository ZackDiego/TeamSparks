package org.example.teamspark.webRtc;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class GroupVideoCallSocketHandler {
    private static final Map<String, String> userRoomMap = new HashMap<>();
    private final SocketIOServer server;
    private final RoomManager roomManager;

    public GroupVideoCallSocketHandler(@Qualifier("groupVideoCallSocketIOServer") SocketIOServer server,
                                       RoomManager roomManager) {
        this.server = server;
        this.roomManager = roomManager;
        server.addListeners(this);
        server.start();
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("Client connected: " + client.getSessionId());

    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String clientId = client.getSessionId().toString();
        String room = userRoomMap.get(clientId);
        if (!Objects.isNull(room)) {
            log.info(String.format("Client disconnected: %s from : %s", clientId, room));
            // remove client
            roomManager.removeClientFromRoom(room, clientId);
            userRoomMap.remove(clientId);
            client.getNamespace().getRoomOperations(room).sendEvent("userDisconnected", clientId);
        }
        printLog("onDisconnect", client, room);
    }

    @OnEvent("joinRoom")
    public void onJoinRoom(SocketIOClient client, Map<String, Object> payload) {
        String room = (String) payload.get("roomName");
        String userName = (String) payload.get("userName");
        String userId = client.getSessionId().toString();

        int connectedClients = server.getRoomOperations(room).getClients().size();

        if (connectedClients == 0) {
            client.sendEvent("created");
        } else {
            log.info("JoinRoom: send joined event with clients in room: " + roomManager.getClientsInRoom(room));
            // return existing users in room
            client.sendEvent("joined", roomManager.getClientsInRoom(room));
        }

        client.joinRoom(room);

        ClientIdentity clientIdentity = new ClientIdentity(userId, userName);
        roomManager.addClientToRoom(clientIdentity, room);
        userRoomMap.put(userId, room);

        printLog("onJoinRoom", client, room);
    }

    @OnEvent("candidate")
    public synchronized void onCandidate(SocketIOClient client, Map<String, Object> payload) {
        String room = (String) payload.get("room");
        String targetClientId = (String) payload.get("targetClientId");

        SocketIOClient targetClient = server.getClient(UUID.fromString(targetClientId));
        payload.put("candidateClientId", client.getSessionId().toString());
        targetClient.sendEvent("candidate", payload);
        printLog("onCandidate", client, room);
    }

    @OnEvent("offer")
    public synchronized void onOffer(SocketIOClient client, Map<String, Object> payload) {
        // parse payload
        String targetClientId = (String) payload.get("targetClientId");
        String room = (String) payload.get("room");
        String userName = (String) payload.get("userName");

        SocketIOClient targetClient = server.getClient(UUID.fromString(targetClientId));
        Map<String, Object> offerPayload = new HashMap<>();


        ClientIdentity clientIdentity = new ClientIdentity(client.getSessionId().toString(), userName);

        offerPayload.put("offerClient", clientIdentity);
        offerPayload.put("sdp", payload.get("sdp"));

        log.info("send offer to " + targetClient);

        // send offer to newly joined client
        targetClient.sendEvent("offer", offerPayload);
        printLog("onOffer", client, room);
    }

    @OnEvent("answer")
    public synchronized void onAnswer(SocketIOClient client, Map<String, Object> payload) {
        String room = (String) payload.get("room");
        String offerClientId = (String) payload.get("offerClientId");
        SocketIOClient targetClient = server.getClient(UUID.fromString(offerClientId));

        log.info("sent answer event " + targetClient);

        Map<String, Object> answerPayload = new HashMap<>();
        answerPayload.put("answerClientId", client.getSessionId().toString());
        answerPayload.put("sdp", payload.get("sdp"));

        targetClient.sendEvent("answer", answerPayload);
        printLog("onAnswer", client, room);
    }

    @OnEvent("leaveRoom")
    public void onLeaveRoom(SocketIOClient client, String room) {
        client.leaveRoom(room);
        String leaveCliendId = client.getSessionId().toString();
        roomManager.removeClientFromRoom(room, leaveCliendId);
        // send userDisconnected to rest of users in the same room
        client.getNamespace().getRoomOperations(room).sendEvent("userDisconnected", leaveCliendId);

        printLog("onLeaveRoom", client, room);
    }

    private void printLog(String header, SocketIOClient client, String room) {
        if (room == null) return;
        int size = 0;
        try {
            size = client.getNamespace().getRoomOperations(room).getClients().size();
        } catch (Exception e) {
            log.error("error ", e);
        }
        log.info("#ConncetedClients - {} => room: {}, count: {}", header, room, size);
    }
}