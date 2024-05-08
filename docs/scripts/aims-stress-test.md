# AIMS Stress Test

aims-stress-test is a tool that has been developed to test various components in aims.

## Contributors

* Dominik Hannen <dominik.hannen@solumesl.com>

## Scope

* Test rest-api for aims-portal
* Test aims-import-queue with RabbitMQ messages
* Test aims-client (depreciated, replaced by aims-import-queue)

## How to use

### Step 1 - Export variables

```
export RABBITMQ_HOST=20.79.211.250
export RABBITMQ_PORT=5671
export RABBITMQ_VHOST=aims-vhost
export RABBITMQ_USER=aims
export RABBITMQ_PASS=aims-admin-jr89031hf83
```

### Step 2 - Initialize Python Script

```
. /venv/bin/activate
python
import lidl
```

### Step 3 - Import 100.000 articles

```
lidl.post_random_articles(articles=100000, threads=8, endpoint="rmq-map", batchsize=1000)
```

Output

```
Sending 100000 Articles in 100 Batches (1000 Articles each)
=============================================
Articles: 100000        (Threads: 8, Via: rmq-map)
Success: 100    Fails: 0        (Batches)
Runtime: 15.39s (0.15ms per Article)
=============================================
```

### Step 4 - Check imported articles in RabbitMQ

* Url: https://aims-lidl-staging.k8s.de.solumesl.com/rabbitmq-management/
* Username: aims
* Password: aims-admin-jr89031hf83


