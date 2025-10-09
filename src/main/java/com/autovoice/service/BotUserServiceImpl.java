package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.enums.Branch;
import com.autovoice.enums.Position;
import com.autovoice.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotUserServiceImpl implements BotUserService {

    private final BotUserRepository botUserRepository;

    @Override
    public boolean existsByChatId(Long chatId) {
        return botUserRepository.existsBotUserByChatId(chatId);
    }

    @Override
    public void savePosition(long chatId, Position position) {
        BotUser botUser = botUserRepository.findByChatId(chatId).orElse(new BotUser());

        if (botUser.getPosition() == null) {
            botUser.setChatId(chatId);
            botUser.setPosition(position);
            botUserRepository.save(botUser);
        }
    }

    @Override
    public void saveBranch(long chatId, Branch branch) {
        BotUser botUser = botUserRepository.findByChatId(chatId)
                .orElseGet(() -> {
                    BotUser newUser = new BotUser();
                    newUser.setChatId(chatId);
                    return newUser;
                });

        botUser.setBranch(branch);
        botUserRepository.save(botUser);
    }
}
