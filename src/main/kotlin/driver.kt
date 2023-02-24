package jp.hisano.cozy.jdbc

import java.sql.Connection
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger
import java.sql.Driver

class CozyDriver: Driver {
    override fun connect(url: String?, info: Properties?): Connection {
        TODO("Not yet implemented")
    }

    override fun acceptsURL(url: String?): Boolean {
        TODO("Not yet implemented")
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
