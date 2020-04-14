FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8080

ENV JAVA_OPTS  "\
    -XX:+UnlockExperimentalVMOptions \
    -Xmx512M"

ADD target/career-app*.jar career-app.jar

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar career-app.jar"]