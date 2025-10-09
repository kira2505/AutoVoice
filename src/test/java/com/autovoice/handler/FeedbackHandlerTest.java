package com.autovoice.handler;

import com.autovoice.entity.Feedback;
import com.autovoice.enums.Sentiment;
import com.autovoice.service.FeedbackService;
import com.autovoice.service.GoogleSheetsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FeedbackHandlerTest {

    private FeedbackService feedbackService;
    private GoogleSheetsService googleSheetsService;

    @BeforeEach
    void setUp() {
        feedbackService = mock(FeedbackService.class);
        googleSheetsService = mock(GoogleSheetsService.class);
    }

    @Test
    void handleNewFeedback_ShouldUseFallback_WhenOpenAIClientIsNull() throws Exception {
        FeedbackHandler handler = new FeedbackHandler(feedbackService, null, googleSheetsService);

        Long chatId = 123L;
        String message = "поганий сервіс";

        Feedback fakeFeedback = Feedback.builder().chatId(chatId).message(message).build();
        when(feedbackService.saveFeedback(anyLong(), anyString(), any(), anyInt(), anyString()))
                .thenReturn(fakeFeedback);

        Feedback result = handler.handleNewFeedback(chatId, message);

        verify(feedbackService, times(1)).saveFeedback(eq(chatId), eq(message), eq(Sentiment.NEGATIVE), eq(5), anyString());
        verify(googleSheetsService, times(1)).appendRow(anyList(), eq("Sheet1"));
        assertEquals(fakeFeedback, result);
    }

    @Test
    void handleNewFeedback_ShouldCatchGoogleSheetsException() throws Exception {
        // Arrange
        FeedbackHandler handler = new FeedbackHandler(feedbackService, null, googleSheetsService);
        Long chatId = 999L;
        String message = "тест";

        when(feedbackService.saveFeedback(anyLong(), anyString(), any(), anyInt(), anyString()))
                .thenReturn(Feedback.builder().chatId(chatId).message(message).build());
        doThrow(new RuntimeException("Sheets error")).when(googleSheetsService).appendRow(anyList(), anyString());

        handler.handleNewFeedback(chatId, message);

        verify(feedbackService, times(1)).saveFeedback(anyLong(), anyString(), any(), anyInt(), anyString());
    }
}
