<?php

$servername = "localhost";
$username = "root";
$password = "";
$database = "abcd";

//information to get from the request
$staff_id = $_GET["staff_id"];
$class_id = $_GET["class_id"];
$class_attendance_id = $class_id . "-attendance";
$attendance = $_GET["attendance"];
$todays_date = date('Y-m-d');
$length_of_attendance_string = strlen($attendance);

$conn = new mysqli($servername, $username, $password, $database);
$que = "select * from staff_table where id='$staff_id'"; //checks if the staff ID is present in the table
$result=$conn->query($que); //runs the query

if($result->num_rows != 0) //if there are staff members with the sent ID
{
  //check if the table with the class actually exists
  $que = "select * from `$class_id`";
  $result = $conn->query($que);
  if($result != false && $result->num_rows > 0) //will only continue if not false, false means there isn't a table with the given class_id
  {
    //this will delete todays attendance from the database
    $que = "delete from `$class_attendance_id` where Date=\"$todays_date\"";
    $result = $conn->query($que);
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
      $result = $conn->query($to_insert);
      if($result != false)
      {
        echo "OK";
      }
  }
  else {
    //ERROR: no class with ID
    echo "CLASS_ERROR";
  }
}
else {
  //ERROR: no staff with ID
  echo "STAFF_ERROR";
}
 ?>
