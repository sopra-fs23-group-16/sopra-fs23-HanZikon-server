package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

public class RoomRepository {
    private static final HashMap<Integer, Room> roomRepository = new HashMap<>();

    private RoomRepository() {
    }

    public static void addRoom(int roomId, Room room) {
        roomRepository.put(roomId, room);
    }

    public static void removeRoom(int roomId) {
        roomRepository.remove(roomId);
    }

    public static Room findByRoomId(int roomId) {
        Room room = roomRepository.get(roomId);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
        }
        return room;
    }
}