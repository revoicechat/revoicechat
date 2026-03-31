# Host to install Core-Server

## Install Java 25

```sh
wget https://download.oracle.com/java/25/latest/jdk-25_linux-x64_bin.deb
```

```sh 
sudo apt install ./jdk-25_linux-x64_bin.deb
```

```sh
java --version
```

## Install and configure PostGreSQL

```sh 
sudo apt install postgresql
```

```sh 
sudo -i -u postgres
```

```sh 
psql
```

```sql
CREATE USER revoicechat_user WITH PASSWORD 'secure_password';
```

```sql
CREATE DATABASE revoicechat_db OWNER = revoicechat_user;
```

`exit` to quit psql

```exit``` to quit postgres user

## generate rsa key for JWT tokens

```sh
cd core-server/
./scripts/generate_jwtKey.sh
```

it will generate two file in `/jwt`

## Configure server.properties

Copy `server.exemple.properties` to `server.properties`

```sh 
cp ./server.exemple.properties ./server.properties
```

Edit `./server.properties` and update `quarkus.datasource.username` and `quarkus.datasource.password`

### Build the app
 - run `./scripts/build-app.sh`

### Run server

#### Option A : as an app (.jar)
 - run the `./scripts/run-app.sh`

#### Option B : As a service (systemd)
- Copy `revoicechat.service.example` to `revoicechat.service`
```sh
cp revoicechat.service.example revoicechat.service
```
- Link the service file
```sh
sudo systemctl link /srv/revoicechat/core-server/revoicechat.service
```
- Reload daemon
```sh
sudo systemctl daemon-reload
```
- Enable service
```sh
sudo systemctl enable revoicechat.service
```
- Start service
```sh
sudo systemctl start revoicechat.service
```
- Check service status
```sh
sudo systemctl status revoicechat.service
```

Expected output
```log 
* rvc-core.service - ReVoiceChat Core Server
     Loaded: loaded (/etc/systemd/system/revoicechat.service; enabled; preset: enabled)
     Active: active (running) since Sun 2025-08-24 12:14:11 UTC; 1s ago
```