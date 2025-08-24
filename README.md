# How to selfhost ReVoiceChat

You can use one host per server or one host for everything. 

Keep in mind that ```ReVoiceChat-MediaServer``` can get quite big if you store a lot of files.

# Install services

[How to install ReVoiceChat-CoreServer](https://github.com/revoicechat/ReVoiceChat-CoreServer/blob/main/README.md)

[How to install ReVoiceChat-MediaServer](https://github.com/revoicechat/ReVoiceChat-MediaServer/blob/main/INSTALL.md)

[How to install ReVoiceChat-WebClient](https://github.com/revoicechat/ReVoiceChat-WebClient/blob/main/INSTALL.md)

# Configure reverse proxy

We recommend using [Nginx Proxy Manager](https://nginxproxymanager.com/).

## Routes

Assuming you are using `app.yourdomain.me` for the frontend and `core.yourdomain.me` for the backend, here is the table of routes : 

Destination | IP | Port
---|---|---
app.yourdomain.me/* | IP of WebClient | 80
core.yourdomain.me/* | IP of CoreServer | 8080
core.yourdomain.me/sse | IP of CoreServer | 8080
core.yourdomain.me/signal | IP of CoreServer | 8080
core.yourdomain.me/stun | IP of CoreServer | 3478
core.yourdomain.me/media | IP of MediaServer | 88

## Configuring Nginx Proxy Manager

### WebClient
Add a new proxy host with the following :

#### Details
- Domain names : `app.yourdomain.me`
- Scheme : `http`
- Forward Hostname / IP : `Your IP`
- Forward Port : `80`

#### Custom locations
- None

#### SSL
- You may want to add SSL, if so, enable `Force SSL` and `HTTP/2 Support`

#### Advanced
- None

### Core and Media

Add a new proxy host with the following :

#### Details
- Domain names : `core.yourdomain.me`
- Scheme : `http`
- Forward Hostname / IP : `IP of CoreServer`
- Forward Port : `8080`
- Websockets support : `enable`

#### Custom locations

#### Custom location `/sse`
- Location : `/sse`
- Scheme : `http`
- Forware Hostname/IP : `IP of CoreServer`
- Forward Port : `8080`
- Advance (click on the cog) : `proxy_read_timeout 4h;`

#### Custom location `/signal`
- Location : `/signal`
- Scheme : `http`
- Forware Hostname/IP : `IP of CoreServer`
- Forward Port : `8080`
- Advance (click on the cog) : `proxy_read_timeout 4h;`

#### Custom location `/stun`

- Location : `/signal`
- Scheme : `http`
- Forware Hostname/IP : `IP of StunServer` (Should be the same as CoreServer)
- Forward Port : `3478` (default for coturn)

#### Custom location `/media`

- Location : `/signal`
- Scheme : `http`
- Forware Hostname/IP : `IP of MediaServer`
- Forward Port : `88` (default for coturn)

### SSL
- You may want to add SSL, if so, enable `Force SSL` and `HTTP/2 Support`

### Advanced
- None