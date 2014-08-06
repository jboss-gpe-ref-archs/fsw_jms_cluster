#!/bin/bash

. /environment
. /env.sh

IPADDR=$(ip a s | sed -ne '/127.0.0.1/!{s/^[ \t]*inet[ \t]*\([0-9.]\+\)\/.*$/\1/p}')

echo "IPADDR = $IPADDR"

echo "HornetQ node = $HORNETQ_NODE"
echo "HornetQ backup node = $HORNETQ_BACKUP_NODE"

# Sanity checks
if [ ! -d $SERVER_INSTALL_DIR/$SERVER_NAME ]
then
  echo "EAP not installed."
  exit 0
fi

CLEAN=false

for var in $@
do
    case $var in
        --clean)
            CLEAN=true
            ;;
        --admin-only)
            ADMIN_ONLY=--admin-only 
    esac
done

# Clean data, log and temp directories
if [ "$CLEAN" = "true" ] 
then
    rm -rf $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/data $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/log $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/tmp
fi

# start fsw
su jboss <<EOF
nohup ${SERVER_INSTALL_DIR}/${SERVER_NAME}/bin/standalone.sh -Djboss.bind.address=$IPADDR -Djboss.bind.address.management=$IPADDR -Djboss.bind.address.insecure=$IPADDR -Djboss.node.name=server-$IPADDR -Dhornetq.node=$HORNETQ_NODE -Dhornetq.backup.node=$HORNETQ_BACKUP_NODE  --server-config=$JBOSS_CONFIG $ADMIN_ONLY 0<&- &>/dev/null &
EOF
echo "EAP started"
