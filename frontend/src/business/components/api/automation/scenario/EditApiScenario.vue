<template>
  <el-card class="card-content">
    <div class="ms-main-div">
      <el-row>
        <el-col>
          <!--操作按钮-->
          <div class="ms-opt-btn">
            <el-button type="primary" size="small" @click="editScenario">{{$t('commons.save')}}</el-button>
          </div>
        </el-col>
      </el-row>
      <div class="tip">{{$t('test_track.plan_view.base_info')}}</div>
      <el-form :model="currentScenario" label-position="right" label-width="80px" size="small" :rules="rules" ref="currentScenario" style="margin-right: 20px">
        <!-- 基础信息 -->
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('commons.name')" prop="name">
              <el-input class="ms-scenario-input" size="small" v-model="currentScenario.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('test_track.module.module')" prop="apiScenarioModuleId">
              <el-select class="ms-scenario-input" size="small" v-model="currentScenario.apiScenarioModuleId">
                <el-option v-for="item in moduleOptions" :key="item.id" :label="item.path" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('commons.status')" prop="status">
              <el-select class="ms-scenario-input" size="small" v-model="currentScenario.status">
                <el-option v-for="item in options" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('api_test.definition.request.responsible')" prop="principal">
              <el-select v-model="currentScenario.principal"
                         :placeholder="$t('api_test.definition.request.responsible')" filterable size="small"
                         class="ms-scenario-input">
                <el-option
                  v-for="item in maintainerOptions"
                  :key="item.id"
                  :label="item.id + ' (' + item.name + ')'"
                  :value="item.id">
                </el-option>
              </el-select>

            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('test_track.case.priority')" prop="level">
              <el-select class="ms-scenario-input" size="small" v-model="currentScenario.level">
                <el-option v-for="item in levels" :key="item.id" :label="item.label" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('api_test.automation.follow_people')" prop="followPeople">
              <el-select v-model="currentScenario.followPeople"
                         :placeholder="$t('api_test.automation.follow_people')" filterable size="small"
                         class="ms-scenario-input">
                <el-option
                  v-for="item in maintainerOptions"
                  :key="item.id"
                  :label="item.id + ' (' + item.name + ')'"
                  :value="item.id">
                </el-option>
              </el-select>

            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="Tag" prop="tagId">
              <el-select v-model="currentScenario.tagId" size="small" class="ms-scenario-input" placeholder="Tag" :multiple="true">
                <el-option
                  v-for="item in tags"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"/>
                <el-button size="mini" type="primary" @click="openTagConfig" class="ms-scenario-button">
                  {{ $t('api_test.automation.create_tag') }}
                </el-button>
                <template v-slot:empty>
                  <div>
                    <el-button size="mini" type="primary" @click="openTagConfig" class="ms-scenario-button">
                      {{ $t('api_test.automation.create_tag') }}
                    </el-button>
                  </div>
                </template>
              </el-select>

            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('commons.description')" prop="description">
              <el-input class="ms-http-textarea"
                        v-model="currentScenario.description"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 10}"
                        :rows="2" size="small"/>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>

      <!-- 场景步骤-->
      <div v-loading="loading">
        <p class="tip">{{$t('api_test.automation.scenario_step')}} </p>
        <el-row>
          <el-col :span="21">
            <!-- 调试部分 -->
            <div class="ms-debug-div">
              <el-row style="margin: 5px">
                <el-col :span="6" class="ms-col-one">
                  {{currentScenario.name ===undefined || ''? $t('api_test.scenario.name') : currentScenario.name}}
                </el-col>
                <el-col :span="4" class="ms-col-one">
                  {{$t('api_test.automation.step_total')}}：{{scenarioDefinition.length}}
                </el-col>
                <el-col :span="4" class="ms-col-one">
                  <el-link style="font-size: 13px" @click="showScenarioParameters">{{$t('api_test.automation.scenario_total')}}：
                    {{this.currentScenario.variables!=undefined?this.currentScenario.variables.length-1: 0}}
                  </el-link>
                </el-col>
                <el-col :span="8">
                  {{$t('api_test.definition.request.run_env')}}:
                  <el-select v-model="currentEnvironmentId" size="small" class="ms-htt-width"
                             :placeholder="$t('api_test.definition.request.run_env')"
                             clearable>
                    <el-option v-for="(environment, index) in environments" :key="index"
                               :label="environment.name + (environment.config.httpConfig.socket ? (': ' + environment.config.httpConfig.protocol + '://' + environment.config.httpConfig.socket) : '')"
                               :value="environment.id"/>
                    <el-button class="ms-scenario-button" size="mini" type="primary" @click="openEnvironmentConfig">
                      {{ $t('api_test.environment.environment_config') }}
                    </el-button>
                    <template v-slot:empty>
                      <div class="empty-environment">
                        <el-button class="ms-scenario-button" size="mini" type="primary" @click="openEnvironmentConfig">
                          {{ $t('api_test.environment.environment_config') }}
                        </el-button>
                      </div>
                    </template>
                  </el-select>
                </el-col>
                <el-col :span="2">
                  <el-button size="small" type="primary" @click="runDebug">{{$t('api_test.request.debug')}}</el-button>
                </el-col>
              </el-row>
            </div>
            <!-- 场景步骤内容 -->
            <div style="margin-top: 10px" v-loading="loading">
              <el-tree node-key="resourceId" :props="props" :data="scenarioDefinition"
                       :default-expanded-keys="expandedNode"
                       :expand-on-click-node="false"
                       @node-expand="nodeExpand"
                       @node-collapse="nodeCollapse"
                       :allow-drop="allowDrop" @node-drag-end="allowDrag" @node-click="nodeClick" v-if="!loading" draggable>
                 <span class="custom-tree-node father" slot-scope="{ node, data}" style="width: 96%">
                    <template>
                      <!-- 场景 -->
                      <ms-api-scenario-component v-if="data.type==='scenario'" :scenario="data" :node="node" @remove="remove" @copyRow="copyRow"/>
                      <!--条件控制器-->
                      <ms-if-controller :controller="data" :node="node" v-if="data.type==='IfController'" @remove="remove" @copyRow="copyRow"/>
                      <!--等待控制器-->
                      <ms-constant-timer :timer="data" :node="node" v-if="data.type==='ConstantTimer'" @remove="remove" @copyRow="copyRow"/>
                      <!--自定义脚本-->
                      <ms-jsr233-processor v-if="data.type==='JSR223Processor'" @remove="remove" @copyRow="copyRow" :title="$t('api_test.automation.customize_script')"
                                           style-type="color: #7B4D12;background-color: #F1EEE9" :jsr223-processor="data" :node="node"/>
                      <!--前置脚本-->
                      <ms-jsr233-processor v-if="data.type==='JSR223PreProcessor'" @remove="remove" @copyRow="copyRow" :title="$t('api_test.definition.request.pre_script')"
                                           style-type="color: #B8741A;background-color: #F9F1EA" :jsr223-processor="data" :node="node"/>
                      <!--后置脚本-->
                      <ms-jsr233-processor v-if="data.type==='JSR223PostProcessor'" @remove="remove" @copyRow="copyRow" :title="$t('api_test.definition.request.post_script')"
                                           style-type="color: #783887;background-color: #F2ECF3" :jsr223-processor="data" :node="node"/>
                      <!--断言规则-->
                      <ms-api-assertions v-if="data.type==='Assertions'" @remove="remove" @copyRow="copyRow" customizeStyle="margin-top: 0px" :assertions="data" :node="node"/>
                      <!--提取规则-->
                      <ms-api-extract @remove="remove" @copyRow="copyRow" v-if="data.type==='Extract'" customizeStyle="margin-top: 0px" :extract="data" :node="node"/>
                      <!--API 导入 -->
                      <ms-api-component :request="data" :currentEnvironmentId="currentEnvironmentId" @remove="remove" @copyRow="copyRow" v-if="data.type==='HTTPSamplerProxy'||data.type==='DubboSampler'||data.type==='JDBCSampler'||data.type==='TCPSampler'" :node="node"/>
                    </template>
                   </span>
              </el-tree>
            </div>
          </el-col>
          <!-- 按钮列表 -->
          <div>
            <el-col :span="3" class="ms-left-cell">
              <el-button type="primary" icon="el-icon-refresh" size="small" @click="showAll">{{$t('commons.show_all')}}</el-button>
              <br/>
              <div v-if="operatingElements.indexOf('HTTPSamplerProxy')>0 || operatingElements.indexOf('DubboSampler')>0 || operatingElements.indexOf('JDBCSampler')>0 || operatingElements.indexOf('TCPSampler')>0 ">
                <el-button class="ms-right-buttion ms-btn-1" size="small" @click="apiListImport">+{{$t('api_test.automation.api_list_import')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('OT_IMPORT')>0">
                <el-button class="ms-right-buttion" size="small" style="color: #409EFF;background-color: #EEF5FE" @click="addComponent('OT_IMPORT')">+{{$t('api_test.automation.external_import')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('ConstantTimer')>0">
                <el-button class="ms-right-buttion ms-btn-3" size="small" @click="addComponent('ConstantTimer')">+{{$t('api_test.automation.wait_controller')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('IfController')>0">
                <el-button class="ms-right-buttion ms-btn-4" size="small" @click="addComponent('IfController')">+{{$t('api_test.automation.if_controller')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('scenario')===0">
                <el-button class="ms-right-buttion ms-btn-5" size="small" @click="addComponent('scenario')">+{{$t('api_test.automation.scenario_import')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('JSR223Processor')>0">
                <el-button class="ms-right-buttion ms-btn-6" size="small" @click="addComponent('JSR223Processor')">+{{$t('api_test.automation.customize_script')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('CustomizeReq')>0">
                <el-button class="ms-right-buttion ms-btn-7" size="small" @click="addComponent('CustomizeReq')">+{{$t('api_test.automation.customize_req')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('JSR223PreProcessor')>0">
                <el-button class="ms-right-buttion ms-btn-8" size="small" @click="addComponent('JSR223PreProcessor')">+{{$t('api_test.definition.request.pre_script')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('JSR223PostProcessor')>0">
                <el-button class="ms-right-buttion ms-btn-9" size="small" @click="addComponent('JSR223PostProcessor')">+{{$t('api_test.definition.request.post_script')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('Assertions')>0">
                <el-button class="ms-right-buttion ms-btn-10" size="small" @click="addComponent('Assertions')">+{{$t('api_test.definition.request.assertions_rule')}}</el-button>
              </div>
              <div v-if="operatingElements.indexOf('Extract')>0">
                <el-button class="ms-right-buttion ms-btn-11" size="small" @click="addComponent('Extract')">+{{$t('api_test.definition.request.extract_param')}}</el-button>
              </div>
            </el-col>
          </div>
        </el-row>
      </div>

      <!--接口列表-->
      <el-drawer :visible.sync="apiListVisible" :destroy-on-close="true" direction="ltr" :withHeader="false" :title="$t('api_test.automation.api_list_import')" :modal="false" size="90%">
        <ms-api-definition :visible="visibleRef" :currentRow="currentRow"/>
        <el-button style="float: right;margin: 0px 20px 0px" type="primary" @click="pushApiOrCase('REF')">{{$t('api_test.scenario.reference')}}</el-button>
        <el-button style="float: right;" type="primary" @click="pushApiOrCase('Copy')">{{ $t('commons.copy') }}</el-button>
      </el-drawer>

      <!--自定义接口-->
      <el-drawer :visible.sync="customizeVisible" :destroy-on-close="true" direction="ltr" :withHeader="false" :title="$t('api_test.automation.customize_req')" style="overflow: auto" :modal="false" size="90%">
        <ms-api-customize :request="customizeRequest" @addCustomizeApi="addCustomizeApi"/>
        <el-button style="float: right;margin: 20px" @click="addCustomizeApi">{{$t('commons.save')}}</el-button>
      </el-drawer>
      <!--场景导入 -->
      <el-drawer :visible.sync="scenarioVisible" :destroy-on-close="true" direction="ltr" :withHeader="false" :title="$t('api_test.automation.scenario_import')" style="overflow: auto" :modal="false" size="90%">
        <ms-import-api-scenario @addScenario="addScenario"/>
      </el-drawer>

      <!-- 环境 -->
      <api-environment-config ref="environmentConfig" @close="environmentConfigClose"/>

      <!--TAG-->
      <ms-add-tag @refreshTags="refreshTags" ref="tag"/>
      <!--执行组件-->
      <ms-run :debug="true" :environment="currentEnvironmentId" :reportId="reportId" :run-data="debugData"
              @runRefresh="runRefresh" ref="runTest"/>
      <!-- 调试结果 -->
      <el-drawer :visible.sync="debugVisible" :destroy-on-close="true" direction="ltr" :withHeader="true" :modal="false" size="90%">
        <ms-api-report-detail :report-id="reportId" :debug="true" :currentProjectId="projectId"/>
      </el-drawer>

      <!--场景公共参数-->
      <ms-scenario-parameters :currentScenario="currentScenario" @addParameters="addParameters" ref="scenarioParameters"/>
      <!--外部导入-->
      <api-import ref="apiImport" :saved="false" @refresh="apiImport"/>
    </div>
  </el-card>
</template>

<script>
  import {API_STATUS, PRIORITY} from "../../definition/model/JsonData";
  import {WORKSPACE_ID} from '@/common/js/constants';
  import {Assertions, Extract, IfController, JSR223Processor, ConstantTimer} from "../../definition/model/ApiTestModel";
  import MsJsr233Processor from "./Jsr233Processor";
  import {parseEnvironment} from "../../definition/model/EnvironmentModel";
  import MsConstantTimer from "./ConstantTimer";
  import MsIfController from "./IfController";
  import MsApiAssertions from "../../definition/components/assertion/ApiAssertions";
  import MsApiExtract from "../../definition/components/extract/ApiExtract";
  import MsApiDefinition from "./api/ApiDefinition";
  import MsApiComponent from "./ApiComponent";
  import {ELEMENTS, ELEMENT_TYPE} from "./Setting";
  import MsApiCustomize from "./ApiCustomize";
  import {getUUID, getCurrentProjectID} from "@/common/js/utils";
  import ApiEnvironmentConfig from "../../definition/components/environment/ApiEnvironmentConfig";
  import MsAddTag from "./AddTag";
  import MsRun from "./DebugRun";
  import MsImportApiScenario from "./ImportApiScenario";
  import MsApiScenarioComponent from "./ApiScenarioComponent";
  import MsApiReportDetail from "../report/ApiReportDetail";
  import MsScenarioParameters from "./ScenarioParameters";
  import ApiImport from "../../definition/components/import/ApiImport";

  export default {
    name: "EditApiScenario",
    props: {
      moduleOptions: Array,
      currentScenario: {},
    },
    components: {
      ApiEnvironmentConfig,
      MsScenarioParameters,
      MsApiReportDetail,
      MsAddTag, MsRun,
      MsApiScenarioComponent,
      MsImportApiScenario,
      MsJsr233Processor,
      MsConstantTimer,
      MsIfController,
      MsApiAssertions,
      MsApiExtract,
      MsApiDefinition,
      MsApiComponent,
      MsApiCustomize,
      ApiImport,
    },
    data() {
      return {
        props: {
          label: "label",
          children: "hashTree"
        },
        rules: {
          name: [
            {required: true, message: this.$t('test_track.case.input_name'), trigger: 'blur'},
            {max: 50, message: this.$t('test_track.length_less_than') + '50', trigger: 'blur'}
          ],
          userId: [{required: true, message: this.$t('test_track.case.input_maintainer'), trigger: 'change'}],
          apiScenarioModuleId: [{required: true, message: this.$t('test_track.case.input_module'), trigger: 'change'}],
          status: [{required: true, message: this.$t('commons.please_select'), trigger: 'change'}],
          principal: [{required: true, message: this.$t('api_test.definition.request.responsible'), trigger: 'change'}],
        },
        environments: [],
        tags: [],
        currentEnvironmentId: "",
        maintainerOptions: [],
        value: API_STATUS[0].id,
        options: API_STATUS,
        levels: PRIORITY,
        scenario: {},
        loading: false,
        apiListVisible: false,
        customizeVisible: false,
        scenarioVisible: false,
        debugVisible: false,
        customizeRequest: {protocol: "HTTP", type: "API", hashTree: [], referenced: 'Created', active: false},
        operatingElements: [],
        currentRow: {cases: [], apis: [], referenced: true},
        selectedTreeNode: undefined,
        expandedNode: [],
        scenarioDefinition: [],
        path: "/api/automation/create",
        debugData: {},
        reportId: "",
        projectId: "",
        visibleRef: "",
      }
    },
    created() {
      if (!this.currentScenario.apiScenarioModuleId) {
        this.currentScenario.apiScenarioModuleId = "";
      }
      this.projectId = getCurrentProjectID();
      this.operatingElements = ELEMENTS.get("ALL");
      this.getMaintainerOptions();
      this.refreshTags();
      this.getApiScenario();
      this.getEnvironments();
    },
    watch: {},
    methods: {
      addComponent(type) {
        switch (type) {
          case ELEMENT_TYPE.IfController:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new IfController()) :
              this.scenarioDefinition.push(new IfController());
            break;
          case ELEMENT_TYPE.ConstantTimer:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new ConstantTimer()) :
              this.scenarioDefinition.push(new ConstantTimer());
            break;
          case ELEMENT_TYPE.JSR223Processor:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new JSR223Processor()) :
              this.scenarioDefinition.push(new JSR223Processor());
            break;
          case ELEMENT_TYPE.JSR223PreProcessor:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new JSR223Processor({type: "JSR223PreProcessor"})) :
              this.scenarioDefinition.push(new JSR223Processor({type: "JSR223PreProcessor"}));
            break;
          case ELEMENT_TYPE.JSR223PostProcessor:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new JSR223Processor({type: "JSR223PostProcessor"})) :
              this.scenarioDefinition.push(new JSR223Processor({type: "JSR223PostProcessor"}));
            break;
          case ELEMENT_TYPE.Assertions:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new Assertions()) :
              this.scenarioDefinition.push(new Assertions());
            break;
          case ELEMENT_TYPE.Extract:
            this.selectedTreeNode != undefined ? this.selectedTreeNode.hashTree.push(new Extract()) :
              this.scenarioDefinition.push(new Extract());
            break;
          case ELEMENT_TYPE.CustomizeReq:
            this.customizeRequest = {protocol: "HTTP", type: "API", hashTree: [], referenced: 'Created', active: false};
            this.customizeVisible = true;
            break;
          case ELEMENT_TYPE.scenario:
            this.scenarioVisible = true;
            break;
          default:
            this.$refs.apiImport.open();
            break;
        }
        this.sort();
        this.reload();
      },
      nodeClick(e) {
        if (e.referenced != 'REF' && e.referenced != 'Deleted') {
          this.operatingElements = ELEMENTS.get(e.type);
        } else {
          this.operatingElements = [];
        }
        this.selectedTreeNode = e;
      },
      showAll() {
        this.operatingElements = ELEMENTS.get("ALL");
        this.selectedTreeNode = undefined;
        this.reload();
      },
      apiListImport() {
        this.visibleRef = getUUID();
        this.apiListVisible = true;
      },
      recursiveSorting(arr) {
        for (let i in arr) {
          arr[i].index = Number(i) + 1;
          if (arr[i].hashTree != undefined && arr[i].hashTree.length > 0) {
            this.recursiveSorting(arr[i].hashTree);
          }
        }
      },
      sort() {
        for (let i in this.scenarioDefinition) {
          this.scenarioDefinition[i].index = Number(i) + 1;
          if (this.scenarioDefinition[i].hashTree != undefined && this.scenarioDefinition[i].hashTree.length > 0) {
            this.recursiveSorting(this.scenarioDefinition[i].hashTree);
          }
        }
      },
      addCustomizeApi(request) {
        this.customizeVisible = false;
        request.enable === undefined ? request.enable = true : request.enable;
        if (this.selectedTreeNode != undefined) {
          this.selectedTreeNode.hashTree.push(request);
        } else {
          this.scenarioDefinition.push(request);
        }
        this.customizeRequest = {};
        this.sort();
        this.reload();
      },
      addScenario(arr) {
        if (arr && arr.length > 0) {
          arr.forEach(item => {
            if (item.id === this.currentScenario.id) {
              this.$error("不能引用或复制自身！");
              return;
            }
            item.enable === undefined ? item.enable = true : item.enable;
            this.scenarioDefinition.push(item);
          })
        }
        this.sort();
        this.reload();
        this.scenarioVisible = false;
      },
      setApiParameter(item, refType, referenced) {
        let request = {};
        if (Object.prototype.toString.call(item.request).indexOf("String") > 0) {
          request = JSON.parse(item.request);
        } else {
          request = item.request;
        }
        request.id = item.id;
        request.name = item.name;
        request.refType = refType;
        request.referenced = referenced;
        request.enable === undefined ? request.enable = true : request.enable;
        request.active = false;
        request.resourceId = getUUID();
        if (!request.url) {
          request.url = "";
        }
        if (referenced === 'REF' || !request.hashTree) {
          request.hashTree = [];
        }
        if (this.selectedTreeNode != undefined) {
          this.selectedTreeNode.hashTree.push(request);
        } else {
          this.scenarioDefinition.push(request);
        }
      },
      pushApiOrCase(referenced) {
        if (this.currentRow.cases.length === 0 && this.currentRow.apis.length === 0) {
          this.$warning(this.$t('api_test.automation.reference_info'));
          return;
        }
        this.currentRow.cases.forEach(item => {
          this.setApiParameter(item, "CASE", referenced);
        })
        this.currentRow.apis.forEach(item => {
          this.setApiParameter(item, "API", referenced);
        })
        this.apiListVisible = false;
        this.currentRow.cases = [];
        this.currentRow.apis = [];
        this.sort();
        this.reload();
      },
      getMaintainerOptions() {
        let workspaceId = localStorage.getItem(WORKSPACE_ID);
        this.$post('/user/ws/member/tester/list', {workspaceId: workspaceId}, response => {
          this.maintainerOptions = response.data;
        });
      },
      openTagConfig() {
        if (!this.projectId) {
          this.$error(this.$t('api_test.select_project'));
          return;
        }
        this.$refs.tag.open();
      },
      refreshTags() {
        let obj = {projectId: this.projectId};
        let tagIds = [];
        this.$post('/api/tag/list', obj, response => {
          this.tags = response.data;
          this.tags.forEach(item => {
            tagIds.push(item.id);
          })
          if (this.currentScenario.tagId != undefined && this.currentScenario.tagId.length > 0) {
            this.currentScenario.tagId = this.currentScenario.tagId.filter(id => tagIds.indexOf(id) != -1);
          }
        });

      },
      remove(row, node) {
        const parent = node.parent
        const hashTree = parent.data.hashTree || parent.data;
        const index = hashTree.findIndex(d => d.resourceId != undefined && row.resourceId != undefined && d.resourceId === row.resourceId)
        hashTree.splice(index, 1);
        this.sort();
        this.reload();
      },
      copyRow(row, node) {
        const parent = node.parent
        const hashTree = parent.data.hashTree || parent.data;
        // 深度复制
        let obj = JSON.parse(JSON.stringify(row));
        obj.resourceId = getUUID();
        hashTree.push(obj);
        this.sort();
        this.reload();
      },
      reload() {
        this.loading = true
        this.$nextTick(() => {
          this.loading = false
        })
      },
      runDebug() {
        /*触发执行操作*/
        if (!this.currentEnvironmentId) {
          this.$error(this.$t('api_test.environment.select_environment'));
          return;
        }
        this.debugData = {
          id: this.currentScenario.id, name: this.currentScenario.name, type: "scenario",
          variables: this.currentScenario.variables, referenced: 'Created',
          environmentId: this.currentEnvironmentId, hashTree: this.scenarioDefinition
        };
        this.reportId = getUUID().substring(0, 8);
      },
      getEnvironments() {
        if (this.projectId) {
          this.$get('/api/environment/list/' + this.projectId, response => {
            this.environments = response.data;
            this.environments.forEach(environment => {
              parseEnvironment(environment);
            });
          });
        }
      },
      openEnvironmentConfig() {
        if (!this.projectId) {
          this.$error(this.$t('api_test.select_project'));
          return;
        }
        this.$refs.environmentConfig.open(this.projectId);
      },
      environmentConfigClose() {
        this.getEnvironments();
      },
      allowDrop(draggingNode, dropNode, type) {
        if (dropNode.data.type === draggingNode.data.type || ELEMENTS.get(dropNode.data.type).indexOf(draggingNode.data.type) != -1) {
          return true;
        }
        return false;
      },
      allowDrag() {
        this.sort();
        this.reload();
      },
      nodeExpand(data) {
        if (data.resourceId) {
          this.expandedNode.push(data.resourceId);
        }
      },
      nodeCollapse(data) {
        if (data.resourceId) {
          this.expandedNode.splice(this.expandedNode.indexOf(data.resourceId), 1);
        }
      },
      getPath(id) {
        if (id === null) {
          return null;
        }
        let path = this.moduleOptions.filter(function (item) {
          return item.id === id ? item.path : "";
        });
        return path[0].path;
      },
      setFiles(item, bodyUploadFiles, obj) {
        if (item.body) {
          if (item.body.kvs) {
            item.body.kvs.forEach(param => {
              if (param.files) {
                param.files.forEach(item => {
                  if (item.file) {
                    if (!item.id) {
                      let fileId = getUUID().substring(0, 12);
                      item.name = item.file.name;
                      item.id = fileId;
                    }
                    obj.bodyUploadIds.push(item.id);
                    bodyUploadFiles.push(item.file);
                  }
                });
              }
            });
          }
          if (item.body.binary) {
            item.body.binary.forEach(param => {
              if (param.files) {
                param.files.forEach(item => {
                  if (item.file) {
                    if (!item.id) {
                      let fileId = getUUID().substring(0, 12);
                      item.name = item.file.name;
                      item.id = fileId;
                    }
                    obj.bodyUploadIds.push(item.id);
                    bodyUploadFiles.push(item.file);
                  }
                });
              }
            });
          }
        }
      },
      recursiveFile(arr, bodyUploadFiles, obj) {
        arr.forEach(item => {
          this.setFiles(item, bodyUploadFiles, obj);
          if (item.hashTree != undefined && item.hashTree.length > 0) {
            this.recursiveFile(item.hashTree, bodyUploadFiles, obj);
          }
        });
      },
      getBodyUploadFiles(obj) {
        let bodyUploadFiles = [];
        obj.bodyUploadIds = [];
        this.scenarioDefinition.forEach(item => {
          this.setFiles(item, bodyUploadFiles, obj);
          if (item.hashTree != undefined && item.hashTree.length > 0) {
            this.recursiveFile(item.hashTree, bodyUploadFiles, obj);
          }
        })
        return bodyUploadFiles;
      },
      editScenario() {
        this.$refs['currentScenario'].validate((valid) => {
          if (valid) {
            this.setParameter();
            let bodyFiles = this.getBodyUploadFiles(this.currentScenario);
            this.$fileUpload(this.path, null, bodyFiles, this.currentScenario, response => {
              this.$success(this.$t('commons.save_success'));
              this.path = "/api/automation/update";
              if (response.data) {
                this.currentScenario.id = response.data.id;
              }
              this.currentScenario.tagId = JSON.parse(this.currentScenario.tagId);
              this.$emit('refresh');
            })
          }
        })
      },
      getApiScenario() {
        if (this.currentScenario.tagId != undefined && !(this.currentScenario.tagId instanceof Array)) {
          this.currentScenario.tagId = JSON.parse(this.currentScenario.tagId);
        }
        if (this.currentScenario.id) {
          this.result = this.$get("/api/automation/getApiScenario/" + this.currentScenario.id, response => {
            if (response.data) {
              this.path = "/api/automation/update";
              if (response.data.scenarioDefinition != null) {
                let obj = JSON.parse(response.data.scenarioDefinition);
                if (obj) {
                  this.currentEnvironmentId = obj.environmentId;
                  this.currentScenario.variables = obj.variables;
                  this.scenarioDefinition = obj.hashTree;
                }
              }
              if (this.currentScenario.copy) {
                this.path = "/api/automation/create";
              }
            }
          })
        }
      },
      setParameter() {
        this.currentScenario.stepTotal = this.scenarioDefinition.length;
        this.currentScenario.projectId = getCurrentProjectID();
        this.currentScenario.modulePath = this.getPath(this.currentScenario.apiScenarioModuleId);
        // 构建一个场景对象 方便引用处理
        let scenario = {
          id: this.currentScenario.id, name: this.currentScenario.name, variables: this.currentScenario.variables,
          type: "scenario", referenced: 'Created', environmentId: this.currentEnvironmentId, hashTree: this.scenarioDefinition
        };
        this.currentScenario.scenarioDefinition = scenario;
        if (this.currentScenario.tagId instanceof Array) {
          this.currentScenario.tagId = JSON.stringify(this.currentScenario.tagId);
        }
        if (this.currentModule != null) {
          this.currentScenario.modulePath = this.currentModule.method !== undefined ? this.currentModule.method : null;
          this.currentScenario.apiScenarioModuleId = this.currentModule.id;
        }
        this.currentScenario.projectId = this.projectId;
      },
      runRefresh() {
        this.debugVisible = true;
        this.loading = false;
      },
      showScenarioParameters() {
        this.$refs.scenarioParameters.open(this.currentScenario.variables);
      },
      addParameters(data) {
        this.currentScenario.variables = data;
        this.reload();
      },
      apiImport(importData) {
        if (importData && importData.data) {
          importData.data.forEach(item => {
            this.setApiParameter(item, "API", "Copy");
          })
          this.sort();
          this.reload();
        }
      }
    }
  }
</script>

<style scoped>
  .ms-scenario-input {
    width: 100%;
  }

  .ms-main-div {
    background-color: white;
  }

  .ms-opt-btn {
    float: right;
    margin-right: 20px;
  }

  .ms-debug-div {
    margin-left: 20px;
    border: 1px #DCDFE6 solid;
    border-radius: 4px;
    margin-right: 10px;
  }

  .ms-scenario-button {
    margin-left: 30%;
    padding: 7px;
  }

  .tip {
    padding: 3px 5px;
    font-size: 16px;
    border-radius: 4px;
    border-left: 4px solid #783887;
  }

  .ms-api-col {
    background-color: #7C3985;
    border-color: #7C3985;
    margin-right: 10px;
    color: white;
  }

  .ms-col-one {
    margin-top: 6px;
  }

  .ms-right-buttion {
    margin-top: 10px;
  }

  .ms-btn-1 {
    color: #F56C6C;
    background-color: #FCF1F1
  }

  .ms-btn-2 {
    color: #F56C6C;
    background-color: #FCF1F1
  }

  .ms-btn-3 {
    color: #67C23A;
    background-color: #F2F9EE
  }

  .ms-btn-4 {
    color: #E6A23C;
    background-color: #FCF6EE
  }

  .ms-btn-5 {
    color: #606266;
    background-color: #F4F4F5
  }

  .ms-btn-6 {
    color: #7B4D12;
    background-color: #F1EEE9
  }

  .ms-btn-7 {
    color: #008080;
    background-color: #EBF2F2
  }

  .ms-btn-8 {
    color: #B8741A;
    background-color: #F9F1EA
  }

  .ms-btn-9 {
    color: #783887;
    background-color: #F2ECF3
  }

  .ms-btn-10 {
    color: #A30014;
    background-color: #F7E6E9
  }

  .ms-btn-11 {
    color: #015478;
    background-color: #E6EEF2
  }

  /deep/ .el-tree-node__content {
    height: 100%;
    margin-top: 8px;
    vertical-align: center;
  }

  /deep/ .el-card__body {
    padding: 15px;
  }

  /deep/ .el-drawer__body {
    overflow: auto;
  }

  /deep/ .el-step__icon.is-text {
    border: 1px solid;
  }

  /deep/ .el-drawer__header {
    margin-bottom: 0px;
  }
</style>
