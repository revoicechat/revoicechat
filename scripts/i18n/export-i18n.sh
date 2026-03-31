#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

rm -rf .tmp-i18n
mkdir -p .tmp-i18n

files=(
  "core-server/i18n/src/main/resources/error_en.properties"
  "core-server/i18n/src/main/resources/risks_en.properties"
  "web-client/www/src/i18n/admin-dashboard_en.properties"
  "web-client/www/src/i18n/frontend_en.properties"
  "web-client/www/src/i18n/login_en.properties"
  "web-client/www/src/i18n/server-settings_en.properties"
  "web-client/www/src/i18n/user-settings_en.properties"
)

count=0
for file in "${files[@]}"; do
  if [ ! -f "$file" ]; then
    printf "${RED}Missing${NC}: ${file}\n"
    continue
  fi
  cp "$file" ".tmp-i18n/$(basename $file)"
  printf "✅ ${GREEN}$(basename $file)${NC} copied\n"
  count=$((count + 1))
done

printf "\n🎉 ${count}/${#files[@]} files copied to ${GREEN}.tmp-i18n${NC}\n"