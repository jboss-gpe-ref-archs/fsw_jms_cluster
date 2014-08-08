FROM mattdm/fedora:f20

# Perform updates
RUN yum -y update; yum clean all

# Install supervisor
RUN yum -y install supervisor; yum clean all

# Create directories for supervisor
RUN mkdir -p /var/run/supervisord

# Install mysql
RUN yum -y install mariadb-server; yum clean all

# Init mysql
RUN mysql_install_db
RUN chown -R mysql:mysql /var/lib/mysql

# Add admin user
RUN /usr/bin/mysqld_safe & \
    sleep 10s &&\
    echo "GRANT ALL ON *.* TO admin@'%' IDENTIFIED BY 'redhat' WITH GRANT OPTION; FLUSH PRIVILEGES" | mysql

# Configure supervisor
ADD supervisord.conf /etc/supervisord.conf
ADD start-container.sh /start-container.sh

# MySQL - create jboss user and fsw schema
ADD ./sql /sql
ADD /create-schema.sh /create-schema.sh
RUN chmod 755 /create-schema.sh

RUN /create-schema.sh

# MySQL - application schema
ADD /create-app-schema.sh /create-app-schema.sh
RUN chmod 755 /create-app-schema.sh

RUN /create-app-schema.sh

#Expose port 3306
EXPOSE 3306

#Entrypoint
ENTRYPOINT ["/bin/bash", "/start-container.sh"]
