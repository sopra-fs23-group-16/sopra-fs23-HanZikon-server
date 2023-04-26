package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class WSTestHandlers {
    static class MyStompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            super.afterConnected(session, connectedHeaders);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            super.handleException(session, command, headers, payload, exception);
        }
    }
    public static class FrameHandlerCreateRoom implements StompFrameHandler {
        private final Consumer<String> frameHandler;
        public FrameHandlerCreateRoom(Consumer<String> frameHandler) {
            this.frameHandler = frameHandler;
        }
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Room.class;
        }
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            if (payload instanceof Room) {
                Room obj = (Room) payload;
                frameHandler.accept(obj.toString());
            } else {
                throw new IllegalArgumentException("Invalid payload type");
            }
        }
    }
}