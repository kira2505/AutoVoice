package com.autovoice.handler;

import com.autovoice.enums.Branch;
import com.autovoice.enums.RegistrationStep;
import com.autovoice.enums.Role;
import com.autovoice.service.BotUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RegistrationHandler {

    private final BotUserServiceImpl botUserService;

    private final Map<Long, RegistrationStep> registrationStep = new ConcurrentHashMap<>();

    public SendMessage createRoleSelectionMessage(Long chatId) {
        if(botUserService.existsByChatId(chatId) && botUserService.hasRole(chatId) != null) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("You have already selected a role 👌");
            return message;
        }
        registrationStep.put(chatId, RegistrationStep.ASK_ROLE);
        return createSelectionMessage(chatId, "\uD83D\uDC64 Select your role: ", Role.values());
    }

    public SendMessage handleCallback(Long chatId, String data) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());

        try {
            try {
                Role role = Role.valueOf(data);
                botUserService.saveRole(chatId, role);

                message = createSelectionMessage(chatId, "\uD83C\uDFE2 Select your branch: ",  Branch.values());
                return message;
            } catch (IllegalArgumentException e) {
                try {
                    Branch branch = Branch.valueOf(data);
                    botUserService.saveBranch(chatId, branch);

                    SendMessage completed = new SendMessage();
                    completed.setChatId(chatId.toString());
                    message.setText("✅ Registration completed! Use the menu below \uD83D\uDC47");
                    return completed;
                } catch (IllegalArgumentException ex) {
                    message.setText("Wrong choice: " + data);
                    return message;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("An error occurred while processing your selection.");
            return message;
        }
    }

    public SendMessage createSelectionMessage(Long chatId, String text, Enum<?>[] values) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Enum<?> value : values) {
            InlineKeyboardButton button = new InlineKeyboardButton();

            String displayName;
            if (value instanceof Role) {
                displayName = ((Role) value).getDisplayName();
            } else {
                displayName = ((Branch) value).getDisplayName();
            }

            button.setText(displayName);
            button.setCallbackData(value.name());
            rows.add(List.of(button));
        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        return message;
    }
}
