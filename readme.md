# Serpiente NoIp

A simple dynamic no-ip user based on aws, java 8, spring boot and docker.

## Preparation

1. Buy a domain on your favorite salesite.
2. Redirect your domain to Route53 on Amazon Web Services
3. Create a hosted zone on Route53
4. Add a subdomain or subdomain type A on Route53
5. Create a user with a group with the rights to modify the domain or subdomain that you want to use, you can use the aws_iam_user_rights.jsonfile to know the rights for user (rememember to change the arn id on rights, that should be like arn:aws:route53:::hostedzone/XXXXXXXXXXXXXXXXXXXXX
6. Create your vars.env file (you can use vars.env.example file as example), add the right values

### You will eed java8 and maven 3+ to compile
Download and settingup maven and java on environment variables on linux
dowload maven from https://maven.apache.org/download.cgi
download java from https://www.oracle.com/mx/java/technologies/javase-jdk11-downloads.html
(you could use jdk 11 to compile despite the code use java 8)
```bash
export MAVEN_HOME=/home/ubuntu/apache-maven-3.8.1^
```
```bash
export JAVA_HOME=/home/ubuntu/jdk-11.0.11
```
```bash
export PATH=$PATH%:$MAVEN_HOME/bin
```

## Compile the code
Required
* Java JDK 8 or highger
* Maven 3.0.6

```bash
mvn clean package -Dmaven.test.skip=true
```

## Build the Docker Image
Required
* Docker installed
```bash
docker build -t noip .
```

## Run on test mode
```bash
docker run -e TZ=America/Mexico_City --env-file ./vars.env noip
```

## Run as daemon and non-stop flags
```bash
docker run -d -e TZ=America/Mexico_City --restart unless-stopped --env-file ./vars.env noip
```


all questions and collaborations are welcome eduardo_gd@hotmail.com
