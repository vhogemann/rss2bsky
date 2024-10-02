FROM eclipse-temurin:21 AS build_image
ENV APP_HOME=/root/dev/
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
COPY app/build.gradle settings.gradle gradlew gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle
# download dependencies
RUN ./gradlew build -x test --continue
COPY . .
RUN ./gradlew build

FROM eclipse-temurin:21-jre
WORKDIR /root/
COPY --from=build_image /root/dev/app/build/libs/app.jar .
RUN mkdir -p /root/dev/json

# Set environment variables
ENV JSON_PATH=/root/dev/json

# Use this to have access to the json files
VOLUME /root/dev/json

CMD ["java","-jar","app.jar"]