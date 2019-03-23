/*
 * Copyright 2019 ELIXIR EGA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.elixir.ega.ebi.keyproviderservice.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile("db")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "keyEntityManagerFactory",
    transactionManagerRef = "keyTransactionManager", basePackages = {"eu.elixir.ega.ebi.keyproviderservice.domain.key.repository"})
public class EncryptionKeyConfig {

  @Bean(name = "keyDataSource")
  @ConfigurationProperties(prefix = "spring.secondDatasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "keyEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean keyEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("keyDataSource") DataSource dataSource) {
    return builder.dataSource(dataSource).packages("eu.elixir.ega.ebi.keyproviderservice.domain.key.entity").persistenceUnit("encryptionKey")
        .build();
  }

  @Bean(name = "keyTransactionManager")
  public PlatformTransactionManager keyTransactionManager(
      @Qualifier("keyEntityManagerFactory") EntityManagerFactory keyEntityManagerFactory) {
    return new JpaTransactionManager(keyEntityManagerFactory);
  }

}