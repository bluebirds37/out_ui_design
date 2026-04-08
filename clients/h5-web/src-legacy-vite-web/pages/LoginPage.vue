<script setup lang="ts">
defineProps<{
  account: string;
  password: string;
  accountError: string;
  passwordError: string;
  submitting: boolean;
  loginError: string;
}>();

const emit = defineEmits<{
  back: [];
  updateAccount: [value: string];
  updatePassword: [value: string];
  markAccountTouched: [];
  markPasswordTouched: [];
  submit: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('back')">←</button>
    <div class="page-header__title">登录 TrailNote</div>
    <div class="page-header__placeholder"></div>
  </div>
  <section class="panel-card">
    <label class="field">
      <span class="field__label">账号</span>
      <input
        :value="account"
        class="field__control"
        type="text"
        placeholder="邮箱或手机号"
        @input="emit('updateAccount', ($event.target as HTMLInputElement).value)"
        @blur="emit('markAccountTouched')"
      />
      <span v-if="accountError" class="field__error">{{ accountError }}</span>
    </label>
    <label class="field">
      <span class="field__label">密码</span>
      <input
        :value="password"
        class="field__control"
        type="password"
        placeholder="请输入密码"
        @input="emit('updatePassword', ($event.target as HTMLInputElement).value)"
        @blur="emit('markPasswordTouched')"
      />
      <span v-if="passwordError" class="field__error">{{ passwordError }}</span>
    </label>
    <p v-if="loginError" class="field__error">{{ loginError }}</p>
    <button class="button-primary" :disabled="submitting" @click="emit('submit')">
      {{ submitting ? "登录中..." : "登录" }}
    </button>
  </section>
</template>
