import { defineStore } from 'pinia'

const STORAGE_KEY = 'campus-market-auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    userId: null,
    username: '',
    role: ''
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    isAdmin: (state) => state.role === 'ADMIN'
  },
  actions: {
    load() {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (!raw) return
      Object.assign(this, JSON.parse(raw))
    },
    save(payload) {
      this.token = payload.token
      this.userId = payload.userId
      this.username = payload.username
      this.role = payload.role
      localStorage.setItem(STORAGE_KEY, JSON.stringify({
        token: this.token,
        userId: this.userId,
        username: this.username,
        role: this.role
      }))
    },
    logout() {
      this.token = ''
      this.userId = null
      this.username = ''
      this.role = ''
      localStorage.removeItem(STORAGE_KEY)
    }
  }
})
