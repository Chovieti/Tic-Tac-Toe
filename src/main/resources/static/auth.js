const AUTH_TOKEN_KEY = 'auth_token';
const AUTH_LOGIN_KEY = 'auth_login';
const USER_ID_KEY = 'user_id';

function saveCredentials(login, password) {
    localStorage.setItem(AUTH_LOGIN_KEY, login);
    const token = btoa(login + ':' + password);
    localStorage.setItem(AUTH_TOKEN_KEY, token);
}

function saveUserId(id) {
    localStorage.setItem(USER_ID_KEY, id);
}

function getUserId() {
    return localStorage.getItem(USER_ID_KEY);
}

function getAuthHeaders() {
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    if (!token) return {};
    return {
        'Authorization': 'Basic ' + token,
        'Content-Type': 'application/json'
    };
}

function isAuthenticated() {
    return !!localStorage.getItem(AUTH_TOKEN_KEY);
}

function logout() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_LOGIN_KEY);
    localStorage.removeItem(USER_ID_KEY);
    window.location.href = '/login.html';
}

async function handleResponse(response) {
    if (response.status === 401) {
        logout();
        throw new Error('Сессия истекла. Пожалуйста, войдите снова.');
    }
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Ошибка ${response.status}`);
    }
    return response;
}

async function authFetch(url, options = {}) {
    const headers = {
        ...getAuthHeaders(),
        ...(options.headers || {})
    };
    const response = await fetch(url, { ...options, headers });
    return handleResponse(response);
}