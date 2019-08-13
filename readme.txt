enter your specific MySQL username & password in the following documents:

src/main/resources/application.properties:
	spring.datasource.username=USERHERE
	spring.datasource.password=PASSHERE

src/main/kotlin/com/oos/oos/HomeController.kt AND src/.../oos/ListDBs.kt:
	internal var username = "USERHERE"
	internal var password = "PASSHERE"


	



