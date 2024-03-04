FROM openjdk:17

COPY target/Post-0.0.1-SNAPSHOT.jar /post-service/post-service.jar

WORKDIR /post-service
# Câu lệnh CMD để chạy ứng dụng Java khi container được khởi động
CMD ["java", "-jar", "post-service.jar"]