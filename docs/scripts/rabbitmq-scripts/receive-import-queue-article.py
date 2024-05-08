#!/usr/bin/env python
import sys
import os
import ssl
import pika
import logging

def main():

    host="20.79.211.250"
    port=5672
    virtual_host="aims-vhost"
    username="aims"
    password="aims-admin-jr89031hf83"
    queue="aims.import.queue.article"

    def on_message(channel, method_frame, header_frame, body):
        print(method_frame.delivery_tag)
        print(body)
        print()
        channel.basic_ack(delivery_tag=method_frame.delivery_tag)

    # Create a connection
    credentials = pika.credentials.PlainCredentials(username, password)
    parameters = pika.ConnectionParameters(
        host=host,
        port=port,
        virtual_host=virtual_host,
        credentials=credentials
    )
    connection = pika.BlockingConnection(parameters)


    # Create the channel
    channel = connection.channel()

    #channel.queue_declare(queue='aims.import.queue.mappingInfo', durable=True, arguments={"x-queue-type": "quorum"})

    channel.basic_consume(queue, on_message)

    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)