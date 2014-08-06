#!/bin/bash

. env.sh

if [ ! -d $SERVER_INSTALL_DIR/$SERVER_NAME ]
then
  echo "EAP not installed."
  exit 0
fi

# replace placeholders in cli file
VARS=( HQ_SHARED_JOURNAL_DIR )
for i in "${VARS[@]}"
do
    sed -i "s'@@${i}@@'${!i}'" $CLI_HORNETQ	
done

# start eap in admin-only mode
${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/standalone.sh --server-config=$JBOSS_CONFIG --admin-only &
sleep 12
${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/jboss-cli.sh --connect --controller=${IP_ADDR} --file=${CLI_HORNETQ}
${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/jboss-cli.sh --connect --controller=${IP_ADDR} ":shutdown"



