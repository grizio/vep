package vep.framework.database

import scalikejdbc.{DB, DBSession}

trait DatabaseContainer {
  def withQueryConnection[A](block: DBSession => A): A = {
    DB.readOnly(block)
  }

  def withCommandTransaction[A](block: DBSession => A): A = {
    DB.localTx(block)
  }
}
