package com.autovoice.bot;

import com.autovoice.config.TelegramConfig;
import com.autovoice.entity.Feedback;
import com.autovoice.enums.Branch;
import com.autovoice.handler.FeedbackHandler;
import com.autovoice.handler.RegistrationHandler;
import com.autovoice.service.BotUserServiceImpl;
import com.autovoice.service.FeedbackServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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

    private final FeedbackHandler feedbackHandler;

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

            if (update.hasMessage() && update.getMessage().isReply()) {
                String replyToText = update.getMessage().getReplyToMessage().getText();
                if ("✍️ Напишите ваш отзыв:".equals(replyToText)) {
                    String feedbackText = update.getMessage().getText();
                    Feedback feedback = feedbackHandler.handleNewFeedback(chatId, feedbackText);
                    String response = String.format(
                            "✅ Feedback send!\n📊 Sentiment: %s\n🔥 Critical: %d\n💡 Solution: %s",
                            feedback.getSentiment(),
                            feedback.getCriticalLevel(),
                            feedback.getSolution()
                    );
                    executeMessage(new SendMessage(chatId.toString(), response));
                    return;
                }
            }

            switch (text) {
                case "/start": {
                    if (!botUserService.existsByChatId(chatId)) {
                        executeMessage(registrationHandler.createRoleSelectionMessage(chatId));
                    }

                    SendMessage message = new SendMessage(chatId.toString(), "\uD83D\uDCCB Main menu: ");
                    message.setReplyMarkup(createPersistentKeyboard());
                    executeMessage(message);
                    break;
                }
                case "\uD83C\uDFE2 Change branch": {
                    SendMessage changeBranchMessage = registrationHandler.createSelectionMessage(
                            chatId, "\uD83C\uDFE2 Choose new branch: ", Branch.values());
                    executeMessage(changeBranchMessage);
                    break;
                }
                case "\uD83D\uDCCB My feedbacks": {
                    feedbackService.findByChatId(chatId).forEach(feedback -> {
                        executeMessage(new SendMessage(chatId.toString(), feedback.toString()));
                    });
                    break;
                }
                case "\uFE0F Write feedback": {
                    SendMessage askFeedback = new SendMessage(chatId.toString(), "✍️ Напишите ваш отзыв:");
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setSelective(true);
                    askFeedback.setReplyMarkup(forceReply);
                    executeMessage(askFeedback);
                    break;
                }
                default: {
                    String response = "Please choose a valid option!";
                    executeMessage(new SendMessage(chatId.toString(), response));
                    break;
                }
            }
        }
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            SendMessage sendMessage = registrationHandler.handleCallback(chatId, data);
            if (sendMessage != null) {
                executeMessage(sendMessage);
            }
        }
    }

    private void executeMessage(SendMessage message) {
        if (message == null || message.getText() == null) {
            System.err.println("⚠️ Attempted to send null or empty message");
            return;
        }
        if (message.getReplyMarkup() == null) {
            message.setReplyMarkup(createPersistentKeyboard());
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public ReplyKeyboardMarkup createPersistentKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("\uFE0F Write feedback"));
        row1.add(new KeyboardButton("\uD83D\uDCCB My feedbacks"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("\uD83C\uDFE2 Change branch"));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }
}
