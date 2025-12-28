package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StFamilyMember;

/**
 * 家庭成员Mapper接口
 */
public interface StFamilyMemberMapper {
    StFamilyMember selectStFamilyMemberById(Long id);

    List<StFamilyMember> selectStFamilyMemberList(StFamilyMember query);

    int insertStFamilyMember(StFamilyMember member);

    int updateStFamilyMember(StFamilyMember member);

    int deleteStFamilyMemberById(Long id);

    int deleteStFamilyMemberByIds(Long[] ids);

    int deleteByStudentId(Long studentId);
}


