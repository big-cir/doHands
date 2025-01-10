FROM gradle:8.5-jdk17
WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY src /app/src
RUN gradle clean build -x test --no-daemon
EXPOSE 8080
CMD ["gradle", "bootRun"]