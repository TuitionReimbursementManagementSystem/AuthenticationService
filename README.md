# TRMS: Authentication Service

## Project Description
The Authentication Service API for the Tuition Reimbursement Management System manages authentication and authorization for the application.

## Technologies Used
![](https://img.shields.io/badge/-Java-007396?style=flat-square&logo=java&logoColor=white)
![](https://img.shields.io/badge/-Spring_Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![](https://img.shields.io/badge/-Spring_Webflux-236DB33F?style=flat-square&logo=spring&logoColor=white)
![](https://img.shields.io/badge/-Cassandra-1287B1?style=flat-square&logo=apachecassandra&logoColor=white)
![](https://img.shields.io/badge/-RabbitMQ-23FF66?style=flat-square&logo=rabbitmq&color=white)
![Spring Security](https://img.shields.io/badge/-Spring_Security-6DB33F?style=flat-square&logo=spring-security&logoColor=white)
![JUnit](https://img.shields.io/badge/-JUnit-25A162?style=flat-square&logo=junit5&logoColor=white)
![Docker](https://img.shields.io/badge/-Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/-AWS-232F3E?style=flat-square&logo=amazon-aws&logoColor=white)
![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)
![Microservices](https://img.shields.io/badge/-Microservices-000000?style=flat-square&logo=cloud&logoColor=white)
![Eureka](https://img.shields.io/badge/-Eureka-E50914?style=flat-square&logo=Netflix&logoColor=white)


## Features
* 

To-Do List:
* 

## Getting Started
1. Using your CLI tool, `cd` into the directory you want to store the project in.

2. Clone the repository using: `git clone git@github.com:TuitionReimbursementManagementSystem/AuthenticationService.git`

3. Configure your environment variables if you do not wish to use the provided defaults:
   * EUREKA_URL: The URL for the TRMS Discovery Service
   * AWS_USER: The AWS IAM username that has Keyspaces permissions
   * AWS_PASS: The password for the IAM User that has Keyspaces permissions

4. Create a Keyspace with the name: `trms` or choose one of your own by modifying the `CassandraConfig.java` file:
```
@Configuration
@EnableReactiveCassandraRepositories(basePackages = {"com.skillstorm.repositores"})
public class CassandraConfig {

    @Bean
    CqlSessionFactoryBean session() {
        CqlSessionFactoryBean cqlSessionFactory = new CqlSessionFactoryBean();
        DriverConfigLoader loader = DriverConfigLoader.fromClasspath("application.conf");
        cqlSessionFactory.setSessionBuilderConfigurer(sessionBuilder ->
              sessionBuilder.withConfigLoader(loader).withKeyspace("trms"));
        cqlSessionFactory.setKeyspaceName("trms");

        return cqlSessionFactory;
    }

    ...
}
```

5. The schema will be auto-generated when you run the program, but you can modify the auto-ddl statements by editing the `CassandraConfig.java`
   file in the `configs` package:
```
@Configuration
@EnableReactiveCassandraRepositories(basePackages = {"com.skillstorm.repositores"})
public class CassandraConfig {

    ...

    @Bean
    public SessionFactoryFactoryBean sessionFactory(CqlSession session, CassandraConverter converter) {
        SessionFactoryFactoryBean sessionFactory = new SessionFactoryFactoryBean();
        ((MappingCassandraConverter) converter).setUserTypeResolver(new SimpleUserTypeResolver(session));
        sessionFactory.setSession(session);
        sessionFactory.setConverter(converter);
        // Auto-ddl statement: CREATE, CREATE_IF_NOT_EXISTS, NONE:
        sessionFactory.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);

        return sessionFactory;
    }

    ...  
}
```

6. Set up the Trust Store for AWS Keyspaces.
   #### Note: These commands do not work in Powershell! You will need to use a Bash terminal if using Windows:
   * `cd` into the `/src/main/resources` folder and run the following commands:
   * `curl https://certs.secureserver.net/repository/sf-class2-root.crt -O`
   * `openssl x509 -outform der -in sf-class2-root.crt -out temp_file.der`
   * `keytool -import -alias cassandra -keystore cassandra_truststore.jks -file temp_file.der`
   * Set the password to match the `truststore-password` set in the `application.conf` file: `p4ssw0rd`
   * Say `yes` to trust the certificate when prompted
  
7. Configure datastax in the `application.conf` file to set the `basic.contact-points` and the `local-datacenter` to the region where your Keyspace is located. By default,
   we have chosen `us-east-2`:
```
datastax-java-driver {

    basic.contact-points = [ "cassandra.us-east-2.amazonaws.com:9142"]
    basic.request.consistency = LOCAL_QUORUM
    advanced.auth-provider{
        class = PlainTextAuthProvider
        username = ${AWS_USER}
        password = ${AWS_PASS}
    }
    basic.load-balancing-policy {
        local-datacenter = "us-east-2"
    }

    advanced.ssl-engine-factory {
        class = DefaultSslEngineFactory
        truststore-path = "./src/main/resources/cassandra_truststore.jks"
        truststore-password = "p4ssw0rd"
    }
}
```


## Usage

1. Ideally all requests would be sent through the Gateway Service, which by default is configured for port `8125`. If sending requests to the Authentication Service server directly rather than through the Gateway Service we utilize port `8083`.
   This can be changed in the `/src/main/resources/application.yml` file:
```
{
  # Configure Netty server:
  server:
    port: 8083
}
```
2. Assuming you are hosting locally and sending requests directly to the User Service API, it functions as follows:

### Login:
1. Note: Placeholder.
3. `POST` to `http://localhost:8083/authentications/login`
4. With the request body:
```
{
    "username": "[String]",
    "password": "[String]"
}
```


## Contributors
* Quentin Hardwick

## License
This project uses the following license: <license_name>.
