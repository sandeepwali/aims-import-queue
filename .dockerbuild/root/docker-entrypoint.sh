#!/usr/bin/env bash

#
# Banner
#

echo '
--------------------------------------------------------------------
           _____ __  __  _____    _____           _
     /\   |_   _|  \/  |/ ____|  / ____|         | |
    /  \    | | | \  / | (___   | (___  _   _ ___| |_ ___ _ __ ___
   / /\ \   | | | |\/| |\___ \   \___ \| | | / __| __/ _ \  _ ` _ \
  / ____ \ _| |_| |  | |____) |  ____) | |_| \__ \ ||  __/ | | | | |
 /_/    \_\_____|_|  |_|_____/  |_____/ \__, |___/\__\___|_| |_| |_|
                                         __/ |
                                        |___/
Brought to you by solumesl.com

'

echo "
--------------------------------------------------------------------------
Unprivileged user
--------------------------------------------------------------------------
User name:   aims
User uid:    $(id -u aims)
User gid:    $(id -g aims)

"

#
# Functions
#

replace_with_old_variable_value ()
{
    #
    # Description:
    #   Replace old variable value with new empty variable value
    #
    # Usage:
    #   replace_with_old_variable_value old_variable new_variable
    #
    # Example:
    #   DATABASE_URL="jdbc:postgresql://127.0.0.1:6010/AIMS_PORTAL_DB"
    #   AIMS_CLIENT_DATABASE_URL=
    #   replace_with_old_variable_value DATABASE_URL AIMS_CLIENT_DATABASE_URL
    #

    old_variable=$1
    new_variable=$2

    # Expand old_variable value into old_variable_value
    eval "old_variable_value=\$$old_variable"

    if [ -n "$old_variable_value" ]
    then
        # Old variable value is not empty, show depreciated message
        echo "Warning: \$${old_variable} is depreciated use \$${new_variable}"

        # Check if new variable is empty
        eval "new_variable_value=\$$new_variable"
        if [ -z "$new_variable_value" ]
        then
            echo "New variable \$${new_variable} is empty..."
            echo "Replacing \$${old_variable} with \$${new_variable}"
            eval "$new_variable=$old_variable_value"
        fi
    fi

}

replace_empty_variable_with_default_value ()
{
    #
    # Description:
    #   Replace empty variable with default value
    #
    # Usage:
    #   replace_empty_variable_with_default_value variable_name default_value
    #
    # Example:
    #   AIMS_CLIENT_DATABASE_URL=""
    #   replace_empty_variable_with_default_value AIMS_CLIENT_DATABASE_URL jdbc:postgresql://127.0.0.1:6010/AIMS_PORTAL_DB
    #   echo $AIMS_CLIENT_DATABASE_URL

    variable_name=$1
    default_value=$2

    # Expand variable value into variable_value
    eval "variable_value=\$$variable_name"

    if [ -z "$variable_value" ]
    then
        echo "Variable \$$variable_name is empty using default value $default_value"
        export ${variable_name}=${default_value}
    fi
}



replace_property ()
{
    property_name=$1
    property_value=$2

    sed -i -e "s|^${property_name}=.*|${property_name}=${property_value}|" "/app/application.properties"
}

#
# Variables
#

## Use old variable value if new variable is not provided
## Usage: replace_with_old_variable_value <old_variable> <new_variable>
#replace_with_old_variable_value SERVER_PORT AIMS_CLIENT_SERVER_PORT


## Set empty variables with default value
## Usage: replace_empty_variable_with_default_value <variable_name> <variable_value>
replace_empty_variable_with_default_value JAVA_XMS "1g"
replace_empty_variable_with_default_value JAVA_XMX "1g"

replace_empty_variable_with_default_value SERVER_PORT "8080"

replace_empty_variable_with_default_value LOGGING_LEVEL_COM_SOLUM "INFO"

replace_empty_variable_with_default_value SOLACE_HOST "tcps://<SOLACE_HOST>:55443"
replace_empty_variable_with_default_value SOLACE_VPN "<SOLACE_VPN>"
replace_empty_variable_with_default_value SOLACE_USERNAME "<SOLACE_USERNAME>"
replace_empty_variable_with_default_value SOLACE_PASSWORD "<SOLACE_PASSWORD>"
replace_empty_variable_with_default_value SOLACE_QUEUE "<SOLACE_QUEUE>"
replace_empty_variable_with_default_value SOLACE_ERROR_TOPIC "<SOLACE_ERROR_TOPIC>"
replace_empty_variable_with_default_value SOLACE_DELETE_QUEUE "<SOLACE_DELETE_QUEUE>"

replace_empty_variable_with_default_value SOLACE_SSL_VALIDATE_CERTIFICATE "true"
replace_empty_variable_with_default_value SOLACE_SSL_TRUST_STORE_PASSWORD "aimssuite"
replace_empty_variable_with_default_value SOLACE_SSL_TRUST_STORE "/app/solace-certificates.jks"

replace_empty_variable_with_default_value SOLACE_ERROR_SOURCE_HOST "<SOLACE_ERROR_SOURCE_HOST>"
replace_empty_variable_with_default_value SOLACE_MESSAGE_COUNT_ENABLE "true"
replace_empty_variable_with_default_value SOLACE_MESSAGE_COUNT_URL "http://<SOLACE_MESSAGE_COUNT_URL>:8080/SEMP/v2/monitor/msgVpns/lidl-staging/queues/<SOLACE_QUEUE>?select=msgs.count"

replace_empty_variable_with_default_value AIMS_URL "http://aims-portal:8000"
replace_empty_variable_with_default_value AIMS_API_KEY "NsKQvyOF.zguBCKAfwFCLBgIizGGaClomAJebJVZaRxy"

replace_empty_variable_with_default_value AIMS_PROPERTIES_MAX_STORAGE_SIZE "4000"
replace_empty_variable_with_default_value AIMS_PROPERTIES_SLEEP_TIME_ONCE_STORAGE_REACH_MAX_SIZE "100"

replace_empty_variable_with_default_value AIMS_PROPERTIES_BATCH_SIZE "1000"
replace_empty_variable_with_default_value AIMS_PROPERTIES_SLEEP_TIME_FOR_BATCH_PROCESS_IF_STORAGE_IS_EMPTY "100"


replace_property "server.port" "${SERVER_PORT}"

replace_property "logging.level.com.solum" "${LOGGING_LEVEL_COM_SOLUM}"

replace_property "spring.cloud.stream.binders.solace.environment.solace.java.host" "${SOLACE_HOST}"
replace_property "spring.cloud.stream.binders.solace.environment.solace.java.msgVpn" "${SOLACE_VPN}"
replace_property "spring.cloud.stream.binders.solace.environment.solace.java.clientUsername" "${SOLACE_USERNAME}"
replace_property "spring.cloud.stream.binders.solace.environment.solace.java.clientPassword" "${SOLACE_PASSWORD}"
replace_property "spring.cloud.stream.bindings.articleConsumeSolace-in-0.destination" "${SOLACE_QUEUE}"
replace_property "spring.cloud.stream.bindings.articleError-out-0.destination" "${SOLACE_ERROR_TOPIC}"
replace_property "spring.cloud.stream.bindings.articleDeleteConsumeSolace-in-0.destination" "${SOLACE_DELETE_QUEUE}"

replace_property "spring.cloud.stream.binders.solace.environment.solace.java.apiProperties.ssl_validate_certificate" "${SOLACE_SSL_VALIDATE_CERTIFICATE}"
replace_property "spring.cloud.stream.binders.solace.environment.solace.java.apiProperties.ssl_trust_store_password" "${SOLACE_SSL_TRUST_STORE_PASSWORD}"
replace_property "spring.cloud.stream.binders.solace.environment.solace.java.apiProperties.ssl_trust_store" "${SOLACE_SSL_TRUST_STORE}"

replace_property "aims-properties.sourceHost" "${SOLACE_ERROR_SOURCE_HOST}"

#replace_property "aims-properties.msgCountCheckApi" "${SOLACE_MESSAGE_COUNT_ENABLE}"
#replace_property "solace.message.count.url" "${SOLACE_MESSAGE_COUNT_URL}"




replace_property "aims.url.node" "${AIMS_URL}"
replace_property "aims.apikey" "${AIMS_API_KEY}"


replace_property "aims-properties.max-storage-size" "${AIMS_PROPERTIES_MAX_STORAGE_SIZE}" # Maximum messages from solace
replace_property "aims-properties.sleep-time-once-storage-reach-max-size" "${AIMS_PROPERTIES_SLEEP_TIME_ONCE_STORAGE_REACH_MAX_SIZE}" # Maximum messages from solace

replace_property "aims-properties.batch-size" "${AIMS_PROPERTIES_BATCH_SIZE}" # Batch size to aims-portal
replace_property "aims-properties.sleep-time-for-batch-process-if-storage-is-empty" "${AIMS_PROPERTIES_SLEEP_TIME_FOR_BATCH_PROCESS_IF_STORAGE_IS_EMPTY}" # Fixed delay after storage is empty 


echo "
--------------------------------------------------------------------------
AIMS Client
--------------------------------------------------------------------------
$(env)
"

#
# Exec Java Application
#

exec /opt/java/openjdk/bin/java ${JAVA_OPTS} \
    -Xms${JAVA_XMS} \
    -Xmx${JAVA_XMX} \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /app/aims-import-queue.jar;
