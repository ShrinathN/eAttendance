<?php
//this script assumes the attendance for today has already been taken, and will need to be updated, this will be done by first sending the current attendance details, then deleting them, and then finally receiving and entering the updated details
//details about the MySQL server
$servername = "localhost";
$username = "root";
$password = "";
$database = "abcd";

//details to obtain from the
$staff_id = $_GET["staff_id"];
$class_id = $_GET["class_id"];
$class_attendance_id = $class_id . "-attendance";
//$attendance = $_GET["attendance"];
$todays_date = date('Y-m-d');

$conn = new mysqli($servername, $username, $password, $database);

$que = "select * from `$class_attendance_id`";//query to receive all the rows in the table, ie list of all
$result = $conn->query($que); //running the query
while($d=$result->fetch_assoc()) //receives all the students details into an array
{
	$output[] = $d; //saves details in $output array
}
echo json_encode($output); //sends the JSON encoded information

?>
