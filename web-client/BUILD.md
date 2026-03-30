## How to build ReVoiceChat-WebClient

### Production

```sh
npm install
npm run tauri:build
```

### Development

```sh
npm install
npm run tauri:dev
```

### Dependencies for Debian 13
```sh
sudo apt-get install build-essential libgtk-3-dev
sudo apt-get install librust-soup3-sys-dev
sudo apt-get install webkit2gtk-4.1.pc
```