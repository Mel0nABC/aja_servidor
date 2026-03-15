package dev.aja.aja.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Clase donde vamos a especificar la configuración, de manera programática de
 * nuestra aplicación. Qué tenemos, ubicación de los datos, configuración de
 * spring dónde ubicamos datos, etcétera
 */
@Configuration
public class ApplicationConfig {

    /**
     * Constructor creado para ignorar warnings cuando se crea javadoc
     */
    public ApplicationConfig() {
    }

    /**
     * Especificamos donde vamos a almacenar los datos, en este caso, una base de
     * datos postgresql
     * 
     * @return DataSource, Configuración donde se almacenan los datos
     */
    @Bean
    public DataSource dataSource() {

        // https://jdbc.postgresql.org/documentation/use/
        String url = "jdbc:postgresql://docker:5432/aja.database";
        String username = "root";
        String password = "144741"; // this password is for development use only

        // https://docs.spring.io/spring-framework/reference/data-access/jdbc/connections.html#jdbc-datasource
        // Se configura bbdd postgre, quizá se cambie a algo emdebido.
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(url, username, password);
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");

        return driverManagerDataSource;
    }

    /**
     * Configuramos dónde almacenaremos los datos, se obtiene de dataSource()
     * 
     * @return LocalContainerEntityManagerFactoryBean, configuración de Jpa dentro
     *         de spring
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("dev.aja.aja");
        factory.setDataSource(dataSource());

        // Properties to recreate tables when restart application.
        Properties prop = new Properties();
        // prop.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        // prop.setProperty("hibernate.hbm2ddl.auto", "create");
        factory.setJpaProperties(prop);
        return factory;
    }
}
