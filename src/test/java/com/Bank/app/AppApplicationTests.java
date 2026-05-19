package com.Bank.app;

import com.Bank.app.security.JwtAuthenticationEntryPoint;
import com.Bank.app.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
})
class AppApplicationTests {

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Test
	void contextLoads() {
	}

}
