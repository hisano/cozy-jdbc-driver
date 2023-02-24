package cozy.jdbc.mysql

import jp.hisano.cozy.jdbc.CozyDriver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager

@Testcontainers(disabledWithoutDocker = true)
class DriverTest {
    @Container
    val container = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:15.2-alpine"))

    @Test
    fun testBasic() {
        assertTrue(container.isRunning)
        
        DriverManager.registerDriver(CozyDriver())

        val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("select 100")
        while (resultSet.next()) {
            assertEquals("100", resultSet.getInt(1))
        }
    }
}
