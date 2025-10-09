package com.autovoice.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotInitializer {

    private final AutoVoiceBot autoVoiceBot;

    public TelegramBotInitializer(AutoVoiceBot autoVoiceBot) {
        this.autoVoiceBot = autoVoiceBot;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(autoVoiceBot);
        return botsApi;
    }
}
