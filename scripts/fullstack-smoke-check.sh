#!/usr/bin/env bash

set -euo pipefail

API_BASE_URL="${API_BASE_URL:-http://127.0.0.1:8080}"
ADMIN_API_BASE_URL="${ADMIN_API_BASE_URL:-http://127.0.0.1:8081}"
RUN_MUTATIONS="${1:-}"

fail() {
  echo "[FAIL] $1" >&2
  exit 1
}

pass() {
  echo "[PASS] $1"
}

json_get() {
  local payload="$1"
  local expression="$2"
  python3 -c 'import json,sys
payload=json.loads(sys.argv[1])
value=payload
for part in sys.argv[2].split("."):
    if not part:
        continue
    if part.isdigit():
        value=value[int(part)]
    else:
        value=value.get(part)
print("" if value is None else value)' "$payload" "$expression"
}

assert_success() {
  local payload="$1"
  local label="$2"
  local success
  success="$(json_get "$payload" "success")"
  [[ "$success" == "True" || "$success" == "true" ]] || fail "$label did not return success=true"
  pass "$label"
}

echo "== TrailNote full-stack smoke check =="
echo "API: ${API_BASE_URL}"
echo "Admin API: ${ADMIN_API_BASE_URL}"

api_health="$(curl -fsS "${API_BASE_URL}/actuator/health")" || fail "API health endpoint unavailable"
admin_health="$(curl -fsS "${ADMIN_API_BASE_URL}/actuator/health")" || fail "Admin API health endpoint unavailable"
[[ "$(json_get "$api_health" "status")" == "UP" ]] || fail "API health is not UP"
[[ "$(json_get "$admin_health" "status")" == "UP" ]] || fail "Admin API health is not UP"
pass "API health is UP"
pass "Admin API health is UP"

api_login="$(curl -fsS -X POST "${API_BASE_URL}/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"account":"tester@example.com","password":"123456"}')" || fail "API login request failed"
assert_success "$api_login" "API login"

profile_payload="$(curl -fsS "${API_BASE_URL}/api/me")" || fail "Profile request failed"
assert_success "$profile_payload" "My profile"
[[ -n "$(json_get "$profile_payload" "data.nickname")" ]] || fail "Profile nickname is empty"
pass "Profile nickname present"

search_payload="$(curl -fsS "${API_BASE_URL}/api/search?q=%E5%B1%B1%E8%84%8A")" || fail "Search request failed"
assert_success "$search_payload" "Search"
pass "Search routes count: $(python3 -c 'import json,sys; print(len(json.loads(sys.argv[1])["data"]["routes"]))' "$search_payload")"

favorites_payload="$(curl -fsS "${API_BASE_URL}/api/me/favorites?page=1&pageSize=3")" || fail "Favorites request failed"
assert_success "$favorites_payload" "My favorites"
pass "Favorites total: $(json_get "$favorites_payload" "data.total")"

comments_payload="$(curl -fsS "${API_BASE_URL}/api/routes/1001/comments?page=1&pageSize=3")" || fail "Comments request failed"
assert_success "$comments_payload" "Route comments"
pass "Top comment sample: $(json_get "$comments_payload" "data.records.0.content")"

admin_login="$(curl -fsS -X POST "${ADMIN_API_BASE_URL}/admin/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}')" || fail "Admin login request failed"
assert_success "$admin_login" "Admin login"
admin_access_token="$(json_get "$admin_login" "data.accessToken")"
[[ -n "$admin_access_token" ]] || fail "Admin access token is empty"

dashboard_payload="$(curl -fsS "${ADMIN_API_BASE_URL}/admin/dashboard/overview" \
  -H "Authorization: Bearer ${admin_access_token}")" || fail "Admin dashboard request failed"
assert_success "$dashboard_payload" "Admin dashboard overview"
pass "Dashboard total routes: $(json_get "$dashboard_payload" "data.totalRoutes")"

if [[ "$RUN_MUTATIONS" == "--mutate" ]]; then
  echo "== Running mutation checks =="

  favorite_payload="$(curl -fsS -X POST "${API_BASE_URL}/api/routes/1001/favorite")" || fail "Favorite toggle failed"
  assert_success "$favorite_payload" "Favorite toggle"
  pass "Route 1001 favorite count now: $(json_get "$favorite_payload" "data.favoriteCount")"

  comment_text="smoke-check-$(date +%Y%m%d%H%M%S)"
  comment_create_payload="$(curl -fsS -X POST "${API_BASE_URL}/api/routes/1001/comments" \
    -H 'Content-Type: application/json' \
    -d "{\"content\":\"${comment_text}\"}")" || fail "Comment publish failed"
  assert_success "$comment_create_payload" "Comment publish"
  pass "Published comment: ${comment_text}"

  follow_payload="$(curl -fsS -X POST "${API_BASE_URL}/api/authors/1002/follow")" || fail "Follow toggle failed"
  assert_success "$follow_payload" "Author follow toggle"
  pass "Author 1002 follower count now: $(json_get "$follow_payload" "data.followerCount")"
fi

echo "== Smoke check completed =="
