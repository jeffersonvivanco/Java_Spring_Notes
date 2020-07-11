FROM openjdk:14-alpine

# We added a VOLUME pointing to "/tmp" because that is where a spring boot application creates working directories for
# tomcat by default. The effect is to create a temporary file on your host under "/var/lib/docker" and link it to the
# container under "/tmp". This step can be necessary for apps that need to write in the filesystem.
VOLUME /tmp

WORKDIR /javaspringnotes

COPY build/libs/javaspringnotes-1.0-SNAPSHOT.jar app.jar

EXPOSE 8443

# setting active spring profile
ENV spring_profiles_active=prod
CMD ["java", "-jar", "app.jar"]