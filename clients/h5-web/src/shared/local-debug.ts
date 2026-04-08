export type DebugScreenTarget =
  | "discover"
  | "search"
  | "profile"
  | "route-detail"
  | "author-profile"
  | "record-live"
  | "creator-studio"
  | "map-results"
  | "add-waypoint";

export function isLocalH5Debug() {
  if (typeof window === "undefined") {
    return false;
  }
  return ["127.0.0.1", "localhost"].includes(window.location.hostname);
}

export function readDebugParams() {
  if (typeof window === "undefined" || !isLocalH5Debug()) {
    return {
      token: "",
      screen: "",
    };
  }

  const params = new URLSearchParams(window.location.search);
  return {
    token: params.get("debugToken") || "",
    screen: params.get("debugScreen") || "",
  };
}
