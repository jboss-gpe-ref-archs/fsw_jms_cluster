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

# Create HornetQ journal directory
RUN mkdir -p /opt/jboss/hq-journal

# Change permissions to user jboss
RUN chown -R jboss:jboss /opt/jboss

# Mount as volume
VOLUME /opt/jboss/hq-journal

CMD true
