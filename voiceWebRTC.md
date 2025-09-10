# Install and configure Voice (WebRTC)

## Prerequisite
The following ports need to be open and forwarded to your server :
- 3478 TCP/UDP (TURN)
- 49152–65535 UDP (Voice data)

## Install Coturn

```sh
sudo apt install coturn -y
```

```sh
sudo systemctl enable coturn
```

```sh
sudo systemctl start coturn
```

## Configure Coturn (WIP)

```sh
sudo nano /etc/turnserver.conf
```

By default, all lines in this file are commented out. Below is an example configuration that you can copy and paste into your file.

- Replace `yourdomain.me` with the your domain name.
- Replace `12.34.56.78` with the server public IP address or `0.0.0.0`.
- Set a long and secure authenticate secret. (You can use the openssl rand -base64 20 command to generate a random string.)


```conf
# Specify listening port. Change to 80 or 443 to go around some strict NATs.
listening-port=3478

# Specify listening IP, if not set then Coturn listens on all system IPs. 
listening-ip=12.34.56.78
relay-ip=12.34.56.78

### The following lines enable support for WebRTC ###
# Use fingerprints in the TURN messages.
fingerprint
# Use long-term credentials mechanism
lt-cred-mech
realm=your-domain.com

# Authentication method
use-auth-secret
static-auth-secret=your-auth-secret

total-quota=100

# Total bytes-per-second bandwidth the TURN server is allowed to allocate
# for the sessions, combined (input and output network streams are treated separately).
bps-capacity=0

# This line provides extra security.
stale-nonce

log-file=/var/log/turnserver/turn.log
no-loopback-peers
no-multicast-peers
```