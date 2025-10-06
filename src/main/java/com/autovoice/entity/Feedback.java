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

    @NotBlank(message = "Please specify the subject of your inquiry")
    private String title;

    @NotBlank(message = "Please specify the description of your inquiry")
    private String description;

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
}
