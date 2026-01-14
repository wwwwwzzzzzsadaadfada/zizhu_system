<template>
  <div class="report-preview-page">
    <div class="report-preview-container">
      <!-- 左侧报表树 -->
      <div class="report-tree-pane">
        <div class="tree-header">
          <div class="header-content">
            <i class="el-icon-folder-opened header-icon"></i>
            <div class="header-text">
              <span class="title">报表分类</span>
            </div>
          </div>
          <el-tooltip content="报表排序管理" placement="top">
            <el-button
              type="text"
              icon="el-icon-s-operation"
              @click="openReportManage"
              class="manage-btn"
            />
          </el-tooltip>
        </div>
        <el-tree
          v-loading="reportTreeLoading"
          :data="reportTreeData"
          node-key="id"
          :props="{ label: 'label', children: 'children' }"
          :highlight-current="true"
          :expand-on-click-node="false"
          :default-expand-all="true"
          class="report-tree"
          @node-click="handleReportTreeClick"
        >
          <span slot-scope="{ data }" class="custom-tree-node">
            <span class="node-main">
              <img :src="data.children && data.children.length ? folderIcon : fileIcon" alt="报表" class="tree-icon" />
              <span class="node-label">{{ data.label }}</span>
            </span>
            <el-tag
              v-if="data.count !== undefined"
              size="mini"
              type="info"
              effect="plain"
              class="node-count"
            >{{ data.count }}</el-tag>
          </span>
        </el-tree>
      </div>

      <!-- 右侧主体：搜索栏 + 预览 -->
      <div class="report-main-pane">
        <div class="preview-glass-wrapper">
          <!-- 搜索区域 -->
          <div class="search-block">
            <div class="search-header">
              <i class="el-icon-search"></i>
              <span>搜索条件</span>
            </div>
            <el-form :inline="true" :model="queryForm" class="search-form">
              <el-form-item label="学生姓名" class="form-item-student">
                <el-autocomplete
                  v-model="queryForm.studentName"
                  :fetch-suggestions="queryStudents"
                  placeholder="请输入学生姓名"
                  :trigger-on-focus="true"
                  @select="handleStudentSelect"
                  prefix-icon="el-icon-user"
                  clearable
                  class="student-autocomplete"
                >
                  <template slot-scope="{ item }">
                    <div class="student-item">
                      <span class="student-name">{{ item.name }}</span>
                      <span class="student-info">{{ item.gradeName }}</span>
                    </div>
                  </template>
                </el-autocomplete>
              </el-form-item>
              <el-form-item label="报表名称" class="form-item-report">
                <el-input
                  v-model="currentReportName"
                  placeholder="请在左侧报表树中选择"
                  prefix-icon="el-icon-document"
                  disabled
                  class="report-input"
                />
              </el-form-item>
            </el-form>
            <div class="search-actions">
              <el-dropdown 
                split-button 
                class="white-btn" 
                @click="previewReport" 
                @command="handlePreviewCommand" 
                :disabled="!canPreview"
              >
                <i class="el-icon-view" style="color: #606266;"></i> 预览报表
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="current">
                    <i class="el-icon-monitor"></i> 当前页面预览
                  </el-dropdown-item>
                  <el-dropdown-item command="newWindow">
                    <i class="el-icon-link"></i> 新窗口预览
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <el-dropdown 
                split-button 
                class="white-btn" 
                @click="generateAndSavePdf" 
                @command="handleArchiveCommand" 
                :disabled="!canPreview"
                :loading="pdfGenerating"
              >
                <i class="el-icon-download" style="color: #606266;"></i> 报表归档
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="singlePdf" :disabled="!canPreview">
                    <i class="el-icon-download"></i> PDF归档
                  </el-dropdown-item>
                  <el-dropdown-item command="singleExcel" :disabled="!canPreview">
                    <i class="el-icon-document"></i> Excel归档
                  </el-dropdown-item>
                  <el-dropdown-item command="batch" :disabled="!queryForm.reportId">
                    <i class="el-icon-document-copy"></i> 批量归档
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <el-button icon="el-icon-refresh" @click="resetQuery">
                重置
              </el-button>
            </div>
          </div>

          <!-- 报表展示区域 -->
          <div class="report-display-area">
            <div v-if="!reportUrl" class="empty-state">
              <div class="empty-content">
                <img :src="emptyIcon" alt="空状态" class="empty-icon" />
                <p class="empty-text">{{ emptyStateText }}</p>
                <p class="empty-hint">{{ emptyStateHint }}</p>
              </div>
            </div>
            <i-frame
              v-else
              ref="reportIframe"
              :src="reportUrl"
              class="report-iframe"
              @iframe-load="onReportLoad"
            ></i-frame>
          </div>
        </div>
      </div>
    </div>

    <!-- 报表排序管理弹窗 -->
    <el-dialog
      title="报表排序管理"
      :visible.sync="sortDialogVisible"
      width="800px"
      :close-on-click-modal="false"
      class="sort-dialog"
    >
      <!-- 提示信息 -->
      <el-alert
        title="拖动报表可调整显示顺序，调整后请点击【保存排序】按钮"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px; background: #f5f5f5; border: 1px solid #e0e0e0; color: #666;"
      />

      <!-- 学段筛选 -->
      <div class="filter-bar" style="margin-bottom: 20px;">
        <el-radio-group v-model="sortPlanId" @change="loadSortReports" size="medium">
          <el-radio-button :label="3">高中</el-radio-button>
          <el-radio-button label="义教">义教</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 报表列表（可拖拽） -->
      <div class="sort-report-list" v-loading="sortLoading">
        <el-empty v-if="sortReportList.length === 0" description="暂无报表数据" :image-size="80"></el-empty>
        
        <draggable
          v-else
          v-model="sortReportList"
          handle=".drag-handle"
          animation="300"
          @end="onSortDragEnd"
          class="draggable-list"
        >
          <transition-group type="transition" name="flip-list">
            <div
              v-for="(report, index) in sortReportList"
              :key="report.id"
              class="sort-report-item"
            >
              <div class="report-item-content">
                <div class="drag-handle">
                  <i class="el-icon-rank"></i>
                </div>
                <div class="report-index">{{ index + 1 }}</div>
                <div class="report-info">
                  <div class="report-name">{{ report.name }}</div>
                  <div class="report-meta">
                    <el-tag size="mini" type="info">ID: {{ report.id }}</el-tag>
                    <el-tag size="mini" type="success" v-if="report.schoolingPlanName">
                      {{ report.schoolingPlanName }}
                    </el-tag>
                    <el-tag size="mini" type="warning">
                      排序号: {{ report.sortOrder }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </transition-group>
        </draggable>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="sortDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="sortSaving" @click="saveSortOrder">保存排序</el-button>
      </div>
    </el-dialog>

    <!-- 批量导出PDF弹窗 -->
    <el-dialog
      title="批量导出PDF"
      :visible.sync="batchDialogVisible"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form label-width="90px" :model="batchForm">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报表">
              <el-input v-model="currentReportName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学段">
              <el-select v-model="batchForm.schoolingPlanId" placeholder="可选，选择学段" @change="onSchoolingPlanChange" style="width: 100%;">
                <el-option
                  v-for="plan in schoolingPlanOptions"
                  :key="plan.value"
                  :label="plan.label"
                  :value="plan.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学生姓名">
              <el-input v-model="batchForm.studentName" placeholder="可选，支持模糊" @input="handleStudentNameSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学年学期">
              <el-input v-model="batchForm.yearSemesterId" placeholder="可选，留空表示当前" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="批次名称">
          <el-input v-model="batchForm.batchName" placeholder="可选，不填将自动生成" />
        </el-form-item>
        <el-form-item label="学生选择">
          <div class="student-table-container">
            <div class="student-table-header">
              <div class="search-wrapper">
                <el-input
                  v-model="studentSearchKeyword"
                  placeholder="搜索学生姓名"
                  prefix-icon="el-icon-search"
                  clearable
                  @input="handleStudentTableSearch"
                  class="student-search-input"
                />
              </div>
              <div class="action-wrapper">
                <span class="selected-count">
                  <i class="el-icon-check"></i>
                  已选择 <strong>{{ batchForm.studentIds.length }}</strong> 人
                </span>
                <el-button size="small" type="text" @click="handleSelectAll">
                  <i class="el-icon-check"></i> 全选
                </el-button>
                <el-button size="small" type="text" @click="handleClearSelection">
                  <i class="el-icon-close"></i> 清空
                </el-button>
              </div>
            </div>
            <div class="table-wrapper">
              <el-table
                ref="studentTable"
                :data="studentTableData"
                height="320"
                @selection-change="handleStudentSelectionChange"
                v-loading="studentLoading"
                :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
                :row-style="{ height: '48px' }"
                class="student-table"
              >
                <el-table-column type="selection" width="60" align="center" :reserve-selection="true" />
                <el-table-column prop="name" label="姓名" min-width="120" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <span class="student-name">{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="gradeClass" label="年级/班级" min-width="200" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <span class="grade-class">{{ scope.row.gradeClass || '未分配' }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="student-table-pagination">
              <el-pagination
                @size-change="handleStudentPageSizeChange"
                @current-change="handleStudentPageChange"
                :current-page="studentTablePage.pageNum"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="studentTablePage.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="studentTablePage.total"
                background
              />
            </div>
            <div class="help-text">
              <i class="el-icon-info"></i>
              提示：不选择学生则按学段+姓名条件批量生成
            </div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="batchDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="batchGenerating" @click="submitBatchGenerate">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listStudentsBase, getSchoolPlanList } from "@/api/system/studentRecord";
import { listReport, listReportTree, checkReportType } from "@/api/system/report";
import { generatePdf, downloadPdf, batchGeneratePdf, listReportPdf, mergePdfs, mergePdfsByCondition, downloadBatchPdf, listReportPdfBatch } from "@/api/system/reportPdf";
import { generateExcel, downloadExcel } from "@/api/system/reportExcel";
import iFrame from "@/components/iFrame/index";
import draggable from 'vuedraggable';
import { updateReportSort } from '@/api/system/report';

export default {
  name: "ReportPreview",
  components: {
    iFrame,
    draggable
  },
  data() {
    return {
      // 查询表单
      queryForm: {
        studentName: '',
        studentId: null,
        reportId: null
      },
      // 选中的学生信息
      selectedStudent: null,
      // 报表列表
      reportList: [],
      // 报表URL
      reportUrl: '',
      // 加载状态
      loading: false,
      // PDF生成状态
      pdfGenerating: false,
      // 当前学年学期ID（用于PDF生成）
      currentYearSemesterId: null,
      // 批量导出弹窗
      batchDialogVisible: false,
      batchGenerating: false,
      batchForm: {
        studentIds: [],
        schoolingPlanId: '',
        studentName: '',
        yearSemesterId: '',
        batchName: ''
      },
      schoolingPlanOptions: [],
      studentOptions: [],
      studentLoading: false,
      // 学生表格相关
      studentTableData: [],
      studentSearchKeyword: '',
      studentTablePage: {
        pageNum: 1,
        pageSize: 20,
        total: 0
      },
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
      // 合并结果列表
      batchList: [],
      batchLoading: false,
      batchPage: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      // 报表树
      reportTreeLoading: false,
      reportTreeData: [],
      folderIcon: require('@/assets/images/zb/wj.png'),
      fileIcon: require('@/assets/images/zb/xlsx.png'),
      emptyIcon: require('@/assets/images/zb/kzt.png'),
      // 当前选中的报表信息（后端返回）
      currentReportInfo: null,
      // 排序弹窗相关
      sortDialogVisible: false,
      sortLoading: false,
      sortSaving: false,
      sortPlanId: 3, // 默认高中
      sortReportList: [],
      originalSortReportList: []
    }
  },
  computed: {
    canPreview() {
      // 使用后端返回的 needStudent 字段判断
      if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
        // 统计报表：只需要报表ID
        return !!this.queryForm.reportId
      }
      // 学生报表：需要学生ID和报表ID
      return this.queryForm.studentId && this.queryForm.reportId
    },
    currentReportName() {
      const target = this.reportList.find(item => item.id === this.queryForm.reportId)
      return target ? target.name : ''
    },
    currentMergeReportName() {
      if (!this.mergeForm.reportId) return ''
      const report = this.availableReports.find(r => r.id === this.mergeForm.reportId) ||
                     this.reportList.find(r => r.id === this.mergeForm.reportId)
      return report ? report.name : ''
    },
    // 空状态提示文字
    emptyStateText() {
      if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
        return '请选择统计报表后点击预览'
      }
      // 检查是否已选择学生或报表
      if (this.queryForm.studentId && !this.queryForm.reportId) {
        return `已选择学生：${this.queryForm.studentName || ''}，请在左侧选择报表`
      }
      if (!this.queryForm.studentId && this.queryForm.reportId) {
        return '请输入学生姓名并选择'
      }
      return '请选择学生和报表'
    },
    // 空状态提示详情
    emptyStateHint() {
      if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
        return '左侧选择统计报表模板，无需选择学生'
      }
      if (this.queryForm.studentId && !this.queryForm.reportId) {
        return '👈 请在左侧报表树中点击选择报表模板，即可查看该学生的报表'
      }
      if (!this.queryForm.studentId && this.queryForm.reportId) {
        return '已选择报表，请上方输入学生姓名并选择'
      }
      return '可以先选学生再选报表，也可以先选报表再选学生'
    }
  },
  mounted() {
    this.loadReportList()
    this.loadReportTree()
    this.loadAvailableReports()
    this.loadBatchList()
  },
  methods: {
    /** 
     * 查询学生（根据姓名）
     * 
     * 功能说明：
     * 1. 根据当前报表的学段自动过滤学生
     * 2. 支持按学生姓名模糊查询
     * 
     * 安全设计：
     * - 前端只负责传递 schoolingPlanId 参数
     * - 所有过滤逻辑由后端MyBatis处理
     * - 后端保证数据安全性，前端无法绕过
     */
    queryStudents(queryString, cb) {
      this.loading = true

      // 构建查询参数，逻辑由后端处理
      const params = {
        pageNum: 1,
        pageSize: 10
      }

      // 如果选择了报表，且报表有学段信息，则按学段过滤学生
      if (this.currentReportInfo && this.currentReportInfo.schoolingPlanId) {
        params.schoolingPlanId = this.currentReportInfo.schoolingPlanId
      }

      // 如果有输入文字，传递给后端进行模糊查询
      if (queryString && queryString.trim() !== '') {
        params.name = queryString.trim()
      }

      listStudentsBase(params).then(response => {
        this.loading = false
        if (response.code === 200 && response.rows) {
          const students = response.rows.map(item => {
            // 处理年级信息，兼容不同的字段名
            const gradeName = item.gradeName || item.grade_name || ''

            return {
              value: item.name,
              name: item.name,
              studentId: item.id,
              gradeName: gradeName, // 只返回年级名称
              schoolingPlanId: item.schoolingPlanId || item.schooling_plan_id || null
            }
          })
          cb(students)
        } else {
          cb([])
        }
      }).catch(() => {
        this.loading = false
        cb([])
      })
    },

    /** 选择学生 */
    handleStudentSelect(item) {
      this.queryForm.studentId = item.studentId
      this.selectedStudent = item

      // 根据学生ID重新加载报表列表（后端会根据学生学制自动过滤）
      // 安全：所有过滤逻辑在后端完成，前端只传递学生ID
      this.loadReportList(item.studentId)

      // 如果已经选择了报表，检查是否需要自动预览
      if (this.queryForm.reportId && this.currentReportInfo) {
        // 如果当前报表需要学生，则自动预览
        if (this.currentReportInfo.needStudent !== false) {
          this.$nextTick(() => {
            this.previewReport()
          })
        }
      }
    },

    /** 报表选择改变 */
    handleReportChange(value) {
      if (value && this.queryForm.studentId) {
        this.$nextTick(() => {
          this.previewReport()
        })
      } else {
        this.reportUrl = ''
      }
    },

    /** 加载报表树（后端已按学段分组） */
    loadReportTree() {
      this.reportTreeLoading = true
      const params = {}
      if (this.queryForm.studentId) {
        params.studentId = this.queryForm.studentId
      }
      listReportTree(params).then(res => {
        if (res.code === 200) {
          this.reportTreeData = res.data || res
        } else {
          this.reportTreeData = []
        }
      }).catch(err => {
        console.error('加载报表树失败:', err)
        this.reportTreeData = []
      }).finally(() => {
        this.reportTreeLoading = false
      })
    },

    /** 报表树节点点击 */
    handleReportTreeClick(node) {
      // 仅在点击叶子节点（具体报表）时处理
      if (!node || !node.id || node.children) {
        return
      }
      this.queryForm.reportId = node.id
      
      // 调用后端接口获取报表信息（包含 needStudent 字段）
      checkReportType(node.id).then(res => {
        if (res.code === 200) {
          this.currentReportInfo = res.data
          
          // 根据后端返回的 needStudent 字段决定是否需要学生
          if (this.currentReportInfo.needStudent === false) {
            // 统计报表：直接预览
            this.$nextTick(() => {
              this.previewReport()
            })
          } else if (this.queryForm.studentId) {
            // 学生报表且已选择学生：自动预览
            this.$nextTick(() => {
              this.previewReport()
            })
          }
          // 如果是学生报表但未选择学生，不做任何操作，等待用户选择学生
        }
      }).catch(err => {
        console.error('获取报表信息失败:', err)
      })
    },

    /** 打开报表排序管理弹窗 */
    openReportManage() {
      this.sortDialogVisible = true
      this.sortPlanId = 3 // 默认高中
      // 加载报表列表
      this.loadSortReports()
    },

    /** 加载排序报表列表 */
    loadSortReports() {
      this.sortLoading = true
      const params = {}
      
      // 处理学段筛选
      if (this.sortPlanId === '义教') {
        // 义教：小学(1) + 初中(2)，需要查询两次并合并
        this.loadCompulsoryEducationReports()
        return
      } else if (this.sortPlanId) {
        params.schoolingPlanId = this.sortPlanId
      }

      listReport(params).then(res => {
        if (res.code === 200 && res.data) {
          // 按 sortOrder 排序
          this.sortReportList = res.data.map(item => ({
            id: item.id,
            name: item.name,
            code: item.code,
            sortOrder: item.sortOrder || 0,
            schoolingPlanId: item.schoolingPlanId,
            schoolingPlanName: this.getSchoolingPlanName(item.schoolingPlanId)
          })).sort((a, b) => a.sortOrder - b.sortOrder)

          // 保存原始顺序
          this.originalSortReportList = JSON.parse(JSON.stringify(this.sortReportList))
        } else {
          this.sortReportList = []
        }
      }).catch(err => {
        console.error('加载报表列表失败:', err)
        this.$modal.msgError('加载报表列表失败')
        this.sortReportList = []
      }).finally(() => {
        this.sortLoading = false
      })
    },

    /** 加载义教报表（小学+初中） */
    async loadCompulsoryEducationReports() {
      try {
        // 并行查询小学和初中报表
        const [primaryRes, juniorRes] = await Promise.all([
          listReport({ schoolingPlanId: 1 }), // 小学
          listReport({ schoolingPlanId: 2 })  // 初中
        ])

        let reports = []
        if (primaryRes.code === 200 && primaryRes.data) {
          reports = reports.concat(primaryRes.data)
        }
        if (juniorRes.code === 200 && juniorRes.data) {
          reports = reports.concat(juniorRes.data)
        }

        // 按 sortOrder 排序
        this.sortReportList = reports.map(item => ({
          id: item.id,
          name: item.name,
          code: item.code,
          sortOrder: item.sortOrder || 0,
          schoolingPlanId: item.schoolingPlanId,
          schoolingPlanName: this.getSchoolingPlanName(item.schoolingPlanId)
        })).sort((a, b) => a.sortOrder - b.sortOrder)

        // 保存原始顺序
        this.originalSortReportList = JSON.parse(JSON.stringify(this.sortReportList))
      } catch (err) {
        console.error('加载义教报表失败:', err)
        this.$modal.msgError('加载义教报表失败')
        this.sortReportList = []
      } finally {
        this.sortLoading = false
      }
    },

    /** 获取学段名称 */
    getSchoolingPlanName(planId) {
      if (!planId) return '通用'
      const nameMap = {
        1: '小学',
        2: '初中',
        3: '高中'
      }
      return nameMap[planId] || '未知学段'
    },

    /** 拖拽结束 */
    onSortDragEnd() {
      // 重新计算排序号（间隔10）
      this.sortReportList.forEach((report, index) => {
        report.sortOrder = (index + 1) * 10
      })
    },

    /** 保存排序 */
    saveSortOrder() {
      // 检查是否有变化
      const hasChanges = this.sortReportList.some((report, index) => {
        return report.id !== this.originalSortReportList[index]?.id
      })

      if (!hasChanges) {
        this.$modal.msgWarning('排序未发生变化')
        return
      }

      this.sortSaving = true
      // 构造排序数据
      const sortData = this.sortReportList.map((report, index) => ({
        id: report.id,
        sortOrder: (index + 1) * 10
      }))

      updateReportSort(sortData).then(res => {
        if (res.code === 200) {
          this.$modal.msgSuccess('保存成功')
          // 关闭弹窗
          this.sortDialogVisible = false
          // 刷新报表树
          this.loadReportTree()
        } else {
          this.$modal.msgError(res.msg || '保存失败')
        }
      }).catch(err => {
        console.error('保存排序失败:', err)
        this.$modal.msgError('保存排序失败')
      }).finally(() => {
        this.sortSaving = false
      })
    },

    /** 加载报表列表
     * @param {Number} studentId 学生ID（可选）
     *                           如果提供，后端会根据学生学制自动过滤报表
     *                           如果为null，返回所有报表
     */
    loadReportList(studentId = null) {
      this.loading = true
      // 构建查询参数
      // 安全：只传递学生ID，后端会查询学生学制并过滤报表
      const params = {}
      if (studentId != null) {
        params.studentId = studentId
        // 额外传递学段，便于后端按学段限制可生成的报表
        if (this.selectedStudent && this.selectedStudent.schoolingPlanId) {
          params.schoolingPlanId = this.selectedStudent.schoolingPlanId
        }
      }

      // 通过后端API获取报表列表
      listReport(params).then(response => {
        this.loading = false
        console.log('报表列表API响应:', response)

        if (response.code === 200) {
          if (response.data && Array.isArray(response.data) && response.data.length > 0) {
            this.reportList = response.data.map(item => ({
              id: item.id,
              name: item.name,
              code: item.code,
              schoolingPlanId: item.schoolingPlanId || null,
              reportType: item.reportType || 'student' // 默认为学生报表
            }))
            console.log('成功加载报表列表:', this.reportList)

            // 如果根据学生过滤后没有报表，给出提示
            if (studentId != null && this.reportList.length === 0) {
              this.$modal.msgWarning('该学生学段暂无适用的报表模板')
            }
          } else {
            console.warn('报表列表为空')
            if (studentId != null) {
              // 有学生时，不再回退默认报表，保持为空，避免跨学段显示
              this.reportList = []
              this.$modal.msgWarning('该学生学段暂无适用的报表模板')
            } else {
              this.$modal.msgWarning('未查询到报表模板，使用默认报表')
            this.loadDefaultReportList()
            }
          }
        } else {
          console.error('报表列表API返回错误:', response.msg || '未知错误')
          this.$modal.msgError(response.msg || '获取报表列表失败')
          // 仅在未选择学生时回退默认列表
          if (studentId == null) {
          this.loadDefaultReportList()
          } else {
            this.reportList = []
          }
        }
      }).catch(error => {
        this.loading = false
        console.error('报表列表API调用失败:', error)
        this.$modal.msgError('获取报表列表失败: ' + (error.message || '网络错误'))
        // 仅在未选择学生时回退默认列表
        if (studentId == null) {
        this.loadDefaultReportList()
        } else {
          this.reportList = []
        }
      })
    },

    /** 加载默认报表列表（备用方案） */
    loadDefaultReportList() {
      // 这里可以设置一些默认的报表ID
      // 或者从系统配置中获取
      this.reportList = [
        {
          id: '1159821341794144256',
          name: '普通高中国家助学金申请表',
          code: 'student_aid_application'
        }
      ]
    },

    /** 预览报表 */
    previewReport() {
      if (!this.canPreview) {
        if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
          this.$modal.msgWarning('请选择报表')
        } else {
          this.$modal.msgWarning('请选择学生和报表')
        }
        return
      }

      const baseUrl = process.env.NODE_ENV === 'development'
        ? 'http://localhost:8080'
        : (process.env.VUE_APP_BASE_API || window.location.origin)

      // 根据后端返回的 needStudent 字段构建URL
      if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
        // 统计报表：无需studentId参数
        this.reportUrl = `${baseUrl}/jmreport/view/${this.queryForm.reportId}`
      } else {
        // 学生报表：需要studentId参数，并且开启脱敏模式
        this.reportUrl = `${baseUrl}/jmreport/view/${this.queryForm.reportId}?studentId=${this.queryForm.studentId}&desensitize=true`
        console.log('📋 预览URL:', this.reportUrl)
      }
    },

    /** 处理预览下拉菜单命令 */
    handlePreviewCommand(command) {
      if (!this.canPreview) {
        if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
          this.$modal.msgWarning('请选择报表')
        } else {
          this.$modal.msgWarning('请选择学生和报表')
        }
        return
      }

      const baseUrl = process.env.NODE_ENV === 'development'
        ? 'http://localhost:8080'
        : (process.env.VUE_APP_BASE_API || window.location.origin)

      // 根据后端返回的 needStudent 字段构建URL
      let reportUrl
      if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
        // 统计报表
        reportUrl = `${baseUrl}/jmreport/view/${this.queryForm.reportId}`
      } else {
        // 学生报表
        reportUrl = `${baseUrl}/jmreport/view/${this.queryForm.reportId}?studentId=${this.queryForm.studentId}`
      }

      if (command === 'current') {
        // 当前页面预览
        this.reportUrl = reportUrl
      } else if (command === 'newWindow') {
        // 新窗口预览
        window.open(reportUrl, '_blank', 'width=1400,height=900,scrollbars=yes,resizable=yes')
        this.$modal.msgSuccess('已在新窗口打开报表')
      }
    },

    /** 处理归档下拉菜单命令 */
    handleArchiveCommand(command) {
      if (command === 'singlePdf') {
        // 归档当前报表为PDF
        this.generateAndSavePdf()
      } else if (command === 'singleExcel') {
        // 归档当前报表为Excel
        this.generateAndSaveExcel()
      } else if (command === 'batch') {
        // 批量归档
        this.openBatchDialog()
      }
    },

    /** 重置查询 */
    resetQuery() {
      this.queryForm = {
        studentName: '',
        studentId: null,
        reportId: null
      }
      this.selectedStudent = null
      this.reportUrl = ''
      // 重新加载所有报表列表（不传学生ID，返回所有报表）
      this.loadReportList()
    },

    /** 报表加载完成 */
    onReportLoad() {
      this.hideReportToolbar()
      this.centerReport()
    },

    /** 隐藏报表内置工具栏 */
    hideReportToolbar() {
      try {
        const iframeComp = this.$refs.reportIframe
        const iframeEl = iframeComp && iframeComp.$refs && iframeComp.$refs.iframe
        const doc = iframeEl && iframeEl.contentDocument
        if (!doc) return

        const style = doc.createElement('style')
        style.innerHTML = `
          .jimu-report-header,
          .jimu-report-action,
          .jimu-toolbar,
          .jr-toolbar,
          .report-operate,
          .ant-card-head,
          .ant-card-actions,
          .vxe-toolbar,
          .jr-tabs-header {
            display: none !important;
          }
          /* 居中报表内容 */
          html, body {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            width: 100%;
            box-sizing: border-box;
            background: #f5f7fa !important;
          }
          /* 让报表容器本身居中 */
          .jimu-report,
          .jimu-container,
          .jimu-report-main,
          .jr-view,
          .jr-layout,
          .jr-sheet,
          .report-view-container,
          .report-view-main,
          .jr-main,
          .jr-body {
            margin-left: auto !important;
            margin-right: auto !important;
            display: flex !important;
            justify-content: center !important;
          }
          .jr-view > * {
            margin-left: auto !important;
            margin-right: auto !important;
          }
          .jr-report,
          .jr-body,
          .jr-container,
          .jr-layout,
          .jr-sheet,
          .jimu-report,
          .jr-view,
          .report-view-container {
            margin: 0 auto !important;
          }
          /* 针对常见根节点与表格再居中一层 */
          body > * {
            margin-left: auto !important;
            margin-right: auto !important;
          }
          #app, .app, .jr-app, .jr-view-main, .jr-view-content {
            display: flex !important;
            justify-content: center !important;
            margin-left: auto !important;
            margin-right: auto !important;
          }
          table {
            margin-left: auto !important;
            margin-right: auto !important;
          }
          body {
            padding: 0 !important;
            margin: 0 !important;
            background: #f5f7fa !important;
          }
        `
        doc.head.appendChild(style)
        // 额外尝试居中
        this.centerReport(doc)
      } catch (e) {
        // 忽略跨域或注入失败
        console.warn('隐藏报表工具栏失败', e)
      }
    },

    /** 尝试将报表主体再居中一次 */
    centerReport(doc) {
      try {
        const iframeComp = this.$refs.reportIframe
        const iframeEl = iframeComp && iframeComp.$refs && iframeComp.$refs.iframe
        const d = doc || (iframeEl && iframeEl.contentDocument)
        if (!d) return

        // 包一层居中容器
        const body = d.body
        if (body && !body.querySelector('.__center-wrap')) {
          const wrapper = d.createElement('div')
          wrapper.className = '__center-wrap'
          wrapper.style.display = 'flex'
          wrapper.style.justifyContent = 'center'
          wrapper.style.alignItems = 'flex-start'
          wrapper.style.width = '100%'
          wrapper.style.boxSizing = 'border-box'
          wrapper.style.background = '#f5f7fa'

          while (body.firstChild) {
            wrapper.appendChild(body.firstChild)
          }
          body.appendChild(wrapper)
          body.style.margin = '0'
          body.style.padding = '0'
          body.style.display = 'block'
        }

        // 找到可能的报表容器或表格
        const candidates = [
          '#app',
          '.jr-view',
          '.jr-view-main',
          '.jr-view-content',
          '.jr-container',
          '.jr-report',
          '.jimu-report',
          '.report-view-container',
          '.report-view-main',
          '.report-container',
          '.jimu-table',
          'table'
        ]

        for (const sel of candidates) {
          const el = d.querySelector(sel)
          if (el) {
            el.style.marginLeft = 'auto'
            el.style.marginRight = 'auto'
            el.style.display = 'block'
            if (el.style.width === '' || el.style.width === 'auto') {
              el.style.width = 'fit-content'
              el.style.maxWidth = '100%'
            }
          }
        }

        // 对 body 再包装一层 flex
        if (body) {
          body.style.display = 'block'
          body.style.background = '#f5f7fa'
        }
      } catch (err) {
        console.warn('居中报表失败', err)
      }
    },

    /** 新窗口打开 */
    openInNewWindow() {
      if (this.reportUrl) {
        window.open(this.reportUrl, '_blank', 'width=1200,height=800,scrollbars=yes')
      }
    },

    /** 打印报表 */
    printReport() {
      if (this.reportUrl) {
        const printWindow = window.open(this.reportUrl, '_blank', 'width=1200,height=800')
        if (printWindow) {
          printWindow.onload = () => {
            setTimeout(() => {
              printWindow.print()
            }, 500)
          }
        }
      }
    },

    /** 导出PDF */
    exportReport() {
      if (this.reportUrl) {
        // 积木报表的导出PDF功能，可以在URL后添加参数
        const exportUrl = this.reportUrl + '&exportType=pdf'
        window.open(exportUrl, '_blank')
      }
    },

    /** 生成并保存PDF */
    async generateAndSavePdf() {
      if (!this.canPreview) {
        if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
          this.$modal.msgWarning('请选择报表')
        } else {
          this.$modal.msgWarning('请选择学生和报表')
        }
        return
      }

      this.pdfGenerating = true
      try {
        // 获取当前学年学期ID（可以从系统配置或学生信息中获取）
        const yearSemesterId = this.currentYearSemesterId || null

        // 根据报表类型构建请求参数
        const params = {
          reportId: this.queryForm.reportId,
          yearSemesterId: yearSemesterId
        }

        // 只在学生报表时传递 studentId
        if (this.currentReportInfo && this.currentReportInfo.needStudent !== false) {
          params.studentId = this.queryForm.studentId
        }

        const response = await generatePdf(params)

        if (response.code === 200) {
          this.$modal.msgSuccess('PDF生成并保存成功')
          // 通知归档页面刷新
          window.dispatchEvent(new CustomEvent('report-archived'))
          // 可以提示用户下载
          this.$modal.confirm('PDF已保存到服务器，是否立即下载？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '取消',
            type: 'success'
          }).then(() => {
            // 使用下载API下载PDF
            downloadPdf(response.data.id).then(res => {
              const blob = new Blob([res], { type: 'application/pdf' })
              const link = document.createElement('a')
              link.href = window.URL.createObjectURL(blob)
              link.download = response.data.fileName
              document.body.appendChild(link)
              link.click()
              document.body.removeChild(link)
              window.URL.revokeObjectURL(link.href)
              this.$modal.msgSuccess('下载成功')
            }).catch(err => {
              console.error('下载失败:', err)
              this.$modal.msgError('下载失败：' + (err.message || '网络错误'))
            })
          }).catch(() => {})
        } else {
          this.$modal.msgError(response.msg || 'PDF生成失败')
        }
      } catch (error) {
        console.error('生成PDF失败:', error)
        this.$modal.msgError('PDF生成失败：' + (error.message || '网络错误'))
      } finally {
        this.pdfGenerating = false
      }
    },

    /** 生成并保存Excel */
    async generateAndSaveExcel() {
      if (!this.canPreview) {
        if (this.currentReportInfo && this.currentReportInfo.needStudent === false) {
          this.$modal.msgWarning('请选择报表')
        } else {
          this.$modal.msgWarning('请选择学生和报表')
        }
        return
      }

      this.pdfGenerating = true // 复用PDF生成状态
      try {
        // 获取当前学年学期ID（可以从系统配置或学生信息中获取）
        const yearSemesterId = this.currentYearSemesterId || null

        // 根据报表类型构建请求参数
        const params = {
          reportId: this.queryForm.reportId,
          yearSemesterId: yearSemesterId
        }

        // 只在学生报表时传递 studentId
        if (this.currentReportInfo && this.currentReportInfo.needStudent !== false) {
          params.studentId = this.queryForm.studentId
        } else {
          // 统计报表不需要传递studentId
          params.studentId = null
        }

        // 调用后端API生成Excel并保存
        const response = await generateExcel(params)

        if (response.code === 200) {
          this.$modal.msgSuccess('Excel生成并保存成功')
          // 通知归档页面刷新
          window.dispatchEvent(new CustomEvent('report-archived'))
          // 可以提示用户下载
          this.$modal.confirm('Excel已保存到服务器，是否立即下载？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '取消',
            type: 'success'
          }).then(() => {
            // 使用下载API下载Excel
            downloadExcel(response.data.id).then(res => {
              const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
              const link = document.createElement('a')
              link.href = window.URL.createObjectURL(blob)
              link.download = response.data.fileName
              document.body.appendChild(link)
              link.click()
              document.body.removeChild(link)
              window.URL.revokeObjectURL(link.href)
              this.$modal.msgSuccess('下载成功')
            }).catch(err => {
              console.error('下载失败:', err)
              this.$modal.msgError('下载失败：' + (err.message || '网络错误'))
            })
          }).catch(() => {})
        } else {
          this.$modal.msgError(response.msg || 'Excel生成失败')
        }
      } catch (error) {
        console.error('生成Excel失败:', error)
        this.$modal.msgError('Excel生成失败：' + (error.message || '网络错误'))
      } finally {
        this.pdfGenerating = false
      }
    },

    /** 打开批量导出弹窗 */
    openBatchDialog() {
      if (!this.queryForm.reportId) {
        this.$modal.msgWarning('请先选择报表')
        return
      }
      // 默认批次名
      this.batchForm.batchName = this.batchForm.batchName || `批次_${new Date().getTime()}`
      // 若已有当前学年学期，带上
      if (this.currentYearSemesterId) {
        this.batchForm.yearSemesterId = this.currentYearSemesterId
      }
      // 重置学生表格数据
      this.batchForm.studentIds = []
      this.studentSearchKeyword = ''
      this.studentTablePage.pageNum = 1
      // 初始化学段列表
      if (!this.schoolingPlanOptions.length) {
        this.loadSchoolingPlans()
      } else {
        // 已有学段列表时，加载学生表格
        this.loadStudentTable()
      }
      this.batchDialogVisible = true
    },

    /** 提交批量导出 */
    async submitBatchGenerate() {
      if (!this.queryForm.reportId) {
        this.$modal.msgWarning('请先选择报表')
        return
      }

      // 防止重复提交
      if (this.batchGenerating) {
        return
      }

      // 处理学生ID：转换为数字、过滤无效值、去重
      let studentIds = (this.batchForm.studentIds || []).map(x => Number(x)).filter(x => !isNaN(x) && x > 0)
      // 去重
      studentIds = [...new Set(studentIds)]

      // 如果没有选择学生，也没有筛选条件，提示用户
      if (studentIds.length === 0 && !this.batchForm.schoolingPlanId && !this.batchForm.studentName) {
        this.$modal.msgWarning('请选择学生或提供筛选条件（学段/姓名）')
        return
      }

      const payload = {
        studentIds: studentIds.length > 0 ? studentIds : null,
        reportId: this.queryForm.reportId,
        yearSemesterId: this.batchForm.yearSemesterId || null,
        batchName: this.batchForm.batchName || null,
        schoolingPlanId: this.batchForm.schoolingPlanId || null,
        studentName: this.batchForm.studentName || null
      }

      this.batchGenerating = true
      try {
        const res = await batchGeneratePdf(payload)
        if (res.code === 200) {
          this.$modal.msgSuccess('批量生成已提交，批次ID：' + res.data.batchId)
          // 通知归档页面刷新
          window.dispatchEvent(new CustomEvent('report-archived'))
          // 清空表单
          this.batchForm.studentIds = []
          this.batchDialogVisible = false
        } else {
          this.$modal.msgError(res.msg || '批量生成失败')
        }
      } catch (e) {
        console.error('批量生成失败', e)
        this.$modal.msgError('批量生成失败：' + (e.message || '网络错误'))
      } finally {
        this.batchGenerating = false
      }
    },

    /** 加载学生表格数据 */
    loadStudentTable() {
      this.studentLoading = true
      const params = {
        pageNum: this.studentTablePage.pageNum,
        pageSize: this.studentTablePage.pageSize,
        schoolingPlanId: this.batchForm.schoolingPlanId || undefined
      }

      // 如果有搜索关键字，添加到查询参数
      const searchKeyword = this.studentSearchKeyword || this.batchForm.studentName
      if (searchKeyword && searchKeyword.trim()) {
        params.name = searchKeyword.trim()
      }

      listStudentsBase(params).then(res => {
        if (res.code === 200 && res.rows) {
          this.studentTableData = res.rows.map(item => {
            const gradeName = item.gradeName || item.grade_name || ''
            const className = item.className || item.class_name || ''
            const gradeClass = gradeName && className ? `${gradeName}/${className}` : (gradeName || className || '')
            return {
              id: item.id,
              name: item.name,
              gradeClass: gradeClass
            }
          })
          this.studentTablePage.total = res.total || 0

          // 恢复已选中的学生
          this.$nextTick(() => {
            this.restoreStudentSelection()
          })
        } else {
          this.studentTableData = []
          this.studentTablePage.total = 0
        }
      }).catch(err => {
        console.error('加载学生列表失败:', err)
        this.studentTableData = []
        this.studentTablePage.total = 0
      }).finally(() => {
        this.studentLoading = false
      })
    },

    /** 恢复已选中的学生 */
    restoreStudentSelection() {
      if (!this.$refs.studentTable || !this.batchForm.studentIds || this.batchForm.studentIds.length === 0) {
        return
      }
      this.studentTableData.forEach(row => {
        if (this.batchForm.studentIds.includes(row.id)) {
          this.$refs.studentTable.toggleRowSelection(row, true)
        }
      })
    },

    /** 学段变化时，重新加载学生表格 */
    onSchoolingPlanChange() {
      // 清空已选学生，避免跨学段误选
      this.batchForm.studentIds = []
      this.studentTablePage.pageNum = 1
      this.loadStudentTable()
    },

    /** 学生表格搜索 */
    handleStudentTableSearch() {
      this.studentTablePage.pageNum = 1
      this.loadStudentTable()
    },

    /** 学生姓名输入框搜索 */
    handleStudentNameSearch() {
      this.studentSearchKeyword = this.batchForm.studentName
      this.studentTablePage.pageNum = 1
      this.loadStudentTable()
    },

    /** 学生表格选择变化 */
    handleStudentSelectionChange(selection) {
      const selectedIds = selection.map(item => item.id)
      // 合并当前页选中的学生ID
      const currentPageIds = this.studentTableData.map(item => item.id)
      // 移除当前页未选中的ID
      this.batchForm.studentIds = this.batchForm.studentIds.filter(id => !currentPageIds.includes(id))
      // 添加当前页新选中的ID
      this.batchForm.studentIds = [...this.batchForm.studentIds, ...selectedIds]
      // 去重
      this.batchForm.studentIds = [...new Set(this.batchForm.studentIds)]
    },

    /** 全选 */
    handleSelectAll() {
      if (!this.$refs.studentTable) {
        return
      }
      this.studentTableData.forEach(row => {
        this.$refs.studentTable.toggleRowSelection(row, true)
      })
      // 更新选中列表
      const selectedIds = this.studentTableData.map(item => item.id)
      this.batchForm.studentIds = [...new Set([...this.batchForm.studentIds, ...selectedIds])]
    },

    /** 清空选择 */
    handleClearSelection() {
      if (!this.$refs.studentTable) {
        return
      }
      this.$refs.studentTable.clearSelection()
      // 移除当前页的学生ID
      const currentPageIds = this.studentTableData.map(item => item.id)
      this.batchForm.studentIds = this.batchForm.studentIds.filter(id => !currentPageIds.includes(id))
    },

    /** 学生表格分页大小变化 */
    handleStudentPageSizeChange(size) {
      this.studentTablePage.pageSize = size
      this.studentTablePage.pageNum = 1
      this.loadStudentTable()
    },

    /** 学生表格页码变化 */
    handleStudentPageChange(page) {
      this.studentTablePage.pageNum = page
      this.loadStudentTable()
    },

    /** 加载学段列表 */
    loadSchoolingPlans() {
      getSchoolPlanList().then(res => {
        if (res.code === 200 && Array.isArray(res.data)) {
          this.schoolingPlanOptions = res.data.map(item => ({
            label: item.name || item.planName || item.plan_name || item.id,
            value: item.id
          }))
          // 如果未选择学段，默认选第一个并触发学生表格加载
          if (!this.batchForm.schoolingPlanId && this.schoolingPlanOptions.length > 0) {
            this.batchForm.schoolingPlanId = this.schoolingPlanOptions[0].value
            this.loadStudentTable()
          } else {
            this.loadStudentTable()
          }
        }
      })
    },

    /** 跳转到合并页面 */
    goMergePage() {
      this.$router.push({ path: '/system/report/merge' })
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
      // 先加载所有报表的PDF统计
      listReportPdf({ status: 1, isMerged: 0 }).then(res => {
        if (res.code === 200) {
          const allPdfs = res.rows || res.data || []
          // 按报表ID分组统计
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

          // 转换为数组，只显示有PDF的报表
          this.availableReports = Array.from(reportMap.values()).filter(r => r.pdfCount > 0)

          // 如果当前选择的报表在列表中，保持选中
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

      // 获取报表名称
      const report = this.availableReports.find(r => r.id === reportId) ||
                     this.reportList.find(r => r.id === reportId)
      const reportName = report ? report.name : '报表'

      // 自动生成合并文件名：报表名称+汇总
      this.mergeForm.mergedFileName = reportName + '汇总'

      // 加载该报表的所有PDF
      this.loadPdfListByReport(reportId)
    },

    /** 根据报表ID加载PDF列表 */
    loadPdfListByReport(reportId) {
      this.pdfTableLoading = true
      const params = {
        pageNum: this.pdfTablePage.pageNum,
        pageSize: this.pdfTablePage.pageSize,
        reportId: reportId,
        status: 1, // 只查询有效的PDF
        isMerged: 0 // 只查询未合并的PDF
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

    /** PDF表格分页大小变化 */
    handlePdfPageSizeChange(size) {
      this.pdfTablePage.pageSize = size
      this.pdfTablePage.pageNum = 1
      if (this.mergeForm.reportId) {
        this.loadPdfListByReport(this.mergeForm.reportId)
      }
    },

    /** PDF表格页码变化 */
    handlePdfPageChange(page) {
      this.pdfTablePage.pageNum = page
      if (this.mergeForm.reportId) {
        this.loadPdfListByReport(this.mergeForm.reportId)
      }
    },

    /** 格式化文件大小 */
    formatFileSize(bytes) {
      if (!bytes || bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    },

    /** 下载单个PDF */
    downloadSinglePdf(id) {
      downloadPdf(id).then(res => {
        const blob = new Blob([res], { type: 'application/pdf' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        // 从PDF列表中获取文件名
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

      // 防止重复提交
      if (this.merging) {
        return
      }

      this.merging = true
      try {
        // 使用按条件合并接口，更高效
        const response = await mergePdfsByCondition({
          reportId: this.mergeForm.reportId,
          yearSemesterId: null, // 不限制学年学期
          schoolingPlanId: null, // 不限制学段
          gradeId: null, // 不限制年级
          classId: null, // 不限制班级
          mergedFileName: this.mergeForm.mergedFileName.trim()
        })

        if (response.code === 200) {
          this.$modal.msgSuccess('PDF合并成功')
          // 提示用户下载
          this.$modal.confirm('PDF合并成功，是否立即下载？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '取消',
            type: 'success'
          }).then(() => {
            // 下载合并后的PDF
            if (response.data && response.data.id) {
              downloadBatchPdf(response.data.id).then(res => {
                const blob = new Blob([res], { type: 'application/pdf' })
                const link = document.createElement('a')
                link.href = window.URL.createObjectURL(blob)
                link.download = response.data.mergedFileName || 'merged.pdf'
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
                window.URL.revokeObjectURL(link.href)
                this.$modal.msgSuccess('下载成功')
              }).catch(err => {
                console.error('下载失败:', err)
                this.$modal.msgError('下载失败：' + (err.message || '网络错误'))
              })
            }
          }).catch(() => {})

          // 刷新列表
          this.loadAvailableReports()
          if (this.mergeForm.reportId) {
            this.loadPdfListByReport(this.mergeForm.reportId)
          }
          this.loadBatchList()
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

    /** 右侧合并结果列表 */
    loadBatchList() {
      this.batchLoading = true
      const params = {
        pageNum: this.batchPage.pageNum,
        pageSize: this.batchPage.pageSize
      }
      listReportPdfBatch(params).then(res => {
        if (res.code === 200) {
          this.batchList = res.rows || res.data || []
          this.batchPage.total = res.total || 0
        } else {
          this.batchList = []
          this.batchPage.total = 0
        }
      }).catch(err => {
        console.error('加载合并结果失败:', err)
        this.batchList = []
        this.batchPage.total = 0
      }).finally(() => {
        this.batchLoading = false
      })
    },

    handleBatchPageSizeChange(size) {
      this.batchPage.pageSize = size
      this.batchPage.pageNum = 1
      this.loadBatchList()
    },

    handleBatchPageChange(page) {
      this.batchPage.pageNum = page
      this.loadBatchList()
    },

    /** 下载合并后的PDF */
    downloadMergedBatch(id, fileName) {
      downloadBatchPdf(id).then(res => {
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

    /** 预览合并后的PDF（新窗口） */
    previewMergedBatch(id) {
      downloadBatchPdf(id).then(res => {
        const blob = new Blob([res], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        window.open(url, '_blank')
        setTimeout(() => window.URL.revokeObjectURL(url), 10000)
      }).catch(err => {
        console.error('预览失败:', err)
        this.$modal.msgError('预览失败：' + (err.message || '网络错误'))
      })
    }
  }
}
</script>

<style scoped lang="scss">
// ============ 字体设置 ============
.report-preview-page {
  font-family: 'Source Han Sans CN', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
}

// ============ 设计系统变量 ============
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;
$text-primary: #303133;
$text-secondary: #606266;
$text-tertiary: #909399;
$border-color: #dcdfe6;
$bg-primary: #ffffff;
$bg-secondary: #f5f7fa;
$bg-tertiary: #fafafa;
$shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.06);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.1);
$radius-sm: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;

// ============ 页面容器 ============
.report-preview-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  padding: 20px;
}

.report-preview-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 40px);
  max-width: 1920px;
  margin: 0 auto;
}

// ============ 左侧报表树 ============
.report-tree-pane {
  width: 320px;
  background: $bg-primary;
  border-radius: $radius-xl;
  border: 1px solid $border-color;
  box-shadow: $shadow-md;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: $shadow-lg;
  }
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #ffffff;
  color: #262626;
  border-bottom: 1px solid #e8e8e8;

  .header-content {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
  }

  .manage-btn {
    color: #595959 !important;
    font-size: 20px;
    padding: 8px;
    
    &:hover {
      background-color: #f5f5f5;
      border-radius: 4px;
      color: #262626 !important;
    }
  }

  .header-icon {
    font-size: 24px;
    opacity: 0.9;
    color: #eeeb16; // 黄色
  }

  .header-text {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .title {
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 0.5px;
    color: #262626;
  }

  .subtitle {
    font-size: 12px;
    opacity: 0.65;
    color: #8c8c8c;
  }
}

.report-tree {
  flex: 1;
  overflow: auto;
  padding: 12px;
  background: transparent;

  :deep(.el-tree-node__content) {
    height: 40px;
    border-radius: $radius-md;
    margin-bottom: 4px;
    padding: 0 12px;
    transition: all 0.2s ease;

    &:hover {
      background: #f0f5ff;
    }
  }

  :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background: #e6f7e6;
    border-left: 3px solid #52c41a;
    font-weight: 600;
    color: #52c41a;
  }
}

.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 4px 0;

  .node-main {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;

    .tree-icon {
      width: 18px;
      height: 18px;
      object-fit: contain;
    }

    .node-label {
      font-size: 14px;
      color: $text-primary;
    }
  }

  .node-count {
    background: #f0f9ff;
    color: #52c41a;
    border: none;
    font-weight: 500;
  }
}

// ============ 右侧主体区域 ============
.report-main-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.preview-glass-wrapper {
  flex: 1;
  background: $bg-primary;
  border-radius: $radius-xl;
  border: 1px solid $border-color;
  box-shadow: $shadow-md;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: $shadow-lg;
  }
}

// ============ 搜索区域 ============
.search-block {
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-bottom: 1px solid $border-color;
}

.search-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 15px;
  font-weight: 600;
  color: $text-primary;

  i {
    color: $primary-color;
    font-size: 18px;
  }
}

.search-form {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;

  :deep(.el-form-item) {
    margin: 0;
    display: flex;
    align-items: center;

    .el-form-item__label {
      color: $text-secondary;
      font-weight: 500;
      font-size: 13px;
      white-space: nowrap;
      margin-bottom: 0 !important;
    }
    
    .el-form-item__content {
      margin-left: 0 !important;
    }
  }
  
  // 学生姓名输入框
  .form-item-student {
    flex: 0 0 auto;
    
    :deep(.el-form-item__label) {
      width: auto;
      padding-right: 8px;
    }
    
    :deep(.el-form-item__content) {
      width: 200px;
    }
  }
  
  // 报表名称输入框
  .form-item-report {
    flex: 0 0 auto;
    
    :deep(.el-form-item__label) {
      width: auto;
      padding-right: 8px;
    }
    
    :deep(.el-form-item__content) {
      width: 280px;
    }
  }

  .student-autocomplete,
  .report-input {
    width: 100%;

    :deep(.el-input__inner) {
      border-radius: $radius-md;
      border: 1px solid $border-color;
      transition: all 0.3s ease;

      &:focus {
        border-color: $primary-color;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
      }
    }
  }
}

.search-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  :deep(.el-button) {
    border-radius: $radius-md;
    font-weight: 500;
    transition: all 0.2s ease;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: $shadow-md;
    }
  }
  
  // 白色按钮样式 - 普通按钮
  :deep(.white-btn.el-button) {
    background-color: #ffffff !important;
    border-color: #dcdfe6 !important;
    color: #606266 !important;
    
    i {
      color: #606266 !important;
    }
    
    &:hover:not(.is-disabled) {
      background-color: #f5f7fa !important;
      border-color: #c0c4cc !important;
      color: #303133 !important;
    }
    
    &:focus,
    &:active {
      background-color: #e4e7ed !important;
      border-color: #c0c4cc !important;
      color: #303133 !important;
    }
    
    &.is-disabled {
      background-color: #f5f7fa !important;
      border-color: #e4e7ed !important;
      color: #c0c4cc !important;
    }
  }
  
  // 白色按钮样式 - 下拉按钮
  .white-btn.el-dropdown {
    :deep(.el-button) {
      background-color: #ffffff !important;
      background-image: none !important;
      border-color: #dcdfe6 !important;
      color: #606266 !important;
      
      i {
        color: #606266 !important;
      }
      
      &:hover:not(.is-disabled) {
        background-color: #f5f7fa !important;
        background-image: none !important;
        border-color: #c0c4cc !important;
        color: #303133 !important;
      }
      
      &:focus,
      &:active {
        background-color: #e4e7ed !important;
        background-image: none !important;
        border-color: #c0c4cc !important;
        color: #303133 !important;
      }
      
      &.is-disabled {
        background-color: #f5f7fa !important;
        background-image: none !important;
        border-color: #e4e7ed !important;
        color: #c0c4cc !important;
      }
    }
    
    :deep(.el-dropdown__caret-button) {
      background-color: #ffffff !important;
      background-image: none !important;
      border-color: #dcdfe6 !important;
      color: #606266 !important;
      
      &::before {
        border-left-color: #dcdfe6 !important;
      }
      
      i {
        color: #606266 !important;
      }
      
      &:hover:not(.is-disabled) {
        background-color: #f5f7fa !important;
        background-image: none !important;
        border-color: #c0c4cc !important;
        color: #303133 !important;
      }
      
      &:focus,
      &:active {
        background-color: #e4e7ed !important;
        background-image: none !important;
        border-color: #c0c4cc !important;
        color: #303133 !important;
      }
      
      &.is-disabled {
        background-color: #f5f7fa !important;
        background-image: none !important;
        border-color: #e4e7ed !important;
        color: #c0c4cc !important;
      }
    }
  }
}

// ============ 报表展示区域 ============
.report-display-area {
  flex: 1;
  overflow: auto;
  padding: 20px;
  background: #b1b6bd; // 两侧灰色背景
  display: flex;
  justify-content: center;
  align-items: flex-start;

  .report-iframe {
    width: 85%; // 调整为85%宽度，更紧凑
    max-width: 1200px; // 最大宽度限制
    min-height: 800px;
    border: 1px solid $border-color;
    border-radius: $radius-md;
    background: $bg-primary;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); // 增强阴影效果，突出报表区域
  }
}

.empty-state {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600px;

  .empty-content {
    text-align: center;
    padding: 24px;
    background: rgba(255, 255, 255, 0.6);
    border-radius: $radius-md;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    max-width: 240px;
    min-width: 200px;

    .empty-icon {
      width: 80px;
      height: 80px;
      margin: 0 auto 12px;
      display: block;
      opacity: 0.9;
    }

    .empty-text {
      font-size: 14px;
      color: $text-secondary;
      margin: 0 0 8px 0;
      font-weight: 500;
      line-height: 1.4;
    }

    .empty-hint {
      font-size: 11px;
      color: $text-tertiary;
      margin: 0;
      line-height: 1.5;
    }
  }
}

// ============ 学生选项 ============
.student-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;

  .student-name {
    font-weight: 600;
    color: $text-primary;
    font-size: 14px;
  }

  .student-info {
    font-size: 12px;
    color: $text-tertiary;
    margin-left: 20px;
  }
}

:deep(.el-autocomplete-suggestion__list) {
  .student-item {
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }
  }
}

