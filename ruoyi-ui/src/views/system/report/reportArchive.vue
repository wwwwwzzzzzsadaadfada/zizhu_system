<template>
  <div class="report-archive-page">
    <div class="archive-glass-wrapper">
      <!-- 页头 -->
      <div class="page-header">
        <div class="page-title">
          <div class="title-icon">
            <img :src="titleIcon" alt="档案打包" class="title-icon-img" />
          </div>
          <div class="title-content">
            <span class="title-text">档案打包管理</span>
          </div>
        </div>
        <div class="page-actions">
          <el-button 
            type="success"
            icon="el-icon-s-operation"
            size="small"
            @click="openHybridPackageDialog"
          >
            混合打包
          </el-button>
          <el-button 
            type="primary" 
            icon="el-icon-search" 
            size="small"
            @click="drawerVisible = !drawerVisible"
          >
            {{ drawerVisible ? '关闭查询' : '高级查询' }}
          </el-button>
        </div>
      </div>

      <!-- Tab标签页 -->
      <el-tabs v-model="activeTab" class="archive-tabs" @tab-click="handleTabClick">
        <!-- 报表档案Tab -->
        <el-tab-pane label="报表档案" name="report">
          <template slot="label">
            <span><i class="el-icon-folder-opened"></i> 报表档案</span>
          </template>

      <!-- 文件包区域（上方） -->
      <div class="packages-block">
        <div class="packages-section-header">
          <div class="section-title">
            <i class="el-icon-folder-opened"></i>
            <span>文件包列表</span>
            <el-badge :value="packageList.length" class="item" type="primary" v-if="packageList.length > 0" />
          </div>
        </div>
        <div class="packages-toolbar" v-if="packageList.length > 0">
          <div class="toolbar-left">
            <el-checkbox
              v-model="packageSelectAll"
              :indeterminate="packageSelectIndeterminate"
              @change="handlePackageSelectAll"
            >
              全选
            </el-checkbox>
            <span class="toolbar-tip" v-if="selectedPackageIds.length > 0">
              已选 <strong>{{ selectedPackageIds.length }}</strong> 个文件包
            </span>
          </div>
          <div class="toolbar-right">
            <el-dropdown split-button type="primary" size="small" @click="openAllPackageDialog" @command="handleBatchPackageCommand">
              <i class="el-icon-box"></i> 批量打包
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="all">
                  <i class="el-icon-box"></i> 一键打包全部
                </el-dropdown-item>
                <el-dropdown-item command="mixed" :disabled="selectedPackageIds.length === 0">
                  <i class="el-icon-tickets"></i> 混合打包选中 ({{ selectedPackageIds.length }}个)
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
        <div v-loading="loading" class="packages-grid">
          <div
            v-for="pkg in packageList"
            :key="pkg.reportName"
            class="package-card"
            :class="{
              'package-card-active': selectedReportName === pkg.reportName,
              'package-card-selected': selectedPackageIds.includes(pkg.reportId)
            }"
            @click="handlePackageClick(pkg.reportName)"
          >
            <div class="package-checkbox-wrapper" @click.stop>
              <el-checkbox
                :value="selectedPackageIds.includes(pkg.reportId)"
                @change="handlePackageCheckboxChange(pkg.reportName, $event)"
              />
            </div>
            <div class="package-icon-wrapper">
              <div class="package-icon">
                <img :src="xlsxIcon" alt="报表" class="icon-image" />
              </div>
            </div>
            <div class="package-content">
              <div class="package-name" :title="pkg.reportName">{{ pkg.reportName }}</div>
              <el-tag
                v-if="pkg.hasMultipleNames"
                size="mini"
                type="warning"
                effect="plain"
                style="margin-top: 4px"
              >
                包含历史改名
              </el-tag>
            </div>
            <div class="package-actions" @click.stop>
              <el-dropdown trigger="click" @command="(cmd) => handlePackageAction(cmd, pkg)" placement="bottom-end">
                <el-button
                  size="mini"
                  icon="el-icon-more"
                  circle
                  class="more-btn"
                  title="更多操作"
                />
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="package" icon="el-icon-box">
                    打包
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <span style="color: #f56c6c;">
                      <i class="el-icon-delete"></i> 删除
                    </span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>

        <div v-if="!loading && packageList.length === 0" class="empty-state">
          <img :src="xlsxIcon" alt="空状态" class="empty-icon" />
          <p class="empty-text">暂无归档文件包</p>
        </div>
      </div>

      <!-- 记录列表区域（下方） -->
      <div v-if="selectedReportName" class="detail-block">
        <div class="detail-section-header">
          <div class="section-title">
            <i class="el-icon-document"></i>
            <span>{{ selectedReportName }}</span>
            <el-tag size="small" type="info" style="margin-left: 12px">共 {{ detailTotal }} 条记录</el-tag>
          </div>
        </div>
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-button
              type="danger"
              class="btn-plain-white"
              icon="el-icon-delete"
              :disabled="multiple"
              @click="handleBatchRemove"
            >
              批量销毁
            </el-button>
            <el-button
              type="info"
              class="btn-plain-white"
              icon="el-icon-files"
              @click="openMergeDialog"
            >
              合并PDF
            </el-button>
            <el-button
              type="success"
              class="btn-plain-white"
              icon="el-icon-box"
              v-if="selectedReportName"
              @click="openPackageDialogForReport(selectedReportName, detailTotal)"
            >
              打包该文件包
            </el-button>
            <el-button
              type="warning"
              class="btn-plain-white"
              icon="el-icon-folder-opened"
              :disabled="multiple"
              @click="openPackageDialogForSelected"
            >
              打包选中记录
            </el-button>
            <el-button
              type="primary"
              class="btn-plain-white"
              icon="el-icon-document"
              @click="openPackageListDrawer"
            >
              打包记录
            </el-button>
            <el-button
              type="success"
              class="btn-plain-white"
              icon="el-icon-paperclip"
              v-if="selectedReportName"
              @click="openAttachmentDialog"
            >
              附件管理
            </el-button>
            <span class="selected-info" v-if="!multiple">
              已选择 <strong>{{ Object.keys(selectedRecordsMap).length }}</strong> 条
              <span v-if="Object.keys(selectedRecordsMap).length > ids.length" style="color: #67c23a; margin-left: 8px">
                (含其他文件包 {{ Object.keys(selectedRecordsMap).length - ids.length }} 条)
              </span>
            </span>
          </div>
          <div class="toolbar-right">
            <el-button
              icon="el-icon-refresh-right"
              size="mini"
              @click="loadDetailList"
            >
              刷新
            </el-button>
          </div>
        </div>

        <el-table
          ref="detailTable"
          v-loading="detailLoading"
          :data="detailList"
          border
          highlight-current-row
          class="detail-table"
          :row-class-name="tableRowClassName"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" fixed="left" />
          <el-table-column label="类型" width="90" align="center">
            <template slot-scope="scope">
              <el-popover
                v-if="scope.row.isMerged"
                placement="right"
                width="260"
                trigger="click"
              >
                <div class="merged-popover-content">
                  <div class="merged-popover-title">合并学生</div>
                  <div v-if="scope.row.mergedStudentNames" class="merged-popover-list">
                    <el-tag
                      v-for="(name, idx) in scope.row.mergedStudentNames.split(/[,，、]/).map(s => s.trim()).filter(Boolean)"
                      :key="idx"
                      size="mini"
                      effect="plain"
                      class="merged-student-tag"
                    >
                      {{ name }}
                    </el-tag>
                  </div>
                  <div v-else class="merged-empty">
                    暂无学生明细
                  </div>
                </div>
                <el-link
                  slot="reference"
                  type="primary"
                  :underline="false"
                  class="type-link"
                >
                  合并
                </el-link>
              </el-popover>
              <span
                v-else
                class="type-text-single"
              >
                单份
              </span>
            </template>
          </el-table-column>
          <el-table-column label="报表" prop="reportName" width="200" show-overflow-tooltip align="center">
            <template slot-scope="scope">
              <div class="report-name-cell">
                <img :src="pdfIcon" class="pdf-icon" alt="PDF" />
                <span>{{ displayText(scope.row.reportName) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="学年学期" prop="yearSemesterName" width="160" show-overflow-tooltip align="center" />
          <el-table-column label="学生/报表" prop="studentName" width="120" show-overflow-tooltip align="center">
            <template slot-scope="scope">{{ displayText(scope.row.studentName) }}</template>
          </el-table-column>
          <!-- <el-table-column label="学号" prop="studentNo" width="140" show-overflow-tooltip align="center">
            <template slot-scope="scope">{{ displayText(scope.row.studentNo) }}</template>
          </el-table-column> -->
          <el-table-column label="年级" prop="gradeName" width="100" show-overflow-tooltip align="center">
            <template slot-scope="scope">{{ displayText(scope.row.gradeName) }}</template>
          </el-table-column>
          <el-table-column label="文件大小" prop="fileSize" width="110" align="center">
            <template slot-scope="scope">
              {{ formatSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column label="归档时间" prop="createTime" width="180" align="center" />
          <el-table-column label="状态" width="180" align="center">
            <template slot-scope="scope">
              <div class="status-switch-wrapper">
                <el-switch
                  v-model="scope.row.status"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="正常"
                  inactive-text="已销毁"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                  @change="handleStatusChange(scope.row)"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="180" align="center">
            <template slot-scope="scope">
              <div class="table-actions">
                <el-link
                  type="primary"
                  icon="el-icon-view"
                  :underline="false"
                  @click="handlePreview(scope.row)"
                >
                  预览
                </el-link>
                <span class="action-divider">|</span>
                <el-link
                  type="primary"
                  icon="el-icon-download"
                  :underline="false"
                  @click="handleDownload(scope.row)"
                >
                  下载
                </el-link>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="detailTotal > 0"
          :total="detailTotal"
          :page.sync="detailQueryParams.pageNum"
          :limit.sync="detailQueryParams.pageSize"
          @pagination="loadDetailList"
        />
      </div>
        </el-tab-pane>

        <!-- 自定义档案包Tab -->
        <el-tab-pane label="自定义档案包" name="custom">
          <template slot="label">
            <span><i class="el-icon-files"></i> 自定义档案包</span>
          </template>

          <!-- 档案包区域（上方） -->
          <div class="packages-block">
            <div class="packages-section-header">
              <div class="section-title">
                <i class="el-icon-files"></i>
                <span>自定义档案包列表</span>
                <el-badge :value="customPackageList.length" class="item" type="warning" v-if="customPackageList.length > 0" />
              </div>
            </div>
            <div v-loading="customPackageLoading" class="packages-grid">
              <!-- 现有档案包卡片 -->
              <div
                v-for="pkg in customPackageList"
                :key="pkg.packageCode"
                class="package-card"
                :class="{
                  'package-card-active': selectedCustomPackageCode === pkg.packageCode
                }"
                @click="handleCustomPackageClick(pkg.packageCode)"
              >
                <div class="package-icon-wrapper">
                  <div class="package-icon">
                    <img :src="customPackageIcon" class="icon-image" alt="档案包" />
                  </div>
                </div>
                <div class="package-content">
                  <div class="package-name" :title="pkg.packageName">{{ pkg.packageName }}</div>
                  <el-tag
                    v-if="pkg.attachmentCount > 0"
                    size="mini"
                    type="info"
                    effect="plain"
                    style="margin-top: 4px"
                  >
                    {{ pkg.attachmentCount }} 个附件
                  </el-tag>
                </div>
                <div class="package-actions" @click.stop>
                  <el-dropdown trigger="click" @command="(cmd) => handleCustomPackageAction(cmd, pkg)" placement="bottom-end">
                    <el-button
                      size="mini"
                      icon="el-icon-more"
                      circle
                      class="more-btn"
                      title="更多操作"
                    />
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="package" icon="el-icon-box">
                        打包
                      </el-dropdown-item>
                      <el-dropdown-item command="edit" icon="el-icon-edit">
                        编辑
                      </el-dropdown-item>
                      <el-dropdown-item command="delete" divided>
                        <span style="color: #f56c6c;">
                          <i class="el-icon-delete"></i> 删除
                        </span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
              </div>

              <!-- 新增档案包卡片（+号） -->
              <div
                class="package-card package-card-add"
                @click="openCreateCustomPackageDialog"
                v-if="!customPackageLoading"
              >
                <div class="add-icon-wrapper">
                  <img :src="addPackageIcon" class="add-icon-img" alt="创建档案包" />
                </div>
                <div class="package-content">
                  <div class="add-text">创建档案包</div>
                </div>
              </div>
            </div>

            <div v-if="!customPackageLoading && customPackageList.length === 0" class="empty-state">
              <i class="el-icon-files" style="font-size: 64px; color: #dcdfe6;"></i>
              <p class="empty-text">暂无自定义档案包</p>
              <p class="empty-hint">点击右侧 + 号创建档案包</p>
            </div>
          </div>

          <!-- 附件列表区域（下方） -->
          <div v-if="selectedCustomPackageCode" class="detail-block">
            <div class="detail-section-header">
              <div class="section-title">
                <i class="el-icon-paperclip"></i>
                <span>{{ selectedCustomPackageName }}</span>
                <el-tag size="small" type="warning" style="margin-left: 12px">共 {{ customAttachmentTotal }} 个附件</el-tag>
              </div>
            </div>
            <div class="table-toolbar">
              <div class="toolbar-left">
                <el-button
                  type="primary"
                  class="btn-plain-white"
                  icon="el-icon-upload"
                  @click="openCustomUploadDialog"
                >
                  上传附件
                </el-button>
                <el-button
                  type="danger"
                  class="btn-plain-white"
                  icon="el-icon-delete"
                  :disabled="customAttachmentSelection.length === 0"
                  @click="handleBatchDeleteCustomAttachments"
                >
                  批量删除
                </el-button>
                <span class="selected-info" v-if="customAttachmentSelection.length > 0">
                  已选择 <strong>{{ customAttachmentSelection.length }}</strong> 个附件
                </span>
              </div>
              <div class="toolbar-right">
                <el-button
                  icon="el-icon-refresh-right"
                  size="mini"
                  @click="loadCustomAttachmentList"
                >
                  刷新
                </el-button>
              </div>
            </div>

            <el-table
              ref="customAttachmentTable"
              v-loading="customAttachmentLoading"
              :data="customAttachmentList"
              border
              highlight-current-row
              class="detail-table"
              @selection-change="handleCustomAttachmentSelectionChange"
            >
              <el-table-column type="selection" width="55" align="center" fixed="left" />
              <el-table-column type="index" label="序号" width="70" align="center" />
              <el-table-column prop="originalName" label="文件名称" min-width="250" show-overflow-tooltip />
              <el-table-column prop="fileType" label="类型" width="90" align="center">
                <template slot-scope="scope">
                  <el-tag :type="getFileTypeTag(scope.row.fileType)" size="mini">
                    {{ scope.row.fileType || '其他' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="fileSize" label="文件大小" width="100" align="right">
                <template slot-scope="scope">
                  {{ formatSize(scope.row.fileSize) }}
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" width="180" show-overflow-tooltip>
                <template slot-scope="scope">
                  <span v-if="scope.row.editingRemark" style="width: 100%">
                    <el-input
                      v-model="scope.row.tempRemark"
                      size="mini"
                      @keyup.enter.native="saveCustomRemark(scope.row)"
                      @blur="cancelEditCustomRemark(scope.row)"
                      placeholder="请输入备注"
                    />
                  </span>
                  <span v-else @dblclick="editCustomRemark(scope.row)" style="cursor: pointer">
                    {{ scope.row.remark || '无' }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="yearSemesterName" label="学年学期" width="150" align="center" show-overflow-tooltip />
              <el-table-column prop="createTime" label="上传时间" width="160" align="center" />
              <el-table-column label="操作" width="200" align="center" fixed="right">
                <template slot-scope="scope">
                  <el-link
                    type="primary"
                    icon="el-icon-view"
                    :underline="false"
                    @click="handlePreviewAttachment(scope.row)"
                  >
                    预览
                  </el-link>
                  <span class="action-divider">|</span>
                  <el-link
                    type="primary"
                    icon="el-icon-download"
                    :underline="false"
                    @click="handleDownloadAttachment(scope.row)"
                  >
                    下载
                  </el-link>
                  <span class="action-divider">|</span>
                  <el-link
                    type="danger"
                    icon="el-icon-delete"
                    :underline="false"
                    @click="handleDeleteCustomAttachment(scope.row)"
                  >
                    删除
                  </el-link>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 高级查询折叠面板(右侧) -->
    <div class="query-panel" :class="{ 'query-panel-expanded': drawerVisible }" :style="{ top: panelTop + 'px' }">
      <div
        class="query-panel-toggle"
        :class="{ 'dragging': isDragging }"
        @click="handleToggleClick"
        @mousedown="handleToggleMouseDown"
      >
        <i :class="drawerVisible ? 'el-icon-close' : 'el-icon-search'"></i>
        <span v-if="!drawerVisible">高级查询</span>
        <div class="drag-indicator">
          <i class="el-icon-more"></i>
        </div>
      </div>
      <div class="query-panel-content" v-show="drawerVisible">
        <div class="query-panel-header">
          <span class="query-panel-title">高级查询</span>
        </div>
        <div class="query-panel-body">
          <el-form ref="queryFormRef" :model="queryParams" label-width="100px" class="drawer-form">
        <el-form-item label="报表名称">
          <el-input
            v-model="queryParams.reportName"
            placeholder="请输入报表名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学段">
          <el-select
            v-model="queryParams.schoolingPlanId"
            placeholder="请选择学段"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in schoolingPlanOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select
            v-model="queryParams.gradeId"
            placeholder="请选择年级"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="item in gradeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="归档时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="至"
            value-format="yyyy-MM-dd"
            style="width: 100%"
            clearable
          />
        </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        </div>
      </div>
    </div>

    <!-- 合并PDF弹窗 -->
    <el-dialog
      title="合并PDF"
      :visible.sync="mergeDialogVisible"
      width="900px"
      :close-on-click-modal="false"
      @close="resetMergeForm"
    >
      <el-form :model="mergeForm" label-width="120px">
        <el-form-item label="报表">
          <el-select
            v-model="mergeForm.reportId"
            placeholder="请选择要合并的报表"
            style="width: 100%"
            @change="handleReportSelectChange"
            filterable
            :loading="pdfTableLoading"
            clearable
          >
            <el-option
              v-for="report in availableReports"
              :key="report.id"
              :label="report.name"
              :value="report.id"
            >
              <span style="float: left">{{ report.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ report.pdfCount || 0 }} 个PDF
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="合并文件名">
          <el-input
            v-model="mergeForm.mergedFileName"
            placeholder="将自动生成：报表名称+汇总"
            disabled
          />
        </el-form-item>
        <el-form-item label="待合并文件">
          <div style="margin-bottom: 10px; color: #909399; font-size: 13px">
            当前报表待合并：<strong>{{ pdfTablePage.total }}</strong> 个PDF
          </div>
          <el-table
            :data="pdfTableData"
            height="300"
            v-loading="pdfTableLoading"
            border
          >
            <el-table-column prop="fileName" label="文件名" min-width="260" show-overflow-tooltip />
            <el-table-column prop="fileSize" label="大小" width="110" align="right">
              <template slot-scope="scope">
                {{ formatSize(scope.row.fileSize) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="scope">
                <el-link type="primary" :underline="false" size="mini" @click="downloadSinglePdf(scope.row.id)">下载</el-link>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="pdfTablePage.total > 0"
            @size-change="handlePdfPageSizeChange"
            @current-change="handlePdfPageChange"
            :current-page="pdfTablePage.pageNum"
            :page-sizes="[10, 20, 50]"
            :page-size="pdfTablePage.pageSize"
            layout="total, sizes, prev, pager, next"
            :total="pdfTablePage.total"
            style="margin-top: 10px; text-align: right"
            small
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="mergeDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="merging" :disabled="!mergeForm.reportId || pdfTablePage.total < 1" @click="submitMerge">
          合并汇总
        </el-button>
      </div>
    </el-dialog>

    <!-- 打包弹窗 -->
    <el-dialog
      title="打包档案"
      :visible.sync="packageDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="packageForm" label-width="100px">
        <el-form-item label="打包名称">
          <el-input
            v-model="packageForm.packageName"
            placeholder="请输入打包名称（留空将自动生成）"
            clearable
          />
        </el-form-item>
        <el-form-item label="文件包列表" v-if="packageForm.mode === 'mixed'">
          <div class="selected-packages-list">
            <el-tag
              v-for="reportName in packageForm.reportNames"
              :key="reportName"
              type="info"
              effect="plain"
              style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ reportName }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="选中记录" v-if="packageForm.mode === 'selected'">
          <div class="selected-records-list">
            <el-table
              :data="packageForm.selectedRecords"
              border
              max-height="300"
              size="mini"
            >
              <el-table-column prop="reportName" label="报表" width="120" show-overflow-tooltip />
              <el-table-column prop="studentName" label="学生" width="100" show-overflow-tooltip />
              <el-table-column prop="studentNo" label="学号" width="100" show-overflow-tooltip />
              <el-table-column prop="yearSemesterName" label="学年学期" width="150" show-overflow-tooltip />
              <el-table-column prop="fileName" label="文件名" min-width="200" show-overflow-tooltip />
            </el-table>
          </div>
        </el-form-item>
        <el-form-item label="档案包" v-if="packageForm.mode === 'custom'">
          <el-tag type="success" effect="plain">
            {{ packageForm.customPackageName }}
          </el-tag>
        </el-form-item>
        <el-form-item label="文件数量">
          <span>{{ packageForm.fileCount }} 个文件</span>
        </el-form-item>
        <el-form-item label="说明" v-if="packageForm.mode === 'custom'">
          <div style="color: #909399; font-size: 12px">
            该自定义档案包下的所有附件将被打包
          </div>
        </el-form-item>
        <el-form-item label="说明" v-if="packageForm.mode === 'mixed'">
          <div style="color: #909399; font-size: 12px">
            每个文件包下的档案将打包到各自的文件夹中，所有文件夹都在同一个ZIP包内
          </div>
        </el-form-item>
        <el-form-item label="说明" v-if="packageForm.mode === 'selected'">
          <div style="color: #909399; font-size: 12px">
            选中的记录将按照报表名称分组打包，每个报表一个文件夹
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="packageDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="packaging" @click="submitPackage">
          开始打包
        </el-button>
      </div>
    </el-dialog>

    <!-- 打包记录列表抽屉 -->
    <el-drawer
      title="打包记录"
      :visible.sync="packageListDrawerVisible"
      size="60%"
      :before-close="handlePackageListDrawerClose"
    >
      <el-table
        v-loading="packageListLoading"
        :data="packageRecordList"
        border
        style="width: 100%"
      >
        <el-table-column prop="packageName" label="打包名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="packageType" label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.packageType === 'all' ? 'warning' : 'success'" size="mini">
              {{ scope.row.packageType === 'all' ? '全部档案' : '手动打包' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileCount" label="文件数" width="100" align="center" />
        <el-table-column prop="totalSize" label="总大小" width="120" align="right">
          <template slot-scope="scope">
            {{ formatSize(scope.row.totalSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'danger' : 'info'"
              size="mini"
            >
              {{ scope.row.status === 1 ? '已生成' : scope.row.status === 2 ? '失败' : '待打包' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-link
              v-if="scope.row.status === 1"
              type="primary"
              icon="el-icon-download"
              :underline="false"
              @click="handleDownloadPackage(scope.row)"
            >
              下载
            </el-link>
            <el-link
              type="danger"
              icon="el-icon-delete"
              :underline="false"
              style="margin-left: 10px"
              @click="handleDeletePackage(scope.row)"
            >
              删除
            </el-link>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="packageListPage.total > 0"
        @size-change="handlePackageListPageSizeChange"
        @current-change="handlePackageListPageChange"
        :current-page="packageListPage.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="packageListPage.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="packageListPage.total"
        style="margin-top: 20px; text-align: right"
      />
    </el-drawer>

    <!-- 附件管理对话框 -->
    <el-dialog
      title="附件管理"
      :visible.sync="attachmentDialogVisible"
      width="900px"
      :close-on-click-modal="false"
      @close="handleAttachmentDialogClose"
    >
      <div slot="title" class="dialog-title">
        <i class="el-icon-paperclip"></i>
        <span>附件管理 - {{ currentAttachmentRelatedId }}</span>
      </div>
      
      <div class="attachment-dialog-content">
        <div class="attachment-toolbar">
          <el-button
            type="primary"
            icon="el-icon-upload"
            size="small"
            @click="openUploadDialog"
          >
            上传附件
          </el-button>
          <el-button
            icon="el-icon-refresh"
            size="small"
            @click="loadAttachmentList"
          >
            刷新
          </el-button>
          <span class="attachment-count">
            共 <strong>{{ attachmentList.length }}</strong> 个附件
          </span>
        </div>

        <el-table
          v-loading="attachmentLoading"
          :data="attachmentList"
          border
          style="width: 100%; margin-top: 16px"
          max-height="400"
        >
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="originalName" label="文件名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="fileType" label="类型" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="getFileTypeTag(scope.row.fileType)" size="mini">
                {{ scope.row.fileType || '其他' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="大小" width="110" align="right">
            <template slot-scope="scope">
              {{ formatSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip>
            <template slot-scope="scope">
              <span v-if="scope.row.editingRemark" style="width: 100%">
                <el-input
                  v-model="scope.row.tempRemark"
                  size="mini"
                  @keyup.enter.native="saveRemark(scope.row)"
                  @blur="cancelEditRemark(scope.row)"
                  placeholder="请输入备注"
                />
              </span>
              <span v-else @dblclick="editRemark(scope.row)" style="cursor: pointer">
                {{ scope.row.remark || '无' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="上传时间" width="160" align="center" />
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template slot-scope="scope">
              <el-link
                type="primary"
                icon="el-icon-download"
                :underline="false"
                @click="handleDownloadAttachment(scope.row)"
              >
                下载
              </el-link>
              <el-link
                type="danger"
                icon="el-icon-delete"
                :underline="false"
                style="margin-left: 10px"
                @click="handleDeleteAttachment(scope.row)"
              >
                删除
              </el-link>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="!attachmentLoading && attachmentList.length === 0" class="attachment-empty">
          <i class="el-icon-folder-opened" style="font-size: 48px; color: #ccc"></i>
          <p>暂无附件</p>
        </div>
      </div>
    </el-dialog>

    <!-- 上传附件对话框 -->
    <el-dialog
      title="上传附件"
      :visible.sync="uploadDialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="handleUploadDialogClose"
    >
      <el-form label-width="80px">
        <el-form-item label="附件">
          <el-upload
            ref="upload"
            :action="uploadAction"
            :headers="uploadHeaders"
            :data="uploadData"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :file-list="uploadFileList"
            :auto-upload="false"
            multiple
            drag
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">
              支持PDF、Word、Excel、图片等文件类型，单个文件不超过50MB
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="uploadRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="uploading"
          @click="submitUpload"
        >
          {{ uploading ? '上传中...' : '确定上传' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 创建自定义档案包对话框 -->
    <el-dialog
      :title="customPackageForm.id ? '编辑档案包' : '创建档案包'"
      :visible.sync="customPackageDialogVisible"
      width="450px"
      :close-on-click-modal="false"
      @close="handleCustomPackageDialogClose"
    >
      <el-form
        ref="customPackageFormRef"
        :model="customPackageForm"
        :rules="customPackageRules"
        label-width="100px"
      >
        <el-form-item label="档案包名称" prop="packageName">
          <el-input
            v-model="customPackageForm.packageName"
            placeholder="请输入档案包名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="customPackageForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入档案包描述（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="customPackageDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="customPackageSaving"
          @click="submitCustomPackage"
        >
          确 定
        </el-button>
      </div>
    </el-dialog>

    <!-- 混合打包选择对话框 -->
    <el-dialog
      title="混合打包"
      :visible.sync="hybridPackageDialogVisible"
      width="900px"
      :close-on-click-modal="false"
      @close="handleHybridPackageDialogClose"
    >
      <div class="hybrid-package-content">
        <div class="hybrid-section">
          <div class="hybrid-section-header">
            <h4>报表档案包</h4>
            <el-button
              type="text"
              size="small"
              @click="handleSelectAllReports"
            >
              {{ hybridSelection.reportNames.length === packageList.length ? '取消全选' : '全选' }}
            </el-button>
          </div>
          <div class="hybrid-list">
            <el-checkbox-group v-model="hybridSelection.reportNames">
              <el-checkbox
                v-for="pkg in packageList"
                :key="pkg.reportName"
                :label="pkg.reportName"
                class="hybrid-checkbox"
              >
                <span class="package-info">
                  <i class="el-icon-folder-opened"></i>
                  <span class="package-name">{{ pkg.reportName }}</span>
                  <el-tag size="mini" type="info">{{ pkg.count }} 个文件</el-tag>
                </span>
              </el-checkbox>
            </el-checkbox-group>
            <div v-if="packageList.length === 0" class="empty-hint">
              <i class="el-icon-folder-opened"></i>
              <p>暂无报表档案包</p>
            </div>
          </div>
        </div>

        <div class="hybrid-section">
          <div class="hybrid-section-header">
            <h4>自定义档案包</h4>
            <el-button
              type="text"
              size="small"
              @click="handleSelectAllCustomPackages"
            >
              {{ hybridSelection.customPackageCodes.length === customPackageList.length ? '取消全选' : '全选' }}
            </el-button>
          </div>
          <div class="hybrid-list">
            <el-checkbox-group v-model="hybridSelection.customPackageCodes">
              <el-checkbox
                v-for="pkg in customPackageList"
                :key="pkg.packageCode"
                :label="pkg.packageCode"
                class="hybrid-checkbox"
              >
                <span class="package-info">
                  <i class="el-icon-files"></i>
                  <span class="package-name">{{ pkg.packageName }}</span>
                  <el-tag size="mini" type="warning">{{ pkg.fileCount || 0 }} 个附件</el-tag>
                </span>
              </el-checkbox>
            </el-checkbox-group>
            <div v-if="customPackageList.length === 0" class="empty-hint">
              <i class="el-icon-files"></i>
              <p>暂无自定义档案包</p>
            </div>
          </div>
        </div>

        <div class="hybrid-summary">
          <el-alert
            :title="hybridSummaryText"
            type="info"
            :closable="false"
            show-icon
          />
        </div>

        <el-form label-width="100px" style="margin-top: 20px">
          <el-form-item label="打包名称">
            <el-input
              v-model="hybridPackageName"
              placeholder="请输入打包名称（留空自动生成）"
              clearable
            />
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="hybridPackageDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="hybridPackaging"
          :disabled="hybridSelection.reportNames.length === 0 && hybridSelection.customPackageCodes.length === 0"
          @click="submitHybridPackage"
        >
          开始混合打包
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReportPdf, listPackages, listAvailableReports, downloadPdf, delReportPdf, delReportPdfs, updateReportPdf, mergePdfsByCondition, downloadBatchPdf, delPackageByReportId } from '@/api/system/reportPdf'
import { getSchoolPlanList } from '@/api/system/studentRecord'
import { listReportPackage, previewPackage, createPackage, createPackageByCondition, downloadPackage, delReportPackage } from '@/api/system/reportPackage'
import { listAttachmentByRelated, downloadAttachment, delReportAttachment, updateReportAttachment } from '@/api/system/reportAttachment'
import { listPackageCustom, createCustomPackage, updatePackageCustom, delPackageCustom } from '@/api/system/reportPackageCustom'

export default {
  name: 'ReportArchive',
  data() {
    return {
      loading: false,
      detailLoading: false,
      drawerVisible: false,
      // 滑动相关
      isDragging: false,
      dragStartX: 0,
      dragStartY: 0,
      dragStartTime: 0,
      dragThreshold: 10, // 拖拽阈值(像素)
      panelTop: 300, // 面板距离顶部的距离
      panelStartTop: 0, // 拖拽开始时面板的top值
      selectedReportName: '',
      // 文件包查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 1000, // 获取足够多的数据用于分组
        reportName: undefined,
        schoolingPlanId: undefined,
        gradeId: undefined,
        params: {}
      },
      dateRange: [],
      // 文件包列表
      packageList: [],
      // 详情视图查询参数
      detailQueryParams: {
        pageNum: 1,
        pageSize: 10,
        reportId: undefined,  // 添加reportId查询参数
        reportName: undefined,
        params: {}
      },
      // 详情列表数据
      detailList: [],
      detailTotal: 0,
      // 学段、年级选项
      schoolingPlanOptions: [],
      gradeOptions: [],
      // 选中（支持跨文件包选择）
      ids: [],
      selectedRecordsMap: {}, // 存储所有选中的记录 { id: record }
      multiple: true,
      isUpdatingSelection: false, // 防止循环更新的标志
      // 图标
      xlsxIcon: require('@/assets/images/zb/t.png'),
      pdfIcon: require('@/assets/images/zb/pdf.png'),
      customPackageIcon: require('@/assets/images/zb/t.png'), // 自定义档案包图标
      addPackageIcon: require('@/assets/images/zb/cj.png'), // 创建档案包图标
      titleIcon: require('@/assets/images/zb/db.png'), // 页面标题图标
      // 合并PDF相关
      mergeDialogVisible: false,
      merging: false,
      mergeForm: {
        mergedFileName: '',
        reportId: null
      },
      // 待合并PDF列表
      pdfTableData: [],
      pdfTableLoading: false,
      pdfTablePage: {
        pageNum: 1,
        pageSize: 20,
        total: 0
      },
      availableReports: [], // 有PDF的报表列表
      // 合并详情已改为气泡提示，不再使用弹窗状态
      // 打包相关
      packageDialogVisible: false,
      packaging: false,
      packageForm: {
        packageName: '',
        fileCount: 0,
        mode: 'report', // report=单个文件包打包, mixed=混合打包多个文件包, selected=选中记录打包, custom=自定义档案包打包
        reportName: '', // 单个文件包名称
        reportNames: [], // 多个文件包名称（混合打包，兼容旧逻辑）
        reportIds: [], // 多个文件包ID（混合打包，推荐使用）
        selectedIds: [], // 选中的记录ID列表
        selectedRecords: [], // 选中的记录详情列表（用于显示）
        customPackageCode: '', // 自定义档案包编码（自定义档案包打包）
        customPackageName: '' // 自定义档案包名称（用于显示）
      },
      // 文件包多选(存储reportId而非reportName)
      selectedPackageIds: [],
      packageSelectAll: false,
      packageSelectIndeterminate: false,
      // 打包记录列表
      packageListDrawerVisible: false,
      packageListLoading: false,
      packageRecordList: [], // 打包记录列表（避免与文件包列表冲突）
      packageListPage: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      // 附件管理相关
      attachmentDialogVisible: false,
      attachmentLoading: false,
      attachmentList: [],
      currentAttachmentRelatedId: '', // 当前查看附件的关联ID（reportName）
      uploadDialogVisible: false,
      uploadFileList: [],
      uploadRemark: '',
      uploading: false,
      // Tab标签页
      activeTab: 'report',
      // 自定义档案包相关
      customPackageLoading: false,
      customPackageList: [],
      customPackageTotal: 0,
      customPackageQueryParams: {
        pageNum: 1,
        pageSize: 10
      },
      customPackageSelection: [],
      customPackageDialogVisible: false,
      customPackageSaving: false,
      customPackageForm: {
        id: null,
        packageName: '',
        description: ''
      },
      customPackageRules: {
        packageName: [
          { required: true, message: '请输入档案包名称', trigger: 'blur' }
        ]
      },
      currentCustomPackage: null, // 当前选中的自定义档案包
      // 自定义档案包附件列表相关
      selectedCustomPackageCode: '', // 当前选中的自定义档案包编码
      selectedCustomPackageName: '', // 当前选中的自定义档案包名称
      customAttachmentLoading: false,
      customAttachmentList: [],
      customAttachmentTotal: 0,
      customAttachmentQueryParams: {
        pageNum: 1,
        pageSize: 10
      },
      customAttachmentSelection: [],
      // 混合打包相关
      hybridPackageDialogVisible: false,
      hybridPackaging: false,
      hybridPackageName: '',
      hybridSelection: {
        reportNames: [],
        customPackageCodes: []
      }
    }
  },
  created() {
    this.initOptions()
    this.loadPackages()
    // 加载自定义档案包列表
    this.loadCustomPackageList()
  },
  mounted() {
    // 监听归档完成事件，刷新文件包列表
    window.addEventListener('report-archived', this.handleReportArchived)
  },
  beforeDestroy() {
    // 组件销毁前移除事件监听
    window.removeEventListener('report-archived', this.handleReportArchived)
    // 清理拖拽事件监听器
    this.resetDragState()
  },
  methods: {
    /** 初始化下拉选项 */
    initOptions() {
      // 学段
      getSchoolPlanList().then(res => {
        if (res.code === 200 && res.data) {
          this.schoolingPlanOptions = res.data.map(item => ({
            label: item.name || item.schoolingPlanName || item.label,
            value: item.id || item.value
          }))
        }
      }).catch(() => {})

      // 年级：这里先用一个基础静态列表，后续可根据实际年级接口替换
      this.gradeOptions = [
        { label: '一年级', value: 1 },
        { label: '二年级', value: 2 },
        { label: '三年级', value: 3 },
        { label: '四年级', value: 4 },
        { label: '五年级', value: 5 },
        { label: '六年级', value: 6 }
      ]
    },

    /** 处理归档完成事件 */
    handleReportArchived() {
      // 刷新文件包列表
      this.loadPackages()
      // 如果当前有选中的文件包，也刷新详情列表
      if (this.selectedReportName) {
        this.loadDetailList()
      }
    },

    /** 加载文件包列表（按reportId分组） */
    loadPackages() {
      this.loading = true
      const params = { ...this.queryParams }
      // 处理时间范围
      if (this.dateRange && this.dateRange.length === 2) {
        params.params = params.params || {}
        params.params.beginCreateTime = this.dateRange[0]
        params.params.endCreateTime = this.dateRange[1]
      }
      // 调用后端分组接口，后端已处理分组、聚合、排序逻辑
      listPackages(params).then(res => {
        if (res.code === 200) {
          this.packageList = res.data || []
        } else {
          this.packageList = []
        }
      }).finally(() => {
        this.loading = false
      })
    },

    /** 点击文件包（改为按reportId查询） */
    handlePackageClick(reportName) {
      if (!reportName) return
      
      // 查找对应的文件包信息，获取reportId
      const pkg = this.packageList.find(p => p.reportName === reportName)
      if (!pkg) return
      
      // 设置选中的文件包并加载详情列表
      this.selectedReportName = reportName
      // 按reportId查询，而非reportName
      this.detailQueryParams.reportId = pkg.reportId
      this.detailQueryParams.reportName = undefined // 清空名称查询
      this.detailQueryParams.pageNum = 1
      this.loadDetailList()
    },

    /** 处理报表档案包下拉菜单命令 */
    handlePackageAction(command, pkg) {
      switch (command) {
        case 'package':
          // 打包
          this.openPackageDialogForReport(pkg.reportName, pkg.count)
          break
        case 'delete':
          // 删除
          this.handleDeleteFilePackage(pkg)
          break
        default:
          break
      }
    },

    /** 加载详情列表 */
    loadDetailList() {
      if (!this.selectedReportName) {
        this.detailList = []
        this.detailTotal = 0
        // 清空当前文件包的选中ID，但保留全局选中记录
        this.ids = []
        return
      }
      this.detailLoading = true
      const params = { ...this.detailQueryParams }
      listReportPdf(params).then(res => {
        if (res.code === 200) {
          this.detailList = res.rows || res.data || []
          this.detailTotal = res.total || 0
          // 更新当前文件包下的选中ID（仅当前文件包的记录）
          this.ids = this.detailList
            .filter(item => this.selectedRecordsMap[item.id])
            .map(item => item.id)
          // 更新表格选中状态
          this.$nextTick(() => {
            this.updateTableSelection()
          })
        } else {
          this.detailList = []
          this.detailTotal = 0
          this.ids = []
        }
      }).catch(err => {
        console.error('加载详情列表失败:', err)
        this.detailList = []
        this.detailTotal = 0
        this.ids = []
      }).finally(() => {
        this.detailLoading = false
      })
    },

    /** 搜索 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.loadPackages()
      // 查询后关闭抽屉
      this.drawerVisible = false
    },

    /** 重置 */
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 1000,
        reportName: undefined,
        schoolingPlanId: undefined,
        gradeId: undefined,
        params: {}
      }
      this.handleQuery()
    },

    /** 切换按钮点击处理 */
    handleToggleClick(e) {
      // 如果正在拖拽，不触发点击事件
      if (this.isDragging) {
        return
      }
      this.drawerVisible = !this.drawerVisible
    },

    /** 切换按钮鼠标按下 */
    handleToggleMouseDown(e) {
      this.isDragging = false
      this.dragStartX = e.clientX
      this.dragStartY = e.clientY
      this.panelStartTop = this.panelTop
      this.dragStartTime = Date.now()

      // 添加全局事件监听（使用箭头函数确保this绑定）
      this._handleDocumentMouseMove = (event) => this.handleDocumentMouseMove(event)
      this._handleDocumentMouseUp = (event) => this.handleDocumentMouseUp(event)

      document.addEventListener('mousemove', this._handleDocumentMouseMove)
      document.addEventListener('mouseup', this._handleDocumentMouseUp)

      // 阻止默认行为
      e.preventDefault()
      e.stopPropagation()
    },

    /** 文档级别鼠标移动（处理拖拽） */
    handleDocumentMouseMove(e) {
      if (this.dragStartX === 0 && this.dragStartY === 0) {
        return
      }

      const deltaX = e.clientX - this.dragStartX
      const deltaY = e.clientY - this.dragStartY
      const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)

      // 如果移动距离超过阈值，认为是拖拽操作
      if (distance > this.dragThreshold && !this.isDragging) {
        this.isDragging = true
      }

      // 如果是拖拽操作
      if (this.isDragging) {
        // 优先处理垂直拖拽（上下移动面板位置）
        if (Math.abs(deltaY) > Math.abs(deltaX)) {
          // 计算新的top值
          let newTop = this.panelStartTop + deltaY
          // 限制在可视范围内（留出最小边距）
          const minTop = 100
          const maxTop = window.innerHeight - 200
          newTop = Math.max(minTop, Math.min(maxTop, newTop))
          this.panelTop = newTop
        }
        // 水平拖拽（展开/收起面板）
        else {
          // 向左拖拽（展开面板，因为按钮在右侧）
          if (deltaX < -30 && !this.drawerVisible) {
            this.drawerVisible = true
            this.isDragging = false
            this.resetDragState()
          }
          // 向右拖拽（收起面板）
          else if (deltaX > 30 && this.drawerVisible) {
            this.drawerVisible = false
            this.isDragging = false
            this.resetDragState()
          }
        }
      }
    },

    /** 文档级别鼠标释放 */
    handleDocumentMouseUp(e) {
      // 如果只是点击（移动距离很小且时间很短），不处理（点击事件由handleToggleClick处理）
      if (!this.isDragging && this.dragStartX !== 0 && this.dragStartY !== 0) {
        const deltaX = e.clientX - this.dragStartX
        const deltaY = e.clientY - this.dragStartY
        const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
        const timeElapsed = Date.now() - this.dragStartTime

        // 如果移动距离很小且时间很短，认为是点击，不做处理（点击已在handleToggleClick中处理）
        if (distance < 5 && timeElapsed < 300) {
          // 点击事件已在handleToggleClick中处理，这里不做任何操作
        }
      }

      this.resetDragState()
    },

    /** 重置拖拽状态 */
    resetDragState() {
      this.isDragging = false
      this.dragStartX = 0
      this.dragStartY = 0
      this.dragStartTime = 0

      // 移除全局事件监听
      if (this._handleDocumentMouseMove) {
        document.removeEventListener('mousemove', this._handleDocumentMouseMove)
        this._handleDocumentMouseMove = null
      }
      if (this._handleDocumentMouseUp) {
        document.removeEventListener('mouseup', this._handleDocumentMouseUp)
        this._handleDocumentMouseUp = null
      }
    },

    /** 抽屉关闭前处理 */
    handleDrawerClose(done) {
      done()
    },

    /** 多选变化 */
    handleSelectionChange(selection) {
      // 如果正在更新选中状态，跳过处理，避免循环调用
      if (this.isUpdatingSelection) {
        return
      }

      // 更新当前文件包下的选中ID
      const currentIds = selection.map(item => item.id)

      // 移除当前文件包下之前选中的记录（如果取消选择）
      const currentReportIds = this.detailList.map(item => item.id)
      currentReportIds.forEach(id => {
        if (!currentIds.includes(id)) {
          // 从全局选中列表中移除
          delete this.selectedRecordsMap[id]
        } else {
          // 添加到全局选中列表
          const record = this.detailList.find(item => item.id === id)
          if (record) {
            this.selectedRecordsMap[id] = record
          }
        }
      })

      // 更新当前显示的选中ID（仅当前文件包）
      this.ids = currentIds

      // 计算全局选中数量
      const globalSelectedCount = Object.keys(this.selectedRecordsMap).length
      this.multiple = globalSelectedCount === 0
    },

    /** 更新表格选中状态（显示所有已选中的记录，包括其他文件包的） */
    updateTableSelection() {
      if (this.$refs.detailTable && !this.isUpdatingSelection) {
        this.isUpdatingSelection = true
        try {
          // 清除所有选中
          this.$refs.detailTable.clearSelection()
          // 选中当前文件包下已选择的记录
          this.detailList.forEach(row => {
            if (this.selectedRecordsMap[row.id]) {
              this.$refs.detailTable.toggleRowSelection(row, true)
            }
          })
        } finally {
          this.$nextTick(() => {
            this.isUpdatingSelection = false
          })
        }
      }
    },

    /** 表格行样式 */
    tableRowClassName({ row }) {
      return row && row.isMerged ? 'merged-row' : ''
    },

    /** 空值占位 */
    displayText(value) {
      return value ? value : '—'
    },

    /** 预览：新窗口打开 PDF */
    handlePreview(row) {
      if (!row || !row.id) return
      downloadPdf(row.id).then(res => {
        const blob = new Blob([res], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        window.open(url, '_blank')
      }).catch(() => {
        this.$message.error('预览失败')
      })
    },

    /** 下载 */
    handleDownload(row) {
      if (!row || !row.id) return
      downloadPdf(row.id).then(res => {
        const blob = new Blob([res], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = row.fileName || 'report.pdf'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      }).catch(() => {
        this.$message.error('下载失败')
      })
    },

    /** 状态切换 */
    handleStatusChange(row) {
      const statusText = row.status === 1 ? '恢复' : '销毁'
      const actionText = row.status === 1 ? '恢复成功' : '销毁成功'

      updateReportPdf({
        id: row.id,
        status: row.status
      }).then(() => {
        this.$modal.msgSuccess(actionText)
        this.loadDetailList()
        // 刷新文件包列表
        this.loadPackages()
        // 如果详情列表为空，清除选中状态
        if (this.detailList.length === 0) {
          this.selectedReportName = ''
          this.detailList = []
          this.detailTotal = 0
        }
      }).catch(() => {
        // 失败时恢复原状态
        row.status = row.status === 1 ? 0 : 1
        this.$message.error(`${statusText}失败`)
      })
    },

    /** 单条销毁 */
    handleRemove(row) {
      this.$modal
        .confirm(`确认要销毁该归档记录吗？该操作不可恢复。`)
        .then(() => delReportPdf(row.id))
        .then(() => {
          this.$modal.msgSuccess('销毁成功')
          this.loadDetailList()
          // 刷新文件包列表
          this.loadPackages()
          // 如果详情列表为空，清除选中状态
          if (this.detailList.length <= 1) {
            this.selectedReportName = ''
            this.detailList = []
            this.detailTotal = 0
          }
        })
        .catch(() => {})
    },

    /** 批量销毁 */
    handleBatchRemove() {
      if (!this.ids.length) return
      this.$modal
        .confirm(`确认要销毁选中的 ${this.ids.length} 条归档记录吗？该操作不可恢复。`)
        .then(() => delReportPdfs(this.ids.join(',')))
        .then(() => {
          this.$modal.msgSuccess('销毁成功')
          this.loadDetailList()
          // 刷新文件包列表
          this.loadPackages()
        })
        .catch(() => {})
    },

    /** 删除文件包（删除该报表的所有PDF归档及物理文件） */
    handleDeleteFilePackage(pkg) {
      if (!pkg || !pkg.reportId) return
      
      const confirmMsg = pkg.hasMultipleNames 
        ? `该文件包包含历史改名的归档记录，共 ${pkg.count} 个文件。

确认要删除文件包「${pkg.reportName}」吗？

注意：删除后无法恢复，所有PDF文件将被永久删除！`
        : `确认要删除文件包「${pkg.reportName}」吗？\n\n该文件包共 ${pkg.count} 个文件，删除后无法恢复！`
      
      this.$modal.confirm(confirmMsg).then(() => {
        return delPackageByReportId(pkg.reportId)
      }).then((res) => {
        if (res.code === 200) {
          this.$modal.msgSuccess(res.msg || '删除成功')
          // 刷新文件包列表
          this.loadPackages()
          // 如果删除的是当前选中的文件包，清空详情
          if (this.selectedReportName === pkg.reportName) {
            this.selectedReportName = ''
            this.detailList = []
            this.detailTotal = 0
            this.detailQueryParams.reportId = undefined
            this.detailQueryParams.reportName = undefined
          }
        } else {
          this.$modal.msgError(res.msg || '删除失败')
        }
      }).catch((err) => {
        if (err !== 'cancel') {
          console.error('删除文件包失败:', err)
          this.$modal.msgError('删除失败：' + (err.message || '网络错误'))
        }
      })
    },

    /** 打开合并PDF弹窗 */
    openMergeDialog() {
      this.mergeDialogVisible = true
      this.loadAvailableReports()
    },

    /** 重置合并表单 */
    resetMergeForm() {
      this.mergeForm = {
        mergedFileName: '',
        reportId: null
      }
      this.pdfTableData = []
      this.pdfTablePage.pageNum = 1
      this.pdfTablePage.total = 0
    },

    /** 加载有PDF的报表列表 */
    loadAvailableReports() {
      listReportPdf({ status: 1, isMerged: 0 }).then(res => {
        if (res.code === 200) {
          const allPdfs = res.rows || res.data || []
          const reportMap = new Map()
          allPdfs.forEach(pdf => {
            const reportId = pdf.reportId
            const reportName = pdf.reportName || '未知报表'
            if (!reportMap.has(reportId)) {
              reportMap.set(reportId, {
                id: reportId,
                name: reportName,
                pdfCount: 0
              })
            }
            reportMap.get(reportId).pdfCount++
          })
          this.availableReports = Array.from(reportMap.values()).filter(r => r.pdfCount > 0)
          if (this.mergeForm.reportId && this.availableReports.find(r => r.id === this.mergeForm.reportId)) {
            this.handleReportSelectChange(this.mergeForm.reportId)
          }
        }
      }).catch(err => {
        console.error('加载报表列表失败:', err)
        this.availableReports = []
      })
    },

    /** 报表选择变化 */
    handleReportSelectChange(reportId) {
      if (!reportId) {
        this.pdfTableData = []
        this.pdfTablePage.total = 0
        this.mergeForm.mergedFileName = ''
        return
      }
      const report = this.availableReports.find(r => r.id === reportId)
      const reportName = report ? report.name : '报表'
      this.mergeForm.mergedFileName = reportName + '汇总'
      this.pdfTablePage.pageNum = 1
      this.loadPdfListByReport(reportId)
    },

    /** 根据报表ID加载PDF列表 */
    loadPdfListByReport(reportId) {
      this.pdfTableLoading = true
      const params = {
        pageNum: this.pdfTablePage.pageNum,
        pageSize: this.pdfTablePage.pageSize,
        reportId: reportId,
        status: 1,
        isMerged: 0
      }
      listReportPdf(params).then(res => {
        if (res.code === 200) {
          this.pdfTableData = (res.rows || res.data || []).map(item => ({
            id: item.id,
            reportName: item.reportName || '未知报表',
            fileName: item.fileName || '未知文件',
            fileSize: item.fileSize || 0,
            createTime: item.createTime || '',
            isMerged: item.isMerged || 0
          }))
          this.pdfTablePage.total = res.total || 0
        } else {
          this.pdfTableData = []
          this.pdfTablePage.total = 0
        }
      }).catch(err => {
        console.error('加载PDF列表失败:', err)
        this.pdfTableData = []
        this.pdfTablePage.total = 0
        this.$modal.msgError('加载PDF列表失败：' + (err.message || '网络错误'))
      }).finally(() => {
        this.pdfTableLoading = false
      })
    },

    /** PDF表格分页 */
    handlePdfPageSizeChange(size) {
      this.pdfTablePage.pageSize = size
      this.pdfTablePage.pageNum = 1
      if (this.mergeForm.reportId) {
        this.loadPdfListByReport(this.mergeForm.reportId)
      }
    },
    handlePdfPageChange(page) {
      this.pdfTablePage.pageNum = page
      if (this.mergeForm.reportId) {
        this.loadPdfListByReport(this.mergeForm.reportId)
      }
    },

    /** 下载单个PDF */
    downloadSinglePdf(id) {
      downloadPdf(id).then(res => {
        const blob = new Blob([res], { type: 'application/pdf' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        const pdf = this.pdfTableData.find(p => p.id === id)
        link.download = pdf ? pdf.fileName : 'download.pdf'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
        this.$modal.msgSuccess('下载成功')
      }).catch(err => {
        console.error('下载失败:', err)
        this.$modal.msgError('下载失败：' + (err.message || '网络错误'))
      })
    },

    /** 提交合并 */
    async submitMerge() {
      if (!this.mergeForm.reportId) {
        this.$modal.msgWarning('请选择要合并的报表')
        return
      }
      if (this.pdfTablePage.total < 1) {
        this.$modal.msgWarning('该报表没有可合并的PDF文件')
        return
      }
      if (this.merging) {
        return
      }
      this.merging = true
      try {
        const response = await mergePdfsByCondition({
          reportId: this.mergeForm.reportId,
          yearSemesterId: null,
          schoolingPlanId: null,
          gradeId: null,
          classId: null,
          mergedFileName: this.mergeForm.mergedFileName.trim()
        })
        if (response.code === 200) {
          this.$modal.msgSuccess('PDF合并成功')
          // 后端已完成合并与归档，前端仅提示下载
          this.$modal.confirm('是否立即下载合并后的PDF？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '取消',
            type: 'success'
          }).then(() => {
            this.downloadMergedPdf(response.data.id, response.data.mergedFileName)
          }).catch(() => {})
          // 刷新列表
          this.loadAvailableReports()
          if (this.mergeForm.reportId) {
            this.loadPdfListByReport(this.mergeForm.reportId)
          }
          // 通知归档页面刷新
          window.dispatchEvent(new CustomEvent('report-archived'))
        } else {
          this.$modal.msgError(response.msg || 'PDF合并失败')
        }
      } catch (error) {
        console.error('合并PDF失败:', error)
        this.$modal.msgError('PDF合并失败：' + (error.message || '网络错误'))
      } finally {
        this.merging = false
      }
    },

    /** 下载合并后的PDF */
    downloadMergedPdf(batchId, fileName) {
      downloadBatchPdf(batchId).then(res => {
        const blob = new Blob([res], { type: 'application/pdf' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = fileName || 'merged.pdf'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
        this.$modal.msgSuccess('下载成功')
      }).catch(err => {
        console.error('下载失败:', err)
        this.$modal.msgError('下载失败：' + (err.message || '网络错误'))
      })
    },

    /** 文件大小格式化 */
    formatSize(size) {
      if (!size && size !== 0) return '0 B'
      const kb = size / 1024
      if (kb < 1024) return kb.toFixed(1) + ' KB'
      const mb = kb / 1024
      if (mb < 1024) return mb.toFixed(2) + ' MB'
      const gb = mb / 1024
      return gb.toFixed(2) + ' GB'
    },

    /** 根据学段ID获取学段名称 */
    getSchoolingPlanName(schoolingPlanId) {
      if (!schoolingPlanId) return '-'
      const option = this.schoolingPlanOptions.find(item => item.value === schoolingPlanId)
      return option ? option.label : '-'
    },

    /** 格式化时间 */
    formatTime(timeStr) {
      if (!timeStr) return '暂无'
      // 如果是完整的时间字符串，只显示日期部分
      if (timeStr.includes(' ')) {
        return timeStr.split(' ')[0]
      }
      return timeStr
    },

    /** 文件包多选处理 */
    handlePackageCheckboxChange(reportName, checked) {
      // 根据reportName查找对应的reportId
      const pkg = this.packageList.find(p => p.reportName === reportName)
      if (!pkg) return
      
      const reportId = pkg.reportId
      if (checked) {
        if (!this.selectedPackageIds.includes(reportId)) {
          this.selectedPackageIds.push(reportId)
        }
      } else {
        const index = this.selectedPackageIds.indexOf(reportId)
        if (index > -1) {
          this.selectedPackageIds.splice(index, 1)
        }
      }
      this.updatePackageSelectAllState()
    },

    /** 文件包全选处理 */
    handlePackageSelectAll(checked) {
      if (checked) {
        this.selectedPackageIds = this.packageList.map(p => p.reportId)
      } else {
        this.selectedPackageIds = []
      }
      this.updatePackageSelectAllState()
    },

    /** 更新文件包全选状态 */
    updatePackageSelectAllState() {
      const total = this.packageList.length
      const selected = this.selectedPackageIds.length
      this.packageSelectAll = selected === total && total > 0
      this.packageSelectIndeterminate = selected > 0 && selected < total
    },

    /** 打开打包弹窗（针对某个文件包） */
    openPackageDialogForReport(reportName, fileCount) {
      if (!reportName) {
        this.$modal.msgWarning('请先选择文件包')
        return
      }
      // 如果fileCount未提供，尝试从packageList中获取
      if (fileCount === undefined || fileCount === null) {
        const pkg = this.packageList.find(p => p.reportName === reportName)
        fileCount = pkg ? pkg.count : 0
      }
      if (fileCount === 0) {
        this.$modal.msgWarning('该文件包下没有可打包的文件')
        return
      }
      this.packageForm = {
        packageName: reportName + '_档案打包',
        fileCount: fileCount,
        reportName: reportName,
        reportNames: [],
        mode: 'report'
      }
      this.packageDialogVisible = true
    },

    /** 处理批量打包下拉菜单命令 */
    handleBatchPackageCommand(command) {
      if (command === 'all') {
        this.openAllPackageDialog()
      } else if (command === 'mixed') {
        this.openMixedPackageDialog()
      }
    },

    /** 打开一键打包弹窗（打包全部文件包） */
    async openAllPackageDialog() {
      // 查询所有符合条件的PDF记录，获取ID列表，然后调用预览接口
      try {
        // 构建查询条件（不指定reportName，查询所有符合条件的记录）
        const queryParams = {
          status: 1 // 只查询正常状态的记录
        }
        // 添加高级查询条件
        if (this.queryParams.yearSemesterId) {
          queryParams.yearSemesterId = this.queryParams.yearSemesterId
        }
        if (this.queryParams.schoolingPlanId) {
          queryParams.schoolingPlanId = this.queryParams.schoolingPlanId
        }
        if (this.queryParams.gradeId) {
          queryParams.gradeId = this.queryParams.gradeId
        }
        if (this.queryParams.classId) {
          queryParams.classId = this.queryParams.classId
        }
        if (this.queryParams.studentName) {
          queryParams.studentName = this.queryParams.studentName
        }
        // 如果有时间范围，添加到查询条件中
        if (this.dateRange && this.dateRange.length === 2) {
          queryParams.beginCreateTime = this.dateRange[0]
          queryParams.endCreateTime = this.dateRange[1]
        }

        // 查询所有符合条件的PDF记录
        const response = await listReportPdf(queryParams)
        if (response.code === 200) {
          const pdfList = response.rows || []

          if (pdfList.length === 0) {
            this.$modal.msgWarning('没有可打包的文件')
            return
          }

          // 获取PDF ID列表
          const pdfIds = pdfList.map(pdf => pdf.id)

          // 调用预览接口，获取统计信息和建议的打包名称
          const previewResponse = await previewPackage({ pdfIds: pdfIds })
          if (previewResponse.code === 200) {
            const preview = previewResponse.data
            this.packageForm = {
              packageName: preview.suggestedPackageName || '',
              fileCount: preview.fileCount || 0,
              reportName: '',
              reportNames: [], // 一键打包不限制报表名称
              selectedIds: [],
              selectedRecords: [],
              mode: 'all' // 一键打包全部
            }
            this.packageDialogVisible = true
          } else {
            this.$modal.msgError(previewResponse.msg || '预览失败')
          }
        } else {
          this.$modal.msgError(response.msg || '查询失败')
        }
      } catch (error) {
        console.error('打开一键打包弹窗失败:', error)
        this.$modal.msgError('预览失败：' + (error.message || '网络错误'))
      }
    },

    /** 打开混合打包弹窗 */
    async openMixedPackageDialog() {
      if (this.selectedPackageIds.length === 0) {
        this.$modal.msgWarning('请至少选择一个文件包')
        return
      }

      // 先查询符合条件的PDF记录，获取ID列表，然后调用预览接口
      try {
        // 构建查询条件（不指定reportName，查询所有符合条件的记录，然后过滤）
        const queryParams = {
          status: 1 // 只查询正常状态的记录
        }
        // 添加高级查询条件
        if (this.queryParams.yearSemesterId) {
          queryParams.yearSemesterId = this.queryParams.yearSemesterId
        }
        if (this.queryParams.schoolingPlanId) {
          queryParams.schoolingPlanId = this.queryParams.schoolingPlanId
        }
        if (this.queryParams.gradeId) {
          queryParams.gradeId = this.queryParams.gradeId
        }
        if (this.queryParams.classId) {
          queryParams.classId = this.queryParams.classId
        }
        if (this.queryParams.studentName) {
          queryParams.studentName = this.queryParams.studentName
        }
        // 如果有时间范围，添加到查询条件中
        if (this.dateRange && this.dateRange.length === 2) {
          queryParams.beginCreateTime = this.dateRange[0]
          queryParams.endCreateTime = this.dateRange[1]
        }

        // 查询所有符合条件的PDF记录
        const response = await listReportPdf(queryParams)
        if (response.code === 200) {
          let pdfList = response.rows || []
          // 按reportId过滤（而非reportName）
          pdfList = pdfList.filter(pdf => this.selectedPackageIds.includes(pdf.reportId))

          if (pdfList.length === 0) {
            this.$modal.msgWarning('所选文件包下没有可打包的文件')
            return
          }

          // 获取PDF ID列表
          const pdfIds = pdfList.map(pdf => pdf.id)

          // 调用预览接口，获取统计信息和建议的打包名称
          const previewResponse = await previewPackage({ pdfIds: pdfIds })
          if (previewResponse.code === 200) {
            const preview = previewResponse.data
            this.packageForm = {
              packageName: preview.suggestedPackageName || '',
              fileCount: preview.fileCount || 0,
              reportName: '',
              reportNames: [], // 不再使用reportNames
              reportIds: [...this.selectedPackageIds], // 直接传递reportIds
              selectedIds: [],
              selectedRecords: [],
              mode: 'mixed'
            }
            this.packageDialogVisible = true
          } else {
            this.$modal.msgError(previewResponse.msg || '预览失败')
          }
        } else {
          this.$modal.msgError(response.msg || '查询失败')
        }
      } catch (error) {
        console.error('打开混合打包弹窗失败:', error)
        this.$modal.msgError('预览失败：' + (error.message || '网络错误'))
      }
    },

    /** 打开选中记录打包弹窗 */
    async openPackageDialogForSelected() {
      // 从全局选中记录中获取所有选中的记录ID（可能来自不同文件包）
      const selectedIds = Object.keys(this.selectedRecordsMap).map(id => parseInt(id))
      if (selectedIds.length === 0) {
        this.$modal.msgWarning('请先选择要打包的记录')
        return
      }

      // 调用后端预览接口，获取统计信息和建议的打包名称（所有逻辑在后端处理）
      try {
        const response = await previewPackage({ pdfIds: selectedIds })
        if (response.code === 200) {
          const preview = response.data
          this.packageForm = {
            packageName: preview.suggestedPackageName || '',
            fileCount: preview.fileCount || 0,
            reportName: '',
            reportNames: preview.reportNames || [],
            selectedIds: selectedIds,
            selectedRecords: preview.selectedRecords || [],
            mode: 'selected'
          }
          this.packageDialogVisible = true
        } else {
          this.$modal.msgError(response.msg || '预览失败')
        }
      } catch (error) {
        console.error('预览打包信息失败:', error)
        this.$modal.msgError('预览失败：' + (error.message || '网络错误'))
      }
    },

    /** 提交打包 */
    async submitPackage() {
      if (this.packaging) {
        return
      }
      this.packaging = true
      try {
        let response
        if (this.packageForm.mode === 'selected') {
          // 选中记录打包：只发送ID列表和打包名称，所有验证和逻辑都在后端处理
          response = await createPackage({
            pdfIds: this.packageForm.selectedIds,
            packageName: this.packageForm.packageName || undefined // 如果为空，后端会自动生成
          })
        } else if (this.packageForm.mode === 'all') {
          // 一键打包全部：不限制报表名称
          const queryParams = {
            allPackages: true, // 标识为一键打包全部
            packageName: this.packageForm.packageName
          }
          // 添加高级查询条件
          if (this.queryParams.yearSemesterId) {
            queryParams.yearSemesterId = this.queryParams.yearSemesterId
          }
          if (this.queryParams.schoolingPlanId) {
            queryParams.schoolingPlanId = this.queryParams.schoolingPlanId
          }
          if (this.queryParams.gradeId) {
            queryParams.gradeId = this.queryParams.gradeId
          }
          if (this.queryParams.classId) {
            queryParams.classId = this.queryParams.classId
          }
          if (this.queryParams.studentName) {
            queryParams.studentName = this.queryParams.studentName
          }
          // 如果有时间范围，添加到查询条件中
          if (this.dateRange && this.dateRange.length === 2) {
            queryParams.beginCreateTime = this.dateRange[0]
            queryParams.endCreateTime = this.dateRange[1]
          }
          response = await createPackageByCondition(queryParams)
        } else if (this.packageForm.mode === 'mixed') {
          // 混合打包：优先使用reportIds，其次reportNames（兼容旧版本）
          const queryParams = {
            packageName: this.packageForm.packageName
          }
          // 优先使用reportIds
          if (this.packageForm.reportIds && this.packageForm.reportIds.length > 0) {
            queryParams.reportIds = this.packageForm.reportIds
          } else if (this.packageForm.reportNames && this.packageForm.reportNames.length > 0) {
            // 兼容旧逻辑：使用reportNames
            queryParams.reportNames = this.packageForm.reportNames
          }
          // 如果有高级查询条件，也加上
          if (this.queryParams.yearSemesterId) {
            queryParams.yearSemesterId = this.queryParams.yearSemesterId
          }
          if (this.queryParams.schoolingPlanId) {
            queryParams.schoolingPlanId = this.queryParams.schoolingPlanId
          }
          if (this.queryParams.gradeId) {
            queryParams.gradeId = this.queryParams.gradeId
          }
          if (this.queryParams.classId) {
            queryParams.classId = this.queryParams.classId
          }
          if (this.queryParams.studentName) {
            queryParams.studentName = this.queryParams.studentName
          }
          // 如果有时间范围，添加到查询条件中
          if (this.dateRange && this.dateRange.length === 2) {
            queryParams.beginCreateTime = this.dateRange[0]
            queryParams.endCreateTime = this.dateRange[1]
          }
          response = await createPackageByCondition(queryParams)
        } else if (this.packageForm.mode === 'custom') {
          // 自定义档案包打包
          const queryParams = {
            customPackageCodes: [this.packageForm.customPackageCode],
            packageName: this.packageForm.packageName
          }
          response = await createPackageByCondition(queryParams)
        } else {
          // 单个文件包打包
          const queryParams = {
            reportName: this.packageForm.reportName,
            packageName: this.packageForm.packageName
          }
          // 如果有高级查询条件，也加上
          if (this.queryParams.yearSemesterId) {
            queryParams.yearSemesterId = this.queryParams.yearSemesterId
          }
          if (this.queryParams.schoolingPlanId) {
            queryParams.schoolingPlanId = this.queryParams.schoolingPlanId
          }
          if (this.queryParams.gradeId) {
            queryParams.gradeId = this.queryParams.gradeId
          }
          if (this.queryParams.classId) {
            queryParams.classId = this.queryParams.classId
          }
          if (this.queryParams.studentName) {
            queryParams.studentName = this.queryParams.studentName
          }
          // 如果有时间范围，添加到查询条件中
          if (this.dateRange && this.dateRange.length === 2) {
            queryParams.beginCreateTime = this.dateRange[0]
            queryParams.endCreateTime = this.dateRange[1]
          }
          response = await createPackageByCondition(queryParams)
        }

        if (response.code === 200) {
          this.$modal.msgSuccess('打包成功')
          this.packageDialogVisible = false
          // 如果打包记录抽屉已打开，刷新列表
          if (this.packageListDrawerVisible) {
            this.loadPackageList()
          }
          // 提示下载
          this.$modal.confirm('是否立即下载打包文件？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '稍后',
            type: 'success'
          }).then(() => {
            this.handleDownloadPackage(response.data)
          }).catch(() => {})
        } else {
          this.$modal.msgError(response.msg || '打包失败')
        }
      } catch (error) {
        console.error('打包失败:', error)
        this.$modal.msgError('打包失败：' + (error.message || '网络错误'))
      } finally {
        this.packaging = false
      }
    },

    /** 打开打包记录列表抽屉 */
    openPackageListDrawer() {
      this.packageListDrawerVisible = true
      this.loadPackageList()
    },

    /** 关闭打包记录列表抽屉 */
    handlePackageListDrawerClose() {
      this.packageListDrawerVisible = false
    },

    /** 加载打包记录列表 */
    async loadPackageList() {
      this.packageListLoading = true
      try {
        const response = await listReportPackage({
          pageNum: this.packageListPage.pageNum,
          pageSize: this.packageListPage.pageSize
        })
        if (response.code === 200) {
          this.packageRecordList = response.rows || response.data || []
          this.packageListPage.total = response.total || 0
        } else {
          this.packageRecordList = []
          this.packageListPage.total = 0
        }
      } catch (error) {
        console.error('加载打包记录失败:', error)
        this.packageRecordList = []
        this.packageListPage.total = 0
        this.$modal.msgError('加载打包记录失败：' + (error.message || '网络错误'))
      } finally {
        this.packageListLoading = false
      }
    },

    /** 打包记录分页 */
    handlePackageListPageSizeChange(size) {
      this.packageListPage.pageSize = size
      this.packageListPage.pageNum = 1
      this.loadPackageList()
    },
    handlePackageListPageChange(page) {
      this.packageListPage.pageNum = page
      this.loadPackageList()
    },

    /** 下载打包文件 */
    async handleDownloadPackage(row) {
      try {
        const response = await downloadPackage(row.id)
        const blob = new Blob([response], { type: 'application/zip' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = row.zipFileName || 'package.zip'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
        this.$modal.msgSuccess('下载成功')
      } catch (error) {
        console.error('下载失败:', error)
        this.$modal.msgError('下载失败：' + (error.message || '网络错误'))
      }
    },

    /** 删除打包记录 */
    handleDeletePackage(row) {
      this.$modal.confirm('确定要删除该打包记录吗？删除后无法恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delReportPackage(row.id).then(() => {
          this.$modal.msgSuccess('删除成功')
          this.loadPackageList()
        }).catch(err => {
          console.error('删除失败:', err)
          this.$modal.msgError('删除失败：' + (err.message || '网络错误'))
        })
      }).catch(() => {})
    },

    /** 打开附件管理对话框 */
    openAttachmentDialog() {
      if (!this.selectedReportName) {
        this.$modal.msgWarning('请先选择文件包')
        return
      }
      this.currentAttachmentRelatedId = this.selectedReportName
      this.attachmentDialogVisible = true
      this.loadAttachmentList()
    },

    /** 关闭附件管理对话框 */
    handleAttachmentDialogClose() {
      this.attachmentList = []
      this.currentAttachmentRelatedId = ''
    },

    /** 加载附件列表 */
    async loadAttachmentList() {
      if (!this.currentAttachmentRelatedId) {
        return
      }
      this.attachmentLoading = true
      try {
        const params = {
          attachType: 'report',
          relatedId: this.currentAttachmentRelatedId
        }
        const response = await listAttachmentByRelated(params)
        if (response.code === 200) {
          this.attachmentList = (response.data || []).map(item => ({
            ...item,
            editingRemark: false,
            tempRemark: item.remark || ''
          }))
        } else {
          this.attachmentList = []
          this.$modal.msgError(response.msg || '加载附件列表失败')
        }
      } catch (error) {
        console.error('加载附件列表失败:', error)
        this.attachmentList = []
        this.$modal.msgError('加载附件列表失败：' + (error.message || '网络错误'))
      } finally {
        this.attachmentLoading = false
      }
    },

    /** 打开上传对话框 */
    openUploadDialog() {
      this.uploadDialogVisible = true
      this.uploadFileList = []
      this.uploadRemark = ''
    },

    /** 关闭上传对话框 */
    handleUploadDialogClose() {
      this.uploadFileList = []
      this.uploadRemark = ''
    },

    /** 提交上传 */
    submitUpload() {
      if (!this.$refs.upload || !this.$refs.upload.uploadFiles || this.$refs.upload.uploadFiles.length === 0) {
        this.$modal.msgWarning('请选择要上传的文件')
        return
      }
      this.uploading = true
      this.$refs.upload.submit()
    },

    /** 上传前验证 */
    beforeUpload(file) {
      const isLt50M = file.size / 1024 / 1024 < 50
      if (!isLt50M) {
        this.$modal.msgError(`文件大小不能超过50MB！`)
        return false
      }
      return true
    },

    /** 上传成功 */
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.$modal.msgSuccess('上传成功')
        // 刷新附件列表
        if (this.activeTab === 'custom') {
          this.loadCustomAttachmentList()
          // 刷新档案包列表（后端已自动更新统计）
          this.loadCustomPackageList()
        } else {
          this.loadAttachmentList()
        }
        // 关闭对话框
        this.uploadDialogVisible = false
      } else {
        this.$modal.msgError(response.msg || '上传失败')
      }
      this.uploading = false
    },

    /** 上传失败 */
    handleUploadError(err, file, fileList) {
      console.error('上传失败:', err)
      this.$modal.msgError('上传失败：' + (err.message || '网络错误'))
      this.uploading = false
    },

    /** 下载附件 */
    async handleDownloadAttachment(row) {
      try {
        const response = await downloadAttachment(row.id)
        const blob = new Blob([response])
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = row.originalName || row.fileName || 'attachment'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
        this.$modal.msgSuccess('下载成功')
      } catch (error) {
        console.error('下载附件失败:', error)
        this.$modal.msgError('下载附件失败：' + (error.message || '网络错误'))
      }
    },

    /** 预览附件 */
    async handlePreviewAttachment(row) {
      try {
        // 获取文件类型
        const fileExt = row.fileExt ? row.fileExt.toLowerCase() : ''
        const fileType = row.fileType ? row.fileType.toLowerCase() : ''
        
        // 支持预览的文件类型
        const previewableTypes = ['pdf', 'image', 'txt', 'text']
        const previewableExts = ['pdf', 'jpg', 'jpeg', 'png', 'gif', 'bmp', 'txt']
        
        const canPreview = previewableTypes.includes(fileType) || previewableExts.includes(fileExt)
        
        if (!canPreview) {
          this.$modal.msgWarning('该文件类型不支持预览，请下载后查看')
          return
        }
        
        // 下载文件并预览
        const response = await downloadAttachment(row.id)
        
        // 根据文件类型设置 MIME type
        let mimeType = 'application/octet-stream'
        if (fileType === 'pdf' || fileExt === 'pdf') {
          mimeType = 'application/pdf'
        } else if (fileType === 'image' || ['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(fileExt)) {
          mimeType = `image/${fileExt === 'jpg' ? 'jpeg' : fileExt}`
        } else if (fileType === 'text' || fileExt === 'txt') {
          mimeType = 'text/plain'
        }
        
        const blob = new Blob([response], { type: mimeType })
        const url = window.URL.createObjectURL(blob)
        
        // 在新窗口中打开预览
        window.open(url, '_blank')
        
        // 延迟释放URL，给浏览器时间加载
        setTimeout(() => {
          window.URL.revokeObjectURL(url)
        }, 1000)
      } catch (error) {
        console.error('预览附件失败:', error)
        this.$modal.msgError('预览失败：' + (error.message || '网络错误'))
      }
    },

    /** 删除附件 */
    handleDeleteAttachment(row) {
      this.$modal.confirm('确定要删除该附件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delReportAttachment([row.id]).then(() => {
          this.$modal.msgSuccess('删除成功')
          // 刷新附件列表
          if (this.activeTab === 'custom') {
            this.loadCustomAttachmentList()
            // 刷新档案包列表（后端已自动更新统计）
            this.loadCustomPackageList()
          } else {
            this.loadAttachmentList()
          }
        }).catch(err => {
          console.error('删除附件失败:', err)
          this.$modal.msgError('删除附件失败：' + (err.message || '网络错误'))
        })
      }).catch(() => {})
    },

    /** 编辑备注 */
    editRemark(row) {
      this.$set(row, 'editingRemark', true)
      this.$set(row, 'tempRemark', row.remark || '')
    },

    /** 取消编辑备注 */
    cancelEditRemark(row) {
      this.$set(row, 'editingRemark', false)
      this.$set(row, 'tempRemark', row.remark || '')
    },

    /** 保存备注 */
    async saveRemark(row) {
      try {
        await updateReportAttachment({
          id: row.id,
          remark: row.tempRemark
        })
        this.$modal.msgSuccess('保存成功')
        row.remark = row.tempRemark
        this.$set(row, 'editingRemark', false)
      } catch (error) {
        console.error('保存备注失败:', error)
        this.$modal.msgError('保存失败：' + (error.message || '网络错误'))
      }
    },

    /** 文件类型标签颜色 */
    getFileTypeTag(fileType) {
      const typeMap = {
        pdf: 'danger',
        image: 'success',
        word: 'primary',
        excel: 'warning',
        powerpoint: '',
        archive: 'info',
        text: ''
      }
      return typeMap[fileType] || 'info'
    },

    /** Tab切换 */
    handleTabClick(tab) {
      if (tab.name === 'custom') {
        this.loadCustomPackageList()
      }
    },

    /** 加载自定义档案包列表 */
    async loadCustomPackageList() {
      this.customPackageLoading = true
      try {
        const response = await listPackageCustom(this.customPackageQueryParams)
        if (response.code === 200) {
          this.customPackageList = response.rows || []
          this.customPackageTotal = response.total || 0
        } else {
          this.customPackageList = []
          this.customPackageTotal = 0
          this.$modal.msgError(response.msg || '加载失败')
        }
      } catch (error) {
        console.error('加载自定义档案包列表失败:', error)
        this.customPackageList = []
        this.customPackageTotal = 0
        this.$modal.msgError('加载失败：' + (error.message || '网络错误'))
      } finally {
        this.customPackageLoading = false
      }
    },

    /** 打开创建自定义档案包对话框 */
    openCreateCustomPackageDialog() {
      this.customPackageForm = {
        id: null,
        packageName: '',
        description: ''
      }
      this.customPackageDialogVisible = true
      this.$nextTick(() => {
        this.$refs.customPackageFormRef && this.$refs.customPackageFormRef.clearValidate()
      })
    },

    /** 编辑自定义档案包 */
    handleEditCustomPackage(row) {
      this.customPackageForm = {
        id: row.id,
        packageName: row.packageName,
        description: row.description
      }
      this.customPackageDialogVisible = true
      this.$nextTick(() => {
        this.$refs.customPackageFormRef && this.$refs.customPackageFormRef.clearValidate()
      })
    },

    /** 关闭自定义档案包对话框 */
    handleCustomPackageDialogClose() {
      this.customPackageForm = {
        id: null,
        packageName: '',
        description: ''
      }
      this.$refs.customPackageFormRef && this.$refs.customPackageFormRef.clearValidate()
    },

    /** 提交自定义档案包 */
    submitCustomPackage() {
      this.$refs.customPackageFormRef.validate(async valid => {
        if (!valid) {
          return
        }
        this.customPackageSaving = true
        try {
          if (this.customPackageForm.id) {
            // 编辑
            await updatePackageCustom(this.customPackageForm)
            this.$modal.msgSuccess('修改成功')
          } else {
            // 创建
            await createCustomPackage({
              packageName: this.customPackageForm.packageName,
              description: this.customPackageForm.description
            })
            this.$modal.msgSuccess('创建成功')
          }
          this.customPackageDialogVisible = false
          this.loadCustomPackageList()
        } catch (error) {
          console.error('保存失败:', error)
          this.$modal.msgError('保存失败：' + (error.message || '网络错误'))
        } finally {
          this.customPackageSaving = false
        }
      })
    },

    /** 删除自定义档案包 */
    handleDeleteCustomPackage(row) {
      this.$modal.confirm('确定要删除档案包“' + row.packageName + '”吗？删除后所有关联附件也将被删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delPackageCustom(row.id).then(() => {
          this.$modal.msgSuccess('删除成功')
          this.loadCustomPackageList()
        }).catch(err => {
          console.error('删除失败:', err)
          this.$modal.msgError('删除失败：' + (err.message || '网络错误'))
        })
      }).catch(() => {})
    },

    /** 批量删除自定义档案包 */
    handleDeleteCustomPackages() {
      if (this.customPackageSelection.length === 0) {
        this.$modal.msgWarning('请选择要删除的档案包')
        return
      }
      const ids = this.customPackageSelection.map(item => item.id)
      this.$modal.confirm('确定要删除选中的 ' + ids.length + ' 个档案包吗？删除后所有关联附件也将被删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delPackageCustom(ids).then(() => {
          this.$modal.msgSuccess('删除成功')
          this.loadCustomPackageList()
        }).catch(err => {
          console.error('删除失败:', err)
          this.$modal.msgError('删除失败：' + (err.message || '网络错误'))
        })
      }).catch(() => {})
    },

    /** 自定义档案包选择变化 */
    handleCustomPackageSelectionChange(selection) {
      this.customPackageSelection = selection
    },

    /** 自定义档案包复选框变化 */
    handleCustomPackageCheckboxChange(pkg, checked) {
      if (checked) {
        if (!this.customPackageSelection.some(item => item.packageCode === pkg.packageCode)) {
          this.customPackageSelection.push(pkg)
        }
      } else {
        const index = this.customPackageSelection.findIndex(item => item.packageCode === pkg.packageCode)
        if (index > -1) {
          this.customPackageSelection.splice(index, 1)
        }
      }
    },

    /** 点击自定义档案包卡片 */
    handleCustomPackageClick(packageCode) {
      if (!packageCode) return
      // 查找对应的档案包信息
      const pkg = this.customPackageList.find(p => p.packageCode === packageCode)
      if (!pkg) return
      
      // 设置当前选中的档案包
      this.selectedCustomPackageCode = packageCode
      this.selectedCustomPackageName = pkg.packageName
      // 加载附件列表
      this.customAttachmentQueryParams.pageNum = 1
      this.loadCustomAttachmentList()
    },

    /** 处理自定义档案包下拉菜单命令 */
    handleCustomPackageAction(command, pkg) {
      switch (command) {
        case 'package':
          // 打包
          this.openPackageDialogForCustom(pkg)
          break
        case 'edit':
          // 编辑
          this.handleEditCustomPackage(pkg)
          break
        case 'delete':
          // 删除
          this.handleDeleteCustomPackage(pkg)
          break
        default:
          break
      }
    },

    /** 打开单独打包自定义档案包弹窗 */
    async openPackageDialogForCustom(pkg) {
      if (!pkg || !pkg.packageCode) {
        this.$modal.msgWarning('请先选择档案包')
        return
      }
      
      // 直接设置打包表单并打开弹窗,验证逻辑由后端处理
      this.packageForm = {
        packageName: pkg.packageName + '_档案打包',
        fileCount: pkg.attachmentCount || 0, // 仅用于显示,实际数量由后端确定
        reportName: '',
        reportNames: [],
        reportIds: [],
        selectedIds: [],
        selectedRecords: [],
        mode: 'custom', // custom=自定义档案包打包
        customPackageCode: pkg.packageCode, // 自定义档案包编码
        customPackageName: pkg.packageName // 保存档案包名称用于显示
      }
      this.packageDialogVisible = true
    },

    /** 加载自定义档案包附件列表 */
    async loadCustomAttachmentList() {
      if (!this.selectedCustomPackageCode) {
        this.customAttachmentList = []
        this.customAttachmentTotal = 0
        return
      }
      
      this.customAttachmentLoading = true
      try {
        const params = {
          attachType: 'custom',
          relatedId: this.selectedCustomPackageCode
        }
        const response = await listAttachmentByRelated(params)
        if (response.code === 200) {
          // 后端返回的是AjaxResult，数据在data字段中
          this.customAttachmentList = (response.data || []).map(item => ({
            ...item,
            editingRemark: false,
            tempRemark: item.remark || ''
          }))
          this.customAttachmentTotal = this.customAttachmentList.length
        } else {
          this.customAttachmentList = []
          this.customAttachmentTotal = 0
        }
      } catch (error) {
        console.error('加载自定义档案包附件失败:', error)
        this.customAttachmentList = []
        this.customAttachmentTotal = 0
      } finally {
        this.customAttachmentLoading = false
      }
    },

    /** 自定义档案包附件选择变化 */
    handleCustomAttachmentSelectionChange(selection) {
      this.customAttachmentSelection = selection
    },

    /** 打开自定义档案包上传对话框 */
    openCustomUploadDialog() {
      this.uploadDialogVisible = true
      this.uploadFileList = []
      this.uploadRemark = ''
    },

    /** 批量删除自定义档案包附件 */
    async handleBatchDeleteCustomAttachments() {
      if (this.customAttachmentSelection.length === 0) {
        this.$modal.msgWarning('请选择要删除的附件')
        return
      }
      
      try {
        await this.$modal.confirm(`确定要删除选中的 ${this.customAttachmentSelection.length} 个附件吗？`)
        const ids = this.customAttachmentSelection.map(item => item.id)
        const response = await delReportAttachment(ids.join(','))
        if (response.code === 200) {
          this.$modal.msgSuccess('删除成功')
          this.loadCustomAttachmentList()
          // 刷新档案包列表（后端已自动更新统计）
          this.loadCustomPackageList()
        } else {
          this.$modal.msgError(response.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除附件失败:', error)
        }
      }
    },

    /** 删除自定义档案包单个附件 */
    async handleDeleteCustomAttachment(row) {
      try {
        await this.$modal.confirm(`确定要删除附件 "${row.originalName}" 吗？`)
        const response = await delReportAttachment(row.id)
        if (response.code === 200) {
          this.$modal.msgSuccess('删除成功')
          this.loadCustomAttachmentList()
          // 刷新档案包列表（后端已自动更新统计）
          this.loadCustomPackageList()
        } else {
          this.$modal.msgError(response.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除附件失败:', error)
        }
      }
    },

    /** 编辑自定义档案包附件备注 */
    editCustomRemark(row) {
      this.$set(row, 'editingRemark', true)
      this.$set(row, 'tempRemark', row.remark || '')
    },

    /** 取消编辑备注 */
    cancelEditCustomRemark(row) {
      this.$set(row, 'editingRemark', false)
    },

    /** 保存自定义档案包附件备注 */
    async saveCustomRemark(row) {
      try {
        const response = await updateReportAttachment({
          id: row.id,
          remark: row.tempRemark
        })
        if (response.code === 200) {
          this.$modal.msgSuccess('修改备注成功')
          row.remark = row.tempRemark
          this.$set(row, 'editingRemark', false)
        } else {
          this.$modal.msgError(response.msg || '修改失败')
        }
      } catch (error) {
        console.error('保存备注失败:', error)
        this.$modal.msgError('保存失败')
      }
    },

    /** 自定义档案包行点击 */
    handleCustomPackageRowClick(row) {
      this.currentCustomPackage = row
    },

    /** 打开混合打包对话框 */
    openHybridPackageDialog() {
      // 确保数据已加载
      if (this.packageList.length === 0) {
        this.loadPackages()
      }
      if (this.customPackageList.length === 0) {
        this.loadCustomPackageList()
      }
      
      // 重置选择
      this.hybridSelection = {
        reportNames: [],
        customPackageCodes: []
      }
      this.hybridPackageName = ''
      this.hybridPackageDialogVisible = true
    },

    /** 关闭混合打包对话框 */
    handleHybridPackageDialogClose() {
      this.hybridSelection = {
        reportNames: [],
        customPackageCodes: []
      }
      this.hybridPackageName = ''
    },

    /** 全选/取消全选报表档案包 */
    handleSelectAllReports() {
      if (this.hybridSelection.reportNames.length === this.packageList.length) {
        this.hybridSelection.reportNames = []
      } else {
        this.hybridSelection.reportNames = this.packageList.map(pkg => pkg.reportName)
      }
    },

    /** 全选/取消全选自定义档案包 */
    handleSelectAllCustomPackages() {
      if (this.hybridSelection.customPackageCodes.length === this.customPackageList.length) {
        this.hybridSelection.customPackageCodes = []
      } else {
        this.hybridSelection.customPackageCodes = this.customPackageList.map(pkg => pkg.packageCode)
      }
    },

    /** 提交混合打包 */
    async submitHybridPackage() {
      if (this.hybridSelection.reportNames.length === 0 && this.hybridSelection.customPackageCodes.length === 0) {
        this.$modal.msgWarning('请至少选择一个档案包')
        return
      }

      this.hybridPackaging = true
      try {
        const response = await createPackageByCondition({
          reportNames: this.hybridSelection.reportNames.length > 0 ? this.hybridSelection.reportNames : null,
          customPackageCodes: this.hybridSelection.customPackageCodes.length > 0 ? this.hybridSelection.customPackageCodes : null,
          packageName: this.hybridPackageName || null
        })

        if (response.code === 200) {
          this.$modal.msgSuccess('混合打包成功')
          this.hybridPackageDialogVisible = false
          
          // 提示下载
          this.$modal.confirm('是否立即下载打包文件？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '稍后',
            type: 'success'
          }).then(() => {
            this.handleDownloadPackage(response.data)
          }).catch(() => {})
        } else {
          this.$modal.msgError(response.msg || '混合打包失败')
        }
      } catch (error) {
        console.error('混合打包失败:', error)
        this.$modal.msgError('混合打包失败：' + (error.message || '网络错误'))
      } finally {
        this.hybridPackaging = false
      }
    }
  },
  computed: {
    /** 上传接口 */
    uploadAction() {
      return process.env.VUE_APP_BASE_API + '/system/report/attachment/upload'
    },
    /** 上传请求头 */
    uploadHeaders() {
      return {
        Authorization: 'Bearer ' + this.$store.getters.token
      }
    },
    /** 上传数据 */
    uploadData() {
      // 判断当前tab是否为自定义档案包
      const attachType = this.activeTab === 'custom' ? 'custom' : 'report'
      const relatedId = this.activeTab === 'custom' ? this.selectedCustomPackageCode : this.currentAttachmentRelatedId
      return {
        attachType: attachType,
        relatedId: relatedId,
        remark: this.uploadRemark
      }
    },
    /** 混合打包摘要文本 */
    hybridSummaryText() {
      const reportCount = this.hybridSelection.reportNames.length
      const customCount = this.hybridSelection.customPackageCodes.length
      const total = reportCount + customCount
      
      if (total === 0) {
        return '请选择至少一个报表档案包或自定义档案包'
      }
      
      let text = `已选择 ${total} 个档案包：`
      if (reportCount > 0) {
        text += `${reportCount}个报表档案包`
      }
      if (customCount > 0) {
        if (reportCount > 0) text += '、'
        text += `${customCount}个自定义档案包`
      }
      return text
    }
  }
}
</script>

<style scoped lang="scss">
.report-archive-page {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.archive-glass-wrapper {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 20px;
  background: #ffffff;
  color: #303133;
  border-bottom: 2px solid #e4e7ed;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 80px;
    height: 2px;
    background: #909399;
  }
}

.page-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-icon {
  width: 48px;
  height: 48px;
  background: #f5f7fa;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e4e7ed;
  
  i {
    font-size: 24px;
    color: #606266;
  }
  
  .title-icon-img {
    width: 32px;
    height: 32px;
    object-fit: contain;
  }
}

.title-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-text {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.title-subtitle {
  font-size: 13px;
  color: #909399;
}

.page-actions {
  .el-button {
    background: #f5f7fa;
    border: 1px solid #dcdfe6;
    color: #606266;
    font-weight: 500;
    
    &:hover {
      background: #e4e7ed;
      border-color: #c0c4cc;
      color: #303133;
    }
  }
}


// 文件包视图样式
.packages-block {
  padding: 20px 24px;
}

.packages-section-header {
  margin-bottom: 16px;
  
  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    padding-left: 12px;
    border-left: 3px solid #909399;
    
    i {
      font-size: 18px;
      color: #606266;
    }
    
    .el-badge {
      margin-left: 8px;
    }
  }
}

.detail-section-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
  
  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    padding-left: 12px;
    border-left: 3px solid #909399;
    
    i {
      font-size: 18px;
      color: #606266;
    }
  }
}


.packages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  min-height: 120px;
  
  @media (max-width: 1400px) {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 10px;
  }
  
  @media (max-width: 768px) {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 8px;
  }
}

.package-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  background: transparent;
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  box-shadow: none;

  &:hover {
    background: rgba(64, 158, 255, 0.04);

    .package-icon {
      transform: scale(1.1);
    }
    
    .package-name {
      color: #409eff;
    }
  }

  &.package-card-active {
    background: rgba(64, 158, 255, 0.08);
    
    .package-icon {
      transform: scale(1.05);
    }
    
    .package-name {
      color: #409eff;
      font-weight: 600;
    }
  }
  
  &.package-card-selected {
    border-color: #409eff;
    background: #287dde;
  }
}

.package-icon-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 6px;
}

.package-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
  background: transparent;

  .icon-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

// 自定义档案包图片图标
.package-icon-img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  transition: transform 0.3s ease;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.08));
  
  &:hover {
    transform: scale(1.1);
  }
}

.package-content {
  width: 100%;
  text-align: center;
}

.package-name {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
  margin-top: 4px;
  text-align: center;
  transition: color 0.2s ease;
}

.package-checkbox-wrapper {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none; /* 默认不拦截点击事件 */
}

.package-card:hover .package-checkbox-wrapper,
.package-card-selected .package-checkbox-wrapper {
  opacity: 1;
  pointer-events: auto; /* 悬停或选中时才拦截点击事件 */
}

.package-checkbox-wrapper .el-checkbox {
  pointer-events: auto; /* checkbox本身可以点击 */
}

.packages-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  
  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
  .toolbar-tip {
    font-size: 13px;
    color: #606266;
    
    strong {
      color: #303133;
      font-weight: 600;
    }
  }
  
  .toolbar-right {
    .el-button-group {
      .el-button {
        &:first-child {
          border-top-left-radius: 4px;
          border-bottom-left-radius: 4px;
        }
        &:last-child {
          border-top-right-radius: 4px;
          border-bottom-right-radius: 4px;
        }
      }
    }
  }
}

.selected-packages-list {
  max-height: 150px;
  overflow-y: auto;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.selected-records-list {
  max-height: 300px;
  overflow-y: auto;
}

.package-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 6px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;

  .el-button {
    background: rgba(255, 255, 255, 0.95);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    &.el-button--success {
      border-color: #67c23a;
      color: #67c23a;

      &:hover {
        background: #67c23a;
        color: #fff;
      }
    }
    
    &.el-button--danger {
      border-color: #f56c6c;
      color: #f56c6c;

      &:hover {
        background: #f56c6c;
        color: #fff;
      }
    }
  }
  
  // 更多操作按钮样式
  .more-btn {
    border: px solid #a0cfff;
    color: #409eff;
    background: rgba(255, 255, 255, 0.98);
    transition: all 0.3s ease;
    
    &:hover {
      background: #409eff;
      border-color: #409eff;
      color: #fff;
      transform: scale(1.05);
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    }
    
    &:active {
      transform: scale(0.95);
    }
  }
}

.package-card:hover .package-actions {
  opacity: 1;
}

// 新增档案包卡片样式
.package-card-add {
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: rgba(64, 158, 255, 0.04);
    
    .add-icon-img {
      transform: scale(1.1);
    }
    
    .add-text {
      color: #409eff;
    }
  }
  
  .add-icon-wrapper {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 8px;
    height: 56px;
    
    .add-icon-img {
      width: 56px;
      height: 56px;
      object-fit: contain;
      transition: transform 0.2s ease;
    }
  }
  
  .add-text {
    font-size: 13px;
    font-weight: 500;
    color: #606266;
    transition: color 0.2s ease;
  }
}

.empty-hint {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  opacity: 0.5;
  object-fit: contain;
}

.empty-text {
  font-size: 14px;
  margin: 0;
}

// 详情视图样式
.detail-block {
  margin-top: 24px;
  padding: 20px 24px;
  border-top: 3px solid #e4e7ed;
  width: 100%;
  overflow-x: auto;
  min-height: 200px;
  display: block !important;
  background: #fafafa;
}

// 抽屉表单样式
.drawer-form {
  padding: 20px;
}

.drawer-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 12px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.selected-info {
  font-size: 12px;
  color: #909399;
}

.selected-info strong {
  color: #303133;
  font-weight: 600;
}

/* 按钮白底样式 */
.btn-plain-white {
  background: #fff !important;
  border: 1px solid #dcdfe6 !important;
  color: #606266 !important;
  
  &:hover {
    background: #f5f7fa !important;
    border-color: #c0c4cc !important;
  }
  
  &[disabled] {
    color: #c0c4cc !important;
    background: #f5f7fa !important;
    border-color: #e4e7ed !important;
  }
}

/* 类型列样式 */
.type-link {
  cursor: pointer;
  font-size: 13px;
  padding: 0 4px;
}

.type-text-single {
  font-size: 13px;
  color: #606266;
}

/* 合并行淡色背景 */
:deep(.merged-row) {
  background-color: #f8fafc;
}

.merged-student-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.merged-student-tag {
  margin: 0;
}

.merged-empty {
  color: #909399;
  text-align: center;
  padding: 20px 0;
}

.merged-popover-content {
  max-height: 260px;
  overflow-y: auto;
}

.merged-popover-title {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 6px;
  color: #606266;
}

.merged-popover-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

// 表格样式优化
.detail-table {
  font-size: 13px;
  width: 100%;

  :deep(.el-table__header-wrapper) {
    width: 100% !important;
  }

  :deep(.el-table__body-wrapper) {
    width: 100% !important;
  }

  :deep(.el-table__header) {
    width: 100% !important;

    th {
      background-color: #f5f7fa;
      color: #606266;
      font-weight: 600;
      padding: 12px 0;
      text-align: center;
    }
  }

  :deep(.el-table__body) {
    width: 100% !important;

    td {
      padding: 12px 0;
      text-align: center;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  :deep(.el-table__row) {
    &:hover {
      background-color: #f5f7fa;
    }
  }

  :deep(.el-table__cell) {
    padding: 0 8px;
  }

  // 移除固定列后的空白列
  :deep(.el-table__fixed-right) {
    right: 0 !important;
  }

  :deep(.el-table__fixed-right-patch) {
    display: none;
  }

  :deep(.el-button--mini) {
    padding: 7px 12px;
    font-size: 12px;
  }

  // Switch样式
  .status-switch-wrapper {
    display: inline-block;
    white-space: nowrap;
    width: 100%;
    text-align: center;
  }

  :deep(.status-switch-wrapper .el-switch) {
    white-space: nowrap !important;
    display: inline-block;

    .el-switch__core {
      white-space: nowrap;
    }

    .el-switch__label {
      font-size: 12px;
      white-space: nowrap !important;
      display: inline-block;
    }

    .el-switch__label--left,
    .el-switch__label--right {
      white-space: nowrap !important;
      display: inline-block;
    }
  }
}

// 报表名称单元格样式
.report-name-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  .pdf-icon {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
    vertical-align: middle;
  }

  span {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

// 操作栏样式
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  white-space: nowrap;

  .el-link {
    font-size: 13px;
    padding: 0 4px;

    i {
      margin-right: 4px;
    }
  }

  .action-divider {
    color: #dcdfe6;
    font-size: 12px;
    margin: 0 2px;
  }
}

// 高级查询折叠面板样式(右侧固定)
.query-panel {
  position: fixed;
  right: 0;
  z-index: 999;
  transition: box-shadow 0.3s ease;

  .query-panel-toggle {
    position: absolute;
    left: -44px;
    top: 0;
    width: 44px;
    height: 110px;
    background: #409eff;
    color: #fff;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: grab;
    border-radius: 10px 0 0 10px;
    box-shadow: -3px 0 12px rgba(64, 158, 255, 0.3);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
    touch-action: none;
    gap: 8px;

    i {
      font-size: 18px;
      transition: transform 0.2s ease;
    }

    span {
      font-size: 12px;
      writing-mode: vertical-lr;
      text-orientation: upright;
      letter-spacing: 2px;
      transition: opacity 0.2s ease;
    }

    .drag-indicator {
      position: absolute;
      bottom: 6px;
      left: 50%;
      transform: translateX(-50%);
      font-size: 14px;
      opacity: 0.6;
      transition: opacity 0.2s ease;

      i {
        font-size: 14px;
        transform: rotate(90deg);
      }
    }

    &:hover {
      background: #66b1ff;
      transform: translateX(-6px);
      box-shadow: -6px 0 20px rgba(64, 158, 255, 0.4);

      .drag-indicator {
        opacity: 1;
      }
    }

    &:active {
      cursor: grabbing;
    }

    &.dragging {
      cursor: grabbing;
      background: #337ecc;
      box-shadow: -6px 0 20px rgba(64, 158, 255, 0.4);

      i {
        transform: scale(1.1);
      }

      .drag-indicator {
        opacity: 1;
        animation: pulse 0.8s infinite;
      }
    }
  }

  @keyframes pulse {
    0%, 100% {
      opacity: 1;
      transform: translateX(-50%) scale(1);
    }
    50% {
      opacity: 0.7;
      transform: translateX(-50%) scale(1.1);
    }
  }

  .query-panel-content {
    width: 420px;
    background: #fff;
    box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
    border-radius: 8px 0 0 8px;
    overflow: hidden;
    transform: translateX(100%);
    transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    will-change: transform;
    border-left: 3px solid #409eff;
  }

  &.query-panel-expanded .query-panel-content {
    transform: translateX(0);
    box-shadow: -4px 0 16px rgba(0, 0, 0, 0.2);
  }

  .query-panel-header {
    padding: 20px 24px;
    background: #409eff;
    border-bottom: none;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .query-panel-title {
      font-size: 16px;
      font-weight: 600;
      color: #ffffff;
      display: flex;
      align-items: center;
      gap: 8px;
      
      &::before {
        content: '';
        display: inline-block;
        width: 4px;
        height: 16px;
        background: #ffffff;
        border-radius: 2px;
      }
    }
  }

  .query-panel-body {
    padding: 24px;
    max-height: calc(100vh - 200px);
    overflow-y: auto;
    
    .el-form {
      .el-form-item {
        margin-bottom: 20px;
      }
      
      .el-button {
        min-width: 90px;
      }
    }
  }
}

// 查询切换按钮样式（页头）
.query-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  background: #409EFF;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;

  i {
    margin-right: 6px;
    font-size: 16px;
  }

  &:hover {
    background: #66b1ff;
  }

  &.query-toggle-expanded {
    background: #67c23a;

    &:hover {
      background: #85ce61;
    }
  }
}

// 附件管理样式
.dialog-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  
  i {
    font-size: 18px;
    color: #409eff;
  }
}

.attachment-dialog-content {
  min-height: 300px;
}

.attachment-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  
  .attachment-count {
    margin-left: auto;
    font-size: 14px;
    color: #606266;
    
    strong {
      color: #409eff;
      font-size: 16px;
    }
  }
}

