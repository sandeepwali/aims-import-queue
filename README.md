# aims-import-queue

Java middleware to import articles and EANs using RabbitMQ for LiDL.

## Scope

* Microservice to fetch messages syncronized from the Solace
## Features

### Phase 1 (MVP)

#### Solace Message Broker

* [X] Read article json from solace, combine and send to aims portal (queue:aims.import.queue.article)
  * [X] Implement #2 - Validate article message schema
  * [X] Deduplicate article information per store by merging the messages in the batch
  * [X] Send to portal in configurable batches (see [error-handling](#error-handling))
  * [X] Acknowledge message after it has been sent to aims-portal

#### Error Handling

* [X] Solace
  * [X] Solace is not reachable, then try again in 5 minutes.
* [X] aims-portal
  * [X] Implement #3 - Portal is not reachable (Client Timeout)
  * [X] Implement #4 - Portal respond with 4xx or 5xx message

### Phase 2 [SKIP]

* [X] ~~Create audit table for each request to aims portal~~
* [X] ~~Create REST Service to retreive audit records~~
* [X] ~~Create Swagger UI to publish REST Services~~

### Phase 3

* [X] Expose Prometheus endpoint with number of articles, mappingInfo processed, error
* [X] Expose Actuator for health monitoring

### Considerations

* What needs to happen if the aims portal returns 500?
* What needs to happen if aims-portal is time-out.
* How can we trace each message (log files?)

### Phase 4

#### Redirect to RabbitMQ

* [X] Read article json from solace, combine and send directly into to RabbitMQ 
      aims.portal.queue.batch.article queue.
* [X] Make aims_portal_batch JSON message to update the article.
* [X] Send to RabbitMQ in configurable batches.
* [X] Acknowledge message after it has been sent to RabbitMq exchange.

#### Error Handling

* [X] RabbitMq producer respond with nack or ack message.

## How To

### Run from harbor

```
docker pull harbor.solumesl.com/schwarz-it/lidl/aims-import-queue:0.0.6-SNAPSHOT
docker run -ti harbor.solumesl.com/schwarz-it/lidl/aims-import-queue:0.0.6-SNAPS6OT
```

## Variables

| Name | Default Value | 
| :---:| :--- |
| [TZ](#tz) |  Etc/GMT |
| [SERVER_PORT](#server_port) |  8000 |
| [JAVA_XMS](#java_xms) |  1g |
| [JAVA_XMX](#java_xmx) |  1g |
| [LOGGING_LEVEL_COM_SOLUM](#logging_level_com_solum) |  INFO |
| [SOLACE_HOST](#solace_host) |  <SOLACE_HOST> |
| [SOLACE_VPN](#solace_vpn) |  <SOLACE_VPN> |
| [SOLACE_USERNAME] (#solace_username) |  <SOLACE_USERNAME> |
| [SOLACE_PASSWORD](#solace_password) |  <SOLACE_PASSWORD> |
| [SOLACE_QUEUE](#solace_queue) |<SOLACE_QUEUE> |
| [SOLACE_ERROR_TOPIC](#solace_error_topic) |  <SOLACE_ERROR_TOPIC> |
| [SOLACE_SSL_VALIDATE_CERTIFICATE](#solace_ssl_validate_certificate) |  true |
| [SOLACE_SSL_TRUST_STORE_PASSWORD](#solace_ssl_trust_store_password) |  aimssuite |
| [SOLACE_SSL_TRUST_STORE](#solace_ssl_trust_store) |  /app/solace-certificates.jks |
| [AIMS_PROPERTIES_MAX_STORAGE_SIZE](#aims_properties_max_storage_size) |  4000 |
| [AIMS_PROPERTIES_SLEEP_TIME_ONCE_STORAGE_REACH_MAX_SIZE](#aims_properties_sleep_time_once_storage_reach_max_size) |  100 |
| [AIMS_PROPERTIES_BATCH_SIZE](#aims_properties_batch_size) |  1000 |
| [AIMS_PROPERTIES_SLEEP_TIME_FOR_BATCH_PROCESS_IF_STORAGE_IS_EMPTY](#aims_properties_sleep_time_for_batch_process_if_storage_is_empty) |  100 |
| [AIMS_URL](#aims_url) |  http://aims-portal:8000 |
| [AIMS_API_KEY](#aims_api_key) |   |
| [AIMS_PROPERTIES_BATCH_SIZE](#aims_properties_batch_size) |  1000 |
| [AIMS_PORTAL_QUEUE_ENABLED](#aims_portal_queue_enabled) |  true |
| [CUSTOMER_FILE_NUMBER_OF_ROWS_PER_ON] (#customer_file_number_of_rows_per_once) |  1000 |
| [RABBITMQ_ADDRESSES](#rabbitmq_addresses) | <RABBITMQ_ADDRESSES>  |
| [RABBITMQ_SSL_ENABLED](#rabbitmq_ssl_enabled) | <RABBITMQ_SSL_ENABLE> |
| [RABBITMQ_SSL_VERIFY_HOSTNAME](#rabbitmq_ssl_verify_hostname) | false |
| [RABBITMQ_SSL_KEY_STORE](#rabbitmq_ssl_key_store) | classpath:configuration/client_key_p12  |
| [RABBITMQ_SSL_KEY_STORE_PASSWORD](#rabbitmq_ssl_key_store_password) |  <RABBITMQ_SSL_STORE_PASSWORD> |
| [RABBITMQ_SSL_TRUST_STORE](#rabbitmq_ssl_trust_store) | classpath:configuration/rabbitstore_jks  |
| [RABBITMQ_SSL_TRUST_STORE_PASSWORD](#rabbitmq_ssl_trust_store_password) | <RABBITMQ_SSL_STORE_PASSWORD>  |
| [RABBITMQ_USERNAME](#rabbitmq_username) | <RABBITMQ_USERNAME>  |
| [RABBITMQ_PASSWORD](#rabbitmq_password) | <RABBITMQ_PASSWORD>  |
| [RABBITMQ_VIRTUAL_HOST](#rabbitmq_virtual_host) | aims_vhost  |
| [RABBITMQ_TEMPLATE_RETRY_ENABLED](#rabbitmq_template_retry_enabled) | true  |
| [RABBITMQ_TEMPLATE_RETRY_INITIAL_INTERVAL](#rabbitmq_template_retry_initial_interval) | 2000  |
| [RABBITMQ_PUBLISHER_CONFIRM_TYPE](#rabbitmq_publisher_confirm_type) | correlated  |
| [RABBITMQ_PUBLISHER_RETURNS](#rabbitmq_publisher_returns) | true  |
| [RABBITMQ_TEMPLATE_MANDATORY](#rabbitmq_template_mandatory) | true  |

### TZ

Timezone

https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

### SERVER_PORT

Listening port for incomming http requests

### JAVA_XMS

Initial Java heap size

### JAVA_XMX

Maximum Java heap size

### LOGGING_LEVEL_COM_SOLUM

Set Loglevel for com.solum java package

### SOLACE_HOST

Solace host format `<tcp/tcps>://<IP>:<PORT>`

* Example

```
tcps://10.20.30.50:55443
```

### SOLACE_VPN

Solace username 

### SOLACE_PASSWORD

Solace password

### SOLACE_QUEUE

Solace queue which will be used to consume messages

* Example

```
aims.import.queue.article
```

### SOLACE_ERROR_TOPIC

Solace topic which will be used to communicate errors as producer

* Example

```
aims/import/queue/error
```

### SOLACE_SSL_VALIDATE_CERTIFICATE

Flag which will inform the solace client to use SSL or not (true/false)

### SOLACE_SSL_TRUST_STORE_PASSWORD

Solace java certificate store password

### SOLACE_SSL_TRUST_STORE

Location of the solace java certificate store

### AIMS_PROPERTIES_MAX_STORAGE_SIZE

Maximum messages from solace

### AIMS_PROPERTIES_SLEEP_TIME_ONCE_STORAGE_REACH_MAX_SIZE

Fixed delay when max storage size is reached

### AIMS_PROPERTIES_BATCH_SIZE

Batch size to aims-portal

### AIMS_PROPERTIES_SLEEP_TIME_FOR_BATCH_PROCESS_IF_STORAGE_IS_EMPTY

Fixed delay after storage is empty 

### AIMS_URL

aims-portal URL

### AIMS_API_KEY

api-key in the header which will be sent to aims-portal

### AIMS_PROPERTIES_BATCH_SIZE

Number of message which will be fetched from solace and sent in batch to aims-portal

### AIMS_PORTAL_QUEUE_ENABLED

Flag which will be used to switch the feature of sending solace message via rabbitmq and aims-portal (true/false).

### RABBITMQ_ADDRESSES

RabbitMq host format `<amqps>://<IP>:<PORT>`

* Example

```
amqps://192.168.10.100:5671

```

### RABBITMQ_SSL_ENABLED

Flag which will inform the rabbtimq to use SSL or not (true/false).

### RABBITMQ_SSL_VERIFY_HOSTNAME

Property can set to false to disable host name verification.

### RABBITMQ_SSL_KEY_STORE

Path to the key store that holds the SSL certificate

### RABBITMQ_SSL_KEY_STORE_PASSWORD

Password used to access the key store.

### RABBITMQ_SSL_TRUST_STORE

Trust store that holds SSL certificates.

### RABBITMQ_SSL_TRUST_STORE_PASSWORD

Password used to access the trust store.

### RABBITMQ_USERNAME

RabbitMq username.

### RABBITMQ_PASSWORD

RabbitMq password.

### RABBITMQ_VIRTUAL_HOST

Virtual host to use when connecting to the broker.

### RABBITMQ_TEMPLATE_RETRY_ENABLED

Set to true to enable retries in the RabbitTemplate.

### RABBITMQ_TEMPLATE_RETRY_INITIAL_INTERVAL

Interval between the first and second attempt to publish a message.

### RABBITMQ_PUBLISHER_CONFIRM_TYPE

CorrelationData object to correlate confirmations with sent messages.

### RABBITMQ_PUBLISHER_RETURNS

Enable publisher returns.

### RABBITMQ_TEMPLATE_MANDATORY

Enable mandatory messages.

### SOLACE DETAILS
https://20.113.13.192:1943/#/

lidl-demo

jdn41!SDF5
