<?php
//this script assumes the attendance for today has not been taken yet, and will be taken using this script
//details about the MySQL server
$servername = "localhost";
$username = "root";
$password = "";
$database = "abcd";
//details to obtain from the
$staff_id = $_GET["staff_id"];
$class_id = $_GET["class_id"];
$class_attendance_id = $class_id . "-attendance";
$attendance = $_GET["attendance"];

//getting string with today's date
$todays_date = date('D, d M Y');

//starting a connection to MySQL server using above details
$sql_connection = new mysqli($servername, $username, $password, $database);
//gets the length of the attendace string
$length_of_attendance_string = strlen($attendance);
//creates the part of the query to insert the data into the table
$to_insert = "insert into `$class_attendance_id` values(";
//appends the first element in the attendance array
$to_insert .= "\"$attendance[0]\"";
//setting the initial value of $counter
$counter = 1;
//this loop iterates over all elements of $attendance array and adds the data into the $to_insert query to be executed
do
{
  $to_insert .= ",\"$attendance[$counter]\""; //adds data into the query string
  $counter++; //increments counter
}
while($counter < $length_of_attendance_string); //cycles through all the characters present
$to_insert .= ",\"$todays_date\",\"$staff_id\")"; //adds todays date and the staff_id into the table as well
// echo "$to_insert";
$result = $sql_connection->query($to_insert);
if($result != false)
{
  echo "INSERTED_SUCCESSFULLY";
}
?>
