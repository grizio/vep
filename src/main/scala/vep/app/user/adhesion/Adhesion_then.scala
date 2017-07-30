import org.joda.time.DateTime

case class Adhesion(
  id: String,
  user: String,
  accepted: Boolean,
  members: Seq[AdhesionMember]
)

case class AdhesionMember(
  firstName: String,
  lastName: String,
  birthday: DateTime,
  activity: String
)