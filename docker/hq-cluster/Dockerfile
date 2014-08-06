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

# Install java, unzip
RUN yum -y install java-1.7.0-openjdk-devel unzip; yum clean all

# Add scripts and resources
ADD ./install-eap.sh /install-eap.sh
ADD ./configure-eap.sh /configure-eap.sh
ADD ./create-queues.sh /create-queues.sh
ADD ./env.sh /env.sh
ADD ./copy-env.sh /copy-env.sh
ADD ./start-container.sh /start-container.sh
ADD ./supervisord.conf /etc/supervisord.conf
ADD ./start-eap.sh /start-eap.sh
ADD ./stop-eap.sh /stop-eap.sh
ADD ./configure-hornetq.cli /configure-hornetq.cli
ADD ./create-queues.cli /create-queues.cli
RUN mkdir /resources
ADD ./resources /resources

# Set script permissions
RUN chmod 755 /start-container.sh /install-eap.sh /env.sh /copy-env.sh /start-eap.sh /stop-eap.sh /configure-eap.sh /create-queues.sh /configure-hornetq.cli /create-queues.cli

# Install EAP
RUN /install-eap.sh

# Configure EAP
RUN /configure-eap.sh

# Create Queues
RUN /create-queues.sh

# Change owner of /opt/jboss to user jboss
RUN chown -R jboss:jboss /opt/jboss

# Cleanup
RUN rm -rf /resources

# Expose port 8080,9990,9999
EXPOSE 8080 9990 9999

# Entrypoint
ENTRYPOINT ["/bin/bash", "/start-container.sh"]
