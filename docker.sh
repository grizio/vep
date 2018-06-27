#!/usr/bin/env bash
<<<<<<< Updated upstream

CONTAINER=$(docker ps --all | grep vep | cut -d ' ' -f 1)
RUNNING_CONTAINER=$(docker ps | grep vep | cut -d ' ' -f 1)
=======
CONTAINER=$(docker ps --all | grep vep-postgres | cut -d ' ' -f 1)
RUNNING_CONTAINER=$(docker ps | grep vep-postgres | cut -d ' ' -f 1)
>>>>>>> Stashed changes

if [[ "$1" = "start" ]]
then
  if [[ -z "$CONTAINER" ]]
  then
<<<<<<< Updated upstream
    docker run -p 5432:5432/tcp --name vep -e POSTGRES_PASSWORD=vep -e POSTGRES_USER=vep -e POSTGRES_DB=vep -d postgres:9.5
=======
    docker run -p 5432:5432 --name vep -e POSTGRES_PASSWORD=vep -e POSTGRES_USER=vep -e POSTGRES_DB=vep -d postgres:9.5
>>>>>>> Stashed changes
  elif [[ -z "$RUNNING_CONTAINER" ]]
  then
    docker start "$CONTAINER"
  else
    echo "Container already running"
  fi
elif [[ "$1" = "stop" ]]
then
  if [[ -z "$CONTAINER" ]]
  then
    echo "Container does not exist"
  elif [[ -z "$RUNNING_CONTAINER" ]]
  then
    echo "Container not started yet"
  else
    docker stop "$CONTAINER"
  fi
elif [[ "$1" = "clean" ]]
then
  if [[ -z "$CONTAINER" ]]
  then
    echo "Container does not exist"
  elif [[ -z "$RUNNING_CONTAINER" ]]
  then
    docker rm "$CONTAINER"
  else
    docker stop "$CONTAINER"
    docker rm "$CONTAINER"
  fi
<<<<<<< Updated upstream
=======
elif [[ "$1" = "logs" ]]
then
  if [[ -z "$RUNNING_CONTAINER" ]]
  then
    echo "Container not started"
  else
    docker logs "$CONTAINER" --follow
  fi
>>>>>>> Stashed changes
else
  echo "./docker start|stop|clean"
fi

exit 0