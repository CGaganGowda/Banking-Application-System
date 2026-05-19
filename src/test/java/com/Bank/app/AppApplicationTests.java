package com.Bank.app;

import com.Bank.app.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = {
				"spring.autoconfigure.exclude=" +
						"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
						"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
						"org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration," +
						"org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
						"org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration"
		}
)
@Import(TestSecurityConfig.class)
class AppApplicationTests {

	@Test
	void contextLoads() {
	}
}