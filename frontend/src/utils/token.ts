// utils/token.ts
const KEY = 'accessToken';
export const tokenManager = {
    get: () => localStorage.getItem(KEY),
    set: (t: string) => localStorage.setItem(KEY, t),
    clear: () => localStorage.removeItem(KEY),
};
