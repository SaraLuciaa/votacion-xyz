# Sistema de Votación (modulo votación)
## Descripción
Sistema distribuido de votación que garantiza entrega confiable de votos mediante el patrón Reliable Messaging, implementado con Ice (ZeroC).

### Compilación
Compilar el proyecto con Gradle:
gradle clean build  

Compilar cada modulo:
- ./gradlew :estacion:build
- ./gradlew :mesa:build
- ./gradlew :reliable:build

### Ejecución 
1. Iniciar Servidor Confiable (Reliable)
java -jar reliable/build/libs/reliable.jar 


2. Iniciar Estación de Recepción
java -jar estacion/build/libs/estacion.jar 


3. Ejecutar Mesa de Votación
java -jar mesa/build/libs/mesa.jar


### Aspectos a destacar
- Se debe ejecutar primero el reliable

### Link a video en youtube y link a documento
- https://youtu.be/dZ4GB_8AcR0
- https://docs.google.com/document/d/1kZk6dM5svRyFkwq8QcS6cZYk0AiT6h0YDknymY7tdlE/edit?tab=t.0