.attachment-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
  
  p {
    margin-top: 16px;
    font-size: 14px;
  }
}

// 上传组件样式
.el-upload {
  width: 100%;
}

.el-upload-dragger {
  width: 100%;
}

// Tab标签页样式
.archive-tabs {
  padding-left: 24px; // 直接在tabs容器上增加左边距
  
  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 24px;
    background: #fafafa;
  }
  
  :deep(.el-tabs__nav-wrap) {
    padding: 8px 0;
  }
  
  :deep(.el-tabs__nav) {
    transform: translateX(20px); // 使用transform强制移动
  }
  
  :deep(.el-tabs__item) {
    font-size: 14px;
    font-weight: 500;
    height: 40px;
    line-height: 40px;
    
    i {
      margin-right: 6px;
    }
  }
  
  :deep(.el-tabs__content) {
    padding: 0;
  }
}

// 自定义档案包样式
.custom-package-container {
  padding: 24px;
}

.custom-toolbar {
  margin-bottom: 16px;
  padding: 12px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  display: flex;
  gap: 12px;
}

.custom-package-table {
  background: #ffffff;
  
  :deep(.el-table__header) {
    th {
      background-color: #f5f7fa;
      color: #606266;
      font-weight: 600;
    }
  }
  
  :deep(.el-table__body) {
    .el-button--text {
      padding: 0;
      margin: 0 8px;
    }
  }
}

