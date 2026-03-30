# How to install ReVoiceChat-WebClient

## Clone this repository

For this guide, we will use ```/srv/rvc``` but you can use any directory (don't forget to change ```/srv/rvc``` to your path)

```sh
git clone https://github.com/revoicechat/ReVoiceChat-WebClient
```
```sh
cd ReVoiceChat-WebClient/
```

## Option A : Auto install
### Run installer (debian)
```sh
sudo ./install.sh
```

## Option B : Manual install
### Install Apache2 (skip this if you already installed MediaServer)
```sh
sudo apt-get install apache2-utils apache2 -y
```

```sh
sudo systemctl enable apache2
```

```sh
sudo a2enmod headers
```

### Create VirtualHost

Create new **VirtualHost**

### Create new VirtualHost from exemple
```sh
sudo cp rvc_client.exemple.conf /etc/apache2/sites-available/rvc_client.conf
```

**Cache-Control** can be set to **max-age=86400, must-revalidate**

Make sure **/var/log/rvc/** exist and apache2 can write to it

Enable **VirtualHost**
```sh
sudo a2ensite rvc_client.conf
```
```sh
sudo systemctl reload apache2
```

You can now access this app in your favorite browser

## Troubleshooting

If you get apache2 default page, you need to disable the default config

```sh
sudo a2dissite 000-default.conf
```
```sh
sudo systemctl reload apache2
```