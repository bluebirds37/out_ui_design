#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

LOCAL_JDK_HOME=""
if [[ -d "${ROOT_DIR}/.tools/jdk-17" ]]; then
  LOCAL_JDK_HOME="${ROOT_DIR}/.tools/jdk-17/Contents/Home"
elif [[ -d "${ROOT_DIR}/.tools/jdk-17.jdk" ]]; then
  LOCAL_JDK_HOME="${ROOT_DIR}/.tools/jdk-17.jdk/Contents/Home"
fi

LOCAL_MAVEN_HOME=""
if [[ -d "${ROOT_DIR}/.tools/apache-maven" ]]; then
  LOCAL_MAVEN_HOME="${ROOT_DIR}/.tools/apache-maven"
fi

if [[ -n "${LOCAL_JDK_HOME}" && -x "${LOCAL_JDK_HOME}/bin/java" ]]; then
  export JAVA_HOME="${LOCAL_JDK_HOME}"
elif [[ -x /opt/homebrew/opt/openjdk@17/bin/java ]]; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
elif [[ -x /opt/homebrew/opt/openjdk/bin/java ]]; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk"
fi

if [[ -n "${JAVA_HOME:-}" ]]; then
  export PATH="${JAVA_HOME}/bin:${PATH}"
fi

if [[ -n "${LOCAL_MAVEN_HOME}" && -x "${LOCAL_MAVEN_HOME}/bin/mvn" ]]; then
  export MAVEN_HOME="${LOCAL_MAVEN_HOME}"
  export PATH="${MAVEN_HOME}/bin:${PATH}"
elif [[ -x /opt/homebrew/bin/mvn ]]; then
  export MAVEN_HOME="/opt/homebrew"
  export PATH="/opt/homebrew/bin:${PATH}"
fi
