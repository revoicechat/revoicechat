#!/bin/bash

# Script to update all submodules from their remote repositories
# If changes are detected, automatically commit and push them

set -e  # Exit on any error

echo "🔄 Updating submodules from remote repositories..."

# Update all submodules to their latest remote version
# --remote: fetch latest changes from remote
# --merge: merge changes into current branch
git submodule update --remote --merge

# Check if there are any changes (staged or unstaged)
if git diff --quiet && git diff --cached --quiet; then
    echo "✅ No changes detected in submodules"
    exit 0
fi

echo "📝 Changes detected, creating commit..."

# Stage all changes
git add .

# Create commit with timestamp
COMMIT_MSG="Update submodules - $(date '+%Y-%m-%d %H:%M:%S')"
git commit -m "$COMMIT_MSG"

echo "🚀 Pushing changes to remote..."
git push

echo "✅ Submodules successfully updated and pushed!"