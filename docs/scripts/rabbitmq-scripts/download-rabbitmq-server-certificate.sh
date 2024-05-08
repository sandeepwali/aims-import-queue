#!/bin/bash

HOST=20.79.211.250
PORT=5671
SERVERNAME=rabbitmq-aims-lidl-staging

echo -n | openssl s_client -connect $HOST:$PORT -servername $SERVERNAME \
    | openssl x509 > $SERVERNAME.crt