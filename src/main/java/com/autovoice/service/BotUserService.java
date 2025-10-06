package com.autovoice.service;

import com.autovoice.entity.BotUser;
import com.autovoice.enums.Branch;
import com.autovoice.enums.Role;

public interface BotUserService {

    public BotUser getByChatId(Long chatId);

    public boolean existsByChatId(Long chatId);

    public void save(BotUser botUser);

    public void saveRole(long chatId, Role role);

    public void saveBranch(long chatId, Branch branch);
}
