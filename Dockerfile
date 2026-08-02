# 1. Imagem base super leve contendo apenas a máquina virtual Java (JRE) 21
FROM eclipse-temurin:21-jre-alpine

# 2. Define a pasta interna do contêiner onde a nossa aplicação vai morar
WORKDIR /app

# 3. Copia o arquivo .jar gerado pelo Gradle para dentro do contêiner
COPY build/libs/*.jar app.jar

# 4. O comando de ignição que será executado assim que o contêiner ligar
ENTRYPOINT ["java", "-jar", "app.jar"]