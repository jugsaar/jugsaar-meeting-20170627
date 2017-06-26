# Service Discovery and Client-side Load Balancing

Consul, Spring Boot, Spring Cloud, Feign, Ribbon

# Setup

## Start Consul as single-node cluster

```
etc/single-node-consul-cluster.sh
```

## Start the greeting-service Instances

Instance 1
```
java \
  -Dspring.profiles.active=instance1,consul -Xmx128m \
  -jar greeting-service/target/greeting-service-*.jar
```

Instance 2
```
java \
  -Dspring.profiles.active=instance2,consul -Xmx128m \
  -jar greeting-service/target/greeting-service-*.jar
```

Instance 3
```
java \
  -Dspring.profiles.active=instance3,consul -Xmx128m \
  -jar greeting-service/target/greeting-service-*.jar
```

Instance 4
```
java \
  -Dspring.profiles.active=instance4,consul -Xmx128m \
  -jar greeting-service/target/greeting-service-*.jar
```

## Run consuming client

test-Environment
```
java \
  -Dspring.profiles.active=test \
  -jar greeting-client-dynamic-feign-example/target/greeting-client-*.jar
```

prod-Environment
```
java \
  -Dspring.profiles.active=prod \
  -jar greeting-client-dynamic-feign-example/target/greeting-client-*.jar
```

# Git config in Consul

## Create git repositories
```bash
cd ./consul-git-config

mkdir balanced-greeting-service
cd balanced-greeting-service
git init . \
  && mkdir config \
  && touch config/default.yml \
  && git add . \
  && git commit -m "Initial import" \
  && git co -b dev \
  && git co -b test
cd ..

mkdir edlohn 
cd edlohn
git init . \
  && mkdir config \
  && touch config/default.yml \
  && git add . \
  && git commit -m "Initial import" \
  && git co -b dev \
  && git co -b test
cd ..
```

## Install git2consul
See https://github.com/Cimpress-MCP/git2consul for installing `git2consul`
 
## git2consul example config

```bash
cat <<EOF > /tmp/git2consul.json
{
  "version": "1.0",
  "repos" : [{
    "name" : "edlohn",
    "url" : "/home/tom/dev/playground/consul/git-repos/edlohn",
    "source_root": "",
    "mountpoint": "config",
    "ignore_file_extension": true,
    "branches" : ["dev","test"],
    "expand_keys": true,
    "hooks": [{
      "type" : "polling",
      "interval" : "1"
    }]
  },
  {
    "name" : "balanced-greeting-service",
    "url" : "/home/tom/dev/playground/consul/git-repos/balanced-greeting-service",
    "source_root": "config",
    "mountpoint": "config",
    "ignore_file_extension": true,
    "branches" : ["dev","test"],
    "expand_keys": true,
    "hooks": [{
      "type" : "polling",
      "interval" : "1"
    }]
  }]
}
EOF
```

## Run git2consul

```bash
git2consul --endpoint 127.0.0.1 --port 8500 --config-file /tmp/git2consul.json | jq .
```