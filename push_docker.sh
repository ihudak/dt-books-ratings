#!/bin/bash
./gradlew clean build
docker image build --platform linux/amd64 -t ihudak/dt-ratings-service:latest .
docker push ihudak/dt-ratings-service:latest

docker image build --platform linux/arm64 -t ihudak/dt-ratings-service:arm64 .
docker push ihudak/dt-ratings-service:arm64
