#!/usr/bin/env bash

# exit when any command fails
set -e

# keep track of the last executed command
trap 'last_command=$current_command; current_command=$BASH_COMMAND' DEBUG
# echo an error message before exiting
trap 'echo "\"${last_command}\" command filed with exit code $?."' EXIT

TIMEOUT=120

# -- wait_for_container_log --
# $1: docker container name
# S2: spring value to wait to appear in container logs
function wait_for_container_log() {
  local log_waiting="Waiting for string '$2' in the $1 logs ..."
  echo "${log_waiting} It will timeout in ${TIMEOUT}s"
  SECONDS=0

  while true ; do
    local log=$(docker logs $1 2>&1 | grep "$2")
    if [ -n "$log" ] ; then
      echo $log
      break
    fi

    if [ $SECONDS -ge $TIMEOUT ] ; then
      echo "${log_waiting} TIMEOUT"
      break;
    fi
    sleep 1
  done
}
