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
$attendance = $_GET["attendance"];

?>
