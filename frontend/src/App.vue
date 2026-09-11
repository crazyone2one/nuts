<script setup lang="ts">
import {dateZhCN, zhCN} from 'naive-ui'
</script>

<template>
  <n-config-provider :locale="zhCN" :date-locale="dateZhCN">
    <n-global-style/>
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <application>
            <router-view/>
          </application>
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>

  </n-config-provider>
</template>
