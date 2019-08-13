package com.oos.oos.model

class User (var fname : String, var lname : String, var username : String, var password : String, var email : String){
	var appointments = ArrayList<Appointment>(10)
}
