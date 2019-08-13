package com.oos.oos

import java.sql.*
import java.util.Properties

class listDBs {
	internal var conn: Connection? = null
	internal var username = "#USERHERE" // provide the username
	internal var password = "#PASSHERE" // provide the corresponding password

	fun start(){
		println("starting...")
		 // make a connection to MySQL Server
        getConnection()
        // execute the query via connection object
        executeMySQLQuery()
		println("...done")
	}
	
    fun executeMySQLQuery() {
        var stmt: Statement? = null
        var resultset: ResultSet? = null
        try {
			stmt = conn!!.createStatement()
            if (stmt.execute("SHOW DATABASES;")) {
                resultset = stmt.resultSet
            }
			var index = 1
            while (resultset!!.next()) {
                println(resultset.getString(index))
            }
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            // release resources
            if (resultset != null) {
                try {
                    resultset.close()
                } catch (sqlEx: SQLException) {
                }
                resultset = null
            }
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
                stmt = null
            }
            if (conn != null) {
                try {
                    conn!!.close()
                } catch (sqlEx: SQLException) {
                }
                conn = null
            }
        }
    }
    /**
     * This method makes a connection to MySQL Server
     * In this example, MySQL Server is running in the local host (so 127.0.0.1)
     * at the standard port 3306
     */
    fun getConnection() {
        val connectionProps = Properties()
        connectionProps.put("user", username)
        connectionProps.put("password", password)
        try {
            conn = DriverManager.getConnection(
                    "jdbc:" + "mysql" + "://" +
                            "localhost" +
                            ":" + "3306" + "/" +
                            "",
                    connectionProps)
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } catch (ex: Exception) {
            // handle any errors
            ex.printStackTrace()
        }
    }
}				
