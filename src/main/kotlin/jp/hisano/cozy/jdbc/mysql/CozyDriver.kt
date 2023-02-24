package jp.hisano.cozy.jdbc.mysql

import java.net.URI
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.util.*
import java.util.logging.Logger

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

    override fun getPropertyInfo(url: String?, info: Properties?): Array<DriverPropertyInfo> {
        TODO("Not yet implemented")
    }

    override fun getMajorVersion(): Int {
        TODO("Not yet implemented")
    }

    override fun getMinorVersion(): Int {
        TODO("Not yet implemented")
    }

    override fun jdbcCompliant(): Boolean = IS_TCK_PASSED && HAS_MYSQL_SQL_92_COMPLIANCE

    override fun getParentLogger(): Logger = throw SQLFeatureNotSupportedException()
}

private const val IS_TCK_PASSED = false
private const val HAS_MYSQL_SQL_92_COMPLIANCE = false
