package jp.hisano.cozy.jdbc.mysql

import java.net.URI
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger

class CozyDriver : Driver {
    override fun connect(url: String?, info: Properties?): Connection? {
        if (url == null || !acceptsURL(url)) {
            return null
        }
        requireNotNull(info)

        val user = info["user"]?.toString() ?: return null
        val password = info["password"]?.toString() ?: return null

        val uri = URI("jdbc://${url.substringAfter("//")}")
        return CozyConnection(uri.host, uri.port, uri.path.substringAfter("/"), user, password)
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

    override fun jdbcCompliant(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getParentLogger(): Logger {
        TODO("Not yet implemented")
    }
}

