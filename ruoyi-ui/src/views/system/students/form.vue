<template>
  <div class="app-container student-form-page">
    <!-- 顶部标题栏 -->
    <div class="form-header">
      <div class="form-title">{{ title }}</div>
      <div v-if="!isNewStudent && form.name" class="student-name">
        <img src="@/assets/jb_xx/student.png" alt="" class="student-icon" />
        <span class="name">{{ form.name }}</span>
      </div>
      <el-button @click="handleCancel" plain size="small">关 闭</el-button>
    </div>

    <!-- 主内容区域，使用el-container布局 -->
    <el-container class="main-container">
      <!-- 左侧学生列表 -->
      <el-aside width="280px" class="student-list-aside" :class="{ 'new-student-mode': isNewStudent }">
        <div class="student-list-header">
          <div class="student-list-header-content">
            <h3>学生列表</h3>
            <el-input
              v-model="studentListQuery"
              placeholder="搜索学生姓名"
              size="small"
              prefix-icon="el-icon-search"
              clearable
              @input="loadStudentList"
              @clear="loadStudentList"
              :disabled="isNewStudent"
            ></el-input>
          </div>
        </div>
        <div class="student-list-container">
          <el-table
            :data="studentList"
            :loading="studentListLoading"
            @row-click="handleStudentSelect"
            height="calc(100vh - 200px)"
            class="student-list-table"
            ref="studentListTable"
            row-key="id"
            :class="{ 'list-disabled': isNewStudent }"
            :row-style="rowStyle"
          >
            <el-table-column prop="name" label="姓名" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="gradeName" label="年级" width="160" show-overflow-tooltip></el-table-column>
          </el-table>
          <div v-if="studentList.length === 0 && !studentListLoading" class="no-students-tip">
            <i class="el-icon-warning"></i>
            <span v-if="!isNewStudent">暂无学生数据</span>
            <span v-else>新增模式下无法选择其他学生</span>
          </div>

          <!-- 分页组件 -->
          <div class="student-list-pagination" v-if="!isNewStudent">
            <el-pagination
              @size-change="handleStudentListSizeChange"
              @current-change="handleStudentListPageChange"
              :current-page="studentListPageNum"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="studentListPageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="studentListTotal"
              small
            >
            </el-pagination>
          </div>
        </div>
      </el-aside>

      <!-- 右侧学生详情 -->
      <el-main class="student-detail-main">
        <!-- 表单内容容器 -->
        <div class="form-container">
          <!-- 选项卡 -->
          <div class="section-nav">
            <el-tabs v-model="activeTab" type="card" class="section-tabs" @tab-click="handleTabClick">
              <el-tab-pane name="basic">
                <template slot="label">
                  <img src="@/assets/jb_xx/1.png" alt="" class="tab-icon" />
                  <span>基本信息</span>
                </template>
              </el-tab-pane>
              <el-tab-pane name="family" :disabled="isTabDisabled">
                <template slot="label">
                  <span :class="{'disabled-tab': isTabDisabled}">
                    <img src="@/assets/jb_xx/2.png" alt="" class="tab-icon" />
                    <span>家庭成员</span>
                    <el-tooltip v-if="isTabDisabled" content="请先保存学生基本信息" placement="top">
                      <i class="el-icon-warning-outline" style="margin-left: 5px; color: #E6A23C;"></i>
                    </el-tooltip>
                  </span>
                </template>
              </el-tab-pane>
              <el-tab-pane name="bank" :disabled="isTabDisabled">
                <template slot="label">
                  <span :class="{'disabled-tab': isTabDisabled}">
                    <img src="@/assets/jb_xx/3.png" alt="" class="tab-icon" />
                    <span>银行卡</span>
                    <el-tooltip v-if="isTabDisabled" content="请先保存学生基本信息" placement="top">
                      <i class="el-icon-warning-outline" style="margin-left: 5px; color: #E6A23C;"></i>
                    </el-tooltip>
                  </span>
                </template>
              </el-tab-pane>
              <el-tab-pane name="report" :disabled="isTabDisabled">
                <template slot="label">
                  <span :class="{'disabled-tab': isTabDisabled}">
                    <img src="@/assets/jb_xx/4.png" alt="" class="tab-icon" />
                    <span>助学金申请表</span>
                    <el-tooltip v-if="isTabDisabled" content="请先保存学生基本信息" placement="top">
                      <i class="el-icon-warning-outline" style="margin-left: 5px; color: #E6A23C;"></i>
                    </el-tooltip>
                  </span>
                </template>
              </el-tab-pane>
              <!-- 受助记录标签页 -->
              <el-tab-pane name="subsidy" v-if="showSubsidyTab">
                <template slot="label">
                  <img src="@/assets/jb_xx/5.png" alt="" class="tab-icon" />
                  <span>受助记录</span>
                </template>
              </el-tab-pane>
            </el-tabs>
          </div>

          <!-- 表单内容 -->
          <div class="form-content">
            <el-form ref="form" :model="form" :rules="rules" label-position="top" class="form-layout compact-form" :disabled="isViewMode">
              <!-- 基本信息标签页 -->
              <div v-show="activeTab === 'basic'">
                <!-- 学期信息 -->
                <div class="section-title">学期信息</div>
                <el-row :gutter="12">
                  <el-col :span="8">
                    <el-form-item label="学年学期" prop="yearSemesterId">
                      <el-select v-model="form.yearSemesterId" placeholder="请选择学年学期" style="width: 100%">
                        <el-option
                          v-for="item in yearSemesterOptions"
                          :key="item.id"
                          :label="`${item.schoolYear || ''} ${item.semesterLabel || ''}`"
                          :value="item.id"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>

                <!-- 基本信息 -->
                <div class="section-title">基本信息</div>
                <el-row :gutter="12">
                  <el-col :span="8">
                    <el-form-item label="姓名" prop="name">
                      <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="性别" prop="gender">
                      <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                        <el-option label="男" value="1" />
                        <el-option label="女" value="0" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="民族" prop="ethnicity">
                      <el-select v-model="form.ethnicity" placeholder="请选择民族" filterable style="width: 100%">
                        <el-option
                          v-for="dict in dict.type.sys_student_ethnicity"
                          :key="dict.value"
                          :label="dict.label"
                          :value="dict.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="12">
                  <el-col :span="8">
                    <el-form-item label="出生日期" prop="birthday">
                      <el-date-picker
                        v-model="form.birthday"
                        type="date"
                        placeholder="请选择出生日期"
                        value-format="yyyy-MM-dd"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="政治面貌" prop="politicalStatus">
                      <el-select v-model="form.politicalStatus" placeholder="请选择政治面貌" style="width: 100%">
                        <el-option label="群众" value="群众" />
                        <el-option label="团员" value="团员" />
                        <el-option label="预备党员" value="预备党员" />
                        <el-option label="党员" value="党员" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="联系电话" prop="phone">
                      <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="12">
                  <el-col :span="12">
                    <el-form-item label="身份证号" prop="idCardNo">
                      <el-input v-model="form.idCardNo" placeholder="请输入18位身份证号" maxlength="18" @blur="handleIdCardNoBlur" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="学籍号" prop="studentNo">
                      <el-input v-model="form.studentNo" placeholder="请输入学籍号" maxlength="32" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <!-- 户籍信息 -->
                <div class="section-title">户籍信息</div>
                <el-row :gutter="12">
                  <el-col :span="24">
                    <el-form-item label="户籍所在地" prop="domicile">
                      <div class="domicile-inputs">
                        <el-cascader
                          v-model="form.regionCodes"
                          :options="guangxiRegions"
                          :props="{
                            value: 'value',
                            label: 'label',
                            children: 'children',
                            checkStrictly: true,
                            expandTrigger: 'hover'
                          }"
                          placeholder="请选择市/县/乡镇"
                          clearable
                          filterable
                          style="width: 40%"
                          @change="handleRegionChange"
                        />
                        <el-input
                          v-model="form.village"
                          placeholder="请输入村/社区"
                          maxlength="50"
                          style="width: 28%; margin-left: 10px"
                        />
                        <el-input
                          v-model="form.hamlet"
                          placeholder="请输入屯/组"
                          maxlength="50"
                          style="width: 28%; margin-left: 10px"
                        />
                      </div>
                    </el-form-item>
                  </el-col>
                </el-row>

                <!-- 学籍信息 -->
                <div class="section-title">学籍信息</div>
                <el-row :gutter="12">
                  <el-col :span="12">
                    <el-form-item label="学制-年级-班级" prop="schoolPlanGradeClass">
                      <el-cascader
                        v-model="schoolPlanGradeClassValue"
                        :options="schoolPlanGradeClassTree"
                        :props="cascaderProps"
                        placeholder="请选择学制-年级-班级"
                        style="width: 100%"
                        @change="handleSchoolPlanGradeClassChange"
                        clearable
                        filterable
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="入学时间" prop="enrollmentDate">
                      <el-date-picker
                        v-model="form.enrollmentDate"
                        type="date"
                        placeholder="请选择入学时间"
                        value-format="yyyy-MM-dd"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="12">
                  <el-col :span="12">
                    <el-form-item label="就读状态" prop="studyStatus">
                      <el-select v-model="form.studyStatus" placeholder="请选择就读状态" style="width: 100%">
                        <el-option
                          v-for="dict in dict.type.sys_study_status"
                          :key="dict.value"
                          :label="dict.label"
                          :value="dict.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="民族高中班" prop="isEthnicClass">
                      <el-radio-group v-model="form.isEthnicClass">
                        <el-radio label="1">是</el-radio>
                        <el-radio label="0">否</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                </el-row>

                <!-- 困难认定 -->
                <div class="section-title">困难认定</div>
                <el-row :gutter="12">
                  <el-col :span="12">
                    <el-form-item label="困难类型" prop="difficultyTypeId">
                      <el-select v-model="form.difficultyTypeId" placeholder="请选择困难类型" style="width: 100%" @change="handleDifficultyTypeChange">
                        <el-option
                          v-for="dict in dict.type.sys_difficulty_type"
                          :key="dict.value"
                          :label="dict.label"
                          :value="dict.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="困难等级" prop="difficultyLevelId">
                      <el-select v-model="form.difficultyLevelId" placeholder="请选择困难等级" style="width: 100%">
                        <el-option
                          v-for="dict in dict.type.sys_difficulty_level"
                          :key="dict.value"
                          :label="dict.label"
                          :value="dict.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="12" v-if="showPovertyReliefYear">
                  <el-col :span="12">
                    <el-form-item label="是否脱贫户" prop="isPovertyReliefFamily">
                      <el-radio-group v-model="form.isPovertyReliefFamily" disabled>
                        <el-radio label="1">是</el-radio>
                        <el-radio label="0">否</el-radio>
                      </el-radio-group>
                      <span style="margin-left: 10px; color: #909399; font-size: 12px;">（根据困难类型自动设置）</span>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="脱贫年份" prop="povertyReliefYear">
                      <el-date-picker
                        v-model="form.povertyReliefYear"
                        type="year"
                        placeholder="请选择脱贫年份"
                        value-format="yyyy"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="12">
                  <el-col :span="24">
                    <el-form-item label="备注" prop="memo">
                      <el-input
                        v-model="form.memo"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入备注信息"
                        maxlength="500"
                        show-word-limit
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="16" class="form-buttons">
                  <el-col :span="24">
                    <el-button v-if="!isViewMode" type="primary" @click="submitForm">保 存</el-button>
                    <el-button @click="handleCancel">返 回</el-button>
                  </el-col>
                </el-row>
              </div>

              <!-- 家庭成员标签页 -->
              <div v-show="activeTab === 'family'">
                <div class="section-title">家庭成员信息</div>
                <div style="margin-bottom: 12px;">
                  <el-button v-if="!isViewMode" type="primary" plain icon="el-icon-plus" size="mini" @click="addFamilyMember">添加成员</el-button>
                </div>
                <el-table :data="familyMembers" border size="small" style="width: 100%;">
                  <el-table-column label="姓名" align="center" width="140">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.name" placeholder="姓名" size="small" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="年龄" align="center" width="100">
                    <template slot-scope="scope">
                      <el-input-number v-model="scope.row.age" :min="0" :max="150" size="small" controls-position="right" style="width: 100%;" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="关系" align="center" width="130">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.relation" placeholder="关系" size="small" style="width: 100%;" :disabled="isViewMode">
                        <el-option v-for="item in relationOptions" :key="item.value" :label="item.label" :value="item.value" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="工作单位" align="center" min-width="160">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.employer" placeholder="工作单位" size="small" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="职业" align="center" min-width="130">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.occupation" placeholder="职业" size="small" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="健康状态" align="center" width="130">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.healthStatus" placeholder="健康状态" size="small" style="width: 100%;" :disabled="isViewMode">
                        <el-option
                          v-for="dict in dict.type.sys_health_status || []"
                          :key="dict.value"
                          :label="dict.label"
                          :value="dict.value"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="100" fixed="right">
                    <template slot-scope="scope">
                      <el-button v-if="!isViewMode" type="text" size="mini" @click="removeFamilyMember(scope.$index)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div v-if="familyMembers.length === 0" style="margin-top: 8px; color: #909399; font-size: 13px;">
                  暂无家庭成员，请点击"添加成员"。
                </div>
                <el-row :gutter="16" class="form-buttons">
                  <el-col :span="24">
                    <el-button v-if="!isViewMode" type="primary" icon="el-icon-user-solid" @click="addFamilyMember">添加家庭成员</el-button>
                    <el-button v-if="!isViewMode" type="primary" @click="submitForm">保 存</el-button>
                    <el-button @click="handleCancel">返 回</el-button>
                  </el-col>
                </el-row>
              </div>

              <!-- 受助记录标签页 -->
              <div v-show="activeTab === 'subsidy'">
                <div class="section-title">受助记录（已审核通过）</div>
                <el-table
                  v-loading="subsidyRecordsLoading"
                  :data="subsidyRecords"
                  border
                  size="small"
                  style="width: 100%"
                >
                  <el-table-column label="序号" type="index" width="60" align="center" />
                  <el-table-column label="学年/学期" align="center" min-width="160">
                    <template slot-scope="scope">
                      {{ formatAcademicYearAndSemesterForSubsidyRecord(scope.row) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="补助类型" align="center" prop="subsidyType" width="120" />
                  <el-table-column label="补助金额" align="center" width="120">
                    <template slot-scope="scope">
                      ￥{{ formatAmount(scope.row.subsidyAmount) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="经济分类" align="center" width="120">
                    <template slot-scope="scope">
                      {{ getEconomyCategoryLabel(scope.row.economyCategory) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="审批状态" align="center" width="100">
                    <template slot-scope="scope">
                      <span v-if="scope.row.approvalStatusLabel">
                        {{ scope.row.approvalStatusLabel }}
                      </span>
                      <span v-else>-</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="审批时间" align="center" width="180">
                    <template slot-scope="scope">
                      {{ parseTime(scope.row.approvalDate, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
                    </template>
                  </el-table-column>
                  <el-table-column label="发放状态" align="center" width="100">
                    <template slot-scope="scope">
                      {{ scope.row.paymentStatusLabel || '-' }}
                    </template>
                  </el-table-column>
                  <el-table-column label="备注" align="center" prop="memo" show-overflow-tooltip />
                </el-table>
                <div v-if="!subsidyRecordsLoading && subsidyRecords.length === 0" class="empty-tip">
                  <i class="el-icon-info"></i>
                  <span>暂无已审核通过的受助记录</span>
                </div>
                <el-row :gutter="16" class="form-buttons">
                  <el-col :span="24">
                    <el-button @click="handleCancel">返 回</el-button>
                  </el-col>
                </el-row>
              </div>

              <!-- 银行卡标签页 -->
              <div v-show="activeTab === 'bank'">
                <div class="section-title">银行卡信息</div>
                <div style="margin-bottom: 12px;">
                  <el-button v-if="!isViewMode" type="primary" plain icon="el-icon-plus" size="mini" @click="addBankCard">添加银行卡</el-button>
                </div>
                <el-table :data="bankCards" border size="small" style="width: 100%;">
                  <el-table-column label="银行卡号" align="center" min-width="180">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.bankAccountNo" placeholder="请输入19位银行卡号" size="small" maxlength="19" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="所属银行" align="center" min-width="140">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.bankType" placeholder="请选择银行" size="small" style="width: 100%;" :disabled="isViewMode">
                        <el-option label="信用社" value="信用社" />
                        <el-option label="中幸银行" value="中幸银行" />
                        <el-option label="工商银行" value="工商银行" />
                        <el-option label="中国银行" value="中国银行" />
                        <el-option label="农业银行" value="农业银行" />
                        <el-option label="建设银行" value="建设银行" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="开户行" align="center" min-width="160">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.bankName" placeholder="请输入开户行" size="small" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="支行/网点" align="center" min-width="140">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.branchName" placeholder="可选" size="small" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="开卡人" align="center" min-width="120">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.accountHolder" placeholder="请输入开卡人" size="small" :disabled="isViewMode" />
                    </template>
                  </el-table-column>
                  <el-table-column label="主卡" align="center" width="90">
                    <template slot-scope="scope">
                      <el-switch
                        v-model="scope.row.isPrimary"
                        :active-value="1"
                        :inactive-value="0"
                        active-color="#13ce66"
                        inactive-color="#dcdfe6"
                        @change="val => handlePrimaryChange(scope.$index, val)"
                        size="small"
                        :disabled="isViewMode"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="100" fixed="right">
                    <template slot-scope="scope">
                      <el-button v-if="!isViewMode" type="text" size="mini" @click="removeBankCard(scope.$index)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div v-if="bankCards.length === 0" style="margin-top: 8px; color: #909399; font-size: 13px;">
                  暂无银行卡信息，请点击"添加银行卡"。
                </div>
                <el-row :gutter="16" class="form-buttons">
                  <el-col :span="24">
                    <el-button v-if="!isViewMode" type="primary" icon="el-icon-wallet" @click="addBankCard">添加银行卡</el-button>
                    <el-button v-if="!isViewMode" type="primary" @click="submitForm">保 存</el-button>
                    <el-button @click="handleCancel">返 回</el-button>
                  </el-col>
                </el-row>
              </div>

              <!-- 助学金申请表标签页 -->
              <div v-show="activeTab === 'report'">
                <div class="section-title">普通高中国家助学金申请表</div>
                <div v-if="reportLoading" class="report-loading-container" style="padding: 40px; text-align: center; color: #909399;">
                  <i class="el-icon-loading" style="font-size: 24px; margin-right: 8px; animation: rotating 2s linear infinite;"></i>
                  <p>正在加载助学金申请表...</p>
                </div>
                <div v-else-if="reportError" class="report-error-container" style="padding: 40px; text-align: center; color: #F56C6C;">
                  <i class="el-icon-warning" style="font-size: 48px;"></i>
                  <p>{{ reportError }}</p>
                  <el-button type="primary" @click="loadReport" style="margin-top: 20px;">重新加载</el-button>
                </div>
                <div v-else-if="reportUrl" class="report-container" style="height: 600px; border: 1px solid #ebeef5; border-radius: 4px; overflow: hidden;">
                  <i-frame
                    :src="reportUrl"
                    class="report-iframe"
                    @iframe-load="onReportLoad"
                    :key="'report-iframe-' + form.id + '-' + Date.now()"
                  ></i-frame>
                </div>
                <div v-else class="report-placeholder" style="padding: 40px; text-align: center; color: #909399;">
                  <p>暂无助学金申请表数据</p>
                  <p v-if="!form.id">请先保存学生信息后，再查看申请表。</p>
                  <el-button v-if="form.id" type="primary" @click="loadReport" style="margin-top: 20px;">加载申请表</el-button>
                </div>
                <el-row :gutter="16" class="form-buttons">
                  <el-col :span="24">
                    <el-button v-if="reportUrl && !reportLoading" type="primary" @click="loadReport">刷新报表</el-button>
                    <el-button @click="handleCancel">返 回</el-button>
                  </el-col>
                </el-row>
              </div>
            </el-form>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import {
  getStudents, addStudents, updateStudents,
  getSchoolPlanList, getGradeList, getClassList,
  getSchoolPlanGradeClassTree,
  listStudents
} from "@/api/system/students";
import { listYearSemesters } from "@/api/system/baseconfig";
import { listSubsidyRecord } from "@/api/system/subsidyRecord";
import { getSubsidyReportUrl } from "@/api/system/report";
import { getRegionTree } from "@/api/system/region";

import iFrame from "@/components/iFrame/index";

export default {
  name: "StudentForm",
  components: { iFrame },
  dicts: ['sys_student_gender', 'sys_student_ethnicity', 'sys_study_status', 'sys_difficulty_type', 'sys_difficulty_level', 'sys_health_status', 'sys_approval_status', 'sys_payment_status', 'sys_economy_category'],
  data() {
    return {
      title: "新增学生",
      activeTab: "basic",
      loading: false,
      isViewMode: false, // 标记是否为查看模式（只读）
      isNewStudent: true, // 标记是否为新增模式，初始为true
      hasUnsavedChanges: false, // 标记是否有未保存的修改
      hasSavedData: false, // 标记是否保存过数据，用于通知列表刷新
      originalFormData: null, // 保存原始表单数据用于对比
      isLoadingData: false, // 标记是否正在加载数据，加载时不检测变化
      showPovertyReliefYear: false, // 是否显示脱贫年份字段
      yearSemesterOptions: [],
      schoolPlanOptions: [],
      gradeOptions: [],
      classOptions: [],
      schoolPlanGradeClassTree: [], // 学制-年级-班级树状数据
      schoolPlanGradeClassValue: [], // 级联选择器的值 [学制ID, 年级ID, 班级ID]
      cascaderProps: {
        value: 'value',
        label: 'label',
        children: 'children',
        expandTrigger: 'hover'
      },
      guangxiRegions: [], // 从后端获取
      relationOptions: [
        { value: '父亲', label: '父亲' },
        { value: '母亲', label: '母亲' },
        { value: '祖父母', label: '祖父母' },
        { value: '兄弟姐妹', label: '兄弟姐妹' },
        { value: '监护人', label: '监护人' },
        { value: '其他', label: '其他' }
      ],
      familyMembers: [],
      bankCards: [],
      subsidyRecords: [], // 受助记录数据
      subsidyRecordsLoading: false, // 受助记录加载状态
      // 报表相关
      reportUrl: '',
      reportLoading: false,
      reportError: null,
      // 学生列表相关
      studentList: [],
      studentListLoading: false,
      studentListQuery: '',
      studentListPageNum: 1,
      studentListPageSize: 10,
      studentListTotal: 0,
      form: {},
      rules: {
        yearSemesterId: [
          { required: true, message: "学年学期不能为空", trigger: "change" }
        ],
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "性别不能为空", trigger: "change" }
        ],
        ethnicity: [
          { required: true, message: "民族不能为空", trigger: "change" }
        ],
        domicile: [
          { required: true, message: "户籍所在地不能为空", trigger: "blur" }
        ],
        studentNo: [
          { required: true, message: "学籍号不能为空", trigger: "blur" }
        ],
        // 学制-年级-班级的验证逻辑已移至后端，前端仅做基本提示
        // 后端会在 validateBusinessData 方法中验证学制-年级-班级是否完整
        schoolPlanGradeClass: [],
        schoolingPlanId: [],
        gradeId: [],
        classId: [],
        studyStatus: [
          { required: true, message: "就读状态不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    // 先加载地区数据，再加载其他数据
    this.loadRegionData();
    this.loadSchoolPlanGradeClassTree(); // 加载学制-年级-班级树状数据
    this.loadData();
    this.$nextTick(() => {
      // 加载学生列表
      // 在非新增模式下（包括编辑和查看模式）加载学生列表
      if (!this.isNewStudent) {
        this.loadStudentListWithCallback();
      }
    });
  },
  watch: {
    // 监听路由变化，当路由参数变化时重新加载数据
    '$route'(to, from) {
      // 如果是同一个页面，但参数变了，重新加载
      if (to.path === from.path) {
        console.log('[Form.vue] 路由参数变化，重新加载数据');
        this.loadData();
      }
    },
    // 监听表单ID变化，更新左侧学生列表选中状态
    'form.id'(newId) {
      if (newId) {
        this.$nextTick(() => {
          const currentStudent = this.studentList.find(s => s.id === newId);
          if (currentStudent && this.$refs.studentListTable) {
            this.$refs.studentListTable.setCurrentRow(currentStudent);
          }
        });
      }
    },
    // 监听表单数据变化
    form: {
      handler() {
        this.checkFormChanges();
      },
      deep: true
    },
    // 监听家庭成员变化
    familyMembers: {
      handler() {
        this.checkFormChanges();
      },
      deep: true
    },
    // 监听银行卡变化
    bankCards: {
      handler() {
        this.checkFormChanges();
      },
      deep: true
    },
    // 监听Tab切换，切换到受助记录tab或报表tab时加载数据
    activeTab(newVal) {
      if (newVal === 'subsidy' && this.form.id) {
        this.loadStudentSubsidyRecords(this.form.id);
      }
      if (newVal === 'report' && this.form.id && !this.reportUrl && !this.reportLoading) {
        this.loadReport();
      }
    }
  },
  computed: {
    /**
     * 计算属性：判断标签页是否应该被禁用
     * 逻辑：
     * 1. 如果是新增模式（isNewStudent = true）且没有学生ID，则禁用关联标签页
     * 2. 如果是编辑模式（isNewStudent = false），则允许访问所有标签页
     * 3. 如果新增后保存成功并获得ID，也允许访问其他标签页
     * 4. 这确保了必须先保存学生基本信息，再添加关联信息
     */
    isTabDisabled() {
      // 编辑模式：不禁用
      if (!this.isNewStudent) {
        return false;
      }
      // 新增模式：如果没有学生ID，则禁用其他标签页
      return !this.form.id;
    },
    // 计算属性：判断是否显示受助记录标签页
    showSubsidyTab() {
      // 从路由参数判断是否显示受助记录tab，例如当参数中有 showSubsidy=true 时显示
      return this.$route.query.showSubsidy === 'true' || this.$route.query.from === 'aidedStudent';
    },

    // 计算属性：当前选中的学生
    currentSelectedStudent() {
      if (this.form && this.form.id) {
        const selected = this.studentList.find(student => student.id === this.form.id);
        return selected || null;
      }
      return null;
    }
  },
  methods: {
    // 加载地区数据（从后端获取）
    loadRegionData() {
      getRegionTree().then(response => {
        if (response.code === 200 && response.data) {
          this.guangxiRegions = response.data;
          console.log('[Form.vue] 地区数据加载成功，共', response.data.length, '个顶级节点');
        } else {
          console.error('[Form.vue] 地区数据加载失败:', response.msg);
          this.$modal.msgError('加载地区数据失败：' + (response.msg || '未知错误'));
        }
      }).catch(error => {
        console.error('[Form.vue] 地区数据加载异常:', error);
        this.$modal.msgError('加载地区数据异常');
      });
    },
    loadSchoolPlanGradeClassTree() {
      // 如果树形数据已加载，直接返回 Promise.resolve
      if (this.schoolPlanGradeClassTree && this.schoolPlanGradeClassTree.length > 0) {
        return Promise.resolve();
      }

      return getSchoolPlanGradeClassTree().then(response => {
        if (response.code === 200 && response.data) {
          this.schoolPlanGradeClassTree = response.data;
          console.log('[Form.vue] 学制-年级-班级树状数据加载成功，共', response.data.length, '个学制节点');
        } else {
          console.error('[Form.vue] 学制-年级-班级树状数据加载失败:', response.msg);
          this.$modal.msgError('加载学制-年级-班级数据失败：' + (response.msg || '未知错误'));
        }
      }).catch(error => {
        console.error('[Form.vue] 学制-年级-班级树状数据加载异常:', error);
        this.$modal.msgError('加载学制-年级-班级数据异常');
      });
    },
    handleSchoolPlanGradeClassChange(value) {
      // 级联选择器的值变化时，更新表单中的学制、年级、班级ID
      if (value && value.length === 3) {
        this.form.schoolingPlanId = value[0];
        this.form.gradeId = value[1];
        this.form.classId = value[2];
        console.log('[Form.vue] 学制-年级-班级选择变化:', {
          schoolingPlanId: this.form.schoolingPlanId,
          gradeId: this.form.gradeId,
          classId: this.form.classId
        });
        // 验证逻辑在后端实现，前端仅负责数据收集
      } else {
        // 清空选择
        this.form.schoolingPlanId = null;
        this.form.gradeId = null;
        this.form.classId = null;
      }
    },
    handleIdCardNoBlur() {
      // 前端UI预览：当输入身份证号时，实时显示学籍号的预览
      // 注意：这只是UI层面的展示，真正的业务逻辑验证和保存由后端处理
      if (!this.form.idCardNo) {
        return;
      }

      const idCardNo = this.form.idCardNo.trim();
      if (idCardNo.length === 0) {
        return;
      }

      const expectedStudentNo = 'G' + idCardNo;

      // 情况1：学籍号为空，直接填充
      if (!this.form.studentNo) {
        this.form.studentNo = expectedStudentNo;
        return;
      }

      // 情况2：学籍号等于当前身份证号（未加G前缀），填充为"G"+身份证号
      if (this.form.studentNo === idCardNo) {
        this.form.studentNo = expectedStudentNo;
        return;
      }

      // 情况3：学籍号以"G"开头
      if (this.form.studentNo.startsWith('G')) {
        const studentNoWithoutG = this.form.studentNo.substring(1);

        // 如果学籍号后面是当前身份证号，说明已经匹配，不需要更新
        if (studentNoWithoutG === idCardNo) {
          return;
        }

        // 检查学籍号后面是否是身份证号格式（18位：17位数字+1位数字或X）
        const isIdCardFormat = /^\d{17}[\dXx]$/.test(studentNoWithoutG);

        if (isIdCardFormat) {
          // 学籍号是基于身份证号生成的，现在身份证号变了，应该更新学籍号
          this.form.studentNo = expectedStudentNo;
          return;
        }

        // 学籍号后面不是身份证号格式，可能是手动输入的
        // 如果是新增模式，强制更新为"G"+当前身份证号（遵循业务规则）
        if (!this.form.id) {
          this.form.studentNo = expectedStudentNo;
        }
      } else {
        // 情况4：学籍号不以"G"开头
        // 如果是新增模式，自动填充为"G"+身份证号
        if (!this.form.id) {
          this.form.studentNo = expectedStudentNo;
        }
      }
    },
    loadData() {
      this.getYearSemesters();
      this.getSchoolPlans();
      this.loadRegionData(); // 加载地区数据

      const studentId = this.$route.query.id;
      const mode = this.$route.query.mode; // 获取模式参数
      console.log('[Form.vue] 路由参数 id:', studentId, 'mode:', mode);

      // 设置查看模式：只有明确传入 mode='view' 才是查看模式
      this.isViewMode = (mode === 'view');
      console.log('[Form.vue] isViewMode 设置为:', this.isViewMode);

      if (studentId) {
        // 编辑或查看模式
        this.title = this.isViewMode ? "学生详情" : "维护学生";
        this.isNewStudent = false; // 设置为编辑模式
        this.getStudentDetail(studentId);
      } else {
        // 新增模式
        this.title = "新增学生";
        this.isNewStudent = true; // 设置为新增模式
        this.isViewMode = false; // 新增不能是查看模式
        this.initForm();

        // 在新增模式下，不需要加载学生列表
        // 左侧列表将保持为空白状态
      }
    },

    // 重写loadStudentList方法，支持分页和回调
    loadStudentListWithCallback(callback) {
      this.studentListLoading = true;

      const params = {
        pageNum: this.studentListPageNum,
        pageSize: this.studentListPageSize
      };

      // 如果有搜索关键词，添加到查询参数
      if (this.studentListQuery && this.studentListQuery.trim() !== '') {
        params.name = this.studentListQuery.trim();
      }

      listStudents(params).then(response => {
        if (response.code === 200 && response.rows) {
          // 处理学生列表数据，添加年级名称
          this.studentList = response.rows.map(item => {
            return {
              id: item.id,
              name: item.name,
              className: item.className || item.class_name || '未分配班级',
              gradeName: item.gradeName || item.grade_name || '未分配年级',
              studentNo: item.studentNo,
              schoolingPlanId: item.schoolingPlanId
            };
          });

          // 更新总记录数
          this.studentListTotal = response.total || 0;
        } else {
          this.studentList = [];
          this.studentListTotal = 0;
        }

        // 执行回调
        if (callback && typeof callback === 'function') {
          callback();
        }
      }).catch(error => {
        console.error('加载学生列表失败:', error);
        this.$message.error('加载学生列表失败');
        this.studentList = [];
        this.studentListTotal = 0;

        // 执行回调
        if (callback && typeof callback === 'function') {
          callback();
        }
      }).finally(() => {
        this.studentListLoading = false;
      });
    },

    // 分页变化处理
    handleStudentListPageChange(pageNum) {
      this.studentListPageNum = pageNum;
      this.loadStudentListWithCallback();
    },

    // 每页条数变化处理
    handleStudentListSizeChange(pageSize) {
      this.studentListPageSize = pageSize;
      this.studentListPageNum = 1; // 重置到第一页
      this.loadStudentListWithCallback();
    },
    initForm() {
      // 设置加载标记，禁用变化检测
      this.isLoadingData = true;

      this.form = {
        yearSemesterId: null,
        name: null,
        gender: null,
        ethnicity: null,
        birthday: null,
        politicalStatus: null,
        phone: null,
        idCardNo: null,
        studentNo: null,
        schoolingPlanId: null,
        gradeId: null,
        classId: null,
        enrollmentDate: null,
        studyStatus: '1', // 默认值：在校
        isEthnicClass: '0',
        domicile: null,
        regionCodes: [],
        village: null,
        hamlet: null,
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: '0',
        povertyReliefYear: null,
        memo: null
      };
      this.familyMembers = [];
      this.bankCards = [];
      this.subsidyRecords = [];
      this.schoolPlanGradeClassValue = []; // 重置级联选择器的值

      // 保存原始数据
      this.saveOriginalData();

      // 数据初始化完成，启用变化检测
      this.$nextTick(() => {
        this.isLoadingData = false;
      });
    },
    getYearSemesters() {
      console.log('[Form.vue] 开始加载学年学期列表...');
      listYearSemesters({ status: 1 }).then(res => {
        console.log('[Form.vue] 学年学期数据:', res);
        this.yearSemesterOptions = res.rows || res.data || [];
        console.log('[Form.vue] yearSemesterOptions:', this.yearSemesterOptions);

        // 如果是新增，默认选中当前学年学期
        if (!this.form.id && this.yearSemesterOptions.length > 0) {
          const currentSemester = this.yearSemesterOptions.find(item => item.isCurrent === 1);
          if (currentSemester) {
            this.$set(this.form, 'yearSemesterId', currentSemester.id);
            console.log('[Form.vue] 默认选中当前学期:', currentSemester);
          }
        }
      }).catch(error => {
        console.error('[Form.vue] 获取学年学期列表失败:', error);
        this.$message.error('获取学年学期列表失败');
      });
    },
    getSchoolPlans() {
      getSchoolPlanList().then(res => {
        this.schoolPlanOptions = res.data || [];
      });
    },
    handleSchoolPlanChange() {
      this.form.gradeId = null;
      this.form.classId = null;
      this.classOptions = [];
      if (this.form.schoolingPlanId) {
        getGradeList(this.form.schoolingPlanId).then(res => {
          this.gradeOptions = res.data || [];
        });
      } else {
        this.gradeOptions = [];
      }
    },
    handleGradeChange() {
      this.form.classId = null;
      if (this.form.gradeId) {
        getClassList(this.form.gradeId).then(res => {
          this.classOptions = res.data || [];
        });
      } else {
        this.classOptions = [];
      }
    },
    getStudentDetail(id) {
      console.log('[Form.vue] 开始加载学生信息, id:', id);

      // 设置加载标记，禁用变化检测
      this.isLoadingData = true;

      const loading = this.$loading({
        lock: true,
        text: "加载中...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)"
      });

     getStudents(id).then(res => {
        console.log('[Form.vue] 获取学生信息成功:', res.data);
        console.log('[Form.vue] 学制-年级-班级原始数据:', {
          schoolingPlanId: res.data?.schoolingPlanId,
          schoolingPlanIdType: typeof res.data?.schoolingPlanId,
          gradeId: res.data?.gradeId,
          gradeIdType: typeof res.data?.gradeId,
          classId: res.data?.classId,
          classIdType: typeof res.data?.classId
        });

        this.form = res.data || {};

        // 处理户籍地址 - 将完整地址解析为级联选择器的值
        if (this.form.domicile) {
          console.log('[Form.vue] 原始户籍地址:', this.form.domicile);
          this.parseDomicileAddress(this.form.domicile);
        }

        // 处理学年学期
        const resolvedSemesterId = this.form.currentYearSemesterId || this.form.yearSemesterId;
        this.$set(this.form, 'yearSemesterId', resolvedSemesterId);
        this.$set(this.form, 'currentYearSemesterId', resolvedSemesterId || null);

        // 处理脱贫年份
        if (this.form.povertyReliefYear !== null && this.form.povertyReliefYear !== undefined) {
          this.form.povertyReliefYear = String(this.form.povertyReliefYear);
        }

        // 根据后端返回的 isPovertyReliefFamily 值控制是否显示脱贫年份字段
        // 后端已经根据困难类型设置好了 isPovertyReliefFamily 的值
        this.showPovertyReliefYear = (this.form.isPovertyReliefFamily === '1');

        // 设置级联选择器的值（确保ID类型为数字，与树形数据的value类型一致）
        // 注意：后端返回的ID可能是字符串或数字，需要统一转换为数字类型
        const setCascaderValue = () => {
          console.log('[Form.vue] 准备设置级联选择器值，当前form数据:', {
            schoolingPlanId: this.form.schoolingPlanId,
            schoolingPlanIdType: typeof this.form.schoolingPlanId,
            gradeId: this.form.gradeId,
            gradeIdType: typeof this.form.gradeId,
            classId: this.form.classId,
            classIdType: typeof this.form.classId,
            schoolPlanGradeClassTreeLength: this.schoolPlanGradeClassTree?.length || 0
          });

          if (this.form.schoolingPlanId && this.form.gradeId && this.form.classId) {
            // 将ID转换为数字类型，确保与级联选择器树形数据的value类型匹配
            const planId = Number(this.form.schoolingPlanId);
            const gradeId = Number(this.form.gradeId);
            const classId = Number(this.form.classId);

            console.log('[Form.vue] ID转换结果:', {
              planId,
              planIdType: typeof planId,
              gradeId,
              gradeIdType: typeof gradeId,
              classId,
              classIdType: typeof classId,
              isPlanIdValid: !isNaN(planId),
              isGradeIdValid: !isNaN(gradeId),
              isClassIdValid: !isNaN(classId)
            });

            // 验证转换后的ID是否有效
            if (!isNaN(planId) && !isNaN(gradeId) && !isNaN(classId) && planId > 0 && gradeId > 0 && classId > 0) {
              this.schoolPlanGradeClassValue = [planId, gradeId, classId];
              console.log('[Form.vue] ✅ 成功设置级联选择器值:', {
                original: {
                  schoolingPlanId: this.form.schoolingPlanId,
                  gradeId: this.form.gradeId,
                  classId: this.form.classId
                },
                converted: {
                  planId,
                  gradeId,
                  classId
                },
                schoolPlanGradeClassValue: this.schoolPlanGradeClassValue,
                treeDataLength: this.schoolPlanGradeClassTree?.length || 0
              });

              // 使用 $nextTick 确保DOM更新后再验证值是否正确显示
              this.$nextTick(() => {
                console.log('[Form.vue] DOM更新后，级联选择器值:', this.schoolPlanGradeClassValue);
              });
            } else {
              console.warn('[Form.vue] ❌ ID转换失败或无效，无法设置级联选择器值:', {
                schoolingPlanId: this.form.schoolingPlanId,
                gradeId: this.form.gradeId,
                classId: this.form.classId,
                converted: {
                  planId,
                  gradeId,
                  classId
                },
                isValid: {
                  planId: !isNaN(planId) && planId > 0,
                  gradeId: !isNaN(gradeId) && gradeId > 0,
                  classId: !isNaN(classId) && classId > 0
                }
              });
              this.schoolPlanGradeClassValue = [];
            }
          } else {
            this.schoolPlanGradeClassValue = [];
            console.warn('[Form.vue] ⚠️ 学制-年级-班级信息不完整，清空级联选择器值:', {
              schoolingPlanId: this.form.schoolingPlanId,
              gradeId: this.form.gradeId,
              classId: this.form.classId
            });
          }
        };

        // 确保树形数据已加载，如果未加载则等待加载完成后再设置值
        if (this.schoolPlanGradeClassTree && this.schoolPlanGradeClassTree.length > 0) {
          // 树形数据已加载，直接设置值
          console.log('[Form.vue] 树形数据已加载，直接设置值');
          setCascaderValue();
        } else {
          // 树形数据未加载，等待加载完成后再设置值
          console.log('[Form.vue] 树形数据未加载，等待加载完成...');
          this.loadSchoolPlanGradeClassTree().then(() => {
            console.log('[Form.vue] 树形数据加载完成，设置级联选择器值');
            setCascaderValue();
          }).catch((error) => {
            console.error('[Form.vue] 树形数据加载失败:', error);
            // 即使加载失败，也尝试设置值（可能树形数据在其他地方已加载）
            setCascaderValue();
          });
        }

        // 加载家庭成员和银行卡信息
        this.familyMembers = (res.data && res.data.familyMembers) ? res.data.familyMembers : [];
        this.bankCards = (res.data && res.data.bankCards) ? res.data.bankCards : [];

        console.log('[Form.vue] 最终 form 数据:', this.form);

        // 保存原始数据用于对比
        this.saveOriginalData();

        // 数据加载完成，启用变化检测
        this.$nextTick(() => {
          this.isLoadingData = false;

          // 设置表格选中行
          if (this.$refs.studentListTable) {
            const currentStudent = this.studentList.find(s => s.id === this.form.id);
            if (currentStudent) {
              this.$refs.studentListTable.setCurrentRow(currentStudent);
            }
          }
        });

        loading.close();
      }).catch(error => {
        this.isLoadingData = false; // 加载失败也要重置标记
        loading.close();
        console.error('[Form.vue] 获取学生信息失败:', error);
        this.$message.error('获取学生信息失败，请稍后重试');
        this.$router.go(-1);
      });
    },
    handleRegionChange(value) {
      // 处理地区级联变化
      if (value && value.length > 0) {
        // 获取选中的各级名称
        const labels = this.getRegionLabels(value);
        // 拼接基础地址：广西壮族自治区 + 市 + 县 + 乡镇
        const baseAddress = '广西壮族自治区' + labels.join('');
        // 如果有村和屯，会在提交时拼接
        this.form.domicile = baseAddress;
      } else {
        this.form.domicile = null;
      }
    },
    getRegionLabels(codes) {
      // 根据区划代码获取名称
      if (!codes || codes.length === 0) return [];

      const labels = [];
      let currentLevel = this.guangxiRegions;

      for (let i = 0; i < codes.length; i++) {
        const code = codes[i];
        const item = currentLevel.find(region => region.value === code);
        if (item) {
          labels.push(item.label);
          currentLevel = item.children || [];
        }
      }

      return labels;
    },
    parseDomicileAddress(fullAddress) {
      // 将完整地址解析为级联选择器的值和村/屯
      if (!fullAddress) return;

      // 移除“广西壮族自治区”前缀
      let address = fullAddress.replace('广西壮族自治区', '');

      // 尝试匹配市/县/乡镇级联结构
      const regionCodes = [];
      let currentLevel = this.guangxiRegions;
      let remainingAddress = address;

      // 最多匹配3级（市/县/乡镇）
      for (let level = 0; level < 3 && currentLevel && currentLevel.length > 0; level++) {
        let matched = false;

        for (const region of currentLevel) {
          if (remainingAddress.startsWith(region.label)) {
            regionCodes.push(region.value);
            remainingAddress = remainingAddress.substring(region.label.length);
            currentLevel = region.children || [];
            matched = true;
            break;
          }
        }

        if (!matched) break;
      }

      // 设置级联选择器的值
      this.$set(this.form, 'regionCodes', regionCodes);

      // 剩余部分尝试解析为村/屯
      // 假设格式为：村/社区 + 屯/组
      if (remainingAddress) {
        // 简单处理：尝试找到最后一个常见的屯/组分隔符
        const hamletKeywords = ['屯', '组', '队', '小组'];
        let village = remainingAddress;
        let hamlet = '';

        for (const keyword of hamletKeywords) {
          const lastIndex = remainingAddress.lastIndexOf(keyword);
          if (lastIndex > 0) {
            // 找到分界点，分隔村和屯
            village = remainingAddress.substring(0, lastIndex);
            hamlet = remainingAddress.substring(lastIndex);
            break;
          }
        }

        this.$set(this.form, 'village', village);
        this.$set(this.form, 'hamlet', hamlet);
      } else {
        this.$set(this.form, 'village', '');
        this.$set(this.form, 'hamlet', '');
      }

      console.log('[Form.vue] 解析结果:', {
        regionCodes: this.form.regionCodes,
        village: this.form.village,
        hamlet: this.form.hamlet
      });
    },
    // 注释：前端根据字典标签控制UI显示，业务逻辑判断和数据设置由后端处理
    handleDifficultyTypeChange(value) {
      // 前端仅控制UI显示：根据困难类型字典标签判断是否显示脱贫年份字段
      // 这是UI交互逻辑，不是业务逻辑
      if (!value) {
        this.showPovertyReliefYear = false;
        return;
      }

      // 从字典中查找对应的困难类型标签
      const difficultyType = this.dict.type.sys_difficulty_type.find(dict => dict.value === value);
      if (!difficultyType) {
        this.showPovertyReliefYear = false;
        return;
      }

      // 根据标签判断是否显示脱贫年份字段（UI控制）
      const label = difficultyType.label || '';
      const isPovertyReliefType = label.includes('脱贫');
      this.showPovertyReliefYear = isPovertyReliefType;

      // 注意：不在前端设置 isPovertyReliefFamily 和 povertyReliefYear 的值
      // 这些值由后端在保存时自动设置
    },
    addFamilyMember() {
      this.familyMembers.push({
        name: null,
        age: null,
        relation: null,
        employer: null,
        occupation: null,
        healthStatus: null
      });
    },
    removeFamilyMember(index) {
      this.familyMembers.splice(index, 1);
    },
    addBankCard() {
      this.bankCards.push({
        bankAccountNo: null,
        bankType: null,
        bankName: null,
        branchName: null,
        accountHolder: null,
        isPrimary: 0
      });
    },
    removeBankCard(index) {
      this.bankCards.splice(index, 1);
    },
    // 注释：主卡唯一性控制已移至后端，前端不再做业务逻辑判断
    handlePrimaryChange(index, val) {
      // 前端仅更新UI状态，实际校验由后端处理
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return;

        // 注释：所有数据转换和业务逻辑处理已移至后端
        // 前端仅负责数据收集和提交，不进行任何业务逻辑处理
        // 包括：
        // 1. 学年学期字段映射（yearSemesterId -> currentYearSemesterId）
        // 2. 脱贫年份类型转换（String -> Integer）
        // 3. 户籍地址拼接（regionCodes + village + hamlet -> domicile）
        // 这些逻辑均由后端统一处理，确保数据一致性和安全性

        let submitData = {
          ...this.form,
          // 保留原始数据，让后端处理转换
          // regionCodes, village, hamlet 将用于后端拼接完整地址
          // yearSemesterId 将用于后端设置 currentYearSemesterId
          // povertyReliefYear 字符串将用于后端转换为 Integer
          familyMembers: this.familyMembers,
          bankCards: this.bankCards
        };

        const loading = this.$loading({
          lock: true,
          text: "保存中...",
          spinner: "el-icon-loading",
          background: "rgba(0, 0, 0, 0.7)"
        });

        const request = this.form.id ? updateStudents(submitData) : addStudents(submitData);
        request.then((res) => {
          loading.close();

          // 展示后端返回的提示信息
          this.$message.success(res.msg || '保存成功');

          // 标记数据已保存，用于关闭时通知列表刷新
          this.hasSavedData = true;

          // 禁用变化检测，避免更新数据时触发
          this.isLoadingData = true;

          // 如果是新增模式且后端返回了学生ID，设置到 form 中
          if (this.isNewStudent && res.data && res.data.id) {
            console.log('[Form.vue] 新增学生成功，后端返回学生ID:', res.data.id);

            // 设置学生ID，解锁标签页
            this.$set(this.form, 'id', res.data.id);
            this.title = '编辑学生';

            // 同步后端返回的脱贫户相关字段（后端已经处理完毕）
            if (res.data.isPovertyReliefFamily !== undefined) {
              this.$set(this.form, 'isPovertyReliefFamily', res.data.isPovertyReliefFamily);
              this.showPovertyReliefYear = (res.data.isPovertyReliefFamily === '1');
            }
            if (res.data.povertyReliefYear !== undefined && res.data.povertyReliefYear !== null) {
              this.$set(this.form, 'povertyReliefYear', String(res.data.povertyReliefYear));
            }

            // 新增模式：不跳转，留在当前页面继续添加关联信息
            // 保存成功后更新原始数据，重置修改标记
            this.saveOriginalData();

            // 启用变化检测
            this.$nextTick(() => {
              this.isLoadingData = false;
            });
            return;
          }

          // 编辑模式：也不跳转，留在当前页面
          // 同步后端返回的数据（如果有）
          if (res.data) {
            if (res.data.isPovertyReliefFamily !== undefined) {
              this.$set(this.form, 'isPovertyReliefFamily', res.data.isPovertyReliefFamily);
              this.showPovertyReliefYear = (res.data.isPovertyReliefFamily === '1');
            }
            if (res.data.povertyReliefYear !== undefined && res.data.povertyReliefYear !== null) {
              this.$set(this.form, 'povertyReliefYear', String(res.data.povertyReliefYear));
            }
          }

          // 保存成功后更新原始数据，重置修改标记
          this.saveOriginalData();

          // 启用变化检测
          this.$nextTick(() => {
            this.isLoadingData = false;
          });
          // this.$router.go(-1);
        }).catch(error => {
          loading.close();
          console.error('保存失败:', error);
          // 错误信息由后端返回，前端自动展示
        });
      });
    },
    /**
     * 处理标签页点击事件
     * 如果尝试点击被禁用的标签页，给出提示
     */
    handleTabClick(tab) {
      if (this.isTabDisabled && (tab.name === 'family' || tab.name === 'bank' || tab.name === 'report')) {
        this.$message({
          type: 'warning',
          message: '请先保存学生基本信息，再添加关联信息',
          duration: 2000
        });
        // 切换回基本信息标签页
        this.activeTab = 'basic';
      }
    },
    /**
     * 保存原始表单数据用于对比
     * 使用 JSON 深拷贝避免引用问题
     */
    saveOriginalData() {
      this.originalFormData = {
        form: JSON.parse(JSON.stringify(this.form)),
        familyMembers: JSON.parse(JSON.stringify(this.familyMembers)),
        bankCards: JSON.parse(JSON.stringify(this.bankCards))
      };
      this.hasUnsavedChanges = false;
      console.log('[Form.vue] 保存原始数据:', this.originalFormData);
    },
    /**
     * 检测表单是否有修改
     * 对比当前数据与原始数据
     */
    checkFormChanges() {
      // 如果正在加载数据，不检测变化
      if (this.isLoadingData) {
        return;
      }

      if (!this.originalFormData) {
        this.hasUnsavedChanges = false;
        return;
      }

      const currentData = {
        form: JSON.parse(JSON.stringify(this.form)),
        familyMembers: JSON.parse(JSON.stringify(this.familyMembers)),
        bankCards: JSON.parse(JSON.stringify(this.bankCards))
      };

      // 对比当前数据与原始数据
      const hasChanges = JSON.stringify(currentData) !== JSON.stringify(this.originalFormData);
      this.hasUnsavedChanges = hasChanges;
    },
    /**
     * 处理取消/关闭按钮
     * 如果有未保存的修改，弹出确认对话框
     */
    handleCancel() {
      if (this.hasUnsavedChanges) {
        this.$confirm('您有未保存的修改，是否保存？', '提示', {
          confirmButtonText: '保存',
          cancelButtonText: '不保存',
          distinguishCancelAndClose: true,
          type: 'warning'
        }).then(() => {
          // 点击“保存”按钮，触发保存操作
          this.submitForm();
          // 注意：保存成功后不会跳转，需要用户再次点击关闭
        }).catch((action) => {
          // 点击“不保存”按钮或关闭对话框
          if (action === 'cancel') {
            // 点击“不保存”，直接返回
            this.goBackAndRefresh();
          }
          // 如果是点击关闭按钮或ESC，不做任何操作
        });
      } else {
        // 没有修改，直接返回
        this.goBackAndRefresh();
      }
    },
    /**
     * 返回列表并根据需要刷新
     * 如果保存过数据，通知列表页刷新
     */
    goBackAndRefresh() {
      if (this.hasSavedData) {
        // 如果保存过数据，使用事件总线通知列表页刷新
        this.$EventBus.$emit('refreshStudentList');
      }
      // 返回上一页
      this.$router.go(-1);
    },
    /**
     * 加载学生的受助记录（已审核通过）
     */
    loadStudentSubsidyRecords(studentId) {
      if (!studentId) {
        this.subsidyRecords = [];
        return;
      }
      this.subsidyRecordsLoading = true;
      // 使用 studentId 精确查询该学生的所有已审核通过的受助记录
      listSubsidyRecord({
        studentId: studentId,
        approvalStatus: 1 // 只查询已审核通过的记录
      })
        .then(response => {
          // 后端已过滤，但前端再次确保只显示已审核通过的记录（双重保护）
          this.subsidyRecords = (response.rows || []).filter(record =>
            record.approvalStatus === 1 || record.approvalStatus === '1'
          );
        })
        .catch(error => {
          console.error('加载受助记录失败:', error);
          this.$message.error('加载受助记录失败，请稍后再试');
          this.subsidyRecords = [];
        })
        .finally(() => {
          this.subsidyRecordsLoading = false;
        });
    },
    /**
     * 格式化学年学期显示（用于受助记录）
     */
    formatAcademicYearAndSemesterForSubsidyRecord(record) {
      if (!record) return '-';
      // 使用后端返回的学年学期信息
      return `${record.schoolYear || record.academicYear || ''} ${record.semesterLabel || record.semester || ''}`.trim();
    },
    /**
     * 格式化金额显示
     */
    formatAmount(amount) {
      if (amount == null || amount === '') return '0.00';
      return parseFloat(amount).toFixed(2);
    },
    /**
     * 获取经济分类标签
     */
    getEconomyCategoryLabel(value) {
      if (!value) return '-';
      const category = this.dict.type.sys_economy_category.find(item => item.value === value);
      return category ? category.label : value;
    },
    /**
     * 从字典中选择标签
     */
    selectDictLabel(dictList, value) {
      if (!dictList || !value) return '-';
      const dict = dictList.find(item => item.value === value);
      return dict ? dict.label : value;
    },
    /**
     * 解析时间格式
     */
    parseTime(time, cFormat) {
      if (arguments.length === 0 || !time) {
        return null;
      }
      const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}';
      let date;
      if (typeof time === 'object') {
        date = time;
      } else {
        if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
          time = parseInt(time);
        }
        if ((typeof time === 'number') && (time.toString().length === 10)) {
          time = time * 1000;
        }
        date = new Date(time);
      }
      const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay()
      };
      const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
        let value = formatObj[key];
        if (key === 'a') return ['一', '二', '三', '四', '五', '六', '日'][value - 1];
        if (result.length > 0 && value < 10) {
          value = '0' + value;
        }
        return value || 0;
      });
      return time_str;
    },
    /**
     * 加载助学金申请表
     * 所有业务逻辑已移至后端，前端仅负责调用接口和展示
     */
    loadReport() {
      if (!this.form.id) {
        this.reportError = '请先保存学生信息';
        this.reportUrl = '';
        return;
      }

      this.reportLoading = true;
      this.reportError = null;

      // 调用后端接口获取助学金申请表URL（后端处理所有业务逻辑）
      getSubsidyReportUrl(this.form.id).then(res => {
        if (res.code === 200 && res.data) {
          // 获取完整的报表URL（后端返回的是相对路径，需要拼接baseUrl）
                const baseUrl = process.env.NODE_ENV === 'development'
                  ? 'http://localhost:8080'
                  : (process.env.VUE_APP_BASE_API || window.location.origin);

          // 如果后端返回的是完整URL，直接使用；如果是相对路径，拼接baseUrl
          let reportUrl = res.data.reportUrl;
          if (reportUrl && !reportUrl.startsWith('http')) {
            reportUrl = baseUrl + reportUrl;
          }

          this.reportUrl = reportUrl;
          console.log('[Form.vue] 助学金申请表URL加载成功:', reportUrl);
                } else {
          this.reportError = res.msg || '获取助学金申请表URL失败';
                  this.reportUrl = '';
                }
              }).catch(error => {
        console.error('[Form.vue] 获取助学金申请表URL失败:', error);
        // 处理错误信息
        if (error.msg) {
          this.reportError = error.msg;
        } else if (error.message) {
          this.reportError = error.message;
                } else {
          this.reportError = '无法获取助学金申请表，请检查报表配置或联系管理员';
        }
        this.reportUrl = '';
      }).finally(() => {
        this.reportLoading = false;
      });
    },
    /**
     * 报表加载完成回调
     */
    onReportLoad() {
      console.log('助学金申请表加载完成');
      this.reportLoading = false;
      this.reportError = null; // 清除错误状态
    },

    /**
     * 报表加载错误回调
     */
    onReportError() {
      console.error('助学金申请表加载失败');
      this.reportLoading = false;
      // 不清除reportUrl，因为iframe可能加载了错误页面
      // this.reportUrl = '';
      this.reportError = '助学金申请表加载失败，请检查报表配置或联系管理员';
    },

    /**
     * 加载学生列表
     */
    loadStudentList() {
      this.loadStudentListWithCallback(() => {
        // 如果当前有学生信息且在列表中，选中它
        if (this.form && this.form.id) {
          this.$nextTick(() => {
            const currentStudent = this.studentList.find(s => s.id === this.form.id);
            if (currentStudent && this.$refs.studentListTable) {
              this.$refs.studentListTable.setCurrentRow(currentStudent);
            }
          });
        }
      });
    },

    /**
     * 加载学生列表，支持分页和回调
     */
    loadStudentListWithCallback(callback) {
      this.studentListLoading = true;

      const params = {
        pageNum: this.studentListPageNum,
        pageSize: this.studentListPageSize
      };

      // 如果有搜索关键词，添加到查询参数
      if (this.studentListQuery && this.studentListQuery.trim() !== '') {
        params.name = this.studentListQuery.trim();
      }

      listStudents(params).then(response => {
        if (response.code === 200 && response.rows) {
          // 处理学生列表数据，添加年级名称
          this.studentList = response.rows.map(item => {
            return {
              id: item.id,
              name: item.name,
              className: item.className || item.class_name || '未分配班级',
              gradeName: item.gradeName || item.grade_name || '未分配年级',
              studentNo: item.studentNo,
              schoolingPlanId: item.schoolingPlanId
            };
          });

          // 更新总记录数
          this.studentListTotal = response.total || 0;
        } else {
          this.studentList = [];
          this.studentListTotal = 0;
        }

        // 执行回调
        if (callback && typeof callback === 'function') {
          callback();
        }
      }).catch(error => {
        console.error('加载学生列表失败:', error);
        this.$message.error('加载学生列表失败');
        this.studentList = [];
        this.studentListTotal = 0;

        // 执行回调
        if (callback && typeof callback === 'function') {
          callback();
        }
      }).finally(() => {
        this.studentListLoading = false;

        // 如果当前有学生信息且在列表中，确保选中它
        if (this.form && this.form.id) {
          this.$nextTick(() => {
            const currentStudent = this.studentList.find(s => s.id === this.form.id);
            if (currentStudent && this.$refs.studentListTable) {
              // 使用Element UI的表格方法确保选中状态正确
              this.$refs.studentListTable.setCurrentRow(currentStudent);
            }
          });
        }
      });
    },

    /**
     * 处理学生选择
     */
    handleStudentSelect(row) {
      // 如果是新增模式，不允许选择其他学生
      if (this.isNewStudent) {
        this.$message.warning('请先保存当前学生信息，再选择其他学生');
        return;
      }

      // 更新当前学生信息
      this.getStudentDetail(row.id);

      // 设置表格选中行
      this.$nextTick(() => {
        if (this.$refs.studentListTable) {
          this.$refs.studentListTable.setCurrentRow(row);
        }
      });

      // 如果当前在助学金申请表tab，重新加载报表
      if (this.activeTab === 'report') {
        this.loadReport();
      }

      // 如果当前在受助记录tab，重新加载受助记录
      if (this.activeTab === 'subsidy') {
        this.loadStudentSubsidyRecords(row.id);
      }
    },

    /**
     * 行样式
     */
    rowStyle({ row, rowIndex }) {
      if (this.isNewStudent) {
        return { cursor: 'not-allowed' };
      }
      return {};
    },

    /**
     * 选中学生行
     */
    selectStudentRow(row) {
      if (this.$refs.studentListTable) {
        this.$refs.studentListTable.setCurrentRow(row);
      }
    },

    handleCommand(command) {
      switch (command) {
        case 'import':
          this.handleImport();
          break;
        case 'export':
          this.handleExport();
          break;
        default:
          break;
      }
    },

    /**
     * 处理导入功能
     */
    handleImport() {
      // 创建一个隐藏的文件输入元素
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.xlsx, .xls';
      input.onchange = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // 检查文件大小（限制为10MB）
        if (file.size > 10 * 1024 * 1024) {
          this.$message.error('上传文件大小不能超过10MB！');
          return;
        }

        const formData = new FormData();
        formData.append('file', file);

        // 显示导入选项对话框
        this.$confirm(
          '<div>请选择导入模式：</div>' +
          '<div style="margin-top: 10px; display: flex; align-items: center;">' +
          '<label style="margin-right: 10px;"><input type="radio" name="updateSupport" value="false" checked> 跳过已存在记录</label>' +
          '<label><input type="radio" name="updateSupport" value="true"> 更新已存在记录</label>' +
          '</div>',
          '导入学生数据',
          {
            confirmButtonText: '确定导入',
            cancelButtonText: '取消',
            type: 'warning',
            dangerouslyUseHTMLString: true,
            showClose: false
          }
        ).then(() => {
          // 获取用户选择的更新模式
          const updateSupport = document.querySelector('input[name="updateSupport"]:checked').value === 'true';

          // 显示加载提示
          const loading = this.$loading({
            lock: true,
            text: '正在导入数据...',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });

          importStudents(formData, updateSupport).then(response => {
            loading.close();
            this.$alert(response.msg, '导入结果', {
              confirmButtonText: '确定',
              type: 'success',
              dangerouslyUseHTMLString: true
            });
            // 刷新学生列表
            this.loadStudentListWithCallback();
          }).catch(error => {
            loading.close();
            if (error && error.msg) {
              this.$alert(error.msg, '导入失败', {
                confirmButtonText: '确定',
                type: 'error',
                dangerouslyUseHTMLString: true
              });
            } else {
              this.$message.error('导入失败：' + (error || '未知错误'));
            }
          });
        }).catch(() => {
          // 用户取消操作
          this.$message.info('已取消导入');
        });
      };
      input.click();
    },

    /**
     * 下载导入模板
     */
    downloadTemplate() {
      this.download('/system/students/importTemplate', {}, `学生信息导入模板.xlsx`);
    },

    /**
     * 处理导出功能
     */
    handleExport() {
      this.$confirm('确定要导出学生数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const loading = this.$loading({
          lock: true,
          text: '正在导出数据...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });

        // 调用导出API
        this.download('/system/students/export', {}, `学生数据_${new Date().getTime()}.xlsx`).then(() => {
          loading.close();
        }).catch(() => {
          loading.close();
        });
      }).catch(() => {
        this.$message.info('已取消导出');
      });
    },
  }
};
</script>

<style scoped lang="scss">
.student-form-page {
  background: #f5f7fa;
  padding: 16px 16px 16px 0px; /* 调整左边距，让左侧更贴近 */
  margin: 0 auto 0 0; /* 靠左对齐，贴近左侧导航菜单 */
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.main-container {
  height: calc(100vh - 120px);  /* 调整高度以适应新的布局 */
  margin-top: 16px;
  margin-left: -20px; /* 向左移动，靠近左侧导航菜单 */
}

::v-deep .el-container {
  background: #f5f7fa;
}

::v-deep .student-list-aside {
  background: #fff;
  border: 1px solid #d8e1ea;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  margin-left: -180px; /* 紧贴左侧 */
  transition: opacity 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  overflow: hidden; /* 确保圆角效果 */
}

::v-deep .student-list-aside.new-student-mode {
  opacity: 0.7;
  pointer-events: none;
}

::v-deep .student-list-header {
  padding: 12px;
  border-bottom: 1px solid #409EFF;
  background: #e6f7ff;
  border-radius: 3px 3px 0 0;
}

::v-deep .student-list-header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.student-list-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

::v-deep .student-list-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

::v-deep .student-list-table {
  flex: 1;
}

::v-deep .student-list-table .el-table {
  border: none;
}

/* 优化表格背景色以区分右侧信息区域 */
::v-deep .student-list-table .el-table {
  background-color: #f0f9ff;
}

::v-deep .student-list-table .el-table__header-wrapper {
  background-color: #e6f7ff;
}

::v-deep .student-list-table .el-table__header-wrapper tr {
  background-color: #e6f7ff;
}

::v-deep .student-list-table .el-table__header-wrapper th {
  background-color: #e6f7ff;
  color: #303133;
  font-weight: 600;
}

::v-deep .student-list-table .el-table__body-wrapper {
  background-color: #f0f9ff;
}

::v-deep .student-list-table .el-table__body-wrapper tr {
  background-color: #f0f9ff;
}

::v-deep .student-list-table .el-table__body-wrapper td {
  background-color: #f0f9ff;
  border-color: #d9e9f3;
}

::v-deep .student-list-table .el-table__body tr.current-row > td,
::v-deep .student-list-table .el-table__row.current-row > td {
  background-color: #FF6B35 !important; /* 橙红色，与蓝色形成更好对比 */
  color: #fff !important;
}

::v-deep .student-list-table .el-table__body tr:hover > td,
::v-deep .student-list-table .el-table__row:hover > td {
  background-color: #ffe5cc !important; /* 淡橙色 */
}

/* 确保选中状态优先级更高 */
::v-deep .student-list-table .el-table__body tr.current-row:hover > td,
::v-deep .student-list-table .el-table__row.current-row:hover > td {
  background-color: #FF6B35 !important; /* 选中状态保持橙红色 */
  color: #fff !important;
}

::v-deep .student-list-table.list-disabled {
  opacity: 0.6;
  pointer-events: none;
}

::v-deep .student-list-table.list-disabled .el-table__row {
  cursor: not-allowed !important;
}

.no-students-tip {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 14px;
  padding: 20px;
}

.no-students-tip i {
  font-size: 24px;
  margin-bottom: 8px;
}

::v-deep .student-detail-main {
  padding: 0;
  background: #f5f7fa;
  border-radius: 0 4px 4px 0; /* 与左侧列表形成连续的视觉效果 */
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  background: #fff;
  padding: 12px 20px;
  border-radius: 0 4px 0 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  position: relative;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.form-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

// 学生姓名样式：居中、红色、字体比标题小1px、加粗
.student-name {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 15px;  // 比标题(16px)小1px
  color: #F56C6C;  // 红色
  font-weight: bold;  // 加粗
  white-space: nowrap;  // 防止换行
  display: flex;
  align-items: center;
  gap: 6px;  // 图标和姓名之间的间距
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;

  .student-icon {
    width: 16px;
    height: 16px;
    vertical-align: middle;
  }

  .name {
    font-weight: bold;  // 姓名加粗
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
}

.form-container {
  background: #fff;
  border-radius: 0 4px 4px 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-nav {
  padding: 12px 20px 0;
  border-bottom: 1px solid #409EFF;
}

.section-tabs {
  ::v-deep .el-tabs__header {
    margin: 0 !important;
    border-bottom: none;
  }

  ::v-deep .el-tabs__nav {
    border: none;
  }

  ::v-deep .el-tabs__item {
    padding: 0 16px;
    height: 36px;
    line-height: 36px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }

  // 禁用标签页样式
  ::v-deep .el-tabs__item.is-disabled {
    cursor: not-allowed;
    opacity: 0.5;

    &:hover {
      color: #C0C4CC;
    }
  }
}

// 禁用标签文本样式
.disabled-tab {
  opacity: 0.6;
  cursor: not-allowed;

  .tab-icon {
    opacity: 0.5;
  }
}

.tab-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  vertical-align: -2px;
}

.form-content {
  padding: 16px 20px 20px;
  flex: 1;
  overflow-y: auto;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 12px 0;
  padding-bottom: 8px;
  padding-left: 8px;
  border-left: 3px solid #409EFF;
  background: #f5f7fa;
  line-height: 1;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.form-layout {
  ::v-deep .el-form-item {
    margin-bottom: 16px;
  }

  ::v-deep .el-form-item__label {
    font-size: 13px;
    padding-bottom: 6px;
    line-height: 1;
    font-weight: 500;
    color: #606266;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    font-size: 13px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }

  ::v-deep .el-select .el-input__inner {
    font-size: 13px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }

  ::v-deep .el-button {
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
}

.domicile-inputs {
  display: flex;
  gap: 8px;
  width: 100%;
}

.form-buttons {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: center;
  gap: 12px;

  ::v-deep .el-col {
    text-align: center;
  }

  ::v-deep .el-button {
    padding: 8px 20px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
}

/* 报表iframe样式 */
.report-iframe {
  width: 100%;
  height: 100%;
  min-height: 600px;
  border: none;
  border-radius: 4px;
}

/* 旋转动画 */
@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 学生列表分页样式 */
.student-list-pagination {
  padding: 10px 0;
  text-align: center;
  border-top: 1px solid #409EFF;
  background: #fff;
  margin-top: 10px;
}
</style>
