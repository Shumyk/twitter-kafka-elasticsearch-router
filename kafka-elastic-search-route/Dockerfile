FROM adoptopenjdk/openjdk14

COPY target/*.jar /usr/local/lib/kafka-elastic-search-route.jar
EXPOSE 8080
CMD exec java -jar /usr/local/lib/kafka-elastic-search-route.jar --spring.config.name=kafka-elastic-route-config