<?php
//Obtaining details
/////////////////////////////////////////
  //details about the MySQL server
  $servername = "localhost";
  $username = "root";
  $password = "";
  $database = "abcd";

  //information to get from the request
  $staff_id = $_GET["staff_id"];
  $class_id = $_GET["class_id"];
  
  $conn = new mysqli($servername, $username, $password, $database);
  $que = "select * from staff_table where id='$staff_id'"; //checks if the staff ID is present in the table
  $result=$conn->query($que); //runs the query
  if($result->num_rows > 0)//staff with ID exists
  {
	$que = "select * from `$class_id`";//query to receive all the rows in the table, ie list of all the students
	$result = $conn->query($que); //running the query
        while($d=$result->fetch_assoc()) //receives all the students details into an array
      	{
      		$output[] = $d; //saves details in $output array
      	}
     	echo json_encode($output); //sends the JSON encoded information
   }
?>
