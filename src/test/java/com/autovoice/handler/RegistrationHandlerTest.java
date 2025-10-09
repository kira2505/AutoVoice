package com.autovoice.handler;

import com.autovoice.enums.Branch;
import com.autovoice.enums.Position;
import com.autovoice.service.BotUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationHandlerTest {

    private BotUserServiceImpl botUserService;
    private RegistrationHandler registrationHandler;

    @BeforeEach
    void setUp() {
        botUserService = Mockito.mock(BotUserServiceImpl.class);
        registrationHandler = new RegistrationHandler(botUserService);
    }

    @Test
    void testCreateRoleSelectionMessage() {
        Long chatId = 123L;
        SendMessage message = registrationHandler.createRoleSelectionMessage(chatId);

        assertEquals(chatId.toString(), message.getChatId());
        assertTrue(message.getText().contains("Select your position"));

        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) message.getReplyMarkup();
        assertNotNull(markup);
        assertFalse(markup.getKeyboard().isEmpty());
    }

    @Test
    void testHandleCallback_SelectPosition() {
        Long chatId = 123L;
        String data = Position.ELECTRICIAN.name();

        SendMessage message = registrationHandler.handleCallback(chatId, data);

        verify(botUserService).savePosition(chatId, Position.ELECTRICIAN);
        assertTrue(message.getText().contains("Select your branch"));
    }

    @Test
    void testHandleCallback_SelectBranch() {
        Long chatId = 123L;
        String data = Branch.BRANCH_1.name();

        SendMessage message = registrationHandler.handleCallback(chatId, data);

        verify(botUserService).saveBranch(chatId, Branch.BRANCH_1);
        assertTrue(message.getText().contains("Registration completed"));
    }

    @Test
    void testHandleCallback_WrongData() {
        Long chatId = 123L;
        String data = "WRONG_VALUE";

        SendMessage message = registrationHandler.handleCallback(chatId, data);

        assertTrue(message.getText().contains("Wrong choice"));
        verifyNoInteractions(botUserService);
    }

    @Test
    void testCreateSelectionMessage_CreatesButtons() {
        Long chatId = 456L;
        Enum<?>[] values = Position.values();

        SendMessage message = registrationHandler.createSelectionMessage(chatId, "Test", values);

        assertEquals("Test", message.getText());
        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) message.getReplyMarkup();
        assertNotNull(markup);
        assertEquals(values.length, markup.getKeyboard().size());
    }
}
