#!/bin/bash

. /env.sh

#Sanity Checks

if [ -f $EAP ];
then
  echo "File $EAP found"
else
  echo "File $EAP does not exists. Please put it in the resources folder"
  exit 250
fi

if [ -d $SERVER_INSTALL_DIR/jboss-eap-6.1 ] || [ -d $SERVER_INSTALL_DIR/$SERVER_NAME ]
then
  echo "Target directory already exists. Please remove it before installing EAP again."
  exit 250
fi

#Install eap
echo "Unzipping EAP 6"
unzip -q $EAP -d $SERVER_INSTALL_DIR

echo "Renaming the EAP dir to $SERVER_NAME"
mv $SERVER_INSTALL_DIR/jboss-eap-6.1 $SERVER_INSTALL_DIR/$SERVER_NAME

echo "Adjusting JVM Heap"
sed -i 's/-Xms1303m -Xmx1303m/-Xms768m -Xmx768m/' $SERVER_INSTALL_DIR/$SERVER_NAME/bin/standalone.conf

echo "Create management user admin:admin"
RET=`cat $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/mgmt-users.properties | grep "admin=" | grep -v "#"`
if [[ "$RET" == "" ]]
then
  printf '\n' >> $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/mgmt-users.properties
  echo "admin=c22052286cd5d72239a90fe193737253" >> $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/mgmt-users.properties
fi

echo "Create application user user:user"
RET=`cat $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/application-users.properties | grep "user=" | grep -v "#"`
if [[ "$RET" == "" ]]
then
  printf '\n' >> $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/application-users.properties
  echo "user=c5568adea472163dfc00c19c6348a665" >> $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/application-users.properties
  printf '\n' >> $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/application-roles.properties
  echo "user=guest" >> $SERVER_INSTALL_DIR/$SERVER_NAME/standalone/configuration/application-roles.properties
fi

echo "Change owner to user jboss"
chown jboss:jboss $SERVER_INSTALL_DIR
chown -R jboss:jboss $SERVER_INSTALL_DIR/$SERVER_NAME


