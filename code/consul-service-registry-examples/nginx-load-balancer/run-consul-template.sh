#!/usr/bin/env bash

consul-template \
    -template "conf/nginx.conf.ctmpl:conf/nginx.conf"