// 自定义档案包卡片样式
.custom-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  
  i {
    color: #ffffff;
    font-size: 28px;
  }
}

.package-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    
    i {
      font-size: 14px;
    }
  }
}

.package-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 混合打包样式
.hybrid-package-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hybrid-section {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  background: #fafafa;
}

.hybrid-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;
  
  h4 {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}

.hybrid-list {
  max-height: 300px;
  overflow-y: auto;
  background: #ffffff;
  border-radius: 6px;
  padding: 12px;
}

.hybrid-checkbox {
  display: block;
  width: 100%;
  padding: 10px 12px;
  margin: 0 0 8px 0;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #ffffff;
  transition: all 0.3s;
  
  &:hover {
    background: #f5f7fa;
    border-color: #409eff;
  }
  
  :deep(.el-checkbox__label) {
    width: 100%;
  }
}

.package-info {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  
  i {
    font-size: 18px;
    color: #409eff;
  }
  
  .package-name {
    flex: 1;
    font-size: 14px;
    color: #303133;
    font-weight: 500;
  }
  
  .el-tag {
    flex-shrink: 0;
  }
}

.empty-hint {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  
  i {
    font-size: 48px;
    color: #dcdfe6;
    margin-bottom: 12px;
  }
  
  p {
    margin: 0;
    font-size: 14px;
  }
}

.hybrid-summary {
  margin-top: 8px;
}
</style>
