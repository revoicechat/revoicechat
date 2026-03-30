#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define the jwt directory path relative to the script location
JWT_DIR="$SCRIPT_DIR/../jwt"

echo "🔑 Generating JWT keys..."

# Create jwt directory if it doesn't exist
mkdir -p "$JWT_DIR"

# Generate private key
openssl genpkey -algorithm RSA -out "$JWT_DIR/privateKey.pem" -pkeyopt rsa_keygen_bits:2048

# Generate public key from private key
openssl rsa -pubout -in "$JWT_DIR/privateKey.pem" -out "$JWT_DIR/publicKey.pem"

echo "✅ JWT keys generated successfully in $JWT_DIR"