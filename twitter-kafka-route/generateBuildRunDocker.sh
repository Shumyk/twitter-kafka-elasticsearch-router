#!/bin/bash

mvn clean package -DskipTests -q
docker build -t shumyk/twitter-kafka-route .
docker run -p 8080:8080 shumyk/twitter-kafka-route