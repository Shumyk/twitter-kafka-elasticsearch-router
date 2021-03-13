### Two stages to first mvn build jar and then copy to container and run it with java

## build stage
#FROM maven:3.6.3-adoptopenjdk-14 AS build
#
#COPY src /home/app/src
#COPY pom.xml /home/app
#RUN mvn -f /home/app/pom.xml clean package -DskipTests -q
#
#
## Package stage
#FROM adoptopenjdk/openjdk14
#
#COPY --from=build /home/app/target/*.jar /usr/local/lib/twitter-kafka-route.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/usr/local/lib/twitter-kafka-route.jar"]
#--------------------------

# Copies prebuild jar to container and runs it, used to speedify build, cause in container mvn fetches all dependencies everytime
FROM adoptopenjdk/openjdk14

COPY target/*.jar /usr/local/lib/twitter-kafka-route.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/twitter-kafka-route.jar"]