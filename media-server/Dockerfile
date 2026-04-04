FROM php:8.2-apache

RUN a2enmod rewrite

RUN apt-get update && apt-get install -y \
    libpng-dev \
    libjpeg-dev \
    libwebp-dev \
    && rm -rf /var/lib/apt/lists/*

RUN docker-php-ext-configure gd --with-jpeg --with-webp \
    && docker-php-ext-install gd exif

COPY www/ /var/www/html/
COPY settings.ini /var/www/settings.ini

RUN chown -R www-data:www-data /var/www/html/data \
    && chmod -R 755 /var/www/html/data

EXPOSE 80