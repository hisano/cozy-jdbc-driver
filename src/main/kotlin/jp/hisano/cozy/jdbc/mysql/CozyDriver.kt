package jp.hisano.cozy.jdbc.mysql

import java.net.URI
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.util.*
import java.util.logging.Logger

private const val DRIVER_MAJOR_VERSION = 1
private const val DRIVER_MINOR_VERSION = 0

private const val IS_TCK_PASSED = false
private const val HAS_MYSQL_SQL_92_COMPLIANCE = false

class CozyDriver : Driver {
    override fun connect(url: String?, info: Properties?): Connection? {
        if (url == null) {
            throw SQLException()
        }
        if (!acceptsURL(url) || info == null) {
            return null
        }

        val user = info["user"]?.toString() ?: return null
        val password = info["password"]?.toString() ?: return null

        val uri = URI("jdbc://${url.substringAfter("//")}")
        val host = uri.host ?: return null
        val port = if (uri.port != -1) uri.port else return null
        val database = uri.path?.substringAfter("/") ?: return null

        return CozyConnection(host, port, database, user, password)
    }

    override fun acceptsURL(url: String?): Boolean {
        return url?.startsWith("jdbc:cozy:mysql://") ?: false
    }

    override fun getMajorVersion(): Int = DRIVER_MAJOR_VERSION

    override fun getMinorVersion(): Int = DRIVER_MINOR_VERSION

    override fun jdbcCompliant(): Boolean = IS_TCK_PASSED && HAS_MYSQL_SQL_92_COMPLIANCE

    override fun getParentLogger(): Logger = throw SQLFeatureNotSupportedException()

    override fun getPropertyInfo(url: String?, info: Properties?): Array<DriverPropertyInfo> {
        TODO("Not yet implemented")
    }
}
