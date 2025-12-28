<template>
  <div class="app-container students-page">
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
      <el-form-item label="学年学期" prop="yearSemesterId">
        <el-select v-model="queryParams.yearSemesterId" placeholder="请选择学年学期" clearable style="width: 200px">
          <el-option
            v-for="item in yearSemesterOptions"
            :key="item.id"
            :label="`${item.schoolYear || ''} ${item.semesterLabel || ''}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="民族" prop="ethnicity">
        <el-select v-model="queryParams.ethnicity" placeholder="请选择民族" clearable>
          <el-option
            v-for="dict in dict.type.sys_student_ethnicity"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属年级" prop="gradeId">
        <el-select v-model="queryParams.gradeId" placeholder="请选择年级" clearable>
          <el-option
            v-for="item in allGradeOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
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
      <el-form-item label="脱贫户" prop="isPovertyReliefFamily">
        <el-select v-model="queryParams.isPovertyReliefFamily" placeholder="是否脱贫户" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
      </el-collapse-transition>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:students:add']"
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
          v-hasPermi="['system:students:edit']"
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
          v-hasPermi="['system:students:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:students:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-edit-outline"
          size="mini"
          :disabled="multiple"
          @click="handleBatchDifficulty"
          v-hasPermi="['system:students:edit']"
        >批量认定</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-section">
    <el-table v-loading="loading" :data="studentsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="姓名" align="center" prop="name" width="120">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="openStudentDetail(scope.row)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="160" show-overflow-tooltip />
      <el-table-column label="性别" align="center" prop="gender" width="70">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_student_gender" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="民族" align="center" prop="ethnicity" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_student_ethnicity" :value="scope.row.ethnicity"/>
        </template>
      </el-table-column>
      <el-table-column label="户籍所在地" align="center" prop="domicile" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ formatDomicile(scope.row.domicile) }}
        </template>
      </el-table-column>
      <el-table-column label="学籍号" align="center" prop="studentNo" width="140" show-overflow-tooltip />
      <el-table-column label="当前学年学期" align="center" width="150">
        <template slot-scope="scope">
          <span v-if="scope.row.currentSchoolYear">
            {{ scope.row.currentSchoolYear || '' }} {{ scope.row.currentSemesterLabel || '' }}
          </span>
          <span v-else style="color: #909399;">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="所属学制" align="center" prop="schoolingYears" width="100">
        <template slot-scope="scope">
          {{ scope.row.schoolingYears }}
        </template>
      </el-table-column>
      <el-table-column label="年级/班级" align="center" width="180">
        <template slot-scope="scope">
          {{ formatGradeAndClass(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="就读状态" align="center" prop="studyStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_study_status" :value="scope.row.studyStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="困难类型" align="center" prop="difficultyTypeId" width="220">
        <template slot-scope="scope">
          <span>{{ formatSupportInfo(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="困难等级" align="center" prop="difficultyLevelId" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.difficultyLevelId" class="difficulty-pill">
            {{ formatDifficultyLevel(scope.row.difficultyLevelId) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="memo" width="150" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:students:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:students:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    </div>

    </div>

    <!-- 添加或修改困难学生基础信息对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      :width="dialogWidth"
      append-to-body
      :close-on-click-modal="false"
      :class="activeTab === 'report' ? 'report-dialog' : ''"
    >
      <el-alert
        class="form-tip"
        title="提示：学生基础信息是全系统的唯一权威数据，请仔细确认后再保存。"
        type="info"
        show-icon
        :closable="false"
      />
      <div class="section-nav">
        <el-tabs v-model="activeTab" type="card" class="section-tabs">
          <el-tab-pane label="基本信息" name="basic" />
          <el-tab-pane label="家庭成员" name="family" />
          <el-tab-pane label="银行卡" name="bank" />
          <el-tab-pane label="助学金申请表" name="report" />
        </el-tabs>
      </div>
      <div class="form-scroll" ref="formScroll">
        <el-form ref="form" :model="form" :rules="rules" label-width="110px" :disabled="detailMode">
          <div v-show="activeTab === 'basic'">
          <!-- 学期信息 -->
          <el-card shadow="never" class="form-card" ref="semesterSection">
            <div slot="header" class="card-header">
              <i class="el-icon-date"></i>
              <span>学期信息</span>
            </div>
            <el-row :gutter="20">
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
          </el-card>

          <!-- 基本信息 -->
          <el-card shadow="never" class="form-card" ref="basicSection">
            <div slot="header" class="card-header">
              <i class="el-icon-user"></i>
              <span>基本信息</span>
            </div>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option
                      v-for="dict in dict.type.sys_student_gender"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
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
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCardNo">
                  <el-input v-model="form.idCardNo" placeholder="请输入18位身份证号" maxlength="18" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学籍号" prop="studentNo">
                  <el-input v-model="form.studentNo" placeholder="请输入学籍号" maxlength="32" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <!-- 户籍信息 -->
          <el-card shadow="never" class="form-card" ref="domicileSection">
            <div slot="header" class="card-header">
              <i class="el-icon-location"></i>
              <span>户籍信息</span>
            </div>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="户籍所在地" prop="domicile">
                  <div v-if="form.id && form.domicile && !isEditingAddress" class="domicile-preview">
                    <el-input
                      :value="form.domicile"
                      readonly
                      style="width: 70%"
                    />
                    <el-button
                      type="text"
                      icon="el-icon-edit"
                      style="margin-left: 10px;"
                      @click="isEditingAddress = true"
                    >修改地址</el-button>
                  </div>
                  <div v-else class="domicile-inputs">
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
          </el-card>

          <!-- 学籍信息 -->
          <el-card shadow="never" class="form-card" ref="academicSection">
            <div slot="header" class="card-header">
              <i class="el-icon-reading"></i>
              <span>学籍信息</span>
            </div>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="所属学制" prop="schoolingPlanId">
                  <el-select v-model="form.schoolingPlanId" placeholder="请选择学制" @change="handleSchoolPlanChange" style="width: 100%">
                    <el-option
                      v-for="item in schoolPlanOptions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="所属年级" prop="gradeId">
                  <el-select v-model="form.gradeId" placeholder="请选择年级" @change="handleGradeChange" style="width: 100%" :disabled="!form.schoolingPlanId">
                    <el-option
                      v-for="item in gradeOptions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="所属班级" prop="classId">
                  <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%" :disabled="!form.gradeId">
                    <el-option
                      v-for="item in classOptions"
                      :key="item.classId"
                      :label="item.className"
                      :value="item.classId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
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
            </el-row>
          </el-card>

          <!-- 困难认定 -->
          <el-card shadow="never" class="form-card" ref="difficultySection">
            <div slot="header" class="card-header">
              <i class="el-icon-warning-outline"></i>
              <span>困难认定</span>
            </div>
            <el-row :gutter="20">
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
            <el-row :gutter="20" v-if="showPovertyReliefYear">
              <el-col :span="12">
                <el-form-item label="脱贫年份" prop="povertyReliefYear">
                  <el-date-picker
                    v-model="povertyReliefYearString"
                    type="year"
                    placeholder="请选择脱贫年份"
                    value-format="yyyy"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </div>

        <div v-show="activeTab === 'family'">
        <!-- 家庭成员 -->
        <el-card shadow="never" class="form-card" ref="familySection">
          <div slot="header" class="card-header">
            <i class="el-icon-user-solid"></i>
            <span>家庭成员</span>
            <el-button v-if="!detailMode" type="text" icon="el-icon-plus" @click="addFamilyMember" style="margin-left: 12px;">添加成员</el-button>
          </div>
          <el-table :data="familyMembers" border size="small" style="width: 100%">
            <el-table-column label="姓名" align="center" width="140">
              <template slot-scope="scope">
                <el-input v-model="scope.row.name" placeholder="姓名" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="年龄" align="center" width="100">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.age" :min="0" :max="150" size="small" controls-position="right" style="width: 100%;" />
              </template>
            </el-table-column>
            <el-table-column label="关系" align="center" width="130">
              <template slot-scope="scope">
                <el-select v-model="scope.row.relation" placeholder="关系" size="small" style="width: 100%;">
                  <el-option v-for="item in relationOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="工作单位" align="center" min-width="160">
              <template slot-scope="scope">
                <el-input v-model="scope.row.employer" placeholder="工作单位" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="职业" align="center" min-width="130">
              <template slot-scope="scope">
                <el-input v-model="scope.row.occupation" placeholder="职业" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="健康状态" align="center" width="130">
              <template slot-scope="scope">
                <el-select v-model="scope.row.healthStatus" placeholder="健康状态" size="small" style="width: 100%;">
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
                <el-button v-if="!detailMode" type="text" size="mini" @click="removeFamilyMember(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="familyMembers.length === 0" style="margin-top: 8px; color: #909399; font-size: 13px;">
            暂无家庭成员<span v-if="!detailMode">，请点击“添加成员”。</span>
          </div>
        </el-card>
        </div>

        <div v-show="activeTab === 'bank'">
          <!-- 银行卡信息 -->
          <el-card shadow="never" class="form-card" ref="bankCardSection">
            <div slot="header" class="card-header">
              <i class="el-icon-bank-card"></i>
              <span>银行卡信息</span>
              <el-button v-if="!detailMode" type="text" icon="el-icon-plus" @click="addBankCard" style="margin-left: 12px;">添加银行卡</el-button>
            </div>
            <el-table :data="bankCards" border size="small" style="width: 100%">
              <el-table-column label="银行卡号" align="center" min-width="180">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankAccountNo" placeholder="请输入银行卡号" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="开户行" align="center" min-width="160">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankName" placeholder="请输入开户行" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="支行/网点" align="center" min-width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.branchName" placeholder="可选" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="开卡人" align="center" min-width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.accountHolder" placeholder="请输入开卡人" size="small" />
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
                    :disabled="detailMode"
                    size="small"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="100" fixed="right">
                <template slot-scope="scope">
                  <el-button v-if="!detailMode" type="text" size="mini" @click="removeBankCard(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="bankCards.length === 0" style="margin-top: 8px; color: #909399; font-size: 13px;">
              暂无银行卡信息<span v-if="!detailMode">，请点击“添加银行卡”。</span>
            </div>
          </el-card>
        </div>

        <div v-show="activeTab === 'report'">
          <!-- 助学金申请表 -->
          <el-card shadow="never" class="form-card report-card">
            <div slot="header" class="card-header">
              <i class="el-icon-document"></i>
              <span>普通高中国家助学金申请表</span>
              <div style="margin-left: auto; display: flex; gap: 10px;">
                <el-button
                  type="text"
                  icon="el-icon-view"
                  @click="openReportInNewWindow"
                  size="small"
                  :disabled="!reportUrl"
                >新窗口打开</el-button>
                <el-button
                  type="text"
                  icon="el-icon-printer"
                  @click="printReport"
                  size="small"
                  :disabled="!reportUrl"
                >打印</el-button>
                <el-button
                  type="text"
                  icon="el-icon-download"
                  @click="exportReport"
                  size="small"
                  :disabled="!reportUrl"
                >导出PDF</el-button>
              </div>
            </div>
            <div class="report-preview-container">
              <!-- 错误提示 -->
              <div v-if="reportError" class="report-error">
                <el-alert
                  title="报表加载失败"
                  type="error"
                  :description="reportError"
                  show-icon
                  :closable="false"
                >
                  <el-button slot="action" type="text" @click="retryLoadReport">重试</el-button>
                  <el-button slot="action" type="text" @click="openReportInNewWindow" style="margin-left: 10px;">新窗口打开</el-button>
                </el-alert>
              </div>
              <!-- iframe预览 -->
              <i-frame
                v-else-if="reportUrl"
                ref="reportIframe"
                :src="reportUrl"
                class="report-iframe-wrapper"
                @iframe-load="onReportIframeLoad"
              ></i-frame>
              <!-- 加载中或等待生成URL -->
              <div v-else-if="form.id" class="report-empty">
                <el-empty description="正在生成报表..." :image-size="100"></el-empty>
              </div>
            </div>
          </el-card>
        </div>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <template v-if="!detailMode">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </template>
        <template v-else>
          <el-button @click="cancel">关 闭</el-button>
        </template>
      </div>
    </el-dialog>

    <!-- 批量认定困难类型对话框 -->
    <el-dialog title="批量认定困难类型" :visible.sync="batchDifficultyOpen" width="500px" append-to-body>
      <el-form ref="batchDifficultyForm" :model="batchDifficultyForm" label-width="120px">
        <el-alert
          :title="`已选择 ${selectedStudentNames.length} 名学生，将批量更新选中学生的困难认定信息，未填写的字段将保持不变。`"
          type="info"
          show-icon
          :closable="false"
          style="margin-bottom: 15px"
        />
        <div v-if="selectedStudentNames.length > 0" style="margin-bottom: 20px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
          <div style="font-weight: 600; margin-bottom: 8px; color: #303133;">已选择的学生：</div>
          <div style="color: #606266; line-height: 1.8;">
            <span v-for="(name, index) in selectedStudentNames" :key="index" style="display: inline-block; margin-right: 10px;">
              {{ name }}<span v-if="index < selectedStudentNames.length - 1">、</span>
            </span>
          </div>
        </div>
        <el-form-item label="困难类型" prop="difficultyTypeId">
          <el-select v-model="batchDifficultyForm.difficultyTypeId" placeholder="请选择困难类型（留空则不更新）" clearable style="width: 100%">
            <el-option
              v-for="dict in dict.type.sys_difficulty_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="困难等级" prop="difficultyLevelId">
          <el-select v-model="batchDifficultyForm.difficultyLevelId" placeholder="请选择困难等级（留空则不更新）" clearable style="width: 100%">
            <el-option
              v-for="dict in dict.type.sys_difficulty_level"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否脱贫户" prop="isPovertyReliefFamily">
          <el-select v-model="batchDifficultyForm.isPovertyReliefFamily" placeholder="请选择（留空则不更新）" clearable style="width: 100%">
            <el-option label="是" value="1" />
            <el-option label="否" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="脱贫年份" prop="povertyReliefYear" v-if="batchDifficultyForm.isPovertyReliefFamily === '1'">
          <el-date-picker
            v-model="batchDifficultyForm.povertyReliefYear"
            type="year"
            placeholder="请选择脱贫年份"
            value-format="yyyy"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBatchDifficulty">确 定</el-button>
        <el-button @click="cancelBatchDifficulty">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listStudents, getStudents, delStudents, addStudents, updateStudents,
  getSchoolPlanList, getGradeList, getClassList, batchUpdateDifficulty
} from "@/api/system/students";
import {
  listStudentRecords, getStudentRecord, delStudentRecords, addStudentRecord, updateStudentRecord,
  listStudentsBase, getStudentsBase, addStudentsBase, updateStudentsBase
} from "@/api/system/studentRecord";
import { listYearSemesters } from "@/api/system/baseconfig";
import guangxiRegions from "@/assets/data/guangxi-region.js";
import iFrame from "@/components/iFrame/index";

export default {
  name: "Students",
  components: {
    iFrame
  },
  dicts: ['sys_student_gender', 'sys_student_ethnicity', 'sys_study_status', 'sys_difficulty_type', 'sys_difficulty_level', 'sys_health_status'],
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
      // 查询表单折叠
      queryFormCollapsed: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 困难学生基础信息表格数据
      studentsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 学年学期选项
      yearSemesterOptions: [],
      // 学制选项
      schoolPlanOptions: [],
      // 年级选项
      gradeOptions: [],
      // 所有年级选项（用于查询条件）
      allGradeOptions: [],
      // 班级选项
      classOptions: [],
      // 广西行政区划数据
      guangxiRegions: guangxiRegions,
      // 是否正在编辑地址
      isEditingAddress: false,
      // 是否显示脱贫年份字段
      showPovertyReliefYear: false,
      currentYearSemesterId: null,
      // 批量认定对话框
      batchDifficultyOpen: false,
      selectedStudentNames: [],
      batchDifficultyForm: {
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        povertyReliefYear: null
      },
      // 家庭成员
      relationOptions: [
        { value: '父亲', label: '父亲' },
        { value: '母亲', label: '母亲' },
        { value: '祖父母', label: '祖父母' },
        { value: '兄弟姐妹', label: '兄弟姐妹' },
        { value: '监护人', label: '监护人' },
        { value: '其他', label: '其他' }
      ],
      familyMembers: [],
      // 银行卡信息
      bankCards: [],
      bankCardRules: {
        bankAccountNo: [{ required: true, message: '银行卡号不能为空', trigger: 'blur' }],
        bankName: [{ required: true, message: '开户行不能为空', trigger: 'blur' }],
        accountHolder: [{ required: true, message: '开卡人不能为空', trigger: 'blur' }]
      },
      detailMode: false,
      // 报表相关
      reportUrl: '',
      reportId: '1159821341794144256',
      reportLoading: false,
      reportError: null,
      reportTimeout: null, // 超时定时器
      sectionAnchors: [
        { id: 'semester', label: '学期信息', icon: 'el-icon-date', ref: 'semesterSection' },
        { id: 'basic', label: '基本信息', icon: 'el-icon-user', ref: 'basicSection' },
        { id: 'domicile', label: '户籍信息', icon: 'el-icon-location', ref: 'domicileSection' },
        { id: 'academic', label: '学籍信息', icon: 'el-icon-reading', ref: 'academicSection' },
        { id: 'difficulty', label: '困难认定', icon: 'el-icon-warning-outline', ref: 'difficultySection' }
      ],
      activeTab: 'basic',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        yearSemesterId: null,
        name: null,
        idCardNo: null,
        ethnicity: null,
        gradeId: null,
        difficultyTypeId: null,
        isPovertyReliefFamily: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        yearSemesterId: [
          { required: true, message: "学年学期不能为空", trigger: "change" }
        ],
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { validator: this.validateIdCardNo, trigger: "blur" }
        ],
        gender: [
          { required: true, message: "性别不能为空", trigger: "blur" }
        ],
        ethnicity: [
          { required: true, message: "民族不能为空", trigger: "change" }
        ],
        domicile: [
          { required: true, message: "户籍所在地不能为空", trigger: "blur" }
        ],
        studentNo: [
          { required: true, message: "学籍号不能为空", trigger: "blur" },
          { validator: this.validateStudentNo, trigger: "blur" }
        ],
        schoolingPlanId: [
          { required: true, message: "所属学制不能为空", trigger: "blur" }
        ],
        gradeId: [
          { required: true, message: "所属年级不能为空", trigger: "blur" }
        ],
        classId: [
          { required: true, message: "所属班级不能为空", trigger: "blur" }
        ],
        studyStatus: [
          { required: true, message: "就读状态不能为空", trigger: "change" }
        ],
        difficultyLevelId: [
          { required: true, message: "困难等级不能为空", trigger: "change" }
        ],
        isPovertyReliefFamily: [
          { required: true, message: "请选择是否脱贫户", trigger: "change" }
        ],
        povertyReliefYear: [
          { validator: this.validatePovertyYear, trigger: "change" }
        ]
      }
    }
  },
  computed: {
    /** 对话框宽度：报表tab时使用更大宽度 */
    dialogWidth() {
      // 报表tab时使用95%宽度，其他tab使用1000px
      return this.activeTab === 'report' ? '95%' : '1000px'
    },
    /** 脱贫年份字符串（用于 el-date-picker，确保始终是字符串类型） */
    povertyReliefYearString: {
      get() {
        if (!this.form || !this.form.povertyReliefYear) {
          return null
        }
        // 确保返回字符串类型
        const value = this.form.povertyReliefYear
        return typeof value === 'string' ? value : String(value)
      },
      set(value) {
        // el-date-picker 返回的是字符串，直接赋值
        if (this.form) {
          this.form.povertyReliefYear = value
        }
      }
    }
  },
  watch: {
    // 监听Tab切换，切换到报表tab时自动生成URL
    activeTab(newVal) {
      if (newVal === 'report' && this.form && this.form.id && !this.reportUrl) {
        this.generateReportUrl(this.form.id)
      }
    }
  },
  created() {
    this.getList();
    this.getYearSemesterList();
    this.getSchoolPlanList();
    this.getAllGradeList();
    if (!this.reportId) {
      // 可以从环境变量或系统配置中获取
      // this.reportId = process.env.VUE_APP_REPORT_ID || ''
    }
  },
  methods: {
    /** 查询学年学期列表 */
    getYearSemesterList() {
      listYearSemesters({ status: 1 }).then(response => {
        this.yearSemesterOptions = response.rows || response.data || [];
        // 默认选中当前学期
        const currentSemester = this.yearSemesterOptions.find(item => item.isCurrent === 1);
        if (currentSemester) {
          this.currentYearSemesterId = currentSemester.id;
          if (!this.queryParams.yearSemesterId) {
            this.queryParams.yearSemesterId = currentSemester.id;
          }
          this.applyCurrentSemesterToForm();
        }
      });
    },
    /** 验证身份证号唯一性 */
    validateIdCardNo(rule, value, callback) {
      if (!value) {
        callback();
        return;
      }

      // 如果是编辑模式，且身份证号没有变化，则不验证
      if (this.form.id) {
        callback();
        return;
      }

      // 检查身份证号是否已存在
      listStudentsBase({ idCardNo: value }).then(response => {
        if (response.rows && response.rows.length > 0) {
          callback(new Error('该身份证号已存在，请检查'));
        } else {
          callback();
        }
      }).catch(() => {
        callback();
      });
    },
    /** 验证学籍号唯一性 */
    validateStudentNo(rule, value, callback) {
      if (!value) {
        callback();
        return;
      }

      // 如果是编辑模式，且学籍号没有变化，则不验证
      if (this.form.id) {
        callback();
        return;
      }

      // 检查学籍号是否已存在
      listStudentsBase({ studentNo: value }).then(response => {
        if (response.rows && response.rows.length > 0) {
          callback(new Error('该学籍号已存在，请检查'));
        } else {
          callback();
        }
      }).catch(() => {
        callback();
      });
    },
    validatePovertyYear(rule, value, callback) {
      // 如果困难类型是脱贫户类型，则脱贫年份必填
      // 使用计算属性的值进行验证
      const povertyYear = this.povertyReliefYearString
      if (this.form.difficultyTypeId && this.isPovertyReliefDifficultyType(this.form.difficultyTypeId) && !povertyYear) {
        callback(new Error('脱贫年份不能为空'));
      } else {
        callback();
      }
    },
    /** 查询困难学生基础信息列表 */
    getList() {
      this.loading = true
      listStudents(this.queryParams).then(response => {
        this.studentsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.detailMode = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        yearSemesterId: this.currentYearSemesterId,
        currentYearSemesterId: this.currentYearSemesterId,
        studentBaseId: null,
        name: null,
        idCardNo: null,
        gender: null,
        ethnicity: null,
        domicile: null,
        regionCodes: [],
        village: null,
        hamlet: null,
        studentNo: null,
        schoolingPlanId: null,
        gradeId: null,
        classId: null,
        studyStatus: null,
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: '0',
        povertyReliefYear: null,
        memo: null
      }
      this.isEditingAddress = false
      this.showPovertyReliefYear = false
      this.familyMembers = []
      this.bankCards = []
      // 重置报表相关状态
      if (this.reportTimeout) {
        clearTimeout(this.reportTimeout)
        this.reportTimeout = null
      }
      this.reportUrl = ''
      this.reportError = null
      // 重置时，如果困难类型是脱贫户，需要显示脱贫年份
      this.$nextTick(() => {
        if (this.form.difficultyTypeId) {
          const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
          this.showPovertyReliefYear = isPovertyReliefType
        }
      })
      this.applyCurrentSemesterToForm()
    },
    applyCurrentSemesterToForm() {
      if (!this.form) {
        this.form = {}
      }
      if (this.currentYearSemesterId && !this.form.yearSemesterId) {
        this.$set(this.form, 'yearSemesterId', this.currentYearSemesterId)
      }
      if (this.currentYearSemesterId && !this.form.currentYearSemesterId) {
        this.$set(this.form, 'currentYearSemesterId', this.currentYearSemesterId)
      }
    },
    formatSupportInfo(row) {
      if (!row) {
        return ''
      }
      const difficultyLabel = this.selectDictLabel(this.dict.type.sys_difficulty_type, row.difficultyTypeId)
      if (!difficultyLabel || difficultyLabel === '无') {
        return ''
      }
      // 如果困难类型是脱贫户类型，且存在脱贫年份，则在前面加上年份
      if (row.difficultyTypeId && this.isPovertyReliefDifficultyType(row.difficultyTypeId) && row.povertyReliefYear) {
        return `${row.povertyReliefYear}年${difficultyLabel}`
      }
      // 否则直接显示困难类型标签
      return difficultyLabel
    },
    formatGradeAndClass(row) {
      if (!row) {
        return '-'
      }
      const grade = row.gradeName || ''
      const clazz = row.className || ''
      const combined = `${grade}${clazz}`
      return combined || '-'
    },
    formatDifficultyLevel(value) {
      return this.selectDictLabel(this.dict.type.sys_difficulty_level, value) || value || '-'
    },
    scrollToSection(refName) {
      this.$nextTick(() => {
        const scrollWrapper = this.$refs.formScroll
        const target = this.$refs[refName]
        if (!scrollWrapper || !target) {
          return
        }
        const wrapperEl = scrollWrapper instanceof HTMLElement ? scrollWrapper : scrollWrapper.$el
        const targetEl = target.$el || target
        if (!wrapperEl || !targetEl) return
        const offset = targetEl.offsetTop - wrapperEl.offsetTop
        wrapperEl.scrollTo({
          top: offset,
          behavior: 'smooth'
        })
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.detailMode = false
      this.open = true
      this.title = "添加困难学生基础信息"
      // 清空级联选项
      this.gradeOptions = []
      this.classOptions = []
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.detailMode = false
      const id = row.id || this.ids
      getStudents(id).then(response => {
        this.form = response.data
        const resolvedSemesterId = this.form.currentYearSemesterId || this.form.yearSemesterId || this.currentYearSemesterId
        this.$set(this.form, 'yearSemesterId', resolvedSemesterId)
        this.$set(this.form, 'currentYearSemesterId', resolvedSemesterId || null)
        // 编辑时，默认显示原有地址，不显示级联选择器
        this.isEditingAddress = false
        // 将脱贫年份从数字转换为字符串（el-date-picker 需要字符串格式）
        if (this.form.povertyReliefYear !== null && this.form.povertyReliefYear !== undefined) {
          this.form.povertyReliefYear = String(this.form.povertyReliefYear)
        }
        // 判断是否显示脱贫年份（根据困难类型自动判断）
        if (this.form.difficultyTypeId) {
          const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
          this.showPovertyReliefYear = isPovertyReliefType
          if (isPovertyReliefType) {
            this.form.isPovertyReliefFamily = '1'
          } else {
            this.form.isPovertyReliefFamily = this.form.isPovertyReliefFamily || '0'
          }
        } else {
          this.form.isPovertyReliefFamily = this.form.isPovertyReliefFamily || '0'
          this.showPovertyReliefYear = false
        }
        // 如果有学制ID，加载对应的年级列表
        if (this.form.schoolingPlanId) {
          getGradeList(this.form.schoolingPlanId).then(res => {
            this.gradeOptions = res.data || []
          }).catch(error => {
            console.error('获取年级列表失败:', error)
            this.gradeOptions = []
          })
        }
        // 如果有年级ID，加载对应的班级列表
        if (this.form.gradeId) {
          getClassList(this.form.gradeId).then(res => {
            this.classOptions = res.data || []
          }).catch(error => {
            console.error('获取班级列表失败:', error)
            this.classOptions = []
          })
        }
        this.familyMembers = (response.data && response.data.familyMembers) ? response.data.familyMembers : []
        this.bankCards = (response.data && response.data.bankCards) ? response.data.bankCards : []
        this.open = true
        this.title = "修改困难学生基础信息"
      }).catch(error => {
        console.error('获取学生记录失败:', error)
        this.$modal.msgError('获取学生记录失败')
      })
    },
    /** 姓名列点击：打开详情（复用编辑弹窗） */
    openStudentDetail(row) {
      this.detailMode = true
      this.reset()
      const id = row.id || this.ids
      getStudents(id).then(response => {
        this.form = response.data
        const resolvedSemesterId = this.form.currentYearSemesterId || this.form.yearSemesterId || this.currentYearSemesterId
        this.$set(this.form, 'yearSemesterId', resolvedSemesterId)
        this.$set(this.form, 'currentYearSemesterId', resolvedSemesterId || null)
        if (this.form.povertyReliefYear !== null && this.form.povertyReliefYear !== undefined) {
          this.form.povertyReliefYear = String(this.form.povertyReliefYear)
        }
        if (this.form.difficultyTypeId) {
          const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
          this.showPovertyReliefYear = isPovertyReliefType
          if (isPovertyReliefType) {
            this.form.isPovertyReliefFamily = '1'
          } else {
            this.form.isPovertyReliefFamily = this.form.isPovertyReliefFamily || '0'
          }
        } else {
          this.form.isPovertyReliefFamily = this.form.isPovertyReliefFamily || '0'
          this.showPovertyReliefYear = false
        }
        if (this.form.schoolingPlanId) {
          getGradeList(this.form.schoolingPlanId).then(res => {
            this.gradeOptions = res.data || []
          }).catch(error => {
            console.error('获取年级列表失败:', error)
            this.gradeOptions = []
          })
        }
        if (this.form.gradeId) {
          getClassList(this.form.gradeId).then(res => {
            this.classOptions = res.data || []
          }).catch(error => {
            console.error('获取班级列表失败:', error)
            this.classOptions = []
          })
        }
        this.familyMembers = (response.data && response.data.familyMembers) ? response.data.familyMembers : []
        this.bankCards = (response.data && response.data.bankCards) ? response.data.bankCards : []
        // 生成报表预览URL
        this.generateReportUrl(id)
        this.open = true
        this.title = "学生详情（只读）"
      }).catch(error => {
        console.error('获取学生记录失败:', error)
        this.$modal.msgError('获取学生记录失败')
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.currentYearSemesterId = this.form.yearSemesterId || null
          // 根据困难类型自动设置 isPovertyReliefFamily
          if (this.form.difficultyTypeId) {
            const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
            this.form.isPovertyReliefFamily = isPovertyReliefType ? '1' : '0'
            // 如果不是脱贫户类型，清空脱贫年份
            if (!isPovertyReliefType) {
              this.form.povertyReliefYear = null
            } else if (this.form.povertyReliefYear) {
              // 如果是脱贫户类型且有脱贫年份，将字符串转换为数字（后端需要 Integer 类型）
              this.form.povertyReliefYear = parseInt(this.form.povertyReliefYear)
            }
          } else if (this.form.povertyReliefYear) {
            // 如果没有困难类型但有脱贫年份，也转换为数字
            this.form.povertyReliefYear = parseInt(this.form.povertyReliefYear)
          }
          // 只有在新增或修改地址时才拼接完整的户籍地址
          if (!this.form.id || this.isEditingAddress) {
            if (this.form.domicile) {
              let fullAddress = this.form.domicile;
              if (this.form.village) {
                fullAddress += this.form.village;
              }
              if (this.form.hamlet) {
                fullAddress += this.form.hamlet;
              }
              this.form.domicile = fullAddress;
            }
          }

          // 绑定家庭成员并进行简单校验
          if (this.familyMembers && this.familyMembers.length > 0) {
            const invalid = this.familyMembers.find(item => !item.name || !item.relation)
            if (invalid) {
              this.$modal.msgError('家庭成员姓名和关系不能为空')
              return
            }
          }
          this.form.familyMembers = this.familyMembers

          // 绑定银行卡并进行简单校验
          if (this.bankCards && this.bankCards.length > 0) {
            const invalidCard = this.bankCards.find(item => !item.bankAccountNo || !item.bankName || !item.accountHolder)
            if (invalidCard) {
              this.$modal.msgError('银行卡号、开户行、开卡人均不能为空')
              return
            }
          }
          this.form.bankCards = this.bankCards

          if (this.form.id != null) {
            updateStudents(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addStudents(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 添加家庭成员行 */
    addFamilyMember() {
      this.familyMembers.push({
        name: '',
        age: null,
        relation: '',
        employer: '',
        occupation: '',
        healthStatus: ''
      })
    },
    /** 移除家庭成员行 */
    removeFamilyMember(index) {
      this.familyMembers.splice(index, 1)
    },
    addBankCard() {
      this.bankCards.push({
        bankAccountNo: '',
        bankName: '',
        branchName: '',
        accountHolder: this.form.name || '',
        isPrimary: this.bankCards.length === 0 ? 1 : 0,
        status: 0
      })
    },
    handlePrimaryChange(index, val) {
      if (val === 1) {
        this.bankCards = this.bankCards.map((item, idx) => ({
          ...item,
          isPrimary: idx === index ? 1 : 0
        }))
      }
    },
    removeBankCard(index) {
      this.bankCards.splice(index, 1)
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除困难学生基础信息编号为"' + ids + '"的数据项？').then(function() {
        return delStudents(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/students/export', {
        ...this.queryParams
      }, `students_${new Date().getTime()}.xlsx`)
    },
    /** 获取学制列表 */
    getSchoolPlanList() {
      getSchoolPlanList().then(response => {
        this.schoolPlanOptions = response.data;
      });
    },
    /** 获取所有年级列表（用于查询条件） */
    getAllGradeList() {
      // 调用基础配置的年级接口获取所有年级
      import('@/api/system/baseconfig').then(module => {
        module.listGrades().then(response => {
          this.allGradeOptions = response.data;
        });
      });
    },
    /** 学制改变时触发 */
    handleSchoolPlanChange(value) {
      // 清空年级和班级选择
      this.form.gradeId = null;
      this.form.classId = null;
      this.gradeOptions = [];
      this.classOptions = [];

      // 如果选择了学制，则加载对应的年级列表
      if (value) {
        getGradeList(value).then(response => {
          this.gradeOptions = response.data || [];
        }).catch(error => {
          console.error('获取年级列表失败:', error);
          this.$modal.msgError('获取年级列表失败');
        });
      }
    },
    /** 年级改变时触发 */
    handleGradeChange(value) {
      this.form.classId = null;
      this.classOptions = [];
      if (value) {
        getClassList(value).then(response => {
          this.classOptions = response.data;
        });
      }
    },
    /** 行政区划改变时触发 */
    handleRegionChange(value) {
      // value 是数组，如 ['450100', '450102', '450102001']
      if (value && value.length > 0) {
        // 获取选中的各级名称
        const labels = this.getRegionLabels(value);
        // 拼接完整地址：广西壮族自治区 + 市 + 县 + 乡镇
        const baseAddress = '广西壮族自治区' + labels.join('');
        // 如果有村和屯，会在提交时拼接
        this.form.domicile = baseAddress;
      } else {
        this.form.domicile = null;
      }
    },
    /** 获取区划名称 */
    getRegionLabels(codes) {
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
    /** 困难类型变化 */
    handleDifficultyTypeChange(value) {
      // 检查选择的困难类型是否是脱贫户类型
      const isPovertyReliefType = this.isPovertyReliefDifficultyType(value)
      if (isPovertyReliefType) {
        // 如果是脱贫户类型，自动设置 isPovertyReliefFamily 为 '1'
        this.form.isPovertyReliefFamily = '1'
        this.showPovertyReliefYear = true
      } else {
        // 如果不是脱贫户类型，清空脱贫相关字段
        this.form.isPovertyReliefFamily = '0'
        this.showPovertyReliefYear = false
        this.form.povertyReliefYear = null
      }
    },
    /** 判断困难类型是否是脱贫户类型 */
    isPovertyReliefDifficultyType(difficultyTypeId) {
      if (!difficultyTypeId) {
        return false
      }
      // 从字典中查找对应的困难类型
      const difficultyType = this.dict.type.sys_difficulty_type.find(dict => dict.value === difficultyTypeId)
      if (!difficultyType) {
        return false
      }
      // 判断标签或值中是否包含"脱贫"关键字
      const label = difficultyType.label || ''
      const value = difficultyType.value || ''
      return label.includes('脱贫') || value.includes('poverty') || value.includes('脱贫')
    },
    /** 格式化户籍所在地，隐藏"自治区"等字样 */
    formatDomicile(domicile) {
      if (!domicile) return ''
      // 移除常见的行政区划后缀和民族名称
      return domicile
        .replace(/壮族/g, '')
        .replace(/回族/g, '')
        .replace(/维吾尔/g, '')
        .replace(/自治区/g, '')
        .replace(/省/g, '')
        .replace(/特别行政区/g, '')
    },
    /** 批量认定困难类型 */
    handleBatchDifficulty() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning('请先选择要认定的学生');
        return;
      }
      // 根据选中的ids获取学生名字
      this.selectedStudentNames = this.studentsList
        .filter(student => this.ids.includes(student.id))
        .map(student => student.name)
        .filter(name => name) // 过滤空名字
      this.batchDifficultyForm = {
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        povertyReliefYear: null
      }
      this.batchDifficultyOpen = true
    },
    /** 提交批量认定 */
    submitBatchDifficulty() {
      this.$refs["batchDifficultyForm"].validate(valid => {
        if (valid) {
          // 验证脱贫年份
          if (this.batchDifficultyForm.isPovertyReliefFamily === '1' && !this.batchDifficultyForm.povertyReliefYear) {
            this.$modal.msgError('选择脱贫户时必须填写脱贫年份');
            return;
          }

          const params = {
            ids: this.ids,
            difficultyTypeId: this.batchDifficultyForm.difficultyTypeId || null,
            difficultyLevelId: this.batchDifficultyForm.difficultyLevelId || null,
            isPovertyReliefFamily: this.batchDifficultyForm.isPovertyReliefFamily || null,
            povertyReliefYear: this.batchDifficultyForm.povertyReliefYear ? parseInt(this.batchDifficultyForm.povertyReliefYear) : null
          }

          batchUpdateDifficulty(params).then(response => {
            this.$modal.msgSuccess('批量认定成功')
            this.batchDifficultyOpen = false
            this.getList()
          }).catch(() => {
            this.$modal.msgError('批量认定失败')
          })
        }
      })
    },
    /** 取消批量认定 */
    cancelBatchDifficulty() {
      this.batchDifficultyOpen = false
      this.selectedStudentNames = []
      this.batchDifficultyForm = {
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        povertyReliefYear: null
      }
    },
    /** 生成报表预览URL */
    generateReportUrl(studentId) {
      if (!studentId) {
        this.reportUrl = ''
        this.reportError = null
        return
      }

      const reportId = this.reportId

      if (!reportId) {
        this.reportError = '报表ID未配置，请联系管理员配置报表ID'
        this.reportUrl = ''
        return
      }

      try {
        const baseUrl = process.env.NODE_ENV === 'development'
          ? 'http://localhost:8080'
          : (process.env.VUE_APP_BASE_API || window.location.origin)

        this.reportUrl = `${baseUrl}/jmreport/view/${reportId}?studentId=${studentId}`
        this.reportError = null

        if (this.reportTimeout) {
          clearTimeout(this.reportTimeout)
          this.reportTimeout = null
        }
      } catch (error) {
        console.error('生成报表URL失败:', error)
        this.reportError = '生成报表URL失败: ' + (error.message || '未知错误')
        this.reportUrl = ''
      }
    },
    /** iframe加载完成事件 */
    onReportIframeLoad() {
      if (this.reportTimeout) {
        clearTimeout(this.reportTimeout)
        this.reportTimeout = null
      }
      this.reportError = null
    },
    /** 重试加载报表 */
    retryLoadReport() {
      if (this.form && this.form.id) {
        this.reportError = null
        this.generateReportUrl(this.form.id)
      }
    },
    /** 新窗口打开报表 */
    openReportInNewWindow() {
      if (this.reportUrl) {
        window.open(this.reportUrl, '_blank', 'width=1200,height=800,scrollbars=yes')
      } else {
        this.$modal.msgWarning('报表URL未生成，请稍后再试')
      }
    },
    /** 打印报表 */
    printReport() {
      // 由于iframe跨域限制，建议在新窗口中打印
      if (this.reportUrl) {
        const printWindow = window.open(this.reportUrl, '_blank', 'width=1200,height=800')
        if (printWindow) {
          printWindow.onload = () => {
            setTimeout(() => {
              printWindow.print()
            }, 500)
          }
        } else {
          this.$modal.msgWarning('无法打开新窗口，请检查浏览器弹窗设置')
        }
      } else {
        this.$modal.msgWarning('报表URL未生成，请稍后再试')
      }
    },
    /** 导出PDF */
    exportReport() {
      if (!this.form || !this.form.id) {
        this.$modal.msgWarning('学生信息不存在')
        return
      }

      // 方式1：如果积木报表支持导出，可以直接调用
      if (this.reportUrl) {
        // 在URL后添加导出参数
        const exportUrl = this.reportUrl + '&export=pdf'
        window.open(exportUrl, '_blank')
      } else {
        this.$modal.msgWarning('报表URL未生成，请稍后再试')
      }

      // 方式2：如果需要后端接口导出（推荐）
      // this.download(`/system/report/export/${this.reportId}`, {
      //   studentId: this.form.id,
      //   format: 'pdf'
      // }, `助学金申请表_${this.form.name}_${new Date().getTime()}.pdf`)
    }
  }
}
</script>

<style scoped lang="scss">
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

.table-section {
  margin-top: 12px;
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

/* 搜索按钮使用蓝色背景 */
.students-page ::v-deep .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.students-page ::v-deep .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.students-page ::v-deep .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 */
.students-page ::v-deep .action-row .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .action-row .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .action-row .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 统一功能按钮：白底、浅灰边、黑字（排除搜索、文本、折叠） */
.students-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

// 表单卡片样式
.form-card {
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;

  ::v-deep .el-card__header {
    padding: 12px 20px;
    background: #f5f7fa;
    border-bottom: 1px solid #e4e7ed;
  }

  ::v-deep .el-card__body {
    padding: 20px;
  }

  &:last-of-type {
    margin-bottom: 0;
  }
}

.section-tabs {
  margin-bottom: 14px;
  ::v-deep .el-tabs__header {
    border-bottom: 1px solid #e4e7ed;
    margin: 0 0 4px 0;
  }
  ::v-deep .el-tabs__item {
    border: none;
    border-bottom: 2px solid transparent;
    border-radius: 0;
    margin-right: 18px;
    padding: 10px 6px;
    background: transparent;
    color: #606266;
    font-weight: 500;
    transition: color 0.2s ease, border-color 0.2s ease;
  }
  ::v-deep .el-tabs__item.is-active {
    border-bottom-color: #409EFF;
    color: #409EFF;
    font-weight: 600;
  }
  ::v-deep .el-tabs__item:hover {
    color: #409EFF;
  }
}

.form-tip {
  margin-bottom: 15px;
}

.section-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
  background: #f5f7fa;
  padding: 8px 12px;
  border-radius: 6px;
}

.section-nav .el-button {
  color: #1890ff;
  padding: 0 8px;
  font-size: 13px;
}

.form-scroll {
  max-height: 520px;
  overflow-y: auto;
  padding-right: 8px;
}

.domicile-preview,
.domicile-inputs {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
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

.card-header {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;

  i {
    margin-right: 8px;
    font-size: 16px;
    color: #409EFF;
  }
}

// 对话框样式优化
::v-deep .el-dialog__body {
  padding: 20px;
  max-height: 65vh;
  overflow-y: auto;
}

::v-deep .el-dialog__footer {
  padding: 15px 20px;
  text-align: right;
  border-top: 1px solid #e4e7ed;
}

// 报表对话框特殊样式
::v-deep .report-dialog {
  .el-dialog__body {
    max-height: 85vh; /* 报表对话框使用更大的高度，与报表容器高度匹配 */
    padding: 15px;
  }

  .el-dialog {
    margin-top: 3vh !important; /* 减小顶部边距，增大可用空间 */
  }
}

// 表单项间距
::v-deep .el-form-item {
  margin-bottom: 18px;
}

// 响应式设计
@media (max-width: 768px) {
  ::v-deep .el-dialog {
    width: 95% !important;
  }
}

// 报表预览相关样式
.report-card {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.report-preview-container {
  width: 100%;
  min-height: 85vh; /* 增加高度到85vh */
  height: 85vh;
  position: relative;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: auto; /* 改为auto，允许横向滚动 */
  background: #f5f7fa;

  .report-iframe-wrapper {
    width: 100%;
    max-width: 100%; /* 限制最大宽度，不超过容器 */
    height: 100%;
    min-height: 600px;
    border: none;
    background: #fff;

    ::v-deep > div {
      width: 100%;
      max-width: 100%;
      height: 100%;
    }

    ::v-deep iframe {
      width: 100%;
      max-width: 100%; /* 不再强制最小宽度，让报表自适应 */
      height: 100%;
      min-height: 600px;
    }
  }

  .report-error {
    padding: 20px;
    height: 100%;
    min-height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .report-empty {
    height: 100%;
    min-height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  @media (max-width: 768px) {
    height: 400px;
    min-height: 400px;

    .report-iframe-wrapper {
      min-width: 100%;

      ::v-deep iframe {
        min-width: 100%;
      }
    }
  }

  @media (max-width: 768px) {
    height: 400px;
  }
}
</style>
