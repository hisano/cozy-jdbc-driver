package jp.hisano.cozy.jdbc.mysql

import com.github.jasync.sql.db.ConcreteConnection
import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory
import java.sql.*
import java.sql.Connection.*
import java.sql.ResultSet.*
import java.util.*
import java.util.concurrent.Executor

internal class CozyConnection(host: String, port: Int, database: String, username: String, password: String) :
    Connection {
    val concreteConnection: ConcreteConnection

    private var holdability: Int = CLOSE_CURSORS_AT_COMMIT

    init {
        val connectionFactory = MySQLConnectionFactory(Configuration(username, host, port, password, database))
        concreteConnection = connectionFactory.create().get()
    }

    override fun isClosed(): Boolean = !concreteConnection.isConnected()

    override fun close() {
        concreteConnection.disconnect()
    }

    override fun createStatement(): Statement = createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)

    override fun createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement =
        createStatement(resultSetType, resultSetConcurrency, holdability)

    override fun createStatement(resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): Statement {
        throwSQLExceptionIfClosed()
        return CozyStatement(this, resultSetType, resultSetConcurrency, resultSetHoldability)
    }

    override fun prepareStatement(sql: String?): PreparedStatement {
        if (sql == null) {
            throw SQLException()
        }

        throwSQLExceptionIfClosed()

        return CozyPreparedStatement(this, sql)
    }

    private fun throwSQLExceptionIfClosed() {
        if (isClosed) {
            throw SQLException("This connection has been closed.")
        }
    }

    override fun getHoldability(): Int = holdability

    override fun setHoldability(newValue: Int) {
        holdability = newValue
    }

    override fun setAutoCommit(newValue: Boolean) {
        val newNumberValue = if (newValue) 1 else 0
        execute("SET autocommit = $newNumberValue")
    }

    override fun getAutoCommit(): Boolean {
        return getSystemVariable("@@autocommit")
    }

    override fun commit() {
        execute("COMMIT")
    }

    override fun rollback() {
        execute("ROLLBACK ")
    }

    private fun execute(sql: String) {
        createStatement().execute(sql)
    }

    override fun setTransactionIsolation(level: Int) {
        val sqlLevel = when (level) {
            TRANSACTION_READ_UNCOMMITTED -> "READ UNCOMMITTED"
            TRANSACTION_READ_COMMITTED -> "READ COMMITTED"
            TRANSACTION_REPEATABLE_READ -> "REPEATABLE READ"
            TRANSACTION_SERIALIZABLE -> "SERIALIZABLE"
            else -> throw SQLException()
        }
        execute("SET SESSION TRANSACTION ISOLATION LEVEL $sqlLevel")
    }

    override fun getTransactionIsolation(): Int {
        val systemVariableName = when (getMajorVersion()) {
            8 -> "@@transaction_isolation"
            5 -> "@@tx_isolation"
            else -> throw SQLException()
        }
        return when (getSystemVariable<String>(systemVariableName)) {
            "READ-UNCOMMITTED" -> TRANSACTION_READ_UNCOMMITTED
            "READ-COMMITTED" -> TRANSACTION_READ_COMMITTED
            "REPEATABLE-READ" -> TRANSACTION_REPEATABLE_READ
            "SERIALIZABLE" -> TRANSACTION_SERIALIZABLE
            else -> throw SQLException()
        }
    }

    private fun getMajorVersion(): Int {
        return getSystemVariable<String>("@@version").substringBefore(".").toInt()
    }

    private inline fun <reified T> getSystemVariable(name: String): T {
        return createStatement().use {
            it.executeQuery("SELECT $name").use {
                it.next()
                when (T::class) {
                    Boolean::class -> it.getBoolean(1)
                    Int::class -> it.getInt(1)
                    String::class -> it.getString(1)
                    else -> throw IllegalArgumentException()
                }
            } as T
        }
    }

    override fun setCatalog(catalog: String?) {
        if (catalog == null) {
            throw SQLException()
        }

        execute("USE $catalog")
    }

    override fun getCatalog(): String {
        return getSystemVariable("database()")
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        if (iface == null || !isWrapperFor(iface)) {
            throw SQLException()
        }
        return iface.cast(this)
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        if (iface == null) {
            throw SQLException()
        }
        return iface.isInstance(this)
    }

    override fun prepareStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(sql: String?, autoGeneratedKeys: Int): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(sql: String?, columnIndexes: IntArray?): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(sql: String?, columnNames: Array<out String>?): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareCall(sql: String?): CallableStatement {
        TODO("Not yet implemented")
    }

    override fun prepareCall(sql: String?, resultSetType: Int, resultSetConcurrency: Int): CallableStatement {
        TODO("Not yet implemented")
    }

    override fun prepareCall(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): CallableStatement {
        TODO("Not yet implemented")
    }

    override fun nativeSQL(sql: String?): String {
        TODO("Not yet implemented")
    }

    override fun setSavepoint(): Savepoint {
        TODO("Not yet implemented")
    }

    override fun setSavepoint(name: String?): Savepoint {
        TODO("Not yet implemented")
    }

    override fun releaseSavepoint(savepoint: Savepoint?) {
        TODO("Not yet implemented")
    }

    override fun rollback(savepoint: Savepoint?) {
        TODO("Not yet implemented")
    }

    override fun getMetaData(): DatabaseMetaData {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(readOnly: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getWarnings(): SQLWarning {
        TODO("Not yet implemented")
    }

    override fun clearWarnings() {
        TODO("Not yet implemented")
    }

    override fun getTypeMap(): MutableMap<String, Class<*>> {
        TODO("Not yet implemented")
    }

    override fun setTypeMap(map: MutableMap<String, Class<*>>?) {
        TODO("Not yet implemented")
    }

    override fun createClob(): Clob {
        TODO("Not yet implemented")
    }

    override fun createBlob(): Blob {
        TODO("Not yet implemented")
    }

    override fun createNClob(): NClob {
        TODO("Not yet implemented")
    }

    override fun createSQLXML(): SQLXML {
        TODO("Not yet implemented")
    }

    override fun isValid(timeout: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun setClientInfo(name: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun setClientInfo(properties: Properties?) {
        TODO("Not yet implemented")
    }

    override fun getClientInfo(name: String?): String {
        TODO("Not yet implemented")
    }

    override fun getClientInfo(): Properties {
        TODO("Not yet implemented")
    }

    override fun createArrayOf(typeName: String?, elements: Array<out Any>?): java.sql.Array {
        TODO("Not yet implemented")
    }

    override fun createStruct(typeName: String?, attributes: Array<out Any>?): Struct {
        TODO("Not yet implemented")
    }

    override fun setSchema(schema: String?) {
        TODO("Not yet implemented")
    }

    override fun getSchema(): String {
        TODO("Not yet implemented")
    }

    override fun abort(executor: Executor?) {
        TODO("Not yet implemented")
    }

    override fun setNetworkTimeout(executor: Executor?, milliseconds: Int) {
        TODO("Not yet implemented")
    }

    override fun getNetworkTimeout(): Int {
        TODO("Not yet implemented")
    }
}
