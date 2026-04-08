const ACCESS_TOKEN_KEY = "trailnote_access_token";

let unauthorizedHandler: (() => void) | null = null;

export function getAccessToken() {
  return String(uni.getStorageSync(ACCESS_TOKEN_KEY) || "");
}

export function setAccessToken(token: string) {
  uni.setStorageSync(ACCESS_TOKEN_KEY, token);
}

export function clearAccessToken() {
  uni.removeStorageSync(ACCESS_TOKEN_KEY);
}

export function onUnauthorized(handler: (() => void) | null) {
  unauthorizedHandler = handler;
}

export function triggerUnauthorized() {
  unauthorizedHandler?.();
}
