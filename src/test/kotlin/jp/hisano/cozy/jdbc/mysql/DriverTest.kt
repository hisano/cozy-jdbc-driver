package jp.hisano.cozy.jdbc.mysql

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.Connection
import java.sql.DriverManager

@Testcontainers(disabledWithoutDocker = true)
class DriverTest {
    @Container
    val container = MySQLContainer(DockerImageName.parse("mysql:8.0.32"))

    lateinit var mysqlConnection: Connection
    lateinit var cozyConnection: Connection

    @BeforeEach
    fun setUp() {
        mysqlConnection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
        cozyConnection = DriverManager.getConnection(container.jdbcUrl.replace(":mysql:", ":cozy:mysql:"), container.username, container.password)
    }

    @Test
    fun testSmallInt() {
        mysqlConnection.execute("""
            CREATE TABLE test (value smallint);
            INSERT INTO test VALUES (100);
        """)

        val statement = cozyConnection.createStatement()
        val resultSet = statement.executeQuery("SELECT value FROM test")
        while (resultSet.next()) {
            assertEquals(100, resultSet.getInt(1))
        }
    }

    @Test
    fun testUpdate() {
        mysqlConnection.execute("""
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
        """)

        val statement = cozyConnection.createStatement()
        val result = statement.executeUpdate("UPDATE person SET age = 30 WHERE name = 'Tom'")
        assertEquals(1, result)
        val resultSet = statement.executeQuery("SELECT age FROM person")
        while (resultSet.next()) {
            assertEquals(30, resultSet.getInt("age"))
        }
    }

    companion object {
        init {
            DriverManager.registerDriver(CozyDriver())
        }
    }
}

private fun Connection.execute(@Language("SQL") sql: String) = sql.trimMargin().lines().forEach { createStatement().executeUpdate(it) }
