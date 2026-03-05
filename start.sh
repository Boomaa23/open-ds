#!/bin/bash
cd "$(dirname "$0")"
mvn -B package -q 2>/dev/null
java -jar target/open-ds-*-jar-with-dependencies.jar --disable-hotkeys "$@"
