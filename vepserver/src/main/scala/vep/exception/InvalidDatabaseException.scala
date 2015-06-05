package vep.exception

/**
 * This class defines an exception when a database does not exist.
 * @param dbName The database name which does not exist
 */
class InvalidDatabaseException(dbName: String) extends Exception("The database '" + dbName + "' does not exists.")
