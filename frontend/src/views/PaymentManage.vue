<template>
  <div>
    <h2 style="margin-bottom: 20px">缴费管理</h2>
    
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 28px; color: #F56C6C; font-weight: bold">¥{{ totalDebt }}</div>
            <div style="color: #909399; margin-top: 8px">欠款总额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 28px; color: #E6A23C; font-weight: bold">{{ unpaidCount }}</div>
            <div style="color: #909399; margin-top: 8px">待缴账单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 28px; color: #F5A623; font-weight: bold">{{ partialPaidCount }}</div>
            <div style="color: #909399; margin-top: 8px">部分缴费</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 28px; color: #409EFF; font-weight: bold">¥{{ totalLateFee }}</div>
            <div style="color: #909399; margin-top: 8px">滞纳金</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-bottom: 20px">
      <template #header>
        <span>费用优先级说明</span>
      </template>
      <el-steps direction="horizontal" :active="4" finish-status="success">
        <el-step title="物业费" description="优先级最高(1)"></el-step>
        <el-step title="水费" description="优先级(2)"></el-step>
        <el-step title="电费" description="优先级(3)"></el-step>
        <el-step title="停车费" description="优先级最低(4)"></el-step>
      </el-steps>
      <div style="margin-top: 15px; color: #606266; font-size: 14px">
        <el-icon style="color: #67C23A; margin-right: 5px"><InfoFilled /></el-icon>
        缴费系统将自动按以上优先级顺序核销账单欠款，优先保证物业费的收缴。部分缴费后账单将自动拆分状态。
      </div>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header>
        <span>缴费操作</span>
      </template>
      <el-form :inline="true" label-width="80px">
        <el-form-item label="选择业主">
          <el-select v-model="selectedOwner" placeholder="选择业主" style="width: 200px" @change="onOwnerChange">
            <el-option v-for="o in owners" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="缴费金额">
          <el-input-number v-model="amount" :min="0.01" :step="100" :precision="2" :max="ownerTotalDebt"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="makePayment" :loading="paying">确认缴费</el-button>
          <el-button type="success" @click="payAll" :disabled="!selectedOwner" :loading="paying">一键缴清(¥{{ ownerTotalDebt }})</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-bottom: 20px" v-if="selectedOwner">
      <template #header>
        <span>该业主待缴账单（按优先级自动核销）</span>
        <span style="float: right; color: #F56C6C; font-weight: bold">
          待缴总额: ¥{{ ownerTotalDebt }}
        </span>
      </template>
      <el-table :data="ownerBills" border stripe size="small">
        <el-table-column label="优先级" width="80" align="center">
          <template #default="scope">
            <el-tag :type="getPriorityTag(scope.row.feeType)" size="small">{{ getPriority(scope.row.feeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="feeType" label="费用类型" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.feeType === 'PROPERTY_FEE'" type="primary" size="small">物业费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'WATER_FEE'" type="info" size="small">水费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'ELECTRICITY_FEE'" type="warning" size="small">电费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'PARKING_FEE'" type="success" size="small">停车费</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="账单金额" width="100">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已缴金额" width="100">
          <template #default="scope">
            <span v-if="parseFloat(scope.row.paidAmount) > 0" style="color: #67C23A">¥{{ scope.row.paidAmount }}</span>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="unpaidAmount" label="未缴金额" width="100">
          <template #default="scope">
            <span style="color: #F56C6C; font-weight: bold">¥{{ scope.row.unpaidAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lateFee" label="滞纳金" width="90">
          <template #default="scope">
            <span v-if="scope.row.lateFee > 0" style="color: #F56C6C">¥{{ scope.row.lateFee }}</span>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column label="账单状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'UNPAID'" type="danger" size="small">未缴费</el-tag>
            <el-tag v-else-if="scope.row.status === 'PARTIAL_PAID'" type="warning" size="small" effect="dark">部分缴费</el-tag>
            <el-tag v-else-if="scope.row.status === 'PAID'" type="success" size="small">已缴费</el-tag>
            <el-tag v-else-if="scope.row.status === 'OVERDUE'" type="danger" size="small" effect="dark">逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费进度" width="180">
          <template #default="scope">
            <el-progress 
              :percentage="getPaymentProgress(scope.row)" 
              :stroke-width="10" 
              :show-text="false"
              :color="getProgressColor(scope.row)"
            ></el-progress>
            <span style="margin-left: 8px; font-size: 12px; font-weight: bold">{{ getPaymentProgress(scope.row) }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="状态说明" min-width="150">
          <template #default="scope">
            <span v-if="scope.row.status === 'PARTIAL_PAID'" style="color: #E6A23C; font-size: 12px">
              <el-icon><WarningFilled /></el-icon>
              已部分缴费，尚欠 ¥{{ scope.row.unpaidAmount }}
            </span>
            <span v-else-if="scope.row.status === 'UNPAID'" style="color: #F56C6C; font-size: 12px">
              <el-icon><CircleCloseFilled /></el-icon>
              尚未开始缴费
            </span>
            <span v-else-if="scope.row.status === 'PAID'" style="color: #67C23A; font-size: 12px">
              <el-icon><CircleCheckFilled /></el-icon>
              账单已全部结清
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px" v-if="selectedOwner && ownerPartialBills.length > 0">
      <template #header>
        <span>部分缴费账单（拆分状态）</span>
        <el-tag type="warning" size="small">{{ ownerPartialBills.length }} 笔</el-tag>
      </template>
      <el-alert
        title="部分缴费说明"
        type="warning"
        description="以下账单因缴费金额不足，已自动拆分为部分缴费状态，剩余欠款待后续缴费继续核销。"
        show-icon
        :closable="false"
        style="margin-bottom: 15px"
      >
      </el-alert>
      <el-table :data="ownerPartialBills" border stripe size="small">
        <el-table-column label="优先级" width="80" align="center">
          <template #default="scope">
            <el-tag :type="getPriorityTag(scope.row.feeType)" size="small">{{ getPriority(scope.row.feeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="feeType" label="费用类型" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.feeType === 'PROPERTY_FEE'" type="primary" size="small">物业费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'WATER_FEE'" type="info" size="small">水费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'ELECTRICITY_FEE'" type="warning" size="small">电费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'PARKING_FEE'" type="success" size="small">停车费</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账单拆分明细" min-width="300">
          <template #default="scope">
            <div style="display: flex; align-items: center; gap: 10px">
              <div style="flex: 1; text-align: center">
                <div style="color: #909399; font-size: 12px">账单金额</div>
                <div style="font-size: 16px; font-weight: bold">¥{{ scope.row.totalAmount }}</div>
              </div>
              <el-icon style="color: #E6E6E6"><ArrowRight /></el-icon>
              <div style="flex: 1; text-align: center">
                <div style="color: #67C23A; font-size: 12px">已缴金额</div>
                <div style="font-size: 16px; font-weight: bold; color: #67C23A">¥{{ scope.row.paidAmount }}</div>
              </div>
              <el-icon style="color: #E6E6E6"><ArrowRight /></el-icon>
              <div style="flex: 1; text-align: center">
                <div style="color: #F56C6C; font-size: 12px">剩余欠款</div>
                <div style="font-size: 16px; font-weight: bold; color: #F56C6C">¥{{ scope.row.unpaidAmount }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="缴费进度" width="180">
          <template #default="scope">
            <el-progress 
              :percentage="getPaymentProgress(scope.row)" 
              :stroke-width="12" 
              :show-text="false"
              color="#E6A23C"
            ></el-progress>
            <span style="margin-left: 8px; font-size: 12px; font-weight: bold; color: #E6A23C">{{ getPaymentProgress(scope.row) }}%</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px" v-if="lastPaymentDetail">
      <template #header>
        <span>本次缴费核销明细</span>
      </template>
      <el-alert
        :title="lastPaymentDetail.hasPartial ? '部分缴费完成' : '全部缴费完成'"
        :type="lastPaymentDetail.hasPartial ? 'warning' : 'success'"
        :description="`缴费 ¥${lastPaymentDetail.amount}，共核销 ${lastPaymentDetail.bills.length} 笔账单。${lastPaymentDetail.hasPartial ? '其中 ' + lastPaymentDetail.partialCount + ' 笔账单部分缴费，状态已自动拆分。' : '所有账单已全部结清！'}`"
        show-icon
        style="margin-bottom: 15px"
      >
      </el-alert>
      <el-table :data="lastPaymentDetail.bills" border stripe size="small">
        <el-table-column label="核销顺序" width="80" align="center">
          <template #default="scope">{{ scope.$index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="feeType" label="费用类型" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.feeType === 'PROPERTY_FEE'" type="primary" size="small">物业费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'WATER_FEE'" type="info" size="small">水费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'ELECTRICITY_FEE'" type="warning" size="small">电费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'PARKING_FEE'" type="success" size="small">停车费</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="billId" label="账单ID" width="180"></el-table-column>
        <el-table-column prop="beforeAmount" label="缴费前欠款" width="120">
          <template #default="scope">¥{{ scope.row.beforeAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="本次核销" width="120">
          <template #default="scope">
            <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="afterAmount" label="缴费后欠款" width="120">
          <template #default="scope">
            <span v-if="scope.row.afterAmount > 0" style="color: #F56C6C">¥{{ scope.row.afterAmount }}</span>
            <el-tag v-else type="success" size="small">已结清</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态变化" width="180">
          <template #default="scope">
            <div v-if="scope.row.afterAmount <= 0" style="color: #67C23A">
              <el-icon><CircleCheckFilled /></el-icon>
              未缴费 → 已缴费
            </div>
            <div v-else-if="parseFloat(scope.row.beforeAmount) === parseFloat(scope.row.afterAmount)" style="color: #909399">
              <el-icon><Minus /></el-icon>
              无变化
            </div>
            <div v-else style="color: #E6A23C">
              <el-icon><ArrowDown /></el-icon>
              未缴费 → 部分缴费
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header>
        <span>缴费记录</span>
      </template>
      <el-table :data="payments" border stripe>
        <el-table-column prop="id" label="缴费ID" width="180"></el-table-column>
        <el-table-column label="业主" width="100">
          <template #default="scope">{{ getOwnerName(scope.row.ownerId) }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="缴费金额" width="120">
          <template #default="scope">
            <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="核销账单" min-width="200">
          <template #default="scope">
            <el-tag v-for="id in scope.row.billIds" :key="id" size="small" style="margin: 2px">{{ id.slice(-6) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import { InfoFilled, WarningFilled, CircleCloseFilled, CircleCheckFilled, ArrowRight, Minus, ArrowDown } from '@element-plus/icons-vue'

export default {
  components: { InfoFilled, WarningFilled, CircleCloseFilled, CircleCheckFilled, ArrowRight, Minus, ArrowDown },
  data() {
    return {
      owners: [],
      bills: [],
      payments: [],
      selectedOwner: '',
      amount: 100,
      paying: false,
      lastPaymentDetail: null
    }
  },
  computed: {
    ownerBills() {
      return this.bills.filter(b => b.ownerId === this.selectedOwner && b.status !== 'PAID')
        .sort((a, b) => {
          const priority = { PROPERTY_FEE: 1, WATER_FEE: 2, ELECTRICITY_FEE: 3, PARKING_FEE: 4 }
          return priority[a.feeType] - priority[b.feeType]
        })
    },
    ownerPartialBills() {
      return this.bills.filter(b => b.ownerId === this.selectedOwner && b.status === 'PARTIAL_PAID')
        .sort((a, b) => {
          const priority = { PROPERTY_FEE: 1, WATER_FEE: 2, ELECTRICITY_FEE: 3, PARKING_FEE: 4 }
          return priority[a.feeType] - priority[b.feeType]
        })
    },
    ownerTotalDebt() {
      return this.ownerBills.reduce((sum, b) => sum + parseFloat(b.unpaidAmount), 0).toFixed(2)
    },
    totalDebt() {
      return this.bills.filter(b => b.status !== 'PAID').reduce((sum, b) => sum + parseFloat(b.unpaidAmount), 0).toFixed(2)
    },
    unpaidCount() {
      return this.bills.filter(b => b.status === 'UNPAID' || b.status === 'OVERDUE').length
    },
    partialPaidCount() {
      return this.bills.filter(b => b.status === 'PARTIAL_PAID').length
    },
    paidCount() {
      return this.bills.filter(b => b.status === 'PAID').length
    },
    totalLateFee() {
      return this.bills.reduce((sum, b) => sum + parseFloat(b.lateFee || 0), 0).toFixed(2)
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const [owners, bills, payments] = await Promise.all([
        axios.get('/api/owners'),
        axios.get('/api/bills'),
        axios.get('/api/payments')
      ])
      this.owners = owners.data
      this.bills = bills.data
      this.payments = payments.data
    },
    onOwnerChange() {
      this.amount = Math.min(100, this.ownerTotalDebt)
      this.lastPaymentDetail = null
    },
    getOwnerName(ownerId) {
      const owner = this.owners.find(o => o.id === ownerId)
      return owner ? owner.name : ownerId
    },
    getPriority(feeType) {
      const priority = { PROPERTY_FEE: 1, WATER_FEE: 2, ELECTRICITY_FEE: 3, PARKING_FEE: 4 }
      return priority[feeType] || 9
    },
    getPriorityTag(feeType) {
      const p = this.getPriority(feeType)
      return p === 1 ? 'danger' : p === 2 ? 'info' : p === 3 ? 'warning' : 'success'
    },
    getPaymentProgress(bill) {
      const total = parseFloat(bill.totalAmount)
      const paid = parseFloat(bill.paidAmount)
      return Math.round((paid / total) * 100)
    },
    getProgressColor(bill) {
      const progress = this.getPaymentProgress(bill)
      if (progress === 100) return '#67C23A'
      if (progress > 0) return '#E6A23C'
      return '#F56C6C'
    },
    async makePayment() {
      if (!this.selectedOwner) {
        this.$message.warning('请先选择业主')
        return
      }
      this.paying = true
      
      const beforeBills = JSON.parse(JSON.stringify(this.ownerBills))
      await axios.post(`/api/payments?ownerId=${this.selectedOwner}&amount=${this.amount}`)
      
      await this.loadData()
      
      this.showPaymentDetail(beforeBills, this.amount)
      
      this.$message.success('缴费成功，已按优先级自动核销账单')
      this.paying = false
    },
    async payAll() {
      if (!this.selectedOwner) return
      this.amount = this.ownerTotalDebt
      await this.makePayment()
    },
    showPaymentDetail(beforeBills, payAmount) {
      let remaining = parseFloat(payAmount)
      const details = []
      let partialCount = 0
      
      const sortedBills = [...beforeBills].sort((a, b) => {
        const priority = { PROPERTY_FEE: 1, WATER_FEE: 2, ELECTRICITY_FEE: 3, PARKING_FEE: 4 }
        return priority[a.feeType] - priority[b.feeType]
      })
      
      for (const bill of sortedBills) {
        if (remaining <= 0) break
        
        const unpaid = parseFloat(bill.unpaidAmount)
        const toPay = Math.min(remaining, unpaid)
        
        const afterAmount = (unpaid - toPay).toFixed(2)
        
        details.push({
          feeType: bill.feeType,
          billId: bill.id,
          beforeAmount: unpaid.toFixed(2),
          payAmount: toPay.toFixed(2),
          afterAmount: afterAmount
        })
        
        if (afterAmount > 0 && toPay > 0) {
          partialCount++
        }
        
        remaining -= toPay
      }
      
      this.lastPaymentDetail = {
        amount: payAmount,
        bills: details,
        partialCount: partialCount,
        hasPartial: partialCount > 0
      }
    }
  }
}
</script>
