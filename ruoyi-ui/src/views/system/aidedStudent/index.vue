<template>
  <div class="app-container aided-student-page">
    <!-- 查询表单、操作栏、表格 合并毛玻璃容器 -->
    <div class="query-table-wrapper">
      <div class="query-form-header">
        <el-button
          type="text"
          size="mini"
          :icon="queryFormCollapsed ? 'el-icon-arrow-down' : 'el-icon-arrow-up'"
          @click="queryFormCollapsed = !queryFormCollapsed"
          class="collapse-btn"
        >
          {{ queryFormCollapsed ? '展开' : '收起' }}
        </el-button>
      </div>
      <el-collapse-transition>
        <el-form
          v-show="!queryFormCollapsed && showSearch"
          :model="queryParams"
          ref="queryForm"
          size="small"
          :inline="true"
          label-width="68px"
          class="query-form"
        >
      <el-form-item label="学生姓名" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入学生姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input
          v-model="queryParams.idCard"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="困难类型" prop="difficultyTypeId">
        <el-select v-model="queryParams.difficultyTypeId" placeholder="请选择困难类型" clearable>
          <el-option
            v-for="dict in dict.type.sys_difficulty_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="困难等级" prop="difficultyLevelId">
        <el-select v-model="queryParams.difficultyLevelId" placeholder="请选择困难等级" clearable>
          <el-option
            v-for="dict in dict.type.sys_difficulty_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="脱贫户" prop="isPovertyReliefFamily">
        <el-select v-model="queryParams.isPovertyReliefFamily" placeholder="是否脱贫户" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="学年" prop="academicYear">
        <el-input
          v-model="queryParams.academicYear"
          placeholder="默认为当前学年，可手动输入查询历史"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学期" prop="semester">
        <el-select v-model="queryParams.semester" placeholder="默认为当前学期" clearable>
          <el-option
            v-for="item in semesterOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
      </el-collapse-transition>

      <!-- 操作栏 -->
    <el-row :gutter="10" class="mb8 action-row">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:aidedStudent:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleSync"
          v-hasPermi="['system:aidedStudent:sync']"
        >同步基础数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-money"
          size="mini"
          @click="openBatchPaymentDialog"
          v-hasPermi="['system:subsidyRecord:payment']"
        >批量发放</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['system:subsidyRecord:import']"
        >模板批量发放</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleDownloadTemplate"
        >下载模板</el-button>
      </el-col>
      <el-col :span="5" class="current-semester-tip">
        <el-tag
          :type="currentSemester ? 'info' : 'warning'"
          effect="plain"
          size="small"
          class="semester-tag"
        >
          <span v-if="currentSemester">当前学期：{{ currentSemesterDisplay }}</span>
          <span v-else>当前学期未设置</span>
        </el-tag>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
      <div class="table-section">
    <el-table
      class="aided-table"
      v-loading="loading"
      :data="aidedStudentList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" min-width="80" />
      <el-table-column label="学生姓名" align="center" prop="studentName" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="openStudentDetailDialog(scope.row)">
            {{ scope.row.studentName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="身份证号" align="center" prop="idCard" min-width="180" show-overflow-tooltip />
      <el-table-column label="性别" align="center" prop="gender" min-width="80">
        <template slot-scope="scope">
          {{ selectDictLabel(dict.type.sys_student_gender, scope.row.gender) || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="民族" align="center" prop="nationName" min-width="100">
        <template slot-scope="scope">
          {{ scope.row.nationName || scope.row.nation || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="年级班级" align="center" min-width="150">
        <template slot-scope="scope">
          {{ formatGradeAndClass(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="困难类型" align="center" prop="difficultyTypeId" min-width="150">
        <template slot-scope="scope">
          <span>{{ formatSupportInfo(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="困难等级" align="center" prop="difficultyLevelId" min-width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.difficultyLevelId">
            {{ formatDifficultyLevel(scope.row.difficultyLevelId) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="资助金额/元" align="center" min-width="160">
        <template slot-scope="scope">
          <!-- 显示助学金 + 免学杂费总金额 -->
          <div v-if="(scope.row.scholarshipAmount && scope.row.scholarshipAmount > 0) || 
                      (scope.row.tuitionWaiverAmount && scope.row.tuitionWaiverAmount > 0)" 
               style="line-height: 1.8;">
            <div v-if="scope.row.scholarshipAmount > 0">
              <el-tag type="success" size="mini" style="margin-right: 4px;">助学金</el-tag>
              <span class="subsidy-amount-text">￥{{ formatAmount(scope.row.scholarshipAmount) }}</span>
            </div>
            <div v-if="scope.row.tuitionWaiverAmount > 0" style="margin-top: 4px;">
              <el-tag type="warning" size="mini" style="margin-right: 4px;">免学杂费</el-tag>
              <span class="subsidy-amount-text">￥{{ formatAmount(scope.row.tuitionWaiverAmount) }}</span>
            </div>
          </div>
          <div v-else style="display: flex; align-items: center; justify-content: center;">
            <div
              class="subsidy-add-btn-icon"
              @click="openSubsidyDialog(scope.row)"
              v-hasPermi="['system:subsidy:create']"
            >
              <img src="@/assets/images/icon/1.png" alt="录入补助" />
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" align="center" min-width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.approvalStatus != null">
            {{ selectDictLabel(dict.type.sys_approval_status, scope.row.approvalStatus) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right" class-name="small-padding fixed-width op-column">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="syncSingleStudent(scope.row.studentId)"
            v-hasPermi="['system:aidedStudent:sync']"
          >同步</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="openSubsidyDialog(scope.row)"
            v-hasPermi="['system:subsidy:create']"
          >录入补助</el-button>
        </template>
      </el-table-column>
    </el-table>
      </div>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    </div>

    <!-- 录入补助对话框 -->
    <el-dialog
      title="录入补助"
      :visible.sync="subsidyDialogVisible"
      width="600px"
      append-to-body
      @closed="handleSubsidyDialogClosed"
    >
      <el-form
        ref="subsidyFormRef"
        :model="subsidyForm"
        :rules="subsidyFormRules"
        label-width="110px"
      >
        <el-card class="student-info-card" shadow="never">
          <div slot="header" class="student-info-header">
            <i class="el-icon-user"></i>
            <span>学生信息</span>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名" :span="1">
              <span class="info-value">{{ (selectedStudent && selectedStudent.studentName) || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="证件号" :span="1">
              <span class="info-value">{{ (selectedStudent && selectedStudent.idCard) || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="年级" :span="1">
              <span class="info-value">{{ (selectedStudent && (selectedStudent.grade || selectedStudent.gradeName)) || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="班级" :span="1">
              <span class="info-value">{{ (selectedStudent && (selectedStudent.clazzName || selectedStudent.className)) || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="困难类型" :span="2">
              <el-tag type="warning" size="small" v-if="formatSupportInfo(selectedStudent)">
                {{ formatSupportInfo(selectedStudent) }}
              </el-tag>
              <span v-else class="info-value">未认定</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-form-item label="预算选择" prop="budgetIds">
          <el-select
            v-model="subsidyForm.budgetIds"
            placeholder=""
            filterable
            multiple
            style="width: 100%"
            :loading="subsidyDialogLoading"
            popper-class="budget-select-dropdown"
            @change="handleBudgetChange"
          >
            <el-option
              v-for="item in displayBudgets"
              :key="item.id"
              :label="getBudgetOptionLabel(item)"
              :value="item.id"
              :disabled="item.status != null && parseInt(item.status) !== 1"
            >
              <div style="line-height: 1.6;">
                <div style="font-weight: 500; color: #303133;">
                  <span v-if="item.budgetProjectName">{{ item.budgetProjectName }}</span>
                  <span v-else>{{ getEconomyCategoryLabel(item.economyCategory) }}</span>
                  <span style="margin-left: 8px; color: #67c23a; font-weight: 600;">
                    ￥{{ formatAmount(item.availableAmount) }}
                  </span>
                  <!-- 显示预算来源类型标签（学期下达/结余（小学）/结余（初中）） -->
                  <span
                    v-if="getBudgetSourceTypeLabel(item)"
                    :style="{
                      marginLeft: '8px',
                      padding: '2px 6px',
                      borderRadius: '2px',
                      fontSize: '11px',
                      fontWeight: '600',
                      backgroundColor: getBudgetSourceTypeColor(item),
                      color: '#fff'
                    }"
                  >
                    {{ getBudgetSourceTypeLabel(item) }}
                  </span>
                </div>
              </div>
            </el-option>
          </el-select>
          <div class="form-tip" v-if="displayBudgets.length === 0">
            <i class="el-icon-warning"></i>
            暂无可用的预算，请检查：1.是否有匹配的预算 2.预算是否有可用余额
          </div>
        </el-form-item>
        <el-form-item label="补助套餐" prop="packageId">
          <el-select
            v-model="subsidyForm.packageId"
            :placeholder="selectedBudget ? '请选择补助套餐' : '请先选择预算'"
            filterable
            style="width: 100%"
            :disabled="!selectedBudget"
            @change="handlePackageChange"
          >
            <el-option
              v-for="item in filteredSubsidyPackages"
              :key="item.id"
              :label="`${item.packageName}（￥${formatAmount(item.subsidyAmount || 0)}/学期）`"
              :value="item.id"
            />
          </el-select>
          <div class="form-tip" v-if="selectedBudget && filteredSubsidyPackages.length === 0">
            <i class="el-icon-warning"></i>
            暂无可用的补助套餐，请检查：1.是否有匹配的套餐（经济分类：{{ getEconomyCategoryLabel(selectedBudget.economyCategory) }}，学制：{{ getSchoolingPlanLabel() }}）2.套餐是否已启用
          </div>
        </el-form-item>
        <el-form-item label="补助金额">
          <div style="line-height: 32px; font-size: 16px;">
            <span v-if="currentPackage" class="subsidy-amount-display">
              {{ formatAmount(currentPackage.subsidyAmount || 0) }}元
            </span>
            <span v-else style="color: #909399;">请先选择补助套餐</span>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            type="textarea"
            v-model="subsidyForm.memo"
            placeholder="请输入备注"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSubsidy" :loading="subsidySubmitting">确 定</el-button>
        <el-button @click="subsidyDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 学生详情对话框 -->
    <el-dialog
      title="学生详情"
      :visible.sync="studentDetailDialogVisible"
      width="800px"
      append-to-body
      @closed="handleStudentDetailDialogClosed"
    >
      <div v-if="selectedStudentDetail" class="student-detail-content">
        <!-- 学生基本信息 -->
        <el-card class="detail-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-user"></i>
            <span>基本信息</span>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名" :span="1">
              <span class="info-value">{{ selectedStudentDetail.studentName || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="年级" :span="1">
              <span class="info-value">{{ selectedStudentDetail.grade || selectedStudentDetail.gradeName || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="困难类型" :span="2">
              <el-tag type="warning" size="small" v-if="formatSupportInfo(selectedStudentDetail)">
                {{ formatSupportInfo(selectedStudentDetail) }}
              </el-tag>
              <span v-else class="info-value">未认定</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 受助记录 -->
        <el-card class="detail-card" shadow="never" style="margin-top: 20px;">
          <div slot="header" class="card-header">
            <i class="el-icon-document"></i>
            <span>受助记录（已审核通过）</span>
          </div>
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
                <span v-if="scope.row.approvalStatus != null">
                  {{ selectDictLabel(dict.type.sys_approval_status, scope.row.approvalStatus) }}
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
                {{ selectDictLabel(dict.type.sys_payment_status, scope.row.paymentStatus) || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="备注" align="center" prop="memo" show-overflow-tooltip />
          </el-table>
          <div v-if="!subsidyRecordsLoading && subsidyRecords.length === 0" class="empty-tip">
            <i class="el-icon-info"></i>
            <span>暂无已审核通过的受助记录</span>
          </div>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="studentDetailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 批量发放对话框 -->
    <el-dialog
      title="批量发放补助"
      :visible.sync="batchPaymentDialogVisible"
      width="1200px"
      append-to-body
      @opened="handleBatchPaymentDialogOpened"
    >
      <el-tabs v-model="batchPaymentActiveTab" type="card">
        <!-- 第一步：筛选学生 -->
        <el-tab-pane label="1. 筛选学生" name="select">
          <el-form
            ref="batchPaymentFilterFormRef"
            :model="batchPaymentFilterForm"
            label-width="80px"
            size="small"
            style="margin-top: 10px;"
          >
            <el-row :gutter="12">
              <el-col :span="5">
                <el-form-item label="学年学期">
                  <el-input
                    :value="batchPaymentFilterForm.academicYear && batchPaymentFilterForm.semesterLabel ? `${batchPaymentFilterForm.academicYear} ${batchPaymentFilterForm.semesterLabel}` : '未设置'"
                    disabled
                    size="small"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="学段">
                  <el-select
                    v-model="batchPaymentFilterForm.schoolingPlanId"
                    placeholder="选择学段（可选）"
                    clearable
                    size="small"
                    style="width: 100%"
                    @change="handleFilterSchoolingPlanChange"
                  >
                    <el-option
                      v-for="plan in schoolingPlanOptions"
                      :key="plan.id"
                      :label="plan.name"
                      :value="plan.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="年级">
                  <el-select
                    v-model="batchPaymentFilterForm.gradeId"
                    placeholder="选择年级（可选）"
                    clearable
                    size="small"
                    style="width: 100%"
                    :disabled="!batchPaymentFilterForm.schoolingPlanId"
                    @change="handleFilterGradeChange"
                  >
                    <el-option
                      v-for="grade in gradeOptions"
                      :key="grade.id"
                      :label="grade.name"
                      :value="grade.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="班级">
                  <el-select
                    v-model="batchPaymentFilterForm.classId"
                    placeholder="选择班级（可选）"
                    clearable
                    size="small"
                    style="width: 100%"
                    :disabled="!batchPaymentFilterForm.gradeId"
                  >
                    <el-option
                      v-for="clazz in classOptions"
                      :key="clazz.classId"
                      :label="clazz.className"
                      :value="clazz.classId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4" style="text-align: right; padding-top: 5px;">
                <el-button
                  type="primary"
                  icon="el-icon-search"
                  size="small"
                  @click="handleQueryStudents"
                  :loading="batchPaymentQueryLoading"
                >
                  查询
                </el-button>
                <el-button
                  icon="el-icon-refresh"
                  size="small"
                  @click="handleResetStudentFilter"
                >
                  重置
                </el-button>
              </el-col>
            </el-row>
          </el-form>

          <!-- 查询结果列表 -->
          <div style="margin-top: 24px;">
            <!-- 结果统计和操作栏 -->
            <el-card shadow="never" style="border: 1px solid #e4e7ed; margin-bottom: 16px;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <div style="display: flex; align-items: center; gap: 12px;">
                  <i class="el-icon-user-solid" style="font-size: 18px; color: #409EFF;"></i>
                  <span style="font-size: 14px; color: #606266;">
                    查询结果：共
                    <strong style="color: #409EFF; font-size: 16px; margin: 0 4px;">{{ batchPaymentStudentList.length }}</strong>
                    名学生
                  </span>
                  <span v-if="batchPaymentSelectedStudentIds.length > 0">
                    已选择 {{ batchPaymentSelectedStudentIds.length }} 名
                  </span>
                </div>
                <div style="display: flex; gap: 8px;">
                  <el-button
                    size="small"
                    icon="el-icon-check"
                    @click="handleSelectAllStudents"
                    :disabled="batchPaymentStudentList.length === 0"
                  >
                    全选
                  </el-button>
                  <el-button
                    size="small"
                    icon="el-icon-close"
                    @click="handleClearStudentSelection"
                    :disabled="batchPaymentSelectedStudentIds.length === 0"
                  >
                    取消全选
                  </el-button>
                  <el-button
                    type="primary"
                    size="small"
                    icon="el-icon-plus"
                    @click="handleAddSelectedStudents"
                    :disabled="batchPaymentSelectedStudentIds.length === 0"
                  >
                    添加选中学生
                    <span v-if="batchPaymentSelectedStudentIds.length > 0" style="margin-left: 4px;">
                      ({{ batchPaymentSelectedStudentIds.length }})
                    </span>
                  </el-button>
                </div>
              </div>
            </el-card>

            <!-- 学生列表表格 -->
            <el-card shadow="never" style="border: 1px solid #e4e7ed;">
              <el-empty
                v-if="batchPaymentStudentList.length === 0 && !batchPaymentQueryLoading"
                description="暂无学生数据，请先查询学生"
                :image-size="100"
              />
              <el-table
                v-else
                ref="batchPaymentStudentTable"
                :data="batchPaymentStudentList"
                v-loading="batchPaymentQueryLoading"
                border
                stripe
                size="medium"
                max-height="450"
                @selection-change="handleBatchPaymentStudentSelectionChange"
                :header-cell-style="{
                  background: '#f5f7fa',
                  color: '#606266',
                  fontWeight: '600',
                  fontSize: '13px'
                }"
                style="width: 100%"
              >
                <el-table-column type="selection" width="55" align="center" fixed="left" />
                <el-table-column
                  type="index"
                  label="序号"
                  width="70"
                  align="center"
                  :index="index => index + 1"
                />
                <el-table-column label="学生姓名" prop="studentName" width="130" align="center" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <div style="display: flex; align-items: center; justify-content: center;">
                      <i class="el-icon-user" style="color: #909399; margin-right: 6px; font-size: 14px;"></i>
                      <span style="font-weight: 500; color: #303133;">{{ scope.row.studentName || '-' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="学籍号" prop="studentNo" width="160" align="center" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <span style="color: #606266; font-family: 'Courier New', monospace;">
                      {{ scope.row.studentNo || '-' }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column label="年级班级" width="180" align="center">
                  <template slot-scope="scope">
                    <div style="display: flex; align-items: center; justify-content: center; gap: 6px;">
                      <i class="el-icon-school" style="color: #409EFF; font-size: 14px;"></i>
                      <span style="color: #606266;">{{ formatGradeAndClass(scope.row) || '-' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="困难类型" width="140" align="center">
                  <template slot-scope="scope">
                    <span v-if="scope.row.difficultyTypeId">
                      {{ formatSupportInfo(scope.row) }}
                    </span>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="困难等级" width="120" align="center">
                  <template slot-scope="scope">
                    <span v-if="scope.row.difficultyLevelId">
                      {{ formatDifficultyLevel(scope.row.difficultyLevelId) }}
                    </span>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column label="身份证号" prop="idCard" width="180" align="center" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <span style="color: #909399; font-family: 'Courier New', monospace; font-size: 12px;">
                      {{ scope.row.idCard || '-' }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 第二步：录入信息 -->
        <el-tab-pane label="2. 录入信息" name="input" :disabled="batchPaymentForm.studentPayments.length === 0">
          <el-form
            ref="batchPaymentFormRef"
            :model="batchPaymentForm"
            :rules="batchPaymentRules"
            label-width="110px"
            style="margin-top: 20px;"
          >
        <el-form-item label="预算选择" prop="budgetIds">
          <el-select
            v-model="batchPaymentForm.budgetIds"
            placeholder="请选择预算（可多选，单选时只从该预算支出，多选时使用组合逻辑）"
            filterable
            multiple
            style="width: 100%"
            :loading="batchPaymentDialogLoading"
            popper-class="budget-select-dropdown"
            @change="handleBatchPaymentBudgetChange"
          >
            <el-option
              v-for="item in batchPaymentDisplayBudgets"
              :key="item.id"
              :label="getBudgetOptionLabel(item)"
              :value="item.id"
              :disabled="item.status != null && parseInt(item.status) !== 1"
            >
              <div style="line-height: 1.6;">
                <div style="font-weight: 500; color: #303133;">
                  <span v-if="item.budgetProjectName">{{ item.budgetProjectName }}</span>
                  <span v-else>{{ getEconomyCategoryLabel(item.economyCategory) }}</span>
                  <span style="margin-left: 8px; color: #67c23a; font-weight: 600;">
                    可用￥{{ formatAmount(item.availableAmount) }}
                  </span>
                  <!-- 显示预算来源类型标签（学期下达/结余（小学）/结余（初中）） -->
                  <span
                    v-if="getBudgetSourceTypeLabel(item)"
                    :style="{
                      marginLeft: '8px',
                      padding: '2px 6px',
                      borderRadius: '2px',
                      fontSize: '11px',
                      fontWeight: '600',
                      backgroundColor: getBudgetSourceTypeColor(item),
                      color: '#fff'
                    }"
                  >
                    {{ getBudgetSourceTypeLabel(item) }}
                  </span>
                </div>
              </div>
            </el-option>
          </el-select>
          <div class="form-tip" v-if="batchPaymentDisplayBudgets && batchPaymentDisplayBudgets.length === 0">
            <i class="el-icon-warning"></i>
            暂无可用的预算，请检查：1.是否有匹配的预算 2.预算是否有可用余额
          </div>
        </el-form-item>
        <el-form-item label="发放日期" prop="paymentDate">
          <el-date-picker
            v-model="batchPaymentForm.paymentDate"
            type="date"
            placeholder="选择发放日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="发放人" prop="paymentPerson">
          <el-input
            v-model="batchPaymentForm.paymentPerson"
            placeholder="请输入发放人姓名"
            maxlength="50"
          />
        </el-form-item>
        <el-form-item label="学生列表" prop="studentPayments">
          <!-- 统一录入区域 -->
          <el-card shadow="never" style="margin-bottom: 16px; border: 1px solid #e4e7ed;">
            <div slot="header" style="display: flex; align-items: center; justify-content: space-between;">
              <span style="font-weight: 600; color: #303133;">
                <i class="el-icon-setting" style="margin-right: 6px; color: #409EFF;"></i>
                统一录入（批量设置）
              </span>
              <el-button
                type="primary"
                size="small"
                icon="el-icon-check"
                :disabled="!batchPaymentSelectedBudget || batchPaymentForm.studentPayments.length === 0"
                @click="applyUniformPaymentToAll"
              >
                应用到全部
              </el-button>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <div style="margin-bottom: 12px;">
                  <label style="display: block; margin-bottom: 6px; font-size: 13px; color: #606266; font-weight: 500;">
                    补助套餐
                  </label>
                  <el-select
                    v-model="uniformPaymentPackageId"
                    placeholder="选择套餐（可选）"
                    filterable
                    clearable
                    style="width: 100%"
                    :disabled="!batchPaymentSelectedBudget"
                    @change="handleUniformPackageChange"
                  >
                    <el-option
                      v-for="pkg in uniformPaymentPackages"
                      :key="pkg.id"
                      :label="`${pkg.packageName}（￥${formatAmount(pkg.subsidyAmount || 0)}/学期）`"
                      :value="pkg.id"
                    />
                  </el-select>
                </div>
              </el-col>
              <el-col :span="12">
                <div style="margin-bottom: 12px;">
                  <label style="display: block; margin-bottom: 6px; font-size: 13px; color: #606266; font-weight: 500;">
                    发放金额
                  </label>
                  <el-input-number
                    v-model="uniformPaymentAmount"
                    :min="0"
                    :precision="2"
                    :step="100"
                    placeholder="输入金额"
                    style="width: 100%"
                    controls-position="right"
                  />
                </div>
              </el-col>
            </el-row>
            <div style="font-size: 12px; color: #909399; margin-top: 8px;">
              <i class="el-icon-info" style="margin-right: 4px;"></i>
              选择套餐和金额后，点击"应用到全部"按钮，将为所有学生统一设置相同的补助信息
            </div>
          </el-card>

          <div style="margin-bottom: 8px; padding: 8px 12px; background-color: #f5f7fa; border-radius: 4px;">
            <span style="font-size: 13px; color: #606266;">
              <i class="el-icon-info" style="margin-right: 4px; color: #409EFF;"></i>
              已选择 <strong style="color: #303133;">{{ batchPaymentForm.studentPayments.length }}</strong> 名学生，
              <span v-if="batchPaymentSelectedBudget" style="margin-left: 8px;">
                经济分类：<strong style="color: #409EFF; font-size: 14px;">{{ getEconomyCategoryLabel(batchPaymentSelectedBudget.economyCategory) }}</strong>
              </span>
            </span>
          </div>
          <el-table
            :data="batchPaymentForm.studentPayments"
            border
            stripe
            size="small"
            max-height="400"
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
          >
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="学生姓名" prop="studentName" width="120" align="center" show-overflow-tooltip>
              <template slot-scope="scope">
                <span style="font-weight: 500; color: #303133;">{{ scope.row.studentName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="学籍号" prop="studentNo" width="150" align="center" show-overflow-tooltip>
              <template slot-scope="scope">
                <span style="color: #606266;">{{ scope.row.studentNo }}</span>
              </template>
            </el-table-column>
            <el-table-column label="学年学期" width="140" align="center">
              <template slot-scope="scope">
                <span style="color: #909399; font-size: 12px;">
                  {{ scope.row.academicYear || '' }} {{ scope.row.semesterLabel || '' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="补助套餐" min-width="200" align="center">
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.packageId"
                  placeholder="请选择套餐"
                  filterable
                  size="small"
                  style="width: 100%"
                  :disabled="!batchPaymentSelectedBudget"
                  @change="handleBatchPaymentPackageChange(scope.row, scope.$index)"
                >
                  <el-option
                    v-for="pkg in getFilteredPackagesForStudent(scope.row)"
                    :key="pkg.id"
                    :label="`${pkg.packageName}（￥${formatAmount(pkg.subsidyAmount || 0)}/学期）`"
                    :value="pkg.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="发放金额" min-width="150" align="center">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.paymentAmount"
                  :min="0"
                  :precision="2"
                  :step="100"
                  placeholder="选择套餐或输入金额"
                  style="width: 100%"
                  size="small"
                  controls-position="right"
                  @change="handlePaymentAmountChange(scope.row)"
                />
                <div v-if="scope.row.selectedPackage" style="font-size: 11px; color: #909399; margin-top: 4px;">
                  套餐金额：￥{{ formatAmount(scope.row.selectedPackage.subsidyAmount || 0) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="mini"
                  icon="el-icon-delete"
                  style="color: #F56C6C;"
                  @click="removeStudentPayment(scope.$index)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="batchPaymentForm.memo"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button @click="batchPaymentDialogVisible = false">取 消</el-button>
        <el-button
          v-if="batchPaymentActiveTab === 'select'"
          type="primary"
          @click="batchPaymentActiveTab = 'input'"
          :disabled="batchPaymentForm.studentPayments.length === 0"
        >
          下一步
        </el-button>
        <el-button
          v-else
          type="primary"
          @click="submitBatchPayment"
          :loading="batchPaying"
        >
          确 定
        </el-button>
      </div>
    </el-dialog>

    <!-- Excel导入对话框 -->
    <el-dialog
      title="Excel导入批量发放"
      :visible.sync="importDialogVisible"
      width="600px"
      append-to-body
      class="excel-import-dialog"
      :close-on-click-modal="false"
    >
      <div class="import-dialog-content">
        <!-- 文件上传区域 -->
        <div class="upload-section">
          <el-upload
            ref="upload"
            :limit="1"
            accept=".xlsx, .xls"
            :headers="upload.headers"
            :action="upload.url"
            :file-list="upload.fileList"
            :disabled="upload.isUploading"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :on-change="handleFileChange"
            :auto-upload="false"
            drag
            class="excel-upload"
          >
            <div class="upload-area">
              <div class="upload-icon-wrapper">
                <i class="el-icon-upload2 upload-icon"></i>
                <div class="upload-progress" v-if="upload.isUploading">
                  <el-progress :percentage="upload.percent" :status="upload.percent === 100 ? 'success' : ''"></el-progress>
                </div>
              </div>
              <div class="upload-text">
                <div class="upload-main-text">将文件拖到此处，或<em class="upload-link">点击上传</em></div>
                <div class="upload-tip-text">支持 .xlsx 和 .xls 格式</div>
              </div>
            </div>
          </el-upload>
        </div>

        <!-- 操作选项 -->
        <div class="import-options">
          <el-checkbox v-model="upload.updateSupport">是否更新已经存在的记录</el-checkbox>
          <el-button
            type="text"
            icon="el-icon-download"
            @click="handleDownloadTemplate"
            class="download-template-btn"
          >
            下载模板
          </el-button>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="importDialogVisible = false" size="medium">取 消</el-button>
        <el-button
          type="primary"
          size="medium"
          icon="el-icon-upload2"
          @click="submitFileForm"
          :loading="upload.isUploading"
          :disabled="!upload.fileList || upload.fileList.length === 0"
        >
          {{ upload.isUploading ? '上传中...' : '开始导入' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 批量发放/导入结果对话框 -->
    <el-dialog
      :title="importResultDialogTitle"
      :visible.sync="importResultDialogVisible"
      width="900px"
      append-to-body
      class="import-result-dialog"
      :close-on-click-modal="false"
    >
      <!-- 统计卡片 -->
      <div class="import-stats-container">
        <div class="stat-card stat-total">
          <div class="stat-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ importResult.totalCount || 0 }}</div>
            <div class="stat-label">总计</div>
          </div>
        </div>
        <div class="stat-card stat-success">
          <div class="stat-icon">
            <i class="el-icon-success"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ importResult.successCount || 0 }}</div>
            <div class="stat-label">成功</div>
          </div>
        </div>
        <div class="stat-card stat-failure" v-if="importResult.failureCount > 0">
          <div class="stat-icon">
            <i class="el-icon-error"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ importResult.failureCount || 0 }}</div>
            <div class="stat-label">失败</div>
          </div>
        </div>
      </div>

      <!-- 结果提示 -->
      <div class="import-alert-container">
        <el-alert
          :title="importResult.failureCount > 0
            ? `${importResultDialogTitle}完成！成功：${importResult.successCount} 人，失败：${importResult.failureCount} 人，总计：${importResult.totalCount} 人`
            : `${importResult.successCount} 人录入成功，待审核！`"
          :type="importResult.failureCount > 0 ? 'warning' : 'success'"
          :closable="false"
          show-icon
          :effect="importResult.failureCount > 0 ? 'dark' : 'light'"
        />
      </div>

      <!-- 错误列表 -->
      <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list-container">
        <div class="error-list-header">
          <i class="el-icon-warning-outline"></i>
          <span>失败记录详情（共 {{ importResult.errors.length }} 条）</span>
        </div>
        <el-table
          :data="importResult.errors"
          border
          stripe
          max-height="350"
          class="error-table"
          :row-class-name="getErrorRowClassName"
        >
          <el-table-column
            v-if="importResult.errors[0] && importResult.errors[0].rowNum !== undefined"
            label="行号"
            prop="rowNum"
            width="80"
            align="center"
            fixed="left"
          >
            <template slot-scope="scope">
              <el-tag type="info" size="small">{{ scope.row.rowNum }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            v-if="importResult.errors[0] && (importResult.errors[0].studentName || importResult.errors[0].name)"
            label="学生姓名"
            prop="studentName"
            :prop="importResult.errors[0].name ? 'name' : 'studentName'"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <span class="student-name">{{ scope.row.studentName || scope.row.name || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            v-if="importResult.errors[0] && (importResult.errors[0].studentNo || importResult.errors[0].studentNo)"
            label="学籍号"
            prop="studentNo"
            width="160"
            align="center"
          >
            <template slot-scope="scope">
              <span class="student-no">{{ scope.row.studentNo || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            label="错误信息"
            prop="errorMessage"
            :prop="importResult.errors[0].errorMsg ? 'errorMsg' : 'errorMessage'"
            min-width="300"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <div class="error-message">
                <i class="el-icon-warning error-icon"></i>
                <span>{{ scope.row.errorMessage || scope.row.errorMsg || '-' }}</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 成功提示（无错误时显示） -->
      <div v-else class="success-container">
        <div class="success-icon">
          <i class="el-icon-success"></i>
        </div>
        <div class="success-message">所有记录已成功录入，等待审核！</div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          size="medium"
          icon="el-icon-check"
          @click="importResultDialogVisible = false; getList()"
        >
          确 定
        </el-button>
        <el-button
          v-if="importResult.errors && importResult.errors.length > 0"
          size="medium"
          icon="el-icon-download"
          @click="exportErrorList"
        >
          导出错误列表
        </el-button>
      </div>
    </el-dialog>

    <!-- 同步进度对话框 -->
    <el-dialog
      title="同步进度"
      :visible.sync="syncProgressDialogVisible"
      width="600px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="syncProgress.status === 'completed' || syncProgress.status === 'failed'"
      append-to-body
    >
      <div class="sync-progress-container">
        <!-- 进度条 -->
        <div class="progress-section">
          <el-progress
            :percentage="syncProgress.percentage || 0"
            :status="getProgressStatus()"
            :stroke-width="24"
          >
            <template slot="default" slot-scope="{ percentage }">
              <span style="font-size: 14px; font-weight: 600; color: #303133;">
                {{ percentage }}%
              </span>
            </template>
          </el-progress>
        </div>

        <!-- 状态信息 -->
        <div class="status-section">
          <div class="status-item">
            <i class="el-icon-info" style="color: #409EFF; margin-right: 8px;"></i>
            <span class="status-label">状态：</span>
            <el-tag
              :type="getStatusTagType()"
              size="small"
              effect="plain"
            >
              {{ getStatusText() }}
            </el-tag>
          </div>
          <div class="status-item" v-if="syncProgress.message">
            <i class="el-icon-document" style="color: #67C23A; margin-right: 8px;"></i>
            <span class="status-label">当前操作：</span>
            <span class="status-value">{{ syncProgress.message }}</span>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-section">
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-icon" style="background-color: #409EFF;">
                  <i class="el-icon-document"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ syncProgress.total || 0 }}</div>
                  <div class="stat-label">总数</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-icon" style="background-color: #67C23A;">
                  <i class="el-icon-success"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ syncProgress.success || 0 }}</div>
                  <div class="stat-label">成功</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card">
                <div class="stat-icon" style="background-color: #F56C6C;">
                  <i class="el-icon-error"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ syncProgress.failed || 0 }}</div>
                  <div class="stat-label">失败</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 耗时信息 -->
        <div class="time-section" v-if="syncProgress.startTime">
          <div class="time-item">
            <span class="time-label">开始时间：</span>
            <span class="time-value">{{ formatTime(syncProgress.startTime) }}</span>
          </div>
          <div class="time-item" v-if="syncProgress.endTime">
            <span class="time-label">结束时间：</span>
            <span class="time-value">{{ formatTime(syncProgress.endTime) }}</span>
          </div>
          <div class="time-item" v-if="syncProgress.endTime && syncProgress.startTime">
            <span class="time-label">耗时：</span>
            <span class="time-value">{{ calculateDuration() }}</span>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button
          v-if="syncProgress.status === 'completed' || syncProgress.status === 'failed'"
          type="primary"
          @click="closeSyncProgressDialog"
        >
          关 闭
        </el-button>
        <el-button
          v-else
          type="info"
          plain
          disabled
        >
          同步中，请稍候...
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listAidedStudentInfo, syncStudentToAidedTable, syncAllStudentsToAidedTable, getSyncProgress } from "@/api/system/aidedStudentInfo";
import { getCurrentYearSemester } from "@/api/system/yearSemester";
import { getSubsidyPackages, getAvailableBudgets, getHistoricalBudgets, createSubsidy } from "@/api/system/subsidy";
import { getStudents, getSchoolPlanList, getGradeList, getClassList } from "@/api/system/students";
import { listSubsidyRecord, batchPayment, importTemplate } from "@/api/system/subsidyRecord";
import { getToken } from '@/utils/auth';
import { download } from '@/utils/request';

export default {
  name: "AidedStudent",
  dicts: ['sys_student_gender', 'sys_difficulty_type', 'sys_difficulty_level', 'sys_quota_source_type', 'sys_economy_category', 'sys_function_category', 'sys_approval_status', 'sys_payment_status', 'sys_budget_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      queryFormCollapsed: false, // 查询表单折叠状态
      // 总条数
      total: 0,
      // 受助学生信息表格数据
      aidedStudentList: [],
      // 当前学期信息
      currentSemester: null,
      semesterOptions: [
        { value: '1', label: '秋季学期' },
        { value: '2', label: '春季学期' }
      ],
      // 补助录入
      subsidyDialogVisible: false,
      subsidyDialogLoading: false,
      subsidySubmitting: false,
      selectedStudent: null,
      subsidyPackages: [], // 所有套餐（根据学制加载）
      filteredSubsidyPackages: [], // 过滤后的套餐（根据预算经济分类过滤）
      currentPackage: null,
      selectedBudget: null, // 当前选中的预算
      availableBudgets: [],
      historicalBudgets: [],
      budgetSourceOptions: [
        { label: '当前学期预算', value: 'CURRENT' },
        { label: '历史结余', value: 'HISTORY' }
      ],
      subsidyForm: {
        studentSemesterRecordIds: [], // 向后兼容旧版本
        studentInfos: [], // 新版本：学生信息列表
        packageId: null,
        budgetId: null, // 向后兼容
        budgetIds: [], // 新版本：支持多选
        sourceSemesterId: null,
        memo: ''
      },
      subsidyFormRules: {
        packageId: [{ required: true, message: '请选择补助套餐', trigger: 'change' }],
        budgetIds: [{ required: true, message: '请至少选择一个预算', trigger: 'change', type: 'array', min: 1 }]
      },
      studentPlanCache: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentName: null,
        idCard: null,
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        academicYear: null,
        semester: null
      },
      // 学生详情对话框
      studentDetailDialogVisible: false,
      selectedStudentDetail: null,
      subsidyRecords: [],
      subsidyRecordsLoading: false,
      // 批量发放相关
      selectedStudents: [],
      batchPaymentDialogVisible: false,
      batchPaying: false,
      batchPaymentDialogLoading: false,
      batchPaymentDisplayBudgets: [],
      batchPaymentSelectedBudget: null,
      batchPaymentActiveTab: 'select', // 当前激活的标签页
      batchPaymentQueryLoading: false, // 查询学生加载状态
      batchPaymentStudentList: [], // 查询到的学生列表
      batchPaymentSelectedStudentIds: [], // 选中的学生ID列表
      batchPaymentFilterForm: {
        academicYear: null,
        semester: null,
        semesterLabel: null,
        schoolingPlanId: null,
        gradeId: null,
        classId: null
      },
      schoolingPlanOptions: [], // 学段选项
      gradeOptions: [], // 年级选项
      classOptions: [], // 班级选项
      academicYearOptions: [], // 学年选项
      batchPaymentForm: {
        budgetId: null, // 向后兼容
        budgetIds: [], // 新版本：支持多选
        sourceSemesterId: null,
        paymentDate: '',
        paymentPerson: '',
        memo: '',
        studentPayments: [] // 学生发放列表，包含学生信息和金额
      },
      // 统一录入相关
      uniformPaymentPackageId: null, // 统一录入的套餐ID
      uniformPaymentAmount: null, // 统一录入的金额
      uniformPaymentPackages: [], // 统一录入可用的套餐列表（所有学生共有的套餐）
      batchPaymentRules: {
        budgetIds: [
          {
            required: true,
            message: '请至少选择一个预算',
            trigger: ['change', 'blur'],
            type: 'array',
            min: 1,
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                callback(new Error('请至少选择一个预算'));
              } else {
                callback();
              }
            }
          }
        ],
        paymentDate: [
          { required: true, message: '发放日期不能为空', trigger: 'change' }
        ],
        paymentPerson: [
          { required: true, message: '发放人不能为空', trigger: 'blur' }
        ],
        studentPayments: [
          { required: true, message: '请至少添加一名学生', trigger: 'change' }
        ]
      },
      // Excel导入相关
      importDialogVisible: false,
      importResultDialogVisible: false,
      importResultDialogTitle: '批量发放结果',
      importResult: {
        successCount: 0,
        failureCount: 0,
        totalCount: 0,
        errors: []
      },
      upload: {
        open: false,
        title: '',
        isUploading: false,
        percent: 0,
        updateSupport: 0,
        headers: {},
        url: '',
        fileList: []
      },
      // 同步进度相关
      syncProgressDialogVisible: false,
      syncProgressTimer: null, // 轮询定时器
      syncProgress: {
        total: 0,
        processed: 0,
        success: 0,
        failed: 0,
        percentage: 0,
        status: 'not_started', // not_started, running, completed, failed
        message: '',
        startTime: null,
        endTime: null
      }
    };
  },
  computed: {
    currentSemesterDisplay() {
      if (!this.currentSemester) {
        return '未设置';
      }
      const year = this.currentSemester.schoolYear || '';
      const semesterLabel = this.currentSemester.semesterLabel || '';
      return `${year} ${semesterLabel}`.trim();
    },
    displayBudgets() {
      // 统一展示可用的预算：当前学期新下达、结转预算以及历史学期预算
      // 后端已经通过 isHistorical 字段标记，不需要前端再添加标记
      const currentBudgets = Array.isArray(this.availableBudgets) ? this.availableBudgets : []
      const historyBudgets = Array.isArray(this.historicalBudgets) ? this.historicalBudgets : []
      return [...currentBudgets, ...historyBudgets]
    }
  },
  created() {
    this.initializePage();
  },
  methods: {
    /** 初始化页面，拉取当前学期并查询受助数据 */
    initializePage() {
      this.loadCurrentSemester().then(() => {
        this.getList();
      });
    },
    /** 加载当前学年学期 */
    loadCurrentSemester() {
      return getCurrentYearSemester()
        .then(response => {
          const data = response.data;
          if (data) {
            this.currentSemester = data;
            this.applyCurrentSemesterToFilters(true);
          } else {
            this.currentSemester = null;
            this.$modal.msgWarning("尚未设置当前学年学期，请先在学年学期管理中设置。");
          }
        })
        .catch(() => {
          this.currentSemester = null;
          this.$modal.msgError("获取当前学年学期失败，请稍后重试。");
        });
    },
    /** 将当前学期默认写入查询条件 */
    applyCurrentSemesterToFilters(force = false) {
      if (!this.currentSemester) {
        return;
      }
      if (force || !this.queryParams.academicYear) {
        this.queryParams.academicYear = this.currentSemester.schoolYear || null;
      }
      if (force || !this.queryParams.semester) {
        this.queryParams.semester = this.currentSemester.semester != null ? String(this.currentSemester.semester) : null;
      }
    },
    /** 确认当前学期是否可用 */
    ensureCurrentSemesterReady() {
      if (!this.currentSemester || !this.currentSemester.schoolYear || this.currentSemester.semester == null) {
        this.$modal.msgWarning("请先在学年学期管理中设置当前学年学期");
        return false;
      }
      return true;
    },
    /** 获取同步操作需要的学年学期参数 */
    getSyncParams() {
      if (!this.ensureCurrentSemesterReady()) {
        return null;
      }
      return {
        academicYear: this.currentSemester.schoolYear,
        semester: String(this.currentSemester.semester)
      };
    },
    /** 查询受助学生信息列表 */
    getList() {
      if (!this.queryParams.academicYear || !this.queryParams.semester) {
        this.applyCurrentSemesterToFilters();
      }
      if (!this.queryParams.academicYear || !this.queryParams.semester) {
        this.loading = false;
        return;
      }
      this.loading = true;
      listAidedStudentInfo(this.queryParams).then(response => {
        this.aidedStudentList = response.rows;
        this.total = response.total;
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.applyCurrentSemesterToFilters(true);
      this.handleQuery();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/aidedStudentInfo/export', {
        ...this.queryParams
      }, `aidedStudentInfo_${new Date().getTime()}.xlsx`)
    },
    /** 同步学生数据 */
    handleSync() {
      const params = this.getSyncParams();
      if (!params) {
        return;
      }
      this.$modal.confirm('是否确认同步所有学生数据？').then(() => {
        // 初始化进度数据
        this.syncProgress = {
          total: 0,
          processed: 0,
          success: 0,
          failed: 0,
          percentage: 0,
          status: 'running',
          message: '开始同步...',
          startTime: Date.now(),
          endTime: null
        };
        // 显示进度对话框
        this.syncProgressDialogVisible = true;
        
        // 发起同步请求
        syncAllStudentsToAidedTable(params.academicYear, params.semester).then(response => {
          // 同步请求已发起，开始轮询进度
          this.startProgressPolling(params.academicYear, params.semester);
        }).catch(error => {
          // 如果是分布式锁错误，尝试查询进度
          if (error.msg && error.msg.indexOf('正在进行') !== -1) {
            this.startProgressPolling(params.academicYear, params.semester);
          } else {
            this.syncProgress.status = 'failed';
            this.syncProgress.message = error.msg || error.message || '同步失败';
            this.$notify({
              title: '同步失败',
              message: this.syncProgress.message,
              type: 'error',
              duration: 4500
            });
          }
        });
      });
    },
    /** 同步单个学生数据 */
    syncSingleStudent(studentId) {
      const params = this.getSyncParams();
      if (!params) {
        return;
      }
      syncStudentToAidedTable(studentId, params.academicYear, params.semester).then(response => {
        this.getList();
        // 使用Notification显示同步结果
        if (response.data && response.data.insertedCount !== undefined) {
          const result = response.data;
          let message = '';
          if (result.insertedCount > 0) {
            message = `同步完成，已同步${result.insertedCount}人！`;
          } else if (result.updatedCount > 0) {
            message = `同步完成，已更新${result.updatedCount}人！`;
          } else {
            message = '同步完成，无数据变更！';
          }
          this.$notify({
            title: '同步成功',
            message: message,
            type: 'success',
            duration: 4500
          });
        } else {
          // 兼容旧格式
          this.$notify({
            title: '同步成功',
            message: response.msg || '同步成功',
            type: 'success',
            duration: 4500
          });
        }
      }).catch(error => {
        console.error(error);
        this.$notify({
          title: '同步失败',
          message: error.msg || error.message || '同步失败',
          type: 'error',
          duration: 4500
        });
      });
    },
    formatSupportInfo(row) {
      if (!row) {
        return '';
      }
      const difficultyLabel = this.selectDictLabel(this.dict.type.sys_difficulty_type, row.difficultyTypeId);
      if (!difficultyLabel || difficultyLabel === '无') {
        return '';
      }
      // 如果困难类型是脱贫户类型，且存在脱贫年份，则在前面加上年份
      if (row.difficultyTypeId && this.isPovertyReliefDifficultyType(row.difficultyTypeId) && row.povertyReliefYear) {
        return `${row.povertyReliefYear}年${difficultyLabel}`;
      }
      // 否则直接显示困难类型标签
      return difficultyLabel;
    },
    /** 判断困难类型是否是脱贫户类型 */
    isPovertyReliefDifficultyType(difficultyTypeId) {
      if (!difficultyTypeId) {
        return false;
      }
      // 从字典中查找对应的困难类型
      const difficultyType = this.dict.type.sys_difficulty_type.find(dict => dict.value === difficultyTypeId);
      if (!difficultyType) {
        return false;
      }
      // 判断标签或值中是否包含"脱贫"关键字
      const label = difficultyType.label || '';
      const value = difficultyType.value || '';
      return label.includes('脱贫') || value.includes('poverty') || value.includes('脱贫');
    },
    formatGradeAndClass(row) {
      if (!row) {
        return '-';
      }
      const grade = row.grade || '';
      const clazz = row.clazzName || '';
      const combined = `${grade}${clazz}`;
      return combined || '-';
    },
    formatAcademicYearAndSemester(row) {
      if (!row) {
        return '-';
      }
      const year = row.academicYear || '';
      const semester = row.semesterLabel || '';
      const combined = `${year}${semester ? ` ${semester}` : ''}`.trim();
      return combined || '-';
    },
    /** 格式化学年学期（用于补助记录，支持 schoolYear 和 academicYear） */
    formatAcademicYearAndSemesterForSubsidyRecord(row) {
      if (!row) {
        return '-';
      }
      // 优先使用 schoolYear（后端返回的字段），如果没有则使用 academicYear
      const year = row.schoolYear || row.academicYear || '';
      const semester = row.semesterLabel || '';
      const combined = `${year}${semester ? ` ${semester}` : ''}`.trim();
      return combined || '-';
    },
    formatDifficultyLevel(value) {
      return this.selectDictLabel(this.dict.type.sys_difficulty_level, value) || value || '-';
    },
    /** 获取学期记录ID */
    resolveSemesterRecordId(row) {
      const info = row || {};
      return info.studentSemesterRecordId
        || info.semesterRecordId
        || info.studentSemesterId
        || info.studentRecordId
        || info.id;
    },
    /** 打开补助录入弹窗 */
    async openSubsidyDialog(row) {
      // 使用新版本：传递学生信息而不是学期记录ID
      if (!row.studentId || !row.academicYear || !row.semester) {
        this.$modal.msgWarning("当前数据缺少学生信息（studentId、academicYear、semester），无法录入补助");
        return;
      }
      if (!this.ensureCurrentSemesterReady()) {
        return;
      }
      // 学制信息由后端在受助学生列表查询时直接返回
      const schoolingPlanId = row.schoolingPlanId;
      if (!schoolingPlanId) {
        this.$modal.msgWarning("缺少学制信息，无法匹配补助套餐");
        return;
      }
      this.selectedStudent = row;
      this.resetSubsidyForm(false);
      // 使用新版本：传递学生信息
      this.subsidyForm.studentInfos = [{
        studentId: row.studentId,
        academicYear: row.academicYear,
        semester: row.semester
      }];
      this.subsidyForm.sourceSemesterId = (this.currentSemester && this.currentSemester.id) || null;
      this.subsidyDialogVisible = true;
      // 先加载所有套餐（根据学制），后端已根据学制/经济分类等规则进行控制
      this.fetchSubsidyPackages(schoolingPlanId);
      // 加载预算列表（由后端根据功能分类等规则控制）
      this.loadBudgets();
    },
    /** 重置补助表单 */
    resetSubsidyForm(clearSelection = true) {
      if (this.$refs.subsidyFormRef) {
        this.$refs.subsidyFormRef.resetFields();
      }
      this.subsidyForm = {
        studentSemesterRecordIds: [], // 向后兼容旧版本
        studentInfos: [], // 新版本：学生信息列表
        packageId: null,
        budgetId: null, // 向后兼容
        budgetIds: [], // 新版本：支持多选
        sourceSemesterId: (this.currentSemester && this.currentSemester.id) || null,
        memo: ''
      };
      this.subsidyPackages = [];
      this.filteredSubsidyPackages = [];
      this.availableBudgets = [];
      this.historicalBudgets = [];
      this.currentPackage = null;
      this.selectedBudget = null;
      this.subsidyDialogLoading = false;
      this.subsidySubmitting = false;
      if (clearSelection) {
        this.selectedStudent = null;
      }
    },
    handleSubsidyDialogClosed() {
      this.resetSubsidyForm(true);
    },
    /** 获取补助套餐（根据学制加载套餐，可选根据经济分类过滤） */
    async fetchSubsidyPackages(schoolingPlanId, economyCategory) {
      this.subsidyDialogLoading = true;
      try {
        // 构建查询参数
        const params = { schoolingPlanId };
        if (economyCategory) {
          params.economyCategory = economyCategory;
        }
        // 学制ID由后端用来控制可用套餐，经济分类匹配等逻辑在后端处理
        const res = await getSubsidyPackages(params);
        this.subsidyPackages = res.data || [];
        if (!this.subsidyPackages.length) {
          if (economyCategory) {
            this.$modal.msgWarning(`该预算的经济分类（${this.getEconomyCategoryLabel(economyCategory)}）下暂无可用补助套餐，请先在后台配置。`);
          } else {
            this.$modal.msgWarning("该学制暂无可用补助套餐，请先在后台配置。");
          }
        }
        // 直接展示后端返回的套餐（后端已根据经济分类过滤）
        this.filteredSubsidyPackages = this.subsidyPackages;
      } finally {
        this.subsidyDialogLoading = false;
      }
    },
    handlePackageChange(value) {
      this.currentPackage = this.filteredSubsidyPackages.find(item => item.id === value) || null;
    },
    async handleBudgetChange(value) {
      // value 现在是数组（多选）
      if (!value || value.length === 0) {
        this.subsidyForm.sourceSemesterId = (this.currentSemester && this.currentSemester.id) || null;
        this.selectedBudget = null;
        // 清空套餐选择，重新加载所有套餐（不按经济分类过滤）
        this.currentPackage = null;
        this.subsidyForm.packageId = null;
        // 重新加载所有套餐
        if (this.selectedStudent && this.selectedStudent.schoolingPlanId) {
          await this.fetchSubsidyPackages(this.selectedStudent.schoolingPlanId, null);
        }
        return;
      }

      // 获取第一个预算作为主预算（用于显示和确定经济分类）
      const firstBudgetId = value[0];
      const budget = this.displayBudgets.find(item => item.id === firstBudgetId);
      if (budget) {
        // 前端只负责更新数据，预算状态验证在后端处理
        this.subsidyForm.sourceSemesterId = budget.yearSemesterId;
        this.selectedBudget = budget;
        // 根据第一个预算的经济分类重新加载套餐列表（后端过滤）
        // 注意：多选时，所有预算的经济分类应该相同，否则后端会验证
        if (this.selectedStudent && this.selectedStudent.schoolingPlanId) {
          await this.fetchSubsidyPackages(this.selectedStudent.schoolingPlanId, budget.economyCategory);
          // 清空套餐选择，因为套餐列表已变化
          this.currentPackage = null;
          this.subsidyForm.packageId = null;
        }
      }
    },
    /** 获取预算状态标签 */
    getBudgetStatusLabel(status) {
      if (!this.dict.type.sys_budget_status) return ''
      const dict = this.dict.type.sys_budget_status.find(item => parseInt(item.value) === status)
      return dict ? dict.label : ''
    },
    /** 获取预算状态提示信息 */
    getBudgetStatusMessage(status) {
      if (status === 2) {
        return '该指标已被冻结！'
      } else if (status === 3) {
        return '该指标已无可用资金！'
      }
      return '该指标状态异常，无法选择！'
    },
    loadBudgets() {
      if (!this.ensureCurrentSemesterReady()) {
        return;
      }

      // 获取功能分类（由后端根据学制计算后直接返回）
      const functionCategory = this.selectedStudent && this.selectedStudent.functionCategory;

      if (!functionCategory) {
        this.$modal.msgError('无法确定学生的功能分类，请检查学生学制信息是否正确');
        this.subsidyDialogLoading = false;
        return;
      }

      // 不再根据套餐的经济分类过滤预算，而是查询所有匹配功能分类的预算
      // 用户选择预算后，再根据预算的经济分类过滤套餐
      const params = {
        currentSemesterId: this.currentSemester.id,
        functionCategory: functionCategory
        // 不传递经济分类参数，查询所有经济分类的预算
      };

      this.subsidyDialogLoading = true;
      Promise.all([
        getAvailableBudgets(params),
        getHistoricalBudgets(params)
      ]).then(([availableRes, historyRes]) => {
        this.availableBudgets = (availableRes && availableRes.data) ? availableRes.data : [];
        this.historicalBudgets = (historyRes && historyRes.data) ? historyRes.data : [];

        const totalBudgets = [...this.availableBudgets, ...this.historicalBudgets];
        if (!totalBudgets.length) {
          this.$modal.msgWarning(`暂未查询到可用预算，请检查：1.是否有匹配的预算（功能分类：${this.getFunctionCategoryLabel(functionCategory)}）2.预算是否有可用余额`);
        }
      }).catch(error => {
        console.error('加载预算失败:', error);
        this.$modal.msgError('加载预算失败，请稍后再试');
      }).finally(() => {
        this.subsidyDialogLoading = false;
      });
    },
    // 获取经济分类标签
    getEconomyCategoryLabel(value) {
      if (!value) return '-';
      const dict = this.dict.type.sys_economy_category || [];
      const found = dict.find(d => String(d.value) === String(value));
      return found ? found.label : value;
    },
    // 获取功能分类标签
    getFunctionCategoryLabel(value) {
      if (!value) return '-';
      const dict = this.dict.type.sys_function_category || [];
      const found = dict.find(d => String(d.value) === String(value));
      return found ? found.label : value;
    },
    // 获取指标来源类型标签
    getQuotaSourceTypeLabel(value) {
      if (!value) return '-';
      const dict = this.dict.type.sys_quota_source_type || [];
      const found = dict.find(d => String(d.value) === String(value));
      return found ? found.label : '未知';
    },
    // 获取学制标签（用于提示信息）
    getSchoolingPlanLabel() {
      if (!this.selectedStudent || !this.selectedStudent.schoolingPlanId) {
        return '-';
      }
      const planId = Number(this.selectedStudent.schoolingPlanId);
      if (planId === 1) return '小学';
      if (planId === 2) return '初中';
      if (planId === 3) return '高中';
      return '-';
    },
    // 格式化金额
    formatAmount(val) {
      const num = Number(val || 0);
      return num.toFixed(2);
    },
    getBudgetOptionLabel(item) {
      // 显示格式：预算项目名称（小学/初中） + 可用金额 + 标签（学期下达/结余（小学）/结余（初中））
      const budgetProjectName = item.budgetProjectName || this.getEconomyCategoryLabel(item.economyCategory) || '未知';
      const amount = this.formatAmount(item.availableAmount);
      const budgetSourceLabel = this.getBudgetSourceTypeLabel(item);
      return `${budgetProjectName} ￥${amount}${budgetSourceLabel ? '（' + budgetSourceLabel + '）' : ''}`;
    },
    // 获取预算来源类型标签（学期下达/结余（小学）/结余（初中））
    // 所有判断逻辑在后端实现，前端直接使用后端返回的sourceTypeLabel
    getBudgetSourceTypeLabel(item) {
      if (!item) {
        return '';
      }
      // 直接使用后端返回的sourceTypeLabel字段（格式：学期下达、结余（小学）、结余（初中））
      return item.sourceTypeLabel || '';
    },
    // 获取预算来源类型颜色
    // 根据后端返回的carryOverFlag字段进行简单的颜色映射（展示逻辑）
    getBudgetSourceTypeColor(item) {
      if (!item) {
        return '#909399';
      }
      // 使用后端返回的carryOverFlag字段：true=结转（橙色），false=学期下达（蓝色）
      if (item.carryOverFlag === true) {
        return '#E6A23C'; // 橙色：结转资金
      }
      return '#409EFF'; // 蓝色：学期下达
    },
    submitSubsidy() {
      if (!this.$refs.subsidyFormRef) {
        return;
      }
      this.$refs.subsidyFormRef.validate(valid => {
        if (!valid) {
          return;
        }

        // 前端只负责收集数据，所有验证和业务逻辑在后端处理
        const payload = { ...this.subsidyForm };
        // 如果使用了budgetIds，确保budgetId为null（避免混淆）
        if (payload.budgetIds && payload.budgetIds.length > 0) {
          payload.budgetId = null; // 使用新字段，清空旧字段
        } else if (payload.budgetId) {
          // 向后兼容：如果只有budgetId，转换为budgetIds
          payload.budgetIds = [payload.budgetId];
        }
        this.subsidySubmitting = true;
        createSubsidy(payload)
          .then(() => {
            this.$modal.msgSuccess("补助录入成功，已进入待审批。");
            this.subsidyDialogVisible = false;
            this.getList();
            // 通知审批页面刷新
            this.$store.dispatch('subsidy/setShouldRefreshSubsidyList', true);
          })
          .catch(error => {
            // 显示后端返回的错误信息（后端已进行所有验证）
            this.$modal.msgError(error.msg || error.message || '补助录入失败，请稍后再试');
          })
          .finally(() => {
            this.subsidySubmitting = false;
          });
      });
    },
    /** 打开学生详情对话框 */
    openStudentDetailDialog(row) {
      this.selectedStudentDetail = { ...row };
      this.studentDetailDialogVisible = true;
      this.loadStudentSubsidyRecords(row);
    },
    /** 加载学生的受助记录（已审核通过） */
    loadStudentSubsidyRecords(row) {
      if (!row || !row.studentId) {
        this.subsidyRecords = [];
        return;
      }
      this.subsidyRecordsLoading = true;
      // 使用 studentId 精确查询该学生的所有已审核通过的受助记录
      listSubsidyRecord({
        studentId: row.studentId,
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
          this.$modal.msgError('加载受助记录失败，请稍后再试');
          this.subsidyRecords = [];
        })
        .finally(() => {
          this.subsidyRecordsLoading = false;
        });
    },
    /** 关闭学生详情对话框 */
    handleStudentDetailDialogClosed() {
      this.selectedStudentDetail = null;
      this.subsidyRecords = [];
    },

    /** 表格选择变化 */
    handleSelectionChange(selection) {
      this.selectedStudents = selection;
    },

    /** 打开批量发放对话框 */
    async openBatchPaymentDialog() {
      // 重置表单和状态
      this.batchPaymentActiveTab = 'select';
      this.batchPaymentForm.studentPayments = [];
      this.batchPaymentStudentList = [];
      this.batchPaymentSelectedStudentIds = [];
      this.resetBatchPaymentFilterForm();

      // 初始化筛选条件（使用当前学期）
      if (this.currentSemester && this.currentSemester.schoolYear) {
        this.batchPaymentFilterForm.academicYear = this.currentSemester.schoolYear;
        this.batchPaymentFilterForm.semester = String(this.currentSemester.semester || '1');
        this.batchPaymentFilterForm.semesterLabel = this.currentSemester.semesterLabel || '';
      }

      // 生成学年选项（当前学年前后各2年）
      this.generateAcademicYearOptions();

      // 加载学段选项
      await this.loadSchoolingPlanOptions();

      // 初始化批量发放表单
      this.batchPaymentForm = {
        budgetId: null, // 向后兼容
        budgetIds: [], // 新版本：支持多选
        sourceSemesterId: null,
        paymentDate: this.parseTime(new Date(), '{y}-{m}-{d}'),
        paymentPerson: this.$store.state.user.name || '',
        memo: '',
        studentPayments: []
      };

      // 重置统一录入
      this.uniformPaymentPackageId = null;
      this.uniformPaymentAmount = null;
      this.uniformPaymentPackages = [];

      this.batchPaymentDialogVisible = true;
    },

    /** 批量发放对话框打开后 */
    async handleBatchPaymentDialogOpened() {
      // 对话框打开后，如果学年学期已设置，自动查询学生
      if (this.batchPaymentFilterForm.academicYear && this.batchPaymentFilterForm.semester) {
        this.handleQueryStudents();
      }
    },

    /** 生成学年选项 */
    generateAcademicYearOptions() {
      const currentYear = new Date().getFullYear();
      const options = [];
      for (let i = -2; i <= 2; i++) {
        const year = currentYear + i;
        options.push(`${year}-${year + 1}`);
      }
      this.academicYearOptions = options;
    },

    /** 重置筛选表单 */
    resetBatchPaymentFilterForm() {
      this.batchPaymentFilterForm = {
        academicYear: null,
        semester: null,
        semesterLabel: null,
        schoolingPlanId: null,
        gradeId: null,
        classId: null
      };
      this.gradeOptions = [];
      this.classOptions = [];
    },

    /** 加载学段选项 */
    async loadSchoolingPlanOptions() {
      try {
        const response = await getSchoolPlanList();
        if (response.code === 200 && response.data) {
          this.schoolingPlanOptions = response.data.map(item => ({
            id: item.id,
            name: item.name
          }));
        } else {
          // 如果API失败，使用默认值
          this.schoolingPlanOptions = [
            { id: 1, name: '小学' },
            { id: 2, name: '初中' },
            { id: 3, name: '高中' }
          ];
        }
      } catch (error) {
        console.error('加载学段选项失败:', error);
        // 如果API失败，使用默认值
        this.schoolingPlanOptions = [
          { id: 1, name: '小学' },
          { id: 2, name: '初中' },
          { id: 3, name: '高中' }
        ];
      }
    },

    /** 筛选学段变化 */
    async handleFilterSchoolingPlanChange() {
      this.batchPaymentFilterForm.gradeId = null;
      this.batchPaymentFilterForm.classId = null;
      this.gradeOptions = [];
      this.classOptions = [];

      if (this.batchPaymentFilterForm.schoolingPlanId) {
        await this.loadGradeOptions(this.batchPaymentFilterForm.schoolingPlanId);
      }
    },

    /** 加载年级选项 */
    async loadGradeOptions(schoolingPlanId) {
      try {
        const response = await getGradeList(schoolingPlanId);
        if (response.code === 200 && response.data) {
          this.gradeOptions = response.data.map(item => ({
            id: item.id,
            name: item.name
          }));
        } else {
          this.gradeOptions = [];
        }
      } catch (error) {
        console.error('加载年级选项失败:', error);
        this.gradeOptions = [];
      }
    },

    /** 筛选年级变化 */
    async handleFilterGradeChange() {
      this.batchPaymentFilterForm.classId = null;
      this.classOptions = [];

      if (this.batchPaymentFilterForm.gradeId) {
        await this.loadClassOptions(this.batchPaymentFilterForm.gradeId);
      }
    },

    /** 加载班级选项 */
    async loadClassOptions(gradeId) {
      try {
        const response = await getClassList(gradeId);
        if (response.code === 200 && response.data) {
          this.classOptions = response.data.map(item => ({
            classId: item.classId,
            className: item.className
          }));
        } else {
          this.classOptions = [];
        }
      } catch (error) {
        console.error('加载班级选项失败:', error);
        this.classOptions = [];
      }
    },

    /** 学年变化 */
    handleFilterAcademicYearChange() {
      // 学年变化时的处理
    },

    /** 查询学生 */
    async handleQueryStudents() {
      if (!this.batchPaymentFilterForm.academicYear || !this.batchPaymentFilterForm.semester) {
        this.$modal.msgWarning('请选择学年和学期');
        return;
      }

      this.batchPaymentQueryLoading = true;
      try {
        const queryParams = {
          academicYear: this.batchPaymentFilterForm.academicYear,
          semester: this.batchPaymentFilterForm.semester,
          pageNum: 1,
          pageSize: 1000, // 查询所有匹配的学生
          excludeWithSubsidyRecords: true // 后端会过滤：只返回未录入补助（无状态）或退回补助（已驳回）的学生
        };

        // 添加筛选条件
        if (this.batchPaymentFilterForm.schoolingPlanId) {
          queryParams.schoolingPlanId = this.batchPaymentFilterForm.schoolingPlanId;
        }
        if (this.batchPaymentFilterForm.gradeId) {
          queryParams.gradeId = this.batchPaymentFilterForm.gradeId;
        }
        if (this.batchPaymentFilterForm.classId) {
          queryParams.clazzId = this.batchPaymentFilterForm.classId; // 后端使用clazzId字段
        }

        const response = await listAidedStudentInfo(queryParams);
        this.batchPaymentStudentList = response.rows || [];
        this.batchPaymentSelectedStudentIds = [];

        if (this.batchPaymentStudentList.length === 0) {
          this.$modal.msgInfo('未查询到符合条件的学生');
        }
      } catch (error) {
        console.error('查询学生失败:', error);
        this.$modal.msgError('查询学生失败，请稍后再试');
      } finally {
        this.batchPaymentQueryLoading = false;
      }
    },

    /** 重置筛选条件 */
    handleResetStudentFilter() {
      // 重置筛选条件，但保留学年学期（使用当前学期）
      this.batchPaymentFilterForm.schoolingPlanId = null;
      this.batchPaymentFilterForm.gradeId = null;
      this.batchPaymentFilterForm.classId = null;
      this.gradeOptions = [];
      this.classOptions = [];
      this.batchPaymentStudentList = [];
      this.batchPaymentSelectedStudentIds = [];
      // 重置后自动查询
      if (this.batchPaymentFilterForm.academicYear && this.batchPaymentFilterForm.semester) {
        this.handleQueryStudents();
      }
    },

    /** 学生选择变化 */
    handleBatchPaymentStudentSelectionChange(selection) {
      this.batchPaymentSelectedStudentIds = selection.map(item => item.studentId || item.id);
    },

    /** 全选学生 */
    handleSelectAllStudents() {
      if (this.$refs.batchPaymentStudentTable) {
        this.$refs.batchPaymentStudentTable.toggleAllSelection();
      }
    },

    /** 取消全选 */
    handleClearStudentSelection() {
      if (this.$refs.batchPaymentStudentTable) {
        this.$refs.batchPaymentStudentTable.clearSelection();
      }
    },

    /** 添加选中的学生 */
    async handleAddSelectedStudents() {
      if (this.batchPaymentSelectedStudentIds.length === 0) {
        this.$modal.msgWarning('请先选择学生');
        return;
      }

      const selectedStudents = this.batchPaymentStudentList.filter(item =>
        this.batchPaymentSelectedStudentIds.includes(item.studentId || item.id)
      );

      // UI层面的去重检查（避免重复添加，提升用户体验）
      // 注意：后端会进行最终验证，确保学生信息的正确性
      const existingIds = this.batchPaymentForm.studentPayments.map(item => item.studentId);
      const newStudents = selectedStudents.filter(item =>
        !existingIds.includes(item.studentId || item.id)
      );

      if (newStudents.length === 0) {
        this.$modal.msgWarning('所选学生已全部添加');
        return;
      }

      // 添加学生到发放列表（学制ID由后端直接返回）
      for (const student of newStudents) {
        this.batchPaymentForm.studentPayments.push({
          studentId: student.studentId || student.id,
          studentName: student.studentName,
          studentNo: student.studentNo || student.idCard,
          academicYear: student.academicYear || this.batchPaymentFilterForm.academicYear,
          semester: student.semester || this.batchPaymentFilterForm.semester,
          schoolingPlanId: student.schoolingPlanId,
          functionCategory: student.functionCategory,
          packageId: null,
          selectedPackage: null,
          packages: [],
          paymentAmount: null,
          economyCategory: null
        });
      }

      // 加载预算列表
      this.loadBatchPaymentBudgets();

      // 为每个新添加的学生加载套餐列表
      for (let i = this.batchPaymentForm.studentPayments.length - newStudents.length;
           i < this.batchPaymentForm.studentPayments.length; i++) {
        await this.loadPackagesForStudent(this.batchPaymentForm.studentPayments[i]);
      }

      // 如果已选择预算，更新统一录入的套餐列表
      if (this.batchPaymentSelectedBudget) {
        this.updateUniformPaymentPackages();
      }

      this.$modal.msgSuccess(`成功添加 ${newStudents.length} 名学生`);

      // 清空选择
      this.batchPaymentSelectedStudentIds = [];
      if (this.$refs.batchPaymentStudentTable) {
        this.$refs.batchPaymentStudentTable.clearSelection();
      }
    },

    /** 移除学生发放项 */
    removeStudentPayment(index) {
      this.batchPaymentForm.studentPayments.splice(index, 1);
      // 更新统一录入的套餐列表
      if (this.batchPaymentSelectedBudget) {
        this.updateUniformPaymentPackages();
      }
    },

    /** 发放金额变化处理 */
    handlePaymentAmountChange(row) {
      // 如果用户手动修改了金额，清空套餐选择（因为金额与套餐不匹配）
      if (row.selectedPackage && row.paymentAmount !== (row.selectedPackage.subsidyAmount || 0)) {
        row.packageId = null;
        row.selectedPackage = null;
      }
    },

    /** 批量发放套餐选择变化 */
    handleBatchPaymentPackageChange(row, index) {
      if (!row.packageId) {
        row.selectedPackage = null;
        // 不清空金额，允许用户手动输入
        return;
      }
      // 找到选中的套餐
      const selectedPackage = row.packages.find(pkg => pkg.id === row.packageId);
      if (selectedPackage) {
        row.selectedPackage = selectedPackage;
        // 自动设置金额为套餐金额
        row.paymentAmount = selectedPackage.subsidyAmount || 0;
      }
    },

    /** 为单个学生加载套餐列表 */
    async loadPackagesForStudent(studentPayment) {
      if (!studentPayment.schoolingPlanId) {
        studentPayment.packages = [];
        return;
      }
      try {
        // 构建查询参数：学制ID + 经济分类（如果已设置）
        const params = { schoolingPlanId: studentPayment.schoolingPlanId };
        if (studentPayment.economyCategory) {
          params.economyCategory = studentPayment.economyCategory;
        }
        // 由后端根据学制、经济分类等规则直接返回可用套餐列表
        const res = await getSubsidyPackages(params);
        const allPackages = res.data || [];
        studentPayment.packages = allPackages;
      } catch (error) {
        console.error('加载套餐失败:', error);
        studentPayment.packages = [];
      }
    },

    /** 获取学生过滤后的套餐列表（用于下拉框显示） */
    getFilteredPackagesForStudent(studentPayment) {
      return studentPayment.packages || [];
    },

    /** 批量发放预算选择变化 */
    async handleBatchPaymentBudgetChange(value) {
      // value 现在是数组（多选）
      // 前端只负责更新选中的预算ID，所有验证和逻辑判断在后端处理
      if (!value || value.length === 0) {
        this.batchPaymentForm.budgetId = null;
        this.batchPaymentForm.budgetIds = [];
        this.batchPaymentForm.sourceSemesterId = null;
        this.batchPaymentSelectedBudget = null;
        // 清空所有学生的套餐选择（预算已清空，经济分类匹配交由后端控制）
        this.batchPaymentForm.studentPayments.forEach(item => {
          item.economyCategory = null;
          item.packageId = null;
          item.selectedPackage = null;
          // 重新加载所有套餐（由后端控制可用范围）
          this.loadPackagesForStudent(item);
        });
        // 清空统一录入
        this.uniformPaymentPackageId = null;
        this.uniformPaymentAmount = null;
        this.uniformPaymentPackages = [];
        return;
      }
      // 获取第一个预算作为主预算（用于显示和确定经济分类）
      const firstBudgetId = value[0];
      const budget = this.batchPaymentDisplayBudgets.find(item => item.id === firstBudgetId);
      if (budget) {
        this.batchPaymentForm.sourceSemesterId = budget.yearSemesterId;
        this.batchPaymentSelectedBudget = budget;
        // 为所有学生设置预算的经济分类（最终校验在后端）
        // 注意：多选时，所有预算的经济分类应该相同，否则后端会验证
        // 并重新加载每个学生的套餐列表（由后端根据经济分类等规则控制）
        for (const item of this.batchPaymentForm.studentPayments) {
          item.economyCategory = budget.economyCategory;
          // 重新加载该学生的套餐
          await this.loadPackagesForStudent(item);
        }
        // 更新统一录入的套餐列表（所有学生共有的套餐）
        this.updateUniformPaymentPackages();
      }
    },

    /** 更新统一录入的套餐列表（取所有学生共有的套餐） */
    updateUniformPaymentPackages() {
      if (this.batchPaymentForm.studentPayments.length === 0) {
        this.uniformPaymentPackages = [];
        return;
      }

      // 获取第一个学生的套餐列表作为基础
      const firstStudentPackages = this.batchPaymentForm.studentPayments[0].packages || [];
      if (firstStudentPackages.length === 0) {
        this.uniformPaymentPackages = [];
        return;
      }

      // 找出所有学生都有的套餐（按套餐ID匹配）
      const commonPackages = firstStudentPackages.filter(pkg => {
        return this.batchPaymentForm.studentPayments.every(student => {
          const studentPackages = student.packages || [];
          return studentPackages.some(sp => sp.id === pkg.id);
        });
      });

      this.uniformPaymentPackages = commonPackages;

      // 如果当前选择的套餐不在共有套餐中，清空选择
      if (this.uniformPaymentPackageId && !commonPackages.find(p => p.id === this.uniformPaymentPackageId)) {
        this.uniformPaymentPackageId = null;
      }
    },

    /** 统一录入套餐选择变化 */
    handleUniformPackageChange(packageId) {
      if (!packageId) {
        return;
      }
      // 找到选中的套餐，自动设置金额
      const selectedPackage = this.uniformPaymentPackages.find(pkg => pkg.id === packageId);
      if (selectedPackage) {
        this.uniformPaymentAmount = selectedPackage.subsidyAmount || null;
      }
    },

    /** 应用到全部（统一录入） */
    applyUniformPaymentToAll() {
      if (this.batchPaymentForm.studentPayments.length === 0) {
        this.$modal.msgWarning('没有学生需要设置');
        return;
      }

      // 前端只负责设置数据，所有验证在后端处理
      // 应用到所有学生
      this.batchPaymentForm.studentPayments.forEach(student => {
        // 如果选择了套餐，且该学生有这个套餐，则应用
        if (this.uniformPaymentPackageId) {
          const studentPackage = student.packages.find(p => p.id === this.uniformPaymentPackageId);
          if (studentPackage) {
            student.packageId = this.uniformPaymentPackageId;
            student.selectedPackage = studentPackage;
            // 如果统一金额有值，使用统一金额；否则使用套餐金额
            student.paymentAmount = this.uniformPaymentAmount || studentPackage.subsidyAmount || 0;
          }
        } else if (this.uniformPaymentAmount) {
          // 只设置了金额，没有选择套餐
          student.paymentAmount = this.uniformPaymentAmount;
        }
      });

      this.$modal.msgSuccess(`已为 ${this.batchPaymentForm.studentPayments.length} 名学生统一设置补助信息`);
    },

    /** 加载批量发放的预算列表 */
    loadBatchPaymentBudgets() {
      if (!this.ensureCurrentSemesterReady()) {
        this.batchPaymentDisplayBudgets = [];
        return;
      }

      // 获取第一个学生（假设批量发放的学生学制/功能分类相同）
      const firstStudent = this.batchPaymentForm.studentPayments && this.batchPaymentForm.studentPayments.length > 0
        ? this.batchPaymentForm.studentPayments[0]
        : null;

      if (!firstStudent || !firstStudent.functionCategory) {
        this.batchPaymentDisplayBudgets = [];
        return;
      }

      // 使用与个人录入相同的参数格式
      const params = {
        currentSemesterId: this.currentSemester.id,
        functionCategory: firstStudent.functionCategory
      };

      this.batchPaymentDialogLoading = true;
      Promise.all([
        getAvailableBudgets(params),
        getHistoricalBudgets(params)
      ]).then(([availableRes, historyRes]) => {
        const availableBudgets = (availableRes && availableRes.data) ? availableRes.data : [];
        const historicalBudgets = (historyRes && historyRes.data) ? historyRes.data : [];

        this.batchPaymentDisplayBudgets = [...availableBudgets, ...historicalBudgets];
      }).catch(error => {
        console.error('加载预算失败:', error);
        this.$modal.msgError('加载预算失败，请稍后再试');
        this.batchPaymentDisplayBudgets = [];
      }).finally(() => {
        this.batchPaymentDialogLoading = false;
      });
    },


    /** 获取错误行样式类名 */
    getErrorRowClassName({ row, rowIndex }) {
      return 'error-row';
    },
    /** 导出错误列表 */
    exportErrorList() {
      if (!this.importResult.errors || this.importResult.errors.length === 0) {
        this.$modal.msgWarning('没有错误记录可导出');
        return;
      }

      try {
        // 构建 CSV 数据
        const headers = ['序号', '行号', '学生姓名', '学籍号', '错误信息'];
        const rows = this.importResult.errors.map((error, index) => {
          const row = [
            index + 1,
            error.rowNum || '-',
            error.studentName || error.name || '-',
            error.studentNo || '-',
            (error.errorMessage || error.errorMsg || '-').replace(/"/g, '""') // 转义双引号
          ];
          return '"' + row.join('","') + '"'; // CSV 格式
        });

        // 组合 CSV 内容
        const csvContent = [
          '"' + headers.join('","') + '"',
          ...rows
        ].join('\n');

        // 添加 BOM 以支持中文
        const BOM = '\uFEFF';
        const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });

        // 创建下载链接
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', `${this.importResultDialogTitle}_错误列表_${this.parseTime(new Date(), '{y}{m}{d}{h}{i}{s}')}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);

        this.$modal.msgSuccess('错误列表导出成功');
      } catch (error) {
        console.error('导出错误列表失败:', error);
        this.$modal.msgError('导出失败，请稍后重试');
      }
    },
    /** 提交批量发放 */
    submitBatchPayment() {
      this.$refs['batchPaymentFormRef'].validate(valid => {
        if (!valid) {
          return;
        }

        // 前端只负责收集数据，所有逻辑在后端处理
        this.batchPaying = true;

        // 构建请求数据
        const requestData = {
          budgetId: null, // 使用新字段，清空旧字段
          budgetIds: this.batchPaymentForm.budgetIds && this.batchPaymentForm.budgetIds.length > 0
            ? this.batchPaymentForm.budgetIds
            : (this.batchPaymentForm.budgetId ? [this.batchPaymentForm.budgetId] : []), // 向后兼容
          sourceSemesterId: this.batchPaymentForm.sourceSemesterId,
          paymentDate: this.batchPaymentForm.paymentDate,
          paymentPerson: this.batchPaymentForm.paymentPerson,
          memo: this.batchPaymentForm.memo,
          studentPayments: this.batchPaymentForm.studentPayments.map(item => ({
            studentId: item.studentId,
            studentName: item.studentName,
            studentNo: item.studentNo,
            academicYear: item.academicYear,
            semester: item.semester,
            schoolingPlanId: item.schoolingPlanId,
            packageId: item.packageId, // 套餐ID
            paymentAmount: item.paymentAmount,
            economyCategory: item.economyCategory
          }))
        };

        // 调用后端接口，后端会处理所有逻辑
        batchPayment(requestData)
          .then(response => {
            if (response.code === 200 && response.data) {
              const result = response.data;
              const successCount = result.successCount || 0;
              const failureCount = result.failureCount || 0;
              const totalCount = result.totalCount || 0;

              // 显示详细结果（始终显示对话框）
              this.importResultDialogTitle = '批量发放结果';
              this.importResult = {
                successCount: successCount,
                failureCount: failureCount,
                totalCount: totalCount,
                errors: result.errors || []
              };
              this.importResultDialogVisible = true;

              // 根据成功/失败数量给出更准确的提示（避免“全部成功”的错觉）
              if (failureCount > 0) {
                this.$modal.msgWarning(`批量发放完成，成功 ${successCount} 人，失败 ${failureCount} 人，请查看明细。`);
              } else if (successCount > 0) {
                this.$modal.msgSuccess(`${successCount} 人录入成功，待审核！`);
              }

              this.batchPaymentDialogVisible = false;
              this.selectedStudents = [];
              this.getList();
              // 通知审批页面刷新
              this.$store.dispatch('subsidy/setShouldRefreshSubsidyList', true);
            } else {
              const successCount = (response.data && response.data.successCount) || 0;
              const failureCount = (response.data && response.data.failureCount) || 0;
              if (failureCount > 0) {
                this.$modal.msgWarning(`批量发放完成，成功 ${successCount} 人，失败 ${failureCount} 人，请查看明细。`);
              } else if (successCount > 0) {
                this.$modal.msgSuccess(`${successCount} 人录入成功，待审核！`);
              } else {
                this.$modal.msgSuccess(response.msg || '批量发放成功');
              }
              this.batchPaymentDialogVisible = false;
              this.selectedStudents = [];
              this.getList();
              // 通知审批页面刷新
              this.$store.dispatch('subsidy/setShouldRefreshSubsidyList', true);
            }
            this.batchPaying = false;
          })
          .catch(error => {
            console.error('批量发放失败:', error);
            this.$modal.msgError(error.msg || '批量发放失败，请稍后再试');
            this.batchPaying = false;
          });
      });
    },

    /** 打开Excel导入对话框 */
    handleImport() {
        this.upload.url = process.env.VUE_APP_BASE_API + '/system/subsidyRecord/importPayment';
      this.upload.headers = { Authorization: 'Bearer ' + getToken() };
      this.importDialogVisible = true;
    },

    /** 下载导入模板 */
    handleDownloadTemplate() {
      this.download('system/subsidyRecord/importTemplate', {}, '补助发放导入模板.xlsx');
    },

    /** 文件变化处理 */
    handleFileChange(file, fileList) {
      this.upload.fileList = fileList;
    },
    /** 文件上传中处理 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
      this.upload.percent = Math.round(event.percent || 0);
    },

    /** 文件上传成功处理 */
    handleFileSuccess(response, file, fileList) {
      this.upload.isUploading = false;
      this.upload.percent = 0;
      this.upload.fileList = [];
      this.importDialogVisible = false;
      this.$refs.upload.clearFiles();

      if (response.code === 200 && response.data) {
        this.importResultDialogTitle = 'Excel导入结果';
        this.importResult = response.data;
        this.importResultDialogVisible = true;

        // 显示成功提示：xx人录入成功，待审核！（与批量发放一致）
        const successCount = response.data.successCount || 0;
        if (successCount > 0) {
          this.$modal.msgSuccess(`${successCount}人录入成功，待审核！`);
        }

        // 刷新列表
        this.getList();
        // 通知审批页面刷新
        this.$store.dispatch('subsidy/setShouldRefreshSubsidyList', true);
      } else {
        this.$modal.msgError(response.msg || '导入失败');
      }
    },

    /** 提交上传文件 */
    submitFileForm() {
      // 基础文件格式验证（仅检查文件扩展名，提升用户体验）
      // 注意：后端会进行完整的数据验证和业务逻辑处理
      const file = this.$refs.upload.uploadFiles;
      if (!file || file.length === 0 || (!file[0].name.toLowerCase().endsWith('.xls') && !file[0].name.toLowerCase().endsWith('.xlsx'))) {
        this.$modal.msgError("请选择后缀为 \"xls\"或\"xlsx\"的文件。");
        return;
      }
      this.$refs.upload.submit();
    },
    
    // ========== 同步进度相关方法 ==========
    /** 开始轮询进度 */
    startProgressPolling(academicYear, semester) {
      // 清除之前的定时器
      if (this.syncProgressTimer) {
        clearInterval(this.syncProgressTimer);
      }
      
      // 立即查询一次
      this.queryProgress(academicYear, semester);
      
      // 设置定时器，每2秒查询一次
      this.syncProgressTimer = setInterval(() => {
        this.queryProgress(academicYear, semester);
      }, 2000);
    },
    
    /** 查询进度 */
    queryProgress(academicYear, semester) {
      getSyncProgress(academicYear, semester).then(response => {
        if (response.data) {
          this.syncProgress = response.data;
          
          // 如果同步完成或失败，停止轮询
          if (this.syncProgress.status === 'completed' || this.syncProgress.status === 'failed') {
            this.stopProgressPolling();
            
            // 刷新列表
            this.getList();
            
            // 显示通知
            if (this.syncProgress.status === 'completed') {
              this.$notify({
                title: '同步成功',
                message: this.syncProgress.message || '同步完成',
                type: 'success',
                duration: 4500
              });
            }
          }
        }
      }).catch(error => {
        console.error('查询进度失败:', error);
      });
    },
    
    /** 停止轮询 */
    stopProgressPolling() {
      if (this.syncProgressTimer) {
        clearInterval(this.syncProgressTimer);
        this.syncProgressTimer = null;
      }
    },
    
    /** 关闭进度对话框 */
    closeSyncProgressDialog() {
      this.syncProgressDialogVisible = false;
      this.stopProgressPolling();
    },
    
    /** 获取进度条状态 */
    getProgressStatus() {
      if (this.syncProgress.status === 'completed') {
        return 'success';
      } else if (this.syncProgress.status === 'failed') {
        return 'exception';
      }
      return undefined;
    },
    
    /** 获取状态标签类型 */
    getStatusTagType() {
      const statusMap = {
        'not_started': 'info',
        'running': 'warning',
        'completed': 'success',
        'failed': 'danger'
      };
      return statusMap[this.syncProgress.status] || 'info';
    },
    
    /** 获取状态文本 */
    getStatusText() {
      const statusMap = {
        'not_started': '未开始',
        'running': '同步中',
        'completed': '已完成',
        'failed': '失败'
      };
      return statusMap[this.syncProgress.status] || '未知';
    },
    
    /** 格式化时间 */
    formatTime(timestamp) {
      if (!timestamp) return '-';
      const date = new Date(timestamp);
      const Y = date.getFullYear();
      const M = (date.getMonth() + 1).toString().padStart(2, '0');
      const D = date.getDate().toString().padStart(2, '0');
      const h = date.getHours().toString().padStart(2, '0');
      const m = date.getMinutes().toString().padStart(2, '0');
      const s = date.getSeconds().toString().padStart(2, '0');
      return `${Y}-${M}-${D} ${h}:${m}:${s}`;
    },
    
    /** 计算耗时 */
    calculateDuration() {
      if (!this.syncProgress.startTime || !this.syncProgress.endTime) {
        return '-';
      }
      const duration = this.syncProgress.endTime - this.syncProgress.startTime;
      const seconds = Math.floor(duration / 1000);
      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      
      if (minutes > 0) {
        return `${minutes}分${remainingSeconds}秒`;
      }
      return `${remainingSeconds}秒`;
    }
  },
  beforeDestroy() {
    // 组件销毁前清除定时器
    this.stopProgressPolling();
  }
};
</script>

<style scoped>
.aided-student-page {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

.aided-student-page ::v-deep * {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

.aided-student-page ::v-deep button,
.aided-student-page ::v-deep .el-button {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

/* 搜索按钮使用蓝色背景 - 仅针对查询表单中的搜索按钮 */
.aided-student-page ::v-deep .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.aided-student-page ::v-deep .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.aided-student-page ::v-deep .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 */
.aided-student-page ::v-deep .action-row .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page ::v-deep .action-row .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page ::v-deep .action-row .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 统一功能按钮样式：白色背景、浅灰色边框、黑色字体（排除 primary 按钮） */
.aided-student-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 覆盖 plain 按钮样式（排除 primary 按钮） */
.aided-student-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 搜索 + 表格 毛玻璃容器 */
.query-table-wrapper {
  margin-bottom: 12px;
  padding: 16px 20px 20px;
  border-radius: 16px;
  background-color: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.query-form-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(228, 231, 237, 0.5);
}

.collapse-btn {
  padding: 0;
  color: #606266;
  font-size: 13px;
}

.collapse-btn:hover {
  color: #409EFF;
}

.query-table-wrapper .query-form {
  margin-top: 0;
}

.query-table-wrapper .action-row {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(228, 231, 237, 0.5);
}

.table-section {
  margin-top: 12px;
}

.current-semester-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  margin-top: 3px;
}

.semester-tag {
  border-color: #1890ff;
  color: #1890ff;
}

.action-row {
  align-items: center;
}

.aided-table ::v-deep .el-table__header th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  border-top: 1px solid #409EFF !important;
  border-bottom: 1px solid #409EFF !important;
}

.aided-table ::v-deep .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.aided-table ::v-deep .support-column .cell {
  overflow: visible;
  text-overflow: clip;
}

.difficulty-pill {
  display: inline-block;
  padding: 0 10px;
  line-height: 22px;
  border: 1px solid #1890ff;
  border-radius: 6px;
  color: #1890ff;
  background: #ecf5ff;
  font-size: 12px;
}

.aided-table ::v-deep .op-column .cell {
  white-space: nowrap;
  overflow: visible;
  text-overflow: clip;
}

.student-summary {
  font-size: 13px;
  line-height: 22px;
  color: #606266;
}

/* 学生信息卡片样式 */
.student-info-card {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.student-info-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.student-info-header i {
  margin-right: 8px;
  font-size: 18px;
  color: #409EFF;
}

.student-info-card ::v-deep .el-card__body {
  padding: 15px;
}

.student-info-card ::v-deep .el-descriptions__label {
  font-weight: 500;
  color: #606266;
  width: 90px;
}

.student-info-card .info-value {
  color: #303133;
  font-size: 14px;
}

/* 补助金额显示样式 */
.subsidy-amount-display {
  color: #f56c6c;
  font-weight: 600;
  font-size: 18px;
}

/* 表格中资助金额显示样式 */
.subsidy-amount-text {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
}

/* 补助录入按钮样式 - 圆形图标 */
.subsidy-add-btn-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s, opacity 0.2s;
  flex-shrink: 0;
}

.subsidy-add-btn-icon:hover {
  transform: scale(1.1);
  opacity: 0.9;
}

.subsidy-add-btn-icon:active {
  transform: scale(0.95);
}

.subsidy-add-btn-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.5;
}

.form-tip i {
  margin-right: 4px;
  color: #409EFF;
}

/* 预算选项样式优化 */
::v-deep .el-select-dropdown__item {
  padding: 10px 20px;
  line-height: 1.6;
  white-space: normal;
}

.subsidy-entry-dialog ::v-deep .el-dialog__body {
  padding: 20px;
}
</style>

<style>
/* 预算选择下拉框样式（全局样式，因为 popper 是挂载到 body 的） */
.budget-select-dropdown {
  max-height: 600px !important;
}

.budget-select-dropdown .el-select-dropdown__wrap {
  max-height: 600px !important;
}

.budget-select-dropdown .el-scrollbar__wrap {
  max-height: 600px !important;
  overflow-y: auto !important;
}

/* 优化选项样式，让内容更紧凑 */
.budget-select-dropdown .el-select-dropdown__item {
  padding: 8px 15px !important;
  line-height: 1.5 !important;
  white-space: normal !important;
  height: auto !important;
  min-height: 60px !important;
}

.budget-select-dropdown .el-select-dropdown__item:hover {
  background-color: #f5f7fa !important;
}

/* 学生详情对话框样式 */
.student-detail-content {
  padding: 0;
}

.detail-card {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.detail-card .card-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.detail-card .card-header i {
  margin-right: 8px;
  font-size: 18px;
  color: #409EFF;
}

.detail-card ::v-deep .el-card__body {
  padding: 15px;
}

.detail-card ::v-deep .el-descriptions__label {
  font-weight: 500;
  color: #606266;
  width: 90px;
}

.detail-card .info-value {
  color: #303133;
  font-size: 14px;
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}

.empty-tip i {
  margin-right: 8px;
  font-size: 16px;
}

/* 批量导入结果对话框样式 */
.import-result-dialog ::v-deep .el-dialog__body {
  padding: 20px 25px;
}

.import-result-dialog ::v-deep .el-dialog__header {
  padding: 20px 25px 15px;
  border-bottom: 1px solid #e4e7ed;
}

.import-result-dialog ::v-deep .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.import-result-dialog ::v-deep .el-dialog__footer {
  padding: 15px 25px 20px;
  border-top: 1px solid #e4e7ed;
}

/* 统计卡片容器 */
.import-stats-container {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
}

.stat-card.stat-total {
  background: #409EFF;
  color: #fff;
}

.stat-card.stat-success {
  background: #67C23A;
  color: #fff;
}

.stat-card.stat-failure {
  background: #F56C6C;
  color: #fff;
}

.stat-icon {
  font-size: 36px;
  margin-right: 15px;
  opacity: 0.9;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 500;
}

/* 提示信息容器 */
.import-alert-container {
  margin-bottom: 20px;
}

/* 错误列表容器 */
.error-list-container {
  margin-top: 20px;
}

.error-list-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #e6a23c;
}

.error-list-header i {
  margin-right: 8px;
  font-size: 18px;
}

.error-table {
  border-radius: 4px;
  overflow: hidden;
}

.error-table ::v-deep .el-table__header {
  background-color: #f5f7fa;
}

.error-table ::v-deep .el-table__header th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.error-table ::v-deep .el-table__row {
  transition: background-color 0.2s;
}

.error-table ::v-deep .el-table__row.error-row:hover {
  background-color: #fef0f0;
}

.error-message {
  display: flex;
  align-items: flex-start;
  color: #f56c6c;
}

.error-icon {
  margin-right: 6px;
  margin-top: 2px;
  font-size: 16px;
  flex-shrink: 0;
}

.student-name {
  font-weight: 500;
  color: #303133;
}

.student-no {
  color: #606266;
  font-family: 'Courier New', monospace;
}

/* 成功提示容器 */
.success-container {
  text-align: center;
  padding: 40px 20px;
  margin-top: 20px;
}

.success-icon {
  font-size: 64px;
  color: #67c23a;
  margin-bottom: 16px;
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.success-message {
  font-size: 16px;
  color: #67c23a;
  font-weight: 500;
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer .el-button {
  min-width: 100px;
}

/* Excel导入对话框样式 */
.excel-import-dialog ::v-deep .el-dialog__body {
  padding: 20px 25px;
}

.excel-import-dialog ::v-deep .el-dialog__header {
  padding: 20px 25px 15px;
  border-bottom: 1px solid #e4e7ed;
}

.excel-import-dialog ::v-deep .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}


.excel-import-dialog ::v-deep .el-dialog__footer {
  padding: 15px 25px 20px;
  border-top: 1px solid #e4e7ed;
}

.import-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 文件上传区域 */
.upload-section {
  margin: 10px 0;
}

.excel-upload ::v-deep .el-upload {
  width: 100%;
}

.excel-upload ::v-deep .el-upload-dragger {
  width: 100%;
  height: 200px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s;
  padding: 30px 20px;
}

.excel-upload ::v-deep .el-upload-dragger:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.upload-icon-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.upload-icon {
  font-size: 64px;
  color: #409EFF;
  transition: all 0.3s;
}

.excel-upload ::v-deep .el-upload-dragger:hover .upload-icon {
  transform: scale(1.1);
  color: #66b1ff;
}

.upload-progress {
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  width: 200px;
}

.upload-text {
  text-align: center;
}

.upload-main-text {
  font-size: 16px;
  color: #606266;
  margin-bottom: 8px;
}

.upload-link {
  color: #409EFF;
  font-style: normal;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
}

.upload-tip-text {
  font-size: 13px;
  color: #909399;
}

/* 操作选项 */
.import-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding: 12px 0;
}

.download-template-btn {
  color: #409EFF;
  font-size: 14px;
  padding: 0;
}

/* 同步进度对话框样式 */
.sync-progress-container {
  padding: 10px 0;
}

.progress-section {
  margin-bottom: 25px;
}

.status-section {
  margin-bottom: 25px;
}

.status-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
}

.status-value {
  color: #303133;
  flex: 1;
}

.stats-section {
  margin-bottom: 25px;
}

.stats-section .stat-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.stats-section .stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stats-section .stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.stats-section .stat-icon i {
  font-size: 24px;
  color: #ffffff;
}

.stats-section .stat-content {
  flex: 1;
}

.stats-section .stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stats-section .stat-label {
  font-size: 13px;
  color: #909399;
}

.time-section {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 15px;
}

.time-item {
  display: flex;
  align-items: center;
  padding: 6px 0;
}

.time-label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
  min-width: 80px;
}

.time-value {
  color: #303133;
}
</style>

<style>
/* 搜索按钮蓝色背景 - 全局样式确保生效（仅查询表单内） */
.aided-student-page .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.aided-student-page .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.aided-student-page .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 - 全局样式 */
.aided-student-page .action-row .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page .action-row .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.aided-student-page .action-row .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}
</style>
