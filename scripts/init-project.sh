#!/bin/bash

# Initialization script for first-time project setup
# Steps: pull project, update submodules, generate JWT keys

set -e  # Exit on any error

echo "🚀 Starting project initialization..."

echo "🔄 Pulling latest changes from repository..."
git pull

echo "🔄 Updating submodules to committed versions..."
# --init: initialize submodules if not already done
# --recursive: update nested submodules as well
git submodule update --init --recursive

echo "🔑 Generating JWT keys..."

# Check if the generate script exists
if [ ! -f "./ReVoiceChat-CoreServer/scripts/generate_jwtKey.sh" ]; then
    echo "❌ Error: generate_jwtKey.sh script not found"
    exit 1
fi

# Make the script executable if it isn't already
chmod +x ./ReVoiceChat-CoreServer/scripts/generate_jwtKey.sh

# Execute the JWT key generation script
./ReVoiceChat-CoreServer/scripts/generate_jwtKey.sh

# Verify that keys were generated
if [ -f "./ReVoiceChat-CoreServer/jwt/privateKey.pem" ] && [ -f "./ReVoiceChat-CoreServer/jwt/publicKey.pem" ]; then
    echo "✅ JWT keys successfully generated"
else
    echo "❌ Error: JWT keys were not created"
    exit 1
fi

echo "📋 Copying configuration files..."

# Copy CoreServer configuration template to root
if [ -f "./ReVoiceChat-CoreServer/server.exemple.properties" ]; then
    cp "./ReVoiceChat-CoreServer/server.docker.exemple.properties" "./server.core.properties"
    echo "✅ CoreServer config copied to ./server.core.properties"
else
    echo "⚠️  Warning: server.exemple.properties not found"
fi

# Copy MediaServer configuration template to root
if [ -f "./ReVoiceChat-MediaServer/www/settings.ini.exemple" ]; then
    cp "./ReVoiceChat-MediaServer/www/settings.ini.docker.exemple" "./settings.media.ini"
    echo "✅ MediaServer config copied to ./settings.media.ini"
else
    echo "⚠️  Warning: settings.ini.exemple not found"
fi

# Copy .env configuration template to root
if [ -f "./.env.exemple" ]; then
    cp "./.env.exemple" "./.env"
    echo "✅ .env.exemple copied to ./.env"
else
    echo "⚠️  Warning: .env.exemple not found"
fi

echo "✅ Project initialization completed successfully!"
echo "📝 Next steps:"
echo "   - Edit './server.core.properties' with your configuration"
echo "   - Edit './settings.media.ini' with your configuration"
echo "   - Run './deploy-update.sh' to start the services"