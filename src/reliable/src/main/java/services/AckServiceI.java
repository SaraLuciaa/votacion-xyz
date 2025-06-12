package services;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import utils.AppLogger;

public class AckServiceI implements VotacionXYZ.AckService{
    private final ConcurrentHashMap<String, Boolean> acks = new ConcurrentHashMap<>();
    private final Logger log = AppLogger.get();

    @Override
    public void confirm(String messageId, com.zeroc.Ice.Current current) {
        log.info("ACK recibido: " + messageId);
        acks.put(messageId, true);
    }

    public boolean isAcked(String messageId) {
        return acks.getOrDefault(messageId, false);
    }
}