package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.entity.Feedback;
import com.autovoice.enums.Sentiment;
import com.autovoice.repository.BotUserRepository;
import com.autovoice.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private BotUserRepository botUserRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFeedback_ShouldSave_WhenUserExists() {
        Long chatId = 123L;
        String message = "Test feedback";
        Sentiment sentiment = Sentiment.POSITIVE;
        int criticalLevel = 3;
        String solution = "Fix it";

        BotUser user = new BotUser();
        user.setChatId(chatId);

        Feedback savedFeedback = Feedback.builder()
                .id(1L)
                .chatId(chatId)
                .message(message)
                .sentiment(sentiment)
                .criticalLevel(criticalLevel)
                .solution(solution)
                .user(user)
                .build();

        when(botUserRepository.findByChatId(chatId)).thenReturn(Optional.of(user));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);

        Feedback result = feedbackService.saveFeedback(chatId, message, sentiment, criticalLevel, solution);

        assertNotNull(result);
        assertEquals(savedFeedback.getId(), result.getId());
        assertEquals(message, result.getMessage());
        assertEquals(sentiment, result.getSentiment());
        assertEquals(criticalLevel, result.getCriticalLevel());
        assertEquals(solution, result.getSolution());
        assertEquals(user, result.getUser());

        verify(botUserRepository, times(1)).findByChatId(chatId);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void saveFeedback_ShouldThrowException_WhenUserNotFound() {
        Long chatId = 999L;

        when(botUserRepository.findByChatId(chatId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.saveFeedback(chatId, "msg", Sentiment.NEUTRAL, 1, "none");
        });

        verify(botUserRepository, times(1)).findByChatId(chatId);
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void findByChatId_ShouldReturnListOfFeedbacks() {
        Long chatId = 456L;

        Feedback f1 = Feedback.builder().id(1L).chatId(chatId).message("m1").build();
        Feedback f2 = Feedback.builder().id(2L).chatId(chatId).message("m2").build();

        when(feedbackRepository.findByChatId(chatId)).thenReturn(List.of(f1, f2));

        List<Feedback> result = feedbackService.findByChatId(chatId);

        assertEquals(2, result.size());
        assertEquals("m1", result.get(0).getMessage());
        assertEquals("m2", result.get(1).getMessage());

        verify(feedbackRepository, times(1)).findByChatId(chatId);
    }
}
