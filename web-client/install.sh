#!/bin/bash
function pause(){
 read -s -n 1 -p "Press any key to continue ..."
 echo ""
}

echo "ReVoiceChat-WebClient installer"
pause

echo "Installing Apache2 ..."
sudo apt-get install apache2-utils apache2 -y

echo "Configuring Apache2 ..."
sudo cp rvc_client.exemple.conf /etc/apache2/sites-available/rvc_client.conf
sudo a2ensite rvc_client.conf

echo "Enabling Apache2 ..."
sudo systemctl enable apache2
sudo systemctl restart apache2

echo "Done."