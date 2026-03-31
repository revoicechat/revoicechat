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

cd "i18n"
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
