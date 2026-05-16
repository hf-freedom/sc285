<template>
  <div>
    <h2 style="margin-bottom: 20px">首页概览</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 36px; color: #409EFF; font-weight: bold">{{ stats.ownerCount }}</div>
            <div style="color: #909399; margin-top: 10px">业主数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 36px; color: #67C23A; font-weight: bold">{{ stats.houseCount }}</div>
            <div style="color: #909399; margin-top: 10px">房屋数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 36px; color: #E6A23C; font-weight: bold">{{ stats.billCount }}</div>
            <div style="color: #909399; margin-top: 10px">账单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 36px; color: #F56C6C; font-weight: bold">{{ stats.unpaidBillCount }}</div>
            <div style="color: #909399; margin-top: 10px">未缴账单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快速操作</span>
          </template>
          <el-space direction="vertical" style="width: 100%">
            <el-button type="primary" @click="calcLateFees" style="width: 100%">计算滞纳金</el-button>
            <el-button type="success" @click="goOwner" style="width: 100%">业主管理</el-button>
            <el-button type="warning" @click="goBill" style="width: 100%">生成账单</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统说明</span>
          </template>
          <ul style="line-height: 2.2; color: #606266">
            <li>房屋绑定业主后按面积和费用类型生成账单</li>
            <li>支持物业费、水费、电费、停车费组合账单</li>
            <li>缴费时按费用优先级自动核销欠款</li>
            <li>逾期自动产生滞纳金（每日0.5%）</li>
            <li>部分缴费时自动拆分账单状态</li>
            <li>业主申请减免需进入审批流程</li>
            <li>定时任务每月1日自动生成账单</li>
            <li>黑名单业主限制部分物业服务</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      stats: {
        ownerCount: 0,
        houseCount: 0,
        billCount: 0,
        unpaidBillCount: 0
      }
    }
  },
  mounted() {
    this.loadStats()
  },
  methods: {
    async loadStats() {
      const res = await axios.get('/api/stats')
      this.stats = res.data
    },
    async calcLateFees() {
      await axios.post('/api/calculate-late-fees')
      this.$message.success('滞纳金计算完成')
      this.loadStats()
    },
    goOwner() {
      this.$router.push('/owner')
    },
    goBill() {
      this.$router.push('/bill')
    }
  }
}
</script>
