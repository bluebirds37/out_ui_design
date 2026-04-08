#!/usr/bin/env bash

set -euo pipefail

echo "== Toolchain =="
command -v node >/dev/null 2>&1 && node -v || echo "node: missing"
command -v npm >/dev/null 2>&1 && npm -v || echo "npm: missing"
command -v pnpm >/dev/null 2>&1 && pnpm -v || echo "pnpm: missing"
command -v brew >/dev/null 2>&1 && brew --version | head -n 1 || echo "brew: missing"
command -v mysql >/dev/null 2>&1 && mysql --version || echo "mysql: missing"
command -v mvn >/dev/null 2>&1 && mvn -version | head -n 1 || echo "maven: missing"

echo
echo "== Java =="
if [[ -x /opt/homebrew/opt/openjdk@17/bin/java ]]; then
  /opt/homebrew/opt/openjdk@17/bin/java -version
else
  echo "openjdk@17: missing"
fi

echo
echo "== Homebrew packages =="
brew list --versions mysql openjdk@17 maven 2>/dev/null || true
