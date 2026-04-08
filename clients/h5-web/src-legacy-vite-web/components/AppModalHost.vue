<script setup lang="ts">
type ActiveModal = null | "pause" | "finish" | "edit-profile";

defineProps<{
  activeModal: ActiveModal;
  profileSaving: boolean;
  profileEditor: {
    nickname: string;
    avatarUrl: string;
    bio: string;
    city: string;
    levelLabel: string;
  };
  profileErrors: {
    nickname: string;
    bio: string;
  };
}>();

const emit = defineEmits<{
  close: [];
  pauseRecording: [];
  finishRecording: [];
  saveProfile: [];
  updateProfileField: [field: "nickname" | "avatarUrl" | "bio" | "city" | "levelLabel", value: string];
  markNicknameTouched: [];
  markBioTouched: [];
}>();
</script>

<template>
  <div v-if="activeModal" class="modal">
    <button class="modal__scrim" @click="emit('close')"></button>
    <div class="modal__card">
      <template v-if="activeModal === 'pause'">
        <h3>暂停记录？</h3>
        <p>暂停后会保留当前轨迹与点位，稍后可继续记录。</p>
        <div class="modal__actions">
          <button class="button-secondary" @click="emit('close')">取消</button>
          <button class="button-primary" @click="emit('pauseRecording')">确认暂停</button>
        </div>
      </template>

      <template v-else-if="activeModal === 'finish'">
        <h3>结束本次记录？</h3>
        <p>结束后会进入整理和发布阶段，仍可继续补充点位与媒体内容。</p>
        <div class="modal__actions">
          <button class="button-secondary" @click="emit('close')">继续记录</button>
          <button class="button-primary" @click="emit('finishRecording')">结束并整理</button>
        </div>
      </template>

      <template v-else-if="activeModal === 'edit-profile'">
        <h3>编辑个人资料</h3>
        <p>这里对应“我的”页面编辑入口，直接连到真实 `/api/me` 更新接口。</p>
        <div class="modal__form">
          <label class="field">
            <span class="field__label">昵称</span>
            <input
              :value="profileEditor.nickname"
              class="field__control"
              type="text"
              placeholder="请输入昵称"
              @input="emit('updateProfileField', 'nickname', ($event.target as HTMLInputElement).value)"
              @blur="emit('markNicknameTouched')"
            />
            <span v-if="profileErrors.nickname" class="field__error">{{ profileErrors.nickname }}</span>
          </label>
          <label class="field">
            <span class="field__label">城市</span>
            <input
              :value="profileEditor.city"
              class="field__control"
              type="text"
              placeholder="例如：上海"
              @input="emit('updateProfileField', 'city', ($event.target as HTMLInputElement).value)"
            />
          </label>
          <label class="field">
            <span class="field__label">徒步等级</span>
            <input
              :value="profileEditor.levelLabel"
              class="field__control"
              type="text"
              placeholder="例如：轻中度徒步"
              @input="emit('updateProfileField', 'levelLabel', ($event.target as HTMLInputElement).value)"
            />
          </label>
          <label class="field">
            <span class="field__label">头像链接</span>
            <input
              :value="profileEditor.avatarUrl"
              class="field__control"
              type="text"
              placeholder="可选，后续可接上传能力"
              @input="emit('updateProfileField', 'avatarUrl', ($event.target as HTMLInputElement).value)"
            />
          </label>
          <label class="field">
            <span class="field__label">个人简介</span>
            <textarea
              :value="profileEditor.bio"
              class="field__control field__control--textarea"
              placeholder="介绍你的偏好路线、拍摄习惯和出行风格"
              @input="emit('updateProfileField', 'bio', ($event.target as HTMLTextAreaElement).value)"
              @blur="emit('markBioTouched')"
            ></textarea>
            <span v-if="profileErrors.bio" class="field__error">{{ profileErrors.bio }}</span>
          </label>
        </div>
        <div class="modal__actions">
          <button class="button-secondary" @click="emit('close')">取消</button>
          <button class="button-primary" :disabled="profileSaving" @click="emit('saveProfile')">
            {{ profileSaving ? "保存中..." : "保存资料" }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>
