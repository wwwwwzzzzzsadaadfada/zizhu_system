<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card pending">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ pendingList.length }}</div>
              <div class="stats-label">待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card approved">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-success"></i>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ approvedList.filter(item => item.approvalStatus == 1).length }}</div>
              <div class="stats-label">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card rejected">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-error"></i>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ approvedList.filter(item => item.approvalStatus == 2).length }}</div>
              <div class="stats-label">已驳回</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card total">
          <div class="stats-content">
            <div class="stats-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ pendingList.length + approvedList.length }}</div>
              <div class="stats-label">总计</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Tab 切换 -->
    <el-card class="main-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="待审批" name="pending">
          <template slot="label">
            <span>待审批 <el-badge :value="pendingList.length" class="tab-badge" v-if="pendingList.length > 0"></el-badge></span>
          </template>

          <!-- 搜索和操作栏 -->
          <div class="toolbar">
            <div class="toolbar-left">
              <el-input
                v-model="pendingSearch"
                placeholder="搜索学生姓名、年级、项目名称"
                prefix-icon="el-icon-search"
                clearable
                style="width: 300px"
                @input="filterPendingList"
              />
              <el-button
                type="primary"
                icon="el-icon-s-operation"
                size="small"
                @click="openBatchApproveDialog"
                style="margin-left: 10px"
              >
                批量审批
              </el-button>
            </div>
            <div class="toolbar-right">
              <el-button
                icon="el-icon-refresh"
                size="small"
                @click="getList"
              >
                刷新
              </el-button>
            </div>
          </div>

          <!-- 待审批表格 -->
          <el-table
            v-loading="loading"
            :data="filteredPendingList"
            @selection-change="handlePendingSelectionChange"
            stripe
            border
            class="subsidy-table"
            style="margin-top: 15px"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="序号" type="index" width="60" align="center" />
            <el-table-column label="姓名" prop="studentName" width="100" align="center" show-overflow-tooltip>
              <template slot-scope="scope">
                <span :class="{ 'deleted-student': scope.row.studentName && scope.row.studentName.startsWith('已删除学生') }">
                  {{ scope.row.studentName || '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="年级" prop="gradeName" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.gradeName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="学年学期" width="120" align="center">
              <template slot-scope="scope">
                <span>{{ formatYearSemester(scope.row.schoolYear, scope.row.semester, scope.row.semesterLabel) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="补助类型" prop="subsidyType" width="120" align="center" show-overflow-tooltip />
            <el-table-column label="补助金额" width="110" align="center">
              <template slot-scope="scope">
                <span class="amount-text">￥{{ formatAmount(scope.row.subsidyAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="预算项目" prop="budgetProjectName" min-width="180" align="center" show-overflow-tooltip />
            <el-table-column label="经济分类" width="100" align="center">
              <template slot-scope="scope">
                <span>{{ getEconomyCategoryName(scope.row.economyCategory) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="学段" width="80" align="center">
              <template slot-scope="scope">
                {{ getSegmentName(scope.row.schoolingPlanId) }}
              </template>
            </el-table-column>
            <el-table-column label="创建时间" width="160" align="center">
              <template slot-scope="scope">
                {{ parseTime(scope.row.createdAt, '{y}-{m}-{d} {h}:{i}') }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  icon="el-icon-view"
                  @click="viewDetail(scope.row)"
                  class="action-btn"
                >
                  详情
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  icon="el-icon-check"
                  class="action-btn approve-btn"
                  @click="handleApprove(scope.row, 1)"
                >
                  通过
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  icon="el-icon-close"
                  class="action-btn reject-btn"
                  @click="handleReject(scope.row)"
                >
                  驳回
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="已审批" name="approved">
          <template slot="label">
            <span>已审批 <el-badge :value="approvedList.length" class="tab-badge" v-if="approvedList.length > 0"></el-badge></span>
          </template>

          <!-- 搜索栏 -->
          <div class="toolbar">
            <div class="toolbar-left">
              <el-input
                v-model="approvedSearch"
                placeholder="搜索学生姓名、年级、项目名称"
                prefix-icon="el-icon-search"
                clearable
                style="width: 300px"
                @input="filterApprovedList"
              />
            </div>
            <div class="toolbar-right">
              <el-button
                icon="el-icon-refresh"
                size="small"
                @click="getList"
              >
                刷新
              </el-button>
            </div>
          </div>

          <!-- 已审批表格 -->
          <el-table
            v-loading="loading"
            :data="filteredApprovedList"
            stripe
            border
            class="subsidy-table"
            style="margin-top: 15px"
          >
            <el-table-column label="序号" type="index" width="60" align="center" />
            <el-table-column label="姓名" prop="studentName" width="100" align="center" show-overflow-tooltip>
              <template slot-scope="scope">
                <span :class="{ 'deleted-student': scope.row.studentName && scope.row.studentName.startsWith('已删除学生') }">
                  {{ scope.row.studentName || '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="年级" prop="gradeName" width="80" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.gradeName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="学年学期" width="140" align="center">
              <template slot-scope="scope">
                <span>{{ formatYearSemester(scope.row.schoolYear, scope.row.semester, scope.row.semesterLabel) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="补助类型" prop="subsidyType" width="120" align="center" show-overflow-tooltip />
            <el-table-column label="补助金额" width="110" align="center">
              <template slot-scope="scope">
                <span class="amount-text">￥{{ formatAmount(scope.row.subsidyAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="预算项目" prop="budgetProjectName" min-width="180" align="center" show-overflow-tooltip />
            <el-table-column label="经济分类" width="100" align="center">
              <template slot-scope="scope">
                <span>{{ getEconomyCategoryName(scope.row.economyCategory) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="学段" width="80" align="center">
              <template slot-scope="scope">
                {{ getSegmentName(scope.row.schoolingPlanId) }}
              </template>
            </el-table-column>
            <el-table-column label="审批状态" width="100" align="center">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.sys_approval_status" :value="scope.row.approvalStatus" />
              </template>
            </el-table-column>
            <el-table-column label="审批人" prop="approver" width="100" align="center" />
            <el-table-column label="审批时间" width="160" align="center">
              <template slot-scope="scope">
                {{ parseTime(scope.row.approvalDate, '{y}-{m}-{d} {h}:{i}') }}
              </template>
            </el-table-column>
            <el-table-column label="审批意见" prop="approvalMemo" min-width="150" align="center" show-overflow-tooltip />
            <el-table-column label="发放状态" width="100" align="center">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.sys_payment_status" :value="scope.row.paymentStatus" />
              </template>
            </el-table-column>
            <el-table-column label="发放日期" width="120" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.paymentDate">{{ parseTime(scope.row.paymentDate, '{y}-{m}-{d}') }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="发放人" prop="paymentPerson" width="100" align="center" />
            <el-table-column label="操作" width="150" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  icon="el-icon-view"
                  @click="viewDetail(scope.row)"
                  class="action-btn"
                >
                  详情
                </el-button>
                <el-button
                  v-if="scope.row.approvalStatus == 1"
                  type="text"
                  size="small"
                  icon="el-icon-back"
                  @click="handleReturn(scope.row)"
                  class="action-btn return-btn"
                  style="color: #e6a23c;"
                >
                  退回
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog
      :title="approveTitle"
      :visible.sync="approveOpen"
      width="600px"
      append-to-body
    >
      <el-form
        ref="approveFormRef"
        :model="approveForm"
        label-width="100px"
      >
        <el-card class="detail-card" shadow="never">
          <div slot="header" class="detail-header">
            <i class="el-icon-user"></i>
            <span>学生信息</span>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名">{{ approveForm.studentName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="补助金额">
              <span style="color: #f56c6c; font-weight: 600;">￥{{ approveForm.subsidyAmount || '0.00' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="补助类型" :span="2">{{ approveForm.subsidyType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预算项目" :span="2">{{ approveForm.budgetProjectName || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-form-item
          label="审批意见"
          prop="approvalMemo"
          style="margin-top: 20px"
        >
          <el-input
            v-model="approveForm.approvalMemo"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveOpen = false">取 消</el-button>
        <el-button
          type="primary"
          @click="submitApprove"
          :loading="approving"
        >
          确 定
        </el-button>
      </div>
    </el-dialog>

    <!-- 退回对话框 -->
    <el-dialog
      title="退回补助"
      :visible.sync="returnOpen"
      width="600px"
      append-to-body
    >
      <el-form
        ref="returnFormRef"
        :model="returnForm"
        label-width="100px"
      >
        <el-alert
          title="提示"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <div>退回后，该补助记录的金额将回算到学期预算的可用金额中。</div>
        </el-alert>
        
        <el-card class="detail-card" shadow="never">
          <div slot="header" class="detail-header">
            <i class="el-icon-user"></i>
            <span>学生信息</span>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名">{{ returnForm.studentName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="补助金额">
              <span style="color: #f56c6c; font-weight: 600;">￥{{ returnForm.subsidyAmount || '0.00' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="补助类型" :span="2">{{ returnForm.subsidyType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预算项目" :span="2">{{ returnForm.budgetProjectName || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-form-item
          label="退回原因"
          prop="returnMemo"
          style="margin-top: 20px"
        >
          <el-input
            v-model="returnForm.returnMemo"
            type="textarea"
            :rows="4"
            placeholder="请输入退回原因（如：转学、其他原因等）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="returnOpen = false">取 消</el-button>
        <el-button
          type="warning"
          @click="submitReturn"
          :loading="returning"
        >
          确 定 退 回
        </el-button>
      </div>
    </el-dialog>

    <!-- 批量审批对话框 -->
    <el-dialog
      title="批量审批"
      :visible.sync="batchApproveDialogVisible"
      width="900px"
      append-to-body
      @closed="handleBatchApproveDialogClosed"
      class="batch-approve-dialog"
    >
      <!-- 筛选区域 -->
      <el-card class="filter-card" shadow="never">
        <div slot="header" class="filter-header">
          <i class="el-icon-search"></i>
          <span>筛选条件</span>
        </div>
        <div class="filter-content">
          <div class="filter-group">
            <div class="filter-label">困难等级：</div>
            <el-checkbox-group v-model="batchApproveFilter.difficultyLevelIds" @change="filterBatchApproveList" class="filter-checkbox-group">
              <el-checkbox
                v-for="dict in dict.type.sys_difficulty_level"
                :key="dict.value"
                :label="dict.value"
                class="filter-checkbox"
              >
                {{ dict.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
          <div class="filter-group">
            <div class="filter-label">学段：</div>
            <el-checkbox-group v-model="batchApproveFilter.schoolingPlanIds" @change="filterBatchApproveList" class="filter-checkbox-group">
              <el-checkbox
                v-for="item in schoolSegments"
                :key="item.value"
                :label="item.value"
                class="filter-checkbox"
              >
                {{ item.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
          <div class="filter-group">
            <div class="filter-label">经济分类：</div>
            <el-checkbox-group v-model="batchApproveFilter.economyCategories" @change="filterBatchApproveList" class="filter-checkbox-group">
              <el-checkbox
                v-for="dict in dict.type.sys_economy_category"
                :key="dict.value"
                :label="dict.value"
                class="filter-checkbox"
              >
                {{ dict.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
          <div class="filter-actions">
            <el-button type="text" size="small" icon="el-icon-refresh" @click="resetBatchApproveFilter">重置筛选</el-button>
          </div>
        </div>
      </el-card>

      <!-- 统计和操作栏 -->
      <div class="batch-approve-toolbar">
        <div class="toolbar-left">
          <span class="selected-count">
            <i class="el-icon-check"></i>
            已选择 <strong>{{ selectedBatchRecords.length }}</strong> 条记录
          </span>
          <el-button
            type="text"
            size="small"
            icon="el-icon-check"
            @click="selectAllBatchRecords"
            class="toolbar-btn"
          >
            全选
          </el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-close"
            @click="clearBatchSelection"
            class="toolbar-btn"
          >
            清空
          </el-button>
        </div>
        <div class="toolbar-right">
          <span class="total-count">共 {{ filteredBatchApproveList.length }} 条待审批记录</span>
        </div>
      </div>

      <!-- 学生列表 -->
      <el-table
        ref="batchApproveTable"
        v-loading="batchApproveLoading"
        :data="filteredBatchApproveList"
        @selection-change="handleBatchSelectionChange"
        stripe
        border
        max-height="420"
        class="batch-approve-table"
        :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: '600' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="70" align="center" />
        <el-table-column label="姓名" prop="studentName" width="120" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="student-name">{{ scope.row.studentName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="年级" prop="gradeName" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="small" type="info">{{ scope.row.gradeName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="补助类型" prop="subsidyType" min-width="150" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <span>{{ scope.row.subsidyType || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="补助金额" width="130" align="center">
          <template slot-scope="scope">
            <span class="subsidy-amount-badge">￥{{ formatAmount(scope.row.subsidyAmount) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 审批意见 -->
      <el-card class="memo-card" shadow="never" style="margin-top: 20px;">
        <div slot="header" class="memo-header">
          <i class="el-icon-edit"></i>
          <span>审批意见</span>
          <span class="memo-tip">（可选，将应用于所有选中的记录）</span>
        </div>
        <el-input
          v-model="batchApproveForm.approvalMemo"
          type="textarea"
          :rows="3"
          placeholder="请输入审批意见..."
          maxlength="500"
          show-word-limit
        />
      </el-card>

      <div slot="footer" class="batch-approve-footer">
        <el-button @click="batchApproveDialogVisible = false" size="medium">取 消</el-button>
        <el-button
          type="success"
          icon="el-icon-check"
          @click="submitBatchApprove(1)"
          :loading="batchApproving"
          :disabled="selectedBatchRecords.length === 0"
          size="medium"
          class="approve-btn"
        >
          批量通过
          <span v-if="selectedBatchRecords.length > 0" class="btn-count">({{ selectedBatchRecords.length }})</span>
        </el-button>
        <el-button
          type="danger"
          icon="el-icon-close"
          @click="submitBatchApprove(2)"
          :loading="batchApproving"
          :disabled="selectedBatchRecords.length === 0"
          size="medium"
          class="reject-btn"
        >
          批量驳回
          <span v-if="selectedBatchRecords.length > 0" class="btn-count">({{ selectedBatchRecords.length }})</span>
        </el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      title="补助记录详情"
      :visible.sync="detailDialogVisible"
      width="800px"
      append-to-body
    >
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="姓名">{{ currentRecord.studentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ currentRecord.gradeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="补助类型">{{ currentRecord.subsidyType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="补助金额">
          <span style="color: #f56c6c; font-weight: 600;">￥{{ formatAmount(currentRecord.subsidyAmount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="预算项目" :span="2">{{ currentRecord.budgetProjectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="经济分类">{{ getEconomyCategoryName(currentRecord.economyCategory) }}</el-descriptions-item>
        <el-descriptions-item label="学段">{{ getSegmentName(currentRecord.schoolingPlanId) }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <dict-tag :options="dict.type.sys_approval_status" :value="currentRecord.approvalStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="发放状态">
          <dict-tag :options="dict.type.sys_payment_status" :value="currentRecord.paymentStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="发放日期">
          {{ currentRecord.paymentDate ? parseTime(currentRecord.paymentDate, '{y}-{m}-{d}') : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="发放人">{{ currentRecord.paymentPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentRecord.approver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">
          {{ parseTime(currentRecord.approvalDate, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentRecord.approvalMemo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRecord.memo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(currentRecord.createdAt, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ parseTime(currentRecord.updatedAt, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSubsidyRecord, approveSubsidy, getSubsidyRecord, returnSubsidy } from "@/api/system/subsidyRecord";
import { getToken } from '@/utils/auth';

export default {
  name: "SubsidyRecord",
  dicts: ['sys_approval_status', 'sys_payment_status', 'sys_economy_category', 'sys_difficulty_level', 'sys_difficulty_type'],
  data() {
    return {
      // Tab 激活状态
      activeTab: 'pending',
      // 待审批列表
      pendingList: [],
      // 已审批列表
      approvedList: [],
      // 过滤后的待审批列表
      filteredPendingList: [],
      // 过滤后的已审批列表
      filteredApprovedList: [],
      // 待审批搜索关键词
      pendingSearch: '',
      // 已审批搜索关键词
      approvedSearch: '',
      // 选中的待审批记录
      selectedPending: [],
      // 审批对话框标题
      approveTitle: '',
      // 是否显示审批对话框
      approveOpen: false,
      // 审批中状态
      approving: false,
      // 审批表单
      approveForm: {},
      // 退回对话框
      returnOpen: false,
      // 退回中状态
      returning: false,
      // 退回表单
      returnForm: {},
      // 加载状态
      loading: true,
      // 详情对话框
      detailDialogVisible: false,
      currentRecord: null,
      // 学段数据
      schoolSegments: [
        { value: '1', label: '小学' },
        { value: '2', label: '初中' },
        { value: '3', label: '高中' }
      ],
      // 批量审批相关
      batchApproveDialogVisible: false,
      batchApproveLoading: false,
      batchApproving: false,
      batchApproveList: [], // 所有待审批记录
      filteredBatchApproveList: [], // 筛选后的待审批记录
      selectedBatchRecords: [], // 选中的记录
      batchApproveFilter: {
        difficultyLevelIds: [],
        schoolingPlanIds: [],
        economyCategories: []
      },
      batchApproveForm: {
        approvalMemo: ''
      }
    };
  },
  created() {
    this.getList();
  },
  mounted() {
    // 监听 Vuex 状态变化
    this.unsubscribe = this.$store.subscribe((mutation, state) => {
      if (mutation.type === 'subsidy/SET_SHOULD_REFRESH_SUBSIDY_LIST' &&
          mutation.payload === true) {
        this.getList();
        // 重置状态
        this.$store.dispatch('subsidy/setShouldRefreshSubsidyList', false);
      }
    });
  },
  beforeDestroy() {
    // 组件销毁前取消订阅
    if (this.unsubscribe) {
      this.unsubscribe();
    }
  },
  methods: {
    /** 获取列表数据 */
    getList() {
      this.loading = true;
      listSubsidyRecord({}).then(response => {
        // 分离待审批和已审批记录
        this.pendingList = response.rows.filter(item => item.approvalStatus == 0);
        this.approvedList = response.rows.filter(item => item.approvalStatus != 0);
        this.filteredPendingList = [...this.pendingList];
        this.filteredApprovedList = [...this.approvedList];
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },

    /** Tab 切换 */
    handleTabClick(tab) {
      if (tab.name === 'pending') {
        this.filteredPendingList = [...this.pendingList];
      } else {
        this.filteredApprovedList = [...this.approvedList];
      }
    },

    /** 过滤待审批列表 */
    filterPendingList() {
      if (!this.pendingSearch) {
        this.filteredPendingList = [...this.pendingList];
        return;
      }
      const keyword = this.pendingSearch.toLowerCase();
      this.filteredPendingList = this.pendingList.filter(item =>
        (item.studentName && item.studentName.toLowerCase().includes(keyword)) ||
        (item.gradeName && item.gradeName.toLowerCase().includes(keyword)) ||
        (item.budgetProjectName && item.budgetProjectName.toLowerCase().includes(keyword)) ||
        (item.schoolYear && item.schoolYear.includes(keyword))
      );
    },

    /** 过滤已审批列表 */
    filterApprovedList() {
      if (!this.approvedSearch) {
        this.filteredApprovedList = [...this.approvedList];
        return;
      }
      const keyword = this.approvedSearch.toLowerCase();
      this.filteredApprovedList = this.approvedList.filter(item =>
        (item.studentName && item.studentName.toLowerCase().includes(keyword)) ||
        (item.gradeName && item.gradeName.toLowerCase().includes(keyword)) ||
        (item.budgetProjectName && item.budgetProjectName.toLowerCase().includes(keyword)) ||
        (item.schoolYear && item.schoolYear.includes(keyword))
      );
    },

    /** 待审批选择变化 */
    handlePendingSelectionChange(selection) {
      this.selectedPending = selection;
    },

    /** 打开批量审批对话框 */
    openBatchApproveDialog() {
      this.batchApproveDialogVisible = true;
      this.batchApproveList = [...this.pendingList];
      this.filteredBatchApproveList = [...this.pendingList];
      this.resetBatchApproveFilter();
    },

    /** 关闭批量审批对话框 */
    handleBatchApproveDialogClosed() {
      this.selectedBatchRecords = [];
      this.batchApproveForm.approvalMemo = '';
      this.resetBatchApproveFilter();
    },

    /** 重置批量审批筛选条件 */
    resetBatchApproveFilter() {
      this.batchApproveFilter = {
        difficultyLevelIds: [],
        schoolingPlanIds: [],
        economyCategories: []
      };
      this.filterBatchApproveList();
    },

    /** 筛选批量审批列表 */
    filterBatchApproveList() {
      let filtered = [...this.batchApproveList];

      // 按困难等级筛选（多选）
      if (this.batchApproveFilter.difficultyLevelIds && this.batchApproveFilter.difficultyLevelIds.length > 0) {
        filtered = filtered.filter(item =>
          this.batchApproveFilter.difficultyLevelIds.includes(String(item.difficultyLevelId))
        );
      }

      // 按学段筛选（多选）
      if (this.batchApproveFilter.schoolingPlanIds && this.batchApproveFilter.schoolingPlanIds.length > 0) {
        filtered = filtered.filter(item =>
          this.batchApproveFilter.schoolingPlanIds.includes(String(item.schoolingPlanId))
        );
      }

      // 按经济分类筛选（多选）
      if (this.batchApproveFilter.economyCategories && this.batchApproveFilter.economyCategories.length > 0) {
        filtered = filtered.filter(item =>
          this.batchApproveFilter.economyCategories.includes(String(item.economyCategory))
        );
      }

      this.filteredBatchApproveList = filtered;
    },

    /** 批量审批选择变化 */
    handleBatchSelectionChange(selection) {
      this.selectedBatchRecords = selection;
    },

    /** 全选批量审批记录 */
    selectAllBatchRecords() {
      this.$refs.batchApproveTable && this.$refs.batchApproveTable.toggleAllSelection();
    },

    /** 清空批量审批选择 */
    clearBatchSelection() {
      this.$refs.batchApproveTable && this.$refs.batchApproveTable.clearSelection();
    },

    /** 提交批量审批 */
    submitBatchApprove(status) {
      if (this.selectedBatchRecords.length === 0) {
        this.$modal.msgWarning('请先选择要审批的记录');
        return;
      }

      const action = status === 1 ? '通过' : '驳回';
      this.$modal.confirm(`确定要${action}选中的${this.selectedBatchRecords.length}条记录吗？`).then(() => {
        this.batchApproving = true;
        const promises = this.selectedBatchRecords.map(record =>
          approveSubsidy(record.id, status, this.batchApproveForm.approvalMemo || '')
        );

        Promise.all(promises).then(() => {
          this.$modal.msgSuccess(`批量${action}成功`);
          this.batchApproveDialogVisible = false;
          this.getList();
        }).catch(() => {
          this.$modal.msgError(`批量${action}失败`);
        }).finally(() => {
          this.batchApproving = false;
        });
      });
    },

    /** 处理审批 */
    handleApprove(row, status) {
      this.approveForm = {
        id: row.id,
        studentName: row.studentName,
        subsidyAmount: parseFloat(row.subsidyAmount || 0).toFixed(2),
        subsidyType: row.subsidyType,
        budgetProjectName: row.budgetProjectName,
        approvalStatus: status,
        approvalMemo: ''
      };
      this.approveTitle = "审批通过";
      this.approveOpen = true;
    },

    /** 处理驳回 */
    handleReject(row) {
      this.approveForm = {
        id: row.id,
        studentName: row.studentName,
        subsidyAmount: parseFloat(row.subsidyAmount || 0).toFixed(2),
        subsidyType: row.subsidyType,
        budgetProjectName: row.budgetProjectName,
        approvalStatus: 2,
        approvalMemo: ''
      };
      this.approveTitle = "审批驳回";
      this.approveOpen = true;
    },

    /** 处理退回 */
    handleReturn(row) {
      this.returnForm = {
        id: row.id,
        studentName: row.studentName,
        subsidyAmount: parseFloat(row.subsidyAmount || 0).toFixed(2),
        subsidyType: row.subsidyType,
        budgetProjectName: row.budgetProjectName,
        returnMemo: ''
      };
      this.returnOpen = true;
    },

    /** 提交退回 */
    submitReturn() {
      if (!this.returnForm.returnMemo || this.returnForm.returnMemo.trim() === '') {
        this.$modal.msgWarning('请输入退回原因');
        return;
      }
      
      this.$modal.confirm('确定要退回该补助记录吗？退回后金额将回算到预算可用金额中。').then(() => {
        this.returning = true;
        returnSubsidy(
          this.returnForm.id,
          this.returnForm.returnMemo || ''
        ).then(() => {
          this.$modal.msgSuccess('退回成功');
          this.returnOpen = false;
          this.returning = false;
          this.getList();
          // 通知学期预算页面刷新
          this.$store.dispatch('subsidy/setShouldRefreshBudgetList', true);
        }).catch(() => {
          this.$modal.msgError('退回失败');
          this.returning = false;
        });
      }).catch(() => {
        // 用户取消
      });
    },

    /** 提交审批 */
    submitApprove() {
      this.approving = true;
      approveSubsidy(
        this.approveForm.id,
        this.approveForm.approvalStatus,
        this.approveForm.approvalMemo || ''
      ).then(() => {
        this.$modal.msgSuccess('审批成功');
        this.approveOpen = false;
        this.approving = false;
        this.getList();
      }).catch(() => {
        this.$modal.msgError('审批失败');
        this.approving = false;
      });
    },

    /** 查看详情 */
    viewDetail(row) {
      this.currentRecord = { ...row };
      this.detailDialogVisible = true;
    },

    /** 获取学段名称 */
    getSegmentName(segmentId) {
      if (!segmentId) return '-';
      const segment = this.schoolSegments.find(item => item.value == segmentId);
      return segment ? segment.label : '-';
    },

    /** 获取困难类型名称 */
    getDifficultyTypeName(difficultyTypeId) {
      if (!difficultyTypeId) return '-';
      const dict = this.dict.type.sys_difficulty_type.find(item => String(item.value) === String(difficultyTypeId));
      return dict ? dict.label : '-';
    },

    /** 获取困难等级名称 */
    getDifficultyLevelName(difficultyLevelId) {
      if (!difficultyLevelId) return '-';
      const dict = this.dict.type.sys_difficulty_level.find(item => String(item.value) === String(difficultyLevelId));
      return dict ? dict.label : '-';
    },

    /** 获取经济分类名称 */
    getEconomyCategoryName(categoryId) {
      if (!categoryId) return '-';
      const dict = this.dict.type.sys_economy_category.find(item => item.value === categoryId);
      return dict ? dict.label : categoryId;
    },

    /** 格式化金额 */
    formatAmount(amount) {
      if (!amount) return '0.00';
      return parseFloat(amount).toFixed(2);
    },

    /** 格式化学年学期 */
    formatYearSemester(schoolYear, semester, semesterLabel) {
      if (!schoolYear) return '-';
      // 如果后端提供了学期标签，则直接使用
      if (semesterLabel) {
        return `${schoolYear} ${semesterLabel}`;
      }
    },
    
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stats-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stats-card.pending {
  border-left: 4px solid #409EFF;
}

.stats-card.approved {
  border-left: 4px solid #67c23a;
}

.stats-card.rejected {
  border-left: 4px solid #f56c6c;
}

.stats-card.total {
  border-left: 4px solid #909399;
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stats-icon {
  font-size: 40px;
  margin-right: 20px;
  opacity: 0.8;
}

.stats-card.pending .stats-icon {
  color: #409EFF;
}

.stats-card.approved .stats-icon {
  color: #67c23a;
}

.stats-card.rejected .stats-icon {
  color: #f56c6c;
}

.stats-card.total .stats-icon {
  color: #909399;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

/* 主卡片 */
.main-card {
  border-radius: 8px;
}

/* Tab 徽章 */
.tab-badge {
  margin-left: 5px;
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

/* 详情卡片 */
.detail-card {
  margin-bottom: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.detail-header i {
  margin-right: 8px;
  font-size: 18px;
  color: #409EFF;
}

/* 表格样式优化 */
.subsidy-table {
  font-size: 14px;
}

.subsidy-table ::v-deep .el-table__header-wrapper th {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
  text-align: center;
}

.subsidy-table ::v-deep .el-table__body-wrapper td {
  text-align: center;
}

.subsidy-table ::v-deep .el-table__row:hover {
  background-color: #f5f7fa;
}

/* 金额样式 */
.amount-text {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
}

/* 操作按钮样式 */
.action-btn {
  padding: 5px 8px;
  margin: 0 2px;
  font-size: 13px;
}

.approve-btn {
  color: #67c23a;
}

.approve-btn:hover {
  color: #85ce61;
}

.reject-btn {
  color: #f56c6c;
}

.reject-btn:hover {
  color: #f78989;
}

/* 批量审批对话框样式 */
.batch-approve-dialog ::v-deep .el-dialog__body {
  padding: 20px;
}

.batch-approve-dialog ::v-deep .el-dialog__header {
  padding: 20px 20px 15px;
  border-bottom: 1px solid #e4e7ed;
}

.batch-approve-dialog ::v-deep .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.filter-card ::v-deep .el-card__body {
  padding: 15px;
}

.filter-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.filter-header i {
  margin-right: 8px;
  font-size: 16px;
  color: #409EFF;
}

/* 筛选内容 */
.filter-content {
  padding: 10px 0;
}

.filter-group {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-group:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.filter-label {
  min-width: 90px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
  padding-top: 8px;
  flex-shrink: 0;
}

.filter-checkbox-group {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-checkbox {
  margin-right: 0 !important;
  margin-bottom: 0 !important;
}

.filter-checkbox ::v-deep .el-checkbox__input {
  border-radius: 4px;
}

.filter-checkbox ::v-deep .el-checkbox__label {
  padding-left: 8px;
  font-size: 14px;
  color: #606266;
}

.filter-actions {
  text-align: right;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  margin-top: 10px;
}

/* 工具栏 */
.batch-approve-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 10px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-count {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
  padding: 6px 12px;
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
}

.selected-count i {
  margin-right: 6px;
  color: #409EFF;
}

.selected-count strong {
  color: #409EFF;
  font-weight: 600;
  margin: 0 4px;
}

.toolbar-btn {
  padding: 6px 12px;
  color: #606266;
}

.toolbar-btn:hover {
  color: #409EFF;
}

.toolbar-right {
  font-size: 13px;
  color: #909399;
}

.total-count {
  padding: 6px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

/* 表格样式 */
.batch-approve-table {
  border-radius: 6px;
  overflow: hidden;
}

.batch-approve-table ::v-deep .el-table__header-wrapper {
  background: #f5f7fa;
}

.batch-approve-table ::v-deep .el-table__row:hover {
  background-color: #f5f7fa;
}

.student-name {
  font-weight: 500;
  color: #303133;
}

.subsidy-amount-badge {
  display: inline-block;
  padding: 4px 10px;
  background: #fef0f0;
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #fbc4c4;
}

/* 审批意见卡片 */
.memo-card {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.memo-card ::v-deep .el-card__body {
  padding: 15px;
}

.memo-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.memo-header i {
  margin-right: 8px;
  font-size: 16px;
  color: #409EFF;
}

.memo-tip {
  margin-left: 8px;
  font-size: 12px;
  font-weight: normal;
  color: #909399;
}

/* 底部按钮 */
.batch-approve-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 10px;
  border-top: 1px solid #e4e7ed;
}

.batch-approve-footer .el-button {
  min-width: 100px;
}

.batch-approve-footer .approve-btn {
  background: #67c23a !important;
  border-color: #67c23a !important;
  color: #ffffff !important;
}

.batch-approve-footer .approve-btn:hover {
  background: #85ce61 !important;
  border-color: #85ce61 !important;
  color: #ffffff !important;
}

.batch-approve-footer .approve-btn:disabled {
  background: #c2e7b0 !important;
  border-color: #c2e7b0 !important;
  color: #ffffff !important;
}

.batch-approve-footer .reject-btn {
  background: #f56c6c !important;
  border-color: #f56c6c !important;
  color: #ffffff !important;
}

.batch-approve-footer .reject-btn:hover {
  background: #f78989 !important;
  border-color: #f78989 !important;
  color: #ffffff !important;
}

.batch-approve-footer .reject-btn:disabled {
  background: #fbc4c4 !important;
  border-color: #fbc4c4 !important;
  color: #ffffff !important;
}

.btn-count {
  margin-left: 4px;
  font-weight: 600;
  color: #ffffff !important;
}

/* 已删除学生样式 */
.deleted-student {
  color: #f56c6c;
  font-style: italic;
}

/* 响应式 */
@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: 15px;
  }

  .toolbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar-left,
  .toolbar-right {
    width: 100%;
    margin-bottom: 10px;
  }

  .batch-approve-toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .batch-approve-filter-form ::v-deep .el-form-item {
    width: 100%;
    margin-right: 0;
  }
}
</style>
