FROM openjdk:11.0.7-jdk-slim
LABEL maintainer="kwonghung.yip@gmail.com"

ARG BUILD_JAR_FILE
ENV BUILD_JAR_FILE=$BUILD_JAR_FILE
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

WORKDIR /usr/local/springboot

COPY target/$BUILD_JAR_FILE /usr/local/springboot

CMD ["sh","-c","java $JAVA_OPTS -jar $BUILD_JAR_FILE"]
