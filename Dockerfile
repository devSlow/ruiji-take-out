# Docker 镜像构建
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>
FROM openjdk:8-jdk-alpine

# Copy local code to the container image.
WORKDIR /ruiji-takeout
COPY /ruiji-take-out-0.0.1-SNAPSHOT.jar /ruiji-takeout/ruiji-take-out-0.0.1-SNAPSHOT.jar


# Run the web service on container startup.
CMD ["java","-jar","/ruiji-takeout/ruiji-take-out-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]