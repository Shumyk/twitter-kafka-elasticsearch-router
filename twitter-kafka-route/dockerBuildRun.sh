#!/bin/bash

mvn clean package -DskipTests -q
docker build -t shumyk/twitter-kafka-route .
docker rm twitter-kafka-route
docker run --name twitter-kafka-route -p 8080:8080 shumyk/twitter-kafka-route