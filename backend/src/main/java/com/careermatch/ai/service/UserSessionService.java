package com.careermatch.ai.service;

import com.careermatch.ai.model.UserSession;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

    private final Map<Long, UserSession> activeSessions = new ConcurrentHashMap<>();

    public UserSession getOrCreateSession(Long chatId, String username) {
        return activeSessions.computeIfAbsent(chatId, id -> new UserSession(id, username));
    }

    public UserSession getSession(Long chatId) {
        return activeSessions.get(chatId);
    }

    public void resetSession(Long chatId) {
        UserSession session = activeSessions.get(chatId);
        if (session != null) {
            session.reset();
        }
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }
}
