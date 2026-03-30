# How to install ReVoiceChat-MediaServer

## Install Apache2 and PHP
```sh
sudo apt-get install apache2-utils apache2 php libapache2-mod-php php-json php-zip php-curl php-gd -y
```

```sh
sudo systemctl enable apache2
```

```sh
sudo a2enmod headers
```

```sh
sudo a2enmod rewrite
```

```sh
sudo systemctl restart apache2
```

## Create VirtualHost

Create new **VirtualHost**
```sh
sudo nano /etc/apache2/sites-available/rvc_media.conf
```

VirtualHost exemple :
```apache
<VirtualHost *:88>
    Header set Access-Control-Allow-Origin "*"
    Header set Access-Control-Allow-Methods: GET,POST,OPTIONS,DELETE

    DocumentRoot /srv/rvc/revoicechat/media-server/www/
    DirectoryIndex index.php

    <Directory /srv/rvc/revoicechat/media-server/www/>
        AllowOverride all
        Require all granted
    </Directory>

    <Directory /srv/rvc/revoicechat/media-server/www/data/>
        AllowOverride None
        Require all denied
    </Directory>

    ErrorLog /var/log/rvc/media_error.log
    TransferLog /var/log/rvc/media_access.log
    LogLevel info
</VirtualHost>
```

Make sure **/var/log/rvc/** exist and apache2 can write to it

Enable **VirtualHost**
```sh
sudo a2ensite rvcm.conf
```

```sh
sudo systemctl reload apache2
```
