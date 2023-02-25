package jp.hisano.cozy.jdbc.mysql

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
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
            assertEquals(100, resultSet.getByte(1))
            assertEquals(100, resultSet.getShort(1))
            assertEquals(100, resultSet.getInt(1))
            assertEquals(100, resultSet.getLong(1))
            assertEquals(100.0f, resultSet.getFloat(1))
            assertEquals(100.0, resultSet.getDouble(1))
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
        val resultSet = statement.executeQuery("SELECT name, age FROM person")
        while (resultSet.next()) {
            assertEquals("Tom", resultSet.getString(1))
            assertEquals("Tom", resultSet.getString("name"))

            assertEquals(30, resultSet.getInt(2))
            assertEquals(30, resultSet.getInt("age"))
        }
    }

    @Test
    fun testExecute() {
        mysqlConnection.execute("""
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
        """)

        val updateStatement = cozyConnection.createStatement()
        assertFalse(updateStatement.execute("UPDATE person SET age = 30 WHERE name = 'Tom'"))
        assertNull(updateStatement.resultSet)
        assertEquals(1, updateStatement.updateCount)

        val selectStatement = cozyConnection.createStatement()
        assertTrue(selectStatement.execute("SELECT age FROM person"))
        selectStatement.resultSet.run {
            assertNotNull(this)
            assertTrue(next())
            assertEquals(30, getInt("age"))
        }
        assertEquals(-1, selectStatement.updateCount)
    }

    @Test
    fun testAutoCommit() {
        val connection = cozyConnection

        connection.autoCommit = false
        assertFalse(connection.autoCommit)

        connection.autoCommit = true
        assertTrue(connection.autoCommit)
    }

    @Test
    fun testPreparedStatement() {
        mysqlConnection.execute("""
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
        """)

        val connection = cozyConnection

        val updateStatement = connection.prepareStatement("UPDATE person SET age = ? WHERE name = ?")
        updateStatement.setInt(1, 30)
        updateStatement.setString(2, "Tom")
        assertEquals(1, updateStatement.executeUpdate())

        val selectStatement = connection.prepareStatement("SELECT age FROM person WHERE name = ?")
        selectStatement.setString(1, "Tom")
        assertTrue(selectStatement.execute())
        selectStatement.resultSet.run {
            assertNotNull(this)
            assertTrue(next())
            assertEquals(30, getInt("age"))
        }
        assertEquals(-1, selectStatement.updateCount)

        val selectStatementForQuery = connection.prepareStatement("SELECT age FROM person WHERE name = ?")
        selectStatementForQuery.setString(1, "Tom")
        selectStatementForQuery.executeQuery().run {
            assertNotNull(this)
            assertTrue(next())
            assertEquals(30, getInt("age"))
        }
    }

    companion object {
        init {
            DriverManager.registerDriver(CozyDriver())
        }
    }
}

private fun Connection.execute(@Language("SQL") sql: String) = sql.trimMargin().lines().forEach { createStatement().executeUpdate(it) }
