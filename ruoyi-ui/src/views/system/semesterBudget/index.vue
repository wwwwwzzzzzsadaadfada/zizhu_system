<template>
  <div class="app-container semester-budget-page">
    <!-- 顶部统计卡片区域：一体化面板 + 毛玻璃效果 -->
    <div class="budget-stats-wrapper">
      <el-row :gutter="20" class="budget-stats">
      <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-primary">
            <div class="stat-header">
              <div class="stat-label">
                <img src="@/assets/images/zb/2.png" alt="预算总额" class="stat-icon" />
                <span>预算总额(元)</span>
              </div>
            </div>
            <div class="stat-body">
          <div class="stat-value primary">{{ formatAmount(statistics.totalBudget) }}</div>
            </div>
        </el-card>
      </el-col>
      <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-warn">
            <div class="stat-header">
              <div class="stat-label">
                <img src="@/assets/images/zb/1.png" alt="已使用" class="stat-icon" />
                <span>已使用(元)</span>
              </div>
            </div>
            <div class="stat-body">
          <div class="stat-value warn">{{ formatAmount(statistics.usedAmount) }}</div>
            </div>
        </el-card>
      </el-col>
      <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-info">
            <div class="stat-header">
              <div class="stat-label">
                <img src="@/assets/images/zb/4.png" alt="锁定中" class="stat-icon" />
                <span>锁定中(元)</span>
              </div>
            </div>
            <div class="stat-body">
          <div class="stat-value info">{{ formatAmount(statistics.lockedAmount) }}</div>
            </div>
        </el-card>
      </el-col>
      <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-card-success">
            <div class="stat-header">
              <div class="stat-label">
                <img src="@/assets/images/zb/3.png" alt="可用余额" class="stat-icon" />
                <span>可用余额(元)</span>
              </div>
            </div>
            <div class="stat-body">
          <div class="stat-value success">{{ formatAmount(statistics.availableAmount) }}</div>
            </div>
        </el-card>
      </el-col>
    </el-row>
    </div>

    <!-- 查询表单、操作栏、表格 合并毛玻璃容器 -->
    <div class="query-table-wrapper">
      <!-- 操作栏 -->
      <el-row :gutter="10" class="mb8 action-toolbar">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:semesterBudget:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

      <!-- 表格筛选行 -->
      <div v-show="showSearch" class="table-filter-bar">
        <el-form :inline="true" size="small">
          <el-form-item>
            <el-input
              v-model="queryParams.id"
              placeholder="预算编号"
              clearable
              style="width: 120px"
              @keyup.enter.native="handleQuery"
              @clear="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryParams.functionCategory"
              placeholder="功能分类"
              clearable
              filterable
              style="width: 140px"
              @change="handleQuery"
              @clear="handleQuery"
            >
              <el-option
                v-for="dict in dict.type.sys_function_category"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="queryParams.budgetProjectName"
              placeholder="预算项目"
              clearable
              style="width: 160px"
              @keyup.enter.native="handleQuery"
              @clear="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryParams.yearSemesterId"
              placeholder="学年/学期"
              clearable
              filterable
              style="width: 160px"
              @change="handleQuery"
              @clear="handleQuery"
            >
              <el-option
                v-for="item in yearSemesters"
                :key="item.id"
                :label="`${item.schoolYear} ${item.semesterLabel || ''}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryParams.economyCategory"
              placeholder="经济分类"
              clearable
              filterable
              style="width: 140px"
              @change="handleQuery"
              @clear="handleQuery"
            >
              <el-option
                v-for="dict in dict.type.sys_economy_category"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="queryParams.status"
              placeholder="状态"
              clearable
              filterable
              style="width: 120px"
              @change="handleQuery"
              @clear="handleQuery"
            >
              <el-option
                v-for="dict in dict.type.sys_budget_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-section">
    <el-table
          class="budget-table"
      v-loading="loading"
      :data="budgetList"
      :span-method="semesterBudgetSpanMethod"
    >
      <el-table-column label="预算编号" prop="id" width="120" align="center" />
      <el-table-column
        label="功能分类"
        prop="functionCategory"
        min-width="150"
        align="center"
        class-name="function-category-col"
      >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_function_category" :value="scope.row.functionCategory" />
        </template>
      </el-table-column>
      <el-table-column label="预算项目" prop="budgetProjectName" min-width="180" show-overflow-tooltip />
      <el-table-column label="关联指标" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link
            v-if="scope.row.quotaId && scope.row.quotaDocNo"
            type="primary"
            @click.native.prevent="openQuotaDetail(scope.row)"
          >
            {{ scope.row.quotaDocNo }}
          </el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="指标来源" prop="quotaSourceType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_quota_source_type" :value="scope.row.quotaSourceType" />
        </template>
      </el-table-column>
      <el-table-column label="学年/学期" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.schoolYear || '-' }} {{ scope.row.semesterLabel || '' }}
        </template>
      </el-table-column>
      <el-table-column label="经济分类" prop="economyCategory" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_economy_category" :value="scope.row.economyCategory" />
        </template>
      </el-table-column>
      <el-table-column label="预算类型" prop="budgetType" min-width="120" />
      <el-table-column label="预算总额(元)" prop="budgetAmount" width="140">
        <template slot-scope="scope">
          <span class="budget-amount-wrapper">
            {{ formatAmount(scope.row.budgetAmount) }}
            <el-tag
              v-if="scope.row.carryOverFlag"
              size="mini"
              type="warning"
              class="carry-over-tag carry-over-badge"
            >结</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="已使用(元)" prop="usedAmount" width="140">
        <template slot-scope="scope">
          {{ formatAmount(scope.row.usedAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="锁定(元)" prop="lockedAmount" width="140">
        <template slot-scope="scope">
          {{ formatAmount(scope.row.lockedAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="可用余额(元)" prop="availableAmount" width="140">
        <template slot-scope="scope">
          {{ formatAmount(scope.row.availableAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_budget_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="170">
        <template slot-scope="scope">
          {{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150" align="center">
        <template slot-scope="scope">
          <el-select
            v-model="scope.row.status"
            size="mini"
            :disabled="isStatusDisabled(scope.row)"
            @change="handleStatusChange(scope.row)"
            style="width: 100px"
            v-hasPermi="['system:semesterBudget:edit']"
          >
            <el-option
              v-for="dict in dict.type.sys_budget_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            />
          </el-select>
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

    <!-- 指标详情抽屉 -->
    <el-drawer
      title="指标详情"
      :visible.sync="quotaDetailVisible"
      size="55%"
      append-to-body
      class="quota-detail-drawer"
    >
      <div v-if="quotaDetailRecord" class="quota-detail-content">
        <!-- 基本信息卡片 -->
        <el-card class="detail-info-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-document"></i>
            <span>基本信息</span>
      </div>
          <el-descriptions :column="2" border size="medium" class="detail-descriptions">
            <el-descriptions-item label="指标文号" :span="1">
              <span class="info-value">{{ quotaDetailRecord.quotaDocNo || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="学年" :span="1">
              <el-tag size="small" type="info">{{ quotaDetailRecord.schoolYear || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="学期" :span="1">
              <el-tag size="small" type="info">{{ quotaDetailRecord.semesterLabel || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预算项目" :span="2">
              <span class="info-value">{{ quotaDetailRecord.budgetProjectName || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="功能分类" :span="1">
              <dict-tag :options="dict.type.sys_function_category" :value="quotaDetailRecord.functionCategory" />
            </el-descriptions-item>
            <el-descriptions-item label="预算级次" :span="1">
              <dict-tag :options="dict.type.sys_budget_level" :value="quotaDetailRecord.budgetLevel" />
            </el-descriptions-item>
            <el-descriptions-item label="指标来源" :span="1">
              <dict-tag :options="dict.type.sys_quota_source_type" :value="quotaDetailRecord.quotaSourceType" />
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">
              <span class="info-value">{{ quotaDetailRecord.memo || '-' }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 经济分类明细卡片 -->
        <el-card class="detail-table-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-s-grid"></i>
            <span>经济分类明细</span>
            <span class="detail-count">（共 {{ (quotaDetailRecord.detailList || []).length }} 项）</span>
          </div>
          <el-table
            :data="(quotaDetailRecord && quotaDetailRecord.detailList) || []"
            border
            stripe
            class="detail-table"
            :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: '600' }"
          >
            <el-table-column label="序号" type="index" width="60" align="center" />
            <el-table-column label="经济分类" min-width="140" align="center">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.sys_economy_category" :value="scope.row.economyCategory" />
              </template>
            </el-table-column>
            <el-table-column label="总金额(元)" min-width="130" align="right">
              <template slot-scope="scope">
                <span class="amount-text">{{ formatAmount(scope.row.totalAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="已分配(元)" min-width="130" align="right">
              <template slot-scope="scope">
                <span class="amount-text allocated">{{ formatAmount(scope.row.allocatedAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="剩余(元)" min-width="130" align="right">
              <template slot-scope="scope">
                <span class="amount-text available">
                  {{ formatAmount(scope.row.availableAmount != null ? scope.row.availableAmount : scope.row.totalAmount - scope.row.allocatedAmount) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="memo" min-width="150" align="center" show-overflow-tooltip>
              <template slot-scope="scope">
                <span class="memo-text">{{ scope.row.memo || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 指标附件卡片 -->
        <el-card class="detail-attachment-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-paperclip"></i>
            <span>指标附件</span>
            <span class="detail-count">（共 {{ (quotaDetailAttachmentList || []).length }} 个）</span>
          </div>
          <div v-if="quotaDetailAttachmentList && quotaDetailAttachmentList.length > 0" class="attachment-list">
            <div v-for="(file, index) in quotaDetailAttachmentList" :key="file.id || index" class="attachment-item">
              <i class="el-icon-document"></i>
              <span class="file-name" :title="file.name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
              <div class="actions">
                <el-button type="text" size="small" @click="handleQuotaAttachmentPreview(file)">预览</el-button>
                <el-button type="text" size="small" @click="handleQuotaAttachmentDownload(file)">下载</el-button>
              </div>
            </div>
          </div>
          <div v-else class="no-attachment">
            <i class="el-icon-document-delete"></i>
            <span>暂无附件</span>
          </div>
        </el-card>
      </div>
    </el-drawer>

    <!-- 文件预览对话框 -->
    <el-dialog
      title="文件预览"
      :visible.sync="previewDialogVisible"
      width="80%"
      append-to-body
      class="preview-dialog"
    >
      <div v-if="previewData" class="preview-content">
        <!-- PDF预览 -->
        <div v-if="previewData.previewType === 'pdf' && previewData.imageUrls" class="pdf-preview">
          <div v-for="(imageUrl, index) in previewData.imageUrls" :key="index" class="pdf-page">
            <img :src="getImageUrl(imageUrl)" :alt="'第' + (index + 1) + '页'" />
            <div class="page-number">第 {{ index + 1 }} 页</div>
          </div>
        </div>
        <!-- 图片预览 -->
        <div v-else-if="previewData.previewType === 'image'" class="image-preview">
          <img :src="getImageUrl(previewData.imageUrl)" :alt="previewData.fileName" />
        </div>
        <!-- HTML预览 -->
        <div v-else-if="previewData.previewType === 'html'" class="html-preview">
          <iframe :srcdoc="previewData.htmlContent" frameborder="0" style="width: 100%; height: 600px;"></iframe>
        </div>
        <!-- 文本预览 -->
        <div v-else-if="previewData.previewType === 'text'" class="text-preview">
          <pre>{{ previewData.textContent }}</pre>
        </div>
        <!-- 不支持预览 -->
        <div v-else class="unsupported-preview">
          <el-alert
            title="不支持预览此文件类型"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  listSemesterBudget,
  changeSemesterBudgetStatus,
  budgetStatistics
} from '@/api/system/semesterBudget'
import { listYearSemesters } from '@/api/system/yearSemester'
import { getSubsidyQuota } from '@/api/system/subsidyQuota'
import {
  listQuotaAttachmentByQuotaId,
  downloadQuotaAttachment,
  previewQuotaAttachment,
  previewQuotaAttachmentAsync
} from '@/api/system/quotaAttachment'

export default {
  name: 'SemesterBudget',
  dicts: ['sys_budget_status', 'sys_economy_category', 'sys_quota_source_type', 'sys_function_category', 'sys_budget_source', 'sys_budget_level'],
  data() {
    return {
      loading: false,
      showSearch: true,
      queryFormCollapsed: false, // 查询表单折叠状态
      budgetList: [],
      total: 0,
      yearSemesters: [],
      statistics: {
        totalBudget: 0,
        usedAmount: 0,
        lockedAmount: 0,
        availableAmount: 0
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: undefined,
        yearSemesterId: undefined,
        budgetProjectName: undefined,
        functionCategory: undefined,
        economyCategory: undefined,
        status: undefined
      },
      quotaDetailVisible: false,
      quotaDetailRecord: null,
      quotaDetailAttachmentList: [], // 指标详情附件列表
      previewDialogVisible: false, // 预览对话框
      previewData: null // 预览数据
    }
  },
  created() {
    this.getList()
    this.fetchYearSemesters()
  },
  mounted() {
    // 监听预算刷新通知
    this.unsubscribe = this.$store.subscribe((mutation, state) => {
      if (mutation.type === 'subsidy/SET_SHOULD_REFRESH_BUDGET_LIST' &&
          mutation.payload === true) {
        this.getList()
        // 重置状态
        this.$store.dispatch('subsidy/setShouldRefreshBudgetList', false)
      }
    })
  },
  beforeDestroy() {
    // 组件销毁前取消订阅
    if (this.unsubscribe) {
      this.unsubscribe()
    }
  },
  methods: {
    /** 判断状态是否可修改（已使用资金时不可修改） */
    isStatusDisabled(row) {
      const usedAmount = parseFloat(row.usedAmount || 0)
      return usedAmount > 0
    },
    /**
     * 合并相同功能分类（functionCategory）列的单元格
     * 规则：按当前页面顺序，将相同功能分类的连续行合并
     */
    semesterBudgetSpanMethod({ row, column, rowIndex }) {
      if (!column || column.property !== 'functionCategory') {
        return { rowspan: 1, colspan: 1 }
      }

      const list = this.budgetList || []
      if (!list.length || !row) {
        return { rowspan: 1, colspan: 1 }
      }

      const current = row.functionCategory

      // 没有功能分类时，不合并
      if (current === undefined || current === null || current === '') {
        return { rowspan: 1, colspan: 1 }
      }

      const prev = rowIndex > 0 && list[rowIndex - 1] ? list[rowIndex - 1].functionCategory : null

      // 不是组的第一个元素，直接隐藏（rowspan=0）
      if (prev === current) {
        return { rowspan: 0, colspan: 0 }
      }

      // 计算从当前行开始的连续相同功能分类的数量
      let rowspan = 1
      for (let i = rowIndex + 1; i < list.length; i++) {
        if (list[i].functionCategory === current) {
          rowspan++
        } else {
          break
        }
      }

      return { rowspan, colspan: 1 }
    },
    /** 处理状态变更 */
    handleStatusChange(row) {
      const originalStatus = row.status
      const statusText = this.getStatusLabel(row.status)
      this.$modal.confirm(`确定要将该预算状态修改为"${statusText}"吗？`).then(() => {
        return changeSemesterBudgetStatus(row.id, row.status)
      }).then(() => {
        this.$modal.msgSuccess('状态更新成功')
        this.getList()
      }).catch((error) => {
        // 如果取消或失败，恢复原状态并显示错误信息
        row.status = originalStatus
        if (error && error.message) {
          this.$modal.msgError(error.message)
        }
        this.getList()
      })
    },
    /** 获取状态标签 */
    getStatusLabel(status) {
      if (!this.dict.type.sys_budget_status) return status
      const dict = this.dict.type.sys_budget_status.find(item => parseInt(item.value) === status)
      return dict ? dict.label : status
    },
    fetchYearSemesters() {
      listYearSemesters({ pageNum: 1, pageSize: 999 }).then(res => {
        this.yearSemesters = res.rows || []
      }).catch(err => {
        console.error('学年学期数据加载失败:', err)
        this.yearSemesters = []
      })
    },
    getList() {
      this.loading = true
      listSemesterBudget(this.queryParams).then(res => {
        this.budgetList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
      this.loadStatistics()
    },
    loadStatistics() {
      budgetStatistics(this.queryParams).then(res => {
        this.statistics = res.data || this.statistics
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleExport() {
      this.download('system/semesterBudget/export', { ...this.queryParams }, `semester_budget_${new Date().getTime()}.xlsx`)
    },
    formatAmount(val) {
      const num = Number(val || 0)
      return num.toFixed(2)
    },
    /** 打开指标详情 */
    openQuotaDetail(row) {
      if (!row || !row.quotaId) {
        this.$modal.msgWarning('该预算未关联指标')
        return
      }
      this.quotaDetailVisible = true
      this.quotaDetailRecord = null
      this.quotaDetailAttachmentList = []
      getSubsidyQuota(row.quotaId).then(res => {
        this.quotaDetailRecord = res.data || null
        // 加载附件列表
        this.loadQuotaDetailAttachments(row.quotaId)
      }).catch(err => {
        console.error('获取指标详情失败:', err)
        this.$modal.msgError('获取指标详情失败')
        this.quotaDetailVisible = false
      })
    },
    // 加载指标详情附件
    loadQuotaDetailAttachments(quotaId) {
      if (!quotaId) {
        this.quotaDetailAttachmentList = []
        return
      }
      listQuotaAttachmentByQuotaId(quotaId).then(res => {
        if (res.code === 200 && res.data) {
          this.quotaDetailAttachmentList = (res.data || []).map(item => ({
            id: item.id,
            name: item.fileName,
            size: item.fileSize,
            url: item.fileUrl,
            uid: item.id
          }))
        } else {
          this.quotaDetailAttachmentList = []
        }
      }).catch(err => {
        console.error('加载指标详情附件失败:', err)
        this.quotaDetailAttachmentList = []
      })
    },
    // 预览指标附件
    handleQuotaAttachmentPreview(file) {
      if (!file.id) {
        this.$modal.msgWarning('文件未上传成功，无法预览')
          return
        }
      this.$modal.loading('正在加载预览...')
      previewQuotaAttachment(file.id).then(res => {
        this.$modal.closeLoading()
        if (res.code === 200 && res.data) {
          this.previewData = res.data
          this.previewDialogVisible = true
        } else {
          // 如果同步预览失败，尝试异步预览
          this.$modal.msgWarning('文件较大，正在异步处理，请稍后...')
          setTimeout(() => {
            previewQuotaAttachmentAsync(file.id).then(asyncRes => {
              if (asyncRes.code === 200 && asyncRes.data) {
                this.previewData = asyncRes.data
                this.previewDialogVisible = true
              } else {
                this.$modal.msgError('预览失败：' + (asyncRes.msg || '未知错误'))
              }
            }).catch(err => {
              this.$modal.msgError('预览失败：' + (err.msg || err))
            })
          }, 2000)
        }
      }).catch(err => {
        this.$modal.closeLoading()
        this.$modal.msgError('预览失败：' + (err.msg || err))
      })
    },
    // 下载指标附件
    handleQuotaAttachmentDownload(file) {
      if (!file.id) {
        this.$modal.msgWarning('文件未上传成功，无法下载')
        return
      }
      downloadQuotaAttachment(file.id).then(res => {
        const blob = new Blob([res])
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = file.name
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
        this.$modal.msgSuccess('下载成功')
      }).catch(err => {
        this.$modal.msgError('下载失败：' + (err.msg || err))
      })
    },
    // 格式化文件大小
    formatFileSize(size) {
      if (!size) return '0 B'
      if (size < 1024) return size + ' B'
      if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
      if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + ' MB'
      return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
    },
    // 获取基础URL
    getBaseURL() {
      return process.env.VUE_APP_BASE_API || ''
    },
    // 获取图片完整URL
    getImageUrl(imageUrl) {
      if (!imageUrl) return ''
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl
      }
      return this.getBaseURL() + imageUrl
    }
  }
}
</script>

<style scoped>
.semester-budget-page {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

.semester-budget-page ::v-deep button,
.semester-budget-page ::v-deep .el-button {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

/* 搜索按钮使用蓝色背景 - 仅针对查询表单中的搜索按钮 */
.semester-budget-page ::v-deep .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.semester-budget-page ::v-deep .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.semester-budget-page ::v-deep .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 */
.semester-budget-page ::v-deep .action-toolbar .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page ::v-deep .action-toolbar .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page ::v-deep .action-toolbar .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 统一功能按钮样式：白色背景、浅灰色边框、黑色字体（排除 primary 按钮和文本按钮） */
.semester-budget-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 覆盖 plain 按钮样式（排除 primary 按钮） */
.semester-budget-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.budget-stats-wrapper {
  /* 玻璃面板外壳：纯毛玻璃效果 */
  margin-bottom: 8px;
  padding: 14px 16px 10px;
  border-radius: 16px;
  background-color: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.budget-stats {
  margin-bottom: 0;
}

/* 统计卡片：淡玻璃 / 毛玻璃效果 */
.stat-card {
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.65);
  background-color: rgba(255, 255, 255, 0.82);
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.12);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  transition: box-shadow 0.2s ease, transform 0.2s ease, border-color 0.2s ease;
}

.stat-card :deep(.el-card__body) {
  position: relative;
  z-index: 1;
  padding: 10px 14px;
}

/* 头部：信息行 */
.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  position: relative;
  padding-top: 0;
}

/* 内容区域 */
.stat-body {
  padding-top: 0;
}

.stat-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.22);
  border-color: rgba(255, 255, 255, 0.9);
}

.stat-label {
  color: #606266;
  font-size: 22px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-label .stat-icon {
  width: 44px;
  height: 44px;
  padding: 6px;
  border-radius: 50%;
  background-color: #f5f7fa;
  object-fit: contain;
  flex-shrink: 0;
  box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
  /* 兼容图标字体 */
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #606266;
}

.stat-label .stat-icon:hover {
  transform: scale(1.08);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  background-color: #edf4ff;
}

.stat-label span {
  flex: 1;
  line-height: 1.5;
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
  margin-top: 4px;
  margin-left: 56px; /* 图标44px + gap 12px，与标题文字左边缘对齐 */
}

.stat-value.primary {
  color: #1890ff;
}

.stat-value.warn {
  color: #e6a23c;
}

.stat-value.info {
  color: #606266;
}

.stat-value.success {
  color: #67c23a;
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

.query-table-wrapper .action-toolbar {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(228, 231, 237, 0.5);
}

.table-section {
  margin-top: 12px;
}

/* 指标详情抽屉样式 */
.quota-detail-drawer {
  font-size: 14px;
}

.quota-detail-content {
  padding: 0;
}

.detail-info-card,
.detail-table-card {
  margin-bottom: 20px;
}

.detail-info-card:last-child,
.detail-table-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
  font-size: 16px;
  color: #409eff;
}

.detail-descriptions {
  margin-top: 10px;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.amount-stats {
  margin-top: 10px;
}

.amount-item {
  text-align: center;
  padding: 15px 0;
}

.amount-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.amount-value {
  font-size: 24px;
  font-weight: 600;
  line-height: 1.2;
}

.amount-value.primary {
  color: #409eff;
}

.amount-value.warning {
  color: #e6a23c;
}

.amount-value.success {
  color: #67c23a;
}

.budget-amount-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.carry-over-tag {
  padding: 0 6px;
  line-height: 18px;
  height: 18px;
}

.detail-count {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
  margin-left: 8px;
}

.detail-table {
  margin-top: 10px;
}

.amount-text {
  font-weight: 500;
  color: #303133;
}

.amount-text.allocated {
  color: #e6a23c;
}

.amount-text.available {
  color: #67c23a;
}

.memo-text {
  color: #606266;
  font-size: 13px;
}

/* 详情页附件样式 */
.detail-attachment-card {
  margin-top: 20px;
}

.attachment-list {
  margin-top: 10px;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  padding: 10px;
  background-color: #FAFAFA;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #EBEEF5;
}

.attachment-item:last-child {
  border-bottom: none;
}

.attachment-item .el-icon-document {
  color: #909399;
  margin-right: 8px;
  font-size: 18px;
}

.attachment-item .file-name {
  flex: 1;
  color: #606266;
  font-size: 14px;
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attachment-item .file-size {
  color: #909399;
  font-size: 12px;
  margin-right: 15px;
}

.attachment-item .actions {
  margin-left: auto;
}

.attachment-item .actions .el-button {
  padding: 0 5px;
  font-size: 13px;
}

.no-attachment {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
}

.no-attachment .el-icon-document-delete {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
  opacity: 0.5;
}

/* 查询表单、表格容器 - 毛玻璃效果 */
.query-table-wrapper {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 表格筛选行样式 */
.table-filter-bar {
  margin-bottom: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.table-filter-bar .el-form {
  margin-bottom: 0;
}

.table-filter-bar .el-form-item {
  margin-bottom: 0;
  margin-right: 10px;
}

/* 操作栏 */
.action-toolbar {
  margin-bottom: 12px;
}

/* 表格区域 */
.table-section {
  margin-top: 8px;
}

/* 功能分类列左右竖线分隔 */
::v-deep .el-table .function-category-col {
  border-left: 2px solid #dcdfe6 !important;
  border-right: 2px solid #dcdfe6 !important;
}

/* 表头筛选样式 */
.table-header-cell {
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
  font-size: 13px;
  text-align: center;
}

.header-filter-input,
.header-filter-select {
  width: 100%;
  min-width: 100px;
}

/* 输入框和下拉框样式 */
.header-filter-input ::v-deep .el-input__inner,
.header-filter-select ::v-deep .el-input__inner {
  height: 32px !important;
  line-height: 32px !important;
  font-size: 13px;
  padding: 0 10px;
  border-radius: 4px;
  border-color: #dcdfe6;
  background-color: #fff;
}

.header-filter-input ::v-deep .el-input__inner:focus,
.header-filter-select ::v-deep .el-input__inner:focus {
  border-color: #409EFF;
}

.header-filter-input ::v-deep .el-input__suffix,
.header-filter-select ::v-deep .el-input__suffix {
  right: 8px;
}

.header-filter-input ::v-deep .el-input__icon,
.header-filter-select ::v-deep .el-input__icon {
  line-height: 32px !important;
  font-size: 14px;
}

/* 下拉框弹出层样式 */
.header-filter-select ::v-deep .el-select-dropdown {
  min-width: 150px !important;
}

.header-filter-select ::v-deep .el-select-dropdown__item {
  font-size: 13px;
  padding: 0 15px;
  height: 36px;
  line-height: 36px;
}

/* 确保下拉框选项可见 */
.header-filter-select ::v-deep .el-select__popper {
  z-index: 9999 !important;
}

/* 表格表头单元格增加内边距 */
.budget-table ::v-deep .el-table__header th {
  padding: 8px 0 !important;
}

.budget-table ::v-deep .el-table__header th .cell {
  padding: 0 8px;
}

/* 表格标题行蓝色边框 */
.budget-table ::v-deep .el-table__header th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  border-top: 1px solid #409EFF !important;
  border-bottom: 1px solid #409EFF !important;
}

/* 预算金额容器 */
.budget-amount-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

/* 结转资金圆形徽章 */
.carry-over-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background-color: hsl(5, 77%, 57%);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 0;
  border: none;
  line-height: 1;
}

.carry-over-badge.el-tag--mini {
  padding: 0;
  height: 22px;
  line-height: 22px;
}
</style>

<style>
/* 全局样式：确保下拉框显示 */
.el-select-dropdown {
  z-index: 9999 !important;
}

.el-select-dropdown__item {
  min-height: 34px;
  line-height: 34px;
}
</style>

<style>
/* 表格标题行蓝色边框 - 全局样式 */
.budget-table .el-table__header th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  border-top: 1px solid #409EFF !important;
  border-bottom: 1px solid #409EFF !important;
}
</style>

<style>
/* 操作栏中的 primary 按钮保持白色背景 - 全局样式 */
.semester-budget-page .action-toolbar .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page .action-toolbar .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.semester-budget-page .action-toolbar .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}
</style>
