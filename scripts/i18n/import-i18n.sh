#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

declare -A generated_to_source=(
  ["error"]="core-server/i18n/src/main/resources"
  ["risks"]="core-server/i18n/src/main/resources"
  ["admin-dashboard"]="web-client/www/src/i18n"
  ["frontend"]="web-client/www/src/i18n"
  ["login"]="web-client/www/src/i18n"
  ["server-settings"]="web-client/www/src/i18n"
  ["user-settings"]="web-client/www/src/i18n"
)

count=0
for name in "${!generated_to_source[@]}"; do
  dest="${generated_to_source[$name]}"
  for file in i18n/.generated/${name}_*.properties; do
    [ -f "$file" ] || continue
    if [ ! -d "$dest" ]; then
      printf "${RED}Missing destination directory: ${dest}${NC}\n"
      continue
    fi
    cp "$file" "$dest/$(basename $file)"
    printf "✅ ${GREEN}$(basename $file)${NC} → ${dest}\n"
    count=$((count + 1))
  done
done