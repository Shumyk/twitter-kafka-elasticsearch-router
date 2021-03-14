FROM adoptopenjdk/openjdk14

COPY target/*.jar /usr/local/lib/kafka-elastic-search-route.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/kafka-elastic-search-route.jar"]