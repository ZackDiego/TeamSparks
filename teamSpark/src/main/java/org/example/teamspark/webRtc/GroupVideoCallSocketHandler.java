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
        String clientId = client.getSessionId().toString();
        userRoomMap.put(clientId, null);
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
    public void onJoinRoom(SocketIOClient client, String room) {
        int connectedClients = server.getRoomOperations(room).getClients().size();
        client.joinRoom(room);
        roomManager.addClientToRoom(client.getSessionId().toString(), room);

        if (connectedClients == 0) {
            client.sendEvent("created", room);
            roomManager.setRoomCaller(client.getSessionId().toString(), room);
        } else {
            client.sendEvent("joined", room);
            client.sendEvent("setCaller", roomManager.getRoomCaller(room));
        }
        printLog("onJoinRoom", client, room);
    }

    @OnEvent("ready")
    public void onReady(SocketIOClient client, String room) {
        client.getNamespace().getBroadcastOperations().sendEvent("ready", client.getSessionId().toString());
        printLog("onReady", client, room);
    }

    @OnEvent("candidate")
    public void onCandidate(SocketIOClient client, Map<String, Object> payload) {
        String room = (String) payload.get("room");
        String targetClientId = (String) payload.get("targetClientId");

        SocketIOClient targetClient = server.getClient(UUID.fromString(targetClientId));
        payload.put("candidateClientId", client.getSessionId().toString());
        targetClient.sendEvent("candidate", payload);
        printLog("onCandidate", client, room);
    }

    @OnEvent("offer")
    public void onOffer(SocketIOClient client, Map<String, Object> payload) {
        // parse payload
        String joinedClientId = (String) payload.get("joinedClientId");
        String room = (String) payload.get("room");

        SocketIOClient targetClient = server.getClient(UUID.fromString(joinedClientId));
        Map<String, Object> offerPayload = new HashMap<>();
        offerPayload.put("offerClientId", client.getSessionId().toString());
        offerPayload.put("sdp", payload.get("sdp"));

        log.info("send offer to " + targetClient);

        // send offer to newly joined client
        targetClient.sendEvent("offer", offerPayload);
        printLog("onOffer", client, room);
    }

    @OnEvent("answer")
    public void onAnswer(SocketIOClient client, Map<String, Object> payload) {
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