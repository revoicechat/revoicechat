# How to selfhost ReVoiceChat

You can use one host per service or one host for everything. 

Keep in mind that ```ReVoiceChat-MediaServer``` can get quite big if you store a lot of files.

# Install using docker

This repository can help you run all service.

- Clone the project with all submodules : 
```shell
git clone --recursive https://github.com/revoicechat/ReVoiceChat-Selfhost.git
```
- Init the project : 
```shell
./scripts/init-project.sh
```
- You can modify `server.core.properties` and `settings.media.ini` if you need to override some value
- Deploy all the docker images
```shell
./scripts/deploy-update.sh
```
NB : this script will automatically update the project

# Install manually

## Install services

[How to install ReVoiceChat-CoreServer](https://github.com/revoicechat/ReVoiceChat-CoreServer/blob/main/README.md)

[How to install ReVoiceChat-MediaServer](https://github.com/revoicechat/ReVoiceChat-MediaServer/blob/main/INSTALL.md)

[How to install ReVoiceChat-WebClient](https://github.com/revoicechat/ReVoiceChat-WebClient/blob/main/INSTALL.md)


## Configure reverse proxy

We recommend using [Nginx Proxy Manager](https://nginxproxymanager.com/).

### Routes

Assuming you are using `revoicechat.yourdomain.me` , here is the table of routes : 

Destination | IP | Port
---|---|---
revoicechat.yourdomain.me/* | IP of WebClient | 80
revoicechat.yourdomain.me/api | IP of CoreServer | 8080
revoicechat.yourdomain.me/media | IP of MediaServer | 88

### Configuring Nginx Proxy Manager

Add a new proxy host with the following :

#### Details
- Domain names : `revoicechat.yourdomain.me`
- Scheme : `http`
- Forward Hostname / IP : `IP of WebClient`
- Forward Port : `80`
- Websockets support : `enable`

#### Custom locations
##### `/api`
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

##### `/media`

- Location : `/media`
- Scheme : `http`
- Forware Hostname/IP : `IP of MediaServer`
- Forward Port : `88` (default for the VirtualHost)
- Advance :
```nginx
proxy_set_header  Authorization $http_authorization;
proxy_pass_header Authorization;

client_max_body_size 120M;

# Rate limiter (optionnal)
limit_rate 15000k;
limit_rate_after 5000k; 

# Remove header
proxy_hide_header 'Access-Control-Allow-Headers';
proxy_hide_header 'Access-Control-Allow-Origin';
proxy_hide_header 'Access-Control-Allow-Credentials';
proxy_hide_header 'Access-Control-Allow-Methods';

# CORS headers
add_header 'Access-Control-Allow-Origin' '*' always;
add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, PATCH, DELETE, OPTIONS' always;
add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;

# OPTIONS preflight
if ($request_method = OPTIONS) {
    add_header 'Access-Control-Allow-Origin' '*' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, PATCH, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;
    add_header 'Access-Control-Max-Age' 1728000 always;
    add_header 'Content-Length' 0 always;
    add_header 'Content-Type' 'text/plain; charset=UTF-8' always;
    return 204;
}
```

#### SSL
- You may want to add SSL, if so, enable `Force SSL` and `HTTP/2 Support`

#### Advanced

- None

# License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

Copyright (C) 2025 RevoiceChat.fr