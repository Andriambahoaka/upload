<?php

$target_dir = "uploads/";
if (!is_dir($target_dir)) {
    mkdir($target_dir, 777, true);
}
chmod($target_dir, 0777);
$image = $_POST["name"];
echo $image;
for($i=0;$i<count($image);$i++;){
	$decode = base64_decode("$image");
    file_put_contents($target_dir."image".$i.".jpeg",$decode);
}	

?>


