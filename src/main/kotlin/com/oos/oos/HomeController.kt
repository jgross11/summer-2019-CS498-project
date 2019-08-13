package com.oos.oos

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.*
import java.util.Properties
import com.oos.oos.model.User
import com.oos.oos.model.Appointment
import java.util.Date

/**
 * main controller
 */
@RestController
class HomeController {
	private var conn: Connection? = null
	private var username = "USERHERE" // provide the username
	private var password = "PASSHERE" // provide the corresponding password
	
	init{
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
			println("\n\n#### DATABASE CONNECTION SUCCESSFUL ####\n\n")
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } catch (ex: Exception) {
            // handle any errors
            ex.printStackTrace()
        }
		executeSQLQuery(conn!!.prepareStatement("""
		use oosDB
		"""), conn)
		println("\n\n#### DATABASE SELECTION SUCCESSFUL ####\n\n")
	}
	
	@RequestMapping("/login")
	fun login() = """
		<html>
		<!-- VUE import -->
	    <script src="https://unpkg.com/vue"></script>
	
	    <!-- CSS import -->
	<link rel="stylesheet" Type="text/css" href="css/ossLogin.css">
	
	<link rel="stylesheet" Type="text/css" href="css/ossTheme.css">
	    
	<title> 
	    OSS: Log In
	</title>
	
	<body>
	    <div id="app">
	<div id="logInBox">
	Online Schedule Service
	<input v-model="user" placeholder="Username">
	<input type="password" v-model="pass" placeholder="Password"><br>
	<input id="logInButton" type="button" value="Log in" @click="print">
	<input id="newUserButton" type="button" value="New User" @click="print" ><br>
	<div style = "font-size: 70%" v-if="user != ''">
	            <br> Username: {{user}}</div>
	        </div>
	    </div>
	</body>
	    
	<script>
	    const app = new Vue({
	      el: '#app',
	  data: {
	    title: "OSS: Home",
	user: "",
	pass: "",
	  },
	  methods:{
	      print(){
	          console.log("Hello, world!");
	          }
	      }
	    })
	</script>
	
	</html>
	"""
	
	@RequestMapping("/list")
	fun getList() = listDBs().start()
	
    @RequestMapping("/home")
    fun home() = fetchData(conn)
}

