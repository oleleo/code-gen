<template>
  <div class="app-container">
    <el-backtop />
    <el-row :gutter="20">
      <el-col :span="16">
        <el-button-group :class="{ 'hasFix': needFix }" style="margin-bottom: 10px;z-index: 999">
          <el-button type="primary" @click="onSave">保 存</el-button>
          <el-button @click="goRoute('/template/list')">返 回</el-button>
        </el-button-group>
        <el-form
          ref="dialogForm"
          :rules="formRules"
          :model="formData"
          size="mini"
          label-position="top"
        >
          <el-form-item prop="groupId" label="组名称">
            <el-select
              v-model="formData.groupId"
              placeholder="选择模板所在组"
            >
              <el-option
                v-for="item in groupData"
                :key="item.id"
                :label="item.groupName"
                :value="item.id"
              >
                {{ item.groupName }}
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="name" label="模板名称">
            <el-input v-model="formData.name" show-word-limit maxlength="64" />
          </el-form-item>
          <el-form-item prop="folder" label="文件目录">
            <el-input v-model="formData.folder" placeholder="为空则是模板名" show-word-limit maxlength="64" />
          </el-form-item>
          <el-form-item prop="fileName" label="文件名称">
            <el-input v-model="formData.fileName" placeholder="可使用velocity变量" show-word-limit maxlength="100" />
          </el-form-item>
          <el-form-item prop="content" label="模板内容">
            <div style="display: inline-block;margin-bottom: 5px;">
              <el-link type="primary" :underline="false" href="https://www.cnblogs.com/codingsilence/archive/2011/03/29/2146580.html" target="_blank">Velocity语法</el-link>
              <span class="split">|</span>
              <el-dropdown @command="changeCodeTheme">
                <span class="el-dropdown-link">
                  代码样式<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item v-for="item in codeThemeList" :command="item">{{ item }}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <span class="split">|</span>
              快捷键：<el-tag v-for="item in shortcutKeyList" effect="plain" size="mini" style="margin-right: 5px">{{ item }}</el-tag>
            </div>
            <codemirror
              ref="editor"
              v-model="formData.content"
              :options="cmOptions"
            />
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="8">
        <div :class="{ 'hasFix': needFix }" style="font-size: 14px;">
          <h4 style="margin: 5px 0">
            Velocity变量
            <span class="velocity-tip">
              点击变量直接插入
            </span>
          </h4>
          <el-tabs v-model="activeName" @tab-click="onTabClick">
            <el-tab-pane v-for="item in velocityConfig" :key="item.name" :label="item.label" :name="item.name" :content="item" />
          </el-tabs>
          <div class="velocity-var">
            <el-tree
              :data="treeData"
              :props="defaultProps"
              :indent="4"
              accordion
            >
              <span slot-scope="{ data }">
                <span v-if="data.children && data.children.length > 0">{{ data.expression }}</span>
                <span v-else>
                  <a @click="onExpressionClick(data.expression)">{{ data.expression }}</a>：{{ data.text }}
                </span>
              </span>
            </el-tree>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style>
  .el-form-item--mini .el-form-item__content,
  .el-form-item--mini .el-form-item__label,
  .el-form-item__content {
   line-height: 20px;
  }
  .velocity-tip {
    color: #606266;
    font-size: 13px;
    font-weight: normal;
  }
  .velocity-var {
  }
  .velocity-var li {
    font-size: 14px;
    color: #606266;
    line-height: 26px;
  }
  .velocity-var a {
    color: #409EFF;
    font-weight: 500;
  }
  .velocity-var a:hover {
    color: rgba(64, 158, 255, 0.75);
  }
  .hasFix {
    position: fixed;
    top: 0;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
  span.split {
    color: #ccc;
    margin: 0 3px;
  }
</style>

<script>
import { codemirror } from 'vue-codemirror'

import 'codemirror/theme/neat.css'
import "codemirror/theme/darcula.css";
import "codemirror/theme/material.css";

/** 搜索 替换框置顶*/
import 'codemirror/addon/scroll/annotatescrollbar.js'
import 'codemirror/addon/search/matchesonscrollbar.js'
import 'codemirror/addon/search/match-highlighter.js'
import 'codemirror/addon/search/jump-to-line.js'

import 'codemirror/addon/dialog/dialog.js'
import 'codemirror/addon/dialog/dialog.css'
import 'codemirror/addon/search/searchcursor.js'
import 'codemirror/addon/search/search.js'

require('codemirror/mode/velocity/velocity')

const TEMPLATE_CODE_THEME = 'gen.template.code.theme'

export default {
  components: { codemirror },
  data() {
    return {
      groupData: [],
      codeThemeList: ['neat', 'darcula','material'],
      shortcutKeyList: [
        '保存(Ctrl+S)',
        '删除当行(Ctrl+D)',
        '复制当行(Ctrl+C)',
        '剪切当行(Ctrl+X)',
        '替换(Ctrl+Shift+F)'
      ],
      formData: {
        id: 0,
        groupId: '',
        groupName: '',
        name: '',
        folder: '',
        fileName: '',
        content: ''
      },
      formRules: {
        groupId: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        fileName: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      },
      //code mirror配置
      cmOptions: {
        value: '',
        mode: 'text/velocity',
        theme: 'neat',
        lineNumbers: true,
        readOnly: false
      },
      isVelocityBarFixed: false,
      // tree
      activeName: 'java',
      collapseActiveName: '0',
      treeData: [],
      velocityConfig: [],
      defaultProps: {
        children: 'children',
        label: 'expression'
      },
      needFix: false,
      keywordHelpShow: false
    }
  },
  created() {
    const id = this.$route.params.id || 0
    if (id > 0) {
      this.post(`/template/get/${id}`, {}, function(resp) {
        this.formData = resp.data
      })
    }
    this.loadVelocityVar()
    this.loadGroups(this.$route.query.groupId)
    //初始化代码主题
    this.cmOptions.theme = this.getAttr(TEMPLATE_CODE_THEME) || 'neat'
  },
  mounted() {
    window.addEventListener('scroll', this.handlerScroll)
    this.watchKeydown()
  },
  methods: {
    watchKeydown(){
      let _this = this;
      document.onkeydown = function(e) {
        let key = window.event.keyCode;
        if (key== 83 && event.ctrlKey) {//s == 83 && event.ctrlKey
          window.event.preventDefault() //关闭浏览器快捷键
          _this.onSave();
        }
        if (event.ctrlKey && key == 82) {
          window.event.preventDefault() //关闭浏览器快捷键
        }

      };
    },
    handlerScroll() {
      const scrollTop = window.pageYOffset ||
        document.documentElement.scrollTop ||
        document.body.scrollTop
      this.needFix = scrollTop > 80
    },
    loadVelocityVar() {
      this.getFile(`velocity/java.json?q=${new Date().getTime()}`, content => {
        this.velocityConfig.push({
          name: 'java',
          label: 'Java变量',
          data: content.data
        })
        this.treeData = content.data
      })
      this.getFile(`velocity/csharp.json?q=${new Date().getTime()}`, content => {
        this.velocityConfig.push({
          name: 'csharp',
          label: 'C#变量',
          data: content.data
        })
      })
    },
    loadGroups(groupId) {
      this.post(`/group/list/`, {}, function(resp) {
        this.groupData = resp.data
        if (!groupId && this.groupData.length > 0) {
          groupId = this.groupData[0].id
        }
        if (groupId && !this.formData.groupId) {
          this.formData.groupId = parseInt(groupId);
        }
      })
    },
    onExpressionClick(exp) {
      const codemirror = this.$refs.editor.codemirror
      // 插入表达式
      codemirror.replaceSelection(exp)
      // 重新获得光标
      codemirror.focus()
    },
    handleNodeClick(node) {
      console.log(node)
    },
    onSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const opt = this.formData.id ? 'update' : 'add'
          const uri = `/template/${opt}`
          this.post(uri, this.formData, resp => {
            if (opt === 'add') {
              this.formData.id = resp.data.id
            }
            this.tip('保存成功')
          })
        }
      })
    },
    onTabClick(tab) {
      const item = tab.$attrs.content
      this.treeData = item.data
    },
    changeCodeTheme(theme){
      this.cmOptions.theme = theme
      this.setAttr(TEMPLATE_CODE_THEME,theme)
    }
  }
}
</script>
