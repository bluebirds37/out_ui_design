#!/usr/bin/env bash

set -euo pipefail

API_BASE_URL="${API_BASE_URL:-http://127.0.0.1:8080}"
DEBUG_TOKEN="${DEBUG_TOKEN:-mock-token-for-bootstrap}"
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

echo "== uni-app H5 API check =="
echo "API: ${API_BASE_URL}"

health_payload="$(curl -fsS "${API_BASE_URL}/api/public/health")" || fail "Business API health request failed"
assert_success "$health_payload" "Business API health"

login_payload="$(curl -fsS -X POST "${API_BASE_URL}/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"account":"hiker@trailnote.app","password":"123456"}')" || fail "Login request failed"
assert_success "$login_payload" "uni-app login"
pass "Debug token in use: ${DEBUG_TOKEN}"

map_payload="$(curl -fsS "${API_BASE_URL}/api/search/map?q=%E5%B1%B1%E8%84%8A")" || fail "Map search request failed"
assert_success "$map_payload" "Map search"
pass "Map result count: $(python3 -c 'import json,sys; print(len(json.loads(sys.argv[1])["data"]))' "$map_payload")"

profile_payload="$(curl -fsS "${API_BASE_URL}/api/me")" || fail "Profile request failed"
assert_success "$profile_payload" "My profile"
pass "Profile nickname: $(json_get "$profile_payload" "data.nickname")"

draft_payload="$(curl -fsS "${API_BASE_URL}/api/creator/drafts/current")" || fail "Current draft request failed"
assert_success "$draft_payload" "Current draft"
pass "Current draft title: $(json_get "$draft_payload" "data.title")"

if [[ "$RUN_MUTATIONS" == "--mutate" ]]; then
  update_payload="$(curl -fsS -X PUT "${API_BASE_URL}/api/me" \
    -H 'Content-Type: application/json' \
    -d '{"nickname":"景野","avatarUrl":"https://static.trailnote.dev/avatar/jingye.jpg","bio":"偏爱山脊线、湖边营地与日出路线记录","city":"上海","levelLabel":"轻中度徒步"}')" || fail "Profile update failed"
  assert_success "$update_payload" "Profile update"

  draft_create_payload="$(curl -fsS -X POST "${API_BASE_URL}/api/creator/drafts/save" \
    -H 'Content-Type: application/json' \
    -d '{"routeId":null,"title":"uniapp-api-check","coverUrl":"","description":"scripted creator check","difficulty":"INTERMEDIATE","distanceKm":4.2,"durationMinutes":55,"ascentM":320,"maxAltitudeM":1450,"tags":["uni-app","script-check"],"waypoints":[{"id":null,"title":"脚本调试点位","waypointType":"VIEWPOINT","description":"用于 uni-app H5 接口回归","latitude":30.23591,"longitude":120.10458,"altitudeM":1450,"sortOrder":1,"mediaList":[]}]}')" || fail "Draft save failed"
  assert_success "$draft_create_payload" "Draft save"
  draft_id="$(json_get "$draft_create_payload" "data.id")"
  pass "Created draft id: ${draft_id}"

  publish_payload="$(curl -fsS -X POST "${API_BASE_URL}/api/creator/drafts/${draft_id}/publish")" || fail "Draft publish failed"
  assert_success "$publish_payload" "Draft publish"
  pass "Published draft status: $(json_get "$publish_payload" "data.status")"
fi

echo "== uni-app H5 API check completed =="
