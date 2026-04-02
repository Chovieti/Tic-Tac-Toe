// auth.js – JWT version
const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const USER_ID_KEY = 'user_id';
const USER_LOGIN_KEY = 'user_login';

let isRefreshing = false;
let refreshSubscribers = [];

function saveTokens(accessToken, refreshToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
}

function getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
}

function saveUserId(id) {
    localStorage.setItem(USER_ID_KEY, id);
}

function getUserId() {
    return localStorage.getItem(USER_ID_KEY);
}

function saveUserLogin(login) {
    localStorage.setItem(USER_LOGIN_KEY, login);
}

function getUserLogin() {
    return localStorage.getItem(USER_LOGIN_KEY);
}

function isAuthenticated() {
    return !!getAccessToken();
}

function logout() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_ID_KEY);
    localStorage.removeItem(USER_LOGIN_KEY);
    window.location.href = '/login.html';
}

function getAuthHeaders() {
    const token = getAccessToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

async function refreshAccessToken() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) throw new Error('No refresh token');

    const response = await fetch('/auth/token', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
    });
    if (!response.ok) {
        throw new Error('Refresh failed');
    }
    const data = await response.json();
    // data = { type, accessToken, refreshToken }
    saveTokens(data.accessToken, data.refreshToken);
    return data.accessToken;
}

async function fetchWithAutoRefresh(url, options = {}) {
    let accessToken = getAccessToken();
    const makeRequest = (token) => fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        }
    });

    let response = await makeRequest(accessToken);
    if (response.status !== 401) return response;

    // 401 – пытаемся обновить токен
    if (isRefreshing) {
        // Если уже идёт обновление, ждём новый токен
        return new Promise((resolve) => {
            refreshSubscribers.push((newToken) => {
                resolve(makeRequest(newToken));
            });
        });
    }

    isRefreshing = true;
    try {
        const newToken = await refreshAccessToken();
        refreshSubscribers.forEach(cb => cb(newToken));
        refreshSubscribers = [];
        return makeRequest(newToken);
    } catch (e) {
        logout();
        throw new Error('Session expired');
    } finally {
        isRefreshing = false;
    }
}

async function authFetch(url, options = {}) {
    const response = await fetchWithAutoRefresh(url, options);
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Ошибка ${response.status}`);
    }
    return response;
}

async function fetchCurrentUser() {
    const response = await authFetch('/auth/me');
    const auth = await response.json(); // { userId, authorities, authenticated }
    return { userId: auth.principal, roles: auth.authorities };
}