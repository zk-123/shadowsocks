# shadowsocks-server
usage:
```
java -jar shadowsocks-xxx.jar -p 123456 -m aes-128-cfb 
```

more :
```
usage: java -jar shadowsocks-xxx.jar -help
 -bn,--boss_number <arg>          boss thread number
 -cai,--client_all_idle <arg>     client allIdle time(second)
 -cri,--client_read_idle <arg>    client readIdle time(second)
 -cwi,--client_write_idle <arg>   client writeIdle time(second)
 -d,--address                     address bind
 -help                            usage help
 -level,--log_level <arg>         log level
 -m,--method <arg>                encrypt method
 -P,--port <arg>                  port bind
 -p,--password <arg>              password of ssserver
 -rai,--remote_all_idle <arg>     remote allIdle time(second)
 -rri,--remote_read_idle <arg>    remote readIdle time(second)
 -rwi,--remote_write_idle <arg>   remote writeIdle time(second)
 -wn,--workers_number <arg>       workers thread number
 
```
option |  default value| description |
---|---|---
P | 1080 | ss-server expose port
p | - | password
m | - | encrypt method
d | 0.0.0.0 | bind address
bn | process number * 2 | boss thread number
wn | process number * 2 | workers thread number
cri | 20 * 60 | client readIdle time(second)
cwi | 20 * 60 | client writeIdle time(second)
cai | 20 * 60 | client allIdle time(second)
rri | 20 * 60 | remote readIdle time(second)
rwi | 20 * 60 | remote writeIdle time(second)
rai | 20 * 60 | remote allIdle time(second)
level | INFO | log level




> shadowsocks-server learn from [this](https://github.com/TongxiJi/shadowsocks-java)

# shadowsocks-socks5-client
