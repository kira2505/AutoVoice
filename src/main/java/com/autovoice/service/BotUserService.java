package com.autovoice.service;

import com.autovoice.enums.Branch;
import com.autovoice.enums.Role;

public interface BotUserService {

    boolean existsByChatId(Long chatId);

    void saveRole(long chatId, Role role);

    void saveBranch(long chatId, Branch branch);
}
