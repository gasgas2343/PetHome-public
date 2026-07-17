<script setup>
import ForumRuleItem from './ForumRuleItem.vue'

const emit = defineEmits(['close'])

const rules = [
  '禁止人身攻擊',
  '禁止散播不實資訊',
  '禁止色情內容',
  '禁止非法交易',
  '禁止惡意洗版',
  '尊重其他會員',
]

function closeDialog() {
  emit('close')
}
</script>

<template>
  <div class="rule-mask" @click.self="closeDialog">
    <div class="rule-dialog">
      <div class="rule-header">
        <h5>論壇規範</h5>

        <button type="button" class="btn-close" @click="closeDialog">✕</button>
      </div>

      <div class="rule-body">
        <ul class="list-group">
          <ForumRuleItem v-for="rule in rules" :key="rule" :rule="rule" />
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
.rule-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);

  display: flex;
  justify-content: center;
  align-items: center;

  z-index: 9999;
}

.rule-dialog {
  width: 500px;
  max-width: 90vw;

  background: white;

  border-radius: 12px;

  overflow: hidden;
}

.rule-header {
  padding: 16px;

  display: flex;

  justify-content: space-between;

  align-items: center;

  border-bottom: 1px solid #ddd;
}

.rule-body {
  padding: 16px;
}

.btn-close {
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
}
</style>