// ============ 学生表格容器 ============
.student-table-container {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  .student-table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;

    .search-wrapper {
      flex: 1;
      max-width: 300px;

      .student-search-input {
        ::v-deep .el-input__inner {
          border-radius: 20px;
          border: 1px solid #dcdfe6;
          transition: all 0.3s;

          &:focus {
            border-color: #409eff;
            box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
          }
        }
      }
    }

    .action-wrapper {
      display: flex;
      align-items: center;
      gap: 16px;

      .selected-count {
        color: #409eff;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 4px;

        i {
          font-size: 16px;
        }

        strong {
          color: #303133;
          font-weight: 600;
          margin: 0 2px;
        }
      }

      .el-button {
        padding: 6px 12px;
        color: #606266;
        font-size: 13px;

        &:hover {
          color: #409eff;
          background: #ecf5ff;
        }

        i {
          margin-right: 4px;
        }
      }
    }
  }

  .table-wrapper {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    overflow: hidden;
    background: #ffffff;

    .student-table {
      ::v-deep .el-table__header {
        th {
          padding: 12px 0;
          font-size: 14px;
        }
      }

      ::v-deep .el-table__body {
        td {
          padding: 12px 0;
          font-size: 14px;
        }

        tr {
          &:hover {
            background-color: #f5f7fa;
          }
        }

        .student-name {
          color: #303133;
          font-weight: 500;
        }

        .grade-class {
          color: #606266;
        }
      }

      ::v-deep .el-checkbox {
        .el-checkbox__input.is-checked .el-checkbox__inner {
          background-color: #409eff;
          border-color: #409eff;
        }

        .el-checkbox__input.is-indeterminate .el-checkbox__inner {
          background-color: #409eff;
          border-color: #409eff;
        }
      }
    }
  }

  .student-table-pagination {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;

    ::v-deep .el-pagination {
      .el-pagination__total,
      .el-pagination__jump {
        color: #606266;
        font-size: 13px;
      }

      .btn-prev,
      .btn-next,
      .el-pager li {
        background-color: #ffffff;
        color: #606266;
        border: 1px solid #dcdfe6;

        &:hover {
          color: #409eff;
        }

        &.active {
          background-color: #409eff;
          color: #ffffff;
          border-color: #409eff;
        }
      }
    }
  }

  .help-text {
    margin-top: 12px;
    font-size: 12px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    background: #f5f7fa;
    border-radius: 4px;

    i {
      font-size: 14px;
      color: #409eff;
    }
  }

  .pdf-table-container {
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    padding: 16px;
    background: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

    .pdf-table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #f0f0f0;

      .selected-info {
        color: #409eff;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 4px;

        i {
          font-size: 16px;
        }

        strong {
          color: #303133;
          font-weight: 600;
          margin: 0 2px;
        }
      }

      .action-buttons {
        display: flex;
        align-items: center;
        gap: 12px;

        .el-button {
          padding: 6px 12px;
          color: #606266;
          font-size: 13px;

          &:hover {
            color: #409eff;
            background: #ecf5ff;
          }

          i {
            margin-right: 4px;
          }
        }
      }
    }

    .table-wrapper {
      border: 1px solid #ebeef5;
      border-radius: 4px;
      overflow: hidden;
      background: #ffffff;
    }

    .pdf-table-pagination {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
      padding-top: 12px;
      border-top: 1px solid #f0f0f0;
    }

    .help-text {
      margin-top: 12px;
      font-size: 12px;
      color: #909399;
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 12px;
      background: #f5f7fa;
      border-radius: 4px;

      i {
        font-size: 14px;
        color: #409eff;
      }
    }
  }
}

