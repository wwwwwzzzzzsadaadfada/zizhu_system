<template>
  <div class="app-container subsidy-quota-page">
    <!-- 顶部统计卡片区域：一体化面板 + 深色头部条 / 浅色内容 -->
    <div class="quota-stats-wrapper">
    <el-row :gutter="20" class="quota-stats">
      <el-col :span="8">
          <el-card
            shadow="hover"
            class="stat-card"
            :class="{ 'stat-card-selected': selectedQuota, 'stat-card-primary': true }"
          >
          <div class="stat-header">
            <div class="stat-label">
              <img src="@/assets/images/zb/2.png" alt="总指标金额" class="stat-icon" />
              <span>总指标金额(元)</span>
            </div>
            <el-button
              v-if="selectedQuota"
              type="text"
              size="mini"
              icon="el-icon-close"
              @click.stop="clearSelectedQuota"
              class="stat-clear-btn"
            >取消</el-button>
          </div>
            <div class="stat-body">
              <div class="stat-value primary">
                {{ formatAmount(selectedQuota ? (selectedQuota.totalQuota || 0) : (statistics && statistics.totalQuota ? statistics.totalQuota : 0)) }}
              </div>
            </div>
        </el-card>
      </el-col>
      <el-col :span="8">
          <el-card
            shadow="hover"
            class="stat-card"
            :class="{ 'stat-card-selected': selectedQuota, 'stat-card-warn': true }"
          >
            <div class="stat-header">
          <div class="stat-label">
            <img src="@/assets/images/zb/1.png" alt="已分配金额" class="stat-icon" />
            <span>已分配金额(元)</span>
          </div>
            </div>
            <div class="stat-body">
              <div class="stat-value warn">
                {{ formatAmount(selectedQuota ? (selectedQuota.allocatedAmount || 0) : (statistics && statistics.allocatedAmount ? statistics.allocatedAmount : 0)) }}
              </div>
          <div
            v-for="item in displayAllocatedStatLines"
            :key="item.value"
            class="stat-sub-line"
          >
            <span class="allocated-type">{{ item.label }}：</span>
            <span class="allocated-amount">{{ formatAmount(item.amount) }}</span>
              </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
          <el-card
            shadow="hover"
            class="stat-card"
            :class="{ 'stat-card-selected': selectedQuota, 'stat-card-success': true }"
          >
            <div class="stat-header">
          <div class="stat-label">
            <img src="@/assets/images/zb/3.png" alt="剩余额度" class="stat-icon" />
            <span>剩余额度(元)</span>
          </div>
            </div>
            <div class="stat-body">
              <div class="stat-value success">
                {{ formatAmount(selectedQuota ? (selectedQuota.availableQuota != null ? selectedQuota.availableQuota : 0) : (statistics && statistics.availableAmount ? statistics.availableAmount : 0)) }}
              </div>
            </div>
        </el-card>
      </el-col>
    </el-row>
    </div>

    <!-- 查询 + 表格 合并毛玻璃容器 -->
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
      label-width="90px"
      class="query-form"
    >
      <el-form-item label="学年" prop="schoolYear">
        <el-input
          v-model="queryParams.schoolYear"
          placeholder="如：2024-2025"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学期" prop="semester">
        <el-select v-model="queryParams.semester" placeholder="全部" clearable>
          <el-option label="秋季学期" value="1" />
          <el-option label="春季学期" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="指标文号" prop="quotaDocNo">
        <el-input
          v-model="queryParams.quotaDocNo"
          placeholder="支持模糊搜索"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预算项目" prop="budgetProjectName">
        <el-input
          v-model="queryParams.budgetProjectName"
          placeholder="项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="功能分类" prop="functionCategory">
        <el-select v-model="queryParams.functionCategory" placeholder="全部" clearable>
          <el-option
            v-for="dict in dict.type.sys_function_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option
            v-for="dict in dict.type.sys_quota_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="指标来源" prop="quotaSourceType">
        <el-select v-model="queryParams.quotaSourceType" placeholder="全部" clearable>
          <el-option
            v-for="dict in dict.type.sys_quota_source_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
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
    <el-row :gutter="10" class="mb8 action-toolbar">
      <el-col :span="24" class="toolbar-content">
        <div class="toolbar-left">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['system:subsidyQuota:add']"
          >新增指标</el-button>
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['system:subsidyQuota:edit']"
          >修改指标</el-button>
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:subsidyQuota:remove']"
          >删除</el-button>
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['system:subsidyQuota:export']"
          >导出</el-button>
          <el-button
            type="info"
            plain
            icon="el-icon-refresh"
            size="mini"
            @click="handleFixAllocated"
            v-hasPermi="['system:subsidyQuota:edit']"
          >校准金额</el-button>
        </div>
        <div class="toolbar-right">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </div>
      </el-col>
    </el-row>

    <!-- 指标列表 -->
      <div class="table-section">
    <el-table
          class="quota-table"
      v-loading="loading"
      :data="quotaList"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      highlight-current-row
      :row-class-name="getRowClassName"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="指标文号" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" @click.native.prevent="openDetail(scope.row)">
            {{ scope.row.quotaDocNo || '-' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="学年/学期" min-width="160">
        <template slot-scope="scope">
          {{ scope.row.schoolYear || '-' }} {{ scope.row.semesterLabel || '' }}
        </template>
      </el-table-column>
      <el-table-column label="预算项目" prop="budgetProjectName" min-width="180" />
      <el-table-column label="经济分类" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.economyCategoryText || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="功能分类" prop="functionCategory" width="130">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_function_category" :value="scope.row.functionCategory" />
        </template>
      </el-table-column>
      <el-table-column label="总指标(元)" prop="totalQuota" width="140">
        <template slot-scope="scope">
          {{ formatAmount(scope.row.totalQuota) }}
        </template>
      </el-table-column>
      <el-table-column
        label="已分配(元)"
        prop="allocatedAmount"
        width="140"
        class-name="allocated-column"
      >
        <template slot-scope="scope">
          <el-tooltip
            placement="top"
            :disabled="!hasCarriedOverAmount(scope.row)"
          >
            <div slot="content">
              已分配总计：{{ formatAmount(scope.row.allocatedAmount) }} 元<br/>
              其中结转：{{ formatAmount(scope.row.carriedOverAmount || 0) }} 元<br/>
              分配到预算：{{ formatAmount(scope.row.allocatedToBudget || 0) }} 元
            </div>
            <span class="allocated-amount-wrapper">
              <span class="allocated-amount">{{ formatAmount(scope.row.allocatedAmount) }}</span>
              <el-tag
                v-if="hasCarriedOverAmount(scope.row)"
                type="warning"
                size="mini"
                effect="plain"
                class="carry-over-tag"
              >
                含结转
              </el-tag>
            </span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="剩余额度(元)" width="140">
        <template slot-scope="scope">
          <span :class="{ 'danger-text': Number(scope.row.availableQuota) < 0 }">
            {{ formatAmount(scope.row.availableQuota) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="上期结余(元)" width="140">
        <template slot-scope="scope">
          <span class="carry-over-amount">
            {{ formatAmount(scope.row.carryOverAmount || 0) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="指标来源" prop="quotaSourceType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_quota_source_type" :value="scope.row.quotaSourceType" />
        </template>
      </el-table-column>
      <el-table-column label="来源指标" prop="sourceQuotaDocNo" width="200">
        <template slot-scope="scope">
          <span v-if="scope.row.sourceQuotaDocNo" style="color: #409EFF; cursor: pointer;" @click="openDetailById(scope.row.sourceQuotaId)">
            {{ scope.row.sourceQuotaDocNo }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_quota_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150" align="center">
        <template slot-scope="scope">
          <div class="action-buttons">
            <el-button
              type="text"
              size="mini"
              icon="el-icon-view"
              @click="openDetail(scope.row)"
              class="action-btn"
            >详情</el-button>
            <el-dropdown trigger="click" @command="handleMoreAction">
              <el-button type="text" size="mini" class="action-btn">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="{ action: 'edit', row: scope.row }"
                  icon="el-icon-edit"
                  v-hasPermi="['system:subsidyQuota:edit']"
                >
                  编辑
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'allocate', row: scope.row }"
                  icon="el-icon-coin"
                  v-hasPermi="['system:subsidyQuota:allocate']"
                >
                  分配预算
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'carryOver', row: scope.row }"
                  icon="el-icon-right"
                  :disabled="!canCarryOver(scope.row)"
                  v-hasPermi="['system:subsidyQuota:add']"
                  :title="getCarryOverDisabledReason(scope.row)"
                >
                  结转
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'reclaim', row: scope.row }"
                  icon="el-icon-back"
                  v-hasPermi="['system:subsidyQuota:edit']"
                >
                  收回预算
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'delete', row: scope.row }"
                  icon="el-icon-delete"
                  divided
                  class="danger-item"
                  v-hasPermi="['system:subsidyQuota:remove']"
                >
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>
      </div>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    </div>

    <!-- 详情抽屉 -->
    <el-drawer
      title="指标详情"
      :visible.sync="detailVisible"
      size="55%"
      append-to-body
      class="quota-detail-drawer"
    >
      <div v-if="detailRecord" class="quota-detail-content">
        <!-- 基本信息卡片 -->
        <el-card class="detail-info-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-document"></i>
            <span>基本信息</span>
          </div>
          <el-descriptions :column="2" border size="medium" class="detail-descriptions">
            <el-descriptions-item label="指标文号" :span="1">
              <span class="info-value">{{ detailRecord.quotaDocNo || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="学年" :span="1">
              <el-tag size="small" type="info">{{ detailRecord.schoolYear || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="学期" :span="1">
              <el-tag size="small" type="info">{{ detailRecord.semesterLabel || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预算项目" :span="2">
              <span class="info-value">{{ detailRecord.budgetProjectName || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="功能分类" :span="1">
              <dict-tag :options="dict.type.sys_function_category" :value="detailRecord.functionCategory" />
            </el-descriptions-item>
            <el-descriptions-item label="预算级次" :span="1">
              <dict-tag :options="dict.type.sys_budget_level" :value="detailRecord.budgetLevel" />
            </el-descriptions-item>
            <el-descriptions-item label="指标来源" :span="1">
              <dict-tag :options="dict.type.sys_quota_source_type" :value="detailRecord.quotaSourceType" />
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">
              <span class="info-value">{{ detailRecord.memo || '-' }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 经济分类明细卡片 -->
        <el-card class="detail-table-card" shadow="never">
          <div slot="header" class="card-header">
            <i class="el-icon-s-grid"></i>
            <span>经济分类明细</span>
            <span class="detail-count">（共 {{ (detailRecord.detailList || []).length }} 项）</span>
          </div>
          <el-table
            :data="(detailRecord && detailRecord.detailList) || []"
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
            <span class="detail-count">（共 {{ (detailAttachmentList || []).length }} 个）</span>
          </div>
          <div v-if="detailAttachmentList && detailAttachmentList.length > 0" class="attachment-list">
            <div v-for="(file, index) in detailAttachmentList" :key="file.id || index" class="attachment-item">
              <i class="el-icon-document"></i>
              <span class="file-name" :title="file.name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
              <div class="actions">
                <el-button type="text" size="small" @click="handleAttachmentPreview(file)">预览</el-button>
                <el-button type="text" size="small" @click="handleAttachmentDownload(file)">下载</el-button>
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

    <!-- 新增/编辑 对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学年学期" prop="yearSemesterId">
              <el-select v-model="form.yearSemesterId" placeholder="请选择学年学期" filterable>
                <el-option
                  v-for="item in yearSemesters"
                  :key="item.id"
                  :label="`${item.schoolYear} ${item.semesterLabel || ''}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发文时间" prop="issueDate">
              <el-date-picker
                v-model="form.issueDate"
                type="date"
                placeholder="请选择"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="指标文号" prop="quotaDocNo">
              <el-input v-model="form.quotaDocNo" placeholder="请输入指标文号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预算项目" prop="budgetProjectName">
              <el-input v-model="form.budgetProjectName" placeholder="请输入预算项目" />
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="功能分类" prop="functionCategory">
              <el-select
                v-model="form.functionCategory"
                placeholder="请选择功能分类"
                :disabled="(form.quotaSourceType == 2 || form.quotaSourceType == 3) && sourceQuotaInfo && sourceQuotaInfo.functionCategory"
              >
                <el-option
                  v-for="dict in dict.type.sys_function_category"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
              <span v-if="(form.quotaSourceType == 2 || form.quotaSourceType == 3) && sourceQuotaInfo && sourceQuotaInfo.functionCategory" class="form-tip">
                （已自动填充，必须与来源指标一致）
              </span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预算级次">
              <el-select v-model="form.budgetLevel" placeholder="请选择预算级次">
                <el-option
                  v-for="dict in dict.type.sys_budget_level"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="指标来源" prop="quotaSourceType">
              <el-select v-model="form.quotaSourceType" placeholder="请选择指标来源" :disabled="form.id != null">
                <el-option
                  v-for="dict in dict.type.sys_quota_source_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
              <span v-if="form.id != null" class="form-tip">（编辑时不可修改）</span>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.quotaSourceType == 2 || form.quotaSourceType == 3">
            <el-form-item label="来源指标" prop="sourceQuotaId">
              <el-select
                v-model="form.sourceQuotaId"
                placeholder="请选择来源指标"
                filterable
                clearable
                @change="handleSourceQuotaChange"
                style="width: 100%"
              >
                <el-option
                  v-for="quota in filteredSourceQuotas"
                  :key="quota.id"
                  :label="quota.quotaDocNo"
                  :value="quota.id"
                >
                  <span style="float: left">{{ quota.quotaDocNo }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">
                    {{ quota.schoolYear }} {{ quota.semesterLabel || '' }}
                  </span>
                </el-option>
              </el-select>
              <span class="form-tip">（必须选择相同功能分类的指标）</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input v-model="form.memo" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>

        <el-divider content-position="left">指标附件</el-divider>
        <el-form-item label="附件上传">
          <el-upload
            ref="attachmentUpload"
            :action="attachmentUploadUrl"
            :headers="attachmentUploadHeaders"
            :on-success="handleAttachmentSuccess"
            :on-remove="handleAttachmentRemove"
            :on-error="handleAttachmentError"
            :file-list="attachmentList"
            :auto-upload="true"
            :multiple="true"
            :limit="10"
            :disabled="!form.id"
            accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.bmp,.txt,.xml,.json,.md"
          >
            <el-button size="small" type="primary" icon="el-icon-upload" :disabled="!form.id">
              点击上传
            </el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传pdf/doc/docx/xls/xlsx/ppt/pptx/jpg/jpeg/png/gif/bmp/txt/xml/json/md文件，且不超过10个，单个文件不超过50MB
            </div>
          </el-upload>
          <div v-if="attachmentList && attachmentList.length > 0" class="attachment-list">
            <div v-for="(file, index) in attachmentList" :key="file.id || file.uid" class="attachment-item">
              <i class="el-icon-document"></i>
              <span class="file-name" :title="file.name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
              <div class="actions">
                <el-button type="text" size="small" @click="handleAttachmentPreview(file)">预览</el-button>
                <el-button type="text" size="small" @click="handleAttachmentDownload(file)">下载</el-button>
                <el-button type="text" size="small" class="danger-text" @click="handleAttachmentDelete(file, index)">删除</el-button>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-divider content-position="left">经济分类明细</el-divider>
        <div class="detail-table-actions">
          <el-button type="primary" size="mini" icon="el-icon-plus" plain @click="handleDetailAdd">
            新增明细
          </el-button>
          <span class="detail-tip" style="margin-left: 10px; color: #909399; font-size: 12px;">
            <i class="el-icon-info"></i> 已结转的资金（来源明细ID不为空）不允许编辑或删除
          </span>
        </div>
        <el-table :data="form.detailList" border size="mini" class="detail-table">
          <el-table-column label="经济分类" min-width="150">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.economyCategory"
                placeholder="请选择"
                :disabled="!isDetailEditable(scope.row)"
              >
                <el-option
                  v-for="dict in dict.type.sys_economy_category"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="总金额(元)" min-width="140">
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.totalAmount"
                :min="0"
                :precision="2"
                :controls="false"
                placeholder="请输入金额"
                :disabled="!isDetailEditable(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="已分配(元)" min-width="120">
            <template slot-scope="scope">
              {{ formatAmount(scope.row.allocatedAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="剩余(元)" min-width="120">
            <template slot-scope="scope">
              {{ detailAvailable(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" min-width="100">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                :disabled="!isDetailEditable(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="160">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.memo"
                placeholder="备注"
                :disabled="!isDetailEditable(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button
                type="text"
                size="mini"
                icon="el-icon-delete"
                @click="handleDetailRemove(scope.$index)"
                :disabled="!isDetailEditable(scope.row)"
                :title="scope.row && scope.row.isEditable === false ? '已分配或已结转的资金不允许删除' : ''"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 指标结转对话框 -->
    <el-dialog
      title="指标结转"
      :visible.sync="carryOverDialogVisible"
      width="920px"
      append-to-body
      :close-on-click-modal="false"
      class="carry-over-dialog"
    >
      <el-form
        ref="carryOverFormRef"
        :model="carryOverForm"
        :rules="carryOverRules"
        label-width="90px"
      >
        <!-- 第一步：选择来源指标和目标指标 -->
        <div class="carry-over-step-content">
          <div class="carry-over-form-layout">
            <!-- 来源指标 -->
            <div class="carry-over-form-section left-section">
              <div class="section-title">来源指标</div>
              <div class="form-items-container">
                <el-form-item label="功能分类" prop="functionCategory" class="aligned-form-item">
                  <el-select
                    v-model="carryOverForm.functionCategory"
                    placeholder="请选择功能分类"
                    @change="handleCarryOverFunctionCategoryChange"
                    :disabled="isFormDisabled"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="dict in dict.type.sys_function_category"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="学年学期" prop="sourceYearSemesterId" class="aligned-form-item">
                  <el-select
                    v-model="carryOverForm.sourceYearSemesterId"
                    placeholder="请选择学年学期"
                    @change="handleCarryOverSourceYearSemesterChange"
                    :disabled="isFormDisabled || !carryOverForm.functionCategory"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="semester in yearSemesters"
                      :key="semester.id"
                      :label="`${semester.school_year || semester.schoolYear || ''} ${semester.semesterLabel || ''}`"
                      :value="semester.id"
                    />
                  </el-select>
                  <div v-if="!carryOverForm.functionCategory" class="form-tip">请先选择功能分类</div>
                </el-form-item>
                <el-form-item label="来源指标" prop="sourceQuotaId" class="aligned-form-item">
                  <el-select
                    v-model="carryOverForm.sourceQuotaId"
                    placeholder="请选择来源指标"
                    filterable
                    @change="handleCarryOverSourceQuotaChange"
                    :disabled="isFormDisabled || !carryOverForm.functionCategory || !carryOverForm.sourceYearSemesterId"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="quota in filteredCarryOverQuotas"
                      :key="quota.id"
                      :label="quota.quotaDocNo"
                      :value="quota.id"
                    />
                  </el-select>
                  <div v-if="!carryOverForm.functionCategory || !carryOverForm.sourceYearSemesterId" class="form-tip">请先选择功能分类和学年学期</div>
                  <div v-else-if="filteredCarryOverQuotas.length === 0" class="form-tip warning">该学年学期和功能分类下暂无可用的来源指标</div>
                </el-form-item>
                <!-- 来源指标剩余金额 -->
                <div v-if="carryOverSourceQuotaInfo" class="form-amount-info remaining-amount">
                  <span class="amount-label">剩余金额：</span>
                  <span class="amount-value remaining">{{ formatAmount(carryOverSourceAvailableQuota) }} 元</span>
                </div>
              </div>
            </div>

            <!-- 箭头 -->
            <div class="carry-over-arrow">
              <div class="arrow-icon-wrapper">
                <i class="el-icon-right"></i>
              </div>
              <!-- 经济分类信息 -->
              <div
                v-if="carryOverSourceQuotaInfo && carryOverSourceQuotaDetails && carryOverSourceQuotaDetails.length > 0"
                class="arrow-economy-category"
              >
                <div class="economy-category-label">经济分类</div>
                <div class="economy-category-list">
                  <div
                    v-for="detail in carryOverSourceQuotaDetails"
                    :key="detail.economyCategory"
                    class="economy-category-item"
                  >
                    <span class="economy-category-name">{{ economyLabel(detail.economyCategory) }}</span>
                    <span class="economy-category-amount">{{ formatAmount(detail.availableAmount || 0) }}元</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 目标指标 -->
            <div class="carry-over-form-section right-section">
              <div class="section-title">目标指标</div>
              <div class="form-items-container">
                <el-form-item label="功能分类" prop="targetFunctionCategory" class="aligned-form-item">
                  <el-select
                    v-model="carryOverForm.targetFunctionCategory"
                    placeholder="请选择功能分类"
                    @change="handleTargetFunctionCategoryChange"
                    :disabled="isFormDisabled"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="dict in dict.type.sys_function_category"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="学年学期" prop="yearSemesterId" class="aligned-form-item">
                  <el-select
                    v-model="carryOverForm.yearSemesterId"
                    placeholder="请选择目标学期"
                    @change="handleTargetYearSemesterChange"
                    :disabled="isFormDisabled"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="semester in yearSemesters"
                      :key="semester.id"
                      :label="`${semester.school_year || semester.schoolYear || ''} ${semester.semesterLabel || ''}`"
                      :value="semester.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="指标文号" prop="quotaDocNo" class="aligned-form-item">
                  <el-select
                    v-model="carryOverForm.quotaDocNo"
                    placeholder="请选择或输入指标文号"
                    filterable
                    allow-create
                    default-first-option
                    :loading="loadingTargetQuotas"
                    :disabled="isFormDisabled"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="quota in targetQuotaOptions"
                      :key="quota.quotaDocNo"
                      :label="quota.quotaDocNo"
                      :value="quota.quotaDocNo"
                  />
                </el-select>
              </el-form-item>
              <!-- 结转金额信息 -->
              <div v-if="carryOverSourceQuotaInfo" class="form-amount-info carry-over-amount">
                <span class="amount-label">结转金额：</span>
                <span class="amount-value carryover">{{ formatAmount(calculateCarryOverAmount()) }} 元</span>
              </div>
              </div>
            </div>
          </div>
        </div>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="carryOverDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="carryOverSubmitting"
          @click="submitCarryOver"
          :disabled="!carryOverForm.sourceQuotaId || !carryOverForm.yearSemesterId"
        >
          确认结转
        </el-button>
      </div>
    </el-dialog>

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
          <img :src="previewData.imageUrl" :alt="previewData.fileName" />
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

    <!-- 指标额度分配对话框 -->
    <el-dialog
      title="指标额度分配"
      :visible.sync="allocationDialogVisible"
      width="680px"
      append-to-body
      class="allocation-dialog"
    >
      <!-- 指标基本信息卡片 -->
      <el-card shadow="never" class="quota-info-card" v-if="currentQuota">
        <div slot="header" class="card-header">
          <i class="el-icon-document"></i>
          <span>指标信息</span>
        </div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="指标文号" :span="2">
            <span class="info-value">{{ currentQuota.quotaDocNo || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="学年/学期">
            <span class="info-value">{{ formatAllocationYearSemester() }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="功能分类">
            <dict-tag :options="dict.type.sys_function_category" :value="currentQuota.functionCategory" />
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 分配表单卡片 -->
      <el-card shadow="never" class="allocation-form-card">
        <div slot="header" class="card-header">
          <i class="el-icon-edit"></i>
          <span>分配信息</span>
        </div>
        <el-form
          ref="allocationFormRef"
          :model="allocationForm"
          label-width="110px"
          size="medium"
        >
          <el-form-item label="本学期下达">
            <el-select
              v-model="allocationSelection.current"
              multiple
              collapse-tags
              placeholder="可多选经济分类"
              filterable
              style="width: 100%"
              @change="val => handleAllocationSelectionChange('current', val)"
            >
              <el-option
                v-for="detail in currentAllocationDetails"
                :key="detail.id"
                :label="`${formatAmount(detail.availableAmount)}元（${economyLabel(detail.economyCategory)} 本学期下达）`"
                :value="detail.id"
              />
            </el-select>
            <div class="form-tip">
              <i class="el-icon-info"></i>
              选中的经济分类会生成分配行，可一次录入多条预算
            </div>
            <el-table
              v-if="currentAllocationRows.length"
              :data="currentAllocationRows"
              size="small"
              border
              class="allocation-table"
              header-cell-class-name="allocation-header"
              cell-class-name="allocation-cell"
              :header-cell-style="allocationHeaderStyle"
              :cell-style="allocationCellStyle"
              style="width: 100%; margin-top: 10px;"
            >
              <el-table-column label="经济分类" min-width="140">
                <template slot-scope="scope">
                  {{ economyLabel(scope.row.detail.economyCategory) }}
                </template>
              </el-table-column>
              <el-table-column label="可分配额度" width="140" align="center">
                <template slot-scope="scope">
                  {{ formatAmount(scope.row.availableAmount) }} 元
                </template>
              </el-table-column>
              <el-table-column label="分配金额" width="200" align="center">
                <template slot-scope="scope">
                  <el-input-number
                    v-model="allocationAmounts[scope.row.id]"
                    :min="0.01"
                    :max="scope.row.availableAmount"
                    :precision="2"
                    :step="100"
                    :controls="true"
                    placeholder="金额"
                    style="width: 160px"
                  />
                </template>
              </el-table-column>
            </el-table>
            <div class="allocation-total" v-if="currentAllocationRows.length">
              本学期下达合计：<span class="tip-amount">{{ formatAmount(allocationTotal('current')) }}</span> 元
            </div>
          </el-form-item>

          <el-form-item label="结转资金">
            <el-select
              v-model="allocationSelection.carryOver"
              multiple
              collapse-tags
              placeholder="可多选结转资金经济分类"
              filterable
              style="width: 100%"
              @change="val => handleAllocationSelectionChange('carryOver', val)"
            >
              <el-option
                v-for="detail in carryOverAllocationDetails"
                :key="detail.id"
                :label="`${formatAmount(detail.availableAmount)}元（${economyLabel(detail.economyCategory)} 结转资金）`"
                :value="detail.id"
              />
            </el-select>
            <el-table
              v-if="carryOverAllocationRows.length"
              :data="carryOverAllocationRows"
              size="small"
              border
              class="allocation-table"
              header-cell-class-name="allocation-header"
              cell-class-name="allocation-cell"
              :header-cell-style="allocationHeaderStyle"
              :cell-style="allocationCellStyle"
              style="width: 100%; margin-top: 10px;"
            >
              <el-table-column label="经济分类" min-width="140">
                <template slot-scope="scope">
                  {{ economyLabel(scope.row.detail.economyCategory) }}
                </template>
              </el-table-column>
              <el-table-column label="可分配额度" width="140" align="center">
                <template slot-scope="scope">
                  {{ formatAmount(scope.row.availableAmount) }} 元
                </template>
              </el-table-column>
              <el-table-column label="分配金额" width="200" align="center">
                <template slot-scope="scope">
                  <el-input-number
                    v-model="allocationAmounts[scope.row.id]"
                    :min="0.01"
                    :max="scope.row.availableAmount"
                    :precision="2"
                    :step="100"
                    :controls="true"
                    placeholder="金额"
                    style="width: 160px"
                  />
                </template>
              </el-table-column>
            </el-table>
            <div class="allocation-total" v-if="carryOverAllocationRows.length">
              结转资金合计：<span class="tip-amount">{{ formatAmount(allocationTotal('carryOver')) }}</span> 元
            </div>
          </el-form-item>
        </el-form>
      </el-card>

      <div slot="footer" class="dialog-footer">
        <el-button @click="allocationDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="allocationSubmitting"
          @click="submitAllocation"
          icon="el-icon-check"
        >
          确认分配
        </el-button>
      </div>
    </el-dialog>

    <!-- 收回预算对话框 -->
    <el-dialog
      title="收回已分配预算"
      :visible.sync="reclaimDialogVisible"
      width="900px"
      append-to-body
      class="reclaim-dialog"
    >
      <!-- 指标基本信息卡片 -->
      <el-card shadow="never" class="quota-info-card" v-if="currentQuota">
        <div slot="header" class="card-header">
          <i class="el-icon-document"></i>
          <span>指标信息</span>
        </div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="指标文号" :span="2">
            <span class="info-value">{{ currentQuota.quotaDocNo || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="已分配金额">
            <span class="info-value warn">{{ formatAmount(currentQuota.allocatedAmount || 0) }} 元</span>
          </el-descriptions-item>
          <el-descriptions-item label="剩余额度">
            <span class="info-value success">{{ formatAmount(currentQuota.availableQuota || 0) }} 元</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 预算列表 -->
      <el-card shadow="never" class="reclaim-budget-card">
        <div slot="header" class="card-header">
          <i class="el-icon-wallet"></i>
          <span>已分配预算列表</span>
          <el-button
            type="text"
            size="mini"
            icon="el-icon-refresh"
            @click="loadReclaimBudgets"
            :loading="reclaimLoading"
            style="float: right;"
          >
            刷新
          </el-button>
        </div>
        <el-alert
          v-if="reclaimBudgetList.length === 0 && !reclaimLoading"
          title="暂无已分配的预算"
          type="info"
          :closable="false"
          show-icon
        />
        <el-table
          v-else
          :data="reclaimBudgetList"
          v-loading="reclaimLoading"
          @selection-change="handleReclaimSelectionChange"
          :row-key="row => row.id"
          border
          stripe
          size="small"
          max-height="400"
        >
          <el-table-column
            type="selection"
            width="55"
            align="center"
            :selectable="isReclaimable"
          />
              <el-table-column label="学年/学期" prop="schoolYear" width="120">
            <template slot-scope="scope">
              {{ scope.row.schoolYear || '-' }} {{ scope.row.semesterLabel || '' }}
            </template>
          </el-table-column>
          <el-table-column label="经济分类" prop="economyCategory" width="120">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.sys_economy_category" :value="scope.row.economyCategory" />
            </template>
          </el-table-column>
          <el-table-column label="预算金额" prop="budgetAmount" width="120" align="right">
            <template slot-scope="scope">
              {{ formatAmount(scope.row.budgetAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="已使用" prop="usedAmount" width="100" align="right">
            <template slot-scope="scope">
              <span :class="Number(scope.row.usedAmount || 0) > 0 ? 'text-danger' : ''">
                {{ formatAmount(scope.row.usedAmount) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="锁定金额" prop="lockedAmount" width="100" align="right">
            <template slot-scope="scope">
              <span :class="Number(scope.row.lockedAmount || 0) > 0 ? 'text-warning' : ''">
                {{ formatAmount(scope.row.lockedAmount) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="可用余额" prop="availableAmount" width="100" align="right">
            <template slot-scope="scope">
              <span :class="Number(scope.row.availableAmount || 0) > 0 ? 'text-success' : ''">
                {{ formatAmount(scope.row.availableAmount) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="getReclaimStatusType(scope.row)"
                size="small"
              >
                {{ getReclaimStatusText(scope.row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="memo" show-overflow-tooltip />
        </el-table>
        <div class="reclaim-tip" v-if="reclaimBudgetList.length > 0">
          <i class="el-icon-info"></i>
          <span>只能收回未使用的预算（已使用或有锁定金额的预算无法收回）</span>
        </div>
      </el-card>

      <div slot="footer" class="dialog-footer">
        <el-button @click="reclaimDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="reclaimSubmitting"
          :disabled="reclaimSelectedBudgets.length === 0"
          @click="submitReclaim"
          icon="el-icon-back"
        >
          确认收回（{{ reclaimSelectedBudgets.length }}）
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSubsidyQuota,
  listSubsidyQuotaWithUsage,
  getSubsidyQuota,
  addSubsidyQuota,
  updateSubsidyQuota,
  delSubsidyQuota,
  fixQuotaAllocatedAmount,
  listAllocatableDetails,
  allocateQuotaBatch,
  getQuotaStatistics,
  listAllocatedBudgets,
  reclaimBudgets,
  getQuotaDocNos,
  carryOverToTargetQuota
} from '@/api/system/subsidyQuota'
import { listYearSemesters, getCurrentYearSemester } from '@/api/system/yearSemester'
import {
  listQuotaAttachmentByQuotaId,
  uploadQuotaAttachment,
  delQuotaAttachment,
  downloadQuotaAttachment,
  previewQuotaAttachment,
  previewQuotaAttachmentAsync
} from '@/api/system/quotaAttachment'
import { getToken } from '@/utils/auth'

export default {
  name: 'SubsidyQuota',
  dicts: ['sys_quota_status', 'sys_quota_source_type', 'sys_function_category', 'sys_budget_source', 'sys_budget_level', 'sys_economy_category'],
  data() {
    return {
      loading: false,
      total: 0,
      quotaList: [],
      showSearch: true,
      queryFormCollapsed: false, // 查询表单折叠状态
      ids: [],
      single: true,
      multiple: true,
      open: false,
      title: '',
      yearSemesters: [],
      detailVisible: false,
      detailRecord: null,
      detailAttachmentList: [], // 详情页附件列表
      selectedQuota: null, // 选中的指标行
      statistics: {
        totalQuota: 0,
        allocatedAmount: 0,
        availableAmount: 0,
        allocatedByEconomy: {}
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        quotaDocNo: undefined,
        budgetProjectName: undefined,
        functionCategory: undefined,
        status: undefined,
        quotaSourceType: undefined,
        schoolYear: undefined,
        semester: undefined
      },
      form: {
        id: undefined,
        yearSemesterId: undefined,
        issueDate: '',
        quotaDocNo: '',
        budgetProjectName: '',
        functionCategory: undefined,
        budgetLevel: undefined,
        memo: '',
        detailList: []
      },
      rules: {
        yearSemesterId: [{ required: true, message: '请选择学年学期', trigger: 'change' }],
        issueDate: [{ required: true, message: '请选择发文时间', trigger: 'change' }],
        quotaDocNo: [{ required: true, message: '请输入指标文号', trigger: 'blur' }],
        quotaDocNo: [{ required: true, message: '请输入指标文号', trigger: 'blur' }],
        budgetProjectName: [{ required: true, message: '请输入预算项目', trigger: 'blur' }],
        functionCategory: [{ required: true, message: '请选择功能分类', trigger: 'change' }]
      },
      allocationDialogVisible: false,
      allocationDetails: [],
      allocationForm: {
        yearSemesterId: undefined
      },
      allocationSelection: {
        current: [],
        carryOver: []
      },
      allocationAmounts: {},
      allocationRules: {},
      allocationSubmitting: false,
      currentQuota: null,
      availableSourceQuotas: [], // 可用的来源指标列表
      sourceQuotaInfo: null, // 选中的来源指标信息
      // 结转相关
      carryOverDialogVisible: false,
        carryOverForm: {
          quotaSourceType: 2, // 默认上学期结转
          sourceQuotaId: undefined,
          sourceYearSemesterId: undefined, // 来源指标的学年学期
          economyCategory: undefined,
          yearSemesterId: undefined,
          functionCategory: undefined,
          targetFunctionCategory: undefined, // 目标指标功能分类
          quotaDocNo: '',
          detailList: []
        },
      targetQuotaOptions: [], // 目标指标文号选项
      loadingTargetQuotas: false, // 加载目标指标文号中
      carryOverRules: {
        quotaSourceType: [{ required: true, message: '请选择指标来源类型', trigger: 'change' }],
        functionCategory: [{ required: true, message: '请选择功能分类', trigger: 'change' }],
        sourceYearSemesterId: [{ required: true, message: '请选择学年学期', trigger: 'change' }],
        targetFunctionCategory: [{ required: true, message: '请选择目标功能分类', trigger: 'change' }],
        yearSemesterId: [{ required: true, message: '请选择学年学期', trigger: 'change' }],
        sourceQuotaId: [{ required: true, message: '请选择来源指标', trigger: 'change' }]
      },
      carryOverSubmitting: false,
      carryOverAvailableQuotas: [], // 结转可用的来源指标列表
      isFormDisabled: false, // 表单是否禁用
      // 附件相关
      attachmentList: [], // 附件列表
      attachmentUploadHeaders: {
        Authorization: 'Bearer ' + (getToken() || '')
      },
      previewDialogVisible: false, // 预览对话框
      previewData: null, // 预览数据
      carryOverSourceQuotaInfo: null, // 结转选中的来源指标信息
      currentSemesterId: null, // 当前学期ID
      // 收回预算相关
      reclaimDialogVisible: false,
      reclaimBudgetList: [],
      reclaimSelectedBudgets: [],
      reclaimLoading: false,
      reclaimSubmitting: false
    }
  },
  computed: {
    // 结转来源指标的明细列表（有剩余额度的，且未全部结转的）
    carryOverSourceQuotaDetails() {
      if (!this.carryOverSourceQuotaInfo || !Array.isArray(this.carryOverSourceQuotaInfo.detailList)) {
        return []
      }
      const filtered = this.carryOverSourceQuotaInfo.detailList.filter(detail => {
        // 使用 availableAmount（后端已计算实际可结转金额 = 原始剩余 - 已结转）
        const available = Number(detail.availableAmount || 0)
        // 调试：打印过滤结果
        if (detail.economyCategory) {
          console.log(`经济分类 ${detail.economyCategory}: 可结转金额=${available}, 是否显示=${available > 0}`)
        }
        return available > 0
      })
      console.log('过滤后的可结转经济分类:', filtered.map(d => ({
        经济分类: d.economyCategory,
        可结转金额: d.availableAmount
      })))
      return filtered
    },
    // 分配可选明细（本学期下达）
    currentAllocationDetails() {
      if (!Array.isArray(this.allocationDetails)) {
        return []
      }
      return this.allocationDetails.filter(detail => !detail.sourceDetailId)
    },
    // 分配可选明细（结转资金）
    carryOverAllocationDetails() {
      if (!Array.isArray(this.allocationDetails)) {
        return []
      }
      return this.allocationDetails.filter(detail => detail.sourceDetailId)
    },
    // 已选行（本学期下达）
    currentAllocationRows() {
      return this.buildAllocationRows(this.allocationSelection.current)
    },
    // 已选行（结转资金）
    carryOverAllocationRows() {
      return this.buildAllocationRows(this.allocationSelection.carryOver)
    },
    // 结转后的学年学期标签（使用后端返回的 semesterLabel）
    carryOverYearSemesterLabel() {
      if (!this.carryOverForm.yearSemesterId) {
        return '-'
      }
      const semester = this.yearSemesters.find(ys => ys.id === this.carryOverForm.yearSemesterId)
      if (!semester) {
        return '-'
      }
      return `${semester.school_year || semester.schoolYear || ''} ${semester.semesterLabel || ''}`
    },
    // 当前学期标签（使用后端返回的 semesterLabel）
    currentSemesterLabel() {
      if (!this.currentSemesterId) {
        return '-'
      }
      const semester = this.yearSemesters.find(ys => ys.id === this.currentSemesterId)
      if (!semester) {
        return '-'
      }
      return `${semester.school_year || semester.schoolYear || ''} ${semester.semesterLabel || ''}`
    },
    // 附件上传URL
    attachmentUploadUrl() {
      if (!this.form.id) {
        return ''
      }
      return this.getBaseURL() + '/system/quotaAttachment/upload/' + this.form.id
    },
    // 过滤后的来源指标列表（如果已选择功能分类，只显示相同功能分类的指标）
    // 注意：此过滤逻辑保留在前端，因为功能分类是用户动态选择的，重新查询后端会影响性能
    filteredSourceQuotas() {
      if (!this.form.functionCategory || !Array.isArray(this.availableSourceQuotas)) {
        return this.availableSourceQuotas || []
      }
      return this.availableSourceQuotas.filter(q =>
        q.functionCategory === this.form.functionCategory
      )
    },
    // 过滤后的结转可用指标（根据功能分类和学年学期）
    // 注意：此过滤逻辑保留在前端，因为功能分类和学年学期是用户动态选择的，重新查询后端会影响性能
    filteredCarryOverQuotas() {
      if (!this.carryOverForm.functionCategory || !this.carryOverForm.sourceYearSemesterId || !Array.isArray(this.carryOverAvailableQuotas)) {
        return []
      }
      return this.carryOverAvailableQuotas.filter(q =>
        q.functionCategory === this.carryOverForm.functionCategory &&
        q.yearSemesterId === this.carryOverForm.sourceYearSemesterId
      )
    },
    // 获取来源指标的剩余额度（使用后端返回的字段）
    carryOverSourceAvailableQuota() {
      if (!this.carryOverSourceQuotaInfo) {
        return 0
      }
      // 直接使用后端返回的availableQuota字段
      return Number(this.carryOverSourceQuotaInfo.availableQuota != null ? this.carryOverSourceQuotaInfo.availableQuota : 0)
    }
  },
  created() {
    this.resetFormData()
    this.getList()
    this.fetchYearSemesters()
    // 初始化当前学期ID
    getCurrentYearSemester().then(currentRes => {
      this.currentSemesterId = currentRes.data?.id
    }).catch(err => {
      console.error('获取当前学期失败:', err)
    })
  },
  methods: {
    // 附件相关方法
    handleAttachmentSuccess(response, file, fileList) {
      if (response.code === 200) {
        const newAttachment = response.data
        this.attachmentList.push({
          id: newAttachment.id,
          name: newAttachment.fileName,
          size: newAttachment.fileSize,
          url: newAttachment.fileUrl,
          uid: file.uid
        })
        this.$modal.msgSuccess('附件上传成功')
      } else {
        this.$modal.msgError(response.msg || '上传失败')
        const index = fileList.findIndex(f => f.uid === file.uid)
        if (index > -1) {
          fileList.splice(index, 1)
        }
      }
    },
    handleAttachmentRemove(file, fileList) {
      const attachmentId = file.id
      if (attachmentId) {
        this.$modal.confirm('是否确认删除该附件？').then(() => {
          return delQuotaAttachment(attachmentId)
        }).then(() => {
          this.attachmentList = this.attachmentList.filter(item => item.id !== attachmentId)
          this.$modal.msgSuccess('删除成功')
        }).catch(() => {
          // 用户取消删除，将文件重新添加到列表
          this.attachmentList.push(file)
        })
      } else {
        this.attachmentList = this.attachmentList.filter(item => item.uid !== file.uid)
      }
    },
    handleAttachmentError(err, file, fileList) {
      this.$modal.msgError('附件上传失败：' + (err.message || '未知错误'))
    },
    handleAttachmentPreview(file) {
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
    handleAttachmentDownload(file) {
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
    handleAttachmentDelete(file, index) {
      if (!file.id) {
        this.attachmentList.splice(index, 1)
        return
      }
      this.$modal.confirm('是否确认删除该附件？').then(() => {
        return delQuotaAttachment(file.id)
      }).then(() => {
        this.attachmentList.splice(index, 1)
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    loadAttachments(quotaId) {
      if (!quotaId) {
        this.attachmentList = []
        return
      }
      listQuotaAttachmentByQuotaId(quotaId).then(res => {
        if (res.code === 200 && res.data) {
          this.attachmentList = (res.data || []).map(item => ({
            id: item.id,
            name: item.fileName,
            size: item.fileSize,
            url: item.fileUrl,
            uid: item.id
          }))
        } else {
          this.attachmentList = []
        }
      }).catch(err => {
        console.error('加载附件失败:', err)
        this.attachmentList = []
      })
    },
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
    },
    resetFormData() {
      this.form = {
        id: undefined,
        yearSemesterId: undefined,
        issueDate: '',
        quotaDocNo: '',
        budgetProjectName: '',
        functionCategory: undefined,
        budgetLevel: undefined,
        quotaSourceType: 1,
        sourceQuotaId: undefined,
        memo: '',
        detailList: []
      }
    },
    fetchYearSemesters() {
      listYearSemesters({}).then(res => {
        this.yearSemesters = (res.rows || res.data || [])
      }).catch(err => {
        console.error('加载学年学期数据失败:', err)
        this.$modal.msgError('加载学年学期数据失败，请检查后端接口是否正常')
      })
    },
    getList() {
      this.loading = true
      // 并行获取列表数据和统计数据
      Promise.all([
        listSubsidyQuotaWithUsage(this.queryParams),
        getQuotaStatistics(this.queryParams)
      ])
        .then(([listRes, statRes]) => {
          this.quotaList = listRes.rows || []
          this.total = listRes.total || 0

          // 从后端统计接口获取统计数据
          console.log('统计接口返回的完整数据:', statRes)
          const statData = statRes.data || statRes
          console.log('解析后的统计数据:', statData)

          if (statData) {
            // 确保 statistics 对象存在
            if (!this.statistics) {
              this.statistics = {
                totalQuota: 0,
                allocatedAmount: 0,
                availableAmount: 0,
                allocatedByEconomy: {}
              }
            }
            // 确保数据正确转换
            const totalQuota = statData.totalQuota != null ? Number(statData.totalQuota) : 0
            const allocatedAmount = statData.allocatedAmount != null ? Number(statData.allocatedAmount) : 0
            const availableAmount = statData.availableAmount != null ? Number(statData.availableAmount) : 0

            // 使用 Vue.set 确保响应式更新
            this.$set(this.statistics, 'totalQuota', totalQuota)
            this.$set(this.statistics, 'allocatedAmount', allocatedAmount)
            this.$set(this.statistics, 'availableAmount', availableAmount)
            this.$set(this.statistics, 'allocatedByEconomy', statData.allocatedByEconomy || {})

            console.log('更新后的统计数据对象:', this.statistics)
            console.log('totalQuota值:', this.statistics.totalQuota)
            console.log('allocatedAmount值:', this.statistics.allocatedAmount)
            console.log('availableAmount值:', this.statistics.availableAmount)

            // 使用 $nextTick 确保在DOM更新后检查
            this.$nextTick(() => {
              // 直接访问计算属性的值（Vue会自动调用）
              const displayStats = this.displayStatistics
              console.log('nextTick后的displayStatistics值:', displayStats)
              console.log('displayStatistics类型:', typeof displayStats)
              console.log('displayStatistics.totalQuota:', displayStats && typeof displayStats === 'object' ? displayStats.totalQuota : 'undefined')
              console.log('displayStatistics.allocatedAmount:', displayStats && typeof displayStats === 'object' ? displayStats.allocatedAmount : 'undefined')
              console.log('displayStatistics.availableAmount:', displayStats && typeof displayStats === 'object' ? displayStats.availableAmount : 'undefined')
              console.log('statistics对象:', this.statistics)
              console.log('statistics.totalQuota:', this.statistics.totalQuota)
            })
          } else {
            // 如果后端没有返回统计，使用前端计算（兼容性处理）
            this.statistics = this.buildStatistics(this.quotaList)
            console.warn('后端未返回统计数据，使用前端计算')
            // 确保 statistics 对象存在
            if (!this.statistics) {
              this.statistics = {
                totalQuota: 0,
                allocatedAmount: 0,
                availableAmount: 0,
                allocatedByEconomy: {}
              }
            }
            this.statistics = this.buildStatistics(this.quotaList)
          }
        })
        .catch(err => {
          console.error('获取数据失败:', err)
          // 如果统计接口失败，至少获取列表数据
          listSubsidyQuotaWithUsage(this.queryParams)
            .then(res => {
              this.quotaList = res.rows || []
              this.total = res.total || 0
              // 确保 statistics 对象存在
              if (!this.statistics) {
                this.statistics = {
                  totalQuota: 0,
                  allocatedAmount: 0,
                  availableAmount: 0,
                  allocatedByEconomy: {}
                }
              }
              this.statistics = this.buildStatistics(this.quotaList)
            })
            .catch(listErr => {
              console.error('获取指标列表失败:', listErr)
              this.$modal.msgError('获取指标列表失败，请检查后端接口')
            })
        })
        .finally(() => {
          this.loading = false
        })
    },
    buildStatistics(list = []) {
      const stats = {
        totalQuota: 0,
        allocatedAmount: 0,
        availableAmount: 0,
        allocatedByEconomy: {}
      }
      if (!list || list.length === 0) {
        console.warn('指标列表为空，无法计算统计')
        return stats
      }
      list.forEach((item, index) => {
        const total = Number(item.totalQuota || 0)
        const allocated = Number(item.allocatedAmount || 0)
        const available = item.availableQuota != null
          ? Number(item.availableQuota)
          : (total - allocated)

        console.log(`指标${index + 1} (${item.quotaDocNo || item.id}):`, {
          totalQuota: total,
          allocatedAmount: allocated,
          availableQuota: item.availableQuota,
          calculatedAvailable: available
        })

        stats.totalQuota += total
        stats.allocatedAmount += allocated
        stats.availableAmount += available

         // 按经济分类统计已分配金额
        if (Array.isArray(item.detailList) && item.detailList.length) {
          item.detailList.forEach(detail => {
            if (!detail || Number(detail.allocatedAmount || 0) <= 0) return
            const key = String(detail.economyCategory || '')
            if (!key) return
            if (!stats.allocatedByEconomy[key]) {
              stats.allocatedByEconomy[key] = 0
            }
            stats.allocatedByEconomy[key] += Number(detail.allocatedAmount || 0)
          })
        } else if (item.economyCategory && allocated > 0) {
          const key = String(item.economyCategory)
          if (!stats.allocatedByEconomy[key]) {
            stats.allocatedByEconomy[key] = 0
          }
          stats.allocatedByEconomy[key] += allocated
        }
      })
      return stats
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
    },
    handleAdd() {
      this.title = '新增资助指标'
      this.resetFormData()
      this.sourceQuotaInfo = null
      this.loadAvailableSourceQuotas()
      this.open = true
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    handleUpdate(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) {
        this.$modal.msgWarning('请选择需要修改的记录')
        return
      }
      getSubsidyQuota(id).then(res => {
        const data = res.data || {}
        this.resetFormData()
        this.form = Object.assign(this.form, data)
        if (!Array.isArray(this.form.detailList)) {
          this.form.detailList = []
        }
        this.title = '修改资助指标'
        this.open = true
        this.$nextTick(() => {
          this.$refs.formRef && this.$refs.formRef.clearValidate()
          this.loadAttachments(id) // 加载附件
        })
      })
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid || !this.validateDetails()) {
          return
        }
        // 验证功能分类一致性（如果是结转指标）
        if ((this.form.quotaSourceType == 2 || this.form.quotaSourceType == 3) && this.form.sourceQuotaId) {
          if (!this.validateFunctionCategory()) {
            return
          }
        }
        const payload = Object.assign({}, this.form)
        payload.detailList = (this.form.detailList || []).map(d => ({
          id: d.id,
          economyCategory: d.economyCategory,
          totalAmount: d.totalAmount,
          allocatedAmount: d.allocatedAmount,
          status: d.status,
          memo: d.memo
        }))
        const req = payload.id ? updateSubsidyQuota(payload) : addSubsidyQuota(payload)
        req.then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        }).catch(err => {
          // 后端也会验证，这里只是前端提示
          console.error('保存失败:', err)
        })
      })
    },
    validateFunctionCategory() {
      if (!this.sourceQuotaInfo) {
        this.$modal.msgWarning('请先选择来源指标')
        return false
      }
      const sourceCategory = this.sourceQuotaInfo.functionCategory
      const targetCategory = this.form.functionCategory
      if (sourceCategory && targetCategory && sourceCategory !== targetCategory) {
        const sourceLabel = this.getFunctionCategoryLabel(sourceCategory)
        const targetLabel = this.getFunctionCategoryLabel(targetCategory)
        this.$modal.msgError(`功能分类不一致！来源指标为"${sourceLabel}"，当前指标为"${targetLabel}"。结转时功能分类必须相同（高中的资金不能转到小学和初中）。`)
        return false
      }
      return true
    },
    getFunctionCategoryLabel(value) {
      const dict = this.dict.type.sys_function_category || []
      const found = dict.find(d => String(d.value) === String(value))
      return found ? found.label : value
    },
    handleSourceQuotaChange(quotaId) {
      if (!quotaId) {
        this.sourceQuotaInfo = null
        return
      }
      // 查找选中的来源指标信息
      const quota = this.availableSourceQuotas.find(q => q.id === quotaId)
      if (quota) {
        this.sourceQuotaInfo = quota
        // 自动填充功能分类（如果当前未填写）
        if (!this.form.functionCategory && quota.functionCategory) {
          this.form.functionCategory = quota.functionCategory
        }
        // 验证功能分类是否一致
        if (this.form.functionCategory && quota.functionCategory &&
            this.form.functionCategory !== quota.functionCategory) {
          const sourceLabel = this.getFunctionCategoryLabel(quota.functionCategory)
          const targetLabel = this.getFunctionCategoryLabel(this.form.functionCategory)
          this.$modal.msgWarning(`功能分类不一致！来源指标为"${sourceLabel}"，当前指标为"${targetLabel}"。请修改为相同功能分类。`)
        }
      } else {
        this.sourceQuotaInfo = null
      }
    },
    loadAvailableSourceQuotas() {
      // 先获取当前学期ID，然后查询历史学期的指标
      getCurrentYearSemester().then(currentRes => {
        const currentSemesterId = currentRes.data?.id
        console.log('当前学期ID:', currentSemesterId)
        if (!currentSemesterId) {
          console.warn('无法获取当前学期ID，将查询所有指标')
        }

        // 查询可用的来源指标（先尝试使用listWithUsage接口）
        // 注意：不限制status，因为"部分分配"和"已分配"状态的指标也可能有剩余额度
        const query = {
          // 不传status，查询所有状态的指标，后续通过剩余额度过滤
          pageNum: 1,
          pageSize: 1000 // 获取足够多的数据
        }

        // 先尝试使用listWithUsage接口
        listSubsidyQuotaWithUsage(query).then(res => {
          console.log('listSubsidyQuotaWithUsage 响应:', res)
          const allQuotas = res.rows || res.data || []
          console.log('查询到的所有指标:', allQuotas)

          if (allQuotas.length === 0) {
            // 如果listWithUsage返回空，可能是权限问题，尝试使用list接口
            console.warn('listWithUsage返回空，尝试使用list接口')
            return listSubsidyQuotaWithUsage(query).then(listRes => {
              console.log('listSubsidyQuotaWithUsage 响应:', listRes)
              const listQuotas = listRes.rows || listRes.data || []
              console.log('list接口查询到的所有指标:', listQuotas)
              return this.filterSourceQuotas(listQuotas, currentSemesterId)
            })
          }

          return this.filterSourceQuotas(allQuotas, currentSemesterId)
        }).then(filteredQuotas => {
          this.availableSourceQuotas = filteredQuotas || []
          console.log('过滤后的可用来源指标:', this.availableSourceQuotas)

          if (this.availableSourceQuotas.length === 0) {
            console.warn('没有找到可用的来源指标，请检查：1.是否有历史学期的指标 2.历史指标是否有剩余额度 3.指标状态是否为启用')
          }
        }).catch(err => {
          console.error('加载来源指标列表失败:', err)
          console.error('错误详情:', err.response || err.message)
          // 如果listWithUsage失败，尝试使用list接口
          const query = {
            // 不传status，查询所有状态的指标
            pageNum: 1,
            pageSize: 1000
          }
          listSubsidyQuotaWithUsage(query).then(listRes => {
            const listQuotas = listRes.rows || listRes.data || []
            this.availableSourceQuotas = this.filterSourceQuotas(listQuotas, currentSemesterId)
          }).catch(e => {
            console.error('使用listWithUsage接口也失败:', e)
            this.availableSourceQuotas = []
          })
        })
      }).catch(err => {
        console.error('获取当前学期失败:', err)
        // 如果获取当前学期失败，仍然尝试加载所有指标
        const query = {
          // 不传status，查询所有状态的指标
          pageNum: 1,
          pageSize: 1000
        }
        listSubsidyQuotaWithUsage(query).then(res => {
          const allQuotas = res.rows || res.data || []
          this.availableSourceQuotas = this.filterSourceQuotas(allQuotas, null)
        }).catch(e => {
          console.error('加载来源指标列表失败:', e)
          this.availableSourceQuotas = []
        })
      })
    },
    filterSourceQuotas(allQuotas, currentSemesterId) {
      // 过滤：只保留历史学期的指标（year_semester_id < 当前学期ID），且有剩余额度
      // 注意：此过滤逻辑保留在前端，因为需要排除当前编辑的指标，且后端已返回isHistoricalSemester和availableQuota字段
      return allQuotas.filter(q => {
        // 排除当前编辑的指标
        if (this.form.id && q.id === this.form.id) {
          return false
        }

        // 排除"已锁定"状态的指标（status=4），其他状态都可以作为来源
        if (q.status === 4) {
          return false
        }

        // 只保留历史学期的指标（使用后端返回的isHistoricalSemester字段）
        const isHistorical = q.isHistoricalSemester === true || q.isHistoricalSemester === 1 || q.isHistoricalSemester === '1'
        if (!isHistorical) {
          return false
        }

        // 只保留有剩余额度的指标（使用后端返回的availableQuota字段）
        const available = Number(q.availableQuota != null ? q.availableQuota : 0)
        if (available <= 0) {
          return false
        }

        return true
      })
    },
    validateDetails() {
      const list = this.form.detailList || []
      if (!list.length) {
        this.$modal.msgWarning('请至少添加一条经济分类明细')
        return false
      }
      const categories = []
      for (const d of list) {
        if (!d.economyCategory) {
          this.$modal.msgWarning('请填写经济分类')
          return false
        }
        if (d.totalAmount === undefined || d.totalAmount === null) {
          this.$modal.msgWarning('请填写明细金额')
          return false
        }
        if (Number(d.totalAmount) < Number(d.allocatedAmount || 0)) {
          this.$modal.msgWarning('明细金额不能小于已分配金额')
          return false
        }
        categories.push(String(d.economyCategory))
      }
      if (new Set(categories).size !== categories.length) {
        this.$modal.msgWarning('经济分类不允许重复')
        return false
      }
      return true
    },
    handleDetailAdd() {
      if (!Array.isArray(this.form.detailList)) {
        this.form.detailList = []
      }
      this.form.detailList.push({
        id: undefined,
        economyCategory: undefined,
        totalAmount: 0,
        allocatedAmount: 0,
        status: 1,
        memo: ''
      })
    },
    handleDetailRemove(index) {
      this.form.detailList.splice(index, 1)
    },
    openDetail(row) {
      if (!row || !row.id) return
      getSubsidyQuota(row.id).then(res => {
        this.detailRecord = res.data || null
        this.detailVisible = true
        // 加载附件列表
        this.loadDetailAttachments(row.id)
      })
    },
    // 根据ID打开详情（用于来源指标链接）
    openDetailById(quotaId) {
      if (!quotaId) return
      getSubsidyQuota(quotaId).then(res => {
        this.detailRecord = res.data || null
        this.detailVisible = true
        // 加载附件列表
        this.loadDetailAttachments(quotaId)
      }).catch(err => {
        console.error('获取指标详情失败:', err)
        this.$modal.msgError('获取指标详情失败')
      })
    },
    // 加载详情页附件
    loadDetailAttachments(quotaId) {
      if (!quotaId) {
        this.detailAttachmentList = []
        return
      }
      listQuotaAttachmentByQuotaId(quotaId).then(res => {
        if (res.code === 200 && res.data) {
          this.detailAttachmentList = (res.data || []).map(item => ({
            id: item.id,
            name: item.fileName,
            size: item.fileSize,
            url: item.fileUrl,
            uid: item.id
          }))
        } else {
          this.detailAttachmentList = []
        }
      }).catch(err => {
        console.error('加载详情附件失败:', err)
        this.detailAttachmentList = []
      })
    },
    // 判断明细是否可编辑（使用后端返回的字段）
    isDetailEditable(detail) {
      if (!detail) return false
      // 优先使用后端返回的isEditable字段
      if (detail.isEditable !== undefined && detail.isEditable !== null) {
        return detail.isEditable === true || detail.isEditable === 1 || detail.isEditable === '1' || detail.isEditable === 'true'
      }
      // 降级逻辑（向后兼容）
      const hasAllocation = Number(detail.allocatedAmount || 0) > 0
      const isCarryOver = detail && detail.sourceDetailId != null && detail.sourceDetailId !== undefined
      return !hasAllocation && !isCarryOver
    },
    // 判断明细是否为结转资金（用于显示提示信息）
    isCarryOverDetail(detail) {
      return detail && detail.sourceDetailId != null && detail.sourceDetailId !== undefined
    },
    detailAvailable(detail) {
      const total = Number(detail.totalAmount || 0)
      const allocated = Number(detail.allocatedAmount || 0)
      return this.formatAmount(total - allocated)
    },
    cancel() {
      this.open = false
    },
    /** 处理更多操作 */
    handleMoreAction(command) {
      const { action, row } = command;
      switch (action) {
        case 'edit':
          this.handleUpdate(row);
          break;
        case 'allocate':
          this.handleAllocate(row);
          break;
        case 'carryOver':
          this.handleCarryOver(row);
          break;
        case 'reclaim':
          this.handleReclaim(row);
          break;
        case 'delete':
          this.handleDelete(row);
          break;
        default:
          break;
      }
    },
    handleReclaim(row) {
      if (!row || !row.id) {
        this.$modal.msgWarning('请选择一条指标记录')
        return
      }
      this.currentQuota = row
      this.reclaimDialogVisible = true
      this.reclaimBudgetList = []
      this.reclaimSelectedBudgets = []
      this.loadReclaimBudgets()
    },
    loadReclaimBudgets() {
      if (!this.currentQuota || !this.currentQuota.id) {
        return
      }
      this.reclaimLoading = true
      listAllocatedBudgets(this.currentQuota.id)
        .then(res => {
          this.reclaimBudgetList = res.data || []
        })
        .catch(err => {
          console.error('加载预算列表失败:', err)
          this.$modal.msgError('加载预算列表失败')
        })
        .finally(() => {
          this.reclaimLoading = false
        })
    },
    // 判断是否可收回（使用后端返回的字段）
    isReclaimable(budget) {
      if (!budget) return false
      // 优先使用后端返回的isReclaimable字段
      if (budget.isReclaimable !== undefined && budget.isReclaimable !== null) {
        return budget.isReclaimable === true || budget.isReclaimable === 1 || budget.isReclaimable === '1' || budget.isReclaimable === 'true'
      }
      // 降级逻辑（向后兼容）
      const used = Number(budget.usedAmount || 0)
      const locked = Number(budget.lockedAmount || 0)
      return used === 0 && locked === 0
    },
    // 获取收回状态文本（使用后端返回的字段）
    getReclaimStatusText(budget) {
      if (!budget) return ''
      // 优先使用后端返回的reclaimStatus字段
      if (budget.reclaimStatus) {
        return budget.reclaimStatus
      }
      // 降级逻辑（向后兼容）
      const used = Number(budget.usedAmount || 0)
      const locked = Number(budget.lockedAmount || 0)
      if (used > 0) {
        return '已使用'
      } else if (locked > 0) {
        return '已锁定'
      } else {
        return '可收回'
      }
    },
    // 获取收回状态类型（使用后端返回的字段）
    getReclaimStatusType(budget) {
      if (!budget) return ''
      // 优先使用后端返回的reclaimStatusType字段
      if (budget.reclaimStatusType) {
        return budget.reclaimStatusType
      }
      // 降级逻辑（向后兼容）
      const used = Number(budget.usedAmount || 0)
      const locked = Number(budget.lockedAmount || 0)
      if (used > 0) {
        return 'danger'
      } else if (locked > 0) {
        return 'warning'
      } else {
        return 'success'
      }
    },
    handleReclaimSelectionChange(selection) {
      // el-table 的 selectable 已经过滤了不可收回的预算，所以这里直接使用 selection
      this.reclaimSelectedBudgets = selection || []
    },
    submitReclaim() {
      if (this.reclaimSelectedBudgets.length === 0) {
        this.$modal.msgWarning('请选择要收回的预算')
        return
      }

      // 检查是否有不可收回的预算
      const unreclaimable = this.reclaimSelectedBudgets.filter(budget => !this.isReclaimable(budget))
      if (unreclaimable.length > 0) {
        const messages = []
        unreclaimable.forEach(budget => {
          const used = Number(budget.usedAmount || 0)
          const locked = Number(budget.lockedAmount || 0)
          if (used > 0) {
            messages.push(`预算[ID:${budget.id}]已使用金额 ${this.formatAmount(used)} 元`)
          } else if (locked > 0) {
            messages.push(`预算[ID:${budget.id}]有锁定金额 ${this.formatAmount(locked)} 元`)
          }
        })
        this.$modal.msgError('以下预算无法收回：\n' + messages.join('\n'))
        return
      }

      const budgetIds = this.reclaimSelectedBudgets.map(b => b.id)
      const totalAmount = this.reclaimSelectedBudgets.reduce((sum, b) => {
        return sum + Number(b.budgetAmount || 0)
      }, 0)

      this.$modal.confirm(
        `确定要收回 ${this.reclaimSelectedBudgets.length} 个预算，共 ${this.formatAmount(totalAmount)} 元吗？收回后，这些预算将被删除，指标已分配金额将相应减少。`
      ).then(() => {
        this.reclaimSubmitting = true
        return reclaimBudgets(budgetIds)
      }).then(() => {
        this.$modal.msgSuccess('收回成功')
        this.reclaimDialogVisible = false
        this.getList()
      }).catch(err => {
        if (err && err.message) {
          this.$modal.msgError(err.message)
        } else {
          this.$modal.msgError('收回失败')
        }
      }).finally(() => {
        this.reclaimSubmitting = false
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? [row.id] : this.ids
      if (!ids.length) {
        this.$modal.msgWarning('请选择要删除的记录')
        return
      }
      const display = ids.join(',')
      this.$modal.confirm(`是否确认删除编号为"${display}"的指标记录？`).then(() => {
        return delSubsidyQuota(display)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/subsidyQuota/export', { ...this.queryParams }, `subsidy_quota_${new Date().getTime()}.xlsx`)
    },
    handleFixAllocated() {
      this.$modal.confirm('确定根据学期预算数据重新校准所有指标的已分配金额吗？').then(() => {
        return fixQuotaAllocatedAmount()
      }).then(() => {
        this.$modal.msgSuccess('校准成功')
        this.getList()
      }).catch(() => {})
    },
    handleAllocate(row) {
      if (!row || !row.id) {
        this.$modal.msgWarning('请选择一条指标记录')
        return
      }
      this.currentQuota = row
      this.allocationForm = {
        yearSemesterId: row.yearSemesterId || undefined
      }
      this.allocationSelection = { current: [], carryOver: [] }
      this.allocationAmounts = {}
      this.allocationDialogVisible = true
      listAllocatableDetails({ quotaId: row.id }).then(res => {
        this.allocationDetails = res.data || []
      })
      this.$nextTick(() => {
        this.$refs.allocationFormRef && this.$refs.allocationFormRef.clearValidate()
      })
    },
    handleAllocationSelectionChange(type, val) {
      const valueArray = Array.isArray(val) ? val : []
      if (type === 'current') {
        this.allocationSelection.current = valueArray
      } else {
        this.allocationSelection.carryOver = valueArray
      }
      this.cleanupAllocationAmounts()
    },
    buildAllocationRows(ids = []) {
      if (!Array.isArray(ids) || !Array.isArray(this.allocationDetails)) {
        return []
      }
      return ids
        .map(id => {
          const detail = this.findAllocationDetailById(id)
          if (!detail) {
            return null
          }
          const available = Number(detail.availableAmount || 0)
          return {
            id,
            detail,
            availableAmount: available
          }
        })
        .filter(Boolean)
    },
    findAllocationDetailById(id) {
      if (!id || !Array.isArray(this.allocationDetails)) {
        return null
      }
      return this.allocationDetails.find(item => String(item.id) === String(id)) || null
    },
    cleanupAllocationAmounts() {
      const keep = new Set([
        ...(this.allocationSelection.current || []),
        ...(this.allocationSelection.carryOver || [])
      ].map(id => String(id)))
      Object.keys(this.allocationAmounts || {}).forEach(key => {
        if (!keep.has(String(key))) {
          this.$delete(this.allocationAmounts, key)
        }
      })
    },
    allocationTotal(type) {
      const ids = type === 'carryOver' ? (this.allocationSelection.carryOver || []) : (this.allocationSelection.current || [])
      return ids.reduce((sum, id) => {
        const amount = Number(this.allocationAmounts[id] || 0)
        return sum + (isNaN(amount) ? 0 : amount)
      }, 0)
    },
    allocationHeaderStyle() {
      return {
        background: '#e8f2ff',
        color: '#303133',
        fontWeight: 600,
        textAlign: 'center'
      }
    },
    allocationCellStyle() {
      return {
        textAlign: 'center'
      }
    },
    submitAllocation() {
      const allIds = [...(this.allocationSelection.current || []), ...(this.allocationSelection.carryOver || [])]
      if (!allIds.length) {
        this.$modal.msgError('请至少选择一条经济分类')
        return
      }
      const items = []
      for (const id of allIds) {
        const detail = this.findAllocationDetailById(id)
        if (!detail) {
          this.$modal.msgError('存在已失效的经济分类，请重新选择')
          return
        }
        const amount = Number(this.allocationAmounts[id] || 0)
        const available = Number(detail.availableAmount || 0)
        if (!amount || amount <= 0) {
          this.$modal.msgError(`请填写 ${this.economyLabel(detail.economyCategory)} 的分配金额`)
          return
        }
        if (amount > available) {
          this.$modal.msgError(`${this.economyLabel(detail.economyCategory)} 分配金额不能超过可分配额度 ${this.formatAmount(available)} 元`)
          return
        }
        items.push({
          quotaDetailId: id,
          allocateAmount: amount,
          budgetType: '',
          memo: ''
        })
      }
      const targetSemester = this.currentQuota?.yearSemesterId || this.allocationForm.yearSemesterId
      if (!targetSemester) {
        this.$modal.msgError('缺少目标学年学期信息')
        return
      }
      const payload = {
        yearSemesterId: targetSemester,
        items
      }
      this.allocationSubmitting = true
      allocateQuotaBatch(payload)
        .then(() => {
          this.$modal.msgSuccess('分配成功')
          // 通知学期预算列表刷新
          this.$store.dispatch('subsidy/setShouldRefreshBudgetList', true)
          this.allocationDialogVisible = false
          this.getList()
        })
        .finally(() => {
          this.allocationSubmitting = false
        })
    },
    formatAmount(val) {
      const num = Number(val || 0)
      return num.toFixed(2)
    },
    renderEconomyText(row, options = {}) {
      const { allocatedOnly = false } = options
      if (!row) {
        return ''
      }
      const list = []
      const pushValue = val => {
        if (val !== undefined && val !== null && val !== '') {
          list.push(String(val))
        }
      }
      if (!allocatedOnly && row.economyCategory) {
        pushValue(row.economyCategory)
      }
      if (Array.isArray(row.detailList)) {
        row.detailList.forEach(detail => {
          if (!detail || !detail.economyCategory) {
            return
          }
          if (allocatedOnly && Number(detail.allocatedAmount || 0) <= 0) {
            return
          }
          pushValue(detail.economyCategory)
        })
      }
      const unique = Array.from(new Set(list.filter(Boolean)))
      if (!unique.length && allocatedOnly && row.economyCategory && Number(row.allocatedAmount || 0) > 0) {
        unique.push(String(row.economyCategory))
      }
      if (!unique.length) {
        return ''
      }
      return unique.map(val => this.economyLabel(val)).join('、')
    },
    // 已分配金额列表仅展示总金额（不再按经济分类拆分）
    allocatedStatLines() {
      const map = (this.statistics && this.statistics.allocatedByEconomy) || {}
      const keys = Object.keys(map)
      if (!keys.length) return []
      return keys.map(key => ({
        value: key,
        label: this.economyLabel(key),
        amount: map[key]
      }))
    },
    // 显示统计（如果选中了指标，显示该指标的统计；否则显示所有指标的总计）
    displayStatistics() {
      if (this.selectedQuota) {
        return {
          totalQuota: Number(this.selectedQuota.totalQuota || 0),
          allocatedAmount: Number(this.selectedQuota.allocatedAmount || 0),
          availableAmount: Number(this.selectedQuota.availableQuota != null ? this.selectedQuota.availableQuota : 0),
          allocatedByEconomy: this.buildSelectedQuotaAllocatedByEconomy()
        }
      }
      // 确保返回一个包含正确值的对象
      const stats = this.statistics || {}
      return {
        totalQuota: Number(stats.totalQuota || 0),
        allocatedAmount: Number(stats.allocatedAmount || 0),
        availableAmount: Number(stats.availableAmount || 0),
        allocatedByEconomy: stats.allocatedByEconomy || {}
      }
    },
    // 显示已分配统计行（如果选中了指标，显示该指标的分配明细）
    displayAllocatedStatLines() {
      if (this.selectedQuota) {
        return this.buildSelectedQuotaAllocatedLines()
      }
      return this.allocatedStatLines
    },
    formatAllocationYearSemester() {
      if (!this.currentQuota) {
        return ''
      }
      const targetId = this.currentQuota.yearSemesterId || this.allocationForm.yearSemesterId
      if (targetId && Array.isArray(this.yearSemesters) && this.yearSemesters.length) {
        const matched = this.yearSemesters.find(item => String(item.id) === String(targetId))
        if (matched) {
          return `${matched.schoolYear || matched.school_year || ''} ${matched.semesterLabel || ''}`
        }
      }
      const year = this.currentQuota.schoolYear || ''
      const semesterText = this.currentQuota.semesterLabel || ''
      return `${year} ${semesterText}`.trim()
    },
    // 判断是否可以结转（使用后端返回的字段）
    canCarryOver(row) {
      if (!row || !row.id) return false
      // 直接使用后端返回的canCarryOver字段（支持Boolean和数字类型）
      if (row.canCarryOver !== undefined && row.canCarryOver !== null) {
        return row.canCarryOver === true || row.canCarryOver === 1 || row.canCarryOver === '1' || row.canCarryOver === 'true'
      }
      // 如果后端没有返回该字段，降级使用原有逻辑（向后兼容）
      if (row.status === 4) return false
      const available = Number(row.availableQuota || row.carryOverAmount || 0)
      if (available <= 0) return false
      const isHistorical = row.isHistoricalSemester === true || row.isHistoricalSemester === 1 || row.isHistoricalSemester === '1'
      return isHistorical
    },
    // 判断指标是否已经结转（用于显示“结”标记，使用后端返回的字段）
    hasCarriedOver(row) {
      if (!row || !row.id) return false
      // 直接使用后端返回的hasCarriedOver字段（支持Boolean和数字类型）
      if (row.hasCarriedOver !== undefined && row.hasCarriedOver !== null) {
        return row.hasCarriedOver === true || row.hasCarriedOver === 1 || row.hasCarriedOver === '1' || row.hasCarriedOver === 'true'
      }
      // 如果后端没有返回该字段，降级使用原有逻辑（向后兼容）
      const isHistorical = row.isHistoricalSemester === true || row.isHistoricalSemester === 1 || row.isHistoricalSemester === '1'
      if (!isHistorical) return false
      const allocatedAmount = Number(row.allocatedAmount || 0)
      return allocatedAmount > 0
    },
    // 判断指标是否有结转金额（用于Tooltip和角标显示）
    hasCarriedOverAmount(row) {
      if (!row || !row.id) return false
      const carriedOverAmount = Number(row.carriedOverAmount || 0)
      return carriedOverAmount > 0
    },
    // 获取结转按钮被禁用的原因（用于提示）
    getCarryOverDisabledReason(row) {
      if (!row || !row.id) return '无效的指标记录'
      if (row.status === 4) return '指标已锁定，不允许结转'
      if (row.canCarryOver === 0 || row.canCarryOver === false) {
        if (row.isHistoricalSemester !== true && row.isHistoricalSemester !== 1) {
          return '只有历史学期的指标才能结转'
        }
        const available = Number(row.availableQuota || row.carryOverAmount || 0)
        if (available <= 0) {
          return '指标没有剩余额度，无法结转'
        }
        return '不允许结转'
      }
      if (row.canCarryOver === undefined || row.canCarryOver === null) {
        return '后端未返回结转状态'
      }
      return ''
    },
    // 打开结转对话框
    handleCarryOver(row) {
      if (!row || !row.id) {
        this.$modal.msgWarning('请选择一条指标记录')
        return
      }
      // 获取当前学期ID
      getCurrentYearSemester().then(currentRes => {
        this.currentSemesterId = currentRes.data?.id
        // 加载可用的来源指标列表
        this.loadCarryOverAvailableQuotas()
        // 初始化表单，预选当前行
        this.carryOverForm = {
          quotaSourceType: 2, // 默认上学期结转
          sourceQuotaId: row.id, // 预选当前行
          sourceYearSemesterId: row.yearSemesterId, // 来源指标的学年学期
          economyCategory: undefined,
          yearSemesterId: this.currentSemesterId, // 默认当前学期
          functionCategory: row.functionCategory, // 预选当前行的功能分类
          targetFunctionCategory: row.functionCategory, // 默认与来源指标相同的功能分类
          quotaDocNo: '',
          detailList: []
        }
        this.targetQuotaOptions = []
        // 如果预选了来源指标，自动加载其信息
        if (row.id) {
          this.handleCarryOverSourceQuotaChange(row.id)
        }
        // 重置表单状态
        this.isFormDisabled = false
        this.carryOverDialogVisible = true
        this.$nextTick(() => {
          this.$refs.carryOverFormRef && this.$refs.carryOverFormRef.clearValidate()
        })
      }).catch(err => {
        console.error('获取当前学期失败:', err)
        this.$modal.msgError('获取当前学期失败，请重试')
      })
    },
    // 加载结转可用的来源指标列表
    loadCarryOverAvailableQuotas() {
      const query = {
        pageNum: 1,
        pageSize: 1000
      }
      listSubsidyQuotaWithUsage(query).then(res => {
        const allQuotas = res.rows || res.data || []
        // 过滤：只保留历史学期且有剩余额度的指标（使用后端返回的字段）
        this.carryOverAvailableQuotas = allQuotas.filter(q => {
          // 排除已锁定状态
          if (q.status === 4) return false
          // 只保留历史学期的指标（使用后端返回的isHistoricalSemester字段，兼容Boolean和数字类型）
          const isHistorical = q.isHistoricalSemester === true || q.isHistoricalSemester === 1
          if (!isHistorical) {
            return false
          }
          // 只保留有上期结余的指标（使用后端返回的carryOverAmount字段）
          const carryOver = Number(q.carryOverAmount || 0)
          return carryOver > 0
        })
      }).catch(err => {
        console.error('加载结转可用指标列表失败:', err)
        // 降级使用list接口
        listSubsidyQuotaWithUsage(query).then(listRes => {
          const listQuotas = listRes.rows || listRes.data || []
          // 使用后端返回的字段进行过滤
          this.carryOverAvailableQuotas = listQuotas.filter(q => {
            if (q.status === 4) return false
            // 使用后端返回的isHistoricalSemester字段（兼容Boolean和数字类型）
            const isHistorical = q.isHistoricalSemester === true || q.isHistoricalSemester === 1
            if (!isHistorical) {
              return false
            }
            // 使用后端返回的carryOverAmount字段
            const carryOver = Number(q.carryOverAmount || 0)
            return carryOver > 0
          })
        }).catch(e => {
          console.error('使用listWithUsage接口也失败:', e)
          this.carryOverAvailableQuotas = []
        })
      })
    },
    // 结转来源类型改变
    handleCarryOverSourceTypeChange(value) {
      // 如果改为历史学期结转，可能需要调整逻辑
      // 目前先不做特殊处理
    },
    // 结转功能分类改变
    handleCarryOverFunctionCategoryChange(value) {
      // 功能分类改变时，清空学年学期和来源指标选择
      this.carryOverForm.sourceYearSemesterId = undefined
      this.carryOverForm.sourceQuotaId = undefined
      this.carryOverSourceQuotaInfo = null
      this.carryOverForm.quotaDocNo = ''
      this.carryOverForm.economyCategory = undefined
      // 如果目标功能分类未设置，同步更新
      if (!this.carryOverForm.targetFunctionCategory) {
        this.carryOverForm.targetFunctionCategory = value
      }
    },
    // 来源指标学年学期改变
    handleCarryOverSourceYearSemesterChange(value) {
      // 学年学期改变时，清空来源指标选择
      this.carryOverForm.sourceQuotaId = undefined
      this.carryOverSourceQuotaInfo = null
      this.carryOverForm.quotaDocNo = ''
      this.carryOverForm.economyCategory = undefined
    },
    // 结转经济分类改变（已移除选择，改为显示，此方法保留但不使用）
    handleCarryOverEconomyCategoryChange(value) {
      // 经济分类改变时，重新生成指标文号和标题
      this.generateCarryOverDocInfo()
    },
    // 结转学年学期改变
    handleCarryOverYearSemesterChange(value) {
      // 学年学期改变时，重新生成指标文号和标题
      this.generateCarryOverDocInfo()
    },
    // 目标功能分类改变
    handleTargetFunctionCategoryChange(value) {
      // 功能分类改变时，清空指标文号并重新加载
      this.carryOverForm.quotaDocNo = ''
      this.loadTargetQuotaOptions().then(() => {
        this.generateCarryOverDocInfo()
      })
    },
    // 目标学年学期改变
    handleTargetYearSemesterChange(value) {
      // 学年学期改变时，重新加载指标文号选项
      this.carryOverForm.quotaDocNo = ''
      this.loadTargetQuotaOptions().then(() => {
        this.generateCarryOverDocInfo()
      })
    },
    // 加载目标指标文号选项（调用后端接口）
    loadTargetQuotaOptions() {
      if (!this.carryOverForm.yearSemesterId || !this.carryOverForm.targetFunctionCategory) {
        this.targetQuotaOptions = []
        return Promise.resolve()
      }
      this.loadingTargetQuotas = true
      return getQuotaDocNos(this.carryOverForm.yearSemesterId, this.carryOverForm.targetFunctionCategory)
        .then(res => {
          const docNos = res.data || []
          this.targetQuotaOptions = docNos.map(docNo => ({ quotaDocNo: docNo }))
          // 如果只有一个选项，自动选中
          if (this.targetQuotaOptions.length === 1) {
            this.carryOverForm.quotaDocNo = this.targetQuotaOptions[0].quotaDocNo
          }
          this.loadingTargetQuotas = false
        })
        .catch(err => {
          console.error('加载目标指标文号失败:', err)
          this.targetQuotaOptions = []
          this.loadingTargetQuotas = false
          return Promise.reject(err)
        })
    },
    // 结转下一步
    // 生成结转指标的文号和标题
    generateCarryOverDocInfo() {
      const functionCategory = this.carryOverForm.targetFunctionCategory || this.carryOverForm.functionCategory
      if (!functionCategory || !this.carryOverForm.yearSemesterId) {
        return
      }

      // 获取功能分类名称
      const functionCategoryLabel = this.getFunctionCategoryLabel(functionCategory)

      // 获取经济分类名称
      let economyCategoryLabels = []
      if (this.carryOverForm.economyCategory) {
        // 如果选择了特定经济分类，只显示该经济分类
        economyCategoryLabels.push(this.economyLabel(this.carryOverForm.economyCategory))
      } else if (this.carryOverSourceQuotaDetails && this.carryOverSourceQuotaDetails.length > 0) {
        // 如果没有选择，显示所有可结转的经济分类
        economyCategoryLabels = this.carryOverSourceQuotaDetails.map(detail =>
          this.economyLabel(detail.economyCategory)
        )
      }

      // 如果已有指标文号选项，且只有一个选项，自动选中（仅用于UI展示）
      if (this.targetQuotaOptions && this.targetQuotaOptions.length === 1) {
        this.carryOverForm.quotaDocNo = this.targetQuotaOptions[0].quotaDocNo
        return
      }

      // 获取学年学期信息
      const semester = this.yearSemesters.find(ys => ys.id === this.carryOverForm.yearSemesterId)
      const yearSemesterText = semester
        ? `${semester.school_year || semester.schoolYear || ''}${semester.semesterLabel || ''}`
        : ''

      // 生成指标文号（仅用于UI提示，实际是否创建新指标由后端判断）
      // 格式：功能分类名称（经济分类名称）—学年学期结余资金
      const economyText = economyCategoryLabels.length > 0
        ? `（${economyCategoryLabels.join('、')}）`
        : ''
      const suffix = yearSemesterText ? `—${yearSemesterText}结余资金` : '结余资金'

      // 如果已有选项，不自动生成，让用户从下拉列表选择
      // 如果没有选项，生成一个默认的指标文号（后端会根据实际情况决定是更新还是创建）
      if (!this.targetQuotaOptions || this.targetQuotaOptions.length === 0) {
        this.carryOverForm.quotaDocNo = `${functionCategoryLabel}${economyText}${suffix}`
      }
    },
    // 结转来源指标改变
    handleCarryOverSourceQuotaChange(quotaId) {
      if (!quotaId) {
        this.carryOverSourceQuotaInfo = null
        this.carryOverForm.economyCategory = undefined
        this.carryOverForm.quotaDocNo = ''
        this.carryOverForm.detailList = []
        return Promise.resolve()
      }
      // 获取来源指标的详细信息
      return getSubsidyQuota(quotaId).then(res => {
        const sourceQuota = res.data || {}
        this.carryOverSourceQuotaInfo = sourceQuota

        // 调试：打印明细信息，检查 availableAmount 是否正确计算
        if (sourceQuota.detailList && Array.isArray(sourceQuota.detailList)) {
          console.log('来源指标明细列表（已计算可结转金额）:', sourceQuota.detailList.map(d => ({
            经济分类: d.economyCategory,
            总金额: d.totalAmount,
            已分配: d.allocatedAmount,
            原始剩余: (Number(d.totalAmount || 0) - Number(d.allocatedAmount || 0)).toFixed(2),
            可结转金额: d.availableAmount
          })))
        }

        // 确保功能分类一致（如果之前选择的功能分类与来源指标不一致，更新为来源指标的功能分类）
        if (this.carryOverForm.functionCategory !== sourceQuota.functionCategory) {
          this.carryOverForm.functionCategory = sourceQuota.functionCategory
        }
        // 如果目标功能分类未设置，默认与来源指标相同
        if (!this.carryOverForm.targetFunctionCategory) {
          this.carryOverForm.targetFunctionCategory = sourceQuota.functionCategory
        }

        // 清空经济分类选择
        this.carryOverForm.economyCategory = undefined

        // 如果已选择目标学年学期和功能分类，加载目标指标文号选项并生成指标文号
        if (this.carryOverForm.yearSemesterId && this.carryOverForm.targetFunctionCategory) {
          this.loadTargetQuotaOptions()
          this.generateCarryOverDocInfo()
        }
      }).catch(err => {
        console.error('获取来源指标详情失败:', err)
        this.$modal.msgError('获取来源指标详情失败')
        return Promise.reject(err)
      })
    },
    // 提交结转
    submitCarryOver() {
      this.$refs.carryOverFormRef.validate(valid => {
        if (!valid) return

        // 确保指标文号已生成
        if (!this.carryOverForm.quotaDocNo) {
          this.generateCarryOverDocInfo()
        }

        // 构建提交数据 - 所有业务逻辑判断都在后端完成
        // 备注由后端自动生成：从x学年x学期结转
        const payload = {
          sourceQuotaId: this.carryOverForm.sourceQuotaId,
          targetQuotaDocNo: this.carryOverForm.quotaDocNo,
          yearSemesterId: this.carryOverForm.yearSemesterId,
          functionCategory: this.carryOverForm.targetFunctionCategory || this.carryOverForm.functionCategory,
          economyCategories: null, // 默认结转全部经济分类（不再提供选择）
          memo: null // 由后端自动生成
        }

        // 先禁用表单
        this.isFormDisabled = true
        this.carryOverSubmitting = true

        carryOverToTargetQuota(payload)
          .then(() => {
          // 显示成功消息
          this.$modal.msgSuccess('结转成功')
          // 延迟关闭对话框，让用户看到成功消息
          setTimeout(() => {
            this.carryOverDialogVisible = false
            this.getList()
          }, 1500)
        })
        .catch(err => {
          console.error('结转失败:', err)
          this.$modal.msgError(err.msg || '结转失败')
          // 失败时恢复表单状态
          this.isFormDisabled = false
        })
        .finally(() => {
          this.carryOverSubmitting = false
        })
      })
    },
    // 计算结转金额（默认计算所有经济分类的总和）
    calculateCarryOverAmount() {
      if (!this.carryOverSourceQuotaInfo || !Array.isArray(this.carryOverSourceQuotaInfo.detailList)) {
        return 0
      }
      let total = 0
      // 默认结转所有经济分类
      this.carryOverSourceQuotaInfo.detailList.forEach(detail => {
        total += Number(detail.availableAmount || 0)
      })
      return total
    },
    economyLabel(value) {
      const list = this.dict.type.sys_economy_category || []
      const found = list.find(d => String(d.value) === String(value))
      return found ? found.label : value
    },
    // 处理表格行点击
    handleRowClick(row, column, event) {
      // 如果点击的是操作列或固定列，不触发选择
      if (event && (
        event.target.closest('.el-table__fixed-right') ||
        event.target.closest('.el-table__fixed') ||
        event.target.closest('.action-buttons') ||
        event.target.closest('button') ||
        event.target.closest('.el-dropdown')
      )) {
        return
      }
      // 如果点击的是同一个行，取消选择
      if (this.selectedQuota && this.selectedQuota.id === row.id) {
        this.clearSelectedQuota()
      } else {
        this.selectedQuota = { ...row }
        // 如果有详细信息，加载详细数据
        if (row.id && (!row.detailList || !row.detailList.length)) {
          this.loadQuotaDetails(row.id)
        }
      }
    },
    // 加载指标详细信息
    loadQuotaDetails(quotaId) {
      getSubsidyQuota(quotaId).then(res => {
        const data = res.data || {}
        // 更新选中指标的数据
        if (this.selectedQuota && this.selectedQuota.id === quotaId) {
          this.selectedQuota = { ...this.selectedQuota, ...data }
        }
      }).catch(err => {
        console.error('加载指标详情失败:', err)
      })
    },
    // 清除选中的指标
    clearSelectedQuota() {
      this.selectedQuota = null
    },
    // 获取表格行的类名
    getRowClassName({ row }) {
      if (this.selectedQuota && this.selectedQuota.id === row.id) {
        return 'selected-row'
      }
      return ''
    },
    // 构建选中指标的经济分类已分配统计
    buildSelectedQuotaAllocatedByEconomy() {
      if (!this.selectedQuota) {
        return {}
      }
      const map = {}
      if (Array.isArray(this.selectedQuota.detailList) && this.selectedQuota.detailList.length) {
        this.selectedQuota.detailList.forEach(detail => {
          if (!detail || Number(detail.allocatedAmount || 0) <= 0) return
          const key = String(detail.economyCategory || '')
          if (!key) return
          if (!map[key]) {
            map[key] = 0
          }
          map[key] += Number(detail.allocatedAmount || 0)
        })
      } else if (this.selectedQuota.economyCategory && Number(this.selectedQuota.allocatedAmount || 0) > 0) {
        const key = String(this.selectedQuota.economyCategory)
        map[key] = Number(this.selectedQuota.allocatedAmount || 0)
      }
      return map
    },
    // 构建选中指标的已分配统计行
    buildSelectedQuotaAllocatedLines() {
      if (!this.selectedQuota) {
        return []
      }
      const map = this.buildSelectedQuotaAllocatedByEconomy()
      const keys = Object.keys(map)
      if (!keys.length) return []
      return keys.map(key => ({
        value: key,
        label: this.economyLabel(key),
        amount: map[key]
      }))
    }
  }
}
</script>

<style scoped>
.subsidy-quota-page {
  padding-bottom: 20px;
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

.subsidy-quota-page ::v-deep button,
.subsidy-quota-page ::v-deep .el-button {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

/* 搜索按钮使用蓝色背景 - 仅针对查询表单中的搜索按钮 */
.subsidy-quota-page ::v-deep .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.subsidy-quota-page ::v-deep .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.subsidy-quota-page ::v-deep .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 */
.subsidy-quota-page ::v-deep .action-toolbar .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page ::v-deep .action-toolbar .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page ::v-deep .action-toolbar .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 统一功能按钮样式：白色背景、浅灰色边框、黑色字体（排除 primary 按钮和文本按钮） */
.subsidy-quota-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 覆盖 plain 按钮样式（排除 primary 按钮） */
.subsidy-quota-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.quota-stats-wrapper {
  /* 玻璃面板外壳：纯毛玻璃效果，去掉渐变色 */
  margin-bottom: 8px;
  padding: 14px 16px 10px;
  border-radius: 16px;
  background-color: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.quota-stats {
  margin-bottom: 0;
}

/* 搜索表单和操作栏毛玻璃效果 */
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

/* 头部：信息行，配合顶部玻璃色条 */
.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  position: relative;
  padding-top: 0;
}

/* 顶部胶囊色条样式已移除，保留简洁头部信息行 */

/* 内容区域 */
.stat-body {
  padding-top: 0;
}

.stat-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.22);
  border-color: rgba(255, 255, 255, 0.9);
}

.stat-card-selected {
  border-color: #409EFF;
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.75), 0 8px 18px rgba(64, 158, 255, 0.18);
}

.stat-clear-btn {
  padding: 0;
  color: #909399;
  font-size: 12px;
}

.stat-clear-btn:hover {
  color: #409EFF;
}

.stat-quota-info {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.stat-label {
  color: #606266;
  font-size: 22px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 12px;

  /* 图标外层圆角容器，增强科技感 */
  .stat-icon {
    width: 44px;
    height: 44px;
    padding: 6px;
    border-radius: 50%;
    background-color: #f5f7fa;
    object-fit: contain;
    flex-shrink: 0;
    box-shadow: 0 0 0 1px rgba(144, 147, 153, 0.2);
    transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
  }

  .stat-icon:hover {
    transform: scale(1.08);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
    background-color: #edf4ff;
  }

  span {
    flex: 1;
    line-height: 1.5;
  }
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

.stat-value.success {
  color: #67c23a;
}

.stat-sub-line {
  font-size: 14px;
  margin-top: 2px;
}

.danger-text {
  color: #f56c6c;
}

.detail-table-actions {
  margin-bottom: 10px;
}

.detail-table {
  margin-bottom: 10px;
}

.quota-table ::v-deep .el-table__header th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  border-top: 1px solid #409EFF !important;
  border-bottom: 1px solid #409EFF !important;
}

.subsidy-quota-page :deep(.el-table .cell) {
  white-space: nowrap;
  overflow: visible !important;
  text-overflow: clip !important;
  height: auto;
  display: inline-flex;
  align-items: center;
}

.subsidy-quota-page :deep(.allocated-column .cell) {
  white-space: normal !important;
  display: block !important;
}

.allocated-line {
  line-height: 1.4;
}

.allocated-type {
  color: #606266;
}

.allocated-amount-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.allocated-amount {
  font-weight: 500;
}

.carry-over-badge {
  display: inline-block;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  padding: 0 5px;
  font-size: 11px;
  font-weight: bold;
  color: #fff;
  background-color: #f56c6c;
  border-radius: 9px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.4);
  vertical-align: top;
  margin-top: -2px;
}

.carry-over-tag {
  margin-left: 6px;
  font-size: 11px;
  padding: 0 6px;
  height: 20px;
  line-height: 18px;
}

.subsidy-quota-page :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.quota-summary {
  font-size: 13px;
  margin-bottom: 10px;
  line-height: 22px;
  color: #606266;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.5;

  i {
    margin-right: 4px;
    color: #409EFF;
  }

  .tip-amount {
    color: #67c23a;
    font-weight: 600;
  }
}

/* 分配对话框样式 */
.allocation-dialog {
  ::v-deep .el-dialog__body {
    padding: 20px;
  }
}

.quota-info-card {
  margin-bottom: 20px;

  .card-header {
    display: flex;
    align-items: center;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #409EFF;
      font-size: 16px;
    }
  }

  ::v-deep .el-card__body {
    padding: 15px;
  }
}

.allocation-form-card {
  .card-header {
    display: flex;
    align-items: center;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #67c23a;
      font-size: 16px;
    }
  }

  ::v-deep .el-card__body {
    padding: 20px;
  }

  ::v-deep .el-form-item {
    margin-bottom: 22px;
  }

  ::v-deep .el-form-item__label {
    font-weight: 500;
    color: #606266;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    border-radius: 4px;
  }

  /* 分配列表表格 */
  /* 分配列表表格（用 :deep 适配 scoped 样式） */
  .allocation-table {
    :deep(.allocation-header) {
      background: #e8f2ff !important;
      color: #303133 !important;
      font-weight: 600 !important;
      text-align: center !important;
    }
    :deep(.allocation-cell) {
      text-align: center !important;
    }
    :deep(.allocation-cell .cell) {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}

/* 非 scoped，全局兜底，确保表头着色和居中 */
:global(.allocation-table .allocation-header) {
  background: #e8f2ff !important;
  color: #303133 !important;
  font-weight: 600 !important;
  text-align: center !important;
}
:global(.allocation-table .allocation-cell) {
  text-align: center !important;
}
:global(.allocation-table .allocation-cell .cell) {
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

/* 结转对话框样式 */
.carry-over-dialog {
  ::v-deep .el-dialog__header {
    padding: 20px 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px 8px 0 0;

    .el-dialog__title {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn .el-dialog__close {
      color: #fff;
      font-size: 20px;

      &:hover {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 24px;
    max-height: 70vh;
    overflow-y: auto;
  }

  ::v-deep .el-dialog__footer {
    padding: 16px 24px;
    background: #f5f7fa;
    border-top: 1px solid #e4e7ed;
    border-radius: 0 0 8px 8px;
  }
}

/* 步骤条样式 */
.carry-over-steps {
  margin-bottom: 30px;
  padding: 20px 0;
  background: #f8f9fa;
  border-radius: 8px;

  ::v-deep .el-step__title {
    font-size: 14px;
    font-weight: 500;
  }

  ::v-deep .el-step__head.is-process {
    color: #409EFF;
    border-color: #409EFF;
  }
}

/* 表单卡片样式 */
.form-section-card {
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  ::v-deep .el-card__header {
    padding: 16px 20px;
    background: #f8f9fa;
    border-bottom: 1px solid #e4e7ed;
  }

  ::v-deep .el-card__body {
    padding: 20px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
  color: #303133;

  i {
    margin-right: 8px;
    font-size: 18px;
    color: #409EFF;
  }
}

/* 表单提示样式（结转对话框专用） */
.carry-over-form .form-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;

  i {
    margin-right: 4px;
    color: #409EFF;
  }
}

/* 指标描述样式 */
.quota-descriptions {
  ::v-deep .el-descriptions__label {
    font-weight: 500;
    color: #606266;
    width: 120px;
  }

  ::v-deep .el-descriptions__content {
    color: #303133;
  }
}

.info-value {
  font-size: 14px;
  color: #303133;
}

.amount-highlight {
  display: inline-flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 4px;

  i {
    margin-right: 6px;
    font-size: 18px;
  }

  &.success {
    color: #67c23a;
    background: #f0f9ff;
  }
}

/* 表单样式优化 */
.carry-over-form {
  ::v-deep .el-form-item {
    margin-bottom: 20px;
  }

  ::v-deep .el-form-item__label {
    font-weight: 500;
    color: #606266;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    border-radius: 4px;
  }
}

/* 指标详情抽屉样式 */
.quota-detail-drawer {
  ::v-deep .el-drawer__header {
    padding: 20px 24px;
    margin-bottom: 0;
    border-bottom: 1px solid #e4e7ed;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  ::v-deep .el-drawer__title {
    color: #fff;
    font-size: 18px;
    font-weight: 600;
  }

  ::v-deep .el-drawer__close-btn {
    color: #fff;
    font-size: 20px;

    &:hover {
      color: rgba(255, 255, 255, 0.8);
    }
  }

  ::v-deep .el-drawer__body {
    padding: 20px 24px;
    background: #f5f7fa;
  }
}

.quota-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 详情卡片样式 */
.detail-info-card,
.detail-amount-card,
.detail-table-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  background: #fff;
}

.detail-info-card ::v-deep .el-card__header,
.detail-amount-card ::v-deep .el-card__header,
.detail-table-card ::v-deep .el-card__header {
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
  border-radius: 8px 8px 0 0;
}

.detail-info-card ::v-deep .el-card__body,
.detail-amount-card ::v-deep .el-card__body,
.detail-table-card ::v-deep .el-card__body {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
  font-size: 18px;
  color: #409EFF;
}

.detail-count {
  margin-left: 8px;
  font-size: 13px;
  font-weight: normal;
  color: #909399;
}

/* 描述列表样式 */
.detail-descriptions {
  margin-top: 10px;
}

.detail-descriptions ::v-deep .el-descriptions__label {
  font-weight: 500;
  color: #606266;
  background: #fafafa;
  width: 120px;
}

.detail-descriptions ::v-deep .el-descriptions__content {
  color: #303133;
  font-size: 14px;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 金额统计样式 */
.amount-stats {
  margin-top: 10px;
}

.amount-item {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.amount-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.amount-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
  font-weight: 500;
}

.amount-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  line-height: 1.2;
}

.amount-value.primary {
  color: #409EFF;
}

.amount-value.warning {
  color: #E6A23C;
}

.amount-value.success {
  color: #67c23a;
}

.amount-value.warn {
  color: #E6A23C;
}

.amount-value.danger {
  color: #F56C6C;
}

/* 金额汇总卡片特殊样式 */
.detail-amount-card {
  margin-top: 20px;
  margin-bottom: 20px;
}

.amount-descriptions ::v-deep .el-descriptions__label {
  font-weight: 600;
  color: #606266;
  background: #f5f7fa;
  width: 100px;
  text-align: center;
}

.amount-descriptions ::v-deep .el-descriptions__content {
  color: #303133;
  font-size: 14px;
  padding: 16px 20px;
}

.allocated-breakdown {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.allocated-breakdown .total-line {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.allocated-breakdown .sub-line {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  padding-left: 8px;
  color: #606266;
}

.allocated-breakdown .label {
  color: #909399;
  font-weight: normal;
}

.allocated-breakdown .amount-value {
  font-size: 16px;
  font-weight: 600;
}

/* 表单金额信息 - 卡片式设计 */
.form-amount-info {
  margin-top: 16px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #F8F9FA 0%, #FFFFFF 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid #EBEEF5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  width: 100%;
  min-width: 0;
  box-sizing: border-box;

  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
    transform: translateY(-1px);
  }

  .amount-label {
    font-size: 14px;
    color: #909399;
    font-weight: 500;
    display: flex;
    align-items: center;
    white-space: nowrap;
    flex-shrink: 0;

    &::before {
      content: '';
      width: 4px;
      height: 14px;
      border-radius: 2px;
      margin-right: 8px;
      background: #909399;
    }
  }

  .amount-value {
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 0.5px;
    font-family: 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    white-space: nowrap;
    flex-shrink: 0;
    margin-left: 12px;
  }

  .amount-value.remaining {
    color: #67C23A;
  }

  .amount-value.carryover {
    color: #409EFF;
    font-size: 20px;
  }

  /* 剩余金额样式 - 与结转金额样式统一 */
  &.remaining-amount {
    background: linear-gradient(135deg, #E8F4FD 0%, #F0F9FF 100%);
    border-color: #B3D8FF;

    .amount-label::before {
      background: #409EFF;
    }
  }

  /* 结转金额样式 */
  &.carry-over-amount {
    background: linear-gradient(135deg, #E8F4FD 0%, #F0F9FF 100%);
    border-color: #B3D8FF;

    .amount-label::before {
      background: #409EFF;
    }
  }
}

.amount-detail {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.4;
}

/* 明细表格样式 */
.detail-table {
  margin-top: 10px;
  border-radius: 6px;
  overflow: hidden;
}

.detail-table ::v-deep .el-table__header-wrapper {
  background: #f5f7fa;
}

.detail-table ::v-deep .el-table__row:hover {
  background-color: #f5f7fa;
}

.detail-table ::v-deep .el-table__body-wrapper {
  max-height: 400px;
  overflow-y: auto;
}

.amount-text {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.amount-text.allocated {
  color: #E6A23C;
}

.amount-text.available {
  color: #67c23a;
}

.memo-text {
  color: #606266;
  font-size: 13px;
}

/* 操作列按钮样式 */
.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-btn {
  padding: 5px 8px;
  margin: 0;
  font-size: 13px;
  white-space: nowrap;
}

.action-btn:hover {
  color: #409EFF;
}

/* 下拉菜单危险项样式 */
.danger-item {
  color: #f56c6c;
}

.danger-item:hover {
  color: #f78989;
  background-color: #fef0f0;
}

/* 操作栏样式 */
.action-toolbar {
  margin-bottom: 0;
}

.toolbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: nowrap;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: nowrap;
  flex: 1;
}

.toolbar-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 10px;
}

.toolbar-left .el-button {
  margin: 0;
  white-space: nowrap;
}

/* 表格选中行样式 */
.subsidy-quota-page :deep(.el-table .selected-row) {
  background-color: #ecf5ff !important;
}

.subsidy-quota-page :deep(.el-table .selected-row:hover) {
  background-color: #d9ecff !important;
}

.subsidy-quota-page :deep(.el-table tbody tr) {
  cursor: pointer;
}

/* 附件列表样式 */
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
  &:last-child {
    border-bottom: none;
  }
  .el-icon-document {
    color: #909399;
    margin-right: 8px;
    font-size: 18px;
  }
  .file-name {
    flex: 1;
    color: #606266;
    font-size: 14px;
    margin-right: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .file-size {
    color: #909399;
    font-size: 12px;
    margin-right: 15px;
  }
  .actions {
    margin-left: auto;
    .el-button {
      padding: 0 5px;
      font-size: 13px;
    }
    .danger-text {
      color: #F56C6C;
    }
  }
}

/* 预览对话框样式 */
.preview-dialog {
  .preview-content {
    min-height: 400px;
    max-height: 80vh;
    overflow-y: auto;
  }
  .pdf-preview {
    .pdf-page {
      margin-bottom: 20px;
      text-align: center;
      img {
        max-width: 100%;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
      .page-number {
        margin-top: 10px;
        color: #909399;
        font-size: 14px;
      }
    }
  }
  .image-preview {
    text-align: center;
    img {
      max-width: 100%;
      max-height: 70vh;
    }
  }
  .html-preview {
    iframe {
      border: 1px solid #DCDFE6;
      border-radius: 4px;
    }
  }
  .text-preview {
    pre {
      background-color: #F5F7FA;
      padding: 15px;
      border-radius: 4px;
      overflow-x: auto;
      font-family: 'Courier New', monospace;
      font-size: 14px;
      line-height: 1.6;
      white-space: pre-wrap;
      word-wrap: break-word;
    }
  }
  .unsupported-preview {
    padding: 40px;
    text-align: center;
  }
}

/* 详情页附件样式 */
.detail-attachment-card {
  margin-top: 20px;
}

.no-attachment {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
  .el-icon-document-delete {
    font-size: 48px;
    display: block;
    margin-bottom: 10px;
    opacity: 0.5;
  }
}

/* 结转对话框样式优化 */
.carry-over-dialog {
  .el-dialog__body {
    padding: 20px 25px;
  }
}

.carry-over-step-content {
  padding: 20px 0;
}

/* 表单布局 - 使用卡片式设计 */
.carry-over-form-layout {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  position: relative;
}

.carry-over-form-section {
  flex: 1;
  min-width: 0;
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  padding: 24px;
  transition: all 0.3s ease;
  border: 1px solid #F0F2F5;

  &:hover {
    box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.12);
  }

  &.left-section {
    border-left: 4px solid #67C23A;
  }

  &.right-section {
    border-left: 4px solid #409EFF;
  }

  .form-items-container {
    display: flex;
    flex-direction: column;
  }
}

/* 对齐的表单项 - 确保左右两侧标签在同一水平线上 */
.aligned-form-item {
  margin-bottom: 22px;

  /* 确保标签宽度一致，标签对齐 */
  ::v-deep .el-form-item__label {
    width: 100px !important;
    text-align: right;
    padding-right: 16px;
    line-height: 36px;
    padding-top: 0;
    box-sizing: border-box;
    font-weight: 500;
    color: #606266;
    font-size: 14px;
  }

  ::v-deep .el-form-item__content {
    line-height: 36px;
    margin-left: 116px !important; /* 100px label + 16px padding */
  }

  /* 确保表单项内容区域高度一致 */
  ::v-deep .el-select,
  ::v-deep .el-input {
    height: 36px;
    line-height: 36px;

    .el-input__inner {
      height: 36px;
      line-height: 36px;
      border-radius: 6px;
      border-color: #DCDFE6;
      transition: all 0.3s;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
      }
    }
  }

  /* 提示信息不影响对齐 */
  .form-tip {
    margin-top: 8px;
    margin-left: 0;
    font-size: 12px;
    padding-left: 4px;
  }
}

/* 区块标题 - 现代化设计 */
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
  padding-bottom: 12px;
  position: relative;
  display: flex;
  align-items: center;

  &::before {
    content: '';
    width: 4px;
    height: 16px;
    background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
    border-radius: 2px;
    margin-right: 10px;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, #409EFF 0%, transparent 100%);
  }
}

/* 信息卡片 */
.info-card {
  margin-top: 16px;
  padding: 14px 16px;
  background: #F5F7FA;
  border: 1px solid #E4E7ED;
  border-radius: 4px;
}

.info-item {
  font-size: 13px;
  color: #606266;
  line-height: 28px;
  display: flex;
  align-items: center;
}

.info-label {
  color: #909399;
  min-width: 70px;
  margin-right: 8px;
}

.info-value {
  color: #303133;
  font-weight: 500;
  flex: 1;
}

/* 表单提示 - 优化样式 */
.form-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
  padding: 6px 10px;
  background: #F5F7FA;
  border-radius: 4px;
  border-left: 3px solid #DCDFE6;

  &.warning {
    color: #E6A23C;
    background: #FDF6EC;
    border-left-color: #E6A23C;
  }
}

/* 箭头 - 现代化设计 */
.carry-over-arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 180px;
  padding: 20px 0;
  position: relative;
  gap: 16px;

  .arrow-icon-wrapper {
    position: relative;
    z-index: 1;

    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 80px;
      height: 2px;
      background: linear-gradient(90deg, #409EFF 0%, #67C23A 100%);
      border-radius: 1px;
      z-index: -1;
    }

    i {
      font-size: 32px;
      color: #409EFF;
      background: #FFFFFF;
      padding: 8px;
      border-radius: 50%;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
      position: relative;
      transition: all 0.3s;

      &:hover {
        transform: scale(1.1);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
      }
    }
  }

  /* 经济分类信息 */
  .arrow-economy-category {
    width: 100%;
    padding: 12px 16px;
    background: #F5F7FA;
    border: 1px solid #E4E7ED;
    border-radius: 8px;
    min-width: 160px;

    .economy-category-label {
      font-size: 13px;
      color: #909399;
      font-weight: 500;
      margin-bottom: 10px;
      text-align: center;
    }

    .economy-category-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .economy-category-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 6px 8px;
      background: #FFFFFF;
      border-radius: 4px;
      font-size: 13px;

      .economy-category-name {
        color: #303133;
        font-weight: 500;
        flex: 1;
      }

      .economy-category-amount {
        color: #67C23A;
        font-weight: 600;
        margin-left: 8px;
        white-space: nowrap;
      }
    }
  }
}

.carry-over-arrow-large {
  margin: 0 25px;
  display: flex;
  align-items: center;
  justify-content: center;

  i {
    font-size: 32px;
    color: #409EFF;
  }
}

/* 流程展示卡片 */
.carry-over-flow-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding: 24px 20px;
  background: linear-gradient(135deg, #F5F7FA 0%, #FAFBFC 100%);
  border: 1px solid #E4E7ED;
  border-radius: 8px;
}

.flow-item {
  flex: 1;
  text-align: center;
}

.flow-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.flow-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  word-break: break-all;
}

.flow-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.flow-amount {
  font-size: 14px;
  font-weight: 600;
  margin-top: 8px;

  &.amount-remaining {
    color: #67C23A;
  }

  &.amount-carryover {
    color: #409EFF;
  }
}

/* 详细信息区域 */
.carry-over-detail-section {
  .economy-category-select {
    margin-bottom: 20px;
  }
}

.detail-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.detail-card {
  background: #FFFFFF;
  border: 1px solid #E4E7ED;
  border-radius: 6px;
  padding: 18px;
}

.detail-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid #EBEEF5;
}

.detail-value {
  font-weight: 500;
  color: #303133;
}

.detail-amount {
  font-size: 15px;
  font-weight: 600;

  &.remaining {
    color: #67C23A;
  }

  &.carryover {
    color: #409EFF;
    font-size: 16px;
  }
}

/* 对话框样式优化 */
::v-deep .carry-over-dialog {
  .el-dialog__body {
    padding: 24px 30px;
    background: #F5F7FA;
  }

  .el-dialog__header {
    padding: 20px 30px;
    background: linear-gradient(135deg, #FFFFFF 0%, #F8F9FA 100%);
    border-bottom: 1px solid #EBEEF5;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      display: flex;
      align-items: center;

      &::before {
        content: '';
        width: 4px;
        height: 18px;
        background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
        border-radius: 2px;
        margin-right: 10px;
      }
    }
  }

  .el-dialog__footer {
    padding: 16px 30px;
    background: #FFFFFF;
    border-top: 1px solid #EBEEF5;

    .el-button {
      padding: 10px 24px;
      border-radius: 6px;
      font-weight: 500;

      &.el-button--primary {
        background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
        border: none;
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);

        &:hover {
          background: linear-gradient(135deg, #66B1FF 0%, #409EFF 100%);
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
        }
      }
    }
  }
}


/* 响应式调整 */
@media (max-width: 960px) {
  .carry-over-form-layout {
    flex-direction: column;
    gap: 24px;
  }

  .carry-over-form-section {
    padding: 20px;
  }

  .carry-over-arrow {
    width: 100%;
    padding: 16px 0;
    margin: 20px 0;

    .arrow-icon-wrapper {
      &::before {
        width: 60px;
        height: 2px;
      }

      i {
        transform: rotate(90deg);
      }
    }

    .arrow-economy-category {
      width: 100%;
      max-width: 300px;
      margin: 0 auto;
    }
  }

  .detail-cards {
    grid-template-columns: 1fr;
  }
}
</style>

<style>
/* 统计卡片背景色 - 使用全局样式确保生效 */
.quota-stats .stat-card-primary .el-card__body {
  background-color: #f4f7ff !important;
}

.quota-stats .stat-card-warn .el-card__body {
  background-color: #fdf6ec !important;
}

.quota-stats .stat-card-success .el-card__body {
  background-color: #f5fdf5 !important;
}

/* 表格标题行蓝色边框 - 全局样式 */
.quota-table .el-table__header th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  border-top: 1px solid #409EFF !important;
  border-bottom: 1px solid #409EFF !important;
}
</style>

<style>
/* 搜索按钮蓝色背景 - 全局样式确保生效（仅查询表单内） */
.subsidy-quota-page .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.subsidy-quota-page .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.subsidy-quota-page .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 - 全局样式 */
.subsidy-quota-page .action-toolbar .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page .action-toolbar .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-quota-page .action-toolbar .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}
</style>


