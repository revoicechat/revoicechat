#!/bin/bash

# Deployment script to update the project and restart services
# Steps: pull project, update submodules, rebuild and restart Docker Compose

set -e  # Exit on any error

echo "🔄 Pulling latest changes from repository..."
git pull

echo "🔄 Updating submodules to committed versions..."
# --init: initialize submodules if not already done
# --recursive: update nested submodules as well
git submodule update --init --recursive

echo "🛑 Stopping Docker Compose services..."
docker-compose down

echo "🔨 Rebuilding and starting Docker Compose services..."
# -d: detached mode (run in background)
# --build: rebuild images before starting
docker-compose up -d --build

echo "✅ Deployment completed successfully!"
echo "📊 Checking container status:"
docker-compose ps