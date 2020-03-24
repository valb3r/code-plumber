package com.valb3r.codeplumber.restapi.workqueue.config

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.ogm.config.UsernamePasswordCredentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class Neo4jGraphqlConfig(
		private val configuration: org.neo4j.ogm.config.Configuration
) {

	@Bean(destroyMethod = "close")
	open fun driver(): Driver {
		val credentials = configuration.credentials as UsernamePasswordCredentials
		return GraphDatabase.driver(
				configuration.uri,
				AuthTokens.basic(credentials.username, credentials.password)
		)
	}
}