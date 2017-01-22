<?php


     if (is_uploaded_file($_FILES['uploadedfile']['tmp_name'])) {
    
							
							$uploads_dir = 'uploads/';
                            
							$tmp_name = $_FILES['uploadedfile']['tmp_name'];
                            $file_name = $_FILES['uploadedfile']['name'];
                            move_uploaded_file($tmp_name, $uploads_dir.$file_name);
                            }
               else{
                   echo "File not uploaded successfully.";
           }

   ?>