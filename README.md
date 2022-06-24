# AWS Cloud HSM Test Pack

This is a set of tests to highlight implementation challenges encountered whilst using the v5 JCE Provider for AWS HSM. 
Each test contains comments that explain what it is they're proving, and why the author believes the library should work
in the way the test is expecting.

## Setup

The library requires the JCE cloud HSM library to have been 
[installed and configured on the local machine](https://docs.aws.amazon.com/cloudhsm/latest/userguide/java-library-install_5.html).
The JCE provider JAR then needs to be copied from the installation direction to the `lib` directory in this project.

If credentials need provided outside of environment variables then add a line equivalent to the following in `CloudHsmProviderExtension#createDefaultProvider`

```java
provider.login(null, new UsernamePasswordAuthHandler("my_hsm_username", "my_hsm_users_password".toCharArray()));
```

## Execution

`./gradlew test`