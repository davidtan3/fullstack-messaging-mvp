<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { useWebSocket } from '@/composables/useWebSocket'

const username = ref('')
const recipient = ref('')
const messageText = ref('')
const loggedIn = ref(false)

const { messages, connected, error, connect, sendMessage, disconnect } = useWebSocket()

function enterChat() {
  const normalizedUsername = username.value.trim().toLowerCase()

  if (!normalizedUsername) {
    return
  }

  username.value = normalizedUsername

  connect(normalizedUsername)

  loggedIn.value = true
}

function send() {
  const sent = sendMessage(recipient.value, messageText.value)

  if (sent) {
    messageText.value = ''
  }
}

function logout() {
  disconnect()

  loggedIn.value = false
  recipient.value = ''
  messageText.value = ''
}

function formatTime(value: string | null) {
  if (!value) {
    return ''
  }

  return new Date(value).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
  })
}

onBeforeUnmount(() => {
  disconnect()
})
</script>

<template>
  <main class="page">
    <div v-if="!loggedIn" class="login-card">
      <h1>Real-Time Messenger</h1>

      <p>Enter a username to start chatting.</p>

      <form @submit.prevent="enterChat">
        <input v-model="username" placeholder="Username" autocomplete="off" />

        <button type="submit">Enter Chat</button>
      </form>
    </div>

    <div v-else class="chat-card">
      <header>
        <div>
          <h1>Messenger</h1>

          <span :class="connected ? 'online' : 'offline'">
            {{ connected ? 'Connected' : 'Disconnected' }}
          </span>
        </div>

        <div class="user-info">
          <strong>{{ username }}</strong>

          <button class="secondary" @click="logout">Leave</button>
        </div>
      </header>

      <section class="recipient">
        <label> Chat with </label>

        <input v-model="recipient" placeholder="e.g. bob" autocomplete="off" />
      </section>

      <section class="messages">
        <p v-if="messages.length === 0" class="empty">No messages yet.</p>

        <div
          v-for="(message, index) in messages"
          :key="message.clientMessageId ?? index"
          class="message-row"
          :class="{
            mine: message.from === username,
          }"
        >
          <div class="bubble">
            <strong>
              {{ message.from === username ? 'You' : message.from }}
            </strong>

            <p>
              {{ message.content }}
            </p>

            <small>
              {{ formatTime(message.sentAt) }}
            </small>
          </div>
        </div>
      </section>

      <p v-if="error" class="error">
        {{ error }}
      </p>

      <form class="message-form" @submit.prevent="send">
        <input v-model="messageText" placeholder="Type a message..." autocomplete="off" />

        <button type="submit">Send</button>
      </form>
    </div>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 32px;
  background: #f4f5f7;
}

.login-card,
.chat-card {
  width: 100%;
  max-width: 720px;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.login-card {
  max-width: 420px;
}

.login-card form {
  display: flex;
  gap: 8px;
  margin-top: 24px;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
  padding-bottom: 16px;
}

header h1 {
  margin: 0 0 4px;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.online {
  color: green;
}

.offline {
  color: #999;
}

.recipient {
  margin: 20px 0;
}

.recipient label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
}

.messages {
  height: 360px;
  overflow-y: auto;
  padding: 16px;
  background: #f7f7f7;
  border-radius: 8px;
}

.message-row {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 12px;
}

.message-row.mine {
  justify-content: flex-end;
}

.bubble {
  max-width: 70%;
  background: white;
  border-radius: 10px;
  padding: 10px 14px;
}

.mine .bubble {
  background: #dcf8c6;
}

.bubble p {
  margin: 4px 0;
}

.bubble small {
  color: #777;
}

.message-form {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

input {
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 10px 12px;
  font: inherit;
}

button {
  border: none;
  border-radius: 6px;
  padding: 10px 18px;
  cursor: pointer;
  background: #222;
  color: white;
  white-space: nowrap;
}

button.secondary {
  background: #777;
}

.error {
  color: #c62828;
  margin: 12px 0 0;
}

.empty {
  color: #888;
  text-align: center;
}
</style>
