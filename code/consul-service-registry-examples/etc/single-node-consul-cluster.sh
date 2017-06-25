#!/usr/bin/env bash

BASEDIR=$(dirname $BASH_SOURCE)

mkdir -p $BASEDIR/../target/data

consul agent \
  -server \
  -data-dir="$BASEDIR/../target/data" \
  -bootstrap-expect 1 \
  -bind=127.0.0.1 \
  -ui