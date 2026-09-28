package com.careermatch.ai.controller;

import com.careermatch.ai.bot.CareerMatchBot;
import com.careermatch.ai.config.TelegramBotConfig;
import com.careermatch.ai.service.UserSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bot")
public class BotStatusController {

    private final CareerMatchBot careerMatchBot;
    private final TelegramBotConfig botConfig;
    private final UserSessionService sessionService;

    public BotStatusController(CareerMatchBot careerMatchBot, TelegramBotConfig botConfig, UserSessionService sessionService) {
        this.careerMatchBot = careerMatchBot;
        this.botConfig = botConfig;
        this.sessionService = sessionService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "CareerMatch AI Telegram Backend");
        status.put("version", "1.0.0-PHASE1");
        status.put("botUsername", "@" + botConfig.getBotUsername());
        status.put("telegramConnected", careerMatchBot.isConnected());
        status.put("activeUserSessions", sessionService.getActiveSessionCount());
        status.put("tokenConfigured", botConfig.getBotToken() != null && !botConfig.getBotToken().trim().isEmpty());

        if (!careerMatchBot.isConnected()) {
            status.put("note", "Bot is running in STANDBY mode. Set TELEGRAM_BOT_TOKEN environment variable to connect to Telegram live.");
        } else {
            status.put("note", "Bot is live and polling updates from Telegram.");
        }

        return ResponseEntity.ok(status);
    }
}
