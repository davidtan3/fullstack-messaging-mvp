import { ref } from 'vue'
import type { ChatMessage } from '@/types/ChatMessage'

const messages = ref<ChatMessage[]>([])
const connected = ref(false)
const error = ref('')

let socket: WebSocket | null = null

const websocketUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws'

export function useWebSocket() {
  function connect(username: string) {
    disconnect()

    messages.value = []
    error.value = ''

    const normalizedUsername = username.trim().toLowerCase()

    socket = new WebSocket(`${websocketUrl}?username=${encodeURIComponent(normalizedUsername)}`)

    socket.onopen = () => {
      connected.value = true
      error.value = ''
    }

    socket.onmessage = (event: MessageEvent) => {
      try {
        const message: ChatMessage = JSON.parse(event.data)

        if (message.type === 'ERROR') {
          error.value = message.content
          return
        }

        messages.value.push(message)
      } catch {
        error.value = 'Recieveed an invalid response'
      }
    }

    socket.onerror = () => {
      error.value = 'Websocket conection error'
    }

    socket.onclose = () => {
      connected.value = false
    }
  }

  function sendMessage(recipient: string, content: string): boolean {
    error.value = ''

    if (socket === null || socket.readyState !== WebSocket.OPEN) {
      error.value = 'you are not connected'
      return false
    }

    if (!recipient.trim()) {
      error.value = 'Please enter a recipient'
      return false
    }

    if (!content.trim()) {
      error.value = 'Please enter a message'
      return false
    }

    socket.send(
      JSON.stringify({
        type: 'CHAT',
        to: recipient.trim(),
        content: content.trim(),
        clientMessageId: crypto.randomUUID(),
      }),
    )

    return true
  }

  function disconnect() {
    if (socket !== null) {
      socket.close()
      socket = null
    }

    connected.value = false
  }

  return {
    messages,
    connected,
    error,
    connect,
    sendMessage,
    disconnect,
  }
}
