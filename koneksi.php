<?php
	/* ===== www.dedykuncoro.com ===== */
	$server		= "localhost"; //sesuaikan dengan nama server
	$user		= "pajaka83_4kmal"; //sesuaikan username
	$password	= "rahas14"; //sesuaikan password
	$database	= "pajaka83_win"; //sesuaikan target database

	$con = mysqli_connect($server, $user, $password, $database);
	if (mysqli_connect_errno()) {
		echo "Gagal terhubung MySQL: " . mysqli_connect_error();
	}
?>