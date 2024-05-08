#!/usr/bin/env python
import sys
import os
import ssl
import pika
import logging

def main():

    def on_message(channel, method_frame, header_frame, body):
        print(method_frame.delivery_tag)
        print(body)
        print()
        channel.basic_ack(delivery_tag=method_frame.delivery_tag)

    # SSL
    context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)

    # Create a connection
    credentials = pika.credentials.PlainCredentials('aims', 'aims-admin-jr89031hf83')
    parameters = pika.ConnectionParameters(
        host='20.79.211.250',
        port=5671,
        virtual_host='aims-vhost',
        credentials=credentials,
        ssl_options=pika.SSLOptions(context)
    )
    connection = pika.BlockingConnection(parameters)

    # Create the channel
    channel = connection.channel()

    #channel.queue_declare(queue='aims.import.queue.mappingInfo', durable=True, arguments={"x-queue-type": "quorum"})

    channel.basic_qos(prefetch_count=500)
    channel.basic_consume(
            queue='aims.import.queue.mappingInfo',
            on_message_callback=on_message,
            auto_ack=False,
            exclusive=False,
            consumer_tag="receive-tls-import-queue-article.py",
            arguments=None
        )

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