package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.entity.Feedback;
import com.autovoice.enums.Sentiment;
import com.autovoice.repository.BotUserRepository;
import com.autovoice.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final BotUserRepository botUserRepository;

    @Override
    public Feedback saveFeedback(Long chatId, String message, Sentiment sentiment, int criticalLevel, String solution) {
        BotUser user = botUserRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Feedback feedback = Feedback.builder()
                .user(user)
                .message(message)
                .sentiment(sentiment)
                .criticalLevel(criticalLevel)
                .solution(solution)
                .chatId(chatId)
                .build();

        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> findByChatId(Long chatId) {
        return feedbackRepository.findByChatId(chatId);
    }
}