fun fetchData (conn : Connection?) : String{
	
	var user = loadUserInfo("jgross11", conn)
	println(user.toString())
	
	return """
		<html>
		    <!-- VUE import -->
		    <script src="https://unpkg.com/vue"></script>
		
		<!-- CSS import -->
		<link rel="stylesheet" Type="text/css" href="css/ossHome.css">
		
		<link rel="stylesheet" Type="text/css" href="css/ossTheme.css">
		    
		    
		<title> 
		    OSS: Home
		</title>
		
		<body>
		    <div id="app">
		
		<!-- CALENDAR CONTAINER-->
		<!-- CALENDAR CONTAINER-->
		
		<div id="calendar">
		<div id="calendar-header">
		<input type="button" value="Prev" @click="changeMonth(-1)">
		{{getMonthName(displayDate)}}, {{displayDate.getFullYear()}}
		<input type="button" value="Next" @click="changeMonth(1)">
		</div>
		<div id="calendar-info">
		<table id="calendar-info">
		<tr v-for="rowCounter in 6">
		    <td v-for="displayDateCounter in 7">
		        <div style='height: 100%' v-if="7*(rowCounter-1) + displayDateCounter - startDay > 0 && 7*(rowCounter-1) + displayDateCounter - startDay <= endDay">
		            
		            <!--
		                valid when: (current year < display year) || (current year == display year && current month < display month) || (current year == display year && current month == display month && current day < display day)  
		            -->
		            
		            <!-- TODO STRETCH CLICK AREA TO SELECT DATE ONLY FOR VALID DATES -->
		            
		            <div id="validCell" v-if="(todaysDate.getFullYear() < displayDate.getFullYear()) || (todaysDate.getFullYear() == displayDate.getFullYear() && todaysDate.getMonth() < displayDate.getMonth()) || (todaysDate.getFullYear() == displayDate.getFullYear() && todaysDate.getMonth() == displayDate.getMonth() && todaysDate.getDate() <= 7*(rowCounter-1) + displayDateCounter - startDay)" @click="setSelectedDate(7*(rowCounter-1) + displayDateCounter - startDay)">
		                {{calendar[7*(rowCounter-1) + displayDateCounter - startDay]}}
		            </div>
		            <div id="invalidCell" v-else style="color: gray">
		                            {{calendar[7*(rowCounter-1) + displayDateCounter - startDay]}}
		                        </div>
		                    </div>
		                </td>
		            </tr>
		        </table>
		    </div>
		</div>
		
		<!-- INFO CONTAINER-->
		<!-- INFO CONTAINER-->
		
		<div id="infoBox">
		<div id="infoHeader">
		Hello, {{user.fname + " " + user.lname.substring(0, 1) + "."}}
		<br>
		<input style="font-size:11px; width: 35%" type="button" value="Edit Information">
		<input style="font-size:11px; width: 35%" type="button" value="Sign Out">
		<hr>
		{{getMonthName(selectedDay) + " " + selectedDay.getDate() + getPostfix(selectedDay.getDate()) + ", " + selectedDay.getFullYear()}}
		</div>
		<br>
		Appointments
		<div id="appointmentInfo">
		<div v-for="appointment in appointments">
		{{appointment.first + " " + appointment.last.substring(0, 1) + ". - " + getTime(appointment.time)}}
		    </div>
		</div>
		
		Openings
		<div id="availableAppointments">
		<div id = "appointmentSlot" v-for="time in openTimes" @click="selectedTime = time">
		<div id="selectedTime" v-if="selectedTime == time">
		            &nbsp; - {{getTime(time)}}
		        </div>
		        <div v-else>
		            &nbsp; - {{getTime(time)}}
		        </div>
		    </div>
		</div>
		<div v-if="selectedTime != 0">
		Schedule an appointment for: {{getTime(selectedTime)}}
		<br>
		<input @click="scheduleAppointment(selectedDay, selectedTime)" type="button" style="width: 50%"value="Schedule">
		</div>
		<br>
		<div v-if="user.personalAppointments[0] != null"id="personalAppointments">
		Your next appointment is on {{getMonthName(user.personalAppointments[0].date) + " " + user.personalAppointments[0].date.getDate() + getPostfix(user.personalAppointments[0].date + ", " + user.personalAppointments[0].date.getFullYear())}} at {{getTime(user.personalAppointments[0].time)}}.
		            </div>
		            <div v-else> You have no scheduled appointments. </div>
		        </div>
		    </div>
		</body>
		    
		<script>
		    
		    function Appointment(first, last, time, date){
		        this.first = first;
		        this.last = last;
		        this.time = time;
		        console.log("Passed date: " + date);
		this.date = (date != null) ? date : new Date();
		console.log("Set date: " + this.date);
		}
		const app = new Vue({
		  el: '#app',
		  data: {
		    title: "OSS: Home",
		user: {
		    user: "jgross11",
		password: "jgross11",
		fname: "Josh",
		lname: "Gross",
		personalAppointments: [
		    new Appointment("Josh", "Gross", 1130)
		    ],
		},
		displayDate: new Date(),
		todaysDate: new Date(),
		selectedDay: new Date(),
		selectedTime: 0,
		appointments: [
		    new Appointment("Josh", "Gross", 1130),
		new Appointment("Josh", "Ross", 1145),
		new Appointment("Josh", "Oss", 1200),
		new Appointment("Josh", "Ss", 1215),
		new Appointment("Josh", "S", 1230)
		    ],
		    openingTime: 800,
		    closingTime: 1700
		  },
		  methods:{
		      print(){
		          console.log("Hello, world!");
		  },
		  changeMonth(value){
		    this.displayDate = (new Date(this.displayDate.getFullYear(), this.displayDate.getMonth() + value, 1));
		  },
		  getActualYear(d){
		      return 1900 + d.getYear();
		  },
		  getMonthName(d){
		      months = [
		          "January",
		  "February",
		  "March",
		  "April",
		  "May",
		  "June",
		  "July",
		  "August",
		  "September",
		  "October",
		  "November",
		  "December",
		      ];
		      return months[d.getMonth()];
		  },
		  
		  getDayName(d){
		    days = [
		      "Sunday",
		  "Monday",
		  "Tuesday",
		  "Wednesday",
		  "Thursday",
		  "Friday",
		  "Saturday"
		    ];
		    return days[d.getDay()];
		  },
		  
		  getPostfix(n){
		    switch(n){
		        case 1:
		            return "st";
		case 2:
		    return "nd";
		case 3: 
		    return "rd";
		default: 
		    return "th";
		    }
		  },
		  
		  getTime(time){
		      // hour
		  var h = (time > 1259) ? Math.floor((time-1200)/100) : Math.floor(time/100);
		  
		  // logic fixes times such as 3:65, 4:05, 4:50
		  var tmo = time % 100;
		  time = (tmo > 60) ? (time + 140) : time;
		  var m = (tmo < 10) ? ("0" + time % 100) : ((tmo == 0) ? tmo + "0" : tmo);
		  
		  // period (yes, it is actually called period) determination
		  var t = (time > 1159) ? "p" : "a";
		  
		  return(h+":"+m+t);
		      
		  },
		  
		  setSelectedDate(index){
		      this.selectedDay = new Date(this.displayDate.getFullYear(), this.displayDate.getMonth(), index);
		  },
		  
		  scheduleAppointment(day, time){
		      // error message 
		  logicMessage = {
		      message: "",
		      valid: false
		  };
		  
		  appointmentList = []; // will become backend function call
		  // RETRIEVE LIST OF THAT DAY'S APPOINTMENTS
		  for(i = 0; i < appointmentList.size; i++){
		      app = appointmentList[i];
		      if(app.time == time){
		          logicMessage.message = "An appointment is already scheduled for that time.";
		          logicMessage.valid = false;
		          return logicMessage;
		      }
		  }
		  logicMessage.message = "An appointment has been created."
		  logicMessage.valid = true;
		  
		  // call to create appointment in database
		      newApt = new Appointment(this.user.fname, this.user.lname, time, this.selectedDay);
		      this.appointments.push(newApt);
		      this.sortArr(this.appointments);
		      this.user.personalAppointments.push(newApt);
		      this.sortArr(this.user.personalAppointments);
		      return logicMessage;
		  },
		  
		  // bubble sort since small n and deadline
		      sortArr(arr){
		          for(j = 0; j < arr.length; j++){
		              for(i = 0; i < arr.length; i++){
		                  if(arr[j].time < arr[i].time){
		                     tmp = arr[i];
		                     arr[i] = arr[j];
		                     arr[j] = tmp;
		                  }
		              }
		          }
		      }
		  }, 
		  computed:{
		      calendar: function(){
		          // TODO: this is where calendar information loading will go
		      var cal = [42];
		      for(i = 0; i < 42; i++){
		          cal[i] = i;
		      }
		      return cal;
		  },
		  
		 getDate: function(newDate){
		      if(typeof(newDate) === Date){
		
		      }
		      return new Date();
		  },
		  
		  // returns current year: XXXX
		  year: function(){
		      return 1900+this.displayDate.getYear();
		  },
		  
		  // ##### TODO: fix startDay and endDay to allow proper shifting on calendar mapping #####
		  
		  // returns int 0:6 representing first day of the month's value
		  startDay: function(){ 
		      return (new Date(this.displayDate.getFullYear(), this.displayDate.getMonth(), 1).getDay());
		  },
		
		  // returns displayDate value of last day in month
		  endDay: function(){
		    return new Date(this.displayDate.getYear(), this.displayDate.getMonth() + 1, 0).getDate();
		  },
		  
		  // assumes customer times are sorted - TODO on backend / this.appointments instantiation
		          openTimes: function(){
		            times = [];
		            index = 0;
		            for(time = this.openingTime; time < this.closingTime-45; time += 15){
		                if(time % 100 >= 60){
		                   time += 40;
		                }
		                if(index == this.appointments.length || time != this.appointments[index].time){
		                    times.push(time);
		                }
		                else{
		                    index++;
		                }
		            }
		            return times;
		          }
		      }
		    })
		</script>
		
		</html>
 """
	}

	fun executeSQLQuery(statement: PreparedStatement, conn : Connection?) : ResultSet?{
        var resultset: ResultSet? = null
        try {
        	resultset = statement.executeQuery()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
		return resultset;
	}

	fun loadUserInfo(username : String, conn : Connection?) : User{
		var loadUserStatement = conn!!.prepareStatement("""
		select * from Users where Users.username = ?
		""")
		loadUserStatement.setString(1, username)
		var results = executeSQLQuery(loadUserStatement, conn)
		var index = 1
		while(results!!.next()){
			println("### USER FOUND ###")
			// skip id
			index++
			// fname
			var fname = results.getString(index++)
			// lname
			var lname = results.getString(index++)
			// uname
			var uname = results.getString(index++)
			// password
			var password = results.getString(index++)
			// email
			var email = results.getString(index++)
			var user = User(fname, lname, uname, password, email)
			println(user.fname + " " + user.lname)
			user.appointments = getAppointments(uname, conn)			
			return user
		}
		return User("", "", "", "", "")
	}

	fun getAppointments(username : String, conn : Connection?) : ArrayList<Appointment>{
		var getAppointmentsStatement = conn!!.prepareStatement("""
		select * from Appointments where Appointments.username = ?;
		""")		
		getAppointmentsStatement.setString(1, username)	
		var results = executeSQLQuery(getAppointmentsStatement, conn)
		var index = 1
		var list = ArrayList<Appointment>()
		while(results!!.next()){
			println("### APPOINTMENT FOUND ###")

			// skip id
			index++					
			//username
			var username = results.getString(index++)
			// date
			var date = Date(results.getTimestamp(index++).getTime())
			println("APPT INFO: " + username + " | " + date)
			var appt = Appointment(username, date)
			list.add(appt)
			index = 1
		}
		return list
	}

	fun createAppointment(username : String, date : Date, conn : Connection?){
		var createAppointmentStatement = conn!!.prepareStatement("""
		insert into Appointments (username, date) values (?, ?); 
		""")
		createAppointmentStatement.setString(1, username)
		createAppointmentStatement.setString(2, date.toString())
		executeSQLQuery(createAppointmentStatement, conn)
	}

	fun checkAppointmentAvailability(date : Date, conn : Connection?) : Boolean{
		var checkAvailabilityStatement = conn!!.prepareStatement("""
		select username from Appointments where Appointments.date = ?;
		""")
		checkAvailabilityStatement.setString(1, date.toString())
		var results = executeSQLQuery(checkAvailabilityStatement, conn)
		if(results!!.next()){
			return false;
		}
		else{
			return true;
		}
	}

	fun checkForUser(user : User, conn : Connection?) : Boolean{
		var checkAvailabilityStatement = conn!!.prepareStatement("""
		select username from Users where Users.username = ? and Users.password = ?;
		""")
		checkAvailabilityStatement.setString(1, user.username)
		checkAvailabilityStatement.setString(2, user.password)
		var results = executeSQLQuery(checkAvailabilityStatement, conn)
		if(results!!.next()){
			return true;
		}
		else{
			return false;
		}
	}
