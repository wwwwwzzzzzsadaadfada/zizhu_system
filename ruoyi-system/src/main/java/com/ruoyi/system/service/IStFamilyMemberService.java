package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StFamilyMember;

public interface IStFamilyMemberService {
    StFamilyMember selectStFamilyMemberById(Long id);

    List<StFamilyMember> selectStFamilyMemberList(StFamilyMember query);

    int insertStFamilyMember(StFamilyMember member);

    int updateStFamilyMember(StFamilyMember member);

    int deleteStFamilyMemberById(Long id);

    int deleteStFamilyMemberByIds(Long[] ids);

    /**
     * 覆盖保存指定学生的家庭成员（先删后插），空列表时仅清空
     */
    void saveByStudentId(Long studentId, Long yearSemesterId, List<StFamilyMember> members);
}


