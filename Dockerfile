FROM openjdk:8-jre-alpine

LABEL maintainer="taras.chornyi@gmail.com"

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    APP_SLEEP=3 \
    JAVA_OPTS=""

# Add an 'application' user to run our application so that it doesn't need to run as root
RUN adduser -D -s /bin/sh application
WORKDIR /home/application

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown application:application entrypoint.sh
USER application

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 9000

ADD build/libs/*.war app.war
