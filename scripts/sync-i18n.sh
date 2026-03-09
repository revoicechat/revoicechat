#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

step() {
  printf "\n${BLUE}▶ $1${NC}\n"
}

step "Exporting source files to .tmp-i18n..."
bash "./scripts/i18n/export-i18n.sh" || { printf "${RED}export-i18n.sh failed${NC}\n"; exit 1; }

cd "ReVoiceChat-i18n"
step "Updating i18n files..."
bash "../scripts/i18n/update-i18n.sh" -src ../.tmp-i18n || { printf "${RED}update-i18n.sh failed${NC}\n"; cd ..; exit 1; }

step "Updating README progress table..."
bash "./.scripts/generate-progress.sh" || { printf "${RED}generate-progress.sh failed${NC}\n"; cd ..; exit 1; }

step "Generating .generated files..."
bash "../scripts/i18n/generate-i18n.sh" || { printf "${RED}generate-i18n.sh failed${NC}\n"; cd ..; exit 1; }
cd ..

step "Importing back to source projects..."
bash "./scripts/i18n/import-i18n.sh" || { printf "${RED}import-i18n.sh failed${NC}\n"; exit 1; }

printf "\n🎉 ${GREEN}Sync complete.${NC} ${count} files imported back to source projects.\n"

step "Committing changes in submodules and root..."
repos=(
  "ReVoiceChat-CoreServer"
  "ReVoiceChat-WebClient"
  "ReVoiceChat-MediaServer"
  "ReVoiceChat-i18n"
)

for repo in "${repos[@]}"; do
  cd "$repo"
  if git diff --quiet && git diff --cached --quiet; then
    printf "⏭️  ${repo}: nothing to commit\n"
  else
    git add .
    git commit -m "chore(i18n): sync translations"
    git push
    printf "✅ ${GREEN}${repo}${NC} committed\n"
  fi
  cd ..
done

# Commit root repo (updates submodule pointers)
if git diff --quiet && git diff --cached --quiet; then
  printf "⏭️  root: nothing to commit\n"
else
  git add .
  git commit -m "chore(i18n): update submodule references"
  git push
  printf "✅ ${GREEN}root${NC} committed\n"
fi

printf "\n🎉 ${GREEN}All repositories committed.${NC}\n"
