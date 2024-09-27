FROM eclipse-temurin:21 AS BUILD_IMAGE
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
COPY --from=BUILD_IMAGE /root/dev/app/build/libs/app.jar .
CMD ["java","-jar","app.jar"]