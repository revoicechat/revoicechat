> [!CAUTION]
> This application is NOT production ready yet
>
> The main features are implemented but there is still a lot of complementary features missing

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

# [Install manually (Debian)](INSTALL.md)

# First time setup

### How to gain admin rights
- Connect to the WebClient with your server IP.
- On the Login Form, click "Register instead ?" at the bottom
- Put your username and password (it need to comply to your password complexity)
- Leave "invitation code" empty even if you enabled it.
- Click "Register"
- This user is now an admin

**Note : From now on, any user that register will need an invitation code (if enabled), and will be a normal user**

# License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.