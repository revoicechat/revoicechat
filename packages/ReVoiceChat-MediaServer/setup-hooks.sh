#!/bin/bash

# Setup script to install Git hooks for the repository
# This script should be run by each developer after cloning the repo

set -e

HOOKS_DIR=".git/hooks"
TEMPLATE_DIR=".githooks"

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ ERROR: Not a git repository. Please run this script from the repository root."
    exit 1
fi

# Create hooks directory if it doesn't exist
mkdir -p "$HOOKS_DIR"

echo "📋 Installing Git hooks..."

# Copy all hooks from template directory
if [ -d "$TEMPLATE_DIR" ]; then
    for hook in "$TEMPLATE_DIR"/*; do
        if [ -f "$hook" ]; then
            hook_name=$(basename "$hook")
            cp "$hook" "$HOOKS_DIR/$hook_name"
            chmod +x "$HOOKS_DIR/$hook_name"
            echo "  ✅ Installed: $hook_name"
        fi
    done
else
    echo "❌ ERROR: Template directory '$TEMPLATE_DIR' not found"
    exit 1
fi

echo ""
echo "🎉 Git hooks installed successfully!"
echo ""
echo "ℹ️  All commits will now be validated against Conventional Commits format."
echo "   Format: type(scope): description"
echo ""
echo "   Example: feat(auth): add login functionality"
echo ""