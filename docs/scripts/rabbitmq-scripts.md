# RabbitMQ Scripts

This is a script collection to interact with RabbitMQ in python.

## Contributors

* Glenn Goffin <glenn.goffin@solumesl.com>
* Dominik Hannen <dominik.hannen@solumesl.com>

## Requirements

* OpenSSL
* Python 3
* python3 pika

## Setup environemt with apt

### Install dependencies with apt

```
sudo apt install python3.7 python3-venv python3.7-venv python3-pip openssl
```

### Setup python venv

```
python3 -m venv venv
```

### Active python venv

```
. venv/bin/activate
```

## rabbitmq-scripts
### Download RabbitMQ Server Certificate

This script will use openssl to download the server certificate from the RabbitMQ Server.

* Download Script: [download-rabbitmq-server-certificate.sh](./rabbitmq-scripts/download-rabbitmq-server-certificate.sh)
* Download Rabbitmq AIMS LiDL Staging Certificate: [rabbitmq-aims-lidl-staging.crt](./rabbitmq-scripts/rabbitmq-aims-lidl-staging.crt)

```
HOST=20.79.211.250
PORT=5671
SERVERNAME=rabbitmq-aims-lidl-staging

echo -n | openssl s_client -connect $HOST:$PORT -servername $SERVERNAME \
    | openssl x509 > $SERVERNAME.crt
```
