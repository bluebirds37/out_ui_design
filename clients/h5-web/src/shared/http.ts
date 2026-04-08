import { getAccessToken, triggerUnauthorized } from "./session";

export interface ApiEnvelope<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

export interface ApiClientErrorShape {
  code: string;
  message: string;
  status: number;
}

export class ApiClientError extends Error {
  code: string;
  status: number;

  constructor(shape: ApiClientErrorShape) {
    super(shape.message);
    this.name = "ApiClientError";
    this.code = shape.code;
    this.status = shape.status;
  }
}

type Primitive = string | number | boolean | null | undefined;
type QueryValue = Primitive | Primitive[];

export interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  query?: Record<string, QueryValue>;
  body?: Record<string, unknown>;
  timeoutMs?: number;
  skipAuth?: boolean;
}

const DEFAULT_TIMEOUT_MS = 8000;
const RETRYABLE_STATUSES = new Set([408, 429, 500, 502, 503, 504]);

function sleep(delayMs: number) {
  return new Promise((resolve) => {
    setTimeout(resolve, delayMs);
  });
}

function resolveBaseUrls() {
  const isH5 = typeof window !== "undefined" && typeof document !== "undefined";
  const storedPrimary = String(uni.getStorageSync("trailnote_api_base_url") || "").trim();
  const storedCandidates = uni.getStorageSync("trailnote_api_base_urls");
  const storedList = Array.isArray(storedCandidates) ? storedCandidates.map((item) => String(item)) : [];

  if (isH5) {
    return [""];
  }

  const defaults = [
    storedPrimary,
    ...storedList,
    "http://127.0.0.1:8080",
    "http://192.168.0.174:8080",
  ];

  const unique = new Set<string>();
  const result: string[] = [];
  defaults.forEach((url) => {
    const raw = String(url ?? "").trim();
    const normalized = raw.replace(/\/$/, "");
    if (!normalized || unique.has(normalized)) {
      return;
    }
    unique.add(normalized);
    result.push(normalized);
  });
  return result;
}

function normalizeError(error: unknown) {
  if (error instanceof ApiClientError) {
    return error;
  }

  return new ApiClientError({
    code: "NETWORK_ERROR",
    message: typeof error === "string" ? error : "网络请求失败",
    status: 0,
  });
}

async function execute<T>(baseUrl: string, path: string, options: RequestOptions = {}): Promise<T> {
  const method = options.method ?? "GET";
  const token = options.skipAuth ? "" : getAccessToken();

  return new Promise<T>((resolve, reject) => {
    uni.request({
      url: `${baseUrl}${path}`,
      method,
      timeout: options.timeoutMs ?? DEFAULT_TIMEOUT_MS,
      data: method === "GET" ? options.query : options.body,
      header: {
        Accept: "application/json",
        ...(method !== "GET" && options.body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      success(response) {
        const payload = (response.data || {}) as ApiEnvelope<T>;
        const status = response.statusCode || 0;

        if (status === 401 || payload.code === "UNAUTHORIZED") {
          triggerUnauthorized();
        }

        if (status >= 200 && status < 300 && payload.success) {
          resolve(payload.data);
          return;
        }

        reject(
          new ApiClientError({
            code: payload.code || `HTTP_${status}`,
            message: payload.message || `HTTP ${status}`,
            status,
          }),
        );
      },
      fail(error) {
        reject(
          new ApiClientError({
            code: "NETWORK_ERROR",
            message: error.errMsg || "网络请求失败",
            status: 0,
          }),
        );
      },
    });
  });
}

export async function request<T>(path: string, options: RequestOptions = {}) {
  const baseUrls = resolveBaseUrls();
  let lastError: ApiClientError | null = null;

  for (const baseUrl of baseUrls) {
    try {
      return await execute<T>(baseUrl, path, options);
    } catch (error) {
      const normalized = normalizeError(error);
      lastError = normalized;

      if (normalized.status === 401 || normalized.code === "UNAUTHORIZED") {
        throw normalized;
      }

      if (normalized.status && !RETRYABLE_STATUSES.has(normalized.status)) {
        throw normalized;
      }

      await sleep(250);
    }
  }

  throw (
    lastError ||
    new ApiClientError({
      code: "NETWORK_ERROR",
      message: "网络请求失败",
      status: 0,
    })
  );
}
