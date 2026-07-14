FROM eclipse-temurin:17-jre

WORKDIR /app

COPY chat2db-community.jar /app/chat2db-community.jar
COPY lib/ /app/lib/

EXPOSE 10825
VOLUME ["/root/.chat2db-community"]

ENTRYPOINT ["java", "-Dloader.path=/app/lib", "-Dchat2db.gui=false", "-Dchat2db.runtime.mode=community", "-Dchat2db.network.status=OFFLINE", "-Dserver.address=0.0.0.0", "-Dserver.port=10825", "-Dspring.profiles.active=release", "-jar", "/app/chat2db-community.jar"]
