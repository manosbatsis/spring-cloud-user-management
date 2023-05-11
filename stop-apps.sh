#!/usr/bin/env bash


echo
echo "Stopping services..."
docker stop user-service event-service email-service

echo
echo "Stopping compose network..."
docker-compose down

echo
echo "Done."