// ============ 报表排序弹窗 ============
.sort-dialog {
  ::v-deep .el-dialog__body {
    padding: 20px;
  }

  .filter-bar {
    ::v-deep .el-radio-button__inner {
      border-radius: 4px;
      transition: all 0.3s;
      background: #ffffff;
      border-color: #d9d9d9;
      color: #595959;

      &:hover {
        border-color: #8c8c8c;
        color: #262626;
      }
    }

    ::v-deep .el-radio-button__orig-radio:checked + .el-radio-button__inner {
      background-color: #595959;
      border-color: #595959;
      color: #ffffff;
      box-shadow: none;
    }
  }

  .sort-report-list {
    max-height: 500px;
    overflow-y: auto;
    padding: 10px;
    background: #fafafa;
    border-radius: 4px;
    border: 1px solid #e8e8e8;

    .draggable-list {
      .flip-list-move {
        transition: transform 0.3s;
      }
    }

    .sort-report-item {
      background: #ffffff;
      border: 1px solid #e8e8e8;
      border-radius: 4px;
      margin-bottom: 10px;
      transition: all 0.2s;
      cursor: move;

      &:hover {
        border-color: #bfbfbf;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      .report-item-content {
        display: flex;
        align-items: center;
        padding: 14px;
        gap: 12px;

        .drag-handle {
          cursor: grab;
          color: #bfbfbf;
          font-size: 18px;
          transition: color 0.2s;

          &:hover {
            color: #595959;
          }

          &:active {
            cursor: grabbing;
          }
        }

        .report-index {
          width: 32px;
          height: 32px;
          line-height: 32px;
          text-align: center;
          background: #8c8c8c;
          color: #ffffff;
          font-weight: 600;
          font-size: 14px;
          border-radius: 4px;
          flex-shrink: 0;
        }

        .report-info {
          flex: 1;
          min-width: 0;

          .report-name {
            font-size: 14px;
            font-weight: 500;
            color: #262626;
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .report-meta {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;

            ::v-deep .el-tag {
              background: #f5f5f5;
              border-color: #d9d9d9;
              color: #595959;
              font-size: 12px;
              height: 22px;
              line-height: 20px;
              padding: 0 8px;

              &.el-tag--success {
                background: #f6f6f6;
                border-color: #d9d9d9;
                color: #595959;
              }

              &.el-tag--warning {
                background: #fafafa;
                border-color: #d9d9d9;
                color: #595959;
              }
            }
          }
        }
      }
    }
  }
}

// ============ 响应式设计 ============
@media (max-width: 1400px) {
  .report-tree-pane {
    width: 280px;
  }
}

@media (max-width: 1200px) {
  .report-preview-container {
    flex-direction: column;
    height: auto;
  }

  .report-tree-pane {
    width: 100%;
    height: 400px;
  }

  .search-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .report-preview-page {
    padding: 12px;
  }

  .report-preview-container {
    gap: 12px;
  }

  .tree-header {
    padding: 16px;
  }

  .search-block {
    padding: 16px;
  }

  .search-actions {
    flex-direction: column;

    :deep(.el-button) {
      width: 100%;
    }
  }
}
</style>

