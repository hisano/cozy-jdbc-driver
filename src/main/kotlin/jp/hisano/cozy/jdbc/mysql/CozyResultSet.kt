package jp.hisano.cozy.jdbc.mysql

import com.github.jasync.sql.db.QueryResult
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.Array
import java.sql.Blob
import java.sql.Clob
import java.sql.Date
import java.sql.NClob
import java.sql.Ref
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.RowId
import java.sql.SQLException
import java.sql.SQLWarning
import java.sql.SQLXML
import java.sql.Statement
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Suppress("LargeClass")
internal class CozyResultSet(val queryResult: QueryResult) : ResultSet {
    private var rowIndex = AtomicInteger()

    @Volatile
    private var wasNull = false

    private val isClosed = AtomicBoolean()

    override fun close() {
        if (!isClosed.compareAndSet(false, true)) {
            return
        }
    }

    override fun isClosed(): Boolean = isClosed.get()

    override fun next(): Boolean = rowIndex.update { it + 1 }

    override fun previous(): Boolean = rowIndex.update { it - 1 }

    override fun absolute(row: Int): Boolean = rowIndex.update { row }

    override fun relative(rows: Int): Boolean = rowIndex.update { it + rows }

    private fun AtomicInteger.update(updater: (Int) -> Int): Boolean {
        synchronized(this) {
            val newRowIndex = updater(get())
            set(newRowIndex)
            return newRowIndex in 1..queryResult.rows.size
        }
    }

    private operator fun get(columnIndex: Int): Any? {
        val row = queryResult.rows[rowIndex.get() - 1]

        if (columnIndex <= 0 || row.size < columnIndex) {
            throw SQLException()
        }

        val value = row[columnIndex - 1]
        wasNull = (value == null)
        return value
    }

    private operator fun get(columnLabel: String?): Any? {
        if (columnLabel == null || !queryResult.rows.columnNames().contains(columnLabel)) {
            throw SQLException()
        }

        val row = queryResult.rows[rowIndex.get() - 1]
        val value = row[columnLabel]
        wasNull = (value == null)
        return value
    }

    override fun wasNull(): Boolean = wasNull

    override fun getString(columnIndex: Int): String? = this[columnIndex]?.toString()

    override fun getString(columnLabel: String?): String? = this[columnLabel]?.toString()

    override fun getBoolean(columnIndex: Int): Boolean = toBoolean(this[columnIndex])

    override fun getBoolean(columnLabel: String?): Boolean = toBoolean(this[columnLabel])

    private fun toBoolean(value: Any?): Boolean {
        return when (value) {
            is Number -> value.toInt() != 0
            null -> false
            else -> throw SQLException()
        }
    }

    override fun getByte(columnIndex: Int): Byte = toByte(this[columnIndex])

    override fun getByte(columnLabel: String?): Byte = toByte(this[columnLabel])

    private fun toByte(value: Any?): Byte {
        return when (value) {
            is Number -> value.toByte()
            null -> 0
            else -> throw SQLException()
        }
    }

    override fun getShort(columnIndex: Int): Short = toShort(this[columnIndex])

    override fun getShort(columnLabel: String?): Short = toShort(this[columnLabel])

    private fun toShort(value: Any?): Short {
        return when (value) {
            is Number -> value.toShort()
            null -> 0
            else -> throw SQLException()
        }
    }

    override fun getInt(columnIndex: Int): Int = toInt(this[columnIndex])

    override fun getInt(columnLabel: String?): Int = toInt(this[columnLabel])

