<?php 

$servername = "localhost";
$username = "root";
$password = "";
$database = "abcd";

$staff_id = $_GET["staff_id"];
$class_id = $_GET["class_id"];


$conn = new mysqli($servername, $username, $password,$database);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$que = "select * from staff_table where id='$staff_id'"; //checks if the staff ID is present in the table
$result=$conn->query($que); //queries
if($result->num_rows === 0) //if there is no staff with the sent ID
{
	echo "ERROR_ID"; //displays the ERROR_ID error. This means the ID was incorrect
	exit();
}
else //if a non zero (ie the staff is present in the table) number of staff entries are present corresponding to the sent staff_id field, send the list of students in the class in JSON format
{
	$que = "select * from `$class_id`";//query to receive all the rows in the table, ie list of all students
	$result = $conn->query($que);
	if($result == false)
	{
		echo "ERROR_CLASS"; //displays the ERROR_CLASS error. This means the ID was correct, but the class was not
		exit();
	}
	while($d=$result->fetch_assoc()) //if the 
	{
		$output[] = $d;
	}
	echo json_encode($output); //displays JSON encoded information
}

$conn->close();
 ?>
