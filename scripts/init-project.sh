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

echo "✅ Project initialization completed successfully!"
echo "📝 Next steps:"
echo "   - Configure your environment variables"
echo "   - Run './deploy-update.sh' to start the services"