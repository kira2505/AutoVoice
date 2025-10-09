package com.autovoice.service;

import com.autovoice.entity.Feedback;
import com.autovoice.enums.Sentiment;

import java.util.List;

public interface FeedbackService {

    Feedback saveFeedback(Long chatId, String message, Sentiment sentiment, int criticalLevel, String solution);

    List<Feedback> findByChatId(Long chatId);
}
