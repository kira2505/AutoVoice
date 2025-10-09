package com.autovoice.entity;

import com.autovoice.enums.Sentiment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String message;

    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;

    @Min(1)
    @Max(5)
    private int criticalLevel;

    private String solution;

    @ManyToOne
    @JoinColumn(name = "bot_user_id")
    private BotUser user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    private Long chatId;

    @Override
    public String toString() {
        return createdAt.format(DateTimeFormatter.ISO_DATE) + ": " + message;
    }
}
