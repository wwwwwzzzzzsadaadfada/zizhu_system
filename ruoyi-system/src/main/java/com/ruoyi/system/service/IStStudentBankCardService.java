package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StStudentBankCard;

public interface IStStudentBankCardService {
    StStudentBankCard selectStStudentBankCardById(Long id);

    List<StStudentBankCard> selectStStudentBankCardList(StStudentBankCard query);

    int insertStStudentBankCard(StStudentBankCard card);

    int updateStStudentBankCard(StStudentBankCard card);

    int deleteStStudentBankCardById(Long id);

    int deleteStStudentBankCardByIds(Long[] ids);

    /**
     * 根据学生 ID 删除其所有银行卡
     */
    int deleteByStudentId(Long studentId);

    /**
     * 覆盖保存指定学生的银行卡列表（先删后插）
     */
    void saveByStudentId(Long studentId, List<StStudentBankCard> cards);
}


