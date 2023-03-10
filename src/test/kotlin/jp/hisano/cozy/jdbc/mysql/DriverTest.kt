package jp.hisano.cozy.jdbc.mysql

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.Connection
import java.sql.Connection.TRANSACTION_READ_COMMITTED
import java.sql.Connection.TRANSACTION_READ_UNCOMMITTED
import java.sql.Connection.TRANSACTION_REPEATABLE_READ
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.DriverManager
import java.sql.ResultSet.CONCUR_READ_ONLY
import java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE

@Testcontainers(disabledWithoutDocker = true)
class DriverTest {
    @Container
    val container = MySQLContainer(DockerImageName.parse("mysql:" + getTargetVersion()))

    lateinit var mysqlConnection: Connection
    lateinit var cozyConnection: Connection

    @BeforeEach
    fun setUp() {
        mysqlConnection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
        cozyConnection = DriverManager.getConnection(container.jdbcUrl.replace(":mysql:", ":cozy:mysql:"), container.username, container.password)
    }

    @Test
    fun testSmallInt() {
        mysqlConnection.execute(
            """
            CREATE TABLE test (value smallint);
            INSERT INTO test VALUES (100);
            """
        )

        val connection = cozyConnection

        executeCheckFor("Statement#executeQuery", "ResultSet#get*") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT value FROM test")

            assertTrue(resultSet.next())

            assertEquals(100, resultSet.getByte(1))
            assertEquals(100, resultSet.getShort(1))
            assertEquals(100, resultSet.getInt(1))
            assertEquals(100, resultSet.getLong(1))
            assertEquals(100.0f, resultSet.getFloat(1))
            assertEquals(100.0, resultSet.getDouble(1))

            assertFalse(resultSet.next())
        }
    }

    @Test
    fun testStatement() {
        mysqlConnection.execute(
            """
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
            """
        )

        val connection = cozyConnection

        executeCheckFor("Statement#executeUpdate") {
            val statement = connection.createStatement()
            val result = statement.executeUpdate("UPDATE person SET age = 30 WHERE name = 'Tom'")
            assertEquals(1, result)
        }

        executeCheckFor("Statement#executeQuery") {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT name, age FROM person")

            assertTrue(resultSet.next())

            assertEquals("Tom", resultSet.getString(1))
            assertEquals("Tom", resultSet.getString("name"))

            assertEquals(30, resultSet.getInt(2))
            assertEquals(30, resultSet.getInt("age"))

            assertFalse(resultSet.next())
        }
    }

    @Test
    fun testExecute() {
        mysqlConnection.execute(
            """
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
            """
        )

        val connection = cozyConnection

        executeCheckFor("Statement#execute", "Statement#updateCount") {
            val update = connection.createStatement()
            assertFalse(update.execute("UPDATE person SET age = 30 WHERE name = 'Tom'"))
            assertNull(update.resultSet)
            assertEquals(1, update.updateCount)
        }

        executeCheckFor("Statement#execute", "Statement#resultSet") {
            val select = connection.createStatement()
            assertTrue(select.execute("SELECT age FROM person"))
            select.resultSet.run {
                assertNotNull(this)
                assertTrue(next())
                assertEquals(30, getInt("age"))
            }
            assertEquals(-1, select.updateCount)
        }
    }

    @Test
    fun testExecuteWithScroll() {
        mysqlConnection.execute(
            """
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
            INSERT INTO person VALUES ('Ken', 25);
            """
        )

        val connection = cozyConnection

        executeCheckFor("Statement#execute", "Statement#resultSet") {
            val select = connection.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY)
            assertTrue(select.execute("SELECT age FROM person"))
            select.resultSet.run {
                assertNotNull(this)

                assertTrue(next())
                assertTrue(next())
                assertFalse(next())

                assertTrue(previous())
                assertTrue(previous())
                assertFalse(previous())

                assertFalse(absolute(0))
                assertTrue(absolute(1))
                assertEquals(25, getInt("age"))

                assertTrue(absolute(1))
                assertTrue(relative(1))
                assertEquals(25, getInt("age"))

                assertFalse(relative(1))
            }
        }
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
        mysqlConnection.execute(
            """
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
            """
        )

        val connection = cozyConnection

        executeCheckFor("PreparedStatement#executeUpdate") {
            val update = connection.prepareStatement("UPDATE person SET age = ? WHERE name = ?")
            update.setInt(1, 30)
            update.setString(2, "Tom")
            assertEquals(1, update.executeUpdate())
        }

        executeCheckFor("PreparedStatement#execute") {
            val select = connection.prepareStatement("SELECT age FROM person WHERE name = ?")
            select.setString(1, "Tom")
            assertTrue(select.execute())
            select.resultSet.run {
                assertNotNull(this)
                assertTrue(next())
                assertEquals(30, getInt("age"))
            }
            assertEquals(-1, select.updateCount)
        }

        executeCheckFor("PreparedStatement#executeQuery") {
            val select = connection.prepareStatement("SELECT age FROM person WHERE name = ?")
            select.setString(1, "Tom")
            select.executeQuery().run {
                assertNotNull(this)
                assertTrue(next())
                assertEquals(30, getInt("age"))
            }
        }
    }

    @Test
    fun testTransaction() {
        mysqlConnection.execute(
            """
            CREATE TABLE person (name VARCHAR(10), age SMALLINT);
            INSERT INTO person VALUES ('Tom', 25);
            """
        )

        val connection = cozyConnection

        connection.autoCommit = false

        executeCheckFor("Connection#rollback") {
            val update = connection.prepareStatement("UPDATE person SET age = ? WHERE name = ?")
            update.setInt(1, 30)
            update.setString(2, "Tom")
            assertEquals(1, update.executeUpdate())

            connection.rollback()

            assertAge(connection, "Tom", 25)
        }

        executeCheckFor("Connection#commit") {
            val update = connection.prepareStatement("UPDATE person SET age = ? WHERE name = ?")
            update.setInt(1, 30)
            update.setString(2, "Tom")
            assertEquals(1, update.executeUpdate())

            connection.commit()

            assertAge(connection, "Tom", 30)
        }
    }

    @Test
    fun testTransactionIsolation() {
        val connection = cozyConnection

        executeCheckFor("Connection#transactionIsolation") {
            listOf(TRANSACTION_READ_UNCOMMITTED, TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE).forEach {
                connection.transactionIsolation = it
                assertEquals(it, connection.transactionIsolation)
            }
        }
    }

    @Test
    fun testCatalog() {
        val connection = cozyConnection

        executeCheckFor("Connection#catalog") {
            assertEquals(connection.catalog, "test")

            connection.catalog = "test"
            assertEquals(connection.catalog, "test")
        }
    }

    @Test
    fun testWrapper() {
        val connection = cozyConnection

        executeCheckFor("Connection#isWrapperFor", "Connection#unwrap") { 
            assertTrue(connection.isWrapperFor(Connection::class.java))
            assertNotNull(connection.unwrap(Connection::class.java))

            assertFalse(connection.isWrapperFor(RuntimeException::class.java))
        }
    }

    @Test
    fun testSchema() {
        val connection = cozyConnection

        if (connection !is CozyConnection) {
            // MySQL Connector/J switches processing with databaseTerm property: https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-connp-props-connection.html
            return
        }

        executeCheckFor("Connection#schema") {
            assertEquals(connection.schema, "test")

            connection.schema = "test"
            assertEquals(connection.schema, "test")
        }
    }

    private fun assertAge(connection: Connection, name: String, age: Int) {
        val selectStatement = connection.createStatement()
        assertTrue(selectStatement.execute("SELECT age FROM person WHERE name = '$name'"))
        selectStatement.resultSet.run {
            assertNotNull(this)
            assertTrue(next())
            assertEquals(age, getInt("age"))
        }
        assertEquals(-1, selectStatement.updateCount)
    }

    companion object {
        init {
            DriverManager.registerDriver(CozyDriver())
        }
    }
}

// MySQL Docker image versions: https://hub.docker.com/_/mysql/tags
private val LATEST_MAJOR_VERSIONS = listOf("8.0.32", "5.7.41", "5.6.51", "5.5.62")

private fun getTargetVersion(): String {
    val targetVersion = System.getProperty("TARGET_VERSION")
    if (targetVersion.isNullOrEmpty()) {
        return LATEST_MAJOR_VERSIONS[0]
    }
    return targetVersion
}

private fun Connection.execute(@Language("SQL") sql: String) = sql.trimMargin().lines().forEach { createStatement().executeUpdate(it) }

@Suppress("UnusedPrivateMember")
private inline fun executeCheckFor(methodName: String = "", task: () -> Unit) = task()

@Suppress("UnusedPrivateMember")
private inline fun executeCheckFor(firstMethodName: String = "", secondMethodName: String = "", task: () -> Unit) = task()
