# Reverse Proxy settings

Here is the reverse proxy setting to run the core an the media on the same port.

If you run the media server, and the core server, you can run the reverse proxy as well, and you will have : 

 - `http://localhost:3000/api/*` -> `http://localhost:8080/api/*`
 - `http://localhost:3000/media/*` -> `http://localhost:80/media/*`