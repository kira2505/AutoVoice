package com.autovoice.bot;

import com.autovoice.config.TelegramConfig;
import com.autovoice.enums.Branch;
import com.autovoice.handler.RegistrationHandler;
import com.autovoice.service.BotUserServiceImpl;
import com.autovoice.service.FeedbackServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoVoiceBot extends TelegramLongPollingBot {

    private final TelegramConfig telegramConfig;

    private final BotUserServiceImpl botUserService;

    private final FeedbackServiceImpl feedbackService;

    private final RegistrationHandler registrationHandler;

    @Override
    public String getBotUsername() {
        return telegramConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return telegramConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.equals("/start")) {
                if (botUserService.existsByChatId(chatId)) {
                    executeMessage(createMenuMessage(chatId));
                }
                executeMessage(registrationHandler.createRoleSelectionMessage(chatId));
            }
        }

        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            SendMessage message = registrationHandler.handleCallback(chatId, data);

            if (message != null) {
                executeMessage(message);
            }
            switch (data) {
                case "MY_FEEDBACKS":

                    break;
                case "WRITE_FEEDBACK":

                    break;
                case "CHANGE_BRANCH":
                    SendMessage changeBranchMessage = registrationHandler.createSelectionMessage(
                        chatId, "\uD83C\uDFE2 Choose new branch: ", Branch.values());
                    executeMessage(changeBranchMessage);
                        break;
            }
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage createMenuMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Choose action:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("\uD83D\uDCCB My feedbacks");
        button1.setCallbackData("MY_FEEDBACKS");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("\uFE0F Write feedback");
        button2.setCallbackData("WRITE_FEEDBACK");

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("\uD83C\uDFE2 Change branch");
        button3.setCallbackData("CHANGE_BRANCH");


        rows.add(List.of(button1));
        rows.add(List.of(button2));
        rows.add(List.of(button3));

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        return message;
    }
}
