package models

class UserEntity(val email:String, val id: Int, val roleId: Int){
  def this(email: String, id: Int) = this(email, id, 0)
  def isAdmin = roleId == 1
}
