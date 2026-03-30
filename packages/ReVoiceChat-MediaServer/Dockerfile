FROM php:8.2-apache

RUN a2enmod rewrite

COPY www/ /var/www/html/
COPY settings.ini /var/www/settings.ini

RUN chown -R www-data:www-data /var/www/html/data \
    && chmod -R 755 /var/www/html/data

EXPOSE 80
