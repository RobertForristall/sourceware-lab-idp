# Script for building the full docker image using the new base image
# - Can be removed once the new base image is properly hosted
cd ../docker-base
docker build -t idp-server-base .
cd ../../../
mvn clean package