#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ADMIN_DIR="${ROOT_DIR}/vue-vben-admin"

if [[ ! -d "${ADMIN_DIR}" ]]; then
  echo "vue-vben-admin directory not found: ${ADMIN_DIR}" >&2
  exit 1
fi

echo "[1/4] Installing admin workspace dependencies..."
pnpm -C "${ADMIN_DIR}" install

echo "[2/4] Building shared workspace packages required by apps/web-ele..."
PACKAGES=(
  "@vben-core/design"
  "@vben-core/form-ui"
  "@vben-core/popup-ui"
  "@vben-core/layout-ui"
  "@vben-core/menu-ui"
  "@vben-core/tabs-ui"
  "@vben-core/composables"
  "@vben-core/icons"
  "@vben-core/shared"
  "@vben-core/typings"
)

for package_name in "${PACKAGES[@]}"; do
  echo "  - building ${package_name}"
  pnpm -C "${ADMIN_DIR}" -F "${package_name}" run build
done

echo "[3/4] Building TrailNote admin app..."
pnpm -C "${ADMIN_DIR}" -F @vben/web-ele run build

echo "[4/4] Admin workspace bootstrap complete."
echo "Build output: ${ADMIN_DIR}/apps/web-ele/dist"
