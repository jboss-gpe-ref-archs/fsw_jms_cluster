#!/bin/bash

. env.sh

if [ ! -d $SERVER_INSTALL_DIR/$SERVER_NAME ]
then
  echo "FSW not installed."
  exit 0
fi

# Start eap in admin-only mode and execute configuration script
su jboss -c "${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/standalone.sh --server-config=$JBOSS_CONFIG --admin-only &"
sleep 12
su jboss -c "${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/jboss-cli.sh --connect --controller=${IP_ADDR} --file=$CLI_FSW"
su jboss -c "${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/jboss-cli.sh --connect --controller=${IP_ADDR} --file=$CLI_FSW_REFARCH_APP"
su jboss -c "${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/jboss-cli.sh --connect --controller=${IP_ADDR} \":shutdown\" "



