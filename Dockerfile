FROM amazoncorretto:21-alpine-jdk
WORKDIR /usr/src/droll

COPY . .
RUN ls
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

ENTRYPOINT ["java", "-jar", "build/libs/droll-1.0-SNAPSHOT.jar"]