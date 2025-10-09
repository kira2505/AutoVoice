package com.autovoice.repository;

import com.autovoice.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    boolean existsBotUserByChatId(Long chatId);

    Optional<BotUser> findByChatId(Long chatId);
}
