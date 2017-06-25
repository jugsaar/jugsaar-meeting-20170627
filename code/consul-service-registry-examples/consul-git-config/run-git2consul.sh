#!/usr/bin/env bash

git2consul --endpoint 127.0.0.1 --port 8500 --config-file ./git2consul.json | jq .
