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
  $class_attendance_id = $class_id . "-attendance";
  $todays_date = date('Y-m-d');

  //starting a connection to MySQL server using above details
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
      $que = "select * from `$class_attendance_id` where Date=\"$todays_date\""; //this checks if the attendance has been already taken or
      $result = $conn->query($que);//running the query
      if($result->num_rows == 0) //this means the attendance for today hasn't been taken, so the list of students must be sent over so that attendance can be taken
      {
        $que = "select * from `$class_id`";//query to receive all the rows in the table, ie list of all
        $result = $conn->query($que); //running the query
        while($d=$result->fetch_assoc()) //receives all the students details into an array
      	{
      		$output[] = $d; //saves details in $output array
      	}
      	echo json_encode($output); //sends the JSON encoded information
      }
      else if($result->num_rows != 0)//else if the number of rows with current date is != 0, that means the attendance has already been taken
      {
        echo "ERROR_ATTENDANCE_ALREADY_TAKEN"; //sends the error message
        exit();
      }
    }
    else if($result == false) //meaning the table named $class_id doesn't exist
    {
      echo "ERROR_CLASS"; //displays the error message
      exit();
    }
  }
  else //executed if the staff member's ID is not present in the database
  {
    echo "ERROR_ID"; //error message
    exit();
  }
  $conn->close(); //closes the connection
?>