    private fun toInt(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt()
            null -> 0
            else -> throw SQLException()
        }
    }

    override fun getLong(columnIndex: Int): Long = toLong(this[columnIndex])

    override fun getLong(columnLabel: String?): Long = toLong(this[columnLabel])

    private fun toLong(value: Any?): Long {
        return when (value) {
            is Number -> value.toLong()
            null -> 0
            else -> throw SQLException()
        }
    }

    override fun getFloat(columnIndex: Int): Float = toFloat(this[columnIndex])

    override fun getFloat(columnLabel: String?): Float = toFloat(this[columnLabel])

    private fun toFloat(value: Any?): Float {
        return when (value) {
            is Number -> value.toFloat()
            null -> 0.0f
            else -> throw SQLException()
        }
    }

    override fun getDouble(columnIndex: Int): Double = toDouble(this[columnIndex])

    override fun getDouble(columnLabel: String?): Double = toDouble(this[columnLabel])

    private fun toDouble(value: Any?): Double {
        return when (value) {
            is Number -> value.toDouble()
            null -> 0.0
            else -> throw SQLException()
        }
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBigDecimal(columnIndex: Int, scale: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun getBigDecimal(columnLabel: String?, scale: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun getBigDecimal(columnIndex: Int): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun getBigDecimal(columnLabel: String?): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun getBytes(columnIndex: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getBytes(columnLabel: String?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getDate(columnIndex: Int): Date {
        TODO("Not yet implemented")
    }

    override fun getDate(columnLabel: String?): Date {
        TODO("Not yet implemented")
    }

    override fun getDate(columnIndex: Int, cal: Calendar?): Date {
        TODO("Not yet implemented")
    }

    override fun getDate(columnLabel: String?, cal: Calendar?): Date {
        TODO("Not yet implemented")
    }

    override fun getTime(columnIndex: Int): Time {
        TODO("Not yet implemented")
    }

    override fun getTime(columnLabel: String?): Time {
        TODO("Not yet implemented")
    }

    override fun getTime(columnIndex: Int, cal: Calendar?): Time {
        TODO("Not yet implemented")
    }

    override fun getTime(columnLabel: String?, cal: Calendar?): Time {
        TODO("Not yet implemented")
    }

    override fun getTimestamp(columnIndex: Int): Timestamp {
        TODO("Not yet implemented")
    }

    override fun getTimestamp(columnLabel: String?): Timestamp {
        TODO("Not yet implemented")
    }

    override fun getTimestamp(columnIndex: Int, cal: Calendar?): Timestamp {
        TODO("Not yet implemented")
    }

    override fun getTimestamp(columnLabel: String?, cal: Calendar?): Timestamp {
        TODO("Not yet implemented")
    }

    override fun getAsciiStream(columnIndex: Int): InputStream {
        TODO("Not yet implemented")
    }

    override fun getAsciiStream(columnLabel: String?): InputStream {
        TODO("Not yet implemented")
    }

    override fun getUnicodeStream(columnIndex: Int): InputStream {
        TODO("Not yet implemented")
    }

    override fun getUnicodeStream(columnLabel: String?): InputStream {
        TODO("Not yet implemented")
    }

    override fun getBinaryStream(columnIndex: Int): InputStream {
        TODO("Not yet implemented")
    }

    override fun getBinaryStream(columnLabel: String?): InputStream {
        TODO("Not yet implemented")
    }

    override fun getWarnings(): SQLWarning {
        TODO("Not yet implemented")
    }

    override fun clearWarnings() {
        TODO("Not yet implemented")
    }

    override fun getCursorName(): String {
        TODO("Not yet implemented")
    }

    override fun getMetaData(): ResultSetMetaData {
        TODO("Not yet implemented")
    }

    override fun getObject(columnIndex: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getObject(columnLabel: String?): Any {
        TODO("Not yet implemented")
    }

    override fun getObject(columnIndex: Int, map: MutableMap<String, Class<*>>?): Any {
        TODO("Not yet implemented")
    }

    override fun getObject(columnLabel: String?, map: MutableMap<String, Class<*>>?): Any {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> getObject(columnIndex: Int, type: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> getObject(columnLabel: String?, type: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun findColumn(columnLabel: String?): Int {
        TODO("Not yet implemented")
    }

    override fun getCharacterStream(columnIndex: Int): Reader {
        TODO("Not yet implemented")
    }

    override fun getCharacterStream(columnLabel: String?): Reader {
        TODO("Not yet implemented")
    }

    override fun isBeforeFirst(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAfterLast(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isFirst(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLast(): Boolean {
        TODO("Not yet implemented")
    }

    override fun beforeFirst() {
        TODO("Not yet implemented")
    }

    override fun afterLast() {
        TODO("Not yet implemented")
    }

    override fun first(): Boolean {
        TODO("Not yet implemented")
    }

    override fun last(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRow(): Int {
        TODO("Not yet implemented")
    }

    override fun setFetchDirection(direction: Int) {
        TODO("Not yet implemented")
    }

    override fun getFetchDirection(): Int {
        TODO("Not yet implemented")
    }

    override fun setFetchSize(rows: Int) {
        TODO("Not yet implemented")
    }

    override fun getFetchSize(): Int {
        TODO("Not yet implemented")
    }

    override fun getType(): Int {
        TODO("Not yet implemented")
    }

    override fun getConcurrency(): Int {
        TODO("Not yet implemented")
    }

    override fun rowUpdated(): Boolean {
        TODO("Not yet implemented")
    }

    override fun rowInserted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun rowDeleted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateNull(columnIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun updateNull(columnLabel: String?) {
        TODO("Not yet implemented")
    }

    override fun updateBoolean(columnIndex: Int, x: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateBoolean(columnLabel: String?, x: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateByte(columnIndex: Int, x: Byte) {
        TODO("Not yet implemented")
    }

    override fun updateByte(columnLabel: String?, x: Byte) {
        TODO("Not yet implemented")
    }

    override fun updateShort(columnIndex: Int, x: Short) {
        TODO("Not yet implemented")
    }

    override fun updateShort(columnLabel: String?, x: Short) {
        TODO("Not yet implemented")
    }

    override fun updateInt(columnIndex: Int, x: Int) {
        TODO("Not yet implemented")
    }

    override fun updateInt(columnLabel: String?, x: Int) {
        TODO("Not yet implemented")
    }

    override fun updateLong(columnIndex: Int, x: Long) {
        TODO("Not yet implemented")
    }

    override fun updateLong(columnLabel: String?, x: Long) {
        TODO("Not yet implemented")
    }

    override fun updateFloat(columnIndex: Int, x: Float) {
        TODO("Not yet implemented")
    }

    override fun updateFloat(columnLabel: String?, x: Float) {
        TODO("Not yet implemented")
    }

    override fun updateDouble(columnIndex: Int, x: Double) {
        TODO("Not yet implemented")
    }

    override fun updateDouble(columnLabel: String?, x: Double) {
        TODO("Not yet implemented")
    }

    override fun updateBigDecimal(columnIndex: Int, x: BigDecimal?) {
        TODO("Not yet implemented")
    }

    override fun updateBigDecimal(columnLabel: String?, x: BigDecimal?) {
        TODO("Not yet implemented")
    }

    override fun updateString(columnIndex: Int, x: String?) {
        TODO("Not yet implemented")
    }

    override fun updateString(columnLabel: String?, x: String?) {
        TODO("Not yet implemented")
    }

    override fun updateBytes(columnIndex: Int, x: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun updateBytes(columnLabel: String?, x: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun updateDate(columnIndex: Int, x: Date?) {
        TODO("Not yet implemented")
    }

    override fun updateDate(columnLabel: String?, x: Date?) {
        TODO("Not yet implemented")
    }

    override fun updateTime(columnIndex: Int, x: Time?) {
        TODO("Not yet implemented")
    }

    override fun updateTime(columnLabel: String?, x: Time?) {
        TODO("Not yet implemented")
    }

    override fun updateTimestamp(columnIndex: Int, x: Timestamp?) {
        TODO("Not yet implemented")
    }

    override fun updateTimestamp(columnLabel: String?, x: Timestamp?) {
        TODO("Not yet implemented")
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?) {
        TODO("Not yet implemented")
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun updateObject(columnIndex: Int, x: Any?, scaleOrLength: Int) {
        TODO("Not yet implemented")
    }

    override fun updateObject(columnIndex: Int, x: Any?) {
        TODO("Not yet implemented")
    }

    override fun updateObject(columnLabel: String?, x: Any?, scaleOrLength: Int) {
        TODO("Not yet implemented")
    }

    override fun updateObject(columnLabel: String?, x: Any?) {
        TODO("Not yet implemented")
    }

    override fun insertRow() {
        TODO("Not yet implemented")
    }

    override fun updateRow() {
        TODO("Not yet implemented")
    }

    override fun deleteRow() {
        TODO("Not yet implemented")
    }

    override fun refreshRow() {
        TODO("Not yet implemented")
    }

    override fun cancelRowUpdates() {
        TODO("Not yet implemented")
    }

    override fun moveToInsertRow() {
        TODO("Not yet implemented")
    }

    override fun moveToCurrentRow() {
        TODO("Not yet implemented")
    }

    override fun getStatement(): Statement {
        TODO("Not yet implemented")
    }

    override fun getRef(columnIndex: Int): Ref {
        TODO("Not yet implemented")
    }

    override fun getRef(columnLabel: String?): Ref {
        TODO("Not yet implemented")
    }

    override fun getBlob(columnIndex: Int): Blob {
        TODO("Not yet implemented")
    }

    override fun getBlob(columnLabel: String?): Blob {
        TODO("Not yet implemented")
    }

    override fun getClob(columnIndex: Int): Clob {
        TODO("Not yet implemented")
    }

    override fun getClob(columnLabel: String?): Clob {
        TODO("Not yet implemented")
    }

    override fun getArray(columnIndex: Int): Array {
        TODO("Not yet implemented")
    }

    override fun getArray(columnLabel: String?): Array {
        TODO("Not yet implemented")
    }

    override fun getURL(columnIndex: Int): URL {
        TODO("Not yet implemented")
    }

    override fun getURL(columnLabel: String?): URL {
        TODO("Not yet implemented")
    }

    override fun updateRef(columnIndex: Int, x: Ref?) {
        TODO("Not yet implemented")
    }

    override fun updateRef(columnLabel: String?, x: Ref?) {
        TODO("Not yet implemented")
    }

    override fun updateBlob(columnIndex: Int, x: Blob?) {
        TODO("Not yet implemented")
    }

    override fun updateBlob(columnLabel: String?, x: Blob?) {
        TODO("Not yet implemented")
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun updateClob(columnIndex: Int, x: Clob?) {
        TODO("Not yet implemented")
    }

    override fun updateClob(columnLabel: String?, x: Clob?) {
        TODO("Not yet implemented")
    }

    override fun updateClob(columnIndex: Int, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateClob(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateClob(columnIndex: Int, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun updateClob(columnLabel: String?, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun updateArray(columnIndex: Int, x: Array?) {
        TODO("Not yet implemented")
    }

    override fun updateArray(columnLabel: String?, x: Array?) {
        TODO("Not yet implemented")
    }

    override fun getRowId(columnIndex: Int): RowId {
        TODO("Not yet implemented")
    }

    override fun getRowId(columnLabel: String?): RowId {
        TODO("Not yet implemented")
    }

    override fun updateRowId(columnIndex: Int, x: RowId?) {
        TODO("Not yet implemented")
    }

    override fun updateRowId(columnLabel: String?, x: RowId?) {
        TODO("Not yet implemented")
    }

    override fun getHoldability(): Int {
        TODO("Not yet implemented")
    }

    override fun updateNString(columnIndex: Int, nString: String?) {
        TODO("Not yet implemented")
    }

    override fun updateNString(columnLabel: String?, nString: String?) {
        TODO("Not yet implemented")
    }

    override fun updateNClob(columnIndex: Int, nClob: NClob?) {
        TODO("Not yet implemented")
    }

    override fun updateNClob(columnLabel: String?, nClob: NClob?) {
        TODO("Not yet implemented")
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?) {
        TODO("Not yet implemented")
    }

    override fun getNClob(columnIndex: Int): NClob {
        TODO("Not yet implemented")
    }

    override fun getNClob(columnLabel: String?): NClob {
        TODO("Not yet implemented")
    }

    override fun getSQLXML(columnIndex: Int): SQLXML {
        TODO("Not yet implemented")
    }

    override fun getSQLXML(columnLabel: String?): SQLXML {
        TODO("Not yet implemented")
    }

    override fun updateSQLXML(columnIndex: Int, xmlObject: SQLXML?) {
        TODO("Not yet implemented")
    }

    override fun updateSQLXML(columnLabel: String?, xmlObject: SQLXML?) {
        TODO("Not yet implemented")
    }

    override fun getNString(columnIndex: Int): String {
        TODO("Not yet implemented")
    }

    override fun getNString(columnLabel: String?): String {
        TODO("Not yet implemented")
    }

    override fun getNCharacterStream(columnIndex: Int): Reader {
        TODO("Not yet implemented")
    }

    override fun getNCharacterStream(columnLabel: String?): Reader {
        TODO("Not yet implemented")
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        TODO("Not yet implemented")
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?) {
        TODO("Not yet implemented")
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?) {
        TODO("Not yet implemented")
    }
}
