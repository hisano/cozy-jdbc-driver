package jp.hisano.cozy.jdbc.mysql

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager

@Testcontainers(disabledWithoutDocker = true)
class DriverTest {
    @Container
    val container = MySQLContainer(DockerImageName.parse("mysql:8.0.32"))

    companion object {
        init {
            DriverManager.registerDriver(CozyDriver())
        }
    }

    @Test
    fun testNumber() {
        assertTrue(container.isRunning)

        val uri = container.jdbcUrl.replace(":mysql:", ":cozy:mysql:")
//        val uri = container.jdbcUrl

        val connection = DriverManager.getConnection(uri, container.username, container.password)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("select 100")
        while (resultSet.next()) {
            assertEquals(100, resultSet.getInt(1))
        }
    }
}
