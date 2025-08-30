# How to selfhost ReVoiceChat

You can use one host per server or one host for everything. 

Keep in mind that ```ReVoiceChat-MediaServer``` can get quite big if you store a lot of files.

# Install services

[How to install ReVoiceChat-CoreServer](https://github.com/revoicechat/ReVoiceChat-CoreServer/blob/main/README.md)

[How to install ReVoiceChat-MediaServer](https://github.com/revoicechat/ReVoiceChat-MediaServer/blob/main/INSTALL.md)

[How to install ReVoiceChat-WebClient](https://github.com/revoicechat/ReVoiceChat-WebClient/blob/main/INSTALL.md)

[How to install voice services](voice.md)

# Configure reverse proxy

We recommend using [Nginx Proxy Manager](https://nginxproxymanager.com/).

## Routes

Assuming you are using `client.yourdomain.me` for the frontend and `server.yourdomain.me` for the backend, here is the table of routes : 

Destination | IP | Port
---|---|---
client.yourdomain.me/* | IP of WebClient | 80
server.yourdomain.me/* | IP of CoreServer | 8080
server.yourdomain.me/api | IP of CoreServer | 8080
server.yourdomain.me/stun | IP of CoreServer | 3478
server.yourdomain.me/media | IP of MediaServer | 88

## Configuring Nginx Proxy Manager

### WebClient
Add a new proxy host with the following :

#### Details
- Domain names : `client.yourdomain.me`
- Scheme : `http`
- Forward Hostname / IP : `IP of WebClient`
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
- Domain names : `server.yourdomain.me`
- Scheme : `http`
- Forward Hostname / IP : `IP of CoreServer`
- Forward Port : `8080`
- Websockets support : `enable`

#### Custom locations

#### Custom location `/api`
- Location : `/api`
- Scheme : `http`
- Forware Hostname/IP : `IP of CoreServer`
- Forward Port : `8080`
- Advance (click on the cog) : 
```nginx
proxy_hide_header 'Access-Control-Allow-Origin';
proxy_hide_header 'Access-Control-Allow-Credentials';
proxy_hide_header 'Access-Control-Allow-Headers';
proxy_hide_header 'Access-Control-Allow-Methods';

add_header 'Access-Control-Allow-Origin' '*';
add_header 'Access-Control-Allow-Credentials' 'true';
add_header 'Access-Control-Allow-Headers' 'Authorization,Accept,Origin,DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range';
add_header 'Access-Control-Allow-Methods' 'GET,POST,OPTIONS,PUT,DELETE,PATCH';

proxy_read_timeout 8h;
```

#### Custom location `/stun`

- Location : `/stun`
- Scheme : `http`
- Forware Hostname/IP : `IP of StunServer` (Should be the same as CoreServer)
- Forward Port : `3478` (default for coturn)

#### Custom location `/media`

- Location : `/media`
- Scheme : `http`
- Forware Hostname/IP : `IP of MediaServer`
- Forward Port : `88` (default for the VirtualHost)

### SSL
- You may want to add SSL, if so, enable `Force SSL` and `HTTP/2 Support`

### Advanced
- None