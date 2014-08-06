#!/bin/bash

. /env.sh

if [ ! -d $SERVER_INSTALL_DIR/$SERVER_NAME ]
then
  echo "EAP not installed."
  exit 0
fi

# start eap in admin-only mode
${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/standalone.sh --server-config=$JBOSS_CONFIG --admin-only &
sleep 12
${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/jboss-cli.sh --connect --controller=${IP_ADDR} --file=${CLI_HORNETQ_QUEUES}



