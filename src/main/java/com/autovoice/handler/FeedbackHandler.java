package com.autovoice.handler;

import com.autovoice.entity.Feedback;
import com.autovoice.enums.Sentiment;
import com.autovoice.service.FeedbackService;
import com.autovoice.service.GoogleSheetsService;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class FeedbackHandler {

    private final FeedbackService feedbackService;

    private final OpenAIClient openAIClient;

    private final GoogleSheetsService googleSheetsService;

    public FeedbackHandler(FeedbackService feedbackService,
                           @Autowired(required = false) OpenAIClient openAIClient,
                           GoogleSheetsService googleSheetsService) {
        this.feedbackService = feedbackService;
        this.openAIClient = openAIClient;
        this.googleSheetsService = googleSheetsService;
    }

    public Feedback handleNewFeedback(Long chatId, String message) {
        FeedbackAnalysisResult result = analyzeFeedback(message);
        Feedback feedback = feedbackService.saveFeedback(
                chatId,
                message,
                result.sentiment(),
                result.criticalLevel(),
                result.solution()
        );

        try {
            googleSheetsService.appendRow(
                    List.of(chatId, message, result.sentiment().name(), result.criticalLevel(), result.solution()),
                    "Sheet1"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feedback;
    }

    private FeedbackAnalysisResult analyzeFeedback(String userMessage) {
        if (openAIClient == null) {
            return fallbackAnalysis(userMessage);
        }

        try {
            ChatCompletionMessageParam systemMessage = ChatCompletionMessageParam.ofSystem(
                    ChatCompletionSystemMessageParam.builder()
                            .content("You are a feedback analyzer. Return ONLY valid JSON like {\"sentiment\":\"...\",\"criticalLevel\":n, \"solution\":\"...\"}.")
                            .build()
            );

            ChatCompletionMessageParam messageParam = ChatCompletionMessageParam.ofUser(
                    ChatCompletionUserMessageParam.builder()
                            .content(userMessage)
                            .build());

            ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                    .model("gpt-3.5-turbo")
                    .addMessage(systemMessage)
                    .addMessage(messageParam)
                    .build();

            ChatCompletion chatCompletion = openAIClient.chat().completions().create(createParams);

            String content = chatCompletion.choices().get(0).message().content().orElse("");

            String sentimentValue = extractJsonValue(content, "sentiment");
            String criticalLevelValue = extractJsonValue(content, "criticalLevel");
            String solutionValue = extractJsonValue(content, "solution");

            Sentiment sentiment;
            try {
                sentiment = Sentiment.valueOf(sentimentValue.toUpperCase());
            } catch (Exception e) {
                sentiment = Sentiment.NEUTRAL;
            }

            int criticalLevel;
            try {
                criticalLevel = Integer.parseInt(criticalLevelValue);
            } catch (Exception e) {
                criticalLevel = 3;
            }

            return new FeedbackAnalysisResult(sentiment, criticalLevel, solutionValue);
        } catch (Exception e) {
            return fallbackAnalysis(userMessage);
        }
    }

    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"?(.*?)\"?[,}]";
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private record FeedbackAnalysisResult(Sentiment sentiment, int criticalLevel, String solution) {
    }

    private FeedbackAnalysisResult fallbackAnalysis(String userMessage) {
        Sentiment sentiment = Sentiment.NEUTRAL;
        int criticalLevel = 3;
        String solution = "Тестовий режим: GPT недоступний. Тут буде рішення.";

        String text = userMessage.toLowerCase();
        if (text.contains("поганий") || text.contains("затримка") || text.contains("немає")) {
            sentiment = Sentiment.NEGATIVE;
            criticalLevel = 5;
            solution = "Передати скаргу керівництву.";
        } else if (text.contains("дякую") || text.contains("чудово")) {
            sentiment = Sentiment.POSITIVE;
            criticalLevel = 1;
            solution = "Все добре, дякуємо!";
        }

        return new FeedbackAnalysisResult(sentiment, criticalLevel, solution);
    }
}
