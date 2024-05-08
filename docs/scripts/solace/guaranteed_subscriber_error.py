# Consumer that binds to exclusive durable queue
# Assumes existence of queue on broker holding messages.
# Note: create queue with topic subscription
# See https://docs.solace.com/Solace-PubSub-Messaging-APIs/API-Developer-Guide/Adding-Topic-Subscriptio.htm for more details

import os
import platform
import time
import json

from solace.messaging.messaging_service import MessagingService, ReconnectionListener, ReconnectionAttemptListener, ServiceInterruptionListener, ServiceEvent
from solace.messaging.resources.queue import Queue
from solace.messaging.config.retry_strategy import RetryStrategy
from solace.messaging.receiver.persistent_message_receiver import PersistentMessageReceiver
from solace.messaging.receiver.message_receiver import MessageHandler, InboundMessage
from solace.messaging.errors.pubsubplus_client_error import PubSubPlusClientError

if platform.uname().system == 'Windows': os.environ["PYTHONUNBUFFERED"] = "1" # Disable stdout buffer

# Queue name
queue_name = os.environ["SOLACE_QUEUE"] = "aims.import.queue.article.v5.1.error"

# Broker Config. Note: Could pass other properties Look into
broker_props = {
    "solace.messaging.transport.host": os.environ.get('SOLACE_HOST') or "20.79.213.119",
    "solace.messaging.service.vpn-name": os.environ.get('SOLACE_VPN') or "lidl-staging",
    "solace.messaging.authentication.scheme.basic.username": os.environ.get('SOLACE_USERNAME') or "lidl-staging",
    "solace.messaging.authentication.scheme.basic.password": os.environ.get('SOLACE_PASSWORD') or "jdn41!SDF5"
    }



# Handle received messages
class MessageHandlerImpl(MessageHandler):
    def on_message(self, message: InboundMessage):
        topic = message.get_destination_name()
        application_message_id = message.get_application_message_id()
        sequence_number = message.get_sequence_number()
        message_id = message.message_id.value
        payload = message.get_payload_as_bytes()
        #payload = json.dumps(message)
        print("\n" + f"Received message on: {topic} appMessageId: {application_message_id} message_id: {message_id}")
        #print("\n" + f"Message dump: {message} \n")
        print("\n" + f"Message dump: {payload} \n")

# Inner classes for error handling
class ServiceEventHandler(ReconnectionListener, ReconnectionAttemptListener, ServiceInterruptionListener):
    def on_reconnected(self, e: ServiceEvent):
        print("\non_reconnected")
        print(f"Error cause: {e.get_cause()}")
        print(f"Message: {e.get_message()}")

    def on_reconnecting(self, e: "ServiceEvent"):
        print("\non_reconnecting")
        print(f"Error cause: {e.get_cause()}")
        print(f"Message: {e.get_message()}")

    def on_service_interrupted(self, e: "ServiceEvent"):
        print("\non_service_interrupted")
        print(f"Error cause: {e.get_cause()}")
        print(f"Message: {e.get_message()}")


# Build A messaging service with a reconnection strategy of 20 retries over an interval of 3 seconds
# Note: The reconnections strategy could also be configured using the broker properties object
messaging_service = MessagingService.builder().from_properties(broker_props)\
                    .with_reconnection_retry_strategy(RetryStrategy.parametrized_retry(20,3000))\
                    .build()

# Blocking connect thread
messaging_service.connect()
print(f'Messaging Service connected? {messaging_service.is_connected}')

# Event Handling for the messaging service
service_handler = ServiceEventHandler()
messaging_service.add_reconnection_listener(service_handler)
messaging_service.add_reconnection_attempt_listener(service_handler)
messaging_service.add_service_interruption_listener(service_handler)

# Queue name.
# NOTE: This assumes that a persistent queue already exists on the broker with the right topic subscription
durable_exclusive_queue = Queue.durable_exclusive_queue(queue_name)

try:
  # Build a receiver and bind it to the durable exclusive queue
  persistent_receiver: PersistentMessageReceiver = messaging_service.create_persistent_message_receiver_builder()\
            .with_message_auto_acknowledgement()\
            .build(durable_exclusive_queue)
  persistent_receiver.start()

  # Callback for received messages
  persistent_receiver.receive_async(MessageHandlerImpl())
  print(f'PERSISTENT receiver started... Bound to Queue [{durable_exclusive_queue.get_name()}]')
  try:
      while True:
          time.sleep(1)
  except KeyboardInterrupt:
      print('\nKeyboardInterrupt received')
# Handle API exception
except PubSubPlusClientError as exception:
  print(f'\nMake sure queue {queue_name} exists on broker!')

finally:
    if persistent_receiver and persistent_receiver.is_running():
      print('\nTerminating receiver')
      persistent_receiver.terminate(grace_period = 0)
    print('\nDisconnecting Messaging Service')
    messaging_service.disconnect()

