<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  editing: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

// 新增 / 修改毛孩共用表單
const form = reactive({
  petName: '',
  breed: '',
  gender: '',
  birthday: '',
  petAvatarUrl: '',
  petIntro: ''
})

watch(
  () => props.modelValue,
  (value) => {
    form.petName = value.petName || value.pet_name || ''
    form.breed = value.breed || ''
    form.gender = value.gender || ''
    form.birthday = value.birthday || ''
    form.petAvatarUrl = value.petAvatarUrl || value.pet_avatar_url || ''
    form.petIntro = value.petIntro || value.pet_intro || ''
  },
  { immediate: true, deep: true }
)

function submitForm() {
  if (!form.petName.trim()) {
    alert('請輸入毛孩名稱')
    return
  }

  emit('submit', {
    petName: form.petName,
    breed: form.breed,
    gender: form.gender || null,
    birthday: form.birthday || null,
    petAvatarUrl: form.petAvatarUrl,
    petIntro: form.petIntro
  })
}
</script>

<template>
  <section class="pet-form-card">
    <div class="form-title-row">
      <div>
        <p class="eyebrow">My Pet</p>
        <h2>{{ editing ? '修改毛孩資料' : '新增毛孩資料' }}</h2>
      </div>
      <button v-if="editing" class="ghost-btn" type="button" @click="$emit('cancel')">
        取消修改
      </button>
    </div>

    <div class="form-grid">
      <label>
        <span>毛孩名稱</span>
        <input v-model="form.petName" placeholder="例如：豆豆" />
      </label>

      <label>
        <span>品種</span>
        <input v-model="form.breed" placeholder="例如：米克斯、柴犬、英短" />
      </label>

      <label>
        <span>性別</span>
        <select v-model="form.gender">
          <option value="">不指定</option>
          <option value="M">公</option>
          <option value="F">母</option>
        </select>
      </label>

      <label>
        <span>生日</span>
        <input v-model="form.birthday" type="date" />
      </label>
    </div>

    <label class="full-row">
      <span>毛孩頭像網址</span>
      <input v-model="form.petAvatarUrl" placeholder="可貼 Cloudinary 或圖片網址" />
    </label>

    <label class="full-row">
      <span>毛孩介紹</span>
      <textarea v-model="form.petIntro" placeholder="簡單介紹毛孩個性、喜歡的玩具或生活習慣"></textarea>
    </label>

    <button class="submit-btn" type="button" :disabled="loading" @click="submitForm">
      {{ loading ? '儲存中...' : editing ? '儲存修改' : '新增毛孩' }}
    </button>
  </section>
</template>

<style scoped>
.pet-form-card {
  background: #fff7ed;
  border-radius: 28px;
  padding: 28px;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06);
}

.form-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.eyebrow {
  margin: 0 0 4px;
  color: #f97316;
  font-weight: 800;
  letter-spacing: 0.08em;
}

h2 {
  margin: 0;
  font-size: 28px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

label {
  display: block;
}

label span {
  display: block;
  margin-bottom: 6px;
  color: #374151;
  font-weight: 700;
}

input,
select,
textarea {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 12px 14px;
  background: #fff;
  outline: none;
}

textarea {
  min-height: 110px;
  resize: vertical;
}

.full-row {
  margin-top: 14px;
}

.submit-btn,
.ghost-btn {
  border-radius: 999px;
  padding: 11px 22px;
  font-weight: 800;
  cursor: pointer;
}

.submit-btn {
  margin-top: 18px;
  border: none;
  background: #f97316;
  color: #fff;
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.ghost-btn {
  border: 1px solid #fed7aa;
  background: #fff;
  color: #f97316;
}

@media (max-width: 760px) {
  .form-title-row {
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
