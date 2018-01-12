#!/bin/sh

JBOSS_CONFIG_FILE=/opt/jboss/wildfly/standalone/configuration/standalone.xml
RABBIT_HOST=${RABBIT_HOST:-rabbitmq}
RABBIT_PORT=${RABBIT_PORT:-5672}
MONGO_HOST=${MONGO_HOST:-mongodb}
MONGO_PORT=${MONGO_PORT:-27017}

sys_prop="\n    <system-properties>\n        <property name=\"rabbit.host\" value=\"${RABBIT_HOST}\"/>\n        <property name=\"rabbit.port\" value=\"${RABBIT_PORT}\"/>\n        <property name=\"mongo.host\" value=\"${MONGO_HOST}:${MONGO_PORT}\"/>\n        <property name=\"base.dir\" value=\"/opt/jboss/dojot\"/>\n        <property name=\"base.alarm.path\" value=\"/opt/jboss/dojot/alarms\"/>\n    </system-properties>"

sed -i "s|\(</extensions>\)|\1${sys_prop}|" ${JBOSS_CONFIG_FILE}

/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0
