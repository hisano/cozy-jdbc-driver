package jp.hisano.cozy.jdbc.mysql

import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Array
import java.sql.Date
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

internal class CozyPreparedStatement(connection: CozyConnection, val sql: String) : CozyStatement(connection), PreparedStatement {
    private val parameters: MutableList<Any?>

    init {
        parameters = CopyOnWriteArrayList(Array<Any?>(sql.count { it == '?' }, { null }))
    }

    override fun setInt(parameterIndex: Int, x: Int) {
        setParameter(parameterIndex, x)
    }

    override fun setLong(parameterIndex: Int, x: Long) {
        TODO("Not yet implemented")
    }

    override fun setFloat(parameterIndex: Int, x: Float) {
        TODO("Not yet implemented")
    }

    override fun setDouble(parameterIndex: Int, x: Double) {
        TODO("Not yet implemented")
    }

    override fun setBigDecimal(parameterIndex: Int, x: BigDecimal?) {
        TODO("Not yet implemented")
    }

    private fun setParameter(index: Int, value: Any?) {
        parameters[index - 1] = value
    }

    override fun setString(parameterIndex: Int, x: String?) {
        setParameter(parameterIndex, x)
    }

    override fun setBytes(parameterIndex: Int, x: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun setDate(parameterIndex: Int, x: Date?) {
        TODO("Not yet implemented")
    }

    override fun setDate(parameterIndex: Int, x: Date?, cal: Calendar?) {
        TODO("Not yet implemented")
    }

    override fun setTime(parameterIndex: Int, x: Time?) {
        TODO("Not yet implemented")
    }

    override fun setTime(parameterIndex: Int, x: Time?, cal: Calendar?) {
        TODO("Not yet implemented")
    }

    override fun setTimestamp(parameterIndex: Int, x: Timestamp?) {
        TODO("Not yet implemented")
    }

    override fun setTimestamp(parameterIndex: Int, x: Timestamp?, cal: Calendar?) {
        TODO("Not yet implemented")
    }

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun setUnicodeStream(parameterIndex: Int, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun clearParameters() {
        TODO("Not yet implemented")
    }

    override fun setObject(parameterIndex: Int, x: Any?, targetSqlType: Int) {
        TODO("Not yet implemented")
    }

    override fun setObject(parameterIndex: Int, x: Any?) {
        TODO("Not yet implemented")
    }

    override fun setObject(parameterIndex: Int, x: Any?, targetSqlType: Int, scaleOrLength: Int) {
        TODO("Not yet implemented")
    }

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun setRef(parameterIndex: Int, x: Ref?) {
        TODO("Not yet implemented")
    }

    override fun setBlob(parameterIndex: Int, x: Blob?) {
        TODO("Not yet implemented")
    }

    override fun setBlob(parameterIndex: Int, inputStream: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setBlob(parameterIndex: Int, inputStream: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun setClob(parameterIndex: Int, x: Clob?) {
        TODO("Not yet implemented")
    }

    override fun setClob(parameterIndex: Int, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setClob(parameterIndex: Int, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun setArray(parameterIndex: Int, x: Array?) {
        TODO("Not yet implemented")
    }

    override fun getMetaData(): ResultSetMetaData {
        TODO("Not yet implemented")
    }

    override fun setURL(parameterIndex: Int, x: URL?) {
        TODO("Not yet implemented")
    }

    override fun getParameterMetaData(): ParameterMetaData {
        TODO("Not yet implemented")
    }

    override fun setRowId(parameterIndex: Int, x: RowId?) {
        TODO("Not yet implemented")
    }

    override fun setNString(parameterIndex: Int, value: String?) {
        TODO("Not yet implemented")
    }

    override fun setNCharacterStream(parameterIndex: Int, value: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setNCharacterStream(parameterIndex: Int, value: Reader?) {
        TODO("Not yet implemented")
    }

    override fun setNClob(parameterIndex: Int, value: NClob?) {
        TODO("Not yet implemented")
    }

    override fun setNClob(parameterIndex: Int, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun setNClob(parameterIndex: Int, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun setSQLXML(parameterIndex: Int, xmlObject: SQLXML?) {
        TODO("Not yet implemented")
    }

    override fun executeQuery(): ResultSet {
        TODO("Not yet implemented")
    }

    override fun executeUpdate(): Int {
        executeSql(sql, parameters)
        return updateCount
    }

    override fun execute(): Boolean {
        TODO("Not yet implemented")
    }

    override fun addBatch() {
        TODO("Not yet implemented")
    }

    override fun setNull(parameterIndex: Int, sqlType: Int) {
        TODO("Not yet implemented")
    }

    override fun setNull(parameterIndex: Int, sqlType: Int, typeName: String?) {
        TODO("Not yet implemented")
    }

    override fun setBoolean(parameterIndex: Int, x: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setByte(parameterIndex: Int, x: Byte) {
        TODO("Not yet implemented")
    }

    override fun setShort(parameterIndex: Int, x: Short) {
        TODO("Not yet implemented")
    }
}