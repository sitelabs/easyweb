/sbin/ipchains -I input --proto TCP --dport 80 -j REDIRECT 8080
/sbin/iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo ipfw add fwd 127.0.0.1,8080 tcp from me to 127.0.0.1 dst-port 80