<script setup>
import { ref } from 'vue'
import ImageUploader from '@/petpost/components/common/ImageUploader.vue'

const emit = defineEmits(['created'])

const petName = ref('')
const breed = ref('')
const gender = ref('')
const birthday = ref('')
const description = ref('')
const petAvatarUrl = ref('')
const saving = ref(false)

function submit() {
  if (!petName.value.trim()) {
    alert('請輸入寵物名稱')
    return
  }

  saving.value = true

  emit('created', {
    petName: petName.value.trim(),
    name: petName.value.trim(),
    breed: breed.value.trim(),
    gender: gender.value,
    birthday: birthday.value || null,
    petIntro: description.value.trim(),
    description: description.value.trim(),
    petAvatarUrl: petAvatarUrl.value,
    avatarUrl: petAvatarUrl.value,
  })

  petName.value = ''
  breed.value = ''
  gender.value = ''
  birthday.value = ''
  description.value = ''
  petAvatarUrl.value = ''
  saving.value = false
}
</script>

<template>
  <form class="pet-form pc-card" @submit.prevent="submit">
    <div class="form-copy">
      <p class="eyebrow">PET PROFILE</p>
      <h3>建立毛孩資料</h3>
      <p>選擇預先放好的雲端圖片，記錄名字、生日、品種與介紹。</p>
    </div>

    <div class="image-field">
      <ImageUploader
        v-model="petAvatarUrl"
        type="pet"
        label="選擇圖片"
      />
    </div>

    <div class="field-grid">
      <input v-model="petName" class="form-control" placeholder="寵物名稱" />
      <input v-model="breed" class="form-control" placeholder="品種" />
      <select v-model="gender" class="form-select">
        <option value="">性別</option>
        <option value="公">公</option>
        <option value="母">母</option>
      </select>
      <input v-model="birthday" class="form-control" type="date" />
      <textarea v-model="description" class="form-control" rows="3" placeholder="簡短介紹"></textarea>
    </div>

    <button class="pc-btn pc-btn-primary" type="submit" :disabled="saving">
      {{ saving ? '處理中...' : '新增毛孩' }}
    </button>
  </form>
</template>

<style scoped>
.pet-form {
  display: grid;
  grid-template-columns: 1fr 280px 1.2fr auto;
  gap: 22px;
  align-items: center;
  padding: 26px;
  margin: 24px 0;
}

.eyebrow {
  color: var(--pc-brown);
  font-weight: 900;
  letter-spacing: 0.18em;
  font-size: 13px;
}

h3 {
  color: var(--pc-title);
  font-weight: 900;
  margin: 4px 0 8px;
}

.form-copy p:last-child {
  color: var(--pc-muted);
  line-height: 1.7;
}

.image-field {
  min-width: 240px;
}

.field-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.field-grid textarea {
  grid-column: 1 / -1;
}

@media (max-width: 992px) {
  .pet-form {
    grid-template-columns: 1fr;
  }
}
</style>
