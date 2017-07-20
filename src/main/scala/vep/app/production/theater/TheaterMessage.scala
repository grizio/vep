package vep.app.production.theater

object TheaterMessage {
  def seatDefinedTwice(code: String) = s"Le siège ${code} est défini deux fois. Chaque siège doit être unique"
  def unknownTheater = "Le théâtre n'existe pas"
}
