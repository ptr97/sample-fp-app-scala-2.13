#!/bin/bash

cd "$(dirname "$0")"
ARG=$1

case $ARG in
  app)
    echo 'Setting up database...'
    docker-compose --file ./docker/docker-compose.yml up -d
    echo 'Database has been set up'
    echo 'Starting the http application...'
    sbt run
    ;;

  tests)
    echo 'Setting up database...'
    docker-compose --file ./docker/docker-compose.yml up -d
    echo 'Database has been set up'
    echo 'Running tests...'
    sbt test
    ;;

  clean)
    echo 'Cleaning all docker dependencies...'
    docker-compose --file ./docker/docker-compose.yml down --rmi all --volumes
    echo 'Cleaning application artefacts'
    sbt clean
    ;;

  *)
    echo "Run the script in of the possible modes ./`basename $0` app|tests|clean"
esac
