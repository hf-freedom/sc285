<template>
  <div id="app">
    <el-container style="height: 100vh">
      <el-aside width="200px" style="background-color: #2f4050">
        <div style="color: white; padding: 20px; text-align: center; font-size: 18px; font-weight: bold">
          物业缴费系统
        </div>
        <el-menu
          :default-active="activeMenu"
          background-color="#2f4050"
          text-color="#a7b1c2"
          active-text-color="#fff"
          router
        >
          <el-menu-item index="/">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/owner">
            <span>业主管理</span>
          </el-menu-item>
          <el-menu-item index="/bill">
            <span>账单管理</span>
          </el-menu-item>
          <el-menu-item index="/payment">
            <span>缴费管理</span>
          </el-menu-item>
          <el-menu-item index="/discount">
            <span>减免审批</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  computed: {
    activeMenu() {
      return this.$route.path
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}
</style>
