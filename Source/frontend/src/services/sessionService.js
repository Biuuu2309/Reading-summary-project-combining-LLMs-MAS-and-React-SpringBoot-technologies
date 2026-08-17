// Session management service

import { getStoredUser } from './authService';
import { createMASSession, getMASSessions, getMASSession } from './masApi';

const GUEST_USER_ID =
  import.meta.env.VITE_GUEST_USER_ID || '34f8a78d-cd08-41fa-8835-a9b72c4f7c44';

const LOGGED_IN_SESSION_KEY = 'active_session_id';
const GUEST_SESSION_KEY = 'guest_mas_session_id';

export function isLoggedIn() {
  return Boolean(getStoredUser()?.userId);
}

/**
 * Real logged-in user id, or null when anonymous.
 */
export function getCurrentUserId() {
  return getStoredUser()?.userId ?? null;
}

/**
 * User id for MAS/backend calls. Anonymous uses shared guest account (not listed in history UI).
 */
export function getApiUserId() {
  return getCurrentUserId() ?? GUEST_USER_ID;
}

export function shouldPersistHistory() {
  return isLoggedIn();
}

function getSessionStorageKey() {
  return isLoggedIn() ? LOGGED_IN_SESSION_KEY : GUEST_SESSION_KEY;
}

function readStoredSessionId() {
  const key = getSessionStorageKey();
  if (isLoggedIn()) {
    return localStorage.getItem(key);
  }
  return sessionStorage.getItem(key);
}

function writeStoredSessionId(sessionId) {
  const key = getSessionStorageKey();
  if (isLoggedIn()) {
    localStorage.setItem(key, sessionId);
  } else {
    sessionStorage.setItem(key, sessionId);
  }
}

function removeStoredSessionId() {
  localStorage.removeItem(LOGGED_IN_SESSION_KEY);
  sessionStorage.removeItem(GUEST_SESSION_KEY);
}

/**
 * Create or get existing session
 */
export async function getOrCreateSession(userId, conversationId = null) {
  try {
    const activeSessionId = readStoredSessionId();
    if (activeSessionId) {
      try {
        await getMASSession(activeSessionId, userId);
        return activeSessionId;
      } catch {
        removeStoredSessionId();
      }
    }

    const response = await createMASSession({ userId, conversationId });
    const sessionId = response.sessionId;
    writeStoredSessionId(sessionId);
    return sessionId;
  } catch (error) {
    console.error('Get or create session error:', error);
    throw error;
  }
}

export async function loadUserSessions(userId) {
  try {
    const sessions = await getMASSessions(userId);
    return sessions.map((session) => ({
      id: session.sessionId,
      sessionId: session.sessionId,
      userId: session.userId,
      conversationId: session.conversationId,
      status: session.status,
      createdAt: session.createdAt,
      updatedAt: session.updatedAt,
    }));
  } catch (error) {
    console.error('Load user sessions error:', error);
    return [];
  }
}

export function clearActiveSession() {
  removeStoredSessionId();
}

export function clearGuestSession() {
  sessionStorage.removeItem(GUEST_SESSION_KEY);
}

export function setActiveSession(sessionId) {
  writeStoredSessionId(sessionId);
}

export function getActiveSessionId() {
  return readStoredSessionId();
}

export default {
  isLoggedIn,
  getCurrentUserId,
  getApiUserId,
  shouldPersistHistory,
  getOrCreateSession,
  loadUserSessions,
  clearActiveSession,
  clearGuestSession,
  setActiveSession,
  getActiveSessionId,
};
