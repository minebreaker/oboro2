#!/bin/bash

if [[ -z "$1" ]]; then
    exit 1
fi

native-image \
    --no-fallback \
    -jar "./build/libs/$1"
