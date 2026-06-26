#!/usr/bin/env sh
set -e

MAVEN_VERSION="3.9.9"
PROJECT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_HOME="$PROJECT_DIR/.mvn/apache-maven-$MAVEN_VERSION"
MAVEN_ZIP="$PROJECT_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Apache Maven $MAVEN_VERSION..."
    mkdir -p "$PROJECT_DIR/.mvn"
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL "$MAVEN_URL" -o "$MAVEN_ZIP"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$MAVEN_URL" -O "$MAVEN_ZIP"
    else
        echo "curl or wget is required to download Maven."
        exit 1
    fi
    unzip -o "$MAVEN_ZIP" -d "$PROJECT_DIR/.mvn" >/dev/null
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
