#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
source "${ROOT_DIR}/scripts/java-env.sh"

if ! command -v java >/dev/null 2>&1; then
  echo "java is not available yet" >&2
  exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "mvn is not available yet" >&2
  exit 1
fi

cd "${ROOT_DIR}/server"
mvn test
