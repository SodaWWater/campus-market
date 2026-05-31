<template>
  <div>
    <el-card header="站内消息">
      <el-tabs v-model="tab">
        <el-tab-pane label="会话列表" name="conversations">
          <el-table :data="conversations" v-if="conversations.length > 0" @row-click="openChat" highlight-current-row>
            <el-table-column prop="otherUserId" label="用户ID" width="100" />
            <el-table-column prop="lastMessage" label="最后消息" />
            <el-table-column label="未读" width="80">
              <template #default="{ row }"><el-tag v-if="row.unreadCount > 0" type="danger" size="small">{{ row.unreadCount }}</el-tag></template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无消息" />
        </el-tab-pane>
        <el-tab-pane label="聊天" name="chat" v-if="chatUser">
          <el-button text @click="chatUser = null; tab = 'conversations'" style="margin-bottom:12px">← 返回会话列表</el-button>
          <div class="chat-box" ref="chatBox">
            <div v-for="msg in messages" :key="msg.id" :class="['chat-msg', msg.senderId === auth.userId ? 'mine' : 'other']">
              <div class="chat-bubble">{{ msg.content }}</div>
              <div class="chat-time">{{ msg.createdAt }}</div>
            </div>
            <el-empty v-if="messages.length === 0" description="暂无消息，发送第一条吧" :image-size="60" />
          </div>
          <el-input v-model="newMsg" placeholder="输入消息..." @keyup.enter="doSend">
            <template #append><el-button @click="doSend">发送</el-button></template>
          </el-input>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { listConversations, getConversation, sendMessage as apiSendMessage, markMessageRead } from '../api/market'

const auth = useAuthStore()
const tab = ref('conversations')
const conversations = ref([])
const messages = ref([])
const chatUser = ref(null)
const newMsg = ref('')
const chatBox = ref(null)

async function loadConversations() { conversations.value = await listConversations() }
async function openChat(row) {
  chatUser.value = row.otherUserId
  tab.value = 'chat'
  messages.value = await getConversation(row.otherUserId)
  for (const m of messages.value) { if (m.receiverId === auth.userId && m.readStatus === 'UNREAD') await markMessageRead(m.id) }
  await nextTick()
  scrollToBottom()
}
async function doSend() {
  if (!newMsg.value.trim()) return
  await apiSendMessage({ receiverId: chatUser.value, content: newMsg.value })
  newMsg.value = ''
  messages.value = await getConversation(chatUser.value)
  await nextTick()
  scrollToBottom()
}
function scrollToBottom() {
  const el = chatBox.value
  if (el) el.scrollTop = el.scrollHeight
}
onMounted(loadConversations)
</script>

<style scoped>
.chat-box { height: 400px; overflow-y: auto; border: 1px solid #e4e7ed; border-radius: 8px; padding: 16px; margin-bottom: 12px; background: #fafafa; }
.chat-msg { margin-bottom: 14px; }
.chat-msg.mine { text-align: right; }
.chat-msg.mine .chat-bubble { background: #409eff; color: #fff; }
.chat-msg.other .chat-bubble { background: #fff; border: 1px solid #e4e7ed; }
.chat-bubble { max-width: 70%; padding: 10px 14px; border-radius: 12px; display: inline-block; word-break: break-word; text-align: left; }
.chat-time { font-size: 11px; color: #909399; margin-top: 4px; }
</style>
