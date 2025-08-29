# fundicion lara backend

### Lo que necesitas

- Un editor de texto o IDE de tu preferencia
- [JDK 11](https://openjdk.java.net/projects/jdk/14/) o superior
- [Gradle 5+](https://gradle.org/)
- También puedes importar el código directamente en tu IDE:
    * [Spring Tool Suite (STS)](https://spring.io/tools)
    * [IntelliJ IDEA](https://www.jetbrains.com/idea/)

> **NOTA:** también puedes instalar Gradle y Java utilizando [SdkMan](https://sdkman.io/)

---

### Iniciar el proyecto

- Puedes iniciar el proyecto ejecutando el siguiente comando:

  ```bash
  ./gradlew bootRun

- Y puedes probar el endpoint accediendo a la siguiente URL:
  > `http://localhost:8080/swagger-ui/index.html#/`
  > 
  > `/api-docs`
  >

## Flujo de trabajo con Git: [Gitflow Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

1. Se crea una rama `develop` a partir de `master`
2. Se crea una rama `release` a partir de `develop`
3. Las ramas `feature` se crean a partir de `develop`
4. Cuando una `feature` está completa, se fusiona en la rama `develop`
5. Cuando la rama `release` está lista, se fusiona tanto en `develop` como en `master`
6. Si se detecta un problema en `master`, se crea una rama `hotfix` a partir de `master`
7. Una vez que el `hotfix` está completo, se fusiona tanto en `develop` como en `master`

## Referencias

- [Gitflow Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)
