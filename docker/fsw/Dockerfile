FROM mattdm/fedora:f20

# Perform updates
RUN yum -y update; yum clean all

# Install supervisor
RUN yum -y install supervisor; yum clean all

# Create directories for supervisor
RUN mkdir -p /var/run/supervisord

# Create user jboss
RUN groupadd -r jboss -g 400 && useradd -u 400 -r -g jboss -d /opt/jboss -s /bin/bash -c "JBoss user" jboss
RUN mkdir -p /opt/jboss

# Install java, unzip, mysql driver
RUN yum -y install java-1.7.0-openjdk-devel unzip mysql-connector-java; yum clean all

# Scripts
ADD ./install-fsw.sh /install-fsw.sh
ADD ./env.sh /env.sh
RUN mkdir /resources /manifests /temp-install
ADD ./resources /resources
ADD ./manifests /manifests
ADD ./start-container.sh /start-container.sh
ADD ./supervisord.conf /etc/supervisord.conf
ADD ./start-fsw.sh /start-fsw.sh
ADD ./stop-fsw.sh /stop-fsw.sh
ADD ./copy-env.sh /copy-env.sh
ADD ./configure-fsw.sh /configure-fsw.sh
ADD ./configure-fsw.cli /configure-fsw.cli
ADD ./configure-fsw-refarch-app.cli /configure-fsw-refarch-app.cli
ADD ./deploy-refarch-app.sh /deploy-refarch-app.sh

# Modify script permissions
RUN chmod 755 /start-container.sh /install-fsw.sh /env.sh /start-fsw.sh /stop-fsw.sh /copy-env.sh /configure-fsw.sh /deploy-refarch-app.sh

# Install FSW script
RUN /install-fsw.sh

# Configure FSW
RUN /configure-fsw.sh

# Deploy refarch application
RUN /deploy-refarch-app.sh

# Entrypoint
ENTRYPOINT ["/bin/bash", "/start-container.sh"]
