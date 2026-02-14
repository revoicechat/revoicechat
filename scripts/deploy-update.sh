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

echo "📋 Copying configuration files to submodules..."

# Copy CoreServer config from root to submodule (overwrite if exists)
if [ -f "./server.core.properties" ]; then
    cp -f "./server.core.properties" "./ReVoiceChat-CoreServer/server.docker.properties"
    echo "✅ CoreServer config copied to ./ReVoiceChat-CoreServer/server.docker.properties"
else
    echo "⚠️  Warning: ./server.core.properties not found"
fi

# Copy MediaServer config from root to submodule (overwrite if exists)
if [ -f "./settings.media.ini" ]; then
    cp -f "./settings.media.ini" "./ReVoiceChat-MediaServer/settings.ini"
    echo "✅ MediaServer config copied to ./ReVoiceChat-MediaServer/settings.ini"
else
    echo "⚠️  Warning: ./settings.media.ini not found"
fi

echo "🛑 Stopping Docker Compose services..."
docker-compose down

echo "🔨 Rebuilding and starting Docker Compose services..."
# -d: detached mode (run in background)
# --build: rebuild images before starting
docker-compose up -d --build

echo "✅ Deployment completed successfully!"
echo "📊 Checking container status:"
docker-compose ps