<template>
  <div class="app-container subsidy-policy-page">
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
          label-width="90px"
          class="query-form"
        >
      <el-form-item label="政策编号" prop="policyCode">
        <el-input
          v-model="queryParams.policyCode"
          placeholder="请输入政策编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="政策名称" prop="policyName">
        <el-input
          v-model="queryParams.policyName"
          placeholder="请输入政策名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="政策类型" prop="policyType">
        <el-input
          v-model="queryParams.policyType"
          placeholder="请输入政策类型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="草稿" value="0" />
          <el-option label="已发布" value="1" />
          <el-option label="已废止" value="2" />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:subsidyPolicy:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:subsidyPolicy:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:subsidyPolicy:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:subsidyPolicy:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
      <div class="table-section">
    <el-table v-loading="loading" :data="policyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="政策编号" prop="policyCode" width="150" align="center" />
      <el-table-column label="政策名称" prop="policyName" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" @click.stop="handleView(scope.row)">{{ scope.row.policyName }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="政策版本" prop="policyVersion" width="100" align="center" />
      <el-table-column label="政策类型" prop="policyType" width="150" align="center" />
      <el-table-column label="生效日期" prop="effectiveDate" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.effectiveDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效日期" prop="expiryDate" width="120" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.expiryDate">{{ parseTime(scope.row.expiryDate, '{y}-{m}-{d}') }}</span>
          <span v-else style="color: #909399;">长期有效</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_policy_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" prop="publishTime" width="160" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.publishTime">{{ parseTime(scope.row.publishTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="发布人" prop="publisher" width="100" align="center" />
      <el-table-column label="政策文件" min-width="180" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.filePath">
            <el-link type="primary" @click.stop="handlePreviewFile(scope.row)">
              {{ scope.row.fileName || getFileNameFromPath(scope.row.filePath) }}
            </el-link>
          </span>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['system:subsidyPolicy:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:subsidyPolicy:edit']"
            v-if="scope.row.status == 0"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handlePublish(scope.row)"
            v-hasPermi="['system:subsidyPolicy:publish']"
            v-if="scope.row.status == 0"
          >发布</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAbolish(scope.row)"
            v-hasPermi="['system:subsidyPolicy:abolish']"
            v-if="scope.row.status == 1"
          >废止</el-button>
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

    <!-- 添加或修改资助政策对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-form-item label="政策编号" prop="policyCode">
              <el-input v-model="form.policyCode" placeholder="留空将自动生成" />
            </el-form-item>
            <el-form-item label="政策名称" prop="policyName">
              <el-input v-model="form.policyName" placeholder="请输入政策名称" />
            </el-form-item>
            <el-form-item label="政策版本" prop="policyVersion">
              <el-input v-model="form.policyVersion" placeholder="如：v1.0" />
            </el-form-item>
            <el-form-item label="政策类型" prop="policyType">
              <el-input v-model="form.policyType" placeholder="如：国家助学金、免学杂费" />
            </el-form-item>
            <el-form-item label="生效日期" prop="effectiveDate">
              <el-date-picker
                v-model="form.effectiveDate"
                type="date"
                placeholder="选择生效日期"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="失效日期" prop="expiryDate">
              <el-date-picker
                v-model="form.expiryDate"
                type="date"
                placeholder="选择失效日期（留空表示长期有效）"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="政策内容" prop="content">
              <editor v-model="form.content" :min-height="300" />
            </el-form-item>
            <el-form-item label="政策文件" prop="filePath">
              <el-upload
                ref="upload"
                :limit="1"
                accept=".pdf,.doc,.docx"
                :headers="upload.headers"
                :action="upload.url"
                :disabled="upload.isUploading"
                :on-progress="handleFileUploadProgress"
                :on-success="handleFileSuccess"
                :on-error="handleFileUploadError"
                :auto-upload="true"
                :show-file-list="false"
                drag
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">只能上传pdf/doc/docx文件，且不超过10MB</div>
              </el-upload>
              <div v-if="form.filePath" style="margin-top: 10px;">
                <el-link :href="getFileUrl(form.filePath)" target="_blank" type="primary">
                  <i class="el-icon-document"></i> {{ form.fileName || '查看文件' }}
                </el-link>
              </div>
            </el-form-item>
            <el-form-item label="备注" prop="memo">
              <el-input v-model="form.memo" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-tab-pane>
          
          <el-tab-pane label="适用范围" name="scope">
            <el-form-item label="适用范围">
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleAddScope">添加范围</el-button>
            </el-form-item>
            <el-table :data="form.scopeList" border>
              <el-table-column label="适用学制" width="150">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.schoolingPlanId" placeholder="全部学制" clearable style="width: 100%">
                    <el-option label="全部学制" :value="null" />
                    <el-option label="小学" :value="1" />
                    <el-option label="初中" :value="2" />
                    <el-option label="高中" :value="3" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="适用学期" width="150">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.semester" placeholder="全部学期" clearable style="width: 100%">
                    <el-option label="全部学期" :value="null" />
                    <el-option label="春季" :value="1" />
                    <el-option label="秋季" :value="2" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="适用学年" width="200">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.schoolYear" placeholder="如：2024-2025（留空表示全部）" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveScope(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          
          <el-tab-pane label="关联套餐" name="package">
            <el-form-item label="关联套餐">
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="handleAddPackage">添加套餐</el-button>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                提示：可以添加多个套餐，例如高中可以同时选择一档、二档、三档助学金和免学杂费
              </span>
            </el-form-item>
            <el-table :data="form.packageList" border>
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="套餐" width="350">
                <template slot-scope="scope">
                  <el-select 
                    v-model="scope.row.packageId" 
                    placeholder="请选择套餐" 
                    filterable
                    style="width: 100%"
                    @change="handlePackageChange(scope.$index, $event)"
                  >
                    <el-option
                      v-for="pkg in getAvailablePackagesForSelect(scope.row.packageId)"
                      :key="pkg.id"
                      :label="`${pkg.packageName} (${formatAmount(pkg.subsidyAmount)}元)`"
                      :value="pkg.id"
                      :disabled="isPackageSelected(pkg.id, scope.$index)"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="套餐名称" prop="packageName" width="200" />
              <el-table-column label="补助金额" prop="subsidyAmount" width="150" align="right">
                <template slot-scope="scope">
                  <span>{{ formatAmount(scope.row.subsidyAmount) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="排序" width="100">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.sortOrder" :min="0" size="mini" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemovePackage(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="form.packageList && form.packageList.length === 0" style="text-align: center; padding: 20px; color: #909399;">
              暂无关联套餐，请点击"添加套餐"按钮添加
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看政策详情对话框 -->
    <el-dialog title="政策详情" :visible.sync="viewOpen" width="90%" append-to-body>
      <el-tabs type="card">
        <el-tab-pane label="基本信息">
          <el-descriptions :column="3" border v-if="viewForm">
            <el-descriptions-item label="政策编号">{{ viewForm.policyCode }}</el-descriptions-item>
            <el-descriptions-item label="政策名称">{{ viewForm.policyName }}</el-descriptions-item>
            <el-descriptions-item label="政策版本">{{ viewForm.policyVersion }}</el-descriptions-item>
            <el-descriptions-item label="政策类型">{{ viewForm.policyType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="生效日期">{{ parseTime(viewForm.effectiveDate, '{y}-{m}-{d}') }}</el-descriptions-item>
            <el-descriptions-item label="失效日期">
              <span v-if="viewForm.expiryDate">{{ parseTime(viewForm.expiryDate, '{y}-{m}-{d}') }}</span>
              <span v-else>长期有效</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <dict-tag :options="dict.type.sys_policy_status" :value="viewForm.status"/>
            </el-descriptions-item>
            <el-descriptions-item label="发布时间" v-if="viewForm.publishTime">
              {{ parseTime(viewForm.publishTime, '{y}-{m}-{d} {h}:{i}') }}
            </el-descriptions-item>
            <el-descriptions-item label="发布人" v-if="viewForm.publisher">{{ viewForm.publisher }}</el-descriptions-item>
            <el-descriptions-item label="政策文件" :span="3" v-if="viewForm.filePath">
              <el-link :href="getFileUrl(viewForm.filePath)" target="_blank" type="primary">
                <i class="el-icon-document"></i> {{ viewForm.fileName || '查看文件' }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="3">{{ viewForm.memo || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="政策内容">
          <div class="detail-content-panel">
            <div v-if="viewForm.content" v-html="viewForm.content"></div>
            <div v-else style="color: #909399;">暂无内容</div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="适用范围">
          <el-table :data="viewForm.scopeList || []" border style="margin-top: 10px;">
            <el-table-column label="适用学制" width="150">
              <template slot-scope="scope">
                <span v-if="scope.row.schoolingPlanId == 1">小学</span>
                <span v-else-if="scope.row.schoolingPlanId == 2">初中</span>
                <span v-else-if="scope.row.schoolingPlanId == 3">高中</span>
                <span v-else>全部学制</span>
              </template>
            </el-table-column>
            <el-table-column label="适用学期" width="150">
              <template slot-scope="scope">
                <span v-if="scope.row.semester == 1">春季</span>
                <span v-else-if="scope.row.semester == 2">秋季</span>
                <span v-else>全部学期</span>
              </template>
            </el-table-column>
            <el-table-column label="适用学年" prop="schoolYear" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="关联套餐">
          <el-table :data="viewForm.packageList || []" border style="margin-top: 10px;">
            <el-table-column label="套餐名称" prop="packageName" />
            <el-table-column label="补助金额" prop="subsidyAmount" align="right">
              <template slot-scope="scope">
                <span>{{ formatAmount(scope.row.subsidyAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="排序" prop="sortOrder" width="100" align="center" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 废止政策对话框 -->
    <el-dialog title="废止政策" :visible.sync="abolishOpen" width="500px" append-to-body>
      <el-form ref="abolishForm" :model="abolishForm" label-width="100px">
        <el-form-item label="政策编号">
          <el-input v-model="abolishForm.policyCode" disabled />
        </el-form-item>
        <el-form-item label="政策名称">
          <el-input v-model="abolishForm.policyName" disabled />
        </el-form-item>
        <el-form-item label="废止原因" prop="abolishReason">
          <el-input v-model="abolishForm.abolishReason" type="textarea" :rows="4" placeholder="请输入废止原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAbolish">确 定</el-button>
        <el-button @click="abolishOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog
      title="政策文件预览"
      :visible.sync="previewDialogVisible"
      width="90%"
      append-to-body
      class="file-preview-dialog"
    >
      <div v-if="previewFileUrl" class="preview-container">
        <!-- PDF文件预览 -->
        <iframe
          v-if="isPdfFile(previewFileUrl)"
          :src="previewFileUrl"
          frameborder="0"
          class="preview-iframe"
        ></iframe>
        <!-- Word/Excel等Office文件，使用在线预览服务或提示下载 -->
        <div v-else-if="isOfficeFile(previewFileUrl)" class="office-preview">
          <el-alert
            title="Office文件预览"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 20px;"
          >
            <template slot="default">
              <p>Office文件（Word、Excel等）需要下载后查看，或使用在线预览服务。</p>
              <p>文件：{{ previewFileName }}</p>
            </template>
          </el-alert>
          <el-button type="primary" @click="downloadPreviewFile">
            <i class="el-icon-download"></i> 下载文件
          </el-button>
        </div>
        <!-- 图片文件预览 -->
        <div v-else-if="isImageFile(previewFileUrl)" class="image-preview">
          <img :src="previewFileUrl" :alt="previewFileName" style="max-width: 100%; max-height: 70vh;" />
        </div>
        <!-- 其他文件类型，使用iframe尝试预览 -->
        <iframe
          v-else
          :src="previewFileUrl"
          frameborder="0"
          class="preview-iframe"
        ></iframe>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="downloadPreviewFile" type="primary">
          <i class="el-icon-download"></i> 下载
        </el-button>
        <el-button @click="previewDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSubsidyPolicy, getSubsidyPolicy, delSubsidyPolicy, addSubsidyPolicy, updateSubsidyPolicy, publishPolicy, abolishPolicy } from "@/api/system/subsidyPolicy";
import { getToken } from "@/utils/auth";
import { getSubsidyPackages } from "@/api/system/subsidy";
import Editor from "@/components/Editor";

export default {
  name: "SubsidyPolicy",
  components: {
    Editor
  },
  dicts: ['sys_policy_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 查询表单是否折叠
      queryFormCollapsed: false,
      // 总条数
      total: 0,
      // 资助政策表格数据
      policyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 是否显示废止弹出层
      abolishOpen: false,
      // 是否显示预览对话框
      previewDialogVisible: false,
      // 预览文件URL
      previewFileUrl: '',
      // 预览文件名
      previewFileName: '',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        policyCode: null,
        policyName: null,
        policyType: null,
        status: null
      },
      // 表单参数
      form: {},
      // 查看表单
      viewForm: {},
      // 废止表单
      abolishForm: {},
      // 表单校验
      rules: {
        policyName: [
          { required: true, message: "政策名称不能为空", trigger: "blur" }
        ],
        policyVersion: [
          { required: true, message: "政策版本不能为空", trigger: "blur" }
        ],
        effectiveDate: [
          { required: true, message: "生效日期不能为空", trigger: "change" }
        ]
      },
      // 当前激活的标签页
      activeTab: 'basic',
      // 可用套餐列表
      availablePackages: [],
      // 文件上传
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/common/upload"
      }
    };
  },
  created() {
    this.getList();
    this.loadAvailablePackages();
  },
  methods: {
    /** 查询资助政策列表 */
    getList() {
      this.loading = true;
      listSubsidyPolicy(this.queryParams).then(response => {
        this.policyList = response.rows || [];
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 加载可用套餐列表 */
    loadAvailablePackages() {
      // 加载所有学制的套餐
      Promise.all([
        getSubsidyPackages({ schoolingPlanId: 1 }),
        getSubsidyPackages({ schoolingPlanId: 2 }),
        getSubsidyPackages({ schoolingPlanId: 3 })
      ]).then(results => {
        this.availablePackages = [];
        results.forEach(res => {
          // 从响应对象中获取data字段
          const packages = res.data || [];
          if (packages && packages.length > 0) {
            this.availablePackages.push(...packages);
          }
        });
      }).catch(error => {
        console.error('加载套餐列表失败:', error);
        this.$modal.msgError('加载套餐列表失败，请稍后重试');
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        policyCode: null,
        policyName: null,
        policyVersion: null,
        policyType: null,
        content: null,
        filePath: null,
        fileName: null,
        effectiveDate: null,
        expiryDate: null,
        status: 0,
        memo: null,
        scopeList: [],
        packageList: []
      };
      this.activeTab = 'basic';
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加资助政策";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids[0]
      getSubsidyPolicy(id).then(response => {
        this.form = response.data;
        // 确保数组存在，但不覆盖后端返回的数据
        if (!this.form.scopeList) {
          this.form.scopeList = [];
        }
        if (!this.form.packageList) {
          this.form.packageList = [];
        }
        this.open = true;
        this.title = "修改资助政策";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewForm = {};
      const id = row.id;
      getSubsidyPolicy(id).then(response => {
        this.viewForm = response.data;
        this.viewOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 处理日期格式
          if (this.form.effectiveDate) {
            this.form.effectiveDate = this.form.effectiveDate;
          }
          if (this.form.expiryDate) {
            this.form.expiryDate = this.form.expiryDate;
          }
          
          // 所有数据验证和过滤逻辑都在后端处理，前端只负责提交数据
          if (this.form.id != null) {
            updateSubsidyPolicy(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSubsidyPolicy(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除资助政策编号为"' + ids + '"的数据项？').then(function() {
        return delSubsidyPolicy(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 发布按钮操作 */
    handlePublish(row) {
      const id = row.id;
      this.$modal.confirm('是否确认发布该政策？发布后将无法修改。').then(function() {
        return publishPolicy(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("发布成功");
      }).catch(() => {});
    },
    /** 废止按钮操作 */
    handleAbolish(row) {
      this.abolishForm = {
        id: row.id,
        policyCode: row.policyCode,
        policyName: row.policyName,
        abolishReason: null
      };
      this.abolishOpen = true;
    },
    /** 提交废止 */
    submitAbolish() {
      if (!this.abolishForm.abolishReason) {
        this.$modal.msgWarning("请输入废止原因");
        return;
      }
      abolishPolicy(this.abolishForm.id, this.abolishForm.abolishReason).then(() => {
        this.$modal.msgSuccess("废止成功");
        this.abolishOpen = false;
        this.getList();
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/subsidyPolicy/export', {
        ...this.queryParams
      }, `subsidy_policy_${new Date().getTime()}.xlsx`)
    },
    /** 添加适用范围 */
    handleAddScope() {
      if (!this.form.scopeList) {
        this.form.scopeList = [];
      }
      this.form.scopeList.push({
        schoolingPlanId: null,
        gradeName: null,
        semester: null,
        schoolYear: null
      });
    },
    /** 删除适用范围 */
    handleRemoveScope(index) {
      this.form.scopeList.splice(index, 1);
    },
    /** 添加关联套餐 */
    handleAddPackage() {
      if (!this.form.packageList) {
        this.form.packageList = [];
      }
      this.form.packageList.push({
        packageId: null,
        packageName: null,
        subsidyAmount: null,
        sortOrder: 0
      });
    },
    /** 删除关联套餐 */
    handleRemovePackage(index) {
      this.form.packageList.splice(index, 1);
    },
    /** 套餐选择变化 */
    handlePackageChange(index, packageId) {
      // 套餐名称和金额由后端根据packageId自动填充，前端只负责选择
      // 这里可以留空或只做前端显示更新，实际数据由后端处理
      const selectedPackage = this.availablePackages.find(pkg => pkg.id === packageId);
      if (selectedPackage) {
        // 仅用于前端显示，实际保存时后端会重新填充
        this.form.packageList[index].packageName = selectedPackage.packageName;
        this.form.packageList[index].subsidyAmount = selectedPackage.subsidyAmount;
      }
    },
    /** 获取可选择的套餐列表（排除已选择的） */
    getAvailablePackagesForSelect(currentPackageId) {
      // 返回所有套餐，已选择的会被禁用
      return this.availablePackages;
    },
    /** 检查套餐是否已被选择（用于禁用选项） */
    isPackageSelected(packageId, currentIndex) {
      if (!packageId) return false;
      // 检查其他行是否已选择此套餐
      return this.form.packageList.some((pkg, index) => 
        index !== currentIndex && pkg.packageId === packageId
      );
    },
    /** 文件上传中处理 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    /** 文件上传成功处理 */
    handleFileSuccess(response, file, fileList) {
      this.upload.isUploading = false;
      // 后端返回 url(完整路径) 和 fileName(相对路径)，为保持数据库一致性，保存相对路径
      const relativePath = response.fileName || response.url || '';
      this.form.filePath = relativePath;
      this.form.fileName = response.originalFilename || file.name;
      this.$modal.msgSuccess("文件上传成功");
    },
    /** 文件上传失败处理 */
    handleFileUploadError(err) {
      this.upload.isUploading = false;
      this.$modal.msgError("文件上传失败：" + (err.msg || err.message || '未知错误'));
    },
    /** 格式化金额 */
    formatAmount(amount) {
      if (amount == null || amount === '') {
        return '0.00';
      }
      return parseFloat(amount).toFixed(2);
    },
    /** 预览政策文件 */
    handlePreviewFile(row) {
      if (!row.filePath) {
        this.$modal.msgWarning('该政策没有上传文件');
        return;
      }
      const fileUrl = this.getFileUrl(row.filePath);
      console.log('预览文件URL:', fileUrl, '原始路径:', row.filePath);
      
      // 设置预览文件信息
      this.previewFileUrl = fileUrl;
      this.previewFileName = row.fileName || '政策文件';
      
      // 打开预览对话框
      this.previewDialogVisible = true;
    },
    /** 判断是否为PDF文件 */
    isPdfFile(fileUrl) {
      if (!fileUrl) return false;
      const lowerUrl = fileUrl.toLowerCase();
      return lowerUrl.endsWith('.pdf') || lowerUrl.includes('.pdf');
    },
    /** 判断是否为Office文件 */
    isOfficeFile(fileUrl) {
      if (!fileUrl) return false;
      const lowerUrl = fileUrl.toLowerCase();
      return lowerUrl.endsWith('.doc') || lowerUrl.endsWith('.docx') ||
             lowerUrl.endsWith('.xls') || lowerUrl.endsWith('.xlsx') ||
             lowerUrl.endsWith('.ppt') || lowerUrl.endsWith('.pptx') ||
             lowerUrl.includes('.doc') || lowerUrl.includes('.docx') ||
             lowerUrl.includes('.xls') || lowerUrl.includes('.xlsx') ||
             lowerUrl.includes('.ppt') || lowerUrl.includes('.pptx');
    },
    /** 判断是否为图片文件 */
    isImageFile(fileUrl) {
      if (!fileUrl) return false;
      const lowerUrl = fileUrl.toLowerCase();
      return lowerUrl.endsWith('.jpg') || lowerUrl.endsWith('.jpeg') ||
             lowerUrl.endsWith('.png') || lowerUrl.endsWith('.gif') ||
             lowerUrl.endsWith('.bmp') || lowerUrl.endsWith('.webp') ||
             lowerUrl.includes('.jpg') || lowerUrl.includes('.jpeg') ||
             lowerUrl.includes('.png') || lowerUrl.includes('.gif') ||
             lowerUrl.includes('.bmp') || lowerUrl.includes('.webp');
    },
    /** 下载预览文件 */
    downloadPreviewFile() {
      if (!this.previewFileUrl) {
        this.$modal.msgWarning('文件URL不存在');
        return;
      }
      // 创建下载链接
      const link = document.createElement('a');
      link.href = this.previewFileUrl;
      link.download = this.previewFileName || '政策文件';
      link.target = '_blank';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    /** 获取文件完整URL */
    getFileUrl(filePath) {
      if (!filePath) {
        return '';
      }
      // 如果已经是完整URL，直接返回
      if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
        return filePath;
      }
      // 如果以 / 开头，说明是相对路径，需要拼接基础URL
      if (filePath.startsWith('/')) {
        return process.env.VUE_APP_BASE_API + filePath;
      }
      // 否则拼接基础URL和 /profile/upload/ 前缀（根据后端返回的格式）
      return process.env.VUE_APP_BASE_API + '/profile/upload/' + filePath;
    },
    /** 从路径提取文件名 */
    getFileNameFromPath(path) {
      if (!path) return '';
      const parts = path.split('/');
      return parts[parts.length - 1];
    }
  }
};
</script>

<style scoped lang="scss">
.subsidy-policy-page {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

.subsidy-policy-page ::v-deep button,
.subsidy-policy-page ::v-deep .el-button {
  font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
}

/* 搜索按钮使用蓝色背景 - 仅针对查询表单中的搜索按钮 */
.subsidy-policy-page ::v-deep .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.subsidy-policy-page ::v-deep .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.subsidy-policy-page ::v-deep .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 */
.subsidy-policy-page ::v-deep .action-row .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page ::v-deep .action-row .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page ::v-deep .action-row .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 统一功能按钮样式：白色背景、浅灰色边框、黑色字体（排除 primary 按钮和文本按钮） */
.subsidy-policy-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 覆盖 plain 按钮样式（排除 primary 按钮） */
.subsidy-policy-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
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

.app-container {
  .el-table {
    margin-top: 20px;
  }
}

.file-preview-dialog {
  .preview-container {
    width: 100%;
    height: 70vh;
    min-height: 500px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
  }

  .preview-iframe {
    width: 100%;
    height: 100%;
    border: none;
    background-color: #fff;
  }

  .office-preview {
    width: 100%;
    padding: 20px;
    text-align: center;
  }

  .image-preview {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #fff;
    overflow: auto;
  }

.detail-content-panel {
  max-height: 60vh;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fff;
  overflow: auto;
  line-height: 1.7;
}
}
</style>

<style>
/* 搜索按钮蓝色背景 - 全局样式确保生效（仅查询表单内） */
.subsidy-policy-page .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.subsidy-policy-page .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.subsidy-policy-page .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 - 全局样式 */
.subsidy-policy-page .action-row .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page .action-row .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.subsidy-policy-page .action-row .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}
</style>
