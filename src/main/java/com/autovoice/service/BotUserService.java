package com.autovoice.service;

import com.autovoice.enums.Branch;
import com.autovoice.enums.Position;

public interface BotUserService {

    boolean existsByChatId(Long chatId);

    void savePosition(long chatId, Position position);

    void saveBranch(long chatId, Branch branch);
}