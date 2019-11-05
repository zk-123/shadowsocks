# shadowsocks-server
usage:
```
java -jar shadowsocks-server.jar -s "0.0.0.0:1080" -p "123456" -m "aes-128-cfb" 
```

more :
```
usage: java -jar shadowsocks-server.jar -h
 -s,--address <ip:port>     server listen address. e.g: ip:port
 -p,--password <password>   password
 -m,--method <methodName>   encrypt method. support method: [camellia-128-cfb, salsa20, chacha20-ietf, camellia-192-cfb, camellia-256-cfb, chacha20,
                            aes-128-cfb, aes-256-cfb, rc4-md5, aes-192-cfb, aes-192-gcm, aes-256-gcm, aes-128-gcm]
 -h                         usage help
 -help                      usage full help
```
more options with parameter '-help'

# shadowsocks-client-socks5

usage:
```
java -jar shadowsocks-socks.jar -s "127.0.0.1:1088" -p "123456" -m "aes-256-gcm" -c "0.0.0.0:1081"
```

more:
```
usage: java -jar shadowsocks-socks.jar -h
 -s,--server_address <ip:port>   server connect address. e.g: ip:port
 -p,--password <password>        server password
 -m,--method <methodName>        encrypt method. support method: [camellia-128-cfb, salsa20, chacha20-ietf, camellia-192-cfb, camellia-256-cfb,
                                 chacha20, aes-128-cfb, aes-256-cfb, rc4-md5, aes-192-cfb, aes-192-gcm, aes-256-gcm, aes-128-gcm]
 -c,--local_address <ip:port>    local expose address. e.g: 0.0.0.0:1080
 -h                              usage help
 -help                           usage full help
```
more options with parameter '-help'