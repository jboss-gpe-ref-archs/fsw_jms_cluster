#!/bin/bash

. env.sh

if [ ! -d $SERVER_INSTALL_DIR/$SERVER_NAME ]
then
  echo "FSW not installed."
  exit 255
fi

if [ ! -f $REF_ARCH_APP ]
then
  echo "Archive $REF_ARCH_APP not found. Please put it in the resources folder."
  exit 255
fi

su jboss -c "cp $REF_ARCH_APP ${SERVER_INSTALL_DIR}/${SERVER_NAME}/standalone/deployments"
