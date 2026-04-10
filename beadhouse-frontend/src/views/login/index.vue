<template>
  <div class="login-page">
    <section class="login-hero">
      <div class="hero-badge">敬老院管理系统</div>
      <div class="hero-copy">
        <p class="hero-kicker">Warm Care Console</p>
        <h1>让入住、护理与运营在同一张工作台上清晰协同。</h1>
        <p>
          面向管理员与护理员的统一后台，用更清楚的结构管理老人档案、床位状态、健康记录与日常服务流程。
        </p>
      </div>
      <div class="hero-highlights">
        <div class="hero-highlight">
          <span>01</span>
          <strong>入住与床位一体化</strong>
          <p>减少纸面与口头交接，让床位状态和入住流程更直观。</p>
        </div>
        <div class="hero-highlight">
          <span>02</span>
          <strong>护理信息更易检索</strong>
          <p>把老人档案、健康信息和服务记录放到统一入口。</p>
        </div>
        <div class="hero-highlight">
          <span>03</span>
          <strong>日常运营更有秩序</strong>
          <p>让收费、访客、库存和业务趋势在日常工作里更容易判断。</p>
        </div>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-panel-inner">
        <LoginModel
          v-if="showFlag.loginModel.value"
          @forgetPassHandle="forgetPassHandle"
        />
        <ForgetPassModel
          v-if="showFlag.forgetModel.value"
          @returnLoginHandle="returnLoginHandle"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import LoginModel from './components/LoginModel.vue'
import ForgetPassModel from '@/views/login/components/ForgetPassModel.vue'
import { ref } from 'vue'

const showFlag = {
  loginModel: ref(true),
  forgetModel: ref(false)
}

const forgetPassHandle = () => {
  showFlag.loginModel.value = false
  showFlag.forgetModel.value = true
}

const returnLoginHandle = () => {
  showFlag.loginModel.value = true
  showFlag.forgetModel.value = false
}
</script>

<style lang="scss" scoped>
.login-page {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(360px, 480px);
  min-height: 100vh;
  padding: 24px;
  gap: 24px;
  background:
    radial-gradient(circle at top left, rgba(118, 185, 163, 0.2), transparent 28%),
    radial-gradient(circle at right 12% top 16%, rgba(231, 192, 128, 0.22), transparent 22%),
    linear-gradient(180deg, rgba(248, 249, 246, 0.94), rgba(241, 245, 241, 0.98));
}

.login-page::before {
  content: '';
  position: absolute;
  inset: 24px;
  border-radius: 36px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.55), rgba(255, 255, 255, 0.15));
  border: 1px solid rgba(132, 163, 151, 0.12);
  pointer-events: none;
}

.login-hero,
.login-panel {
  position: relative;
  z-index: 1;
}

.login-hero {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: clamp(28px, 4vw, 54px);
  border-radius: 34px;
  background:
    linear-gradient(180deg, rgba(39, 63, 58, 0.96), rgba(31, 50, 47, 0.98)),
    radial-gradient(circle at top right, rgba(162, 221, 202, 0.2), transparent 28%);
  color: rgba(248, 251, 249, 0.96);
  box-shadow: 0 30px 80px rgba(24, 39, 36, 0.22);
  overflow: hidden;
}

.login-hero::after {
  content: '';
  position: absolute;
  right: -80px;
  bottom: -80px;
  width: 260px;
  height: 260px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(244, 208, 147, 0.2), transparent 68%);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  min-height: 38px;
  padding: 0 14px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  font-size: 0.92rem;
  font-weight: 600;
}

.hero-copy {
  max-width: 24rem;
}

.hero-kicker {
  margin: 0 0 14px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: rgba(221, 234, 229, 0.68);
  font-size: 0.8rem;
}

.hero-copy h1 {
  margin: 0;
  font-family: var(--font-display);
  font-size: clamp(2.5rem, 4vw, 4.4rem);
  line-height: 1.05;
}

.hero-copy p {
  margin: 16px 0 0;
  max-width: 28rem;
  line-height: 1.8;
  color: rgba(226, 237, 233, 0.8);
}

.hero-highlights {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.hero-highlight {
  padding: 18px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
}

.hero-highlight span {
  display: inline-flex;
  margin-bottom: 14px;
  color: rgba(239, 202, 141, 0.88);
  font-family: var(--font-display);
  font-size: 1.35rem;
}

.hero-highlight strong {
  display: block;
  margin-bottom: 8px;
  font-size: 1rem;
}

.hero-highlight p {
  margin: 0;
  line-height: 1.7;
  color: rgba(223, 234, 230, 0.74);
  font-size: 0.92rem;
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-panel-inner {
  width: min(100%, 440px);
}

@media (max-width: 1080px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .hero-highlights {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .login-page {
    padding: 14px;
  }

  .login-page::before {
    inset: 14px;
    border-radius: 28px;
  }

  .login-hero {
    padding: 24px;
  }

  .hero-copy h1 {
    font-size: 2.4rem;
  }
}
</style>
