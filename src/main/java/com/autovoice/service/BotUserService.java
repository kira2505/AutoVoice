package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.enums.Branch;
import com.autovoice.enums.Role;

public interface BotUserService {

    BotUser getByChatId(Long chatId);

    boolean existsByChatId(Long chatId);

    void save(BotUser botUser);

    void saveRole(long chatId, Role role);

    void saveBranch(long chatId, Branch branch);

    Role hasRole(Long chatId);
}
