export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

export interface ApiErrorShape {
  code: string;
  message: string;
  status: number;
  requestId?: string | null;
}

export class ApiClientError extends Error {
  code: string;
  status: number;
  requestId?: string | null;

  constructor(shape: ApiErrorShape) {
    super(shape.message);
    this.name = "ApiClientError";
    this.code = shape.code;
    this.status = shape.status;
    this.requestId = shape.requestId;
  }
}

type Primitive = string | number | boolean | null | undefined;
type QueryValue = Primitive | Primitive[];

export interface HttpClientOptions {
  baseUrl?: string;
  timeoutMs?: number;
  retry?: {
    retries: number;
    delayMs: number;
    retryMethods?: string[];
    retryStatuses?: number[];
  };
  getAccessToken?: () => string | null;
  defaultHeaders?: HeadersInit;
  onUnauthorized?: (error: ApiClientError) => void;
}

export interface RequestOptions extends Omit<RequestInit, "body"> {
  body?: unknown;
  query?: Record<string, QueryValue>;
  timeoutMs?: number;
  skipAuth?: boolean;
  responseType?: "json" | "text";
  retry?: {
    retries?: number;
    delayMs?: number;
  };
}

const DEFAULT_TIMEOUT_MS = 8000;
const DEFAULT_RETRY_METHODS = ["GET"];
const DEFAULT_RETRY_STATUSES = [408, 429, 500, 502, 503, 504];

function sleep(delayMs: number) {
  return new Promise((resolve) => {
    window.setTimeout(resolve, delayMs);
  });
}

function buildQuery(query?: Record<string, QueryValue>) {
  if (!query) {
    return "";
  }

  const searchParams = new URLSearchParams();
  for (const [key, rawValue] of Object.entries(query)) {
    if (Array.isArray(rawValue)) {
      rawValue
        .filter((value) => value !== null && value !== undefined && value !== "")
        .forEach((value) => {
          searchParams.append(key, String(value));
        });
      continue;
    }

    if (rawValue === null || rawValue === undefined || rawValue === "") {
      continue;
    }

    searchParams.set(key, String(rawValue));
  }

  const queryString = searchParams.toString();
  return queryString ? `?${queryString}` : "";
}

function normalizeError(error: unknown, fallbackMessage: string): ApiClientError {
  if (error instanceof ApiClientError) {
    return error;
  }

  if (error instanceof DOMException && error.name === "AbortError") {
    return new ApiClientError({
      code: "REQUEST_TIMEOUT",
      message: "请求超时，请稍后重试",
      status: 408,
    });
  }

  if (error instanceof Error) {
    return new ApiClientError({
      code: "NETWORK_ERROR",
      message: error.message || fallbackMessage,
      status: 0,
    });
  }

  return new ApiClientError({
    code: "UNKNOWN_ERROR",
    message: fallbackMessage,
    status: 0,
  });
}

export function createHttpClient(options: HttpClientOptions = {}) {
  const baseUrl = (options.baseUrl ?? "").replace(/\/$/, "");
  const timeoutMs = options.timeoutMs ?? DEFAULT_TIMEOUT_MS;
  const retryConfig = {
    retries: options.retry?.retries ?? 0,
    delayMs: options.retry?.delayMs ?? 300,
    retryMethods: options.retry?.retryMethods ?? DEFAULT_RETRY_METHODS,
    retryStatuses: options.retry?.retryStatuses ?? DEFAULT_RETRY_STATUSES,
  };

  async function request<T>(path: string, requestOptions: RequestOptions = {}): Promise<T> {
    const method = (requestOptions.method ?? "GET").toUpperCase();
    const retries = requestOptions.retry?.retries ?? retryConfig.retries;
    const delayMs = requestOptions.retry?.delayMs ?? retryConfig.delayMs;
    const url = `${baseUrl}${path}${buildQuery(requestOptions.query)}`;

    for (let attempt = 0; attempt <= retries; attempt += 1) {
      const controller = new AbortController();
      const timer = window.setTimeout(() => controller.abort(), requestOptions.timeoutMs ?? timeoutMs);

      try {
        const token = requestOptions.skipAuth ? null : options.getAccessToken?.();
        const headers = new Headers(options.defaultHeaders);
        headers.set("Accept", "application/json");

        if (requestOptions.body !== undefined) {
          headers.set("Content-Type", "application/json");
        }

        if (token) {
          headers.set("Authorization", `Bearer ${token}`);
        }

        if (requestOptions.headers) {
          new Headers(requestOptions.headers).forEach((value, key) => {
            headers.set(key, value);
          });
        }

        const response = await fetch(url, {
          ...requestOptions,
          method,
          headers,
          body:
            requestOptions.body === undefined ? undefined : JSON.stringify(requestOptions.body),
          signal: controller.signal,
        });

        const requestId = response.headers.get("x-request-id");

        if (!response.ok) {
          const error = new ApiClientError({
            code: `HTTP_${response.status}`,
            message: `HTTP ${response.status}`,
            status: response.status,
            requestId,
          });
          if (response.status === 401) {
            options.onUnauthorized?.(error);
          }

          if (
            attempt < retries &&
            retryConfig.retryMethods.includes(method) &&
            retryConfig.retryStatuses.includes(response.status)
          ) {
            await sleep(delayMs * (attempt + 1));
            continue;
          }

          throw error;
        }

        if (requestOptions.responseType === "text") {
          return (await response.text()) as T;
        }

        const payload = (await response.json()) as ApiResponse<T>;
        if (!payload.success) {
          const error = new ApiClientError({
            code: payload.code || "REQUEST_FAILED",
            message: payload.message || "请求失败",
            status: response.status,
            requestId,
          });
          if (response.status === 401 || error.code === "UNAUTHORIZED") {
            options.onUnauthorized?.(error);
          }
          throw error;
        }

        return payload.data;
      } catch (error) {
        const normalizedError = normalizeError(error, "网络请求失败");

        if (
          attempt < retries &&
          retryConfig.retryMethods.includes(method) &&
          retryConfig.retryStatuses.includes(normalizedError.status)
        ) {
          await sleep(delayMs * (attempt + 1));
          continue;
        }

        throw normalizedError;
      } finally {
        window.clearTimeout(timer);
      }
    }

    throw new ApiClientError({
      code: "UNREACHABLE",
      message: "请求未命中返回分支",
      status: 0,
    });
  }

  return {
    request,
  };
}
