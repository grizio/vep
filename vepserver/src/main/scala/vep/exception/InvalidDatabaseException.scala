package vep.exception

class InvalidDatabaseException(dbName: String) extends Exception("The database '" + dbName + "' does not exists.")
