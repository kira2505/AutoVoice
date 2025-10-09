package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.enums.Branch;
import com.autovoice.enums.Role;
import com.autovoice.repository.BotUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotUserServiceImpl implements BotUserService {

    @Autowired
    private BotUserRepository botUserRepository;

    @Override
    public boolean existsByChatId(Long chatId) {
        return botUserRepository.existsBotUserByChatId(chatId);
    }

    @Override
    public void saveRole(long chatId, Role role) {
        BotUser botUser = botUserRepository.findByChatId(chatId).orElse(new BotUser());

        if (botUser.getRole() == null) {
            botUser.setChatId(chatId);
            botUser.setRole(role);
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
