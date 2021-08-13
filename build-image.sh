#!/bin/bash

native-image \
    --no-fallback \
    -jar "./build/libs/oboro-0.1-SNAPSHOT.jar"
