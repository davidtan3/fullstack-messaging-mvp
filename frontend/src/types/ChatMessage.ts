export type MessageType = 'CHAT' | 'ERROR'

export interface ChatMessage {
  type: MessageType
  from: string | null
  to: string | null
  content: string
  clientMessageId: string | null
  sentAt: string | null
}
