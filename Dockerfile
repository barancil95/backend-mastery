# ============================================================
# 1. AŞAMA: DERLEME (BUILD STAGE)
# ============================================================
# Projemizi derlemek için Maven ve JDK 17 barındıran resmi imajı kullanıyoruz.
FROM maven:3.9-eclipse-temurin-17 AS build

# Konteyner içinde çalışma dizinini belirliyoruz
WORKDIR /app

# pom.xml dosyasını kopyalıyoruz
COPY pom.xml .

# Bağımlılıkları (Dependencies) indirmek için ön derleme yapıyoruz. 
# Bu adım Docker cache mekanizmasını kullanmak için önemlidir. 
# pom.xml değişmedikçe kütüphaneler tekrar indirilmez.
RUN mvn dependency:go-offline -B

# src (kaynak kodlar) klasörünü kopyalıyoruz
COPY src ./src

# Projeyi derleyip çalıştırılabilir JAR dosyasını üretiyoruz.
# Testleri build esnasında koşturmamak için -DskipTests ekliyoruz.
RUN mvn clean package -DskipTests

# ============================================================
# 2. AŞAMA: ÇALIŞTIRMA (RUN STAGE)
# ============================================================
# Sadece uygulamayı çalıştırmaya yarayan JRE 17 imajını kullanıyoruz (Apple Silicon uyumlu).
FROM eclipse-temurin:17-jre

WORKDIR /app

# 1. Aşamadaki (build) üretilen JAR dosyasını bu hafif aşamaya kopyalıyoruz
COPY --from=build /app/target/*.jar app.jar

# Uygulamanın dış dünyaya açılacağı port (Spring Boot varsayılan: 8080)
EXPOSE 8080

# Konteyner başladığında çalışacak Java komutu
ENTRYPOINT ["java", "-jar", "app.jar"]
