#!/bin/bash

to_crlf() {
  sed -i 's/\r$//' "$1"   # normalize first (remove any existing CR)
  sed -i 's/$/\r/' "$1"   # then add CR to every line
}

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

src=".generated"
dest="."

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    -src)  src="$2";  shift 2 ;;
    -dest) dest="$2"; shift 2 ;;
    *) printf "${RED}Unknown argument: $1${NC}\n"; exit 1 ;;
  esac
done

if [ -z "$src" ]; then
  printf "${RED}Usage: ./update-i18n.sh <directory>${NC}\n"
  exit 1
fi

if [ ! -d "$src" ]; then
  printf "${RED}Error: '${src}' is not a directory${NC}\n"
  exit 1
fi

# Find all managed languages from existing subdirectories
langs=$(find "$dest" -maxdepth 2 -name "*.properties" -not -name "en.properties" -not -path "./.generated/*" -not -path "./.scripts/*" | xargs -I{} basename {} .properties | sed 's/.*_//' | sort -u)

printf "📦 Detected languages: $(echo $langs | tr '\n' ' ')\n\n"

# For each *_en.properties file in the source directory
find "$src" -name "*_en.properties" | sort | while read en_file; do
  base=$(basename "$en_file" _en.properties)
  out_dir="${dest}/${base}"

  mkdir -p "$out_dir"

  # Regenerate en.properties
  cp "$en_file" "${out_dir}/en.properties"
  to_crlf "${out_dir}/en.properties"
  # shellcheck disable=SC2059
  printf "✅ ${GREEN}${out_dir}/en.properties${NC} regenerated\n"

  # For each managed language
  while read lang; do
    target="${out_dir}/${lang}.properties"

    if [ -f "$target" ]; then
      # Complete with missing keys (empty value)
      added=0
      while IFS='=' read -r key value; do
        [[ "$key" =~ ^#.*$ || -z "$key" ]] && continue
        if ! grep -q "^${key}=" "$target"; then
          echo "${key}=" >> "$target"
          added=$((added + 1))
        fi
      done < "$en_file"
      # shellcheck disable=SC2059
      printf "🟡 ${YELLOW}${target}${NC} updated (+${added} missing keys)\n"
    else
      # Create with empty values
      sed 's/=.*/=/' "$en_file" > "$target"
      to_crlf "$target"
      # shellcheck disable=SC2059
      printf "🆕 ${GREEN}${target}${NC} created\n"
    fi

  done <<< "$langs"

  echo ""
done