<template>
  <div class="merge-page">
    <div class="merge-container">
      <div class="page-header">
        <div class="title">合并PDF</div>
        <div class="desc">选择报表，一键合并所有未合并的PDF文件</div>
      </div>

      <div class="merge-section">
        <!-- 顶部操作栏 -->
        <el-card shadow="never" class="merge-card merge-toolbar">
          <div class="merge-toolbar-inner">
            <div class="left">
              <span class="label">报表</span>
              <el-select
                v-model="mergeForm.reportId"
                placeholder="请选择要合并的报表"
                style="width: 320px;"
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
              <span class="label">合并文件名</span>
              <el-input
                v-model="mergeForm.mergedFileName"
                placeholder="将自动生成：报表名称+汇总"
                style="width: 320px;"
                disabled
              />
            </div>
            <div class="right">
              <el-button type="primary" :loading="merging" :disabled="!mergeForm.reportId || pdfTablePage.total < 1" @click="submitMerge">
                合并汇总
              </el-button>
              <span class="tips">
                当前报表待合并：<strong>{{ pdfTablePage.total }}</strong> 个PDF
              </span>
            </div>
          </div>
        </el-card>

        <!-- 主体：标签页 -->
        <el-card shadow="never" class="merge-card merge-main">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="待合并文件" name="pending">
              <div class="merge-table-container">
                <div class="merge-table-header">
                  <div class="title">待合并 PDF 列表</div>
                  <div class="count">共 {{ pdfTablePage.total }} 个</div>
                </div>
                <el-table
                  :data="pdfTableData"
                  height="460"
                  v-loading="pdfTableLoading"
                  :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
                >
                  <el-table-column prop="fileName" label="文件名" min-width="260" show-overflow-tooltip />
                  <el-table-column prop="fileSize" label="大小" width="110" align="right">
                    <template slot-scope="scope">
                      {{ formatFileSize(scope.row.fileSize) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="createTime" label="创建时间" width="180" />
                  <el-table-column label="操作" width="100" align="center">
                    <template slot-scope="scope">
                      <el-button type="text" size="mini" @click="downloadSinglePdf(scope.row.id)">下载</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="merge-table-pagination">
                  <el-pagination
                    @size-change="handlePdfPageSizeChange"
                    @current-change="handlePdfPageChange"
                    :current-page="pdfTablePage.pageNum"
                    :page-sizes="[10, 20, 50, 100]"
                    :page-size="pdfTablePage.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="pdfTablePage.total"
                    background
                  />
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="合并记录" name="history">
              <div class="merge-table-container">
                <div class="merge-table-header">
                  <div class="title">合并记录</div>
                  <div class="count">共 {{ batchPage.total }} 条</div>
                </div>
                <el-table
                  :data="batchList"
                  height="460"
                  v-loading="batchLoading"
                  :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
                >
                  <el-table-column prop="mergedFileName" label="文件名" min-width="260" show-overflow-tooltip />
                  <el-table-column prop="studentCount" label="文件数" width="90" align="center" />
                  <el-table-column prop="mergedFileSize" label="大小" width="110" align="right">
                    <template slot-scope="scope">
                      {{ formatFileSize(scope.row.mergedFileSize) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="createTime" label="合并时间" width="180" />
                  <el-table-column label="操作" width="160" align="center">
                    <template slot-scope="scope">
                      <el-button type="text" size="mini" @click="previewMergedBatch(scope.row.id)">预览</el-button>
                      <el-button type="text" size="mini" @click="downloadMergedBatch(scope.row.id, scope.row.mergedFileName)">下载</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="merge-table-pagination">
                  <el-pagination
                    @size-change="handleBatchPageSizeChange"
                    @current-change="handleBatchPageChange"
                    :current-page="batchPage.pageNum"
                    :page-sizes="[10, 20, 50, 100]"
                    :page-size="batchPage.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="batchPage.total"
                    background
                  />
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { listReportPdf, mergePdfsByCondition, downloadPdf, downloadBatchPdf, listReportPdfBatch } from "@/api/system/reportPdf";

export default {
  name: "ReportMerge",
  data() {
    return {
      mergeForm: {
        mergedFileName: '',
        reportId: null
      },
      pdfTableData: [],
      pdfTableLoading: false,
      pdfTablePage: {
        pageNum: 1,
        pageSize: 20,
        total: 0
      },
      availableReports: [],
      merging: false,
      batchList: [],
      batchLoading: false,
      batchPage: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      activeTab: 'pending'
    }
  },
  computed: {
    currentMergeReportName() {
      if (!this.mergeForm.reportId) return ''
      const report = this.availableReports.find(r => r.id === this.mergeForm.reportId)
      return report ? report.name : ''
    }
  },
  mounted() {
    this.loadAvailableReports()
    this.loadBatchList()
  },
  methods: {
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
          this.$modal.confirm('PDF合并成功，是否立即下载？', '提示', {
            confirmButtonText: '下载',
            cancelButtonText: '取消',
            type: 'success'
          }).then(() => {
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

    /** 合并结果列表 */
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
.merge-page {
  min-height: 100vh;
  background: #f5f6f7;
  padding: 0 0 32px;
}

.merge-container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px 24px 0 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
  .title {
    font-size: 22px;
    font-weight: 700;
    color: #303133;
  }
  .desc {
    color: #909399;
    font-size: 13px;
  }
}

.merge-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.merge-card {
  background: transparent;

  ::v-deep .el-card__body {
    background: #ffffff;
    padding: 14px 18px;
  }
}

.merge-card.merge-main {
  ::v-deep .el-card__body {
    padding: 0 18px 16px 18px;
  }
}

.merge-card.merge-toolbar {
  ::v-deep .el-card__body {
    display: flex;
    align-items: center;
    padding: 10px 18px;
  }
}

.merge-toolbar-inner {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .left {
    display: flex;
    align-items: center;
    gap: 8px;

    .label {
      color: #606266;
      font-size: 13px;
    }
  }

  .right {
    display: flex;
    align-items: center;
    gap: 8px;

    .tips {
      color: #909399;
      font-size: 13px;

      strong {
        color: #303133;
        margin: 0 2px;
      }
    }
  }
}

.merge-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  .title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .desc {
    color: #909399;
    font-size: 13px;
  }
}

.merge-form {
  margin-bottom: 12px;
}

.merge-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;

  .tips {
    color: #909399;
    font-size: 13px;
  }
}

.merge-table-container {
  border-radius: 6px;
  padding: 12px 0 0 0;
  background: #ffffff;
}

.merge-table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;

  .title {
    font-weight: 600;
    color: #303133;
  }

  .count {
    color: #909399;
    font-size: 13px;
  }
}

.merge-table-pagination {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}
</style>

