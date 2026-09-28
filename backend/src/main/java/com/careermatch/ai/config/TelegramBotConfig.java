package com.careermatch.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.bot.username:CareerMatchAIBot}")
    private String botUsername;

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public boolean isConfigured() {
        return botToken != null && !botToken.trim().isEmpty() && !"YOUR_BOT_TOKEN_HERE".equalsIgnoreCase(botToken.trim());
    }
}
