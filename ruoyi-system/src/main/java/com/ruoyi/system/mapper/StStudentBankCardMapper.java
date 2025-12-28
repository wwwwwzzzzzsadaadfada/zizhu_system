package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StStudentBankCard;

/**
 * 学生银行卡Mapper接口
 */
public interface StStudentBankCardMapper {
    StStudentBankCard selectStStudentBankCardById(Long id);

    List<StStudentBankCard> selectStStudentBankCardList(StStudentBankCard query);

    int insertStStudentBankCard(StStudentBankCard card);

    int updateStStudentBankCard(StStudentBankCard card);

    int deleteStStudentBankCardById(Long id);

    int deleteStStudentBankCardByIds(Long[] ids);

    int deleteByStudentId(Long studentId);
}


