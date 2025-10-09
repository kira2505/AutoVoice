package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.enums.Branch;
import com.autovoice.enums.Position;
import com.autovoice.repository.BotUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BotUserServiceImplTest {

    @Mock
    private BotUserRepository botUserRepository;

    @InjectMocks
    private BotUserServiceImpl botUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsByChatId_ShouldReturnTrue_WhenUserExists() {
        long chatId = 12345L;
        when(botUserRepository.existsBotUserByChatId(chatId)).thenReturn(true);

        boolean result = botUserService.existsByChatId(chatId);

        assertTrue(result);
        verify(botUserRepository, times(1)).existsBotUserByChatId(chatId);
    }

    @Test
    void existsByChatId_ShouldReturnFalse_WhenUserNotExists() {
        long chatId = 12345L;
        when(botUserRepository.existsBotUserByChatId(chatId)).thenReturn(false);

        boolean result = botUserService.existsByChatId(chatId);

        assertFalse(result);
        verify(botUserRepository, times(1)).existsBotUserByChatId(chatId);
    }

    @Test
    void savePosition_ShouldCreateNewUser_WhenUserNotExists() {
        long chatId = 111L;
        Position position = Position.ELECTRICIAN;

        when(botUserRepository.findByChatId(chatId)).thenReturn(Optional.empty());
        when(botUserRepository.save(any(BotUser.class))).thenAnswer(i -> i.getArgument(0));

        botUserService.savePosition(chatId, position);

        verify(botUserRepository).save(argThat(user ->
                user.getChatId() == chatId && user.getPosition() == position
        ));
    }

    @Test
    void savePosition_ShouldNotUpdate_WhenUserAlreadyHasPosition() {
        long chatId = 222L;
        BotUser existing = new BotUser();
        existing.setChatId(chatId);
        existing.setPosition(Position.ELECTRICIAN);

        when(botUserRepository.findByChatId(chatId)).thenReturn(Optional.of(existing));

        botUserService.savePosition(chatId, Position.MANAGER);

        verify(botUserRepository, never()).save(any());
    }

    @Test
    void saveBranch_ShouldCreateNewUser_WhenUserNotExists() {
        long chatId = 333L;
        Branch branch = Branch.BRANCH_3;

        when(botUserRepository.findByChatId(chatId)).thenReturn(Optional.empty());
        when(botUserRepository.save(any(BotUser.class))).thenAnswer(i -> i.getArgument(0));

        botUserService.saveBranch(chatId, branch);

        verify(botUserRepository).save(argThat(user ->
                user.getChatId() == chatId && user.getBranch() == branch
        ));
    }

    @Test
    void saveBranch_ShouldUpdateBranch_WhenUserExists() {
        long chatId = 444L;
        BotUser existing = new BotUser();
        existing.setChatId(chatId);
        existing.setBranch(Branch.BRANCH_1);

        when(botUserRepository.findByChatId(chatId)).thenReturn(Optional.of(existing));
        when(botUserRepository.save(any(BotUser.class))).thenAnswer(i -> i.getArgument(0));

        botUserService.saveBranch(chatId, Branch.BRANCH_2);

        verify(botUserRepository).save(argThat(user ->
                user.getChatId() == chatId && user.getBranch() == Branch.BRANCH_2
        ));
    }
}
