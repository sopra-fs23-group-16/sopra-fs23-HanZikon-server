package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    private ConcurrentHashMap<String, Room> roomCodes;
    private ConcurrentHashMap<Integer, Room> roomIDs;

    public RoomManager() {
        this.roomCodes = new ConcurrentHashMap<>();
        this.roomIDs = new ConcurrentHashMap<>();
    }

    public void addRoom(Room room) {
        roomCodes.put(room.getRoomCode(), room);
        roomIDs.put(room.getRoomID(), room);
    }
    public void removeRoom(Room room) {
        roomCodes.remove(room.getRoomCode());
        roomIDs.remove(room.getRoomID());
    }

    public Room findByRoomCode(String roomCode){
        Room room = roomCodes.get(roomCode);
//        if (room == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
//        }
        return room;
    }

    public Room findByRoomID(int roomID) {
        Room room = roomIDs.get(roomID);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
        }
        return room;
    }
}